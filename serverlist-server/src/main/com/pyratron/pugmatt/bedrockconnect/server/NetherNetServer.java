package main.com.pyratron.pugmatt.bedrockconnect.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.crypto.SecretKey;

import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.BedrockSessionFactory;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.NoopCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SimpleCompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.NetUtil;

import org.cloudburstmc.netty.channel.nethernet.NetherNetChannelFactory;
import org.cloudburstmc.netty.channel.nethernet.config.NetherChannelOption;
import org.cloudburstmc.netty.channel.nethernet.signaling.NetherNetHTTPServerSignaling;
import org.cloudburstmc.netty.channel.nethernet.signaling.PongData;
import org.cloudburstmc.netty.util.nethernet.NetherNetLogging;
import org.cloudburstmc.netty.util.nethernet.OperatorIdentity;
import org.cloudburstmc.netty.util.nethernet.TokenTrust;

import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import tel.schich.libdatachannel.LibDataChannelArchDetect;

// Referencing logic from NetherNetServer.java in https://github.com/GeyserMC/Geyser

public class NetherNetServer {
    private final int webrtcPort;
    private final int signalingPort;
    private final Path dataFolder;

    private volatile NetherNetHTTPServerSignaling signaling;
    private EventLoopGroup inbuiltEventLoopGroup;

    private static final CompressionStrategy NOOP_STRATEGY = new SimpleCompressionStrategy(new NoopCompression());

    private static final List<String> DEFAULT_STUN_SERVERS =
        List.of("stun:stun.cloudflare.com:3478", "stun:stun.l.google.com:19302");

    public NetherNetServer() {
        this.webrtcPort = Integer.parseInt(BedrockConnect.getConfig().getWebRtcPort());
        this.signalingPort = Integer.parseInt(BedrockConnect.getConfig().getPort());
        this.dataFolder = Paths.get(System.getProperty("user.dir")).resolve("nethernet");
    }

    public boolean start() {
        InetAddress bindAddress = new InetSocketAddress(BedrockConnect.getConfig().getBindIp(), 0).getAddress();
        if (bindAddress != null && bindAddress.isLoopbackAddress()) {
            // ICE binds to this address too, and clients never offer loopback candidates, so no candidate pair can connect
            BedrockConnect.logger.info("\"bind_ip\"" + BedrockConnect.getConfig().getBindIp() + " is a loopback address which only accepts connections from this machine. NetherNet " +
                "cannot work that way: every player will time out, including ones on this machine. " +
                "Set \"bind_ip\" section to 0.0.0.0, or to this machine's local network address.");
        }
        if (BedrockConnect.getConfig().getNetMode().equalsIgnoreCase("both") && webrtcPort == signalingPort) {
            BedrockConnect.logger.error("NetherNet will not start! RakNet and NetherNet each need their own UDP port when net_mode is set to 'both', " +
                "but both are set to " + webrtcPort + ". Set \"webrtc_port\" in config.yml to a different available port.");
            return false;
        }

        try {
            // Picks this platform's WebRTC library out of the ones bundled for every platform
            LibDataChannelArchDetect.initialize();
        } catch (LinkageError e) {
            BedrockConnect.logger.error("NetherNet is not available on " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + "). " +
                "Bedrock players can still join over RakNet: set \"transport\" in the \"bedrock\" section of the config to \"raknet\".", e);
            return false;
        }

        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            BedrockConnect.logger.error("NetherNet will not start! Could not create its data folder at " + dataFolder + ".", e);
            return false;
        }

        if (BedrockConnect.getConfig().isDebugEnabled()) {
            NetherNetLogging.setNativeLogLevel("DEBUG");
            NetherNetLogging.setLogLevel("DEBUG");
        } else {
            NetherNetLogging.setNativeLogLevel("WARN");
            NetherNetLogging.setLogLevel("WARN");
        }

        NetherNetHTTPServerSignaling.Builder signalingBuilder = new NetherNetHTTPServerSignaling.Builder();

        List<String> advertised = advertisedAddresses();

        try {
            // Configured addresses already say what STUN would find, and network refuses both
            boolean stun = advertised.isEmpty() && BedrockConnect.getConfig().isStunDiscoveryEnabled();
            if (!stun && BedrockConnect.getConfig().getStunServers() != null && !BedrockConnect.getConfig().getStunServers().isEmpty()) {
                BedrockConnect.logger.warn("stun_servers is ignored, as "
                    + (BedrockConnect.getConfig().isStunDiscoveryEnabled() ? "advertise_addresses is set" : "stun_discovery is off"));
            }
            List<String> stunServers = stun ? stunServers() : List.of();
            if (advertised.isEmpty() && stunServers.isEmpty()) {
                advertised = boundAddress(BedrockConnect.getConfig().getBindIp());
            }
            signalingBuilder.setAdvertisedAddresses(advertised);
            try {
                signalingBuilder.setStunServers(stunServers);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid entry in stun_servers: " + e.getMessage(), e);
            }
        } catch (IllegalArgumentException e) {
            BedrockConnect.logger.error("Signaling will not start! " + e.getMessage());
            return false;
        }


