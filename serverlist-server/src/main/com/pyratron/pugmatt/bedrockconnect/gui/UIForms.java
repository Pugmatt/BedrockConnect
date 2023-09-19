package main.com.pyratron.pugmatt.bedrockconnect.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import main.com.pyratron.pugmatt.bedrockconnect.*;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.NetworkStackLatencyPacket;

import java.util.ArrayList;
import java.util.List;

public class UIForms {
    public static final int ERROR = 2, MAIN = 0, DIRECT_CONNECT = 1, REMOVE_SERVER = 3, MANAGE_SERVER = 4, EDIT_SERVER = 5, EDIT_CHOOSE_SERVER = 6, ADD_SERVER = 7, SERVER_GROUP = 8;

    public static int currentForm = 0;

    public static JsonArray mainMenuButtons = new JsonArray();
    public static JsonArray manageListButtons = new JsonArray();
    public static JsonArray featuredServerButtons = new JsonArray();

    public static final int DEFAULT_PORT = 19132;

    static {
        mainMenuButtons.add(UIComponents.createButton(BedrockConnect.language.getWording("main", "connectBtn")));
        mainMenuButtons.add(UIComponents.createButton(BedrockConnect.language.getWording("main", "manageBtn")));
        mainMenuButtons.add(UIComponents.createButton(BedrockConnect.language.getWording("main", "exitBtn")));

        // Add deprecated support if "removeBtn" text is still in "main" rootKey
        String removeBtnText = !BedrockConnect.language.getWording("manage", "removeBtn").equals("N/A") ? BedrockConnect.language.getWording("manage", "removeBtn")
                : BedrockConnect.language.getWording("main", "removeBtn");
        manageListButtons.add(UIComponents.createButton(BedrockConnect.language.getWording("manage", "addBtn")));
        manageListButtons.add(UIComponents.createButton(BedrockConnect.language.getWording("manage", "editBtn")));
        manageListButtons.add(UIComponents.createButton(removeBtnText));

        featuredServerButtons.add(UIComponents.createButton("The Hive", "https://forum.playhive.com/uploads/default/original/1X/0d05e3240037f7592a0f16b11b57c08eba76f19c.png", "url"));
        featuredServerButtons.add(UIComponents.createButton("CubeCraft Games", "https://i.imgur.com/aFH1NUr.png", "url"));
        featuredServerButtons.add(UIComponents.createButton("Lifeboat Network", "https://i.imgur.com/LoI7bYx.png", "url"));
        featuredServerButtons.add(UIComponents.createButton("Mineville", "https://i.imgur.com/0K4TDut.png", "url"));
        featuredServerButtons.add(UIComponents.createButton("Galaxite", "https://i.imgur.com/VxXO8Of.png", "url"));
        featuredServerButtons.add(UIComponents.createButton("Pixel Paradise", "https://i.imgur.com/IMe5NSf.jpg", "url"));
    }

    public static ModalFormRequestPacket createMain(List<String> servers, BedrockServerSession session) {
        currentForm = MAIN;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.MAIN);

        JsonObject out = UIComponents.createForm("form", BedrockConnect.language.getWording("main", "heading"));
        out.addProperty("content", "");

        JsonArray buttons = new JsonArray();
        CustomEntry[] customServers = CustomServerHandler.getServers();

        if(BedrockConnect.userServers)
            buttons.addAll(mainMenuButtons);
        else
            buttons.add(UIComponents.createButton(BedrockConnect.language.getWording("main", "exitBtn")));

        for(int i=0;i<servers.size();i++) {
            buttons.add(UIComponents.createButton(UIComponents.getServerDisplayName(servers.get(i)), "https://i.imgur.com/nhumQVP.png", "url"));
        }

        for (CustomEntry cs : customServers) {
            buttons.add(UIComponents.createButton(cs.getName(), cs.getIconUrl(), "url"));
        }

        if(BedrockConnect.featuredServers) {
            buttons.addAll(featuredServerButtons);
        }

        out.add("buttons", buttons);

        mf.setFormData(out.toString());

        fixIcons(session);

