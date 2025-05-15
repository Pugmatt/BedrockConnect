package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import main.com.pyratron.pugmatt.bedrockconnect.*;
import main.com.pyratron.pugmatt.bedrockconnect.gui.MainFormButton;
import main.com.pyratron.pugmatt.bedrockconnect.gui.ManageFormButton;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;
import main.com.pyratron.pugmatt.bedrockconnect.utils.BedrockProtocol;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.bedrock.util.JsonUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.Preconditions;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.net.URI;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PacketHandler implements BedrockPacketHandler {

    private Server server;
    private BedrockServerSession session;

    private String name;
    private String uuid;

    private BCPlayer player;

    private JSONObject extraData;

    // Used for server icon fix
    private ScheduledThreadPoolExecutor executor = null;

    public void setPlayer(BCPlayer player) {
        this.player = player;
    }

    public String getIP(String hostname) {
        try {
            if(BedrockConnect.fetchFeaturedIps || BedrockConnect.fetchIps) {
                InetAddress host = InetAddress.getByName(hostname);
                String address = host.getHostAddress();
                BedrockConnect.logger.debug("Retrieved " + address + " host address from hostname " + hostname);
                return address;
            } else {
                return BedrockConnect.featuredServerIps.get(hostname);
            }
        } catch (UnknownHostException ex) {
            BedrockConnect.logger.error("Error retrieving IP from hostname", ex);
        }
        return hostname;
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        if (BedrockConnect.debug && !(packet instanceof PlayerAuthInputPacket)) {
            String id = session.getSocketAddress().toString();
            if (name != null) {
                id = name;
            }
            if (packet instanceof LoginPacket) {
                BedrockConnect.logger.debug(LogColors.gray("[ " + id + " ] " + "LoginPacket"));
            } else {
                BedrockConnect.logger.debug(LogColors.gray("[ " + id + " ] " + packet));
            }
        }
        BedrockPacketHandler.super.handlePacket(packet);
        return PacketSignal.HANDLED;
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
                                    case 1: // Cubecraft
                                        transfer(!BedrockConnect.fetchFeaturedIps ? getIP("mco.cubecraft.net") : "mco.cubecraft.net", 19132);
                                        break;
                                    case 2: // Lifeboat
                                        transfer(getIP("mco.lbsg.net"), 19132);
                                        break;
                                    case 3: // Mineville
                                        transfer(getIP("play.inpvp.net"), 19132);
                                        break;
                                    case 4: // Galaxite
                                        transfer(getIP("play.galaxite.net"), 19132);
                                        break;
                                    case 5: // Enchanted Dragons
                                        transfer(getIP("play.enchanted.gg"), 19132);
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
                    try {
                        if (packet.getFormData() == null || packet.getFormData().contains("null")) {
                            if (player.getCurrentForm() != packet.getFormId())
                                return PacketSignal.HANDLED;
                            player.openForm(UIForms.MANAGE_SERVER);
                        } else {
                            ArrayList<String> data = UIComponents.getFormData(packet.getFormData());

                            int chosen = Integer.parseInt(data.get(0));

                            String server = player.getServerList().get(chosen);

                            String[] serverInfo = UIComponents.validateAddress(server, player);

                            if (serverInfo != null) {
                                String ip = serverInfo[0];
                                String port = serverInfo[1];
                                String name = serverInfo.length > 2 ? serverInfo[2] : "";

                                player.setEditingServer(chosen);

                                session.sendPacketImmediately(UIForms.createEditServer(ip, port, name));
                                player.setCurrentForm(UIForms.EDIT_SERVER);
                            }
                        }
                    } catch(Exception e) {
                        player.createError((BedrockConnect.language.getWording("error", "invalidServerEdit")));
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

        if (executor == null)
            executor = new ScheduledThreadPoolExecutor(1);

        executor.schedule(() -> {
            session.sendPacket(updateAttributesPacket);
        }, 500, TimeUnit.MILLISECONDS);

        return PacketSignal.HANDLED;
    }

    public void transfer(String ip, int port) {
        try {
            TransferPacket tp = new TransferPacket();
            if(BedrockConnect.fetchIps && UIComponents.isDomain(ip)) {
                tp.setAddress(getIP(ip));
            } else {
                tp.setAddress(ip);
            }
            tp.setPort(port);
            session.sendPacketImmediately(tp);
            BedrockConnect.logger.debug("Transferred player " + name + " to " + tp.getAddress() + ":" + tp.getPort());
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
        if(executor != null)
            executor.shutdown();
        if(player != null)
            server.removePlayer(player);
         BedrockConnect.logger.info("[ " + LogColors.cyan(server.getPlayers().size() + " online") + " ] Player disconnected: " + name + " (xuid: " + uuid + ")");
    }

    private boolean verifyJwt(String jwt, PublicKey key) throws JoseException {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setKey(key);
        jws.setCompactSerialization(jwt);

        return jws.verifySignature();
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
        try {
            ChainValidationResult chain = EncryptionUtils.validateChain(packet.getChain());
            JsonNode payload = Server.JSON_MAPPER.valueToTree(chain.rawIdentityClaims());

            if (payload.get("extraData").getNodeType() != JsonNodeType.OBJECT) {
                throw new RuntimeException("AuthData was not found!");
            }

            extraData = new JSONObject(JsonUtils.childAsType(chain.rawIdentityClaims(), "extraData", Map.class));

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }
            ECPublicKey identityPublicKey = EncryptionUtils.parseKey(payload.get("identityPublicKey").textValue());

            String clientJwt = packet.getExtra();
            verifyJwt(clientJwt, identityPublicKey);

            BedrockConnect.logger.debug("Player made it through login: " + extraData.get("displayName") + " (xuid: " + extraData.get("identity") + ")");


            name = (String) extraData.get("displayName");
            uuid = (String) extraData.get("identity");
            
            
            // Whitelist check
            if (Whitelist.hasWhitelist() && !Whitelist.isPlayerWhitelisted(name)) {
            	session.disconnect(Whitelist.getWhitelistMessage());
            	BedrockConnect.logger.info("Kicked " + name + " (xuid: " + uuid + "): \"" + Whitelist.getWhitelistMessage() + "\"");
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
            resourcePacksInfo.setWorldTemplateId(UUID.randomUUID());
            resourcePacksInfo.setWorldTemplateVersion("*");
            session.sendPacket(resourcePacksInfo);
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return PacketSignal.HANDLED;
    }
}
