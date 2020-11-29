package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.nukkitx.protocol.bedrock.v388.Bedrock_v388;
import com.nukkitx.protocol.bedrock.v407.Bedrock_v407;
import io.netty.util.AsciiString;
import io.netty.util.internal.ThreadLocalRandom;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.Server;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import net.minidev.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Heavily referenced from https://github.com/NukkitX/ProxyPass/blob/master/src/main/java/com/nukkitx/proxypass/network/bedrock/session/UpstreamPacketHandler.java

public class PacketHandler implements BedrockPacketHandler {

    private Server server;
    private BedrockServerSession session;

    private String name;
    private String uuid;

    private JSONObject extraData;

    private JSONObject skinData;
    private ArrayNode chainData;

    private boolean print = false;

    private boolean packetListening;

    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BlockEntityDataPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BlockPickRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BookEditPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CommandBlockUpdatePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CraftingEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EntityEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EntityFallPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EntityPickRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ItemFrameDropItemPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LabTablePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LecternUpdatePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MapInfoRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MobArmorEquipmentPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }
    
    @Override
    public boolean handle(MoveEntityAbsolutePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(NetworkStackLatencyPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PhotoTransferPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerHotbarPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerInputPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerSkinPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PurchaseReceiptPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        if(print)
            System.out.println(packet.toString());

        ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
        chunkRadiusUpdatePacket.setRadius(packet.getRadius());
        session.sendPacketImmediately(chunkRadiusUpdatePacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);

        /* Vector3f pos = Vector3f.ZERO;
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

        System.out.println("test");

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);

        System.out.println("test2"); */
        return false;
    }

    @Override
    public boolean handle(RiderJumpPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ServerSettingsRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetDefaultGameTypePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SubClientLoginPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(TextPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddBehaviorTreePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddHangingEntityPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddItemEntityPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddPaintingPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AvailableCommandsPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BlockEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BossEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CameraPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ChangeDimensionPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ChunkRadiusUpdatedPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ClientboundMapItemDataPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CommandOutputPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ContainerOpenPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ContainerSetDataPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CraftingDataPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ExplodePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(GameRulesChangedPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(GuiDataPickItemPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(HurtArmorPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AutomationClientConnectPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MapCreateLockedCopyPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MobEffectPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ModalFormRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MoveEntityDeltaPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(NpcRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(OnScreenTextureAnimationPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerListPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlaySoundPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RemoveObjectivePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePackChunkDataPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePackDataInfoPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ScriptCustomEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ServerSettingsResponsePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetCommandsEnabledPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetDifficultyPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetDisplayObjectivePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetEntityDataPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetEntityLinkPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetEntityMotionPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetLastHurtByPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetScoreboardIdentityPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetScorePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetTitlePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ShowCreditsPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ShowProfilePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ShowStoreOfferPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SimpleEventPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SpawnExperienceOrbPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(StopSoundPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(StructureBlockUpdatePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(TakeItemEntityPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(TransferPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateBlockPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateBlockSyncedPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateEquipPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateSoftEnumPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateTradePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AvailableEntityIdentifiersPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BiomeDefinitionListPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent2Packet packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(NetworkChunkPublisherUpdatePacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SpawnParticleEffectPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(VideoStreamConnectPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return false;
    }
    

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        if(print)
            System.out.println(packet.toString());
        return true;
    }
    

    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        if(print)
            System.out.println(packet.toString());
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

                                        case 1: // techax
                                            transfer("techax.sk", 19133);
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

                                if(data.get(0).length() >= 253)
                                    session.sendPacketImmediately(UIForms.createError("Address is too large. (Must be less than 253)"));
                                else if(data.get(1).length() >= 10)
                                    session.sendPacketImmediately(UIForms.createError("Port is too large. (Must be less than 10)"));
                                else if (!data.get(0).matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$") && !data.get(0).matches("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$"))
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
                case UIForms.DONATION:
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
        if(print)
            System.out.println(packet.toString());
        session.sendPacketImmediately(UIForms.createMain(server.getPlayer(uuid).getServerList()));
        return false;
    }

    public PacketHandler(BedrockServerSession session, Server server, boolean packetListening) {
        this.session = session;
        this.server = server;
        this.packetListening = packetListening;

        session.addDisconnectHandler((DisconnectReason) -> disconnect());
    }

    public void disconnect() {
        System.out.println(name + " disconnected");
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
        if(print)
            System.out.println(packet.toString());
        switch (packet.getStatus()) {
            case COMPLETED:
                BedrockConnect.data.userExists(uuid, name, session);
                break;
            case HAVE_ALL_PACKS:
                ResourcePackStackPacket rs = new ResourcePackStackPacket();
                //rs.setExperimental(false);
                rs.setForcedToAccept(false);
                rs.setGameVersion(Server.codec.getMinecraftVersion());
                session.sendPacket(rs);
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
                status.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
            } else {
                status.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
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
                session.sendPacket(status);

                SetEntityMotionPacket motion = new SetEntityMotionPacket();
                motion.setRuntimeEntityId(1);
                motion.setMotion(Vector3f.ZERO);
                session.sendPacket(motion);

                ResourcePacksInfoPacket resourcePacksInfo = new ResourcePacksInfoPacket();
                resourcePacksInfo.setForcedToAccept(false);
                resourcePacksInfo.setScriptingEnabled(false);
                session.sendPacket(resourcePacksInfo);
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return true;
    }

    public Set<BedrockClient> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public BedrockClient newClient() {
        InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(20000, 60000));
        BedrockClient client = new BedrockClient(bindAddress);
        this.clients.add(client);
        client.bind().join();
        return client;
    }


}
