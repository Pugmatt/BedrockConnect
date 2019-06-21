package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.packet.*;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import main.com.pyratron.pugmatt.bedrockconnect.AuthData;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.PipePlayer;
import main.com.pyratron.pugmatt.bedrockconnect.Server;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Heavily referenced from https://github.com/NukkitX/ProxyPass/blob/master/src/main/java/com/nukkitx/proxypass/network/bedrock/session/UpstreamPacketHandler.java

public class PacketHandler implements BedrockPacketHandler {

    private static final int PIXEL_SIZE = 4;

    public static final int SINGLE_SKIN_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_64_SIZE = 128 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_128_SIZE = 128 * 128 * PIXEL_SIZE;

    private Server server;
    private BedrockServerSession session;

    private String name;
    private String uuid;

    private JSONObject skinData;
    private JSONObject extraData;
    private ArrayNode chainData;
    private AuthData authData;

    /** @Override
    public boolean handle(AdventureSettingsPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(BlockEntityDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(BlockPickRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(BookEditPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(CommandBlockUpdatePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(CraftingEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(EntityEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(EntityFallPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(EntityPickRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(EventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        System.out.println(packet.getClass().getName());
        session.sendPacketImmediately(packet);
        return false;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ItemFrameDropItemPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(LabTablePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(LecternUpdatePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent3Packet packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MapInfoRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MobArmorEquipmentPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    } **/


    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        System.out.println(packet.getClass().getName());
        System.out.println("\"" + packet.getFormData().replaceAll("\\s+","") + "\"");

