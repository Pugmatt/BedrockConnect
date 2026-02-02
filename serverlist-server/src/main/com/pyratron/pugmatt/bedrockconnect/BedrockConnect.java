package main.com.pyratron.pugmatt.bedrockconnect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import main.com.pyratron.pugmatt.bedrockconnect.data.DataUtil;
import main.com.pyratron.pugmatt.bedrockconnect.data.Database;
import main.com.pyratron.pugmatt.bedrockconnect.data.DatabaseTypes;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;
import main.com.pyratron.pugmatt.bedrockconnect.server.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BedrockConnect {
    private static String release = "1.64";

    private static Config config;
    private static Database database;
    private static DataUtil data;
    private static Server server;

    public static Logger logger = LoggerFactory.getLogger(BedrockConnect.class);

    public static void main(String[] args) {
        BedrockConnect.logger.info(
            LogColors.cyan("-= BedrockConnect ") + "( " + LogColors.cyan("Release: ") + LogColors.purple(release) + " )"  + LogColors.cyan(" =-")
        );

        try {
            HashMap<String, String> settings = new HashMap<>();

            boolean settingsArgs = false;
            boolean settingsFile = false;
            boolean settingsEnv = false;

            // Find any settings in startup arguments
            for(String str : args) {
                if(str.indexOf("=") !=  -1 && str.indexOf("=") < str.length() - 1) {
                    settingsArgs = true;
                    settings.put(str.substring(0, str.indexOf("=")), str.substring(str.indexOf("=") + 1));
                }
            }

            // Find any settings in configuration file
            File configFile = new File("config.yml");
            if(configFile.exists() && !configFile.isDirectory()) {
                settingsFile = true;
                try {
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    Map<String, Object> config = mapper.readValue(configFile, Map.class);
                    for (String configKey : config.keySet()) {
                        settings.put(configKey.toLowerCase(), config.get(configKey).toString());
                    }
                } catch(Exception e) {
                    BedrockConnect.logger.error("Issue parsing configuration file", e);
                    System.exit(1);
                }
            }

            // Find any settings in environment variables
            try {
                Map<String, String> env = System.getenv();
                for (String envName : env.keySet()) {
                    if (envName.toLowerCase().startsWith("bc_")) {
                        settingsEnv = true; 
                        settings.put(envName.toLowerCase().replace("bc_", ""), env.get(envName));
                    }
                }
            } catch(SecurityException e) {}       

            config = Config.init();
            config.load(settings, settingsArgs, settingsFile, settingsEnv);

            server = new Server(config.getBindIp(), config.getPort());
        } catch(Exception e) {
            BedrockConnect.logger.error("An error occured", e);
        }

    }

    public static Config getConfig() {
        return config;
    }

    public static DataUtil getDataUtil() {
        return data;
    }

    public static Server getServer() {
        return server;
    }

    public static void loadDatasource(String hostname, String databaseName, String username, String password, DatabaseTypes databaseType, boolean autoReconnect, boolean usingDatabase) {
        if (database != null) return;

        if(usingDatabase) {
            BedrockConnect.logger.info("Player data storage: " + LogColors.purple("Database"));

            database = new Database(hostname, databaseName, username, password, databaseType, autoReconnect);
            database.openConnection();

            data = new DataUtil(database);

            // Keep SQL connection alive
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                int sec;

                public void run() {
                    try {
                        Connection connection = database.getConnection();
                        if (connection == null || connection.isClosed()) {
                            connection = database.openConnection();
                        } else {
                            if (sec == 600) {
                                try {
                                    ResultSet rs = connection
                                            .createStatement()
                                            .executeQuery(
                                                    "SELECT 1");
                                    rs.next();
                                } catch (SQLException e) {
                                    BedrockConnect.logger.error("Error refreshing SQL connection", e);
                                }
                                sec = 0;
                            }
                        }
                    } catch (SQLException e) {
                        BedrockConnect.logger.error("Error refreshing SQL connection", e);
                    }
                    sec++;
                }
            };
            timer.scheduleAtFixedRate(task, 0L, 60 * 1000);
        } else {
            BedrockConnect.logger.info("Player data storage: " + LogColors.cyan("Files"));
            data = new DataUtil(null);
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() { }
            };
            timer.scheduleAtFixedRate(task, 0L, 1200L);
        }
    }

}
