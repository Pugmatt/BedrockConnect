package main.com.pyratron.pugmatt.bedrockconnect;

public class CustomServer {
	public final String DEFAULT_ICON = "https://i.imgur.com/3BmFZRE.png";

	private String name;
	private String iconUrl;
	private String address;
	private int port;

	public CustomServer(String name, String iconUrl, String address, int port) {
		super();

		this.name = name;
		this.iconUrl = iconUrl;
		this.address = address;
		this.port = port;
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

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
