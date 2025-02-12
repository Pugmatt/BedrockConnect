package main.com.pyratron.pugmatt.bedrockconnect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.cloudburstmc.protocol.bedrock.data.inventory.CreativeItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.CreativeItemGroup;
import org.cloudburstmc.protocol.bedrock.data.inventory.CreativeItemCategory;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class BCPlayer {

    private BedrockServerSession session;
    private List<String> serverList = new ArrayList<>();

    private int serverLimit;

    private String uuid;

    private LocalTime lastAction;

    private int currentForm = 0;
    private LocalTime movementOpenCoolDown = LocalTime.now();

    public int editingServer = -1;
    public int selectedGroup = -1;

    private static final NbtMap EMPTY_TAG = NbtMap.EMPTY;
    private static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    static {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(new byte[258]); // Biomes + Border Size + Extra Data Size

            try (NBTOutputStream stream = NbtUtils.createNetworkWriter(outputStream)) {
                stream.writeTag(EMPTY_TAG);
            }

            EMPTY_LEVEL_CHUNK_DATA = outputStream.toByteArray();
        }catch (IOException e) {
            throw new AssertionError("Unable to generate empty level chunk data");
        }
    }


    public BCPlayer(String uuid, BedrockServerSession session, List<String> serverList, int serverLimit) {
        this.uuid = uuid;
        this.session = session;
        this.serverList = serverList;
        this.serverLimit = serverLimit;
        this.lastAction = LocalTime.now();

        if(session != null && session.isConnected())
        joinGame();
    }

    public BedrockServerSession getSession() {
        return session;
    }

    public void disconnect(String reason, Server server) {
        session.disconnect(reason);
        server.removePlayer(this);
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        BedrockConnect.data.setValueString("servers", UIComponents.serversToFormData(serverList), serverList, uuid);
        this.serverList = serverList;
    }

    public boolean addServer(String address, String port, String name) {
        List<String> serverList = getServerList();
        if (serverList.size() >= getServerLimit())
            createError(BedrockConnect.language.getWording("error", "serverLimit").replace("%MAX_SERVERS%", Integer.toString(getServerLimit())));
        else {
            String server;
            // If display name is included from form input, add as parameter
            if(!name.isEmpty())
                server = address + ":" + port + ":" + name;
            else
                server = address + ":" + port;
            serverList.add(server);
            setServerList(serverList);
            return true;
        }
        return false;
    }

    public String getUuid() {
        return uuid;
    }

    public int getServerLimit() {
        return serverLimit;
    }

    public void setServerLimit(int serverLimit) {
        BedrockConnect.data.setValueInt("serverLimit", serverLimit, uuid);
        this.serverLimit = serverLimit;
    }

    public int getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(int form) {
        currentForm = form;

        TextPacket text = new TextPacket();
        text.setType(TextPacket.Type.TIP);
        text.setMessage("==!!== Crouch or punch to re-trigger the popup ==!!==");
        text.setXuid(uuid);
        session.sendPacket(text);

        TextPacket text2 = new TextPacket();
        text2.setType(TextPacket.Type.RAW);
        text2.setMessage("\n\n\n\n\n\n\n\n\n\n\n\n\n\n==!!== Crouch or punch to re-trigger the popup ==!!==\n\n\n\n\n\n\n\n\n\n\n");
        text2.setXuid(uuid);
        session.sendPacket(text2);
    }

    public boolean isActive() { return Duration.between(lastAction, LocalTime.now()).toMillis() <= 600000; }

    public boolean canMovementOpen() { return Duration.between(movementOpenCoolDown, LocalTime.now()).toMillis() > 1000; }

    public void resetMovementOpen() { movementOpenCoolDown = LocalTime.now(); }

    public void setActive() { lastAction = LocalTime.now(); }

    public void setEditingServer(int server) { editingServer = server; }

    public int getEditingServer() { return editingServer; }

    public void setSelectedGroup(int group) { selectedGroup = group; }

    public int getSelectedGroup() { return selectedGroup; }

    public void movementOpen() {

        if(canMovementOpen()) {

            ModalFormRequestPacket form = getForm(getCurrentForm());

            session.sendPacketImmediately(form);

            movementOpenCoolDown = LocalTime.now();
        }
    }

    public void openForm(int formId) {
        ModalFormRequestPacket form = getForm(formId);

        session.sendPacketImmediately(form);

        setCurrentForm(formId);
    }

    private ModalFormRequestPacket getForm(int formId) {
        ModalFormRequestPacket form;

        switch (formId) {
            case UIForms.MAIN:
                form = UIForms.createMain(getServerList(), session);
                break;
            case UIForms.DIRECT_CONNECT:
                form = UIForms.createDirectConnect();
                break;
            case UIForms.MANAGE_SERVER:
                form = UIForms.createManageList();
                break;
            case UIForms.ADD_SERVER:
                form = UIForms.createAddServer();
                break;
            case UIForms.EDIT_CHOOSE_SERVER:
                form = UIForms.createEditChooseServer(getServerList());
                break;
            case UIForms.REMOVE_SERVER:
                form = UIForms.createRemoveServer(getServerList());
                break;
            case UIForms.SERVER_GROUP:
                form = UIForms.createServerGroup((CustomServerGroup) CustomServerHandler.getServers()[getSelectedGroup()], session);
                break;
            default:
                form = UIForms.createMain(getServerList(), session);
                break;
        }

        return form;
    }

    public void createError(String text) {
        session.sendPacketImmediately(UIForms.createError(text));
    }

    public void joinGame() {
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(1);
        startGamePacket.setRuntimeEntityId(1);
        startGamePacket.setPlayerGameType(GameType.SURVIVAL);
        startGamePacket.setPlayerPosition(Vector3f.from(0, 0, 0));
        startGamePacket.setRotation(Vector2f.from(1, 1));
        startGamePacket.setServerId("");
        startGamePacket.setWorldId("");
        startGamePacket.setScenarioId("");

        startGamePacket.setSeed(-1);
        startGamePacket.setDimensionId(0);
        startGamePacket.setGeneratorId(1);
        startGamePacket.setLevelGameType(GameType.SURVIVAL);
        startGamePacket.setDifficulty(1);
        startGamePacket.setDefaultSpawn(Vector3i.ZERO);
        startGamePacket.setAchievementsDisabled(false);
        startGamePacket.setCurrentTick(-1);
        //startGamePacket.setUnknownInt0(-1);
        //startGamePacket.setUnknownInt1(-1);
        startGamePacket.setEduEditionOffers(0);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        //startGamePacket.setPlatformLockedContentConfirmed(false);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        startGamePacket.getGamerules().add((new GameRuleData<>("showcoordinates", false)));
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setTrustingPlayers(true);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);
        startGamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        startGamePacket.setCustomBiomeName("");
        startGamePacket.setEducationProductionId("");
        startGamePacket.setForceExperimentalGameplay(OptionalBoolean.empty());

        startGamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
        startGamePacket.setRewindHistorySize(0);
        startGamePacket.setServerAuthoritativeBlockBreaking(false);
        //startGamePacket.setTrial(false);
        startGamePacket.setVanillaVersion("*");
        startGamePacket.setInventoriesServerAuthoritative(true);
        startGamePacket.setServerEngine("");

        startGamePacket.setLevelId("world");
        startGamePacket.setLevelName("world");
        //startGamePacket.setWorldName("world");
        startGamePacket.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
        startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");
        startGamePacket.setServerEngine("");
        startGamePacket.setPlayerPropertyData(NbtMap.EMPTY);
        startGamePacket.setWorldTemplateId(UUID.randomUUID());

        startGamePacket.setChatRestrictionLevel(ChatRestrictionLevel.NONE);

        startGamePacket.setBlockPalette(BedrockConnect.paletteManager.CACHED_PALLETE);

        session.sendPacket(startGamePacket);

        ItemComponentPacket componentPacket = new ItemComponentPacket();
        session.sendPacket(componentPacket);

        CreativeContentPacket creativeContentPacket = new CreativeContentPacket();
        session.sendPacket(creativeContentPacket);

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
                data2.setData(Unpooled.wrappedBuffer(EMPTY_LEVEL_CHUNK_DATA));
                session.sendPacket(data2);
            }
        }

        BiomeDefinitionListPacket biomePacket = new BiomeDefinitionListPacket();
        biomePacket.setDefinitions(BedrockConnect.paletteManager.BIOMES);
        session.sendPacket(biomePacket);

        AvailableEntityIdentifiersPacket entityPacket = new AvailableEntityIdentifiersPacket();
        entityPacket.setIdentifiers(BedrockConnect.paletteManager.ENTITY_IDENTIFIERS);
        session.sendPacket(entityPacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);

        UpdateAttributesPacket attributesPacket = new UpdateAttributesPacket();
        attributesPacket.setRuntimeEntityId(0);
        List<AttributeData> attributes = new ArrayList<>();
        // Default move speed
        // Bedrock clients move very fast by default until they get an attribute packet correcting the speed
        attributes.add(new AttributeData("minecraft:movement", 0.0f, 1024f, 0.1f, 0.1f));
        attributesPacket.setAttributes(attributes);
        session.sendPacket(attributesPacket);
    }
}
