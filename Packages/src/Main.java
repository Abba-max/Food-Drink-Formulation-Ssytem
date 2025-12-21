import MyClasses.Consumables.Item;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;
import MyClasses.Utilities.AuditTrail;
import MyClasses.Database.DatabaseConfig;
import MyClasses.Database.DatabaseManager;

import java.util.LinkedList;
import java.util.Date;


public class Main {

    private LinkedList<Admin> admins;
    private LinkedList<Author> authors;
    private LinkedList<Customer> customers;
    private LinkedList<Item> allFormulations;

    private Keypad pad;
    private Screen screen;

    // Audit Trail for tracking system activities
    private AuditTrail auditTrail;

    // Database Manager (replaces FileManager)
    private DatabaseManager databaseManager;

    // Currently logged in user
    private Object currentUser;
    private String currentUserType; // "ADMIN", "AUTHOR", "CUSTOMER"

    public Main() {
        this.admins = new LinkedList<>();
        this.authors = new LinkedList<>();
        this.customers = new LinkedList<>();
        this.allFormulations = new LinkedList<>();

        this.pad = new Keypad();
        this.screen = new Screen();

        this.auditTrail = new AuditTrail();

        // Initialize Database Manager instead of File Manager
        initializeDatabase();

        // Load existing data from database
        loadDataFromDatabase();

        // Create default admin if none exists
        initializeSystem();
    }

