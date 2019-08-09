package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.JsonNode;
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
                                String address = server.getPlayer(uuid).getServerList().get(chosen-2);
                                if(address.split(":").length > 1) {
                                    String ip = address.split(":")[0];
                                    String port = address.split(":")[1];

                                    try {
                                        TransferPacket tp = new TransferPacket();
                                        tp.setAddress(ip);
                                        tp.setPort(Integer.parseInt(port));
                                        session.sendPacketImmediately(tp);
                                    } catch(Exception e) {
                                        session.sendPacketImmediately(UIForms.createError("Error connecting to server. Invalid address."));
                                    }
                                } else {
                                    session.sendPacketImmediately(UIForms.createError("Invalid server address"));
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
                                if (!data.get(0).matches("[a-zA-Z.1-9]+"))
                                    session.sendPacketImmediately(UIForms.createError("Enter a valid address. (E.g. play.example.net, 172.16.254.1)"));
                                else if (!data.get(1).matches("[1-9]+"))
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
                                            TransferPacket tp = new TransferPacket();
                                            tp.setAddress(data.get(0).replace(" ", ""));
                                            tp.setPort(Integer.parseInt(data.get(1)));
                                            session.sendPacketImmediately(tp);
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