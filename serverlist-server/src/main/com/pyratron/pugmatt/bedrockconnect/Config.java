package main.com.pyratron.pugmatt.bedrockconnect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.cloudburstmc.netty.channel.raknet.RakConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import main.com.pyratron.pugmatt.bedrockconnect.config.CustomServerHandler;
import main.com.pyratron.pugmatt.bedrockconnect.config.Language;
import main.com.pyratron.pugmatt.bedrockconnect.config.Whitelist;
import main.com.pyratron.pugmatt.bedrockconnect.config.Custom.CustomEntry;
import main.com.pyratron.pugmatt.bedrockconnect.data.DatabaseTypes;
import main.com.pyratron.pugmatt.bedrockconnect.logging.LogColors;

public class Config {
    private static Config instance;
    private boolean loaded = false;

    private String port = "19132";
    private String bindIp = "0.0.0.0";
    private String serverLimit = "100";
    private boolean dbAutoReconnect = false;
    private boolean usingDatabase = false;
    private String motdFile = null;
    private String motdMessage = null;
    private boolean motdFirstJoin = true;
    private int motdCooldown = 0;
    private boolean kickInactive = true;
    private boolean userServers = true;
    private boolean featuredServers = true;
    private boolean fetchFeaturedIps = true;
    private boolean debug = false;
    private boolean fetchIps = false;
    private boolean storeDisplayNames = true;
    private int packetLimit = 200;
    private int globalPacketLimit = RakConstants.DEFAULT_GLOBAL_PACKET_LIMIT;
    private HashMap<String, String> featuredServerIps;
    private Whitelist whitelist;
    private Language language;
    private CustomServerHandler customServers;

    private Config() {}

