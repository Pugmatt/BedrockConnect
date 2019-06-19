package main.com.pyratron.pugmatt.bedrockconnect;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.data.Attribute;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.data.GameRule;
import com.nukkitx.protocol.bedrock.packet.*;
import main.com.pyratron.pugmatt.bedrockconnect.chunk.ChunkData;

import java.util.ArrayList;

public class PipePlayer {

    private BedrockServerSession session;

    public PipePlayer(AuthData data, BedrockServerSession session) {
        this.session = session;
    }

    public BedrockServerSession getSession() {
        return session;
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

        /** SetSpawnPositionPacket pkSpawn = new SetSpawnPositionPacket();
        pkSpawn.setSpawnType(SetSpawnPositionPacket.Type.PLAYER_SPAWN);
        pkSpawn.setSpawnForced(false);
        pkSpawn.setBlockPosition(new Vector3i(-249, 67, -275));
        session.sendPacketImmediately(pkSpawn);

        MovePlayerPacket mp = new MovePlayerPacket();
        mp.setRuntimeEntityId(-1);
        mp.setOnGround(false);
        mp.setMode(MovePlayerPacket.Mode.NORMAL);
        mp.setRotation(new Vector3f(0,0,0));
        mp.setPosition(new Vector3f(0,0,0));
        session.sendPacketImmediately(mp);

        SetTimePacket st = new SetTimePacket();
        st.setTime(0);
        session.sendPacketImmediately(st);

        AdventureSettingsPacket as = new AdventureSettingsPacket();
        as.setCommandPermission(0);
        as.setCustomFlags(0);
        as.setPlayerFlags(0);
        as.setPlayerPermission(0);
        as.setUniqueEntityId(0);
        as.setWorldFlags(0);
        as.setWorldFlags(0);
        session.sendPacketImmediately(as);

        RespawnPacket rp = new RespawnPacket();
        rp.setPosition(new Vector3f(0,0,0));
        session.sendPacketImmediately(rp);


        /** FullChunkDataPacket fullChunk = new FullChunkDataPacket();
        fullChunk.setChunkX(0);
        fullChunk.setChunkZ(0);
        fullChunk.setData(data.getBuffer());
        session.sendPacketImmediately(fullChunk);
        fullChunk.setChunkX(0);
        fullChunk.setChunkZ(-1);
        session.sendPacketImmediately(fullChunk);
        fullChunk.setChunkX(-1);
        fullChunk.setChunkZ(0);
        session.sendPacketImmediately(fullChunk);
        fullChunk.setChunkX(-1);
        fullChunk.setChunkZ(-1);
        session.sendPacketImmediately(fullChunk); **/

        //SetSpawnPositionPacket ss = new SetSpawnPositionPacket();
        //ss.setSpawnType(SetSpawnPositionPacket.Type.PLAYER_SPAWN);
        //session.sendPacketImmediately(ss);


    }
}
