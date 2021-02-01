package main.com.pyratron.pugmatt.bedrockconnect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class Whitelist {

	private static boolean is_whitelist = false;
	private static List<String> whitelist;
	static String whitelist_message = "You are not whitelisted on this server";

	public static void loadWhitelist(File whitelistfile) {
		is_whitelist = true;
		try {
			whitelist =  Files.readAllLines(whitelistfile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//returns whether there is a whitelist.
	public static boolean hasWhitelist() {
		return is_whitelist;
	}

	//returns whitelist list
	public static List<String> getWhitelist() {
		return whitelist;
	}

	//returns true if player name is whitelisted, otherwise returns false.
	public static boolean isPlayerWhitelisted(String name) {
		return whitelist.contains(name);
	}

	public static String getWhitelistMessage() {
		return whitelist_message;
	}
}