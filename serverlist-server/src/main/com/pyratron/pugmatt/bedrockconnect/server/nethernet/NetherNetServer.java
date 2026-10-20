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
import io.netty.channel.EventLoopGroup;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.Config;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannelFactory;
import org.cloudburstmc.netty.channel.nethernet.config.NetherChannelOption;
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
 * the server port, then WebRTC on its own UDP port ("webrtc_port").
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

        NetherNetHTTPServerSignaling signaling = new NetherNetHTTPServerSignaling.Builder()
                .setIdentity(identity)
                // Retail clients present a token issued by Minecraft's auth service
                .setTokenTrust(config.isOnlineModeEnabled() ? TokenTrust.MINECRAFT_AUTH : TokenTrust.ANY)
                .setAdvertisedAddresses(config.getAdvertiseAddresses())
                .setMotd(motd)
                .build();

        try {
            new ServerBootstrap()
                .group(eventLoopGroup)
                .channelFactory(NetherNetChannelFactory.server(signaling))
                // Signaling holds the TCP side of the server port; RakNet may hold its UDP side, so WebRTC gets its own port
                .option(NetherChannelOption.NETHER_SERVER_ICE_ADDRESS, new InetSocketAddress(bindIp, webrtcPort))
                .childHandler(new NetherNetServerInitializer())
                .bind(new InetSocketAddress(bindIp, signalingPort))
                .syncUninterruptibly();
        } catch (Exception e) {
            signaling.close();
            BedrockConnect.logger.error("Error starting NetherNet (Is TCP port " + signalingPort + " already in use?)." + fallback, e);
            return;
        }

        BedrockConnect.logger.info("[ " + LogColors.green("OK") + " ] NetherNet is now running: " + LogColors.cyan(bindIp + ":" + signalingPort)
                + " (signaling, TCP), " + LogColors.cyan(bindIp + ":" + webrtcPort) + " (WebRTC, UDP)");
    }
}
