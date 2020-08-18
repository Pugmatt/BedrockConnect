package main.com.pyratron.pugmatt.bedrockconnect;

import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.sql.MySQL;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;

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

    public static void main(String[] args) {
        System.out.println("-= BedrockConnect =-");
        paletteManager =  new PaletteManager();

        try {
            String hostname = "localhost";
            String database = "bedrock-connect";
            String username = "root";
            String password = "";
            String port = "19132";

            String serverLimit = "100";

            String localIP = null;
            String publicIP = null;
            String localIPV6 = null;
            String publicIPV6 = null;
            boolean enableDNS = false;

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
                    noDB = true;
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
                                "    <DNSMasqHost name=\"play.inpvp.net\" a=\"" + selectedIP + "\" />\n" +
                                "    <DNSMasqHost name=\"mco.lbsg.net\" a=\"" + selectedIP + "\" />\n" +
                                "    <DNSMasqHost name=\"mco.cubecraft.net\" a=\"" + selectedIP + "\" />\n" +
                                "  </DNSMasqEntries>\n" +
                                "</DNSMasqConfig>");
                        br.close();
                    } catch (SocketException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(str.startsWith("localipv4=")) {
                    localIP = getArgValue(str, "localipv4");
                }
                if(str.startsWith("localipv6=")) {
                    localIPV6 = getArgValue(str, "localipv6");
                }
                if(str.startsWith("publicipv4=")) {
                    publicIP = getArgValue(str, "publicipv4");
                }
                if(str.startsWith("publicipv6=")) {
                    publicIPV6 = getArgValue(str, "publicipv6");
                }
            }

            if(!noDB)
            System.out.println("MySQL Host: " + hostname + "\n" +
            "MySQL Database: " + database + "\n" +
            "MySQL User: " + username);

            System.out.println("\nServer Limit: " + serverLimit + "\n" + "Port: " + port + "\n");

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
                timer.scheduleAtFixedRate(task, 0L, 1200L);
            } else {
                data = new Data(serverLimit);
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    int sec;
                    public void run() {
                        sec++;
                    }
                };
                timer.scheduleAtFixedRate(task, 0L, 1200L);
            }

            server = new Server(port);
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
