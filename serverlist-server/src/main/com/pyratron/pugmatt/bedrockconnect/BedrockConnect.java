package main.com.pyratron.pugmatt.bedrockconnect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import main.com.pyratron.pugmatt.bedrockconnect.config.Language;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.sql.DatabaseTypes;
import main.com.pyratron.pugmatt.bedrockconnect.sql.MySQL;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;
import org.cloudburstmc.netty.channel.raknet.RakConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    public static boolean whitelist = false;
    public static String customServers = null;
    public static boolean kickInactive = true;
    public static boolean userServers = true;
    public static boolean featuredServers = true;
    public static boolean fetchFeaturedIps = true;

    public static boolean fetchIps = false;

    public static boolean storeDisplayNames = true;
    public static File whitelistfile;

    public static int packetLimit = 200;

    public static int globalPacketLimit = RakConstants.DEFAULT_GLOBAL_PACKET_LIMIT;

    public static String release = "1.54.1";

    public static HashMap<String, String> featuredServerIps;

    public static Language language;

    public static void main(String[] args) {
        System.out.println("-= BedrockConnect (Release: " + release + ") =-");
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

            // Find any settings in startup arguments
            for(String str : args) {
                if(str.indexOf("=") !=  -1 && str.indexOf("=") < str.length() - 1) {
                    settings.put(str.substring(0, str.indexOf("=")), str.substring(str.indexOf("=") + 1));
                }
            }

            // Find any settings in configuration file
            File configFile = new File("config.yml");
            if(configFile.exists() && !configFile.isDirectory()) {
                try {
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    Map<String, Object> config = mapper.readValue(configFile, Map.class);
                    for (String configKey : config.keySet()) {
                        settings.put(configKey.toLowerCase(), config.get(configKey).toString());
                    }
                } catch(Exception e) {
                    System.out.println("Issue parsing configuration file");
                    throw new RuntimeException(e);
                }
            }

            // Find any settings in environment variables
            try {
                Map<String, String> env = System.getenv();
                for (String envName : env.keySet()) {
                    if (envName.toLowerCase().startsWith("bc_")) {
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
                                System.out.println("Unknown DB Type " + dbType + " using Mysql. Please use mysql, postgres, mariadb, or none");
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
                                        System.out.println(iface.getDisplayName() + ": " + ip);
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
                        try {
                            whitelistfile = new File(setting.getValue());
                            Whitelist.loadWhitelist(whitelistfile);
                        }
                        catch(Exception e) {
                            System.out.println("Unable to load whitelist file: " + whitelistfile.getName());
                            e.printStackTrace();
                        }
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
                }
            }


            if(!noDB) {
                if(nodbWarning || mysqlSettingWarning) {
                    System.out.println("----------------");
                    System.out.println("[!!DEPRECATION!!] Your current database settings may not work in future versions\n");
                    if(mysqlSettingWarning)
                        System.out.println("- mysql_* settings should be replaced with db_* settings");
                    if(nodbWarning)
                        System.out.println("- db_type should be manually set to mysql");
                    System.out.println("\nLearn more here: https://github.com/Pugmatt/BedrockConnect/wiki/Deprecated-Database-Settings");
                    System.out.println("----------------");
                }
                System.out.println("Database Host: " + hostname + "\n" +
                        "Database: " + database + "\n" +
                        "Database User: " + username);
            }

            System.out.println("\nServer Limit: " + serverLimit + "\n" + "Port: " + port + "\n");

            CustomServerHandler.initialize();
            System.out.printf("Loaded %d custom servers\n", CustomServerHandler.getServers().length);

            if (Whitelist.hasWhitelist()) {
            	System.out.printf("There are %d whitelisted players\n", Whitelist.getWhitelist().size());
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
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }

            if(!noDB) {
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
                                        // TODO Auto-generated
                                        // catch block
                                        e.printStackTrace();
                                    }
                                    sec = 0;
                                }
                            }
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        sec++;
                    }
                };
                timer.scheduleAtFixedRate(task, 0L, 60 * 1000);
            } else {
                data = new Data(serverLimit, databaseType);
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() { }
                };
                timer.scheduleAtFixedRate(task, 0L, 1200L);
            }

            server = new Server(bindIp, port);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static String getArgValue(String str, String name) {
        String target = name + "=";
        int index = str.indexOf(target);
        int subIndex = index + target.length();
        return str.substring(subIndex);
    }

}
