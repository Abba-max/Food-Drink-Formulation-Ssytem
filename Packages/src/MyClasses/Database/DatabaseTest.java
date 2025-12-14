package MyClasses.Database;
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== DATABASE CONNECTION TEST ===\n");

        // Test 1: Load configuration
        DatabaseConfig.loadConfig("database.properties");

        // Test 2: Test connection
        boolean connected = DatabaseConfig.testConnection();

        if (connected) {
            System.out.println("\n✓ DATABASE IS READY!");
            System.out.println("You can now run your application.");
        } else {
            System.out.println("\n ❌ DATABASE CONNECTION FAILED!");
            System.out.println("Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. database.properties has correct credentials");
            System.out.println("3. Database 'formulation_system' exists");
        }

        DatabaseConfig.closeConnection();
    }
}