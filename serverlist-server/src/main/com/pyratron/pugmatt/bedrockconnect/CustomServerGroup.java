package main.com.pyratron.pugmatt.bedrockconnect;

import java.util.ArrayList;
import java.util.List;

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
