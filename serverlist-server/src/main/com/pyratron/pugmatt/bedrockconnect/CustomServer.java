package main.com.pyratron.pugmatt.bedrockconnect;

public class CustomServer {
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
		return iconUrl;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
