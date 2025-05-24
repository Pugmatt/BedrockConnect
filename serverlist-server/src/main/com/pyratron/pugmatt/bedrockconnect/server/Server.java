package main.com.pyratron.pugmatt.bedrockconnect.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;

import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private BedrockPong pong;

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private CopyOnWriteArrayList<BCPlayer> players;

    public CopyOnWriteArrayList<BCPlayer> getPlayers() {
        return players;
    }

    public BCPlayer getPlayer(String uuid) {
        for (BCPlayer player : players) {
            if(player != null && player.getUuid() != null && player.getUuid() == uuid)
                return player;
        }
        return null;
    }

    public void addPlayer(BCPlayer player) {
        this.players.add(player);
        BedrockConnect.logger.info("[ " + LogColors.cyan(this.players.size() + " online") + " ] Player connected: " + player.getDisplayName() + " (xuid: " + player.getUuid() + ")");
    }

    public void removePlayer(BCPlayer player) {
        if(this.players.contains(player))
            this.players.remove(player);
    }

    public Server(String bindIp, String port) {
        try {
            players = new CopyOnWriteArrayList<>();

            InetSocketAddress bindAddress = new InetSocketAddress(bindIp, Integer.parseInt(port));

            pong = new BedrockPong();
            pong.edition("MCPE");
            pong.motd(BedrockConnect.getConfig().getLanguage().getWording("serverInfo", "motd"));
            pong.subMotd(BedrockConnect.getConfig().getLanguage().getWording("serverInfo", "subMotd"));
            pong.playerCount(1);
            pong.maximumPlayerCount(20);
            pong.gameType("Survival");
            pong.ipv4Port(Integer.parseInt(port));
            pong.protocolVersion(BedrockProtocol.DEFAULT_BEDROCK_CODEC.getProtocolVersion());
            pong.version(BedrockProtocol.DEFAULT_BEDROCK_CODEC.getMinecraftVersion());

            new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
                .option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
                .option(RakChannelOption.RAK_PACKET_LIMIT, BedrockConnect.getConfig().getPacketLimit())
                .option(RakChannelOption.RAK_GLOBAL_PACKET_LIMIT, BedrockConnect.getConfig().getGlobalPacketLimit())
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void initSession(BedrockServerSession session) {
                        session.setPacketHandler(new PacketHandler(session, false));
                    }
                })
                .bind(bindAddress)
                .syncUninterruptibly();

            BedrockConnect.logger.info("[ " + LogColors.green("OK") + " ] Server is now running: " + LogColors.cyan(bindIp + ":" + port));
            if(BedrockConnect.getConfig().canKickInactive()) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() {
                        for (BCPlayer player : players) {
                            if(player != null && !player.isActive())
                                player.disconnect(BedrockConnect.getConfig().getLanguage().getWording("disconnect", "inactivity"));
                        }
                    }
                };
                timer.scheduleAtFixedRate(task, 0L, 60 * 1000);
            }

            // Command line input (Currently just for stopping BedrockConnect via command)
            new Thread() {
                public void run() {
                    Scanner sc = null;
                    try {
                        sc = new Scanner(System.in);
                        while(sc.hasNextLine()) {
                            String line = sc.nextLine(); 
                            String[] parts = line.trim().split("\\s+");

                            if (parts.length == 0) {
                                continue;
                            }

                            String cmd = parts[0];
                            switch(cmd) {
                                case "end":
                                case "stop":
                                    System.exit(0);
                                    break;
                                case "reload":
                                    if (parts.length > 1) {
                                        String arg = parts[1];
                                        switch(arg) {
                                            case "motd":
                                                if (parts.length > 2) {
                                                    BedrockConnect.getConfig().loadMotdMessage(parts[2]);
                                                } else {
                                                    BedrockConnect.getConfig().loadMotdMessage();
                                                }
                                                break;
                                        }
                                    }
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        BedrockConnect.logger.error("Error reading input", e);
                    } finally {
                        if (sc != null) {
                            try {
                                sc.close();
                            } catch (Exception e1) {
                                // just ignore it
                            }
                            sc = null;
                        }
                    }
                }
            }.start();

        } catch(Exception e) {
            if (e instanceof BindException) {
                 BedrockConnect.logger.error("Error binding to address (Is port " + port + " already in use?)", e);
            } else {
                BedrockConnect.logger.error("Error starting server", e);
            }
        }
    }
}
