package main.com.pyratron.pugmatt.bedrockconnect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.nukkitx.protocol.bedrock.*;
import com.nukkitx.protocol.bedrock.v354.Bedrock_v354;
import main.com.pyratron.pugmatt.bedrockconnect.listeners.PacketHandler;


import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public BedrockServer server;
    public BedrockPong pong;

    private BedrockPacketCodec codec;

    public int getProtocol() {
        return codec.getProtocolVersion();
    }

    public BedrockPacketCodec getCodec() {
        return codec;
    }

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private List<PipePlayer> players = new ArrayList<>();

    public List<PipePlayer> getPlayers() {
        return players;
    }

    public void addPlayer(PipePlayer player) {
        this.players.add(player);
    }

    public PipePlayer addPlayer(AuthData data, BedrockServerSession session) {
        PipePlayer player = new PipePlayer(data, session);
        this.players.add(player);
        return player;
    }

    public void removePlayer(PipePlayer player) {
        this.players.remove(player);
    }


    public Server() {
        Server current = this;
        InetSocketAddress bindAddress = new InetSocketAddress("localhost", 19132);
        codec = Bedrock_v354.V354_CODEC;
        server = new BedrockServer(bindAddress);
        pong = new BedrockPong();
        pong.setEdition("MCPE");
        pong.setMotd("BedrockConnect Connection Successful");
        pong.setSubMotd("Test");
        pong.setPlayerCount(0);
        pong.setMaximumPlayerCount(20);
        pong.setGameType("Survival");
        pong.setProtocolVersion(Bedrock_v354.V354_CODEC.getProtocolVersion());
        server.setHandler(new BedrockServerEventHandler() {
            @Override
            public boolean onConnectionRequest(InetSocketAddress address) {
                return true; // Connection will be accepted
            }
            @Nonnull
            public BedrockPong onQuery(InetSocketAddress address) {
                return pong;
            }
            @Override
            public void onSessionCreation(BedrockServerSession session) {
                session.setPacketHandler(new PacketHandler(session, current));
            }
        });
        // Start server up
        server.bind().join();
        System.out.println("Bedrock Connection Started: localhost:19132");

        while(true) { }
    }
}
