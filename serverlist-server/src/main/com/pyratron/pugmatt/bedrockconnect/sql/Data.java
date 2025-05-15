package main.com.pyratron.pugmatt.bedrockconnect.sql;

import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.BCPlayer;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;
import main.com.pyratron.pugmatt.bedrockconnect.listeners.PacketHandler;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Data {

    String serverLimit;

    public Data(String serverLimit, DatabaseTypes database) {
        this.serverLimit = serverLimit;

        if(!BedrockConnect.noDB) {
            try {
                createTables((database == DatabaseTypes.postgres));
            } catch (Exception e) {
                errorAlert(e);
            }
        }
    }

    public void createTables(boolean postgres) throws SQLException {
        // Create table if table does not exist
        String sqlCreate = "";
        if (!postgres)
                sqlCreate = "CREATE TABLE IF NOT EXISTS servers"
                + "  (id         INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "   uuid            TEXT,"
                + "   name            TEXT,"
                + "   servers         TEXT,"
                + "   serverLimit     INTEGER,"
                + "   INDEX (uuid(255))"
                + ");";
        else
            sqlCreate = "DO $$\n" +
                    "BEGIN\n" +
                    "    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'servers') THEN\n" +
                    "        CREATE TABLE servers (\n" +
                    "            id SERIAL PRIMARY KEY,\n" +
                    "            uuid TEXT,\n" +
                    "            name TEXT,\n" +
                    "            servers TEXT,\n" +
                    "            serverLimit INTEGER\n" +
                    "        );\n" +
                    "    END IF;\n" +
                    "\n" +
                    "    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_uuid') THEN\n" +
                    "        CREATE INDEX idx_uuid ON servers(uuid);\n" +
                    "    END IF;\n" +
                    "END $$;\n";

        Statement stmt = BedrockConnect.connection.createStatement();
        stmt.execute(sqlCreate);
    }

    public void Basic_SQL(final String query){
        new Thread(() -> {
                try {
                    PreparedStatement statement = BedrockConnect.connection.prepareStatement(query);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        ).start();
    }

    public BCPlayer getPlayer(ResultSet rs, String name, String uuid, BedrockServerSession session) {
        try {
            BCPlayer p = new BCPlayer(name, uuid, session, UIComponents.getFormData(rs.getString("servers")), rs.getInt("serverLimit"));
            return p;
        }
        catch(SQLException e) {
            errorAlert(e);
            return null;
        }
    }

    // If user exists
    public void userExists(String uuid, String name, BedrockServerSession session, PacketHandler packetHandler) {
        if(!BedrockConnect.noDB) {
            Data db = this;
            new Thread(() -> {
                try {
                    PreparedStatement searchUUID = BedrockConnect.connection.prepareStatement("SELECT COUNT(*) AS total FROM servers where uuid = ?");
                    searchUUID.setString(1, uuid);
                    ResultSet RS = searchUUID.executeQuery();
                    while (RS.next()) {
                        if (RS.getInt("total") > 0) {
                            PreparedStatement getUser = BedrockConnect.connection.prepareStatement("SELECT * FROM servers WHERE uuid = ?;");
                            getUser.setString(1, uuid);
                            ResultSet rs = getUser.executeQuery();
                            while (rs.next()) {
                                if (BedrockConnect.storeDisplayNames && !rs.getString("name").equals(name)) {
                                    PreparedStatement updateUUID = BedrockConnect.connection.prepareStatement("UPDATE servers SET name = ? WHERE uuid = ?");
                                    updateUUID.setString(1, name);
                                    updateUUID.setString(2, uuid);
                                    updateUUID.executeUpdate();
                                }
                                BCPlayer p = getPlayer(rs, name, uuid, session);
                                packetHandler.setPlayer(p);
                                if (p != null)
                                    BedrockConnect.server.addPlayer(p);
                            }
                        } else {
                            addNewUser(uuid, name, session, packetHandler);
                        }
                    }
                } catch (Exception e) {
                    errorAlert(e);
                    session.disconnect(BedrockConnect.language.getWording("disconnect", "dataError"));
                }
            }).start();
        } else {
            try {
                File f = new File("players");
                f.mkdir();
                File plyrFile = new File("players/" + uuid + ".json");
                if (plyrFile.createNewFile()) {
                    JSONObject jo = new JSONObject();
                    jo.put("uuid", uuid);
                    if(BedrockConnect.storeDisplayNames) {
                        jo.put("name", name);
                    }
                    jo.put("serverLimit", serverLimit);
                    jo.put("servers", new JSONArray());

                    PrintWriter pw = new PrintWriter("players/" + uuid + ".json");
                    pw.write(jo.toJSONString());
                    pw.flush();
                    pw.close();

                    BedrockConnect.logger.info("Added new user " + name + " (xuid: " + uuid + ")");

                    BCPlayer pl = new BCPlayer(name, uuid, session, new ArrayList<>(), Integer.parseInt(serverLimit));
                    packetHandler.setPlayer(pl);
                    BedrockConnect.server.addPlayer(pl);
                } else {
                    Object obj = new JSONParser().parse(new FileReader("players/" + uuid + ".json"));

                    JSONObject jo = (JSONObject) obj;

                    int serverLimit = Integer.parseInt((String) jo.get("serverLimit"));

                    JSONArray servers = (JSONArray) jo.get("servers");

                    BCPlayer p = new BCPlayer(name, uuid, session, servers, serverLimit);
                    packetHandler.setPlayer(p);
                    BedrockConnect.server.addPlayer(p);
                }
            } catch (Exception e) {
                BedrockConnect.logger.error("An error occurred saving to player file", e);
            }
        }
    }

    public void addNewUser(String uuid, String name, BedrockServerSession session, PacketHandler packetHandler)
    {
        Data db = this;
        new Thread(() -> {
                try
                {
                    PreparedStatement s = BedrockConnect.connection.prepareStatement("INSERT INTO servers (uuid, name, serverLimit) VALUES (?, ?, ?)");
                    s.setString(1, uuid);
                    s.setString(2, BedrockConnect.storeDisplayNames ? name : "");
                    s.setInt(3, Integer.parseInt(serverLimit));
                    s.executeUpdate();
                    BedrockConnect.logger.info("Added new user " + name + " (xuid: " + uuid + ") to Database");
                    BCPlayer pl = new BCPlayer(name, uuid, session, new ArrayList<>(), Integer.parseInt(serverLimit));
                    packetHandler.setPlayer(pl);
                    BedrockConnect.server.addPlayer(pl);
                }
                catch (Exception e)
                {
                    errorAlert(e);
                    session.disconnect(BedrockConnect.language.getWording("disconnect", "dataError"));
                }
        }).start();
    }

    public void setValueString(String column, String value, List<String> serverList, String uuid) {
        if(!BedrockConnect.noDB) {
            new Thread(() -> {
                try {
                    PreparedStatement s = BedrockConnect.connection.prepareStatement("UPDATE servers SET " + column + "= ? WHERE uuid= ?");
                    s.setString(1, value);
                    s.setString(2, uuid);

                    s.executeUpdate();
                } catch (Exception e) {
                    errorAlert(e);
                }
            }).start();
        } else {
            try {
                Object obj = new JSONParser().parse(new FileReader("players/" + uuid + ".json"));

                JSONObject jo = (JSONObject) obj;

                jo.put("servers", serverList);

                PrintWriter pw = new PrintWriter("players/" + uuid + ".json");
                pw.write(jo.toJSONString());
                pw.flush();
                pw.close();
            } catch (Exception e) {
                BedrockConnect.logger.error("An error occurred saving to player file", e);
            }
        }
    }

    public void setValueInt(String column, Integer value, String uuid) {
        new Thread(() -> {
                try
                {
                    PreparedStatement s = BedrockConnect.connection.prepareStatement("UPDATE servers SET " + column + "= ? WHERE uuid= ?");
                    s.setInt(1, value);
                    s.setString(2, uuid);

                    s.executeUpdate();
                }
                catch (Exception e)
                {
                    errorAlert(e);
                }
        }).start();
    }

    public void errorAlert(Exception e) {
        BedrockConnect.logger.error("A database error has occured" , e);
    }

}