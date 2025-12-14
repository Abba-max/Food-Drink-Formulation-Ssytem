package MyClasses.Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Configuration and Connection Management
 */
public class DatabaseConfig {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/formulation_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Connection pool settings
    private static final int MAX_CONNECTIONS = 10;
    private static Connection connection = null;

    // Alternative: Load from properties file
    private static Properties dbProperties = null;

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Load database configuration from properties file
     */
    public static void loadConfig(String configFilePath) {
        try {
            dbProperties = new Properties();
            FileInputStream fis = new FileInputStream(configFilePath);
            dbProperties.load(fis);
            fis.close();
            System.out.println("✓ Database configuration loaded from file");
        } catch (IOException e) {
            System.err.println("⚠ Could not load config file, using default settings");
        }
    }

    /**
     * Get database connection
     * @return Connection object
     */
    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        String url = DB_URL;
        String user = DB_USER;
        String password = DB_PASSWORD;

        // Use properties file if available
        if (dbProperties != null) {
            url = dbProperties.getProperty("db.url", DB_URL);
            user = dbProperties.getProperty("db.user", DB_USER);
            password = dbProperties.getProperty("db.password", DB_PASSWORD);
        }

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("✓ Database connection established");
        return connection;
    }

    /**
     * Get a new connection (for multi-threaded scenarios)
     */
    public static Connection getNewConnection() throws SQLException {
        String url = DB_URL;
        String user = DB_USER;
        String password = DB_PASSWORD;

        if (dbProperties != null) {
            url = dbProperties.getProperty("db.url", DB_URL);
            user = dbProperties.getProperty("db.user", DB_USER);
            password = dbProperties.getProperty("db.password", DB_PASSWORD);
        }

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn.isValid(5);
            System.out.println(isValid ? "✓ Database connection is valid" : "❌ Database connection is invalid");
            return isValid;
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get database URL
     */
    public static String getDatabaseUrl() {
        if (dbProperties != null) {
            return dbProperties.getProperty("db.url", DB_URL);
        }
        return DB_URL;
    }

    /**
     * Check if database exists and is accessible
     */
    public static boolean isDatabaseReady() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}