package main.com.pyratron.pugmatt.bedrockconnect.config.Custom;

public class CustomServer extends CustomEntry {
	private String address;
	private int port;

	public CustomServer(String name, String iconUrl, String address, int port) {
		super(name, iconUrl);

		this.address = address;
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
