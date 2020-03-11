package main.com.pyratron.pugmatt.bedrockconnect;

import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.ListTag;
import com.nukkitx.protocol.bedrock.Bedrock;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.data.Attribute;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.data.GameRuleData;
import com.nukkitx.protocol.bedrock.data.PlayerPermission;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.v388.Bedrock_v388;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        data.setValueString("servers", UIComponents.serversToFormData(serverList), serverList, uuid);
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
        mp.setRuntimeEntityId(1);
        mp.setOnGround(false);
        mp.setMode(MovePlayerPacket.Mode.NORMAL);
        mp.setRotation(Vector3f.from(0,0,0));
        mp.setPosition(Vector3f.from(0,0,0));
        session.sendPacket(mp);

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(1);
        startGamePacket.setRuntimeEntityId(1);
        startGamePacket.setPlayerGamemode(1);
        startGamePacket.setPlayerPosition(Vector3f.from(0, 0, 0));
        startGamePacket.setRotation(Vector2f.from(1, 1));

        startGamePacket.setSeed(-1);
        startGamePacket.setDimensionId(0);
        startGamePacket.setGeneratorId(1);
        startGamePacket.setLevelGamemode(1);
        startGamePacket.setDifficulty(1);
        startGamePacket.setDefaultSpawn(Vector3i.from(0, 0, 0));
        startGamePacket.setAchievementsDisabled(false);
        startGamePacket.setTime(-1);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setPlatformLockedContentConfirmed(false);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        startGamePacket.getGamerules().add((new GameRuleData<>("showcoordinates", true)));
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setTrustingPlayers(false);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);
        startGamePacket.setOnlySpawningV1Villagers(false);
        startGamePacket.setMovementServerAuthoritative(false);
        startGamePacket.setTrial(false);
        startGamePacket.setVanillaVersion(Server.codec.getMinecraftVersion());

        startGamePacket.setLevelId("");
        startGamePacket.setWorldName("world");
        startGamePacket.setPremiumWorldTemplateId("");
        startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");

        startGamePacket.setBlockPalette(BedrockConnect.paletteManager.CACHED_PALLETE);

        session.sendPacket(startGamePacket);

        spawn();
    }

    public void spawn() {

        Vector3f pos = Vector3f.ZERO;
        int chunkX = pos.getFloorX() >> 4;
        int chunkZ = pos.getFloorX() >> 4;

        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                LevelChunkPacket data2 = new LevelChunkPacket();
                data2.setChunkX(chunkX + x);
                data2.setChunkZ(chunkZ + z);
                data2.setSubChunksLength(0);
                data2.setData(EMPTY_LEVEL_CHUNK_DATA);
                session.sendPacket(data2);
            }
        }

        BiomeDefinitionListPacket biomePacket = new BiomeDefinitionListPacket();
        biomePacket.setTag(CompoundTag.EMPTY);
        session.sendPacket(biomePacket);
        AvailableEntityIdentifiersPacket entityPacket = new AvailableEntityIdentifiersPacket();
        entityPacket.setTag(CompoundTag.EMPTY);
        session.sendPacket(entityPacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);
    }
}
