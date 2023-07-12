package main.com.pyratron.pugmatt.bedrockconnect;

import main.com.pyratron.pugmatt.bedrockconnect.config.Language;
import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.sql.MySQL;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;
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

    public static String release = "1.36";

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

            String serverLimit = "100";

            String languageFile = null;

            for(String str : args) {
                if(str.startsWith("mysql_host="))
                    hostname = getArgValue(str, "mysql_host");
                if(str.startsWith("mysql_db="))
                    database = getArgValue(str, "mysql_db");
                if(str.startsWith("mysql_user="))
                    username = getArgValue(str, "mysql_user");
                if(str.startsWith("mysql_pass="))
                    password = getArgValue(str, "mysql_pass");
                if(str.startsWith("server_limit="))
                    serverLimit = getArgValue(str, "server_limit");
                if(str.startsWith("port="))
                    port = getArgValue(str, "port");
                if(str.startsWith("nodb="))
                    noDB = getArgValue(str, "nodb").toLowerCase().equals("true");
                if(str.startsWith("custom_servers="))
                    customServers = getArgValue(str, "custom_servers");
                if(str.startsWith("generatedns=")) {
                    String ip;
                    try {
                        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                        System.out.println("Local IPv4 IPs:");
                        while (interfaces.hasMoreElements()) {
                            NetworkInterface iface = interfaces.nextElement();

                            if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
                                continue;

                            Enumeration<InetAddress> addresses = iface.getInetAddresses();
                            while(addresses.hasMoreElements()) {
                                InetAddress addr = addresses.nextElement();

                                if(!(addr instanceof Inet4Address)) continue;

                                ip = addr.getHostAddress();
                                System.out.println(iface.getDisplayName() + ": " + ip);
                            }
                        }

                        Scanner reader = new Scanner(System.in);  // Reading from System.in
                        System.out.print("\nWhich IP should be used for the DNS records: ");
                        String selectedIP = reader.next().replaceAll("\\s+","");
                        reader.close();

                        BufferedWriter br = new BufferedWriter(new FileWriter(new File("bc_dns.conf")));
                        br.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                "<DNSMasqConfig xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                                "  <DNSMasqEntries>\n" +
                                "    <DNSMasqHost name=\"hivebedrock.network\" a=\"" + selectedIP + "\" />\n" +
                                "    <DNSMasqHost name=\"mco.mineplex.com\" a=\"" + selectedIP + "\" />\n" +
                                "    <DNSMasqHost name=\"play.mineplex.com\" a=\"" + selectedIP + "\" />\n" +
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
                if(str.startsWith("kick_inactive=")) {
                    kickInactive = getArgValue(str, "kick_inactive").toLowerCase().equals("true");
                }
                if(str.startsWith("user_servers=")) {
                    userServers = getArgValue(str, "user_servers").toLowerCase().equals("true");
                }
                if (str.startsWith("featured_servers=")) {
                    featuredServers = getArgValue(str, "featured_servers").toLowerCase().equals("true");
                }
                if (str.startsWith("fetch_featured_ips=")) {
                    fetchFeaturedIps = getArgValue(str, "fetch_featured_ips").toLowerCase().equals("true");
                }
                if (str.startsWith("fetch_ips=")) {
                    fetchIps = getArgValue(str, "fetch_ips").toLowerCase().equals("true");
                }
                if (str.startsWith("whitelist=")) {
                	try {
                		whitelistfile = new File(getArgValue(str, "whitelist"));
                		Whitelist.loadWhitelist(whitelistfile);
                	}
                	catch(Exception e) {
                		System.out.println("Unable to load whitelist file: " + whitelistfile.getName());
                		e.printStackTrace();
                	}
                }
                if (str.startsWith("language=")) {
                    languageFile = getArgValue(str, "language");
                }
              	if (str.startsWith("bindip=")) {
              	    bindIp = getArgValue(str, "bindip");
              	}
                if (str.startsWith("store_display_names=")) {
                    storeDisplayNames = getArgValue(str, "store_display_names").toLowerCase().equals("true");
                }
            }

            if(!noDB)
            System.out.println("MySQL Host: " + hostname + "\n" +
            "MySQL Database: " + database + "\n" +
            "MySQL User: " + username);

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
                        featuredServerIps.put("mco.mineplex.com", "108.178.12.125");
                        featuredServerIps.put("mco.cubecraft.net", "51.178.75.10");
                        featuredServerIps.put("mco.lbsg.net", "142.44.240.96");
                        featuredServerIps.put("play.inpvp.net", "52.234.130.241");
                        featuredServerIps.put("play.galaxite.net", "51.222.8.223");
                        featuredServerIps.put("play.pixelparadise.gg", "40.87.84.106");

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
                MySQL = new MySQL(hostname, database, username, password);

                connection = null;

                connection = MySQL.openConnection();

                data = new Data(serverLimit);

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
                data = new Data(serverLimit);
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
