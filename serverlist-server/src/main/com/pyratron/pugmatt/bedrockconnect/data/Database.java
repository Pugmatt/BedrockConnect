package main.com.pyratron.pugmatt.bedrockconnect.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;

public class Database {
    private final String user;
    private final String database;
    private final String password;
    private final String hostname;

    private final DatabaseTypes databasetype;

    private final Boolean autoReconnect;

    private Connection connection;

    public Database(String hostname, String database, String username, String password, DatabaseTypes databasetype, Boolean autoReconnect) {
        this.hostname = hostname;
        this.database = database;
        this.user = username;
        this.password = password;
        this.connection = null;
        this.databasetype = databasetype;
        this.autoReconnect = autoReconnect;
    }

    public Connection openConnection() {
        try {
            String Driver = "";

            switch (databasetype)
            {
                case mysql:
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Driver = "jdbc:mysql://";
                    break;
                case mariadb:
                    Class.forName("org.mariadb.jdbc.Driver");
                    Driver = "jdbc:mariadb://";
                    break;
                case postgres:
                    Class.forName("org.postgresql.Driver");
                    Driver = "jdbc:postgresql://";
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported database type: " + databasetype);
            }

            String Extra = "";

            if (autoReconnect)
            {
                Extra += "&autoReconnect=true";
            }


            connection = DriverManager.getConnection(Driver + this.hostname + "/" + this.database + "?serverTimezone=UTC&useLegacyDatetimeCode=false" + Extra, this.user, this.password);
            BedrockConnect.logger.debug("Connection made with database");

        } catch (SQLException e) {
            BedrockConnect.logger.error("Failed to establish connection with database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            BedrockConnect.logger.error("JDBC Driver not found");
        }
        return connection;
    }

    public boolean checkConnection() {
        return connection != null;
    }

    public synchronized Connection getConnection() {
        // Validate the connection is alive before use and reopen if the server
        // closed the idle session. Gated by autoReconnect so the option remains
        // the single switch for connection recovery across all drivers.
        if (autoReconnect) {
            try {
                if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                    BedrockConnect.logger.debug("Database connection is invalid or closed; reopening");
                    return openConnection();
                }
            } catch (SQLException e) {
                BedrockConnect.logger.error("Database connection validation failed; reopening", e);
                return openConnection();
            }
        }
        return connection;
    }

    public DatabaseTypes getType() {
        return databasetype;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                BedrockConnect.logger.error("Error closing the database connection", e);
            }
        }
    }
}