        // Created on the first start and kept afterwards, as clients pin its public key
        OperatorIdentity identity;
        try {
            identity = OperatorIdentity.fromPemOrCreate(dataFolder.resolve("identity.pem").toFile(), "BedrockConnect");
        } catch (Exception e) {
            BedrockConnect.logger.error("Built-in signaling will not start! Could not load or create this server's identity file, "
                    + dataFolder.resolve("identity.pem") + ". Only delete that file if it cannot be recovered: "
                    + "a new identity means every player has to confirm this server again.", e);
            return false;
        }

        // Start up NetherNet
        try {
            signalingBuilder
                    .setIdentity(identity)
                    .setTokenTrust(BedrockConnect.getConfig().isOnlineModeEnabled() ? TokenTrust.MINECRAFT_AUTH : TokenTrust.ANY)
                    .setMotd(new PongData.Builder()
                                .setServerName(BedrockConnect.getConfig().getLanguage().getWording("serverInfo", "motd"))
                                .setLevelName(BedrockConnect.getConfig().getLanguage().getWording("serverInfo", "subMotd"))
                                .setProtocol(BedrockProtocol.DEFAULT_BEDROCK_CODEC.getProtocolVersion())
                                .setVersion(BedrockProtocol.DEFAULT_BEDROCK_CODEC.getMinecraftVersion())
                                .setPlayerCount(1)
                                .setMaxPlayerCount(20)
                                .build()
                    );

            // The channel binds HTTP signaling over TCP to the signaling port, and by default pins ICE to its UDP side.
            // When the WebRTC port is another port, ICE goes there instead.
            boolean separateIcePort = webrtcPort != signalingPort;
            this.signaling = signalingBuilder.setIceOnLocalPort(!separateIcePort).build();

            this.inbuiltEventLoopGroup = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap();
            b.group(inbuiltEventLoopGroup)
                .channelFactory(NetherNetChannelFactory.server(signaling))
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void initSession(BedrockServerSession session) {
                        session.setPacketHandler(new PacketHandler(session, false));
                    }

                   @Override
                    protected void preInitChannel(Channel channel) {
                        channel.pipeline()
                            .addLast("nethernet-frame-codec", new MessageToMessageCodec<ByteBuf, BedrockBatchWrapper>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
                                    if (msg.getCompressed() == null) {
                                        throw new IllegalStateException("Bedrock batch was not compressed");
                                    }
                                    // Slice so the channel, which fragments by absolute index, always sees a reader index of 0
                                    out.add(msg.getCompressed().retainedSlice());
                                }

                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
                                    if (!msg.isReadable()) {
                                        return;
                                    }
                                    out.add(BedrockBatchWrapper.newInstance(msg.readRetainedSlice(msg.readableBytes()), null));
                                }
                            })
                            .addLast(CompressionCodec.NAME, new CompressionCodec(NOOP_STRATEGY, false));
                    }

                    @Override
                    protected void initPacketCodec(Channel channel) {
                        // The parent picks this by RakNet protocol version, which a NetherNet channel does not have
                        channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v3());
                    }

                    @Override
                    protected BedrockPeer createPeer(Channel channel) {
                        return new NetherNetPeer(channel, this::createSession);
                    }
                });
            if (separateIcePort) {
                b.option(NetherChannelOption.NETHER_SERVER_ICE_ADDRESS, new InetSocketAddress(BedrockConnect.getConfig().getBindIp(), webrtcPort));
            }

            b.bind(new InetSocketAddress(BedrockConnect.getConfig().getBindIp(), signalingPort)).syncUninterruptibly();
            return true;
        } catch (Throwable e) {
            BedrockConnect.logger.error("Built-in signaling could not start", e);
            return false;
        }
    }

    private List<String> advertisedAddresses() {
        List<String> addresses = BedrockConnect.getConfig().getAdvertisedAddresses();
        if (addresses == null || addresses.isEmpty()) {
            return List.of();
        }
        return addresses;
    }

    private List<String> boundAddress(String address) {
        byte[] bound = NetUtil.createByteArrayFromIpAddressString(address);
        try {
            if (bound == null || InetAddress.getByAddress(bound).isAnyLocalAddress()) {
                return List.of();
            }
        } catch (Exception e) {
            return List.of();
        }
        return List.of(address);
    }

    private List<String> stunServers() {
        List<String> servers = BedrockConnect.getConfig().getStunServers();
        if (servers == null || servers.isEmpty()) {
            return DEFAULT_STUN_SERVERS;
        }
        return servers;
    }

    public class NetherNetPeer extends BedrockPeer {
        /**
         * NetherNet compression behaves like RakNet protocol 11: none until network settings, prefixed afterwards.
         */
        private static final int EQUIVALENT_RAK_VERSION = 11;

        public NetherNetPeer(Channel channel, BedrockSessionFactory sessionFactory) {
            super(channel, sessionFactory);
        }

        @Override
        public void enableEncryption(SecretKey secretKey) {
            // No-op: DTLS already encrypts the data channel
        }

        /**
         * A NetherNet channel has no RakNet protocol version; {@link #setCompression(org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm)}
         * uses this to pick the compression strategy.
         */
        @Override
        public int getRakVersion() {
            return EQUIVALENT_RAK_VERSION;
        }
    }
}