    public static Config init() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void load(Map<String, String> settings, boolean usedArgs, boolean usedFile, boolean usedEnv) {
        if (loaded) return;
        loaded = true;

        String dbHost = "localhost";
        String dbName = "bedrock-connect";
        String dbUser = "root";
        String dbPass = "";
        DatabaseTypes dbType = DatabaseTypes.nosql;

        String customServersFile = null;
        String languageFile = null;
        String whitelistFile = null;

        boolean mysqlSettingWarning = false;    

        for (Map.Entry<String, String> setting : settings.entrySet()) {
            switch(setting.getKey().toLowerCase()) {
                case "db_host":
                    dbHost = setting.getValue();
                    break;
                case "db_db":
                    dbName = setting.getValue();
                    break;
                case "db_user":
                    dbUser = setting.getValue();
                    break;
                case "db_pass":
                    dbPass = setting.getValue();
                    break;
                case "db_type":
                    String type = setting.getValue().toLowerCase();
                    switch(type) {
                        case "none":
                            dbType = DatabaseTypes.nosql;
                            break;
                        case "mysql":
                            dbType = DatabaseTypes.mysql;
                            break;
                        case "mariadb":
                            dbType = DatabaseTypes.mariadb;
                            break;
                        case "postgres":
                            dbType = DatabaseTypes.postgres;
                            break;
                        default:
                            BedrockConnect.logger.error("Unknown database type '" + dbType + "'. Valid values: mysql, postgres, mariadb, none");
                            System.exit(1);
                    }
                    break;
                // Backwards-compatibility for legacy database/mysql settings
                // db_ settings above should be used for any future setups/database-related changes
                case "mysql_host":
                    dbType = DatabaseTypes.mysql;
                    mysqlSettingWarning = true;
                    dbHost = setting.getValue();
                    break;
                case "mysql_db":
                    dbType = DatabaseTypes.mysql;
                    mysqlSettingWarning = true;
                    dbName = setting.getValue();
                    break;
                case "mysql_user":
                    dbType = DatabaseTypes.mysql;
                    mysqlSettingWarning = true;
                    dbUser = setting.getValue();
                    break;
                case "mysql_pass":
                    dbType = DatabaseTypes.mysql;
                    mysqlSettingWarning = true;
                    dbPass = setting.getValue();
                    break;
                //
                case "server_limit":
                    serverLimit = setting.getValue();
                    break;
                case "port":
                    port = setting.getValue();
                    break;
                case "custom_servers":
                    customServersFile = setting.getValue();
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
                        } catch (Exception e) {
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
                    whitelistFile = setting.getValue();
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
                    dbAutoReconnect = setting.getValue().equalsIgnoreCase("true");
                    break;
                case "debug":
                    debug = setting.getValue().equalsIgnoreCase("true");
                    break;
                case "motd":
                    motdFile = setting.getValue();
                    break;
                case "motd_first_join":
                    motdFirstJoin = setting.getValue().equalsIgnoreCase("true");
                    break;
                case "motd_cooldown":
                    motdCooldown = Integer.parseInt(setting.getValue());
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
            + (usedArgs ? " [Startup Args]" : "") 
            + (usedFile ? " [Config File]" : "") 
            + (usedEnv ? " [Env Variables]" : "")
            + "):");
            for (Map.Entry<String, String> setting : settings.entrySet()) {
                BedrockConnect.logger.debug(" - " + setting.getKey() + ": " + (censored.contains(setting.getKey()) ? LogColors.gray("[redacted]") : setting.getValue()));
            }
        }

        if (dbType != DatabaseTypes.nosql) {
            usingDatabase = true;
        }

        if(!usingDatabase) {
            if(mysqlSettingWarning) {
                BedrockConnect.logger.warn("[!!DEPRECATION!!] Your current database settings may not work in future versions");
                BedrockConnect.logger.warn("- mysql_* settings should be replaced with db_* settings");
                BedrockConnect.logger.warn("Learn more here: https://github.com/Pugmatt/BedrockConnect/wiki/Deprecated-Database-Settings");
            }
        }

        customServers = new CustomServerHandler(customServersFile);
        if (customServers.getServers().length > 0) {
            BedrockConnect.logger.info("Loaded {} custom servers", customServers.getServers().length);
        }

        whitelist = new Whitelist(whitelistFile);
        if (whitelist.hasWhitelist()) {
            BedrockConnect.logger.info("Loaded {} whitelisted players", whitelist.getPlayers().size());
        }

        language = new Language(languageFile);

        if(!fetchFeaturedIps) {
            try {
                HashMap<String, String> featuredServerIps = new HashMap<>();

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
                    for (Map.Entry<String, String> server : featuredServerIps.entrySet()) {
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

                this.featuredServerIps = featuredServerIps;
            } catch (Exception e) {
                BedrockConnect.logger.error("An error occurred parsing featured_server_ips.json", e);
                System.exit(1);
            }
        }

        loadMotdMessage();

        BedrockConnect.loadDatasource(dbHost, dbName, dbUser, dbPass, dbType, dbAutoReconnect, usingDatabase);
    }

    public void loadMotdMessage() {        
        if (motdFile != null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(motdFile), "UTF-8"));
                String msg = "";
                String line = "";
                while ((line = in.readLine()) != null) {
                    msg += line + "\n";
                }
                motdMessage = msg + "\n";
                in.close();
                BedrockConnect.logger.info("Loaded MOTD");
                BedrockConnect.logger.debug("MOTD data: " + motdMessage.toString());
            } catch (Exception e) {
                BedrockConnect.logger.error("An error occurred reading the MOTD file", e);
            }
        }
    }

    public void loadMotdMessage(String file) {
        motdFile = file;
        loadMotdMessage();
    }

    public String getPort() {
        return port;
    }

    public String getBindIp() {
        return bindIp;
    }

    public String getServerLimit() {
        return serverLimit;
    }

    public boolean isDbAutoReconnect() {
        return dbAutoReconnect;
    }

    public boolean isUsingDatabase() {
        return usingDatabase;
    }

    public String getMotdMessage() {
        return motdMessage;
    }

    public boolean isShowingMotdFirstJoin() {
        return motdFirstJoin;
    }

    public int getMotdCooldown() {
        return motdCooldown;
    }

    public boolean isMotdCooldownEnabled() {
        return getMotdCooldown() > 0;
    }

    public boolean canKickInactive() {
        return kickInactive;
    }

    public boolean isUserServersEnabled() {
        return userServers;
    }

    public boolean isFeaturedServersEnabled() {
        return featuredServers;
    }

    public boolean canFetchFeaturedIps() {
        return fetchFeaturedIps;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public boolean canFetchIps() {
        return fetchIps;
    }

    public boolean canStoreDisplayNames() {
        return storeDisplayNames;
    }

    public int getPacketLimit() {
        return packetLimit;
    }

    public int getGlobalPacketLimit() {
        return globalPacketLimit;
    }

    public HashMap<String, String> getFeaturedServerIps() {
        return featuredServerIps;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public Language getLanguage() {
        return language;
    }

    public CustomEntry[] getCustomServers() {
        return customServers.getServers();
    }
}
