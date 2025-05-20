package main.com.pyratron.pugmatt.bedrockconnect.config.Custom;

public class CustomEntry {
    public final String DEFAULT_ICON = "https://i.imgur.com/3BmFZRE.png";

    private String name;
    private String iconUrl;

    public CustomEntry(String name, String iconUrl) {
        super();

        this.name = name;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        if (iconUrl == null)
            return DEFAULT_ICON;
        else
            return iconUrl;
    }
}
