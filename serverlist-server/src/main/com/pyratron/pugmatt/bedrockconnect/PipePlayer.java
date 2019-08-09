package main.com.pyratron.pugmatt.bedrockconnect;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.data.Attribute;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.data.GameRule;
import com.nukkitx.protocol.bedrock.packet.*;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PipePlayer {

    private BedrockServerSession session;
    private List<String> serverList = new ArrayList<>();

    private int serverLimit;

    private Data data;

    private String uuid;

    private static final CompoundTag EMPTY_TAG = CompoundTagBuilder.builder().buildRootTag();
    private static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    static {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(new byte[258]); // Biomes + Border Size + Extra Data Size

            try (NBTOutputStream stream = NbtUtils.createNetworkWriter(outputStream)) {
                stream.write(EMPTY_TAG);
            }

            EMPTY_LEVEL_CHUNK_DATA = outputStream.toByteArray();
        }catch (IOException e) {
            throw new AssertionError("Unable to generate empty level chunk data");
        }
    }


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
        startGamePacket.setTime(0);
        startGamePacket.setEduLevel(false);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setPlatformLockedContentConfirmed(false);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(false);
        startGamePacket.getGamerules().add((new GameRule<>("showcoordinates", true)));
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
        startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");

        startGamePacket.setCachedPalette(BedrockConnect.paletteManager.getCachedPalette());

        session.sendPacketImmediately(startGamePacket);

        spawn();
    }

    public void spawn() {

        Vector3f pos = new Vector3f(-249, 67, -275);
        int chunkX = pos.getFloorX() >> 4;
        int chunkZ = pos.getFloorX() >> 4;

        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                LevelChunkPacket data2 = new LevelChunkPacket();
                data2.setChunkX(chunkX + x);
                data2.setChunkZ(chunkZ + z);
                data2.setSubChunksLength(0);
                data2.setData(EMPTY_LEVEL_CHUNK_DATA);
                session.sendPacketImmediately(data2);
            }
        }

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacketImmediately(playStatus);
    }
}
