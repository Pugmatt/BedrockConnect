package main.com.pyratron.pugmatt.bedrockconnect.config;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;


public class Whitelist {

	private List<String> whitelist = null;
	private String whitelistMessage = "You are not whitelisted on this server";

	public Whitelist(String whitelistFile) {
		if (whitelistFile == null) return;
		
		try {
			File file = new File(whitelistFile);
			whitelist =  Files.readAllLines(file.toPath());
			BedrockConnect.logger.debug("Whitelist data: " + whitelist.toString());
		} catch (Exception e) {
			BedrockConnect.logger.error("Error loading whitelist", e);
			System.exit(1);
		}
	}

	public boolean hasWhitelist() {
		return whitelist != null;
	}

	public List<String> getPlayers() {
		return whitelist;
	}

	public boolean isPlayerWhitelisted(String name) {
		return whitelist.contains(name);
	}

	public String getWhitelistMessage() {
		return whitelistMessage;
	}
}