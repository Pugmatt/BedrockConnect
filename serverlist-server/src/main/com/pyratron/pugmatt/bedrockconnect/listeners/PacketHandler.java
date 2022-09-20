package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.data.AttributeData;
import com.nukkitx.protocol.bedrock.data.PacketCompressionAlgorithm;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import main.com.pyratron.pugmatt.bedrockconnect.*;
import main.com.pyratron.pugmatt.bedrockconnect.gui.MainFormButton;
import main.com.pyratron.pugmatt.bedrockconnect.gui.ManageFormButton;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIForms;
import main.com.pyratron.pugmatt.bedrockconnect.utils.BedrockProtocol;
import net.minidev.json.JSONObject;

import java.io.IOException;
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
    public boolean handle(RequestChunkRadiusPacket packet) {
        ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
        chunkRadiusUpdatePacket.setRadius(packet.getRadius());
        session.sendPacketImmediately(chunkRadiusUpdatePacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);
        return false;
    }

    // Occasionally, a sent form will not correctly send to a player for whatever reason, and they float in space. This works as a way to open the form back up.

    @Override
    public boolean handle(PlayerActionPacket packet) {
        player.movementOpen();
        return false;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        if(packet.getAction() == AnimatePacket.Action.SWING_ARM)
            player.movementOpen();
        return false;
    }

    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        player.setActive();
        player.resetMovementOpen();

        switch (packet.getFormId()) {
                case UIForms.MAIN:
                    // Re-open window if closed
                    if (packet.getFormData() == null || packet.getFormData().contains("null")) {
                        if(player.getCurrentForm() != packet.getFormId())
                            return false;
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
                            return false;
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
                            return false;
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
                                return false;
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
                                return false;
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
                            return false;
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
                            return false;
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
                                return false;
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

        return false;
    }

    @Override
    public boolean handle(NetworkStackLatencyPacket packet) {
        // Fix bug where server icons don't load
        UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
        updateAttributesPacket.setRuntimeEntityId(1);
        List<AttributeData> attributes = Collections.singletonList(new AttributeData("minecraft:player.level", 0f, 24791.00f, 0, 0f));
        updateAttributesPacket.setAttributes(attributes);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() { if(!session.isClosed()) { session.sendPacket(updateAttributesPacket); } }
        };
        timer.schedule(task, 500);

        return false;
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
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        session.sendPacketImmediately(UIForms.createMain(player.getServerList(), session));
        return false;
    }

    @Override
    public boolean handle(RequestNetworkSettingsPacket packet) {
        PacketCompressionAlgorithm algorithm = PacketCompressionAlgorithm.ZLIB;

        NetworkSettingsPacket responsePacket = new NetworkSettingsPacket();
        responsePacket.setCompressionAlgorithm(algorithm);
        responsePacket.setCompressionThreshold(512);
        session.sendPacketImmediately(responsePacket);

        session.setCompression(algorithm);
        return false;
    }

    public PacketHandler(BedrockServerSession session, Server server, boolean packetListening) {
        session.setPacketCodec(BedrockProtocol.DEFAULT_BEDROCK_CODEC);
        this.session = session;
        this.server = server;

        session.addDisconnectHandler((DisconnectReason) -> disconnect());
    }

    public void disconnect() {
        System.out.println(name + " disconnected");
        if(player != null)
            server.removePlayer(player);
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

        return true;
    }

    // Heavily referenced from https://github.com/NukkitX/ProxyPass/blob/master/src/main/java/com/nukkitx/proxypass/network/bedrock/session/UpstreamPacketHandler.java

    @Override
    public boolean handle(LoginPacket packet) {
        BedrockPacketCodec packetCodec = BedrockProtocol.getBedrockCodec(packet.getProtocolVersion());

        if (packetCodec == null) {
            session.setPacketCodec(BedrockProtocol.DEFAULT_BEDROCK_CODEC);

            PlayStatusPacket status = new PlayStatusPacket();

            if (packet.getProtocolVersion() > BedrockProtocol.DEFAULT_BEDROCK_CODEC.getProtocolVersion()) {
                status.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
            }
            else if (packet.getProtocolVersion() < BedrockProtocol.DEFAULT_BEDROCK_CODEC.getProtocolVersion()) {
                status.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            }

            session.sendPacket(status);
        }

        session.setPacketCodec(packetCodec);

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
            
            
            //whitelist check
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
        return true;
    }


}
