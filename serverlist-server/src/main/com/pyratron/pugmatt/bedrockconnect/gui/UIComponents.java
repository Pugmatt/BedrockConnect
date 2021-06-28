package main.com.pyratron.pugmatt.bedrockconnect.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nukkitx.protocol.bedrock.packet.ModalFormRequestPacket;
import net.minidev.json.JSONUtil;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class UIComponents {

    public static JsonObject createLabel(String text) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "label");
        obj.addProperty("text", text);
        return obj;
    }

    public static JsonObject createInput(String text, String placeholder) {
        JsonObject input = new JsonObject();
        input.addProperty("type", "input");
        input.addProperty("placeholder", placeholder);
        input.addProperty("text", text);
        return input;
    }

    public static JsonObject createInput(String text, String placeholder, String defaultValue) {
        JsonObject input = new JsonObject();
        input.addProperty("type", "input");
        input.addProperty("placeholder", placeholder);
        input.addProperty("text", text);
        input.addProperty("default", defaultValue);
        return input;
    }

    public static JsonObject createButton(String text, String image, String imageType) {
        JsonObject button = new JsonObject();
        button.addProperty("type", "button");
        button.addProperty("text", text);
        if(image != null && imageType != null) {
            JsonObject buttonImage = new JsonObject();
            buttonImage.addProperty("type", imageType);
            buttonImage.addProperty("data", image);
            button.add("image", buttonImage);
        }
        return button;
    }

    public static JsonObject createButton(String text) {
        JsonObject button = new JsonObject();
        button.addProperty("type", "button");
        button.addProperty("text", text);
        return button;
    }

    public static JsonObject createToggle(String text) {
        JsonObject toggle = new JsonObject();
        toggle.addProperty("type", "toggle");
        toggle.addProperty("text", text);
        toggle.addProperty("defaultValue", false);
        return toggle;
    }

    public static JsonObject createToggle(String text, boolean toggled) {
        JsonObject toggle = new JsonObject();
        toggle.addProperty("type", "toggle");
        toggle.addProperty("text", text);
        toggle.addProperty("defaultValue", toggled);
        return toggle;
    }

    public static JsonObject createForm(String type, String title) {
        JsonObject form = new JsonObject();
        form.addProperty("type", type);
        form.addProperty("title", title);
        return form;
    }

    public static JsonObject createDropdown(List<String> options, String title, String defaultIndex) {
        JsonObject dropdown = new JsonObject();
        dropdown.addProperty("type", "dropdown");
        JsonArray servers = new JsonArray();
        for(int i=0;i<options.size();i++) {
            servers.add(options.get(i));
        }
        dropdown.add("options", servers);
        dropdown.addProperty("text", title);
        dropdown.addProperty("defaultOptionIndex", defaultIndex);
        return dropdown;
    }

    public static String serversToFormData(List<String> list) {
        String listString = "[";
        for(int i=0;i<list.size();i++) {
            listString += '"' + list.get(i) + '"';
            if(i != list.size()-1)
                listString += ",";
        }
        listString += "]";
        return listString;
    }

    public static ArrayList<String> getFormData(String data) {
        JSONParser parser = new JSONParser();

        // If no server data
        if(data == null)
            return new ArrayList<>();

        try {
            JSONArray obj = (JSONArray) parser.parse(data);
            ArrayList<String> strings = new ArrayList<>();
            for(int i=0;i<obj.size();i++) {
                strings.add(obj.get(i).toString());
            }
            return strings;
        } catch(ParseException e) {
            System.out.println(e.toString());
        }

        return null;
    }

    /**
     * If server display name is included in server data, return this value
     * If not stored, use server address as display name
     * @param server
     * @return
     */
    public static String getServerDisplayName(String server) {
        String displayName;
        String[] serverParts = server.split(":");
        if(serverParts.length > 2)
            displayName = serverParts[2];
        else
            displayName = server;
        return displayName;
    }
}
