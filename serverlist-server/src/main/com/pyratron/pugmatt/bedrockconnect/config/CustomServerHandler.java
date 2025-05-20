package main.com.pyratron.pugmatt.bedrockconnect.config;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.config.Custom.CustomEntry;
import main.com.pyratron.pugmatt.bedrockconnect.config.Custom.CustomServer;
import main.com.pyratron.pugmatt.bedrockconnect.config.Custom.CustomServerGroup;

public class CustomServerHandler {
	private ArrayList<CustomEntry> servers = new ArrayList<>();

	/**
	 * Loads any custom servers into memory
	 */
	public CustomServerHandler(String serverFile) {
		if (serverFile == null) {
			return;
		}

		JSONParser parser = new JSONParser();
		try (Reader reader = new FileReader(serverFile)) {

			JSONArray serverList = (JSONArray) parser.parse(reader);

			Iterator<JSONObject> i = serverList.iterator();
			while (i.hasNext()) {
				JSONObject obj = i.next();
				if(obj.get("content") != null) {
					String name = (String) obj.get("name");
					String iconUrl = (String) obj.get("iconUrl");
					JSONArray content = (JSONArray) obj.get("content");

					ArrayList<CustomServer> groupServers = new ArrayList<>();
					for (Object contentObj : content) {
						groupServers.add(createServer((JSONObject) contentObj));
					}
					servers.add(new CustomServerGroup(name, iconUrl, groupServers));
				} else {
					servers.add(createServer(obj));
				}
			}

			BedrockConnect.logger.debug("Custom server data: " + serverList.toString());

		} catch (Exception e) {
			BedrockConnect.logger.error("Error loading custom servers", e);
			System.exit(1);
		}

	}

	private CustomServer createServer(JSONObject obj) {
		String name = (String) obj.get("name");
		String iconUrl = (String) obj.get("iconUrl");
		String address = (String) obj.get("address");
		int port = ((Long) obj.get("port")).intValue();

		return new CustomServer(name, iconUrl, address, port);
	}

	public CustomEntry[] getServers() {
		CustomEntry[] arr = new CustomEntry[servers.size()];
		arr = servers.toArray(arr);
		return arr;
	}
}
