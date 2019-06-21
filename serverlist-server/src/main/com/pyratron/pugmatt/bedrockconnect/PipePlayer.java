package main.com.pyratron.pugmatt.bedrockconnect;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.data.Attribute;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.data.GameRule;
import com.nukkitx.protocol.bedrock.packet.*;
import io.netty.buffer.ByteBuf;
import main.com.pyratron.pugmatt.bedrockconnect.chunk.ChunkData;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;

import java.util.ArrayList;
import java.util.List;

public class PipePlayer {

    private BedrockServerSession session;
    private List<String> serverList = new ArrayList<>();

    private int serverLimit;

    private Data data;

    private String uuid;


    public PipePlayer(String uuid, Data data, BedrockServerSession session, List<String> serverList, int serverLimit) {
        this.uuid = uuid;
        this.data = data;
        this.session = session;
        this.serverList = serverList;
        this.serverLimit = serverLimit;

        joinGame();
    }

    public BedrockServerSession getSession() {
        return session;
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        data.setValueString("servers", UIComponents.serversToFormData(serverList), uuid);
        this.serverList = serverList;
    }

    public String getUuid() {
        return uuid;
    }

    public int getServerLimit() {
        return serverLimit;
    }

    public void setServerLimit(int serverLimit) {
        data.setValueInt("serverLimit", serverLimit, uuid);
        this.serverLimit = serverLimit;
    }

    public void joinGame() {

        /**
        StartGamePacket pkStartGame = new StartGamePacket();
        pkStartGame.setUniqueEntityId(packet.getEntityId());
        pkStartGame.setRuntimeEntityId(packet.getEntityId());
        pkStartGame.setDimensionId(0);
        pkStartGame.setSeed(0);
        pkStartGame.setGeneratorId(1);
        pkStartGame.setDifficulty(0);
        pkStartGame.setDefaultSpawn(new Vector3i(0, 72, 0));
        pkStartGame.setPlayerPosition(new Vector3f(0f, 72f + 5, 0f));
        pkStartGame.setLevelId("");
        pkStartGame.setWorldName("World");
        pkStartGame.setDefaultPlayerPermission(2);
        pkStartGame.setCommandsEnabled(true);
        pkStartGame.setPremiumWorldTemplateId(""); **/

        MovePlayerPacket mp = new MovePlayerPacket();
        mp.setRuntimeEntityId(-1);
        mp.setOnGround(false);
        mp.setMode(MovePlayerPacket.Mode.NORMAL);
        mp.setRotation(new Vector3f(0,0,0));
        mp.setPosition(new Vector3f(0,0,0));
        session.sendPacketImmediately(mp);

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(0);
        startGamePacket.setRuntimeEntityId(0);
        startGamePacket.setPlayerGamemode(0);
        startGamePacket.setPlayerPosition(new Vector3f(-249, 67, -275));
        startGamePacket.setRotation(new Vector2f(1, 1));

        startGamePacket.setSeed(1111);
        startGamePacket.setDimensionId(0);
        startGamePacket.setGeneratorId(0);
        startGamePacket.setLevelGamemode(0);
        startGamePacket.setDifficulty(0);
        startGamePacket.setDefaultSpawn(new Vector3i(-249, 67, -275));
        startGamePacket.setAcheivementsDisabled(true);
        startGamePacket.setTime(1300);
        startGamePacket.setEduLevel(false);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setPlatformLockedContentConfirmed(false);
        startGamePacket.setMultiplayerGame(false);
        startGamePacket.setBroadcastingToLan(false);
        startGamePacket.getGamerules().add((new GameRule("showcoordinates", true)));
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setTrustingPlayers(true);
        startGamePacket.setDefaultPlayerPermission(1);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);

        startGamePacket.setLevelId("oerjhii");
        startGamePacket.setWorldName("world");
        startGamePacket.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
        startGamePacket.setCurrentTick(1);
        startGamePacket.setEnchantmentSeed(1);
        startGamePacket.setMultiplayerCorrelationId("");

        startGamePacket.setCachedPalette(BedrockConnect.paletteManager.getCachedPalette());

        session.sendPacketImmediately(startGamePacket);

        spawn();
    }

    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.setRuntimeEntityId(0);
        pk.setAttributes(new ArrayList<Attribute>() {
            {
                add(new Attribute("minecraft:health", 0.00f, 20.00f, 20.00f, 20.00f));
                add(new Attribute("minecraft:player.hunger", 0.00f, 20.00f, 20.00f, 20.00f));
                add(new Attribute("minecraft:movement", 0.00f, 340282346638528859811704183484516925440.00f, 0.10f, 0.10f));
                add(new Attribute("minecraft:player.level", 0.00f, 24791.00f, 0.00f, 0.00f));
                add(new Attribute("minecraft:player.experience", 0.00f, 1.00f, 0.00f, 0.00f));
            }
        });
        session.sendPacketImmediately(pk);

        RespawnPacket rp = new RespawnPacket();
        rp.setPosition(new Vector3f(0, 0 ,0));
        session.sendPacketImmediately(rp);

        spawn();
    }

    public void spawn() {

        ChunkData data = new ChunkData(true);
        data.encode();

        Vector3f pos = new Vector3f(-249, 67, -275);
        int chunkX = pos.getFloorX() >> 4;
        int chunkZ = pos.getFloorX() >> 4;

        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                FullChunkDataPacket data2 = new FullChunkDataPacket();
                data2.setChunkX(chunkX + x);
                data2.setChunkZ(chunkZ + z);
                data2.setData(new byte[0]);
                session.sendPacketImmediately(data2);
            }
        }

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacketImmediately(playStatus);
    }
}
