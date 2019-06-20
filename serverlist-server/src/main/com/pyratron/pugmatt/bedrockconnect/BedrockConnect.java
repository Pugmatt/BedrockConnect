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
            String password = new String(Files.readAllBytes(Paths.get("mysql.txt")));

            MySQL = new MySQL("localhost", "bedrock-connect", "root", password);

            connection = null;

            connection = MySQL.openConnection();

            data = new Data();

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


            server = new Server();
        } catch(IOException e) {
            e.printStackTrace();
        }

        while(true) { }
    }

}
