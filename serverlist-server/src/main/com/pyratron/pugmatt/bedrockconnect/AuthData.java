package main.com.pyratron.pugmatt.bedrockconnect;

import java.util.UUID;

public class AuthData {
    private final String displayName;
    private final UUID identity;

    public String getDisplayName() {
        return displayName;
    }

    public UUID getIdentity() {
        return identity;
    }

    public String getXuid() {
        return xuid;
    }

    private final String xuid;

    public AuthData(String displayName, UUID identity, String xuid) {
        this.displayName = displayName;
        this.identity = identity;
        this.xuid = xuid;
    }


}