package main.com.pyratron.pugmatt.bedrockconnect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import main.com.pyratron.pugmatt.bedrockconnect.listeners.PacketHandler;
import main.com.pyratron.pugmatt.bedrockconnect.utils.BedrockProtocol;
import org.cloudburstmc.netty.channel.raknet.RakChannel;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakConstants;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.BedrockSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;


import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.*;

public class Server {

    private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    public BedrockPong pong;

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private List<BCPlayer> players;

    public List<BCPlayer> getPlayers() {
        return players;
    }

    public BCPlayer getPlayer(String uuid) {
        for(int i=0;i<players.size();i++) {
            if(players.get(i) != null && players.get(i).getUuid() != null && players.get(i).getUuid() == uuid)
                return players.get(i);
        }
        return null;
    }

    public void addPlayer(BCPlayer player) {
        System.out.println("Total users connected: " + this.players.size());
        this.players.add(player);
    }

    public void removePlayer(BCPlayer player) {
        if(this.players.contains(player))
            this.players.remove(player);
    }


    public Server(String bindIp, String port) {
        Server current = this;
        players = new ArrayList<>();

        InetSocketAddress bindAddress = new InetSocketAddress(bindIp, Integer.parseInt(port));

        pong = new BedrockPong();
        pong.edition("MCPE");
        pong.motd(BedrockConnect.language.getWording("serverInfo", "motd"));
        pong.subMotd(BedrockConnect.language.getWording("serverInfo", "subMotd"));
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
                .option(RakChannelOption.RAK_PACKET_LIMIT, BedrockConnect.packetLimit)
                .option(RakChannelOption.RAK_GLOBAL_PACKET_LIMIT, BedrockConnect.globalPacketLimit)
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void initSession(BedrockServerSession session) {
                        session.setPacketHandler(new PacketHandler(session, current, false));
                    }
                })
                .bind(bindAddress)
                .syncUninterruptibly();

        System.out.println("Bedrock Connection Started: " + bindIp + ":" + port);
        if(BedrockConnect.kickInactive) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i) != null && !players.get(i).isActive())
                            players.get(i).disconnect(BedrockConnect.language.getWording("disconnect", "inactivity"), current);
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0L, 60 * 1000);
        }

        new Thread() {
            public void run() {
                Scanner sc = null;
                try {
                    sc = new Scanner(System.in);
                    while(sc.hasNextLine()) {
                        String cmd = sc.next();
                        switch(cmd) {
                            case "end":
                            case "stop":
                                System.exit(0);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    }
}