        return mf;
    }

    public static ModalFormRequestPacket createServerGroup(CustomServerGroup group, BedrockServerSession session) {
        currentForm = SERVER_GROUP;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.SERVER_GROUP);

        JsonObject out = UIComponents.createForm("form", group.getName());
        out.addProperty("content", "");

        JsonArray buttons = new JsonArray();

        buttons.add(UIComponents.createButton(BedrockConnect.language.getWording("serverGroup", "backBtn")));

        for (CustomServer cs : group.getServers()) {
            buttons.add(UIComponents.createButton(cs.getName(), cs.getIconUrl(), "url"));
        }

        out.add("buttons", buttons);

        mf.setFormData(out.toString());

        fixIcons(session);

        return mf;
    }

    public static void fixIcons(BedrockServerSession session) {
        // Fix icons not loading
        NetworkStackLatencyPacket networkStackLatencyPacket = new NetworkStackLatencyPacket();
        networkStackLatencyPacket.setFromServer(true);
        networkStackLatencyPacket.setTimestamp(System.currentTimeMillis());
        session.sendPacket(networkStackLatencyPacket);
    }

    public static int getServerIndex(int btnId, CustomEntry[] customServers, List<String> playerServers) {
        int serverIndex;

        if(BedrockConnect.userServers) {
            // Set server index if button selected is not a menu option
            serverIndex = btnId - mainMenuButtons.size();
        } else {
            serverIndex = btnId - 1;
        }

        return serverIndex;
    }

    public static MainFormButton getMainFormButton(int btnId, CustomEntry[] customServers, List<String> playerServers) {
        int serverIndex = getServerIndex(btnId, customServers, playerServers);

        if(BedrockConnect.userServers) {
            switch (btnId) {
                case 0:
                    return MainFormButton.CONNECT;
                case 1:
                    return MainFormButton.MANAGE;
                case 2:
                    return MainFormButton.EXIT;
            }
        }

        if(btnId == 0) {
            return MainFormButton.EXIT;
        } else if(serverIndex + 1 > playerServers.size() + customServers.length) {
            return MainFormButton.FEATURED_SERVER;
        } else if (serverIndex + 1 > playerServers.size() && serverIndex - playerServers.size() < customServers.length) {
            return MainFormButton.CUSTOM_SERVER;
        } else {
            return MainFormButton.USER_SERVER;
        }
    }

    public static ManageFormButton getManageFormButton(int btnId) {
        switch (btnId) {
            case 0:
                return ManageFormButton.ADD;
            case 1:
                return ManageFormButton.EDIT;
            case 2:
                return ManageFormButton.REMOVE;
        }
        return null;
    }

    public static ModalFormRequestPacket createManageList() {
        currentForm = MANAGE_SERVER;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.MANAGE_SERVER);

        JsonObject out = UIComponents.createForm("form", BedrockConnect.language.getWording("manage", "heading"));
        out.addProperty("content", "");

        JsonArray buttons = new JsonArray();
        buttons.addAll(manageListButtons);

        out.add("buttons", buttons);

        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createAddServer() {
        currentForm = ADD_SERVER;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.ADD_SERVER);
        JsonObject out = UIComponents.createForm("custom_form", BedrockConnect.language.getWording("add", "heading"));

        JsonArray inputs = new JsonArray();

        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "addressTitle"), BedrockConnect.language.getWording("connect", "addressPlaceholder")));
        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "portTitle"), BedrockConnect.language.getWording("connect", "portPlaceholder"), Integer.toString(DEFAULT_PORT)));
        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "displayNameTitle"), "", ""));

        out.add("content", inputs);
        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createDirectConnect() {
        currentForm = DIRECT_CONNECT;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.DIRECT_CONNECT);
        JsonObject out = UIComponents.createForm("custom_form", BedrockConnect.language.getWording("connect", "heading"));

        JsonArray inputs = new JsonArray();

        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "addressTitle"), BedrockConnect.language.getWording("connect", "addressPlaceholder")));
        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "portTitle"), BedrockConnect.language.getWording("connect", "portPlaceholder"), Integer.toString(DEFAULT_PORT)));
        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "displayNameTitle"), "", ""));
        inputs.add(UIComponents.createToggle(BedrockConnect.language.getWording("connect", "addToggle")));

        out.add("content", inputs);
        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createEditChooseServer(List<String> servers) {
        currentForm = EDIT_CHOOSE_SERVER;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.EDIT_CHOOSE_SERVER);
        JsonObject out = UIComponents.createForm("custom_form", BedrockConnect.language.getWording("edit", "chooseHeading"));

        JsonArray inputs = new JsonArray();

        List<String> displayServers = new ArrayList<>();
        for(int i = 0; i < servers.size(); i++) {
            displayServers.add(UIComponents.getServerDisplayName(servers.get(i)));
        }
        inputs.add(UIComponents.createDropdown(displayServers,BedrockConnect.language.getWording("edit", "serverDropdown"), "0"));

        out.add("content", inputs);
        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createEditServer(String address, String port, String name) {
        currentForm = EDIT_SERVER;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.EDIT_SERVER);
        JsonObject out = UIComponents.createForm("custom_form", BedrockConnect.language.getWording("edit", "heading"));

        JsonArray inputs = new JsonArray();

        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "addressTitle"), BedrockConnect.language.getWording("connect", "addressPlaceholder"), address));
        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "portTitle"), BedrockConnect.language.getWording("connect", "portPlaceholder"), port));
        inputs.add(UIComponents.createInput(BedrockConnect.language.getWording("connect", "displayNameTitle"), "", name));

        out.add("content", inputs);
        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createRemoveServer(List<String> servers) {
        currentForm = REMOVE_SERVER;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.REMOVE_SERVER);
        JsonObject out = UIComponents.createForm("custom_form", BedrockConnect.language.getWording("remove", "heading"));

        JsonArray inputs = new JsonArray();

        List<String> displayServers = new ArrayList<>();
        for(int i = 0; i < servers.size(); i++) {
            displayServers.add(UIComponents.getServerDisplayName(servers.get(i)));
        }
        inputs.add(UIComponents.createDropdown(displayServers,BedrockConnect.language.getWording("remove", "serverDropdown"), "0"));

        out.add("content", inputs);
        mf.setFormData(out.toString());

        return mf;
    }

    public static ModalFormRequestPacket createError(String text) {
        currentForm = ERROR;
        ModalFormRequestPacket mf = new ModalFormRequestPacket();
        mf.setFormId(UIForms.ERROR);
        JsonObject form = new JsonObject();
        form.addProperty("type", "custom_form");
        form.addProperty("title", BedrockConnect.language.getWording("error", "heading"));
        JsonArray content = new JsonArray();
        content.add(UIComponents.createLabel(text));
        form.add("content", content);
        mf.setFormData(form.toString());
        return mf;
    }
}
