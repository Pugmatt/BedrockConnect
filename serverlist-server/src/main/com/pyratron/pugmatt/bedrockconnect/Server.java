package main.com.pyratron.pugmatt.bedrockconnect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.nukkitx.protocol.bedrock.*;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;
import main.com.pyratron.pugmatt.bedrockconnect.listeners.PacketHandler;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;


import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public BedrockServer server;
    public BedrockPong pong;

    public static BedrockPacketCodec codec;

    public int getProtocol() {
        return codec.getProtocolVersion();
    }

    public BedrockPacketCodec getCodec() {
        return codec;
    }

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private List<PipePlayer> players;

    public List<PipePlayer> getPlayers() {
        return players;
    }

    public boolean recordingDone = false;
    public List<BedrockPacket> packets = new ArrayList<>();

    public PipePlayer getPlayer(String uuid) {
        for(int i=0;i<players.size();i++) {
            if(players.get(i).getUuid() == uuid)
                return players.get(i);
        }
        return null;
    }

    public void addPlayer(PipePlayer player) {
        //System.gc();
        //Runtime rt = Runtime.getRuntime();
        //long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        //System.out.println("Memory usage: " + usedMB);
        System.out.println("Total users connected: " + this.players.size());
        this.players.add(player);
    }

    public PipePlayer addPlayer(String uuid, Data data, BedrockServerSession session, List<String> serverList, int serverLimit) {
        PipePlayer player = new PipePlayer(uuid, data, session, serverList, serverLimit);
        this.players.add(player);
        return player;
    }

    public void removePlayer(PipePlayer player) {
        this.players.remove(player);
    }


    public Server(String port) {
        Server current = this;
        players = new ArrayList<>();

        InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", Integer.parseInt(port));
        codec = Bedrock_v408.V408_CODEC;
        server = new BedrockServer(bindAddress);
        pong = new BedrockPong();
        pong.setEdition("MCPE");
        pong.setMotd("Join to open Server List");
        pong.setSubMotd("BedrockConnect Server List");
        pong.setPlayerCount(0);
        pong.setMaximumPlayerCount(20);
        pong.setGameType("Survival");
        pong.setIpv4Port(Integer.parseInt(port));
        pong.setProtocolVersion(codec.getProtocolVersion());
        pong.setVersion(codec.getMinecraftVersion());
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
               // if(!recordingDone)
              //      session.setPacketHandler(new PacketHandler(session, current, true));
               // else {
                    session.setPacketHandler(new PacketHandler(session, current, false));
               // }
                //session.setPacketHandler(new PacketHandler(session, current));
            }
        });
        // Start server up
        server.bind().join();
        System.out.println("Bedrock Connection Started: localhost:19132");
    }
}
