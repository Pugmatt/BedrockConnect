package main.com.pyratron.pugmatt.bedrockconnect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import main.com.pyratron.pugmatt.bedrockconnect.config.Language;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.sql.DatabaseTypes;
import main.com.pyratron.pugmatt.bedrockconnect.sql.MySQL;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.cloudburstmc.netty.channel.raknet.RakConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BedrockConnect {


    public static PaletteManager paletteManager;

    public static MySQL MySQL;
    public static Connection connection;
    public static Data data;

    public static Server server;

    public static boolean noDB = false;
    public static String whitelist = null;
    public static String customServers = null;
    public static boolean kickInactive = true;
    public static boolean userServers = true;
    public static boolean featuredServers = true;
    public static boolean fetchFeaturedIps = true;
    public static boolean debug = false;

    public static boolean fetchIps = false;

    public static boolean storeDisplayNames = true;

    public static int packetLimit = 200;

    public static int globalPacketLimit = RakConstants.DEFAULT_GLOBAL_PACKET_LIMIT;

    public static String release = "1.55";

    public static HashMap<String, String> featuredServerIps;

    public static Language language;

    public static Logger logger = LoggerFactory.getLogger(BedrockConnect.class);

    public static void main(String[] args) {
        BedrockConnect.logger.info(
            LogColors.cyan("-= BedrockConnect ") + "( " + LogColors.cyan("Release: ") + LogColors.purple(release) + " )"  + LogColors.cyan(" =-")
        );

        paletteManager =  new PaletteManager();

        try {
            String hostname = "localhost";
            String database = "bedrock-connect";
            String username = "root";
            String password = "";
            String port = "19132";
            String bindIp = "0.0.0.0";
            DatabaseTypes databaseType = DatabaseTypes.mysql;
            boolean autoReconnect = false;

            String serverLimit = "100";

            String languageFile = null;

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

            boolean nodbWarning = true;
            boolean mysqlSettingWarning = false;            

            for (Map.Entry<String, String> setting : settings.entrySet()) {
                switch(setting.getKey().toLowerCase()) {
                    case "db_host":
                        hostname = setting.getValue();
                        break;
                    case "db_db":
                        database = setting.getValue();
                        break;
                    case "db_user":
                        username = setting.getValue();
                        break;
                    case "db_pass":
                        password = setting.getValue();
                        break;
                    case "db_type":
                        nodbWarning = false;
                        String dbType = setting.getValue().toLowerCase();
                        switch(dbType) {
                            case "none":
                                databaseType = DatabaseTypes.nosql;
                                break;
                            case "mysql":
                                databaseType = DatabaseTypes.mysql;
                                break;
                            case "mariadb":
                                databaseType = DatabaseTypes.mariadb;
                                break;
                            case "postgres":
                                databaseType = DatabaseTypes.postgres;
                                break;
                            default:
                                BedrockConnect.logger.error("Unknown database type '" + dbType + "'. Valid values: mysql, postgres, mariadb, none");
                                System.exit(1);
                        }
                        break;
                    // Backwards-compatibility for legacy database/mysql settings
                    // db_ settings above should be used for any future setups/database-related changes
                    case "mysql_host":
                        mysqlSettingWarning = true;
                        hostname = setting.getValue();
                        break;
                    case "mysql_db":
                        mysqlSettingWarning = true;
                        database = setting.getValue();
                        break;
                    case "mysql_user":
                        mysqlSettingWarning = true;
                        username = setting.getValue();
                        break;
                    case "mysql_pass":
                        mysqlSettingWarning = true;
                        password = setting.getValue();
                        break;
                    //
                    case "server_limit":
                        serverLimit = setting.getValue();
                        break;
                    case "port":
                        port = setting.getValue();
                        break;
                    case "nodb":
                        if (setting.getValue().equalsIgnoreCase("true"))
                        {
                            nodbWarning = false;
                            databaseType = DatabaseTypes.nosql;
                            noDB = true;
                        }
                        break;
                    case "custom_servers":
                        customServers = setting.getValue();
                        break;
                    case "generatedns":
                        if(setting.getValue().equalsIgnoreCase("true")) {
                            String ip;
                            try {
                                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                                 System.out.println("Local IPv4 IPs:");
                                while (interfaces.hasMoreElements()) {
                                    NetworkInterface iface = interfaces.nextElement();

                                    if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
                                        continue;

                                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                                    while (addresses.hasMoreElements()) {
                                        InetAddress addr = addresses.nextElement();

                                        if (!(addr instanceof Inet4Address)) continue;

                                        ip = addr.getHostAddress();
                                        System.out.println(" - " + iface.getDisplayName() + ": " + ip);
                                    }
                                }

                                Scanner reader = new Scanner(System.in);  // Reading from System.in
                                System.out.print("\nWhich IP should be used for the DNS records: ");
                                String selectedIP = reader.next().replaceAll("\\s+", "");
                                reader.close();

                                BufferedWriter br = new BufferedWriter(new FileWriter(new File("bc_dns.conf")));
                                br.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                        "<DNSMasqConfig xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                                        "  <DNSMasqEntries>\n" +
                                        "    <DNSMasqHost name=\"hivebedrock.network\" a=\"" + selectedIP + "\" />\n" +
                                        "    <DNSMasqHost name=\"play.inpvp.net\" a=\"" + selectedIP + "\" />\n" +
                                        "    <DNSMasqHost name=\"mco.lbsg.net\" a=\"" + selectedIP + "\" />\n" +
                                        "    <DNSMasqHost name=\"play.lbsg.net\" a=\"" + selectedIP + "\" />\n" +
                                        "    <DNSMasqHost name=\"mco.cubecraft.net\" a=\"" + selectedIP + "\" />\n" +
                                        "    <DNSMasqHost name=\"play.galaxite.net\" a=\"" + selectedIP + "\" />\n" +
                                        "  </DNSMasqEntries>\n" +
                                        "</DNSMasqConfig>");
                                br.close();
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    case "kick_inactive":
                        kickInactive = setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "user_servers":
                        userServers = setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "featured_servers":
                        featuredServers = setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "fetch_featured_ips":
                        fetchFeaturedIps = setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "fetch_ips":
                        fetchIps = setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "whitelist":
                        whitelist = setting.getValue();
                        break;
                    case "language":
                        languageFile = setting.getValue();
                        break;
                    case "bindip":
                        bindIp = setting.getValue();
                        break;
                    case "store_display_names":
                        storeDisplayNames =  setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "packet_limit":
                        packetLimit =  Integer.parseInt(setting.getValue());
                        break;
                    case "global_packet_limit":
                        globalPacketLimit =  Integer.parseInt(setting.getValue());
                        break;
                    case "auto_reconnect":
                        autoReconnect = setting.getValue().equalsIgnoreCase("true");
                        break;
                    case "debug":
                        debug = setting.getValue().equalsIgnoreCase("true");
                        break;
                }
            }

            if (debug) {
                Configurator.setLevel(BedrockConnect.logger.getName(), Level.DEBUG);

                BedrockConnect.logger.debug("Java Version: " + System.getProperty("java.version"));
                BedrockConnect.logger.debug("Java Vendor: " + System.getProperty("java.vendor"));
                BedrockConnect.logger.debug("OS Name: " + System.getProperty("os.name"));

                final List<String> censored = new ArrayList<>(List.of(
                    "db_host",
                    "db_db",
                    "db_user",
                    "db_pass",
                    "mysql_host",
                    "mysql_db",
                    "mysql_user",
                    "mysql_pass"
                ));

               BedrockConnect.logger.debug("Passed-in configuration (Loaded through" 
                + (settingsArgs ? " [Startup Args]" : "") 
                + (settingsFile ? " [Config File]" : "") 
                + (settingsEnv ? " [Env Variables]" : "")
                + "):");
               for (Map.Entry<String, String> setting : settings.entrySet()) {
                 BedrockConnect.logger.debug(" - " + setting.getKey() + ": " + (censored.contains(setting.getKey()) ? LogColors.gray("[redacted]") : setting.getValue()));
               }
            }

            if(!noDB) {
                if(nodbWarning || mysqlSettingWarning) {
                    BedrockConnect.logger.warn("[!!DEPRECATION!!] Your current database settings may not work in future versions");
                    if(mysqlSettingWarning)
                        BedrockConnect.logger.warn("- mysql_* settings should be replaced with db_* settings");
                    if(nodbWarning)
                        BedrockConnect.logger.warn("- db_type should be manually set to mysql");
                    BedrockConnect.logger.warn("Learn more here: https://github.com/Pugmatt/BedrockConnect/wiki/Deprecated-Database-Settings");
                }
            }

            CustomServerHandler.initialize();
            if (CustomServerHandler.getServers().length > 0) {
                BedrockConnect.logger.info("Loaded {} custom servers", CustomServerHandler.getServers().length);
            }

            if (whitelist != null) {
                Whitelist.loadWhitelist(whitelist);
            	BedrockConnect.logger.info("Loaded {} whitelisted players", Whitelist.getWhitelist().size());
            }

            language = new Language(languageFile);

            if(!fetchFeaturedIps) {
                try {
                    featuredServerIps = new HashMap<>();

                    // If the file doesn't already exist, create a configuration file containing the hard-coded IPs
                    // for the featured servers if fetching the featured ips is set to disabled
                    File ipFile = new File("featured_server_ips.json");
                    if (ipFile.createNewFile()) {
                        featuredServerIps.put("hivebedrock.network", "167.114.81.89");
                        featuredServerIps.put("mco.cubecraft.net", "51.178.75.10");
                        featuredServerIps.put("mco.lbsg.net", "142.44.240.96");
                        featuredServerIps.put("play.inpvp.net", "52.234.130.241");
                        featuredServerIps.put("play.galaxite.net", "51.222.8.223");
                        featuredServerIps.put("play.enchanted.gg", "216.39.241.141");

                        JSONObject jo = new JSONObject();
                        for (Map.Entry server : featuredServerIps.entrySet()) {
                            jo.put(server.getKey(), server.getValue());
                        }

                        PrintWriter pw = new PrintWriter("featured_server_ips.json");
                        pw.write(jo.toJSONString());
                        pw.flush();
                        pw.close();
                    } else {
                        Object obj = new JSONParser().parse(new FileReader("featured_server_ips.json"));

                        JSONObject jo = (JSONObject) obj;
                        for (Object server : jo.keySet()) {
                            featuredServerIps.put((String) server, (String) jo.get(server));
                        }
                    }
                } catch (Exception e) {
                    BedrockConnect.logger.error("An error occurred parsing featured_server_ips.json", e);
                    System.exit(1);
                }
            }

            if(!noDB) {
                BedrockConnect.logger.info("Player data storage: " + LogColors.purple("Database"));

                MySQL = new MySQL(hostname, database, username, password, databaseType, autoReconnect);

                connection = null;

                connection = MySQL.openConnection();

                data = new Data(serverLimit, databaseType);

                // Keep MySQL connection alive
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    int sec;

                    public void run() {
                        try {
                            if (connection == null || connection.isClosed()) {
                                connection = MySQL.openConnection();
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
                data = new Data(serverLimit, databaseType);
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() { }
                };
                timer.scheduleAtFixedRate(task, 0L, 1200L);
            }

            server = new Server(bindIp, port);
        } catch(Exception e) {
            BedrockConnect.logger.error("An error occured", e);
        }

    }

    public static String getArgValue(String str, String name) {
        String target = name + "=";
        int index = str.indexOf(target);
        int subIndex = index + target.length();
        return str.substring(subIndex);
    }

}
