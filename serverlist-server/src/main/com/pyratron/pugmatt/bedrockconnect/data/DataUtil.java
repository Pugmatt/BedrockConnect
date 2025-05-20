package main.com.pyratron.pugmatt.bedrockconnect.data;

import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.server.BCPlayer;
import main.com.pyratron.pugmatt.bedrockconnect.server.PacketHandler;
import main.com.pyratron.pugmatt.bedrockconnect.server.gui.UIComponents;

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

public class DataUtil {

    private Database database;

    public DataUtil(Database database) {
        this.database = database;
        if (!BedrockConnect.getConfig().isNoDB()) {
            try {
                createTables((database.getType() == DatabaseTypes.postgres));
            } catch (Exception e) {
                errorAlert(e);
            }
        }
    }

    private void createTables(boolean postgres) throws SQLException {
        // Create BedrockConnect related tables if they do not exist
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

        Statement stmt = database.getConnection().createStatement();
        stmt.execute(sqlCreate);
    }

    private BCPlayer getPlayer(ResultSet rs, String name, String uuid, BedrockServerSession session) {
        try {
            BCPlayer p = new BCPlayer(name, uuid, session, UIComponents.getFormData(rs.getString("servers")),
                    rs.getInt("serverLimit"));
            return p;
        } catch (SQLException e) {
            errorAlert(e);
            return null;
        }
    }

    private void createPlayerRecord(String uuid, String name, BedrockServerSession session, PacketHandler packetHandler) {
        try {
            PreparedStatement s = database.getConnection()
                    .prepareStatement("INSERT INTO servers (uuid, name, serverLimit) VALUES (?, ?, ?)");
            s.setString(1, uuid);
            s.setString(2, BedrockConnect.getConfig().canStoreDisplayNames() ? name : "");
            s.setInt(3, Integer.parseInt(BedrockConnect.getConfig().getServerLimit()));
            s.executeUpdate();
            BedrockConnect.logger.info("Added new user " + name + " (xuid: " + uuid + ") to Database");
            BCPlayer pl = new BCPlayer(name, uuid, session, new ArrayList<>(), Integer.parseInt(BedrockConnect.getConfig().getServerLimit()));
            packetHandler.setPlayer(pl);
            BedrockConnect.getServer().addPlayer(pl);
        } catch (Exception e) {
            errorAlert(e);
            session.disconnect(BedrockConnect.getConfig().getLanguage().getWording("disconnect", "dataError"));
        }
    }

    // Perform a look up of user to confirm if they already exist.
    // If they already exist, simply grab the info for our player object (Also
    // update stored display name for player, if enabled)
    // If they do not exist, create a new record for the player
    public void initializePlayerData(String uuid, String name, BedrockServerSession session, PacketHandler packetHandler) {
        if (!BedrockConnect.getConfig().isNoDB()) {
            new Thread(() -> {
                try {
                    PreparedStatement searchUUID = database.getConnection()
                            .prepareStatement("SELECT COUNT(*) AS total FROM servers where uuid = ?");
                    searchUUID.setString(1, uuid);
                    ResultSet RS = searchUUID.executeQuery();
                    while (RS.next()) {
                        if (RS.getInt("total") > 0) {
                            PreparedStatement getUser = database.getConnection()
                                    .prepareStatement("SELECT * FROM servers WHERE uuid = ?;");
                            getUser.setString(1, uuid);
                            ResultSet rs = getUser.executeQuery();
                            while (rs.next()) {
                                if (BedrockConnect.getConfig().canStoreDisplayNames() && !rs.getString("name").equals(name)) {
                                    PreparedStatement updateUUID = database.getConnection()
                                            .prepareStatement("UPDATE servers SET name = ? WHERE uuid = ?");
                                    updateUUID.setString(1, name);
                                    updateUUID.setString(2, uuid);
                                    updateUUID.executeUpdate();
                                }
                                BCPlayer p = getPlayer(rs, name, uuid, session);
                                packetHandler.setPlayer(p);
                                if (p != null)
                                    BedrockConnect.getServer().addPlayer(p);
                            }
                        } else {
                            createPlayerRecord(uuid, name, session, packetHandler);
                        }
                    }
                } catch (Exception e) {
                    errorAlert(e);
                    session.disconnect(BedrockConnect.getConfig().getLanguage().getWording("disconnect", "dataError"));
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
                    if (BedrockConnect.getConfig().canStoreDisplayNames()) {
                        jo.put("name", name);
                    }
                    jo.put("serverLimit", BedrockConnect.getConfig().getServerLimit());
                    jo.put("servers", new JSONArray());

                    PrintWriter pw = new PrintWriter("players/" + uuid + ".json");
                    pw.write(jo.toJSONString());
                    pw.flush();
                    pw.close();

                    BedrockConnect.logger.info("Added new user " + name + " (xuid: " + uuid + ")");

                    BCPlayer pl = new BCPlayer(name, uuid, session, new ArrayList<>(), Integer.parseInt(BedrockConnect.getConfig().getServerLimit()));
                    packetHandler.setPlayer(pl);
                    BedrockConnect.getServer().addPlayer(pl);
                } else {
                    Object obj = new JSONParser().parse(new FileReader("players/" + uuid + ".json"));

                    JSONObject jo = (JSONObject) obj;

                    int serverLimit = Integer.parseInt((String) jo.get("serverLimit"));

                    JSONArray servers = (JSONArray) jo.get("servers");

                    BCPlayer p = new BCPlayer(name, uuid, session, servers, serverLimit);
                    packetHandler.setPlayer(p);
                    BedrockConnect.getServer().addPlayer(p);
                }
            } catch (Exception e) {
                BedrockConnect.logger.error("An error occurred saving to player file", e);
            }
        }
    }

    public void setValueString(String column, String value, List<String> serverList, String uuid) {
        if (!BedrockConnect.getConfig().isNoDB()) {
            new Thread(() -> {
                try {
                    PreparedStatement s = database.getConnection()
                            .prepareStatement("UPDATE servers SET " + column + "= ? WHERE uuid= ?");
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
            try {
                PreparedStatement s = database.getConnection()
                        .prepareStatement("UPDATE servers SET " + column + "= ? WHERE uuid= ?");
                s.setInt(1, value);
                s.setString(2, uuid);

                s.executeUpdate();
            } catch (Exception e) {
                errorAlert(e);
            }
        }).start();
    }

    public void errorAlert(Exception e) {
        BedrockConnect.logger.error("A database error has occured", e);
    }

}