package main.com.pyratron.pugmatt.bedrockconnect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class Whitelist {

	private static List<String> whitelist = null;
	private static String whitelistMessage = "You are not whitelisted on this server";

	public static void loadWhitelist(String whitelistFile) {
		try {
			File file = new File(whitelistFile);
			whitelist =  Files.readAllLines(file.toPath());
			BedrockConnect.logger.debug("Whitelist data: " + whitelist.toString());
		} catch (Exception e) {
			BedrockConnect.logger.error("Error loading whitelist", e);
			System.exit(1);
		}
	}

	public static boolean hasWhitelist() {
		return whitelist != null;
	}

	public static List<String> getWhitelist() {
		return whitelist;
	}

	public static boolean isPlayerWhitelisted(String name) {
		return whitelist.contains(name);
	}

	public static String getWhitelistMessage() {
		return whitelistMessage;
	}
}