    /**
     * Initialize database connection
     */
    private void initializeDatabase() {
        try {
            screen.display("Connecting to database...");

            // Optional: Load config from properties file
            // DatabaseConfig.loadConfig("database.properties");

            // Test connection
            if (DatabaseConfig.testConnection()) {
                this.databaseManager = new DatabaseManager();
                screen.display("✓ Database connection established");
                auditTrail.logAction("SYSTEM", "Database connected successfully");
            } else {
                screen.display("❌ Database connection failed!");
                screen.display("Please check your database configuration in DatabaseConfig.java");
                screen.display("Make sure MySQL is running and credentials are correct.");
                auditTrail.logAction("SYSTEM", "Database connection failed");
                System.exit(1);
            }
        } catch (Exception e) {
            screen.display("❌ Error initializing database: " + e.getMessage());
            screen.display("Stack trace:");
            e.printStackTrace();
            auditTrail.logAction("SYSTEM", "Database initialization error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Load data from database (replaces loadDataFromFiles)
     */
    private void loadDataFromDatabase() {
        try {
            screen.display("Loading system data from database...");

            // Load users
            LinkedList<Admin> loadedAdmins = databaseManager.loadAdmins();
            if (loadedAdmins != null && !loadedAdmins.isEmpty()) {
                admins = loadedAdmins;
            }

            LinkedList<Author> loadedAuthors = databaseManager.loadAuthors();
            if (loadedAuthors != null && !loadedAuthors.isEmpty()) {
                authors = loadedAuthors;
            }

            LinkedList<Customer> loadedCustomers = databaseManager.loadCustomers();
            if (loadedCustomers != null && !loadedCustomers.isEmpty()) {
                customers = loadedCustomers;
            }

            // Load formulations
            LinkedList<Item> loadedFormulations = databaseManager.loadFormulations();
            if (loadedFormulations != null && !loadedFormulations.isEmpty()) {
                allFormulations = loadedFormulations;
            }

            // Load audit trail
            AuditTrail loadedAudit = databaseManager.loadAuditTrail();
            if (loadedAudit != null) {
                auditTrail = loadedAudit;
            }

            screen.display("✓ Data loaded successfully from database");
            screen.display("  Admins: " + admins.size());
            screen.display("  Authors: " + authors.size());
            screen.display("  Customers: " + customers.size());
            screen.display("  Formulations: " + allFormulations.size());

            auditTrail.logAction("SYSTEM", "Data loaded from database at " + new Date());

        } catch (Exception e) {
            screen.display("⚠ Error loading data: " + e.getMessage());
            screen.display("Starting with fresh data...");
            auditTrail.logAction("SYSTEM", "Failed to load data: " + e.getMessage());
        }
    }


    /**
     * Save data to database (replaces saveDataToFiles)
     */
    private void saveDataToDatabase() {
        try {
            screen.display("\nSaving system data to database...");

            databaseManager.saveAdmins(admins);
            databaseManager.saveAuthors(authors);
            databaseManager.saveCustomers(customers);
            databaseManager.saveFormulations(allFormulations);
            databaseManager.saveAuditTrail(auditTrail);

            screen.display("✓ All data saved successfully to database");
            auditTrail.logAction("SYSTEM", "Data saved to database at " + new Date());

        } catch (Exception e) {
            screen.display("⚠ Error saving data: " + e.getMessage());
            e.printStackTrace();
            auditTrail.logAction("SYSTEM", "Failed to save data: " + e.getMessage());
        }
    }

    // Initialize system with default admin
    private void initializeSystem() {
        if (admins.isEmpty()) {
            Admin defaultAdmin = new Admin(1, "System Admin", "HQ", "+1-000-0000", "1980-01-01", "admin123");
            admins.add(defaultAdmin);

            // Save to database immediately
            databaseManager.saveAdmin(defaultAdmin);

            screen.display("System initialized with default admin account.");
            screen.display("Username: admin (ID: 1) | Password: admin123");

            auditTrail.logAction("SYSTEM", "Default admin account created");
        }
    }


    public void start() {
        screen.display("\n" + "=".repeat(60));
        screen.display("   FOOD & DRINK FORMULATION MANAGEMENT SYSTEM");
        screen.display("   [DATABASE VERSION]");
        screen.display("=".repeat(60));

        while (true) {
            try {
                if (currentUser == null) {
                    showWelcomeMenu();
                } else {
                    showUserMenu();
                }
            } catch (Exception e) {
                screen.display("\n⚠ An unexpected error occurred: " + e.getMessage());
                screen.display("Returning to main menu...");
                auditTrail.logAction("SYSTEM", "Unexpected error: " + e.getMessage());
            }
        }
    }

    // ============ WELCOME MENU (NOT LOGGED IN) ============

    private void showWelcomeMenu() {
        screen.display("\n=== WELCOME ===");
        screen.display("1. Login as Admin");
        screen.display("2. Login as Author");
        screen.display("3. Login as Customer");
        screen.display("4. Register as Customer");
        screen.display("5. Save Data to Database");
        screen.display("0. Exit");
        screen.display("\nEnter choice:");

        try {
            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    loginAsAdmin();
                    break;
                case 2:
                    loginAsAuthor();
                    break;
                case 3:
                    loginAsCustomer();
                    break;
                case 4:
                    registerCustomer();
                    break;
                case 5:
                    saveDataToDatabase();
                    break;
                case 0:
                    exitSystem();
                    break;
                default:
                    screen.display("⚠ Invalid choice! Please enter a number between 0 and 5.");
            }
        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid input! Please enter a valid number.");
            auditTrail.logAction("SYSTEM", "Invalid input in welcome menu: " + e.getMessage());
        } catch (Exception e) {
            screen.display("⚠ Error: " + e.getMessage());
            auditTrail.logAction("SYSTEM", "Error in welcome menu: " + e.getMessage());
        }
    }


    private void exitSystem() {
        screen.display("\n=== SHUTTING DOWN ===");
        screen.display("Do you want to save data before exiting? (1=Yes, 0=No)");

        try {
            int save = pad.getInt();
            if (save == 1) {
                saveDataToDatabase();
            }

            auditTrail.logAction("SYSTEM", "System shutdown at " + new Date());

            // Close database connection
            screen.display("Closing database connection...");
            databaseManager.close();
            DatabaseConfig.closeConnection();

            screen.display("Thank you for using the system. Goodbye!");
            System.exit(0);

        } catch (Exception e) {
            screen.display("Error during shutdown: " + e.getMessage());
            try {
                databaseManager.close();
                DatabaseConfig.closeConnection();
            } catch (Exception ex) {
                // Ignore errors during cleanup
            }
            System.exit(1);
        }
    }

    // ============ LOGIN METHODS ============

    private void loginAsAdmin() {
        screen.display("\n=== ADMIN LOGIN ===");

        try {
//            screen.display("Enter Admin ID:");
//            int id = pad.getInt();
            screen.display("Enter Admin NAME:");
            String name = pad.getString();

            screen.display("Enter password:");
            String password = pad.getString();

            for (Admin admin : admins) {
                if (admin.getName().equals(name) && admin.getPassword().equals(password)) {
                    currentUser = admin;
                    currentUserType = "ADMIN";

                    admin.setAuthors(authors);
                    admin.setAdmins(admins);
                    admin.setCustomers(customers);
                    admin.setAllFormulations(allFormulations);

                    screen.display("\n✓ Login successful!");
                    screen.display("Welcome, " + admin.getName());

                    auditTrail.logAction("ADMIN:" + admin.getName(), "Logged in at " + new Date());
                    return;
                }
            }

            screen.display("⚠ Invalid credentials!");
            auditTrail.logAction("SYSTEM", "Failed admin login attempt for Admin: " + name);

        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid ID format! Please enter a valid number.");
            auditTrail.logAction("SYSTEM", "Invalid admin ID format during login");
        } catch (Exception e) {
            screen.display("⚠ Login error: " + e.getMessage());
            auditTrail.logAction("SYSTEM", "Admin login error: " + e.getMessage());
        }
    }

    private void loginAsAuthor() {
        screen.display("\n=== AUTHOR LOGIN ===");

        try {
//            screen.display("Enter Author ID:");
//            int id = pad.getInt();

            screen.display("Enter Author NAME:");
            String name = pad.getString();

            screen.display("Enter password:");
            String password = pad.getString();

            for (Author author : authors) {
                if (author.getName().equals(name) && author.getPassword().equals(password)) {
                    currentUser = author;
                    currentUserType = "AUTHOR";
                    screen.display("\n✓ Login successful!");
                    screen.display("Welcome, " + author.getName());

                    auditTrail.logAction("AUTHOR:" + author.getName(), "Logged in at " + new Date());
                    return;
                }
            }

            screen.display("⚠ Invalid credentials!");
            screen.display("Note: Authors must be created by an Admin first.");
            auditTrail.logAction("SYSTEM", "Failed author login attempt for Author: " + name);

        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid ID format! Please enter a valid number.");
            auditTrail.logAction("SYSTEM", "Invalid author ID format during login");
        } catch (Exception e) {
            screen.display("⚠ Login error: " + e.getMessage());
            auditTrail.logAction("SYSTEM", "Author login error: " + e.getMessage());
        }
    }

    private void loginAsCustomer() {
        screen.display("\n=== CUSTOMER LOGIN ===");

        try {
//            screen.display("Enter Customer ID:");
//            int id = pad.getInt();
            screen.display("Enter Customer NAME:");
            String name = pad.getString();

            screen.display("Enter password:");
            String password = pad.getString();

            for (Customer customer : customers) {
                if (customer.getName().equals(name) && customer.getPassword().equals(password)) {
                    currentUser = customer;
                    currentUserType = "CUSTOMER";

                    // Set available formulations (only non-vetoed ones)
                    LinkedList<Item> availableItems = getNonVetoedFormulations();
                    customer.setAvailableFormulations(availableItems);

                    screen.display("\n✓ Login successful!");
                    screen.display("Welcome, " + customer.getName());

                    auditTrail.logAction("CUSTOMER:" + customer.getName(), "Logged in at " + new Date());
                    return;
                }
            }

            screen.display("⚠ Invalid credentials!");
            auditTrail.logAction("SYSTEM", "Failed customer login attempt for Customer: " + name);

        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid ID format! Please enter a valid number.");
            auditTrail.logAction("SYSTEM", "Invalid customer ID format during login");
        } catch (Exception e) {
            screen.display("⚠ Login error: " + e.getMessage());
            auditTrail.logAction("SYSTEM", "Customer login error: " + e.getMessage());
        }
    }

    private void registerCustomer() {
        screen.display("\n=== CUSTOMER REGISTRATION ===");

        try {
            screen.display("Enter desired Customer ID:");
            int id = pad.getInt();

            // Check if ID exists
            for (Customer c : customers) {
                if (c.getCustomerID() == id) {
                    screen.display("⚠ Customer ID already exists! Please choose a different ID.");
                    auditTrail.logAction("SYSTEM", "Registration failed: Customer ID " + id + " already exists");
                    return;
                }
            }

            screen.display("Enter name:");
            String name = pad.getString();

            if (name == null || name.trim().isEmpty()) {
                screen.display("⚠ Name cannot be empty!");
                return;
            }

            screen.display("Enter address:");
            String address = pad.getString();

            screen.display("Enter contact:");
            String contact = pad.getString();

            screen.display("Enter date of birth (YYYY-MM-DD):");
            String dob = pad.getString();

            screen.display("Enter age:");
            int age = pad.getInt();

            if (age < 0 || age > 150) {
                screen.display("⚠ Invalid age! Please enter a realistic age.");
                return;
            }

            screen.display("Enter password:");
            String password = pad.getString();

            if (password == null || password.length() < 4) {
                screen.display("⚠ Password must be at least 4 characters long!");
                return;
            }

            Customer customer = new Customer(id, age);
            customer.setName(name);
            customer.setAddress(address);
            customer.setContact(contact);
            customer.setDateofbirth(dob);
            customer.setPassword(password);

            customers.add(customer);

            screen.display("\n✓ Registration successful!");
            screen.display("Customer ID: " + id);
            screen.display("You can now login with your credentials.");

            auditTrail.logAction("SYSTEM", "New customer registered: " + name + " (ID: " + id + ")");

            // Auto-save to database after registration
            databaseManager.saveCustomer(customer);
            screen.display("✓ Customer saved to database");

        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid number format! Please enter valid numeric values.");
            auditTrail.logAction("SYSTEM", "Customer registration error: Invalid number format");
        } catch (Exception e) {
            screen.display("⚠ Registration error: " + e.getMessage());
            auditTrail.logAction("SYSTEM", "Customer registration error: " + e.getMessage());
        }
    }

    // ============ USER MENUS ============

    private void showUserMenu() {
        if ("ADMIN".equals(currentUserType)) {
            showAdminMenu();
        } else if ("AUTHOR".equals(currentUserType)) {
            showAuthorMenu();
        } else if ("CUSTOMER".equals(currentUserType)) {
            showCustomerMenu();
        }
    }

    // ============ ADMIN MENU ============

    private void showAdminMenu() {
        Admin admin = (Admin) currentUser;

        screen.display("\n" + "=".repeat(60));
        screen.display("   ADMIN MENU - " + admin.getName());
        screen.display("=".repeat(60));
        screen.display("1. Account Management");
        screen.display("2. Formulation Management");
        screen.display("3. Check Formulation Issues");
        screen.display("4. View System Statistics");
        screen.display("5. View All Accounts");
        screen.display("6. View Audit Trail");
        screen.display("7. Save Data to Database");
        screen.display("8. Create Database Backup");
        screen.display("0. Logout");
        screen.display("\nEnter choice:");

        try {
            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    showAccountManagementMenu(admin);
                    break;
                case 2:
                    admin.consultFormulation();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Accessed formulation management");
                    break;
                case 3:
                    admin.checkFormulationissues();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Checked formulation issues");
                    break;
                case 4:
                    showSystemStatistics();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Viewed system statistics");
                    break;
                case 5:
                    admin.viewAllAccounts();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Viewed all accounts");
                    break;
                case 6:
                    viewAuditTrail();
                    break;
                case 7:
                    saveDataToDatabase();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Manually saved system data");
                    break;
                case 8:
                    createDatabaseBackup();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Created database backup");
                    break;
                case 0:
                    logout();
                    break;
                default:
                    screen.display("⚠ Invalid choice! Please enter a number between 0 and 8.");
            }
        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid input! Please enter a valid number.");
            auditTrail.logAction("ADMIN:" + admin.getName(), "Invalid input in admin menu");
        } catch (Exception e) {
            screen.display("⚠ Error: " + e.getMessage());
            auditTrail.logAction("ADMIN:" + admin.getName(), "Error in admin menu: " + e.getMessage());
        }
    }

    private void showAccountManagementMenu(Admin admin) {
        screen.display("\n=== ACCOUNT MANAGEMENT ===");
        screen.display("1. Create Author Account");
        screen.display("2. Create Admin Account");
        screen.display("3. View All Authors");
        screen.display("4. View All Admins");
        screen.display("0. Return");
        screen.display("\nEnter choice:");

        try {
            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    Author newAuthor = admin.createAuthorAccount();
                    if (newAuthor != null) {
                        authors.add(newAuthor);
                        auditTrail.logAction("ADMIN:" + admin.getName(),
                                "Created author account: " + newAuthor.getName() + " (ID: " + newAuthor.getAuthorID() + ")");
                        databaseManager.saveAuthor(newAuthor);
                        screen.display("✓ Author saved to database");
                    }
                    break;
                case 2:
                    Admin newAdmin = admin.createAdminAccount();
                    if (newAdmin != null) {
                        admins.add(newAdmin);
                        auditTrail.logAction("ADMIN:" + admin.getName(),
                                "Created admin account: " + newAdmin.getName() + " (ID: " + newAdmin.getAdminID() + ")");
                        databaseManager.saveAdmin(newAdmin);
                        screen.display("✓ Admin saved to database");
                    }
                    break;
                case 3:
                    viewAllAuthors();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Viewed all authors");
                    break;
                case 4:
                    viewAllAdmins();
                    auditTrail.logAction("ADMIN:" + admin.getName(), "Viewed all admins");
                    break;
                case 0:
                    return;
                default:
                    screen.display("⚠ Invalid choice! Please enter a number between 0 and 4.");
            }
        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid input! Please enter a valid number.");
            auditTrail.logAction("ADMIN:" + admin.getName(), "Invalid input in account management");
        } catch (Exception e) {
            screen.display("⚠ Error: " + e.getMessage());
            auditTrail.logAction("ADMIN:" + admin.getName(), "Error in account management: " + e.getMessage());
        }
    }

