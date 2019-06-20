package main.com.pyratron.pugmatt.bedrockconnect.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;




/**
 * Connects to and uses a MySQL database
 *
 * @author -_Husky_-
 * @author tips48
 */
public class MySQL extends Database {
    private final String user;
    private final String database;
    private final String password;
    private final String hostname;

    private Connection connection;

    /**
     * Creates a new MySQL instance
     *
     * @param hostname
     *            Name of the host
     * @param database
     *            Database name
     * @param username
     *            Username
     * @param password
     *            Password
     */
    public MySQL(String hostname, String database, String username, String password) {
        this.hostname = hostname;
        this.database = database;
        this.user = username;
        this.password = password;
        this.connection = null;
    }

    @Override
    public Connection openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + "/" + this.database, this.user, this.password);
            System.out.println("- MySQL Connection Started -");

        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to MySQL server! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: JDBC Driver not found!");
        }
        return connection;
    }

    @Override
    public boolean checkConnection() {
        return connection != null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("ERROR: Error closing the MySQL Connection!");
                e.printStackTrace();
            }
        }
    }

    public ResultSet querySQL(String query) {
        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        ResultSet ret = null;

        try {
            ret = s.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();

        return ret;
    }

    public void updateSQL(String update) {

        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
            s.executeUpdate(update);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        closeConnection();

    }

}