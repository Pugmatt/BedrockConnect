/*
 * Adapted from GeyserMC/Geyser (core/src/main/java/org/geysermc/geyser/network/bedrock/nethernet/NetherNetServer.java).
 *
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package main.com.pyratron.pugmatt.bedrockconnect.server.nethernet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpResponseStatus;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.Config;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannelFactory;
import org.cloudburstmc.netty.channel.nethernet.config.NetherChannelOption;
import org.cloudburstmc.netty.channel.nethernet.config.NetherServerMetrics;
import org.cloudburstmc.netty.channel.nethernet.signaling.NetherNetHTTPServerSignaling;
import org.cloudburstmc.netty.channel.nethernet.signaling.PongData;
import org.cloudburstmc.netty.util.nethernet.NetherNetLogging;
import org.cloudburstmc.netty.util.nethernet.OperatorIdentity;
import org.cloudburstmc.netty.util.nethernet.TokenTrust;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import tel.schich.libdatachannel.LibDataChannelArchDetect;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * The NetherNet transport Bedrock 26.60+ clients connect with: HTTP signaling on the TCP side of
 * the server port, then WebRTC on the UDP port "webrtc_port".
 */
public class NetherNetServer {

    /**
     * Starts NetherNet on the TCP side of the server port and the UDP webrtc_port. On failure the reason is logged,
     * and RakNet (if enabled) is unaffected.
     */
    public static void start(EventLoopGroup eventLoopGroup, String bindIp, int signalingPort, BedrockPong pong) {
        Config config = BedrockConnect.getConfig();
        int webrtcPort = config.getWebrtcPort();
        String fallback = config.isRakNetEnabled()
                ? " Continuing with RakNet only."
                : " Set transport=both or transport=raknet to accept RakNet players.";

        // Whatever fails here must leave RakNet, the inactivity timer and the console running
        NetherNetHTTPServerSignaling signaling = null;
        Channel channel = null;
        try {
            try {
                // Picks this platform's WebRTC library out of the ones bundled for every platform
                LibDataChannelArchDetect.initialize();
            } catch (LinkageError e) {
                BedrockConnect.logger.error("NetherNet is not available on " + System.getProperty("os.name")
                        + " (" + System.getProperty("os.arch") + "): its WebRTC library could not be loaded." + fallback, e);
                return;
            }

            // ICE only binds its UDP port when the first player joins, so check now that it is free
            try (DatagramSocket probe = new DatagramSocket(new InetSocketAddress(bindIp, webrtcPort))) {
                // Bound and released
            } catch (SocketException e) {
                BedrockConnect.logger.error("Error starting NetherNet: UDP port " + webrtcPort + " is not available (Is it already in use? Set webrtc_port to a free port)." + fallback, e);
                return;
            }

            // Created on the first start and kept afterwards, as clients pin its public key
            File identityFile = new File(config.getNetherNetIdentity());
            OperatorIdentity identity;
            try {
                identity = OperatorIdentity.fromPemOrCreate(identityFile, "BedrockConnect");
            } catch (Exception e) {
                BedrockConnect.logger.error("Could not load or create the NetherNet identity file " + identityFile.getAbsolutePath()
                        + ". Only delete it if it cannot be recovered: a new identity means every player has to trust this server again." + fallback, e);
                return;
            }

            NetherNetLogging.setNativeLogLevel(config.isDebugEnabled() ? "DEBUG" : "WARN");

            // The status shown before joining mirrors the RakNet advertisement
            PongData motd = new PongData.Builder()
                    .setServerName(pong.motd())
                    .setLevelName(pong.subMotd())
                    .setProtocol(pong.protocolVersion())
                    .setVersion(pong.version())
                    .setPlayerCount(pong.playerCount())
                    .setMaxPlayerCount(pong.maximumPlayerCount())
                    .build();

            signaling = new NetherNetHTTPServerSignaling.Builder()
                    .setIdentity(identity)
                    // Retail clients present a token issued by Minecraft's auth service
                    .setTokenTrust(config.isOnlineModeEnabled() ? TokenTrust.MINECRAFT_AUTH : TokenTrust.ANY)
                    .setAdvertisedAddresses(config.getAdvertiseAddresses())
                    .setMotd(motd)
                    .build();

            ChannelFuture bind = new ServerBootstrap()
                .group(eventLoopGroup)
                .channelFactory(NetherNetChannelFactory.server(signaling))
                // Signaling holds the TCP side of the server port; WebRTC uses the UDP side of webrtc_port
                .option(NetherChannelOption.NETHER_SERVER_ICE_ADDRESS, new InetSocketAddress(bindIp, webrtcPort))
                .option(NetherChannelOption.NETHER_SERVER_METRICS, new NetherServerMetrics() {
                    @Override
                    public void joinRefused(int httpStatus) {
                        // The join failed before WebRTC could answer, which is how a webrtc_port taken after startup shows up
                        if (httpStatus == HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) {
                            BedrockConnect.logger.error("A NetherNet join failed before WebRTC could answer. If \"UDP socket binding failed on port "
                                    + webrtcPort + "\" is logged above, another program has taken webrtc_port " + webrtcPort
                                    + ": stop it, or set webrtc_port to a free port and restart.");
                        }
                    }
                })
                .childHandler(new NetherNetServerInitializer())
                .bind(new InetSocketAddress(bindIp, signalingPort));
            channel = bind.channel();
            bind.syncUninterruptibly();
        } catch (Throwable e) {
            if (channel != null) {
                channel.close();
            }
            if (signaling != null) {
                signaling.close();
            }
            // The signaling listener reports a taken port as a ConnectException, a plain bind as a BindException
            BedrockConnect.logger.error("Error starting NetherNet" + (e instanceof SocketException ? " (Is TCP port " + signalingPort + " already in use?)" : "")
                    + "." + fallback, e);
            return;
        }

        BedrockConnect.logger.info("[ " + LogColors.green("OK") + " ] NetherNet is now running: " + LogColors.cyan(bindIp + ":" + signalingPort)
                + " (signaling, TCP), " + LogColors.cyan(bindIp + ":" + webrtcPort) + " (WebRTC, UDP)");
        BedrockConnect.logger.info("NetherNet claims UDP port " + webrtcPort + " (webrtc_port) when the first player joins, so keep it free for this server.");
    }
}
