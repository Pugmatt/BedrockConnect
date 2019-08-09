package main.com.pyratron.pugmatt.bedrockconnect;

import main.com.pyratron.pugmatt.bedrockconnect.sql.Data;
import main.com.pyratron.pugmatt.bedrockconnect.sql.MySQL;
import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class BedrockConnect {


    public static PaletteManager paletteManager;

    public static MySQL MySQL;
    public static Connection connection;
    public static Data data;

    public static Server server;

    public static void main(String[] args) {
        paletteManager =  new PaletteManager();

        try {
            String hostname = "localhost";
            String database = "bedrock-connect";
            String username = "root";
            String password = "";
            String port = "19132";

            String serverLimit = "100";

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
            }

            System.out.println("MySQL Host: " + hostname + "\n" +
            "MySQL Database: " + database + "\n" +
            "MySQL User: " + username + "\n" +
            "Server Limit: " + serverLimit + "\n" +
            "Port: " + port + "\n");

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
