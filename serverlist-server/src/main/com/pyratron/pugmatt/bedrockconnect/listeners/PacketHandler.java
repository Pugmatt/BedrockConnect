package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.packet.*;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.Server;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;

// Heavily referenced from https://github.com/NukkitX/ProxyPass/blob/master/src/main/java/com/nukkitx/proxypass/network/bedrock/session/UpstreamPacketHandler.java

public class PacketHandler implements BedrockPacketHandler {

    private Server server;
    private BedrockServerSession session;

    private String name;
    private String uuid;

    private JSONObject extraData;

    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(BlockEntityDataPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(BlockPickRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(BookEditPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(CommandBlockUpdatePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(CraftingEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(EntityEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(EntityFallPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(EntityPickRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(EventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(InteractPacket packet) {

        return false;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ItemFrameDropItemPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(LabTablePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(LecternUpdatePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent3Packet packet) {
        
        return false;
    }

    @Override
    public boolean handle(MapInfoRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(MobArmorEquipmentPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        
        return false;
    }
    
    @Override
    public boolean handle(MoveEntityAbsolutePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {

        return false;
    }

    @Override
    public boolean handle(NetworkStackLatencyPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PhotoTransferPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlayerHotbarPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlayerInputPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlayerSkinPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PurchaseReceiptPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {

        return false;
    }

    @Override
    public boolean handle(RiderJumpPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ServerSettingsRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetDefaultGameTypePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SubClientLoginPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(TextPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AddBehaviorTreePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AddHangingEntityPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AddItemEntityPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AddPaintingPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AvailableCommandsPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(BlockEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(BossEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(CameraPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ChangeDimensionPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ChunkRadiusUpdatedPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ClientboundMapItemDataPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(CommandOutputPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ContainerOpenPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ContainerSetDataPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(CraftingDataPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ExplodePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(GameRulesChangedPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(GuiDataPickItemPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(HurtArmorPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AutomationClientConnectPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(LevelEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(MapCreateLockedCopyPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(MobEffectPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ModalFormRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(MoveEntityDeltaPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(NpcRequestPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(OnScreenTextureAnimationPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlayerListPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlaySoundPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(RemoveObjectivePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ResourcePackChunkDataPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ResourcePackDataInfoPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ScriptCustomEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ServerSettingsResponsePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetCommandsEnabledPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetDifficultyPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetDisplayObjectivePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetEntityDataPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetEntityLinkPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetEntityMotionPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetLastHurtByPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetScoreboardIdentityPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetScorePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SetTitlePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ShowCreditsPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ShowProfilePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(ShowStoreOfferPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SimpleEventPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SpawnExperienceOrbPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(StopSoundPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(StructureBlockUpdatePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(TakeItemEntityPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(TransferPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(UpdateBlockPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(UpdateBlockSyncedPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(UpdateEquipPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(UpdateSoftEnumPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(UpdateTradePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(AvailableEntityIdentifiersPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(BiomeDefinitionListPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent2Packet packet) {
        
        return false;
    }

    @Override
    public boolean handle(NetworkChunkPublisherUpdatePacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(SpawnParticleEffectPacket packet) {
        
        return false;
    }

    @Override
    public boolean handle(VideoStreamConnectPacket packet) {
        
        return false;
    }
    

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        
        return true;
    }
    

    @Override
    public boolean handle(ModalFormResponsePacket packet) {

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
                                session.sendPacketImmediately(UIForms.createRemoveServer(server.getPlayer(uuid).getServerList()));
                            } else { // Choosing Server

                                // If server chosen is a featued server
                                if(chosen-2 > server.getPlayer(uuid).getServerList().size()-1) {
                                    int featuredServer = (chosen - 2) - (server.getPlayer(uuid).getServerList().size() - 1);

                                    switch (featuredServer) {
                                        case 1: // Hive
                                            transfer("54.39.75.136", 19132);
                                            break;
                                        case 2: // Mineplex
                                            transfer("108.178.12.125", 19132);
                                            break;
                                        case 3: // Cubecraft
                                            transfer("213.32.11.233", 19132);
                                            break;
                                        case 4: // Lifeboat
                                            transfer("63.143.40.66", 19132);
                                            break;
                                        case 5: // Mineville
                                            transfer("52.234.131.7", 19132);
                                            break;
                                    }
                                } else { // If server chosen is not a featured server
                                    String address = server.getPlayer(uuid).getServerList().get(chosen-2);

                                    if (address.split(":").length > 1) {
                                        String ip = address.split(":")[0];
                                        String port = address.split(":")[1];

                                        try {
                                            transfer(ip, Integer.parseInt(port));
                                        } catch (Exception e) {
                                            session.sendPacketImmediately(UIForms.createError("Error connecting to server. Invalid address."));
                                        }
                                    } else {
                                        session.sendPacketImmediately(UIForms.createError("Invalid server address"));
                                    }
                                }
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
                            if(data.size() > 1) {
                                // Remove any whitespace
                                data.set(0, data.get(0).replaceAll("\\s",""));
                                data.set(1, data.get(1).replaceAll("\\s",""));

                                if(data.get(0).length() >= 45)
                                    session.sendPacketImmediately(UIForms.createError("Address is too large. (Must be less than 45)"));
                                else if(data.get(1).length() >= 10)
                                    session.sendPacketImmediately(UIForms.createError("Port is too large. (Must be less than 10)"));
                                else if (!data.get(0).matches("[a-zA-Z.0-9]+"))
                                    session.sendPacketImmediately(UIForms.createError("Enter a valid address. (E.g. play.example.net, 172.16.254.1)"));
                                else if (!data.get(1).matches("[0-9]+"))
                                    session.sendPacketImmediately(UIForms.createError("Enter a valid port that contains only numbers"));
                                else {
                                    boolean addServer = Boolean.parseBoolean(data.get(2));
                                    if (addServer) {
                                        List<String> serverList = server.getPlayer(uuid).getServerList();
                                        if (serverList.size() >= server.getPlayer(uuid).getServerLimit())
                                            session.sendPacketImmediately(UIForms.createError("You have hit your serverlist limit of " + server.getPlayer(uuid).getServerLimit() + " servers. Remove some to add more."));
                                        else {
                                            serverList.add(data.get(0) + ":" + data.get(1));
                                            server.getPlayer(uuid).setServerList(serverList);
                                            transfer(data.get(0).replace(" ", ""), Integer.parseInt(data.get(1)));
                                        }
                                    } else {
                                        TransferPacket tp = new TransferPacket();
                                        tp.setAddress(data.get(0).replace(" ", ""));
                                        tp.setPort(Integer.parseInt(data.get(1)));
                                        session.sendPacketImmediately(tp);
                                    }
                                }
                            }
                        }
                    } catch(Exception e) {
                        session.sendPacketImmediately(UIForms.createError("Please enter a valid IP/Address and port that contains only numbers."));
                    }
                    break;
                case UIForms.REMOVE_SERVER:
                    try {
                        if(packet.getFormData().contains("null"))
                            session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
                        else {
                            ArrayList<String> data = UIComponents.getFormData(packet.getFormData());

                            int chosen = Integer.parseInt(data.get(0));

                            List<String> serverList = server.getPlayer(uuid).getServerList();
                            serverList.remove(chosen);

                            server.getPlayer(uuid).setServerList(serverList);

                            session.sendPacketImmediately(UIForms.createMain(serverList));
                        }
                    } catch(Exception e) {
                        session.sendPacketImmediately(UIForms.createError("Invalid server to remove"));
                    }
                    break;
                case UIForms.ERROR:
                    session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
                    break;
            }
        return false;
    }

    public void transfer(String ip, int port) {
        TransferPacket tp = new TransferPacket();
        tp.setAddress(ip);
        tp.setPort(port);
        session.sendPacketImmediately(tp);
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
        return false;
    }

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

        boolean validChain;
        try {
            validChain = validateChainData(certChainData);

            JWSObject jwt = JWSObject.parse(certChainData.get(certChainData.size() - 1).asText());
            JsonNode payload = Server.JSON_MAPPER.readTree(jwt.getPayload().toBytes());

            if (payload.get("extraData").getNodeType() != JsonNodeType.OBJECT) {
                throw new RuntimeException("AuthData was not found!");
            }

            extraData = (JSONObject) jwt.getPayload().toJSONObject().get("extraData");

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }
            ECPublicKey identityPublicKey = EncryptionUtils.generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());
            verifyJwt(clientJwt, identityPublicKey);

            System.out.println("Made it through login - " + "User: " + extraData.getAsString("displayName") + " (" + extraData.getAsString("identity") + ")");

            name = extraData.getAsString("displayName");
            uuid = extraData.getAsString("identity");

            PlayStatusPacket status = new PlayStatusPacket();
            status.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
            session.sendPacketImmediately(status);

            ResourcePacksInfoPacket resourcePacksInfo = new ResourcePacksInfoPacket();
            resourcePacksInfo.setForcedToAccept(false);
            session.sendPacketImmediately(resourcePacksInfo);

        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return true;
    }

}