            switch (packet.getFormId()) {
                case UIForms.MAIN:
                    if(UIForms.currentForm == UIForms.MAIN) {
                        // If exiting main window
                        if (packet.getFormData().contains("null")) {
                            session.disconnect("Exiting Server List");
                        } else { // If selecting button
                            int chosen = Integer.parseInt(packet.getFormData().replaceAll("\\s+",""));
                            if (chosen == 0) { // Add Server
                                session.sendPacketImmediately(UIForms.createDirectConnect());
                            } else if (chosen == 1) { // Remove Server
                                session.sendPacketImmediately(UIForms.createDirectConnect());
                            } else { // Choosing Server
                                List<String> servers = new ArrayList<>();
                                servers.add("Play.SkyBlockpe.com:19132");
                                TransferPacket tp = new TransferPacket();
                                tp.setAddress("Play.SkyBlockpe.com");
                                tp.setPort(19132);
                                session.sendPacketImmediately(tp);
                            }
                        }
                    }
                    break;
                case UIForms.DIRECT_CONNECT:
                    try {
                        if(packet.getFormData().contains("null"))
                            session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
                        else {
                            ArrayList<String> data = UIComponents.getFormData(packet.getFormData());
                            TransferPacket tp = new TransferPacket();
                            tp.setAddress(data.get(0).replace(" ", ""));
                            tp.setPort(Integer.parseInt(data.get(1)));
                            session.sendPacketImmediately(tp);
                        }
                    } catch(Exception e) {
                        session.sendPacketImmediately(UIForms.createError("Please enter a valid IP/Address and port that contains only numbers."));
                    }
                    break;
                case UIForms.ERROR:
                    session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
                    break;
            }
        return false;
    }

    /**
    @Override
    public boolean handle(MoveEntityAbsolutePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        System.out.println(packet.getClass().getName());
        session.sendPacketImmediately(packet);
        return false;
    }

    @Override
    public boolean handle(NetworkStackLatencyPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PhotoTransferPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlayerHotbarPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlayerInputPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlayerSkinPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PurchaseReceiptPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    } **/

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        System.out.println(packet.getClass().getName());
        /**  chunk = new ChunkRadiusUpdatedPacket();
        chunk.setRadius(Math.max(3, Math.min(packet.getRadius(), 5)));
        //PacketHeader ph = new PacketHeader();
       // ph.set
        //chunk.setHeader();
        session.sendPacketImmediately(chunk);

        int chunkPositionX = 0;
        int chunkPositionZ = 0;
        int chunkRadius = 5;
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                FullChunkDataPacket fullChunk = new FullChunkDataPacket();
                fullChunk.setChunkX(chunkPositionX + x);
                fullChunk.setChunkZ(chunkPositionZ + z);
                fullChunk.setData(new byte[0]);
                session.sendPacketImmediately(fullChunk);
            }
        }

        PlayStatusPacket play = new PlayStatusPacket();
        play.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacketImmediately(play);

        SetTimePacket st = new SetTimePacket();
        st.setTime(0);
        session.sendPacketImmediately(st);
        return false; **/
        return false;
    }
    /**
    @Override
    public boolean handle(RiderJumpPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ServerSettingsRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetDefaultGameTypePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    } **/

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
        return false;
    }

    /**
    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SubClientLoginPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(TextPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AddBehaviorTreePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AddHangingEntityPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AddItemEntityPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AddPaintingPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AvailableCommandsPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(BlockEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(BossEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(CameraPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ChangeDimensionPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ChunkRadiusUpdatedPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ClientboundMapItemDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(CommandOutputPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ContainerOpenPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ContainerSetDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(CraftingDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ExplodePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(FullChunkDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(GameRulesChangedPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(GuiDataPickItemPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(HurtArmorPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AutomationClientConnectPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(LevelEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MapCreateLockedCopyPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MobEffectPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ModalFormRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(MoveEntityDeltaPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(NpcRequestPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(OnScreenTextureAnimationPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlayerListPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlaySoundPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(RemoveObjectivePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ResourcePackChunkDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ResourcePackDataInfoPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ScriptCustomEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ServerSettingsResponsePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetCommandsEnabledPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetDifficultyPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetDisplayObjectivePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetEntityDataPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetEntityLinkPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetEntityMotionPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetLastHurtByPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetScoreboardIdentityPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetScorePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SetTitlePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ShowCreditsPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ShowProfilePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(ShowStoreOfferPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SimpleEventPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SpawnExperienceOrbPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(StopSoundPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(StructureBlockUpdatePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(TakeItemEntityPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(TransferPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(UpdateBlockPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(UpdateBlockSyncedPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(UpdateEquipPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(UpdateSoftEnumPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(UpdateTradePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(AvailableEntityIdentifiersPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(BiomeDefinitionListPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent2Packet packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(NetworkChunkPublisherUpdatePacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(SpawnParticleEffectPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    }

    @Override
    public boolean handle(VideoStreamConnectPacket packet) {
        System.out.println(packet.getClass().getName());
        return false;
    } **/

    public PacketHandler(BedrockServerSession session, Server server) {
        this.session = session;
        this.server = server;

        session.addDisconnectHandler((Reason) -> disconnect());
    }

    public void disconnect() {
        System.out.println("Player disconnected");
        server.removePlayer(server.getPlayer(uuid));
    }

    private static boolean validateChainData(JsonNode data) throws Exception {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        for (JsonNode node : data) {
            JWSObject jwt = JWSObject.parse(node.asText());

            if (!validChain) {
                validChain = verifyJwt(jwt, EncryptionUtils.getMojangPublicKey());
            }

            if (lastKey != null) {
                verifyJwt(jwt, lastKey);
            }

            JsonNode payloadNode = Server.JSON_MAPPER.readTree(jwt.getPayload().toString());
            JsonNode ipkNode = payloadNode.get("identityPublicKey");
            Preconditions.checkState(ipkNode != null && ipkNode.getNodeType() == JsonNodeType.STRING, "identityPublicKey node is missing in chain");
            lastKey = EncryptionUtils.generateKey(ipkNode.asText());
        }
        return validChain;
    }


    private static boolean verifyJwt(JWSObject jwt, ECPublicKey key) throws JOSEException {
        return jwt.verify(new DefaultJWSVerifierFactory().createJWSVerifier(jwt.getHeader(), key));
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        return false;
    }

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        System.out.println("Received Chunk Radius request");
        return false;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        switch (packet.getStatus()) {
            case COMPLETED:
                BedrockConnect.data.userExists(uuid, name, session);
                break;
            case HAVE_ALL_PACKS:
                ResourcePackStackPacket rs = new ResourcePackStackPacket();
                rs.setExperimental(false);
                rs.setForcedToAccept(false);
                session.sendPacketImmediately(rs);
                break;
            default:
                session.disconnect("disconnectionScreen.resourcePack");
                break;
        }

        return true;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        int protocolVersion = packet.getProtocolVersion();

        if (protocolVersion != server.getProtocol()) {
            PlayStatusPacket status = new PlayStatusPacket();
            if (protocolVersion > server.getProtocol()) {
                status.setStatus(PlayStatusPacket.Status.FAILED_SERVER);
            } else {
                status.setStatus(PlayStatusPacket.Status.FAILED_CLIENT);
            }
            session.sendPacket(status);
        }
        session.setPacketCodec(server.getCodec());

        JsonNode certData;
        try {
            certData = Server.JSON_MAPPER.readTree(packet.getChainData().toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Certificate JSON can not be read.");
        }

        JsonNode certChainData = certData.get("chain");
        if (certChainData.getNodeType() != JsonNodeType.ARRAY) {
            throw new RuntimeException("Certificate data is not valid");
        }
        chainData = (ArrayNode) certChainData;

        boolean validChain;
        try {
            validChain = validateChainData(certChainData);

            JWSObject jwt = JWSObject.parse(certChainData.get(certChainData.size() - 1).asText());
            JsonNode payload = Server.JSON_MAPPER.readTree(jwt.getPayload().toBytes());

            if (payload.get("extraData").getNodeType() != JsonNodeType.OBJECT) {
                throw new RuntimeException("AuthData was not found!");
            }

            extraData = (JSONObject) jwt.getPayload().toJSONObject().get("extraData");

            this.authData = new AuthData(extraData.getAsString("displayName"),
                    UUID.fromString(extraData.getAsString("identity")), extraData.getAsString("XUID"));

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }
            ECPublicKey identityPublicKey = EncryptionUtils.generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());
            verifyJwt(clientJwt, identityPublicKey);
            skinData = clientJwt.getPayload().toJSONObject();

            System.out.println("Made it through login - " + "User: " + extraData.getAsString("displayName") + " (" + extraData.getAsString("identity") + ")");

            name = extraData.getAsString("displayName");
            uuid = extraData.getAsString("identity");

            PlayStatusPacket status = new PlayStatusPacket();
            status.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
            session.sendPacketImmediately(status);

            ResourcePacksInfoPacket resourcePacksInfo = new ResourcePacksInfoPacket();
            resourcePacksInfo.setForcedToAccept(false);
            session.sendPacketImmediately(resourcePacksInfo);

            //this.player = this.server.addPlayer(authData, session);
            //System.out.println(this.player.getProtocol().getProfile().getId());

            /** MovePlayerPacket mp = new MovePlayerPacket();
            mp.setRuntimeEntityId(-1);
            session.sendPacket(mp);

            SetSpawnPositionPacket ss = new SetSpawnPositionPacket();
            ss.setBlockPosition(new Vector3i(0, 0 ,0));
            ss.setSpawnForced(true);
            ss.setSpawnType(SetSpawnPositionPacket.Type.PLAYER_SPAWN);
            session.sendPacket(ss);

            session.sendPacket(mp);

            SetTimePacket st = new SetTimePacket();
            st.setTime(0);
            session.sendPacket(st);

            RespawnPacket rp = new RespawnPacket();
            rp.setPosition(new Vector3f(0, 0 ,0));
            session.sendPacket(rp); **/

            //MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            //PlayerList playerList = server.getPlayerList();

           // GameProfile gameProfile = new GameProfile(UUID.fromString(extraData.getAsString("identity")), extraData.getAsString("displayName"));

           // PipePlayer pl = new PipePlayer(session);
           // pl.addEvent(new LoginEvent(gameProfile));
           // this.server.addPlayer(pl);




            //saveSkin();
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return true;
    }

}