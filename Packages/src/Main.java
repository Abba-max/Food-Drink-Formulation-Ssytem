import MyClasses.Consumables.Item;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;

import java.util.LinkedList;


public class Main {

    private LinkedList<Admin> admins;
    private LinkedList<Author> authors;
    private LinkedList<Customer> customers;
    private LinkedList<Item> allFormulations;

    private Keypad pad;
    private Screen screen;

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

        // Create default admin account
        initializeSystem();
    }

    /**
     * Initialize system with default admin
     */
    private void initializeSystem() {
        Admin defaultAdmin = new Admin(1, "System Admin", "HQ", "+1-000-0000", "1980-01-01", "admin123");
        admins.add(defaultAdmin);

        screen.display("System initialized with default admin account.");
        screen.display("Username: admin | Password: admin123");
    }

    /**
     * Main entry point
     */
    public void start() {
        screen.display("\n" + "=".repeat(60));
        screen.display("   FOOD & DRINK FORMULATION MANAGEMENT SYSTEM");
        screen.display("=".repeat(60));

        while (true) {
            if (currentUser == null) {
                showWelcomeMenu();
            } else {
                showUserMenu();
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
        screen.display("0. Exit");
        screen.display("\nEnter choice:");

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
            case 0:
                screen.display("Thank you for using the system. Goodbye!");
                System.exit(0);
            default:
                screen.display("Invalid choice!");
        }
    }

    // ============ LOGIN METHODS ============

    private void loginAsAdmin() {
        screen.display("\n=== ADMIN LOGIN ===");
        screen.display("Enter Admin ID:");
        int id = pad.getInt();

        screen.display("Enter password:");
        String password = pad.getString();

        for (Admin admin : admins) {
            if (admin.getAdminID() == id && admin.getPassword().equals(password)) {
                currentUser = admin;
                currentUserType = "ADMIN";

                // Set system references
                admin.setAuthors(authors);
                admin.setAdmins(admins);
                admin.setCustomers(customers);
                admin.setAllFormulations(allFormulations);

                screen.display("\n✓ Login successful!");
                screen.display("Welcome, " + admin.getName());
                return;
            }
        }

        screen.display("⚠ Invalid credentials!");
    }

    private void loginAsAuthor() {
        screen.display("\n=== AUTHOR LOGIN ===");
        screen.display("Enter Author ID:");
        int id = pad.getInt();

        screen.display("Enter password:");
        String password = pad.getString();

        for (Author author : authors) {
            if (author.getAuthorID() == id && author.getPassword().equals(password)) {
                currentUser = author;
                currentUserType = "AUTHOR";
                screen.display("\n✓ Login successful!");
                screen.display("Welcome, " + author.getName());
                return;
            }
        }

        screen.display("⚠ Invalid credentials!");
        screen.display("Note: Authors must be created by an Admin first.");
    }

    private void loginAsCustomer() {
        screen.display("\n=== CUSTOMER LOGIN ===");
        screen.display("Enter Customer ID:");
        int id = pad.getInt();

        screen.display("Enter password:");
        String password = pad.getString();

        for (Customer customer : customers) {
            if (customer.getCustomerID() == id && customer.getPassword().equals(password)) {
                currentUser = customer;
                currentUserType = "CUSTOMER";

                // Set available formulations (only non-vetoed ones)
                LinkedList<Item> availableItems = getNonVetoedFormulations();
                customer.setAvailableFormulations(availableItems);

                screen.display("\n✓ Login successful!");
                screen.display("Welcome, " + customer.getName());
                return;
            }
        }

        screen.display("⚠ Invalid credentials!");
    }

    private void registerCustomer() {
        screen.display("\n=== CUSTOMER REGISTRATION ===");

        screen.display("Enter desired Customer ID:");
        int id = pad.getInt();

        // Check if ID exists
        for (Customer c : customers) {
            if (c.getCustomerID() == id) {
                screen.display("⚠ Customer ID already exists!");
                return;
            }
        }

        screen.display("Enter name:");
        String name = pad.getString();

        screen.display("Enter address:");
        String address = pad.getString();

        screen.display("Enter contact:");
        String contact = pad.getString();

        screen.display("Enter date of birth (YYYY-MM-DD):");
        String dob = pad.getString();

        screen.display("Enter age:");
        int age = pad.getInt();

        screen.display("Enter password:");
        String password = pad.getString();

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
        screen.display("0. Logout");
        screen.display("\nEnter choice:");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                showAccountManagementMenu(admin);
                break;
            case 2:
                admin.consultFormulation();
                break;
            case 3:
                admin.checkFormulationissues();
                break;
            case 4:
                showSystemStatistics();
                break;
            case 5:
                admin.viewAllAccounts();
                break;
            case 0:
                logout();
                break;
            default:
                screen.display("Invalid choice!");
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

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                Author newAuthor = admin.createAuthorAccount();
                if (newAuthor != null) {
                    authors.add(newAuthor);
                }
                break;
            case 2:
                Admin newAdmin = admin.createAdminAccount();
                if (newAdmin != null) {
                    admins.add(newAdmin);
                }
                break;
            case 3:
                viewAllAuthors();
                break;
            case 4:
                viewAllAdmins();
                break;
            case 0:
                return;
            default:
                screen.display("Invalid choice!");
        }
    }

    // ============ AUTHOR MENU ============

    private void showAuthorMenu() {
        Author author = (Author) currentUser;

        screen.display("\n" + "=".repeat(60));
        screen.display("   AUTHOR MENU - " + author.getName());
        screen.display("=".repeat(60));
        screen.display("1. Create New Formulation");
        screen.display("2. Consult My Formulations");
        screen.display("3. Check Formulation Issues");
        screen.display("4. View My Statistics");
        screen.display("0. Logout");
        screen.display("\nEnter choice:");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                Item newItem = author.Formulate();
                if (newItem != null) {
                    allFormulations.add(newItem);
                    screen.display("✓ Formulation added to system catalog");
                }
                break;
            case 2:
                author.consultFormulation();
                break;
            case 3:
                author.checkFormulationissues();
                break;
            case 4:
                showAuthorStatistics(author);
                break;
            case 0:
                logout();
                break;
            default:
                screen.display("Invalid choice!");
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
        screen.display("0. Logout");
        screen.display("\nEnter choice:");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                customer.consultFormulation();
                break;
            case 2:
                customer.viewPurchaseHistory();
                break;
            case 3:
                viewCustomerFavorites(customer);
                break;
            case 4:
                viewCustomerProfile(customer);
                break;
            case 0:
                logout();
                break;
            default:
                screen.display("Invalid choice!");
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

            screen.display("\nLogging out " + name + "...");
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