package main.com.pyratron.pugmatt.bedrockconnect.config.Custom;

import java.util.ArrayList;

public class CustomServerGroup extends CustomEntry {
    private ArrayList<CustomServer> content;

    public CustomServerGroup(String name, String iconUrl, ArrayList<CustomServer> content) {
        super(name, iconUrl);

        this.content = content;
    }

    public ArrayList<CustomServer> getServers() {
        return content;
    }
}
