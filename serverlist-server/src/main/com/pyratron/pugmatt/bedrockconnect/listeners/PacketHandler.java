package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import com.nimbusds.jwt.SignedJWT;
import main.com.pyratron.pugmatt.bedrockconnect.*;
import main.com.pyratron.pugmatt.bedrockconnect.gui.MainFormButton;
import main.com.pyratron.pugmatt.bedrockconnect.gui.ManageFormButton;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import main.com.pyratron.pugmatt.bedrockconnect.utils.BedrockProtocol;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.Preconditions;

import java.io.IOException;
import java.net.URI;
import java.security.interfaces.ECPublicKey;
import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketHandler implements BedrockPacketHandler {

    private Server server;
    private BedrockServerSession session;

    private String name;
    private String uuid;

    private BCPlayer player;

    private JSONObject extraData;

    public void setPlayer(BCPlayer player) {
        this.player = player;
    }

    public static String getIP(String hostname) {
        try {
            if(BedrockConnect.fetchFeaturedIps) {
                InetAddress host = InetAddress.getByName(hostname);
                return host.getHostAddress();
            } else {
                return BedrockConnect.featuredServerIps.get(hostname);
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return "0.0.0.0";
    }

    @Override
    public PacketSignal handle(RequestChunkRadiusPacket packet) {
        ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
        chunkRadiusUpdatePacket.setRadius(packet.getRadius());
        session.sendPacketImmediately(chunkRadiusUpdatePacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);
        return PacketSignal.HANDLED;
    }

    // Occasionally, a sent form will not correctly send to a player for whatever reason, and they float in space. This works as a way to open the form back up.
    
    @Override
    public PacketSignal handle(PlayerActionPacket packet) {
        player.movementOpen();
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AnimatePacket packet) {
        if(packet.getAction() == AnimatePacket.Action.SWING_ARM)
            player.movementOpen();
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ModalFormResponsePacket packet) {
        player.setActive();
        player.resetMovementOpen();

        switch (packet.getFormId()) {
                case UIForms.MAIN:
                    // Re-open window if closed
                    if (packet.getFormData() == null || packet.getFormData().contains("null")) {
                        if(player.getCurrentForm() != packet.getFormId())
                            return PacketSignal.HANDLED;
                        player.openForm(UIForms.MAIN);
                    } else { // If selecting button
                        int chosen = Integer.parseInt(packet.getFormData().replaceAll("\\s+",""));

                        CustomEntry[] customServers = CustomServerHandler.getServers();
                        List<String> playerServers = server.getPlayer(uuid).getServerList();

                        MainFormButton button = UIForms.getMainFormButton(chosen, customServers, playerServers);

                        int serverIndex = UIForms.getServerIndex(chosen, customServers, playerServers);

                        switch(button) {
                            case CONNECT:
                                player.openForm(UIForms.DIRECT_CONNECT);
                                break;
                            case MANAGE:
                                player.openForm(UIForms.MANAGE_SERVER);
                                break;
                            case EXIT:
                                player.disconnect(BedrockConnect.language.getWording("disconnect", "exit"), server);
                                break;
                            case USER_SERVER:
                                String address = server.getPlayer(uuid).getServerList().get(serverIndex);

                                if (address.split(":").length > 1) {
                                    String ip = address.split(":")[0];
                                    String port = address.split(":")[1];

                                    transfer(ip, Integer.parseInt(port));
                                } else {
                                    player.createError((BedrockConnect.language.getWording("error", "invalidUserServer")));
                                }
                                break;
                            case CUSTOM_SERVER:
                                CustomEntry server = customServers[serverIndex - playerServers.size()];

                                if(server instanceof CustomServer) {
                                    transfer(((CustomServer)server).getAddress(), ((CustomServer)server).getPort());
                                } else if(server instanceof CustomServerGroup) {
                                    player.setSelectedGroup(serverIndex - playerServers.size());
                                    player.openForm(UIForms.SERVER_GROUP);
                                }
                                break;
                            case FEATURED_SERVER:
                                int featuredServer = serverIndex - playerServers.size() - customServers.length;

                                switch (featuredServer) {
                                    case 0: // Hive
                                        transfer(getIP("hivebedrock.network"), 19132);
                                        break;
                                    case 1: // Mineplex
                                        transfer(getIP("mco.mineplex.com"), 19132);
                                        break;
                                    case 2: // Cubecraft
                                        transfer("play.cubecraft.net", 19132);
                                        break;
                                    case 3: // Lifeboat
                                        transfer(getIP("mco.lbsg.net"), 19132);
                                        break;
                                    case 4: // Mineville
                                        transfer(getIP("play.inpvp.net"), 19132);
                                        break;
                                    case 5: // Galaxite
                                        transfer(getIP("play.galaxite.net"), 19132);
                                        break;
                                    case 6: // Pixel Paradise
                                        transfer(getIP("play.pixelparadise.gg"), 19132);
                                        break;
                                }
                                break;
                        }
                    }
                    break;
                case UIForms.SERVER_GROUP:
                    if(packet.getFormData() == null || packet.getFormData().contains("null")) {
                        if(player.getCurrentForm() != packet.getFormId())
                            return PacketSignal.HANDLED;
                        player.openForm(UIForms.MAIN);
                    }
                    else {
                        int chosen = Integer.parseInt(packet.getFormData().replaceAll("\\s+",""));

                        CustomEntry[] customServers = CustomServerHandler.getServers();
                        CustomServerGroup group = (CustomServerGroup) customServers[player.getSelectedGroup()];

                        if(chosen == 0) {
                            player.openForm(UIForms.MAIN);
                        } else {
                            CustomServer server = group.getServers().get(chosen - 1);
                            transfer(server.getAddress(), server.getPort());
                        }
                    }
                    break;
                case UIForms.MANAGE_SERVER:
                    if(packet.getFormData() == null) {
                        if(player.getCurrentForm() != packet.getFormId())
                            return PacketSignal.HANDLED;
                        player.openForm(UIForms.MAIN);
                    }
                    else {
                        int chosen = Integer.parseInt(packet.getFormData().replaceAll("\\s+",""));

                        ManageFormButton button = UIForms.getManageFormButton(chosen);

                        switch(button) {
                            case ADD:
                                player.openForm(UIForms.ADD_SERVER);
                                break;
                            case EDIT:
                                player.openForm(UIForms.EDIT_CHOOSE_SERVER);
                                break;
                            case REMOVE:
                                player.openForm(UIForms.REMOVE_SERVER);
                                break;
                        }
                    }
                    break;
                case UIForms.ADD_SERVER:
                    try {
                        if(packet.getFormData() == null || packet.getFormData().contains("null")) {
                            if(player.getCurrentForm() != packet.getFormId())
                                return PacketSignal.HANDLED;
                            player.openForm(UIForms.MANAGE_SERVER);
                        }
                        else {
                            ArrayList<String> data = UIComponents.getFormData(packet.getFormData());
                            if(data.size() > 1) {
                                // Remove any whitespace
                                data = UIComponents.cleanAddress(data);

                                String address = data.get(0);
                                String port = data.get(1);
                                String name = data.get(2);

                                if(UIComponents.validateServerInfo(address, port, name, player)) {
                                    player.addServer(address, port, name);

                                    player.openForm(UIForms.MANAGE_SERVER);
                                }
                            }
                        }
                    } catch(Exception e) {
                        player.createError(BedrockConnect.language.getWording("error", "invalidServerConnect"));
                    }
                    break;
                case UIForms.DIRECT_CONNECT:
                    try {
                        if(packet.getFormData() == null || packet.getFormData().contains("null")) {
                            if(player.getCurrentForm() != packet.getFormId())
                                return PacketSignal.HANDLED;
                            player.openForm(UIForms.MAIN);
                        }
                        else {
                            ArrayList<String> data = UIComponents.getFormData(packet.getFormData());
                            if(data.size() > 1) {
                                // Remove any whitespace
                                data = UIComponents.cleanAddress(data);

                                String address = data.get(0);
                                String port = data.get(1);
                                String name = data.get(2);

                                if(UIComponents.validateServerInfo(address, port, name, player)) {
                                    boolean addServer = Boolean.parseBoolean(data.get(3));
                                    if (addServer) {
                                        if(player.addServer(address, port, name)) {
                                            transfer(address.replace(" ", ""), Integer.parseInt(port));
                                        }
                                    } else {
                                        transfer(address.replace(" ", ""), Integer.parseInt(port));
                                    }
                                }
                            }
                        }
                    } catch(Exception e) {
                        player.createError((BedrockConnect.language.getWording("error", "invalidServerConnect")));
                    }
                    break;
                case UIForms.EDIT_CHOOSE_SERVER:
                    if(packet.getFormData() == null || packet.getFormData().contains("null")) {
                        if(player.getCurrentForm() != packet.getFormId())
                            return PacketSignal.HANDLED;
                        player.openForm(UIForms.MANAGE_SERVER);
                    }
                    else {
                        ArrayList<String> data = UIComponents.getFormData(packet.getFormData());

                        int chosen = Integer.parseInt(data.get(0));

                        String server = player.getServerList().get(chosen);

                        String[] serverInfo = UIComponents.validateAddress(server, player);

                        if(serverInfo != null) {
                            String ip = serverInfo[0];
                            String port = serverInfo[1];
                            String name = serverInfo.length > 2 ? serverInfo[2] : "";

                            player.setEditingServer(chosen);

                            session.sendPacketImmediately(UIForms.createEditServer(ip, port, name));
                            player.setCurrentForm(UIForms.EDIT_SERVER);
                        }
                    }
                    break;
                case UIForms.EDIT_SERVER:
                    if(packet.getFormData() == null || packet.getFormData().contains("null")) {
                        if(player.getCurrentForm() != packet.getFormId())
                            return PacketSignal.HANDLED;
                        player.openForm(UIForms.EDIT_CHOOSE_SERVER);
                    }
                    else {
                        ArrayList<String> data = UIComponents.getFormData(packet.getFormData());
                        if(data.size() > 1) {
                            // Remove any whitespace
                            data.set(0, data.get(0).replaceAll("\\s",""));
                            data.set(1, data.get(1).replaceAll("\\s",""));

                            String address = data.get(0);
                            String port = data.get(1);
                            String name = data.get(2);

                            if(UIComponents.validateServerInfo(address, port, name, player)) {
                                String value = address + ":" + port + ":" + name;

                                List<String> servers = player.getServerList();
                                servers.set(player.getEditingServer(), value);

                                player.setServerList(servers);

                                player.openForm(UIForms.EDIT_CHOOSE_SERVER);
                            }
                        }
                    }
                    break;
                case UIForms.REMOVE_SERVER:
                    try {
                        if(packet.getFormData() == null || packet.getFormData().contains("null")) {
                            if(player.getCurrentForm() != packet.getFormId())
                                return PacketSignal.HANDLED;
                            player.openForm(UIForms.MANAGE_SERVER);
                        }
                        else {
                            ArrayList<String> data = UIComponents.getFormData(packet.getFormData());

                            int chosen = Integer.parseInt(data.get(0));

                            List<String> serverList = player.getServerList();
                            serverList.remove(chosen);

                            player.setServerList(serverList);

                            player.openForm(UIForms.MANAGE_SERVER);
                        }
                    } catch(Exception e) {
                        player.createError((BedrockConnect.language.getWording("error", "invalidServerRemove")));
                    }
                    break;
                case UIForms.ERROR:
                    // Reopen previous form before error
                    player.openForm(player.getCurrentForm());
                    break;
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NetworkStackLatencyPacket packet) {
        // Fix bug where server icons don't load
        UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
        updateAttributesPacket.setRuntimeEntityId(1);
        List<AttributeData> attributes = Collections.singletonList(new AttributeData("minecraft:player.level", 0f, 24791.00f, 0, 0f));
        updateAttributesPacket.setAttributes(attributes);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() { if(session.isConnected()) { session.sendPacket(updateAttributesPacket); } }
        };
        timer.schedule(task, 500);

        return PacketSignal.HANDLED;
    }

    public void transfer(String ip, int port) {
        try {
            TransferPacket tp = new TransferPacket();
            tp.setAddress(ip);
            tp.setPort(port);
            session.sendPacketImmediately(tp);
        } catch (Exception e) {
            player.createError(BedrockConnect.language.getWording("error", "transferError"));
        }
    }

    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
        session.sendPacketImmediately(UIForms.createMain(player.getServerList(), session));
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        int protocolVersion = packet.getProtocolVersion();

        BedrockCodec packetCodec = BedrockProtocol.getBedrockCodec(packet.getProtocolVersion());

        if (packetCodec == null) {
            PlayStatusPacket status = new PlayStatusPacket();
            if (protocolVersion > BedrockProtocol.DEFAULT_BEDROCK_CODEC.getProtocolVersion()) {
                status.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
            } else {
                status.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            }

            session.sendPacketImmediately(status);
            return PacketSignal.HANDLED;
        }
        session.setCodec(packetCodec);

        PacketCompressionAlgorithm algorithm = PacketCompressionAlgorithm.ZLIB;

        NetworkSettingsPacket responsePacket = new NetworkSettingsPacket();
        responsePacket.setCompressionAlgorithm(algorithm);
        responsePacket.setCompressionThreshold(0);
        session.sendPacketImmediately(responsePacket);

        session.setCompression(algorithm);
        return PacketSignal.HANDLED;
    }

    public PacketHandler(BedrockServerSession session, Server server, boolean packetListening) {
        this.session = session;
        this.server = server;
    }

    @Override
    public void onDisconnect(String reason) {
        System.out.println(name + " disconnected");
        if(player != null)
            server.removePlayer(player);
    }

    private static boolean validateChainData(List<SignedJWT> chain) throws Exception {
        if (chain.size() != 3) {
            return false;
        }

        Payload identity = null;
        ECPublicKey lastKey = null;
        boolean mojangSigned = false;
        Iterator<SignedJWT> iterator = chain.iterator();
        while (iterator.hasNext()) {
            SignedJWT jwt = iterator.next();
            identity = jwt.getPayload();

            // x509 cert is expected in every claim
            URI x5u = jwt.getHeader().getX509CertURL();
            if (x5u == null) {
                return false;
            }

            ECPublicKey expectedKey = EncryptionUtils.generateKey(jwt.getHeader().getX509CertURL().toString());
            // First key is self-signed
            if (lastKey == null) {
                lastKey = expectedKey;
            } else if (!lastKey.equals(expectedKey)) {
                return false;
            }

            if (!EncryptionUtils.verifyJwt(jwt, lastKey)) {
                return false;
            }

            if (mojangSigned) {
                return !iterator.hasNext();
            }

            if (lastKey.equals(EncryptionUtils.getMojangPublicKey())) {
                mojangSigned = true;
            }

            Object payload = JSONValue.parse(jwt.getPayload().toString());
            Preconditions.checkArgument(payload instanceof JSONObject, "Payload is not an object");

            Object identityPublicKey = ((JSONObject) payload).get("identityPublicKey");
            Preconditions.checkArgument(identityPublicKey instanceof String, "identityPublicKey node is missing in chain");
            lastKey = EncryptionUtils.generateKey((String) identityPublicKey);
        }

        return mojangSigned;
    }


    private static boolean verifyJwt(JWSObject jwt, ECPublicKey key) throws JOSEException {
        return jwt.verify(new DefaultJWSVerifierFactory().createJWSVerifier(jwt.getHeader(), key));
    }

    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        return PacketSignal.UNHANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
        switch (packet.getStatus()) {
            case COMPLETED:
                BedrockConnect.data.userExists(uuid, name, session, this);
                break;
            case HAVE_ALL_PACKS:
                ResourcePackStackPacket rs = new ResourcePackStackPacket();
                //rs.setExperimental(false);
                rs.setForcedToAccept(false);
                rs.setGameVersion("*");
                session.sendPacket(rs);
                break;
            default:
                session.disconnect("disconnectionScreen.resourcePack");
                break;
        }

        return PacketSignal.HANDLED;
    }

    // Heavily referenced from https://github.com/NukkitX/ProxyPass/blob/master/src/main/java/com/nukkitx/proxypass/network/bedrock/session/UpstreamPacketHandler.java

    @Override
    public PacketSignal handle(LoginPacket packet) {

        List<SignedJWT> certChainData = packet.getChain();

        try {
            JWSObject jwt = certChainData.get(certChainData.size() - 1);
            JsonNode payload = Server.JSON_MAPPER.readTree(jwt.getPayload().toBytes());

            if (payload.get("extraData").getNodeType() != JsonNodeType.OBJECT) {
                throw new RuntimeException("AuthData was not found!");
            }

            extraData = (JSONObject) jwt.getPayload().toJSONObject().get("extraData");

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }
            ECPublicKey identityPublicKey = EncryptionUtils.generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = packet.getExtra();
            verifyJwt(clientJwt, identityPublicKey);

            System.out.println("Made it through login - " + "User: " + extraData.getAsString("displayName") + " (" + extraData.getAsString("identity") + ")");


            name = extraData.getAsString("displayName");
            uuid = extraData.getAsString("identity");
            
            
            // Whitelist check
            if (Whitelist.hasWhitelist() && !Whitelist.isPlayerWhitelisted(name)) {
            	session.disconnect(Whitelist.getWhitelistMessage());
            	System.out.println("Kicked " + name + ": \"" + Whitelist.getWhitelistMessage() + "\"");
            }

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
        return PacketSignal.HANDLED;
    }

    // Handle rest of packets to avoid log warnings

    @Override
    public PacketSignal handle(AdventureSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }


    @Override
    public PacketSignal handle(BlockEntityDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BlockPickRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BookEditPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientCacheBlobStatusPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientCacheMissResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientCacheStatusPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientToServerHandshakePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandBlockUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerClosePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CraftingEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EntityEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EntityPickRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InteractPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryContentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventorySlotPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryTransactionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemFrameDropItemPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LabTablePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LecternUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelEventGenericPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelSoundEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MapInfoRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobArmorEquipmentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobEquipmentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityAbsolutePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PhotoTransferPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerHotbarPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerInputPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerSkinPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PurchaseReceiptPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackChunkRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RiderJumpPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerSettingsRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDefaultGameTypePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetPlayerGameTypePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SubClientLoginPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddBehaviorTreePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddHangingEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddItemEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPaintingPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPlayerPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AvailableCommandsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BlockEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BossEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CameraPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ChangeDimensionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ChunkRadiusUpdatedPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientboundMapItemDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandOutputPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerOpenPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerSetDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CraftingDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ExplodePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelChunkPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(GameRulesChangedPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(GuiDataPickItemPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(HurtArmorPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AutomationClientConnectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MapCreateLockedCopyPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobEffectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ModalFormRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityDeltaPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NpcRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(OnScreenTextureAnimationPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerListPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlaySoundPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayStatusPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RemoveEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RemoveObjectivePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackChunkDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackDataInfoPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePacksInfoPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackStackPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RespawnPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ScriptCustomEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerSettingsResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetCommandsEnabledPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDifficultyPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDisplayObjectivePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityLinkPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityMotionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetHealthPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetLastHurtByPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetScoreboardIdentityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetScorePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetSpawnPositionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetTimePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetTitlePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowCreditsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowProfilePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowStoreOfferPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SimpleEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SpawnExperienceOrbPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StartGamePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StopSoundPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StructureBlockUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StructureTemplateDataRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StructureTemplateDataResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TakeItemEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TransferPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateAttributesPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPropertiesPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockSyncedPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateEquipPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateSoftEnumPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateTradePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AvailableEntityIdentifiersPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BiomeDefinitionListPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelSoundEvent2Packet packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NetworkChunkPublisherUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SpawnParticleEffectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(VideoStreamConnectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EmotePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TickSyncPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AnvilDamagePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SettingsCommandPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EducationSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CompletedUsingItemPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MultiplayerSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

    // 1.16 new packets

    @Override
    public PacketSignal handle(DebugInfoPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EmoteListPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CodeBuilderPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CreativeContentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemStackRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelSoundEvent1Packet packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemStackResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerArmorDamagePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerEnchantOptionsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdatePlayerGameTypePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PacketViolationWarningPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PositionTrackingDBClientRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PositionTrackingDBServerBroadcastPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MotionPredictionHintsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AnimateEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CameraShakePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerFogPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CorrectPlayerMovePredictionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemComponentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(FilterTextPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RequestAbilityPacket packet) {
        return PacketSignal.HANDLED;
    }
}
