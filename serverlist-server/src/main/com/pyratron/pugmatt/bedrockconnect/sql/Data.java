package main.com.pyratron.pugmatt.bedrockconnect.sql;

import com.nukkitx.protocol.bedrock.Bedrock;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.BedrockSession;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import main.com.pyratron.pugmatt.bedrockconnect.PipePlayer;
import main.com.pyratron.pugmatt.bedrockconnect.gui.UIComponents;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Data {

    public Data() {
        try {
            createTables();
        } catch(Exception e) {
            errorAlert(e);
        }
    }

    public void createTables() throws SQLException {
        // Create table if table does not exist
        String sqlCreate = "CREATE TABLE IF NOT EXISTS servers"
                + "  (id         INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "   uuid            TEXT,"
                + "   name            TEXT,"
                + "   servers         TEXT,"
                + "   serverLimit     INTEGER)";

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

    public PipePlayer getPlayer(ResultSet rs, String uuid, BedrockServerSession session) {
        try {
            PipePlayer p = new PipePlayer(uuid,this, session, UIComponents.getFormData(rs.getString("servers")), rs.getInt("serverLimit"));
            return p;
        }
        catch(SQLException e) {
            errorAlert(e);
            return null;
        }
    }

    // If user exists
    public void userExists(String uuid, String name, BedrockServerSession session) {
        Data db = this;
        new Thread(() -> {
                try
                {
                    PreparedStatement statement = BedrockConnect.connection.prepareStatement("SELECT EXISTS(SELECT 1 FROM servers WHERE uuid = '" + uuid + "')");
                    statement.executeQuery();
                    ResultSet RS = statement.executeQuery("SELECT COUNT(*) AS total FROM servers where uuid ='" + uuid + "'");
                    while (RS.next()) {
                        if (RS.getInt("total") > 0)
                        {
                            ResultSet rs = BedrockConnect.connection.createStatement().executeQuery("SELECT * FROM servers WHERE uuid = '" + uuid + "';");
                            while (rs.next()) {
                                if (!rs.getString("name").equals(name)) {
                                    Basic_SQL("UPDATE servers SET name='" + name + "' WHERE uuid='" + uuid + "'");
                                }
                                PipePlayer p = getPlayer(rs, uuid, session);
                                if(BedrockConnect.server == null)
                                    System.out.println("??");
                                if(p != null)
                                    BedrockConnect.server.addPlayer(p);
                            }
                        }
                        else
                        {
                            addNewUser(uuid, name, session);
                        }
                    }
                }
                catch (Exception e)
                {
                    errorAlert(e);
                    session.disconnect("We had some trouble receiving your player data. Please report this to the BedrockConnect discord.");
                }
        }).start();
    }

    public void addNewUser(String uuid, String name, BedrockServerSession session)
    {
        Data db = this;
        new Thread(() -> {
                try
                {
                    PreparedStatement s = BedrockConnect.connection.prepareStatement("INSERT INTO servers (uuid, name, serverLimit) VALUES ('" + uuid + "', '" + name + "', 10)");
                    s.executeUpdate();
                    System.out.println("[BedrockConnect] Added new user '" + name + "' (" + uuid + ") to Database.");
                    PipePlayer pl = new PipePlayer(uuid, db, session, new ArrayList<>(), 10);
                    BedrockConnect.server.addPlayer(pl);
                }
                catch (Exception e)
                {
                    errorAlert(e);
                    session.disconnect("We had some trouble receiving your player data. Please report this to the BedrockConnect discord.");
                }
        }).start();
    }

    public void setValueString(String column, String value, String uuid) {
        new Thread(() -> {
                try
                {
                    Basic_SQL("UPDATE servers SET " + column + "='" + value + "' WHERE uuid='" + uuid + "'");
                }
                catch (Exception e)
                {
                    errorAlert(e);
                }
        }).start();
    }

    public void setValueInt(String column, Integer value, String uuid) {
        new Thread(() -> {
                try
                {
                    Basic_SQL("UPDATE servers SET " + column + "=" + value + " WHERE uuid='" + uuid + "'");
                }
                catch (Exception e)
                {
                    errorAlert(e);
                }
        }).start();
    }

    public void errorAlert(Exception e) {
        System.out.println("[BedrockConnect] WARNING!!! DATABASE ERROR: " + e.getMessage());
        e.printStackTrace();
    }

}