    // ============ AUTHOR MENU ============

    private void showAuthorMenu() {
        Author author = (Author) currentUser;

        screen.display("\n" + "=".repeat(60));
        screen.display("   AUTHOR MENU - " + author.getName());
        screen.display("=".repeat(60));
        screen.display("1. Create New Formulation");
        screen.display("2. Update Existing Formulation");
        screen.display("3. Consult My Formulations");
        screen.display("4. Check Formulation Issues");
        screen.display("5. View My Statistics");
        screen.display("6. Save Data to Database");
        screen.display("0. Logout");
        screen.display("\nEnter choice:");

        try {
            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    Item newItem = author.Formulate();
                    if (newItem != null) {
                        allFormulations.add(newItem);
                        screen.display("✓ Formulation added to system catalog");
                        auditTrail.logAction("AUTHOR:" + author.getName(),
                                "Created formulation: " + newItem.getName() + " (ID: " + newItem.getItemID() + ")");
                        databaseManager.saveItem(newItem);
                        screen.display("✓ Formulation saved to database");
                    }
                    break;
                case 2:
                    author.updateFormulation();
                    auditTrail.logAction("AUTHOR:" + author.getName(), "Updated formulation");
                    saveDataToDatabase();
                    break;
                case 3:
                    author.consultFormulation();
                    auditTrail.logAction("AUTHOR:" + author.getName(), "Consulted formulations");
                    break;
                case 4:
                    author.checkFormulationissues();
                    auditTrail.logAction("AUTHOR:" + author.getName(), "Checked formulation issues");
                    break;
                case 5:
                    showAuthorStatistics(author);
                    auditTrail.logAction("AUTHOR:" + author.getName(), "Viewed statistics");
                    break;
                case 6:
                    saveDataToDatabase();
                    auditTrail.logAction("AUTHOR:" + author.getName(), "Manually saved system data");
                    break;
                case 0:
                    logout();
                    break;
                default:
                    screen.display("⚠ Invalid choice! Please enter a number between 0 and 6.");
            }
        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid input! Please enter a valid number.");
            auditTrail.logAction("AUTHOR:" + author.getName(), "Invalid input in author menu");
        } catch (Exception e) {
            screen.display("⚠ Error: " + e.getMessage());
            auditTrail.logAction("AUTHOR:" + author.getName(), "Error in author menu: " + e.getMessage());
        }
    }

    // ============ CUSTOMER MENU ============

    private void showCustomerMenu() {
        Customer customer = (Customer) currentUser;

        screen.display("\n" + "=".repeat(60));
        screen.display("   CUSTOMER MENU - " + customer.getName());
        screen.display("=".repeat(60));
        screen.display("1. Browse Catalog");
        screen.display("2. My Purchases");
        screen.display("3. My Favorites");
        screen.display("4. My Profile");
        screen.display("5. Save Data to Database");
        screen.display("0. Logout");
        screen.display("\nEnter choice:");

        try {
            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    customer.consultFormulation();
                    auditTrail.logAction("CUSTOMER:" + customer.getName(), "Browsed catalog");
                    break;
                case 2:
                    customer.viewPurchaseHistory();
                    auditTrail.logAction("CUSTOMER:" + customer.getName(), "Viewed purchase history");
                    break;
                case 3:
                    viewCustomerFavorites(customer);
                    auditTrail.logAction("CUSTOMER:" + customer.getName(), "Viewed favorites");
                    break;
                case 4:
                    viewCustomerProfile(customer);
                    auditTrail.logAction("CUSTOMER:" + customer.getName(), "Viewed profile");
                    break;
                case 5:
                    saveDataToDatabase();
                    auditTrail.logAction("CUSTOMER:" + customer.getName(), "Manually saved system data");
                    break;
                case 0:
                    logout();
                    break;
                default:
                    screen.display("⚠ Invalid choice! Please enter a number between 0 and 5.");
            }
        } catch (NumberFormatException e) {
            screen.display("⚠ Invalid input! Please enter a valid number.");
            auditTrail.logAction("CUSTOMER:" + customer.getName(), "Invalid input in customer menu");
        } catch (Exception e) {
            screen.display("⚠ Error: " + e.getMessage());
            auditTrail.logAction("CUSTOMER:" + customer.getName(), "Error in customer menu: " + e.getMessage());
        }
    }

    // ============ HELPER DISPLAY METHODS ============

    private void showSystemStatistics() {
        screen.display("\n=== SYSTEM STATISTICS ===");
        screen.display("Total Admins: " + admins.size());
        screen.display("Total Authors: " + authors.size());
        screen.display("Total Customers: " + customers.size());
        screen.display("Total Formulations: " + allFormulations.size());

        // Count vetoed
        int vetoedCount = 0;
        for (Item item : allFormulations) {
            if (item instanceof MyClasses.Consumables.Food) {
                if (((MyClasses.Consumables.Food) item).isVetoed()) vetoedCount++;
            } else if (item instanceof MyClasses.Consumables.Drink) {
                if (((MyClasses.Consumables.Drink) item).isVetoed()) vetoedCount++;
            }
        }
        screen.display("Vetoed Formulations: " + vetoedCount);

        // Count purchases
        int totalPurchases = 0;
        for (Customer customer : customers) {
            totalPurchases += customer.getPurchasedItems().size();
        }
        screen.display("Total Purchases: " + totalPurchases);
        screen.display("Audit Log Entries: " + auditTrail.records.size());
    }

    private void showAuthorStatistics(Author author) {
        screen.display("\n=== YOUR STATISTICS ===");
        screen.display("Total Formulations: " + author.getFormulatedItems().size());
        screen.display("Food Items: " + author.getFoodCount());
        screen.display("Drink Items: " + author.getDrinkCount());
    }

    private void viewAllAuthors() {
        screen.display("\n=== ALL AUTHORS ===");
        if (authors.isEmpty()) {
            screen.display("No authors registered.");
            return;
        }

        for (Author author : authors) {
            screen.display("\nID: " + author.getAuthorID());
            screen.display("Name: " + author.getName());
            screen.display("Formulations: " + author.getFormulatedItems().size());
        }
    }

    private void viewAllAdmins() {
        screen.display("\n=== ALL ADMINISTRATORS ===");
        for (Admin admin : admins) {
            screen.display("\nID: " + admin.getAdminID());
            screen.display("Name: " + admin.getName());
        }
    }

    private void viewCustomerFavorites(Customer customer) {
        screen.display("\n=== MY FAVORITES ===");
        LinkedList<Item> favorites = customer.getFavoriteFormulations();

        if (favorites.isEmpty()) {
            screen.display("No favorites yet.");
            return;
        }

        for (Item item : favorites) {
            screen.display("- " + item.getName() + " ($" + item.getPrice() + ")");
        }
    }

    private void viewCustomerProfile(Customer customer) {
        screen.display("\n=== MY PROFILE ===");
        screen.display("Customer ID: " + customer.getCustomerID());
        screen.display("Name: " + customer.getName());
        screen.display("Age: " + customer.getAge());
        screen.display("Contact: " + customer.getContact());
        screen.display("Total Purchases: " + customer.getPurchasedItems().size());
        screen.display("Total Favorites: " + customer.getFavoriteFormulations().size());
        screen.display("Feedback Given: " + customer.getFeedbackHistory().size());
    }

    /**
     * View Audit Trail
     */
    private void viewAuditTrail() {
        screen.display("\n=== AUDIT TRAIL ===");
        screen.display("Total log entries: " + auditTrail.records.size());

        if (auditTrail.records.isEmpty()) {
            screen.display("No audit records available.");
            return;
        }

        screen.display("\nRecent activities:");
        screen.display("-".repeat(70));

        // Show last 20 entries
        int startIndex = Math.max(0, auditTrail.records.size() - 20);
        for (int i = startIndex; i < auditTrail.records.size(); i++) {
            screen.display((i + 1) + ". " + auditTrail.records.get(i));
        }

        screen.display("-".repeat(70));

        if (auditTrail.records.size() > 20) {
            screen.display("\nShowing last 20 of " + auditTrail.records.size() + " entries");
        }
    }

    /**
     * Create database backup
     */
    private void createDatabaseBackup() {
        try {
            screen.display("\n=== CREATE DATABASE BACKUP ===");
            screen.display("This will create a SQL dump of the current database.");
            screen.display("Enter backup filename (without extension):");
            String backupName = pad.getString();

            if (backupName == null || backupName.trim().isEmpty()) {
                backupName = "backup_" + System.currentTimeMillis();
            }

            screen.display("\nCreating backup...");
            screen.display("Note: You need to manually run mysqldump for full backup:");
            screen.display("mysqldump -u root -p formulation_system > " + backupName + ".sql");
            screen.display("\nAlternatively, saving current data to database...");

            saveDataToDatabase();

            screen.display("✓ Current data saved to database");
            screen.display("Backup timestamp: " + new java.util.Date());

        } catch (Exception e) {
            screen.display("⚠ Error creating backup: " + e.getMessage());
            auditTrail.logAction("SYSTEM", "Backup creation failed: " + e.getMessage());
        }
    }

    private LinkedList<Item> getNonVetoedFormulations() {
        LinkedList<Item> nonVetoed = new LinkedList<>();

        for (Item item : allFormulations) {
            boolean vetoed = false;

            if (item instanceof MyClasses.Consumables.Food) {
                vetoed = ((MyClasses.Consumables.Food) item).isVetoed();
            } else if (item instanceof MyClasses.Consumables.Drink) {
                vetoed = ((MyClasses.Consumables.Drink) item).isVetoed();
            }

            if (!vetoed) {
                nonVetoed.add(item);
            }
        }

        return nonVetoed;
    }

    private void logout() {
        if (currentUser != null) {
            String name = "";
            if (currentUser instanceof Admin) {
                name = ((Admin) currentUser).getName();
            } else if (currentUser instanceof Author) {
                name = ((Author) currentUser).getName();
            } else if (currentUser instanceof Customer) {
                name = ((Customer) currentUser).getName();
            }

            auditTrail.logAction(currentUserType + ":" + name, "Logged out at " + new Date());
            screen.display("\nLogging out " + name + "...");

            // Auto-save to database on logout
            saveDataToDatabase();

            currentUser = null;
            currentUserType = null;
            screen.display("✓ Logged out successfully");
        }
    }

    // ============ MAIN METHOD ============

    public static void main(String[] args) {
        Main system = new Main();
        system.start();
    }
}