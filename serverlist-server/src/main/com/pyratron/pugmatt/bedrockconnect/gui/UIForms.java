package main.com.pyratron.pugmatt.bedrockconnect.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nukkitx.protocol.bedrock.packet.ModalFormRequestPacket;

public class UIForms {
    public static final int ERROR = 2, MAIN = 0, DIRECT_CONNECT = 1;

    public static int currentForm = 0;

    public static ModalFormRequestPacket createMain() {
        currentForm = MAIN;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.MAIN);

        JsonObject out = UIComponents.createForm("form", "ServerList");
        out.addProperty("content", "");

        JsonArray buttons = new JsonArray();

        buttons.add(UIComponents.createButton("Direct Connect"));
        buttons.add(UIComponents.createButton("Play.SkyBlockpe.com:19132", "https://i.imgur.com/3BmFZRE.png", "url"));
        buttons.add(UIComponents.createButton("Add Server"));
        buttons.add(UIComponents.createButton("Remove Server"));

        out.add("buttons", buttons);

        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createDirectConnect() {
        currentForm = DIRECT_CONNECT;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.DIRECT_CONNECT);
        JsonObject out = UIComponents.createForm("custom_form", "Add Server");

        JsonArray inputs = new JsonArray();

        inputs.add(UIComponents.createInput("Server Address", "Please enter IP or Address"));
        inputs.add(UIComponents.createInput("Server Port", "Please enter Port (E.g. 19132)"));

        out.add("content", inputs);
        mf.setFormData(out.toString());

        return mf;
    }
}
