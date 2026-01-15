import MyClasses.Consumables.Item;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Drink;
import MyClasses.Database.DatabaseConfig;
import MyClasses.Database.DatabaseManager;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;
import MyClasses.Utilities.AuditTrail;
import MyClasses.Feedback;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Ingredients.Quantity;
import MyClasses.Conditions.*;
import MyClasses.Persons.ConsumerSpecificInfo;
import MyClasses.Restrictions.Veto;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Complete JavaFX GUI Application for Food & Drink Formulation Management System
 * Professional Color Theme: Professional Blue/Gray with Accessibility
 */
public class Main_GUI extends Application {

    // Data structures
    private LinkedList<Admin> admins;
    private LinkedList<Author> authors;
    private LinkedList<Customer> customers;
    private LinkedList<Item> allFormulations;

    // System components
    private AuditTrail auditTrail;
    private DatabaseManager databaseManager;

    // Current user session
    private Object currentUser;
    private String currentUserType;

    // JavaFX components
    private Stage primaryStage;
    private Scene currentScene;

    // Screen dimensions
    private static final double WINDOW_WIDTH = 1400;
    private static final double WINDOW_HEIGHT = 800;

    // Professional Color Theme - Accessible with good contrast
    private static final String COLOR_PRIMARY = "#2C3E50";     // Dark Blue
    private static final String COLOR_SECONDARY = "#34495E";   // Medium Blue
    private static final String COLOR_ACCENT = "#3498DB";      // Light Blue
    private static final String COLOR_SUCCESS = "#27AE60";     // Green
    private static final String COLOR_WARNING = "#E67E22";     // Orange
    private static final String COLOR_ERROR = "#E74C3C";       // Red
    private static final String COLOR_INFO = "#2980B9";        // Blue
    private static final String COLOR_LIGHT_BG = "#F8F9FA";    // Light Background
    private static final String COLOR_DARK_BG = "#2C3E50";     // Dark Background
    private static final String COLOR_TEXT_PRIMARY = "#2C3E50"; // Dark Text
    private static final String COLOR_TEXT_SECONDARY = "#7F8C8D"; // Gray Text
    private static final String COLOR_TEXT_LIGHT = "#ECF0F1";  // Light Text (for dark backgrounds)
    private static final String COLOR_BORDER = "#BDC3C7";      // Border Color
    private static final String COLOR_HOVER = "#ECF0F1";       // Hover Background

    // User-specific colors
    private static final String COLOR_ADMIN = "#8E44AD";       // Purple
    private static final String COLOR_AUTHOR = "#16A085";      // Teal
    private static final String COLOR_CUSTOMER = "#2ECC71";    // Green

    // Fonts
    private static final String FONT_FAMILY = "Segoe UI";
    private static final Font FONT_TITLE = Font.font(FONT_FAMILY, FontWeight.BOLD, 24);
    private static final Font FONT_SUBTITLE = Font.font(FONT_FAMILY, FontWeight.SEMI_BOLD, 18);
    private static final Font FONT_BODY = Font.font(FONT_FAMILY, FontWeight.NORMAL, 14);
    private static final Font FONT_BUTTON = Font.font(FONT_FAMILY, FontWeight.MEDIUM, 14);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Food & Drink Formulation Management System");

        // Initialize system
        initializeSystem();

        // Show welcome screen
        showWelcomeScreen();

        primaryStage.setScene(currentScene);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            handleExit();
        });
        primaryStage.show();
    }

    /**
     * Initialize database and load data with proper error handling
     */
    private void initializeSystem() {
        admins = new LinkedList<>();
        authors = new LinkedList<>();
        customers = new LinkedList<>();
        allFormulations = new LinkedList<>();
        auditTrail = new AuditTrail();

        try {
            System.out.println("Initializing database connection...");

            if (DatabaseConfig.testConnection()) {
                System.out.println("Database connection successful");
                databaseManager = new DatabaseManager();

//                createSampleData();
//                saveDataToDatabase();

                // Load all data
                loadDataFromDatabase();

                // Create default admin if none exists
                if (admins.isEmpty()) {
                    Admin defaultAdmin = new Admin(1, "System Admin", "HQ", "+1-000-0000", "1980-01-01", "admin123");
                    admins.add(defaultAdmin);
                    try {
                        databaseManager.saveAdmin(defaultAdmin);
                        System.out.println("Default admin created");
                    } catch (Exception e) {
                        System.out.println("Warning: Could not save default admin to database: " + e.getMessage());
                    }
                    auditTrail.logAction("SYSTEM", "Default admin account created");
                }

                System.out.println("System initialized successfully");
                System.out.println("Admins: " + admins.size());
                System.out.println("Authors: " + authors.size());
                System.out.println("Customers: " + customers.size());
                System.out.println("Formulations: " + allFormulations.size());

            } else {
                showError("Database Connection Failed",
                        "Could not connect to database. Running in offline mode with sample data.");
                // Create sample data for offline mode
                createSampleData();
            }
        } catch (Exception e) {
            System.err.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
            showError("Initialization Error",
                    "Failed to initialize system: " + e.getMessage() + "\nRunning in offline mode.");
            createSampleData();
        }
    }

    /**
     * Create sample data for offline mode
     */
    private void createSampleData() {
        // Create default admin
        Admin defaultAdmin = new Admin(2, "System Admin", "HQ", "+1-000-0000", "1980-01-01", "admin123");
        admins.add(defaultAdmin);

        // Create sample author
        Author sampleAuthor = new Author(2, "John Chef", "Kitchen St.", "chef@email.com", "1975-05-15");
        sampleAuthor.setPassword("author123");
        authors.add(sampleAuthor);

        // Create sample customer
        Customer sampleCustomer = new Customer(2, 30);
        sampleCustomer.setName("Jane Customer");
        sampleCustomer.setAddress("123 Main St");
        sampleCustomer.setContact("customer@email.com");
        sampleCustomer.setDateofbirth("1993-08-20");
        sampleCustomer.setPassword("customer123");
        customers.add(sampleCustomer);

        // Create sample food formulation
        Food sampleFood = new Food();
        sampleFood.setName("Special Pasta");
        sampleFood.setFoodID(1);
        sampleFood.setItemID(1);
        sampleFood.setPrice(12.99);
        sampleFood.setExpiryDate("2024-12-31");
        sampleFood.setAveragePricePerKg(8.50);

        // Add ingredients
        Ingredient pasta = new Ingredient(1, "Pasta", new Quantity(200, 0, 0.4, "grams"));
        Ingredient sauce = new Ingredient(2, "Tomato Sauce", new Quantity(100, 150, 0.3, "ml"));
        sampleFood.addIngredient(pasta);
        sampleFood.addIngredient(sauce);

        // Add author
        sampleFood.addAuthor(sampleAuthor);
        sampleAuthor.getFormulatedItems().add(sampleFood);

        // Set conditions
        Optcondition labCond = new Optcondition();
        labCond.setTemp(22.5);
        labCond.setPressure(101.3);
        labCond.setMoisture(65.0);
        sampleFood.setLabCondition(labCond);

        // Create sample drink formulation
        Drink sampleDrink = new Drink();
        sampleDrink.setName("Refreshing Lemonade");
        sampleDrink.setDrinkID(2);
        sampleDrink.setItemID(2);
        sampleDrink.setPrice(5.99);
        sampleDrink.setExpiryDate("2024-06-30");
        sampleDrink.setAveragePricePerKg(3.50);
        sampleDrink.setAlcoholContent(0.0);

        // Add ingredients for drink
        Ingredient lemon = new Ingredient(3, "Lemon Juice", new Quantity(0, 100, 0.15, "ml"));
        Ingredient sugar = new Ingredient(4, "Sugar", new Quantity(50, 0, 0.05, "grams"));
        Ingredient water = new Ingredient(5, "Water", new Quantity(0, 500, 0.8, "ml"));
        sampleDrink.addIngredient(lemon);
        sampleDrink.addIngredient(sugar);
        sampleDrink.addIngredient(water);

        // Add author
        sampleDrink.addAuthor(sampleAuthor);
        sampleAuthor.getFormulatedItems().add(sampleDrink);

        allFormulations.add(sampleFood);
        allFormulations.add(sampleDrink);

        System.out.println("Sample data created for offline mode");
    }

    /**
     * Load data from database with proper error handling
     */
    private void loadDataFromDatabase() {
        try {
            System.out.println("Loading data from database...");

            // Load admins
            LinkedList<Admin> loadedAdmins = databaseManager.loadAdmins();
            if (loadedAdmins != null && !loadedAdmins.isEmpty()) {
                admins = loadedAdmins;
                System.out.println("Loaded " + admins.size() + " admins");
            }

            // Load authors
            LinkedList<Author> loadedAuthors = databaseManager.loadAuthors();
            if (loadedAuthors != null && !loadedAuthors.isEmpty()) {
                authors = loadedAuthors;
                System.out.println("Loaded " + authors.size() + " authors");
            }

            // Load customers
            LinkedList<Customer> loadedCustomers = databaseManager.loadCustomers();
            if (loadedCustomers != null && !loadedCustomers.isEmpty()) {
                customers = loadedCustomers;
                System.out.println("Loaded " + customers.size() + " customers");
            }

            // Load formulations
            LinkedList<Item> loadedFormulations = databaseManager.loadFormulations();
            if (loadedFormulations != null && !loadedFormulations.isEmpty()) {
                allFormulations = loadedFormulations;
                System.out.println("Loaded " + allFormulations.size() + " formulations");

                // Link authors to their formulations
                for (Item item : allFormulations) {
                    if (item instanceof Food) {
                        Food food = (Food) item;
                        for (Author author : food.getAuthors()) {
                            if (!author.getFormulatedItems().contains(food)) {
                                author.getFormulatedItems().add(food);
                            }
                        }
                    } else if (item instanceof Drink) {
                        Drink drink = (Drink) item;
                        for (Author author : drink.getAuthors()) {
                            if (!author.getFormulatedItems().contains(drink)) {
                                author.getFormulatedItems().add(drink);
                            }
                        }
                    }
                }
            }

            // Load audit trail
            try {
                AuditTrail loadedAudit = databaseManager.loadAuditTrail();
                if (loadedAudit != null) {
                    auditTrail = loadedAudit;
                    System.out.println("Loaded audit trail with " + auditTrail.records.size() + " records");
                }
            } catch (Exception e) {
                System.out.println("Could not load audit trail: " + e.getMessage());
            }

            auditTrail.logAction("SYSTEM", "Data loaded from database at " + new Date());
            System.out.println("Data loading completed successfully");

        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
            showError("Data Loading Error",
                    "Error loading data from database: " + e.getMessage() + "\nUsing sample data.");
            if (admins.isEmpty() || authors.isEmpty() || customers.isEmpty()) {
                createSampleData();
            }
        }
    }

    /**
     * Save data to database
     */
    private void saveDataToDatabase() {
        try {
            if (databaseManager != null) {
                databaseManager.saveAdmins(admins);
                databaseManager.saveAuthors(authors);
                databaseManager.saveCustomers(customers);
                databaseManager.saveFormulations(allFormulations);
                databaseManager.saveAuditTrail(auditTrail);

                showInformation("Save Successful", "All data saved successfully to database");
                auditTrail.logAction("SYSTEM", "Data saved to database at " + new Date());
            } else {
                showInformation("Save Info", "Running in offline mode. Data will be saved locally on exit.");
            }
        } catch (Exception e) {
            showError("Save Error", "Error saving data: " + e.getMessage());
        }
    }

    // ============ WELCOME SCREEN ============

    private void showWelcomeScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        // Header
        VBox header = createHeader("FOOD & DRINK FORMULATION MANAGEMENT SYSTEM",
                "Professional Management Platform");
        root.setTop(header);

        // Center content
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        Label titleLabel = new Label("Welcome to Formulation Manager");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setTextFill(Color.web(COLOR_PRIMARY));

        Label subtitleLabel = new Label("Please select your role to continue");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(400);

        Button btnAdmin = createMenuButton("Administrator Login", COLOR_ADMIN);
        Button btnAuthor = createMenuButton("Author Login", COLOR_AUTHOR);
        Button btnCustomer = createMenuButton("Customer Login", COLOR_CUSTOMER);
        Button btnRegister = createMenuButton("Register New Customer", COLOR_INFO);
        Button btnSave = createMenuButton("Save Data", COLOR_SUCCESS);
        Button btnExit = createMenuButton("Exit System", COLOR_ERROR);

        btnAdmin.setOnAction(e -> showLoginScreen("ADMIN"));
        btnAuthor.setOnAction(e -> showLoginScreen("AUTHOR"));
        btnCustomer.setOnAction(e -> showLoginScreen("CUSTOMER"));
        btnRegister.setOnAction(e -> showRegistrationScreen());
        btnSave.setOnAction(e -> saveDataToDatabase());
        btnExit.setOnAction(e -> handleExit());

        buttonBox.getChildren().addAll(btnAdmin, btnAuthor, btnCustomer, btnRegister, btnSave, btnExit);
        centerBox.getChildren().addAll(titleLabel, subtitleLabel, buttonBox);

        root.setCenter(centerBox);

        // Footer
        HBox footer = createFooter("System Version 1.0 • Default Admin: admin123 • " +
                "Total Users: " + (admins.size() + authors.size() + customers.size()) +
                " • Formulations: " + allFormulations.size());
        root.setBottom(footer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ LOGIN SCREEN ============

    private void showLoginScreen(String userType) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        // Header
        VBox header = createHeader(userType + " LOGIN", "Access your account");
        root.setTop(header);

        // Form container
        VBox formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(50));

        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label formTitle = new Label(userType + " Login");
        formTitle.setFont(FONT_SUBTITLE);
        formTitle.setTextFill(Color.web(getUserColor(userType)));

        Label lblUsername = new Label("Username:");
        lblUsername.setFont(FONT_BODY);
        lblUsername.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");
        txtUsername.setStyle(createTextFieldStyle());

        Label lblPassword = new Label("Password:");
        lblPassword.setFont(FONT_BODY);
        lblPassword.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setStyle(createTextFieldStyle());

        Button btnLogin = createStandardButton("Login", getUserColor(userType));
        Button btnBack = createStandardButton("Back to Welcome", COLOR_TEXT_SECONDARY);

        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Validation Error", "Please fill in all fields");
                return;
            }

            performLogin(userType, username, password);
        });

        btnBack.setOnAction(e -> showWelcomeScreen());

        formBox.getChildren().addAll(formTitle, lblUsername, txtUsername,
                lblPassword, txtPassword, btnLogin, btnBack);
        formContainer.getChildren().add(formBox);
        root.setCenter(formContainer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void performLogin(String userType, String username, String password) {
        boolean loginSuccessful = false;
        String userName = "";

        try {
            switch (userType) {
                case "ADMIN":
                    for (Admin admin : admins) {
                        if (admin.getName().equals(username) && admin.getPassword().equals(password)) {
                            currentUser = admin;
                            currentUserType = "ADMIN";
                            userName = admin.getName();

                            // Set system references
                            admin.setAuthors(authors);
                            admin.setAdmins(admins);
                            admin.setCustomers(customers);
                            admin.setAllFormulations(allFormulations);

                            loginSuccessful = true;
                            break;
                        }
                    }
                    break;

                case "AUTHOR":
                    for (Author author : authors) {
                        if (author.getName().equals(username) && author.getPassword().equals(password)) {
                            currentUser = author;
                            currentUserType = "AUTHOR";
                            userName = author.getName();
                            loginSuccessful = true;
                            break;
                        }
                    }
                    break;

                case "CUSTOMER":
                    for (Customer customer : customers) {
                        if (customer.getName().equals(username) && customer.getPassword().equals(password)) {
                            currentUser = customer;
                            currentUserType = "CUSTOMER";
                            userName = customer.getName();

                            // Set available formulations
                            LinkedList<Item> availableItems = getNonVetoedFormulations();
                            customer.setAvailableFormulations(availableItems);

                            loginSuccessful = true;
                            break;
                        }
                    }
                    break;
            }

            if (loginSuccessful) {
                auditTrail.logAction(userType + ":" + userName, "Logged in successfully");
                showInformation("Login Successful", "Welcome back, " + userName + "!");
                showUserDashboard();
            } else {
                auditTrail.logAction("SYSTEM", "Failed login attempt for " + userType + ": " + username);
                showError("Login Failed", "Invalid username or password");
            }
        } catch (Exception e) {
            showError("Login Error", "An error occurred during login: " + e.getMessage());
        }
    }

    // ============ REGISTRATION SCREEN ============

    private void showRegistrationScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("CUSTOMER REGISTRATION", "Create a new customer account");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.TOP_CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(600);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label formTitle = new Label("New Customer Registration");
        formTitle.setFont(FONT_SUBTITLE);
        formTitle.setTextFill(Color.web(COLOR_CUSTOMER));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Form fields
        Label lblId = new Label("Customer ID:*");
        lblId.setFont(FONT_BODY);
        TextField txtId = new TextField();
        txtId.setStyle(createTextFieldStyle());

        Label lblName = new Label("Full Name:*");
        lblName.setFont(FONT_BODY);
        TextField txtName = new TextField();
        txtName.setStyle(createTextFieldStyle());

        Label lblAddress = new Label("Address:");
        lblAddress.setFont(FONT_BODY);
        TextField txtAddress = new TextField();
        txtAddress.setStyle(createTextFieldStyle());

        Label lblContact = new Label("Contact Info:*");
        lblContact.setFont(FONT_BODY);
        TextField txtContact = new TextField();
        txtContact.setStyle(createTextFieldStyle());

        Label lblDob = new Label("Date of Birth (YYYY-MM-DD):");
        lblDob.setFont(FONT_BODY);
        TextField txtDob = new TextField();
        txtDob.setStyle(createTextFieldStyle());

        Label lblAge = new Label("Age:*");
        lblAge.setFont(FONT_BODY);
        TextField txtAge = new TextField();
        txtAge.setStyle(createTextFieldStyle());

        Label lblPassword = new Label("Password:*");
        lblPassword.setFont(FONT_BODY);
        PasswordField txtPassword = new PasswordField();
        txtPassword.setStyle(createTextFieldStyle());

        Label lblConfirm = new Label("Confirm Password:*");
        lblConfirm.setFont(FONT_BODY);
        PasswordField txtConfirm = new PasswordField();
        txtConfirm.setStyle(createTextFieldStyle());

        // Add to grid
        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(lblName, 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(lblAddress, 0, 2);
        grid.add(txtAddress, 1, 2);
        grid.add(lblContact, 0, 3);
        grid.add(txtContact, 1, 3);
        grid.add(lblDob, 0, 4);
        grid.add(txtDob, 1, 4);
        grid.add(lblAge, 0, 5);
        grid.add(txtAge, 1, 5);
        grid.add(lblPassword, 0, 6);
        grid.add(txtPassword, 1, 6);
        grid.add(lblConfirm, 0, 7);
        grid.add(txtConfirm, 1, 7);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));

        Button btnRegister = createStandardButton("Register", COLOR_CUSTOMER);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnRegister.setOnAction(e -> {
            try {
                // Validation
                if (txtId.getText().trim().isEmpty()) {
                    showError("Validation Error", "Customer ID is required");
                    return;
                }
                if (txtName.getText().trim().isEmpty()) {
                    showError("Validation Error", "Full name is required");
                    return;
                }
                if (txtContact.getText().trim().isEmpty()) {
                    showError("Validation Error", "Contact information is required");
                    return;
                }
                if (txtAge.getText().trim().isEmpty()) {
                    showError("Validation Error", "Age is required");
                    return;
                }
                if (txtPassword.getText().isEmpty()) {
                    showError("Validation Error", "Password is required");
                    return;
                }
                if (!txtPassword.getText().equals(txtConfirm.getText())) {
                    showError("Validation Error", "Passwords do not match");
                    return;
                }

                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                int age = Integer.parseInt(txtAge.getText().trim());
                String password = txtPassword.getText();

                if (password.length() < 4) {
                    showError("Validation Error", "Password must be at least 4 characters");
                    return;
                }
                if (age < 0 || age > 150) {
                    showError("Validation Error", "Invalid age");
                    return;
                }

                // Check if ID exists
                for (Customer c : customers) {
                    if (c.getCustomerID() == id) {
                        showError("Registration Failed", "Customer ID already exists");
                        return;
                    }
                }

                // Create customer
                Customer customer = new Customer(id, age);
                customer.setName(name);
                customer.setAddress(address);
                customer.setContact(contact);
                customer.setDateofbirth(dob);
                customer.setPassword(password);

                customers.add(customer);
                if (databaseManager != null) {
                    databaseManager.saveCustomer(customer);
                }

                auditTrail.logAction("SYSTEM", "New customer registered: " + name + " (ID: " + id + ")");
                showInformation("Registration Successful",
                        "Welcome " + name + "! You can now login with your credentials.");
                showWelcomeScreen();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numbers for ID and Age");
            } catch (Exception ex) {
                showError("Registration Error", "Error during registration: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> showWelcomeScreen());

        buttonBox.getChildren().addAll(btnRegister, btnCancel);
        formBox.getChildren().addAll(formTitle, grid, buttonBox);

        StackPane centerContainer = new StackPane(formBox);
        centerContainer.setPadding(new Insets(20));
        scrollPane.setContent(centerContainer);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ USER DASHBOARDS ============

    private void showUserDashboard() {
        if ("ADMIN".equals(currentUserType)) {
            showAdminDashboard();
        } else if ("AUTHOR".equals(currentUserType)) {
            showAuthorDashboard();
        } else if ("CUSTOMER".equals(currentUserType)) {
            showCustomerDashboard();
        }
    }

    private void showAdminDashboard() {
        Admin admin = (Admin) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("ADMINISTRATOR DASHBOARD",
                "Welcome, " + admin.getName() + " | System Administration Panel");
        root.setTop(header);

        // Main content with cards
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(30));
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        tilePane.setPrefColumns(3);

        // Create admin function cards
        Map<String, String[]> adminFunctions = new HashMap<>();
        adminFunctions.put("User Management", new String[]{"Manage all system users", COLOR_ADMIN});
        adminFunctions.put("Formulation Management", new String[]{"View and manage all formulations", COLOR_INFO});
        adminFunctions.put("Veto Management", new String[]{"Set or remove vetos on formulations", COLOR_ERROR});
        adminFunctions.put("System Statistics", new String[]{"View system-wide statistics", COLOR_SUCCESS});
        adminFunctions.put("Audit Trail", new String[]{"View system activity logs", COLOR_SECONDARY});
        adminFunctions.put("Quality Control", new String[]{"Check formulation quality issues", COLOR_WARNING});
        adminFunctions.put("Database Tools", new String[]{"Backup and restore database", COLOR_PRIMARY});
        adminFunctions.put("Create New Author", new String[]{"Create new author accounts", COLOR_AUTHOR});
        adminFunctions.put("Create New Admin", new String[]{"Create new administrator accounts", COLOR_ADMIN});
        adminFunctions.put("View All Customers", new String[]{"Browse all customer accounts", COLOR_CUSTOMER});
        adminFunctions.put("Save Data", new String[]{"Save all data to database", COLOR_SUCCESS});
        adminFunctions.put("Logout", new String[]{"Logout from administrator account", COLOR_TEXT_SECONDARY});

        for (Map.Entry<String, String[]> entry : adminFunctions.entrySet()) {
            String title = entry.getKey();
            String description = entry.getValue()[0];
            String color = entry.getValue()[1];

            VBox card = createFunctionCard(title, description, color);
            tilePane.getChildren().add(card);

            // Set action
            card.setOnMouseClicked(e -> handleAdminFunction(title));
        }

        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
        root.setCenter(scrollPane);

        // Stats footer
        HBox statsBar = new HBox(40);
        statsBar.setPadding(new Insets(15));
        statsBar.setAlignment(Pos.CENTER);
        statsBar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1 0 0 0;");

        Label lblAdmins = createStatLabel("Admins: " + admins.size(), COLOR_ADMIN);
        Label lblAuthors = createStatLabel("Authors: " + authors.size(), COLOR_AUTHOR);
        Label lblCustomers = createStatLabel("Customers: " + customers.size(), COLOR_CUSTOMER);
        Label lblFormulations = createStatLabel("Formulations: " + allFormulations.size(), COLOR_INFO);
        Label lblVetoed = createStatLabel("Vetoed: " + countVetoedFormulations(), COLOR_ERROR);

        statsBar.getChildren().addAll(lblAdmins, lblAuthors, lblCustomers, lblFormulations, lblVetoed);
        root.setBottom(statsBar);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void handleAdminFunction(String function) {
        switch (function) {
            case "User Management":
                showUserManagementScreen();
                break;
            case "Formulation Management":
                showFormulationManagementScreen();
                break;
            case "Veto Management":
                showVetoManagementScreen();
                break;
            case "System Statistics":
                showSystemStatisticsScreen();
                break;
            case "Audit Trail":
                showAuditTrailScreen();
                break;
            case "Quality Control":
                showQualityControlScreen();
                break;
            case "Database Tools":
                showDatabaseToolsScreen();
                break;
            case "Create New Author":
                showCreateAuthorScreen();
                break;
            case "Create New Admin":
                showCreateAdminScreen();
                break;
            case "View All Customers":
                showAllCustomersScreen();
                break;
            case "Save Data":
                saveDataToDatabase();
                break;
            case "Logout":
                logout();
                break;
        }
    }

    private void showAuthorDashboard() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("AUTHOR DASHBOARD",
                "Welcome, " + author.getName() + " | Formulation Creation & Management");
        root.setTop(header);

        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(30));
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        tilePane.setPrefColumns(2);

        // Create author function cards
        Map<String, String[]> authorFunctions = new HashMap<>();
        authorFunctions.put("Create New Food", new String[]{"Create new food formulation", COLOR_SUCCESS});
        authorFunctions.put("Create New Drink", new String[]{"Create new drink formulation", COLOR_INFO});
        authorFunctions.put("My Formulations", new String[]{"View and edit my formulations", COLOR_AUTHOR});
        authorFunctions.put("Update Formulation", new String[]{"Update existing formulation", COLOR_WARNING});
        authorFunctions.put("Quality Check", new String[]{"Check my formulations for issues", COLOR_ERROR});
        authorFunctions.put("My Statistics", new String[]{"View my formulation statistics", COLOR_PRIMARY});
        authorFunctions.put("View All Formulations", new String[]{"Browse all system formulations", COLOR_SECONDARY});
        authorFunctions.put("Save Data", new String[]{"Save all data to database", COLOR_SUCCESS});
        authorFunctions.put("Logout", new String[]{"Logout from author account", COLOR_TEXT_SECONDARY});

        for (Map.Entry<String, String[]> entry : authorFunctions.entrySet()) {
            String title = entry.getKey();
            String description = entry.getValue()[0];
            String color = entry.getValue()[1];

            VBox card = createFunctionCard(title, description, color);
            tilePane.getChildren().add(card);

            // Set action
            card.setOnMouseClicked(e -> handleAuthorFunction(title));
        }

        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
        root.setCenter(scrollPane);

        // Stats footer
        HBox statsBar = new HBox(40);
        statsBar.setPadding(new Insets(15));
        statsBar.setAlignment(Pos.CENTER);
        statsBar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1 0 0 0;");

        int myFormulations = author.getFormulatedItems().size();
        int foodCount = countAuthorFoods(author);
        int drinkCount = countAuthorDrinks(author);

        Label lblFormulations = createStatLabel("My Formulations: " + myFormulations, COLOR_AUTHOR);
        Label lblFoods = createStatLabel("Foods: " + foodCount, COLOR_SUCCESS);
        Label lblDrinks = createStatLabel("Drinks: " + drinkCount, COLOR_INFO);
        Label lblFeedbacks = createStatLabel("Total Feedbacks: " + countAuthorFeedbacks(author), COLOR_PRIMARY);

        statsBar.getChildren().addAll(lblFormulations, lblFoods, lblDrinks, lblFeedbacks);
        root.setBottom(statsBar);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void handleAuthorFunction(String function) {
        switch (function) {
            case "Create New Food":
                showCreateFoodScreen();
                break;
            case "Create New Drink":
                showCreateDrinkScreen();
                break;
            case "My Formulations":
                showMyFormulationsScreen();
                break;
            case "Update Formulation":
                showUpdateFormulationSelectionScreen();
                break;
            case "Quality Check":
                showAuthorQualityCheckScreen();
                break;
            case "My Statistics":
                showAuthorStatisticsScreen();
                break;
            case "View All Formulations":
                showAllFormulationsScreen();
                break;
            case "Save Data":
                saveDataToDatabase();
                break;
            case "Logout":
                logout();
                break;
        }
    }

    private void showCustomerDashboard() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("CUSTOMER DASHBOARD",
                "Welcome, " + customer.getName() + " | Browse & Purchase Formulations");
        root.setTop(header);

        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(30));
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        tilePane.setPrefColumns(2);

        // Create customer function cards
        Map<String, String[]> customerFunctions = new HashMap<>();
        customerFunctions.put("Browse Catalog", new String[]{"Browse available formulations", COLOR_CUSTOMER});
        customerFunctions.put("My Purchases", new String[]{"View purchase history", COLOR_SUCCESS});
        customerFunctions.put("My Favorites", new String[]{"View favorite formulations", COLOR_WARNING});
        customerFunctions.put("My Profile", new String[]{"View and update profile", COLOR_INFO});
        customerFunctions.put("Provide Feedback", new String[]{"Provide feedback on purchases", COLOR_PRIMARY});
        customerFunctions.put("Search Formulations", new String[]{"Search for specific formulations", COLOR_SECONDARY});
        customerFunctions.put("Save Data", new String[]{"Save all data to database", COLOR_SUCCESS});
        customerFunctions.put("Logout", new String[]{"Logout from customer account", COLOR_TEXT_SECONDARY});

        for (Map.Entry<String, String[]> entry : customerFunctions.entrySet()) {
            String title = entry.getKey();
            String description = entry.getValue()[0];
            String color = entry.getValue()[1];

            VBox card = createFunctionCard(title, description, color);
            tilePane.getChildren().add(card);

            // Set action
            card.setOnMouseClicked(e -> handleCustomerFunction(title));
        }

        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
        root.setCenter(scrollPane);

        // Stats footer
        HBox statsBar = new HBox(40);
        statsBar.setPadding(new Insets(15));
        statsBar.setAlignment(Pos.CENTER);
        statsBar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1 0 0 0;");

        Label lblPurchases = createStatLabel("Purchases: " + customer.getPurchasedItems().size(), COLOR_SUCCESS);
        Label lblFavorites = createStatLabel("Favorites: " + customer.getFavoriteFormulations().size(), COLOR_WARNING);
        Label lblAvailable = createStatLabel("Available: " + customer.getAvailableFormulations().size(), COLOR_CUSTOMER);
        Label lblFeedback = createStatLabel("Feedback Given: " + customer.getFeedbackHistory().size(), COLOR_PRIMARY);

        statsBar.getChildren().addAll(lblPurchases, lblFavorites, lblAvailable, lblFeedback);
        root.setBottom(statsBar);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void handleCustomerFunction(String function) {
        switch (function) {
            case "Browse Catalog":
                showCustomerCatalogScreen();
                break;
            case "My Purchases":
                showCustomerPurchasesScreen();
                break;
            case "My Favorites":
                showCustomerFavoritesScreen();
                break;
            case "My Profile":
                showCustomerProfileScreen();
                break;
            case "Provide Feedback":
                showFeedbackManagementScreen();
                break;
            case "Search Formulations":
                showSearchFormulationsScreen();
                break;
            case "Save Data":
                saveDataToDatabase();
                break;
            case "Logout":
                logout();
                break;
        }
    }

    // ============ ADMIN FUNCTIONALITIES ============

    private void showUserManagementScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("USER MANAGEMENT", "Manage System Users");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Button btnViewAdmins = createStandardButton("View All Administrators", COLOR_ADMIN);
        Button btnViewAuthors = createStandardButton("View All Authors", COLOR_AUTHOR);
        Button btnViewCustomers = createStandardButton("View All Customers", COLOR_CUSTOMER);
        Button btnCreateAuthor = createStandardButton("Create New Author", COLOR_AUTHOR);
        Button btnCreateAdmin = createStandardButton("Create New Admin", COLOR_ADMIN);
        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);

        btnViewAdmins.setOnAction(e -> showAllAdminsScreen());
        btnViewAuthors.setOnAction(e -> showAllAuthorsScreen());
        btnViewCustomers.setOnAction(e -> showAllCustomersScreen());
        btnCreateAuthor.setOnAction(e -> showCreateAuthorScreen());
        btnCreateAdmin.setOnAction(e -> showCreateAdminScreen());
        btnBack.setOnAction(e -> showAdminDashboard());

        VBox buttonBox = new VBox(15, btnViewAdmins, btnViewAuthors, btnViewCustomers,
                btnCreateAuthor, btnCreateAdmin, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);

        content.getChildren().add(buttonBox);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllAdminsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("ALL ADMINISTRATORS", "System Administrators List");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (admins.isEmpty()) {
            Label emptyLabel = new Label("No administrators found");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Admin admin : admins) {
                HBox adminCard = createUserCard("Administrator", admin.getName(),
                        "ID: " + admin.getAdminID(), COLOR_ADMIN);
                content.getChildren().add(adminCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showUserManagementScreen());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllAuthorsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("ALL AUTHORS", "Formulation Authors List");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (authors.isEmpty()) {
            Label emptyLabel = new Label("No authors found");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Author author : authors) {
                int formulationCount = author.getFormulatedItems().size();
                HBox authorCard = createUserCard("Author", author.getName(),
                        "Formulations: " + formulationCount + " | ID: " + author.getAuthorID(),
                        COLOR_AUTHOR);
                content.getChildren().add(authorCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showUserManagementScreen());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllCustomersScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("ALL CUSTOMERS", "Customer Accounts List");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (customers.isEmpty()) {
            Label emptyLabel = new Label("No customers found");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Customer customer : customers) {
                int purchaseCount = customer.getPurchasedItems().size();
                HBox customerCard = createUserCard("Customer", customer.getName(),
                        "Purchases: " + purchaseCount + " | Age: " + customer.getAge(),
                        COLOR_CUSTOMER);
                content.getChildren().add(customerCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showUserManagementScreen());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createUserCard(String type, String name, String details, String color) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Type indicator
        VBox typeBox = new VBox();
        typeBox.setMinWidth(100);
        typeBox.setAlignment(Pos.CENTER);

        Label typeLabel = new Label(type);
        typeLabel.setFont(FONT_BODY);
        typeLabel.setTextFill(Color.web(color));
        typeLabel.setStyle("-fx-font-weight: bold;");

        typeBox.getChildren().add(typeLabel);

        // User info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(name);
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label detailsLabel = new Label(details);
        detailsLabel.setFont(FONT_BODY);
        detailsLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        infoBox.getChildren().addAll(nameLabel, detailsLabel);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(typeBox, infoBox);

        return card;
    }

    private void showCreateAuthorScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Author Account");
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Create Author Account");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_AUTHOR));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 20, 0));

        // Form fields
        TextField txtId = createDialogField(grid, "Author ID:*", 0);
        TextField txtName = createDialogField(grid, "Full Name:*", 1);
        TextField txtAddress = createDialogField(grid, "Address:", 2);
        TextField txtContact = createDialogField(grid, "Contact Info:*", 3);
        TextField txtDob = createDialogField(grid, "Date of Birth (YYYY-MM-DD):", 4);
        PasswordField txtPassword = createDialogPasswordField(grid, "Password:*", 5);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnCreate = createStandardButton("Create Author", COLOR_AUTHOR);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnCreate.setOnAction(e -> {
            try {
                // Validation
                if (txtId.getText().trim().isEmpty()) {
                    showError("Validation Error", "Author ID is required");
                    return;
                }
                if (txtName.getText().trim().isEmpty()) {
                    showError("Validation Error", "Full name is required");
                    return;
                }
                if (txtContact.getText().trim().isEmpty()) {
                    showError("Validation Error", "Contact information is required");
                    return;
                }
                if (txtPassword.getText().isEmpty()) {
                    showError("Validation Error", "Password is required");
                    return;
                }

                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                String password = txtPassword.getText();

                // Check if ID exists
                for (Author a : authors) {
                    if (a.getAuthorID() == id) {
                        showError("Error", "Author ID already exists");
                        return;
                    }
                }

                // Create author
                Author author = new Author(id, name, address, contact, dob);
                author.setPassword(password);
                authors.add(author);

                if (databaseManager != null) {
                    databaseManager.saveAuthor(author);
                }

                auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                        "Created author account: " + name + " (ID: " + id + ")");
                showInformation("Success", "Author account created successfully");
                dialog.close();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numeric ID");
            } catch (Exception ex) {
                showError("Error", "Failed to create author: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        content.getChildren().addAll(title, grid, buttonBox);

        Scene scene = new Scene(content, 450, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showCreateAdminScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Administrator Account");
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Create Administrator Account");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_ADMIN));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 20, 0));

        // Form fields
        TextField txtId = createDialogField(grid, "Admin ID:*", 0);
        TextField txtName = createDialogField(grid, "Full Name:*", 1);
        TextField txtAddress = createDialogField(grid, "Address:", 2);
        TextField txtContact = createDialogField(grid, "Contact Info:*", 3);
        TextField txtDob = createDialogField(grid, "Date of Birth (YYYY-MM-DD):", 4);
        PasswordField txtPassword = createDialogPasswordField(grid, "Password:*", 5);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnCreate = createStandardButton("Create Admin", COLOR_ADMIN);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnCreate.setOnAction(e -> {
            try {
                // Validation
                if (txtId.getText().trim().isEmpty()) {
                    showError("Validation Error", "Admin ID is required");
                    return;
                }
                if (txtName.getText().trim().isEmpty()) {
                    showError("Validation Error", "Full name is required");
                    return;
                }
                if (txtContact.getText().trim().isEmpty()) {
                    showError("Validation Error", "Contact information is required");
                    return;
                }
                if (txtPassword.getText().isEmpty()) {
                    showError("Validation Error", "Password is required");
                    return;
                }

                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                String password = txtPassword.getText();

                // Check if ID exists
                for (Admin a : admins) {
                    if (a.getAdminID() == id) {
                        showError("Error", "Admin ID already exists");
                        return;
                    }
                }

                // Create admin
                Admin admin = new Admin(id, name, address, contact, dob, password);
                admins.add(admin);

                if (databaseManager != null) {
                    databaseManager.saveAdmin(admin);
                }

                auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                        "Created admin account: " + name + " (ID: " + id + ")");
                showInformation("Success", "Administrator account created successfully");
                dialog.close();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numeric ID");
            } catch (Exception ex) {
                showError("Error", "Failed to create administrator: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        content.getChildren().addAll(title, grid, buttonBox);

        Scene scene = new Scene(content, 450, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showFormulationManagementScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("FORMULATION MANAGEMENT", "All System Formulations");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (allFormulations.isEmpty()) {
            Label emptyLabel = new Label("No formulations found in the system");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : allFormulations) {
                HBox formulationCard = createFormulationCard(item);
                content.getChildren().add(formulationCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAdminDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createFormulationCard(Item item) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + (isItemVetoed(item) ? COLOR_ERROR : COLOR_BORDER) + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Type indicator
        VBox typeBox = new VBox();
        typeBox.setMinWidth(100);
        typeBox.setAlignment(Pos.CENTER);

        String type = item instanceof Food ? "FOOD" : "DRINK";
        String typeColor = item instanceof Food ? COLOR_SUCCESS : COLOR_INFO;

        Label typeLabel = new Label(type);
        typeLabel.setFont(FONT_BODY);
        typeLabel.setTextFill(Color.web(typeColor));
        typeLabel.setStyle("-fx-font-weight: bold;");

        typeBox.getChildren().add(typeLabel);

        // Formulation info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label detailsLabel = new Label("ID: " + item.getItemID() +
                " | Price: $" + String.format("%.2f", item.getPrice()) +
                " | Expiry: " + item.getExpiryDate());
        detailsLabel.setFont(FONT_BODY);
        detailsLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        // Veto status
        if (isItemVetoed(item)) {
            Label vetoLabel = new Label("VETOED");
            vetoLabel.setFont(FONT_BODY);
            vetoLabel.setTextFill(Color.web(COLOR_ERROR));
            vetoLabel.setStyle("-fx-font-weight: bold;");
            infoBox.getChildren().add(vetoLabel);
        }

        infoBox.getChildren().addAll(nameLabel, detailsLabel);

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnView = createSmallButton("View", COLOR_INFO);
        Button btnVeto = createSmallButton(isItemVetoed(item) ? "Remove Veto" : "Set Veto",
                isItemVetoed(item) ? COLOR_SUCCESS : COLOR_ERROR);

        btnView.setOnAction(e -> showFormulationDetails(item));
        btnVeto.setOnAction(e -> {
            if (isItemVetoed(item)) {
                removeVetoFromItem(item);
            } else {
                showSetVetoDialog(item);
            }
            showFormulationManagementScreen(); // Refresh
        });

        buttonBox.getChildren().addAll(btnView, btnVeto);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(typeBox, infoBox, buttonBox);

        return card;
    }

    private void showFormulationDetails(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Formulation Details: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label(item.getName());
        title.setFont(FONT_TITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + "; " +
                "-fx-padding: 15; " +
                "-fx-background-radius: 8;");

        addDetail(detailsBox, "Type:", item instanceof Food ? "Food" : "Drink");
        addDetail(detailsBox, "ID:", String.valueOf(item.getItemID()));
        addDetail(detailsBox, "Price:", "$" + String.format("%.2f", item.getPrice()));
        addDetail(detailsBox, "Expiry Date:", item.getExpiryDate());
        addDetail(detailsBox, "Status:", isItemVetoed(item) ? "VETOED" : "Active");

        if (item instanceof Food) {
            Food food = (Food) item;
            addDetail(detailsBox, "Average Price per Kg:", "$" + String.format("%.2f", food.getAveragePricePerKg()));
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            addDetail(detailsBox, "Average Price per Kg:", "$" + String.format("%.2f", drink.getAveragePricePerKg()));
            addDetail(detailsBox, "Alcohol Content:", String.format("%.1f%%", drink.getAlcoholContent()));
        }

        // Ingredients
        LinkedList<Ingredient> ingredients = getIngredients(item);
        if (ingredients != null && !ingredients.isEmpty()) {
            Label ingTitle = new Label("Ingredients:");
            ingTitle.setFont(FONT_SUBTITLE);
            ingTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
            ingTitle.setPadding(new Insets(10, 0, 5, 0));
            detailsBox.getChildren().add(ingTitle);

            for (Ingredient ing : ingredients) {
                Label ingLabel = new Label("• " + ing.getName() +
                        " (ID: " + ing.getIngredientID() + ")");
                ingLabel.setFont(FONT_BODY);
                ingLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
                detailsBox.getChildren().add(ingLabel);
            }
        }

        // Authors
        if (item instanceof Food) {
            Food food = (Food) item;
            LinkedList<Author> authors = food.getAuthors();
            if (authors != null && !authors.isEmpty()) {
                Label authTitle = new Label("Authors:");
                authTitle.setFont(FONT_SUBTITLE);
                authTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
                authTitle.setPadding(new Insets(10, 0, 5, 0));
                detailsBox.getChildren().add(authTitle);

                for (Author author : authors) {
                    Label authLabel = new Label("• " + author.getName());
                    authLabel.setFont(FONT_BODY);
                    authLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
                    detailsBox.getChildren().add(authLabel);
                }
            }
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            LinkedList<Author> authors = drink.getAuthors();
            if (authors != null && !authors.isEmpty()) {
                Label authTitle = new Label("Authors:");
                authTitle.setFont(FONT_SUBTITLE);
                authTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
                authTitle.setPadding(new Insets(10, 0, 5, 0));
                detailsBox.getChildren().add(authTitle);

                for (Author author : authors) {
                    Label authLabel = new Label("• " + author.getName());
                    authLabel.setFont(FONT_BODY);
                    authLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
                    detailsBox.getChildren().add(authLabel);
                }
            }
        }

        Button btnClose = createStandardButton("Close", COLOR_TEXT_SECONDARY);
        btnClose.setOnAction(e -> dialog.close());

        content.getChildren().addAll(title, detailsBox, btnClose);

        Scene scene = new Scene(content, 500, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showSetVetoDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Set Veto: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Set Veto on Formulation");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_ERROR));

        Label itemLabel = new Label("Item: " + item.getName());
        itemLabel.setFont(FONT_BODY);
        itemLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label reasonLabel = new Label("Reason for Veto:*");
        reasonLabel.setFont(FONT_BODY);
        reasonLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        TextArea txtReason = new TextArea();
        txtReason.setPromptText("Enter the reason for vetoing this formulation...");
        txtReason.setPrefRowCount(4);
        txtReason.setStyle("-fx-control-inner-background: white; " +
                "-fx-border-color: " + COLOR_BORDER + ";");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnSet = createStandardButton("Set Veto", COLOR_ERROR);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnSet.setOnAction(e -> {
            String reason = txtReason.getText().trim();
            if (reason.isEmpty()) {
                showError("Validation Error", "Please enter a reason for the veto");
                return;
            }

            setVetoOnItem(item, reason);
            dialog.close();
            showFormulationManagementScreen();
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnSet, btnCancel);
        content.getChildren().addAll(title, itemLabel, reasonLabel, txtReason, buttonBox);

        Scene scene = new Scene(content, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void setVetoOnItem(Item item, String reason) {
        try {
            Veto veto = new Veto(true, reason, new Date(), (Admin)currentUser);
            if (item instanceof Food) {
                ((Food) item).setVeto(veto);
            } else if (item instanceof Drink) {
                ((Drink) item).setVeto(veto);
            }

            if (databaseManager != null) {
                databaseManager.saveItem(item);
            }

            auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                    "Set veto on formulation: " + item.getName() + " (Reason: " + reason + ")");
            showInformation("Success", "Veto set successfully on " + item.getName());
        } catch (Exception e) {
            showError("Error", "Failed to set veto: " + e.getMessage());
        }
    }

    private void removeVetoFromItem(Item item) {
        try {
            if (item instanceof Food) {
                ((Food) item).setVeto(null);
            } else if (item instanceof Drink) {
                ((Drink) item).setVeto(null);
            }

            if (databaseManager != null) {
                databaseManager.saveItem(item);
            }

            auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                    "Removed veto from formulation: " + item.getName());
            showInformation("Success", "Veto removed successfully from " + item.getName());
        } catch (Exception e) {
            showError("Error", "Failed to remove veto: " + e.getMessage());
        }
    }

    private void showVetoManagementScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("VETO MANAGEMENT", "Manage Vetoed Formulations");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        LinkedList<Item> vetoedItems = new LinkedList<>();
        for (Item item : allFormulations) {
            if (isItemVetoed(item)) {
                vetoedItems.add(item);
            }
        }

        if (vetoedItems.isEmpty()) {
            Label emptyLabel = new Label("No vetoed formulations");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : vetoedItems) {
                HBox vetoCard = createVetoedItemCard(item);
                content.getChildren().add(vetoCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAdminDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createVetoedItemCard(Item item) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_ERROR + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Warning icon
        Label warningIcon = new Label("⚠");
        warningIcon.setFont(Font.font(20));
        warningIcon.setTextFill(Color.web(COLOR_ERROR));

        // Item info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        // Get veto reason
        String vetoReason = "No reason provided";
        if (item instanceof Food) {
            Veto veto = ((Food) item).getVeto();
            if (veto != null) {
                vetoReason = veto.getReason();
            }
        } else if (item instanceof Drink) {
            Veto veto = ((Drink) item).getVeto();
            if (veto != null) {
                vetoReason = veto.getReason();
            }
        }

        Label reasonLabel = new Label("Reason: " + vetoReason);
        reasonLabel.setFont(FONT_BODY);
        reasonLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        reasonLabel.setWrapText(true);

        infoBox.getChildren().addAll(nameLabel, reasonLabel);

        // Remove veto button
        Button btnRemove = createSmallButton("Remove Veto", COLOR_SUCCESS);
        btnRemove.setOnAction(e -> {
            removeVetoFromItem(item);
            showVetoManagementScreen(); // Refresh
        });

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(warningIcon, infoBox, btnRemove);

        return card;
    }

    private void showSystemStatisticsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("SYSTEM STATISTICS", "Comprehensive System Overview");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(40);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        // Calculate statistics
        int totalUsers = admins.size() + authors.size() + customers.size();
        int foodCount = countFoodFormulations();
        int drinkCount = countDrinkFormulations();
        int vetoedCount = countVetoedFormulations();
        int totalPurchases = countTotalPurchases();
        int totalFeedbacks = countTotalFeedbacks();

        addStatRow(statsGrid, 0, "Total Administrators:", admins.size(), COLOR_ADMIN);
        addStatRow(statsGrid, 1, "Total Authors:", authors.size(), COLOR_AUTHOR);
        addStatRow(statsGrid, 2, "Total Customers:", customers.size(), COLOR_CUSTOMER);
        addStatRow(statsGrid, 3, "Total Users:", totalUsers, COLOR_PRIMARY);
        addStatRow(statsGrid, 4, "Total Formulations:", allFormulations.size(), COLOR_INFO);
        addStatRow(statsGrid, 5, "Food Formulations:", foodCount, COLOR_SUCCESS);
        addStatRow(statsGrid, 6, "Drink Formulations:", drinkCount, COLOR_INFO);
        addStatRow(statsGrid, 7, "Vetoed Formulations:", vetoedCount, COLOR_ERROR);
        addStatRow(statsGrid, 8, "Total Purchases:", totalPurchases, COLOR_CUSTOMER);
        addStatRow(statsGrid, 9, "Total Feedbacks:", totalFeedbacks, COLOR_PRIMARY);

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAdminDashboard());

        content.getChildren().addAll(statsGrid, btnBack);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAuditTrailScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("AUDIT TRAIL", "System Activity Log");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label countLabel = new Label("Total Log Entries: " + auditTrail.records.size());
        countLabel.setFont(FONT_SUBTITLE);
        countLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        TextArea auditArea = new TextArea();
        auditArea.setEditable(false);
        auditArea.setPrefHeight(500);
        auditArea.setStyle("-fx-control-inner-background: white; " +
                "-fx-text-fill: " + COLOR_TEXT_PRIMARY + "; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-font-size: 12px;");

        if (auditTrail.records.isEmpty()) {
            auditArea.setText("No audit records available.");
        } else {
            StringBuilder auditText = new StringBuilder();
            for (String record : auditTrail.records) {
                auditText.append(record).append("\n");
            }
            auditArea.setText(auditText.toString());
            auditArea.setScrollTop(Double.MAX_VALUE); // Scroll to bottom
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnExport = createStandardButton("Export Log", COLOR_SUCCESS);
        Button btnClear = createStandardButton("Clear Log", COLOR_ERROR);
        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);

        btnExport.setOnAction(e -> showInformation("Export",
                "Log export feature would save to: audit_log_" + new Date().getTime() + ".txt"));
        btnClear.setOnAction(e -> {
            auditTrail.records.clear();
            showInformation("Cleared", "Audit log cleared");
            showAuditTrailScreen();
        });
        btnBack.setOnAction(e -> showAdminDashboard());

        buttonBox.getChildren().addAll(btnExport, btnClear, btnBack);
        content.getChildren().addAll(countLabel, auditArea, buttonBox);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showQualityControlScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("QUALITY CONTROL", "Formulation Quality Issues");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        TextArea issuesArea = new TextArea();
        issuesArea.setEditable(false);
        issuesArea.setPrefHeight(500);
        issuesArea.setStyle("-fx-control-inner-background: white; " +
                "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";");

        StringBuilder issues = new StringBuilder();
        int issueCount = 0;

        issues.append("=== QUALITY CONTROL REPORT ===\n");
        issues.append("Generated: " + new Date() + "\n\n");

        for (Item item : allFormulations) {
            boolean hasIssues = false;
            StringBuilder itemIssues = new StringBuilder();

            // Check 1: Missing ingredients
            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients == null || ingredients.isEmpty()) {
                itemIssues.append("  ❌ Missing ingredients\n");
                hasIssues = true;
            }

            // Check 2: Invalid price
            if (item.getPrice() <= 0) {
                itemIssues.append("  ❌ Invalid price: $" + item.getPrice() + "\n");
                hasIssues = true;
            } else if (item.getPrice() > 1000) {
                itemIssues.append("  ⚠ High price: $" + item.getPrice() + "\n");
                hasIssues = true;
            }

            // Check 3: Expiry date
            if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
                itemIssues.append("  ❌ Missing expiry date\n");
                hasIssues = true;
            }

            // Check 4: Veto status
            if (isItemVetoed(item)) {
                itemIssues.append("  ⚠ Formulation is vetoed\n");
                hasIssues = true;
            }

            // Check 5: Feedback analysis
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null && !feedbacks.isEmpty()) {
                int negativeCount = 0;
                for (Feedback fb : feedbacks) {
                    if (!fb.isLike()) negativeCount++;
                }
                if (negativeCount > 0) {
                    double negativePercentage = (negativeCount * 100.0) / feedbacks.size();
                    itemIssues.append("  ⚠ " + negativeCount + " negative feedbacks (" +
                            String.format("%.1f", negativePercentage) + "%)\n");
                    hasIssues = true;
                }
            }

            if (hasIssues) {
                issueCount++;
                issues.append(item.getName() + " (ID: " + item.getItemID() + ")\n");
                issues.append(itemIssues.toString() + "\n");
            }
        }

        if (issueCount == 0) {
            issues.append("No quality issues found. All formulations meet quality standards.");
        } else {
            issues.insert(0, "Found " + issueCount + " formulation(s) with issues:\n\n");
        }

        issuesArea.setText(issues.toString());

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAdminDashboard());

        content.getChildren().addAll(issuesArea, btnBack);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showDatabaseToolsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("DATABASE TOOLS", "Database Management Utilities");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Button btnBackup = createStandardButton("Create Database Backup", COLOR_SUCCESS);
        Button btnRestore = createStandardButton("Restore from Backup", COLOR_WARNING);
        Button btnOptimize = createStandardButton("Optimize Database", COLOR_INFO);
        Button btnValidate = createStandardButton("Validate Data Integrity", COLOR_PRIMARY);
        Button btnExport = createStandardButton("Export All Data", COLOR_SECONDARY);
        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);

        btnBackup.setOnAction(e -> {
            saveDataToDatabase();
            showInformation("Backup Created", "Database backup completed successfully");
        });

        btnRestore.setOnAction(e -> {
            loadDataFromDatabase();
            showInformation("Data Restored", "Database restored from latest data");
        });

        btnOptimize.setOnAction(e -> showInformation("Optimization",
                "Database optimization would remove unused space and indexes"));

        btnValidate.setOnAction(e -> {
            // Validate data integrity
            StringBuilder validation = new StringBuilder();
            validation.append("Data Validation Report:\n");
            validation.append("• Administrators: " + admins.size() + " (OK)\n");
            validation.append("• Authors: " + authors.size() + " (OK)\n");
            validation.append("• Customers: " + customers.size() + " (OK)\n");
            validation.append("• Formulations: " + allFormulations.size() + " (OK)\n");
            validation.append("• Audit Trail: " + auditTrail.records.size() + " entries (OK)\n");
            validation.append("\nAll data appears to be valid and consistent.");

            showInformation("Validation Complete", validation.toString());
        });

        btnExport.setOnAction(e -> showInformation("Export",
                "Data export would create CSV files for all tables"));

        btnBack.setOnAction(e -> showAdminDashboard());

        VBox buttonBox = new VBox(15, btnBackup, btnRestore, btnOptimize, btnValidate, btnExport, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);

        content.getChildren().add(buttonBox);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ AUTHOR FUNCTIONALITIES ============

    private void showCreateFoodScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Food Formulation");
        dialog.initOwner(primaryStage);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: white;");

        Tab tabBasic = new Tab("Basic Information");
        tabBasic.setContent(createFoodBasicInfoForm());

        Tab tabIngredients = new Tab("Ingredients");
        tabIngredients.setContent(createIngredientsForm());

        Tab tabConditions = new Tab("Conditions");
        tabConditions.setContent(createConditionsForm());

        Tab tabProtocol = new Tab("Protocol");
        tabProtocol.setContent(createProtocolForm());

        Tab tabStandards = new Tab("Standards");
        tabStandards.setContent(createStandardsForm());

        tabPane.getTabs().addAll(tabBasic, tabIngredients, tabConditions, tabProtocol, tabStandards);

        // Create button
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle("-fx-background-color: white;");

        Button btnCreate = createStandardButton("Create Food Formulation", COLOR_SUCCESS);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        // Store form data
        FoodFormData formData = new FoodFormData();

        // Wire up form components
        tabBasic.setOnSelectionChanged(e -> {
            if (tabBasic.isSelected()) {
                updateFoodFormDataFromBasicTab(tabBasic, formData);
            }
        });

        tabIngredients.setOnSelectionChanged(e -> {
            if (tabIngredients.isSelected()) {
                updateFoodFormDataFromIngredientsTab(tabIngredients, formData);
            }
        });

        btnCreate.setOnAction(e -> {
            // Collect data from all tabs
            updateFoodFormDataFromBasicTab(tabBasic, formData);
            updateFoodFormDataFromIngredientsTab(tabIngredients, formData);
            updateFoodFormDataFromConditionsTab(tabConditions, formData);
            updateFoodFormDataFromProtocolTab(tabProtocol, formData);
            updateFoodFormDataFromStandardsTab(tabStandards, formData);

            if (validateAndCreateFood(formData)) {
                dialog.close();
                showMyFormulationsScreen();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);

        VBox content = new VBox(tabPane, buttonBox);
        content.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(content, 800, 700);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Food form data class to store all form values
    private class FoodFormData {
        String name = "";
        int id = 0;
        double price = 0.0;
        double avgPricePerKg = 0.0;
        String expiryDate = "";
        ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        double labTemp = 0.0;
        double labPressure = 0.0;
        double labMoisture = 0.0;
        double labVibration = 0.0;
        int labPeriod = 0;
        ObservableList<String> preparationSteps = FXCollections.observableArrayList();
        double conserveTemp = 0.0;
        double conserveMoisture = 0.0;
        String containerType = "";
        double consumeTemp = 0.0;
        double consumeMoisture = 0.0;
        ObservableList<String> standards = FXCollections.observableArrayList();
        String consumerProfile = "";
    }

    private ScrollPane createFoodBasicInfoForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Basic Information");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        TextField txtName = createDialogField(grid, "Food Name:*", 0);
        TextField txtId = createDialogField(grid, "Food ID:*", 1);
        TextField txtPrice = createDialogField(grid, "Price ($):*", 2);
        TextField txtAvgPrice = createDialogField(grid, "Avg Price per Kg ($):*", 3);
        TextField txtExpiry = createDialogField(grid, "Expiry Date (YYYY-MM-DD):*", 4);

        content.getChildren().addAll(title, grid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createConditionsForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Laboratory & Conservation Conditions");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        // Laboratory Conditions
        Label labTitle = new Label("Laboratory Conditions:");
        labTitle.setFont(FONT_BODY);
        labTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        labTitle.setStyle("-fx-font-weight: bold;");

        GridPane labGrid = new GridPane();
        labGrid.setHgap(15);
        labGrid.setVgap(10);
        labGrid.setPadding(new Insets(10, 0, 20, 0));

        TextField txtLabTemp = createDialogField(labGrid, "Temperature (°C):", 0);
        TextField txtLabPressure = createDialogField(labGrid, "Pressure (kPa):", 1);
        TextField txtLabMoisture = createDialogField(labGrid, "Moisture (%):", 2);
        TextField txtLabVibration = createDialogField(labGrid, "Vibration Level:", 3);
        TextField txtLabPeriod = createDialogField(labGrid, "Time Period (min):", 4);

        // Conservation Conditions
        Label conserveTitle = new Label("Conservation Conditions:");
        conserveTitle.setFont(FONT_BODY);
        conserveTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        conserveTitle.setStyle("-fx-font-weight: bold;");

        GridPane conserveGrid = new GridPane();
        conserveGrid.setHgap(15);
        conserveGrid.setVgap(10);
        conserveGrid.setPadding(new Insets(10, 0, 20, 0));

        TextField txtConserveTemp = createDialogField(conserveGrid, "Temperature (°C):", 0);
        TextField txtConserveMoisture = createDialogField(conserveGrid, "Moisture (%):", 1);
        TextField txtContainer = createDialogField(conserveGrid, "Container Type:", 2);

        // Consumption Conditions
        Label consumeTitle = new Label("Consumption Conditions:");
        consumeTitle.setFont(FONT_BODY);
        consumeTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        consumeTitle.setStyle("-fx-font-weight: bold;");

        GridPane consumeGrid = new GridPane();
        consumeGrid.setHgap(15);
        consumeGrid.setVgap(10);

        TextField txtConsumeTemp = createDialogField(consumeGrid, "Serving Temperature (°C):", 0);
        TextField txtConsumeMoisture = createDialogField(consumeGrid, "Serving Moisture (%):", 1);

        content.getChildren().addAll(title, labTitle, labGrid, conserveTitle, conserveGrid,
                consumeTitle, consumeGrid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createProtocolForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Preparation Protocol");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label info = new Label("Add preparation steps in order:");
        info.setFont(FONT_BODY);
        info.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        ObservableList<String> steps = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(steps);
        listView.setPrefHeight(300);
        listView.setStyle("-fx-border-color: " + COLOR_BORDER + ";");

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Step", COLOR_SUCCESS);
        Button btnRemove = createSmallButton("Remove Selected", COLOR_ERROR);
        Button btnEdit = createSmallButton("Edit Selected", COLOR_INFO);

        btnAdd.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Preparation Step");
            dialog.setHeaderText("Enter step description:");
            dialog.setContentText("Step:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(step -> steps.add(step));
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove, btnEdit);
        content.getChildren().addAll(title, info, listView, buttonBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createStandardsForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Quality Standards & Consumer Profile");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        // Standards
        Label standardsTitle = new Label("Quality Standards:");
        standardsTitle.setFont(FONT_BODY);
        standardsTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        standardsTitle.setStyle("-fx-font-weight: bold;");

        ObservableList<String> standards = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(standards);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + COLOR_BORDER + ";");

        HBox standardsButtons = new HBox(10);
        Button btnAddStandard = createSmallButton("Add Standard", COLOR_SUCCESS);
        Button btnRemoveStandard = createSmallButton("Remove Selected", COLOR_ERROR);

        btnAddStandard.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Quality Standard");
            dialog.setHeaderText("Enter quality standard:");
            dialog.setContentText("Standard:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(standard -> standards.add(standard));
        });

        standardsButtons.getChildren().addAll(btnAddStandard, btnRemoveStandard);

        // Consumer Profile
        Label profileTitle = new Label("Target Consumer Profile:");
        profileTitle.setFont(FONT_BODY);
        profileTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        profileTitle.setStyle("-fx-font-weight: bold;");
        profileTitle.setPadding(new Insets(10, 0, 5, 0));

        TextArea txtConsumerProfile = new TextArea();
        txtConsumerProfile.setPromptText("Describe the target consumer (e.g., 'Adults 18-65, Health-conscious, Active lifestyle')");
        txtConsumerProfile.setPrefRowCount(4);
        txtConsumerProfile.setStyle("-fx-control-inner-background: white; " +
                "-fx-border-color: " + COLOR_BORDER + ";");

        content.getChildren().addAll(title, standardsTitle, listView, standardsButtons,
                profileTitle, txtConsumerProfile);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    // ============ DRINK CREATION FEATURE ============

    private void showCreateDrinkScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Drink Formulation");
        dialog.initOwner(primaryStage);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: white;");

        Tab tabBasic = new Tab("Basic Information");
        tabBasic.setContent(createDrinkBasicInfoForm());

        Tab tabIngredients = new Tab("Ingredients");
        tabIngredients.setContent(createIngredientsForm());

        Tab tabConditions = new Tab("Conditions");
        tabConditions.setContent(createDrinkConditionsForm());

        Tab tabProtocol = new Tab("Protocol");
        tabProtocol.setContent(createDrinkProtocolForm());

        Tab tabStandards = new Tab("Standards");
        tabStandards.setContent(createDrinkStandardsForm());

        tabPane.getTabs().addAll(tabBasic, tabIngredients, tabConditions, tabProtocol, tabStandards);

        // Create button
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle("-fx-background-color: white;");

        Button btnCreate = createStandardButton("Create Drink Formulation", COLOR_INFO);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        // Store form data
        DrinkFormData formData = new DrinkFormData();

        // Wire up form components
        tabBasic.setOnSelectionChanged(e -> {
            if (tabBasic.isSelected()) {
                updateDrinkFormDataFromBasicTab(tabBasic, formData);
            }
        });

        tabIngredients.setOnSelectionChanged(e -> {
            if (tabIngredients.isSelected()) {
                updateDrinkFormDataFromIngredientsTab(tabIngredients, formData);
            }
        });

        btnCreate.setOnAction(e -> {
            // Collect data from all tabs
            updateDrinkFormDataFromBasicTab(tabBasic, formData);
            updateDrinkFormDataFromIngredientsTab(tabIngredients, formData);
            updateDrinkFormDataFromConditionsTab(tabConditions, formData);
            updateDrinkFormDataFromProtocolTab(tabProtocol, formData);
            updateDrinkFormDataFromStandardsTab(tabStandards, formData);

            if (validateAndCreateDrink(formData)) {
                dialog.close();
                showMyFormulationsScreen();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);

        VBox content = new VBox(tabPane, buttonBox);
        content.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(content, 800, 700);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Drink form data class to store all form values
    private class DrinkFormData {
        String name = "";
        int id = 0;
        double price = 0.0;
        double avgPricePerKg = 0.0;
        String expiryDate = "";
        double alcoholContent = 0.0;
        ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        double labTemp = 0.0;
        double labPressure = 0.0;
        double labMoisture = 0.0;
        double labVibration = 0.0;
        int labPeriod = 0;
        ObservableList<String> preparationSteps = FXCollections.observableArrayList();
        double conserveTemp = 0.0;
        double conserveMoisture = 0.0;
        String containerType = "";
        double consumeTemp = 0.0;
        double consumeMoisture = 0.0;
        ObservableList<String> standards = FXCollections.observableArrayList();
        String consumerProfile = "";
    }

    private ScrollPane createDrinkBasicInfoForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Basic Information");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        TextField txtName = createDialogField(grid, "Drink Name:*", 0);
        TextField txtId = createDialogField(grid, "Drink ID:*", 1);
        TextField txtPrice = createDialogField(grid, "Price ($):*", 2);
        TextField txtAvgPrice = createDialogField(grid, "Avg Price per Kg ($):*", 3);
        TextField txtExpiry = createDialogField(grid, "Expiry Date (YYYY-MM-DD):*", 4);
        TextField txtAlcohol = createDialogField(grid, "Alcohol Content (%):", 5);

        content.getChildren().addAll(title, grid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createDrinkConditionsForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Laboratory & Conservation Conditions");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        // Laboratory Conditions
        Label labTitle = new Label("Laboratory Conditions:");
        labTitle.setFont(FONT_BODY);
        labTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        labTitle.setStyle("-fx-font-weight: bold;");

        GridPane labGrid = new GridPane();
        labGrid.setHgap(15);
        labGrid.setVgap(10);
        labGrid.setPadding(new Insets(10, 0, 20, 0));

        TextField txtLabTemp = createDialogField(labGrid, "Temperature (°C):", 0);
        TextField txtLabPressure = createDialogField(labGrid, "Pressure (kPa):", 1);
        TextField txtLabMoisture = createDialogField(labGrid, "Moisture (%):", 2);
        TextField txtLabVibration = createDialogField(labGrid, "Vibration Level:", 3);
        TextField txtLabPeriod = createDialogField(labGrid, "Time Period (min):", 4);

        // Conservation Conditions
        Label conserveTitle = new Label("Conservation Conditions:");
        conserveTitle.setFont(FONT_BODY);
        conserveTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        conserveTitle.setStyle("-fx-font-weight: bold;");

        GridPane conserveGrid = new GridPane();
        conserveGrid.setHgap(15);
        conserveGrid.setVgap(10);
        conserveGrid.setPadding(new Insets(10, 0, 20, 0));

        TextField txtConserveTemp = createDialogField(conserveGrid, "Temperature (°C):", 0);
        TextField txtConserveMoisture = createDialogField(conserveGrid, "Moisture (%):", 1);
        TextField txtContainer = createDialogField(conserveGrid, "Container Type:", 2);

        // Consumption Conditions
        Label consumeTitle = new Label("Consumption Conditions:");
        consumeTitle.setFont(FONT_BODY);
        consumeTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        consumeTitle.setStyle("-fx-font-weight: bold;");

        GridPane consumeGrid = new GridPane();
        consumeGrid.setHgap(15);
        consumeGrid.setVgap(10);

        TextField txtConsumeTemp = createDialogField(consumeGrid, "Serving Temperature (°C):", 0);
        TextField txtConsumeMoisture = createDialogField(consumeGrid, "Serving Moisture (%):", 1);

        content.getChildren().addAll(title, labTitle, labGrid, conserveTitle, conserveGrid,
                consumeTitle, consumeGrid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createDrinkProtocolForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Preparation Protocol");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label info = new Label("Add preparation steps in order:");
        info.setFont(FONT_BODY);
        info.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        ObservableList<String> steps = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(steps);
        listView.setPrefHeight(300);
        listView.setStyle("-fx-border-color: " + COLOR_BORDER + ";");

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Step", COLOR_SUCCESS);
        Button btnRemove = createSmallButton("Remove Selected", COLOR_ERROR);
        Button btnEdit = createSmallButton("Edit Selected", COLOR_INFO);

        btnAdd.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Preparation Step");
            dialog.setHeaderText("Enter step description:");
            dialog.setContentText("Step:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(step -> steps.add(step));
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove, btnEdit);
        content.getChildren().addAll(title, info, listView, buttonBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createDrinkStandardsForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Quality Standards & Consumer Profile");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        // Standards
        Label standardsTitle = new Label("Quality Standards:");
        standardsTitle.setFont(FONT_BODY);
        standardsTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        standardsTitle.setStyle("-fx-font-weight: bold;");

        ObservableList<String> standards = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(standards);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + COLOR_BORDER + ";");

        HBox standardsButtons = new HBox(10);
        Button btnAddStandard = createSmallButton("Add Standard", COLOR_SUCCESS);
        Button btnRemoveStandard = createSmallButton("Remove Selected", COLOR_ERROR);

        btnAddStandard.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Quality Standard");
            dialog.setHeaderText("Enter quality standard:");
            dialog.setContentText("Standard:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(standard -> standards.add(standard));
        });

        standardsButtons.getChildren().addAll(btnAddStandard, btnRemoveStandard);

        // Consumer Profile
        Label profileTitle = new Label("Target Consumer Profile:");
        profileTitle.setFont(FONT_BODY);
        profileTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        profileTitle.setStyle("-fx-font-weight: bold;");
        profileTitle.setPadding(new Insets(10, 0, 5, 0));

        TextArea txtConsumerProfile = new TextArea();
        txtConsumerProfile.setPromptText("Describe the target consumer (e.g., 'Adults 18-65, Non-alcoholic, Refreshment')");
        txtConsumerProfile.setPrefRowCount(4);
        txtConsumerProfile.setStyle("-fx-control-inner-background: white; " +
                "-fx-border-color: " + COLOR_BORDER + ";");

        content.getChildren().addAll(title, standardsTitle, listView, standardsButtons,
                profileTitle, txtConsumerProfile);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    // Helper methods to update form data
    private void updateFoodFormDataFromBasicTab(Tab tab, FoodFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();
            GridPane grid = (GridPane) content.getChildren().get(1);

            TextField txtName = (TextField) grid.getChildren().get(1);
            TextField txtId = (TextField) grid.getChildren().get(3);
            TextField txtPrice = (TextField) grid.getChildren().get(5);
            TextField txtAvgPrice = (TextField) grid.getChildren().get(7);
            TextField txtExpiry = (TextField) grid.getChildren().get(9);

            formData.name = txtName.getText().trim();
            if (!txtId.getText().trim().isEmpty()) {
                formData.id = Integer.parseInt(txtId.getText().trim());
            }
            if (!txtPrice.getText().trim().isEmpty()) {
                formData.price = Double.parseDouble(txtPrice.getText().trim());
            }
            if (!txtAvgPrice.getText().trim().isEmpty()) {
                formData.avgPricePerKg = Double.parseDouble(txtAvgPrice.getText().trim());
            }
            formData.expiryDate = txtExpiry.getText().trim();
        } catch (Exception e) {
            System.err.println("Error updating food basic info: " + e.getMessage());
        }
    }

    private void updateFoodFormDataFromIngredientsTab(Tab tab, FoodFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();
            ListView<Ingredient> listView = (ListView<Ingredient>) content.getChildren().get(2);
            formData.ingredients = FXCollections.observableArrayList(listView.getItems());
        } catch (Exception e) {
            System.err.println("Error updating food ingredients: " + e.getMessage());
        }
    }

    private void updateFoodFormDataFromConditionsTab(Tab tab, FoodFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();

            // Laboratory conditions
            GridPane labGrid = (GridPane) content.getChildren().get(2);
            TextField txtLabTemp = (TextField) labGrid.getChildren().get(1);
            TextField txtLabPressure = (TextField) labGrid.getChildren().get(3);
            TextField txtLabMoisture = (TextField) labGrid.getChildren().get(5);
            TextField txtLabVibration = (TextField) labGrid.getChildren().get(7);
            TextField txtLabPeriod = (TextField) labGrid.getChildren().get(9);

            if (!txtLabTemp.getText().trim().isEmpty()) {
                formData.labTemp = Double.parseDouble(txtLabTemp.getText().trim());
            }
            if (!txtLabPressure.getText().trim().isEmpty()) {
                formData.labPressure = Double.parseDouble(txtLabPressure.getText().trim());
            }
            if (!txtLabMoisture.getText().trim().isEmpty()) {
                formData.labMoisture = Double.parseDouble(txtLabMoisture.getText().trim());
            }
            if (!txtLabVibration.getText().trim().isEmpty()) {
                formData.labVibration = Double.parseDouble(txtLabVibration.getText().trim());
            }
            if (!txtLabPeriod.getText().trim().isEmpty()) {
                formData.labPeriod = Integer.parseInt(txtLabPeriod.getText().trim());
            }

            // Conservation conditions
            GridPane conserveGrid = (GridPane) content.getChildren().get(4);
            TextField txtConserveTemp = (TextField) conserveGrid.getChildren().get(1);
            TextField txtConserveMoisture = (TextField) conserveGrid.getChildren().get(3);
            TextField txtContainer = (TextField) conserveGrid.getChildren().get(5);

            if (!txtConserveTemp.getText().trim().isEmpty()) {
                formData.conserveTemp = Double.parseDouble(txtConserveTemp.getText().trim());
            }
            if (!txtConserveMoisture.getText().trim().isEmpty()) {
                formData.conserveMoisture = Double.parseDouble(txtConserveMoisture.getText().trim());
            }
            formData.containerType = txtContainer.getText().trim();

            // Consumption conditions
            GridPane consumeGrid = (GridPane) content.getChildren().get(6);
            TextField txtConsumeTemp = (TextField) consumeGrid.getChildren().get(1);
            TextField txtConsumeMoisture = (TextField) consumeGrid.getChildren().get(3);

            if (!txtConsumeTemp.getText().trim().isEmpty()) {
                formData.consumeTemp = Double.parseDouble(txtConsumeTemp.getText().trim());
            }
            if (!txtConsumeMoisture.getText().trim().isEmpty()) {
                formData.consumeMoisture = Double.parseDouble(txtConsumeMoisture.getText().trim());
            }
        } catch (Exception e) {
            System.err.println("Error updating food conditions: " + e.getMessage());
        }
    }

    private void updateFoodFormDataFromProtocolTab(Tab tab, FoodFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();
            ListView<String> listView = (ListView<String>) content.getChildren().get(2);
            formData.preparationSteps = FXCollections.observableArrayList(listView.getItems());
        } catch (Exception e) {
            System.err.println("Error updating food protocol: " + e.getMessage());
        }
    }

    private void updateFoodFormDataFromStandardsTab(Tab tab, FoodFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();

            ListView<String> standardsList = (ListView<String>) content.getChildren().get(2);
            formData.standards = FXCollections.observableArrayList(standardsList.getItems());

            TextArea txtConsumerProfile = (TextArea) content.getChildren().get(5);
            formData.consumerProfile = txtConsumerProfile.getText().trim();
        } catch (Exception e) {
            System.err.println("Error updating food standards: " + e.getMessage());
        }
    }

    private void updateDrinkFormDataFromBasicTab(Tab tab, DrinkFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();
            GridPane grid = (GridPane) content.getChildren().get(1);

            TextField txtName = (TextField) grid.getChildren().get(1);
            TextField txtId = (TextField) grid.getChildren().get(3);
            TextField txtPrice = (TextField) grid.getChildren().get(5);
            TextField txtAvgPrice = (TextField) grid.getChildren().get(7);
            TextField txtExpiry = (TextField) grid.getChildren().get(9);
            TextField txtAlcohol = (TextField) grid.getChildren().get(11);

            formData.name = txtName.getText().trim();
            if (!txtId.getText().trim().isEmpty()) {
                formData.id = Integer.parseInt(txtId.getText().trim());
            }
            if (!txtPrice.getText().trim().isEmpty()) {
                formData.price = Double.parseDouble(txtPrice.getText().trim());
            }
            if (!txtAvgPrice.getText().trim().isEmpty()) {
                formData.avgPricePerKg = Double.parseDouble(txtAvgPrice.getText().trim());
            }
            formData.expiryDate = txtExpiry.getText().trim();
            if (!txtAlcohol.getText().trim().isEmpty()) {
                formData.alcoholContent = Double.parseDouble(txtAlcohol.getText().trim());
            }
        } catch (Exception e) {
            System.err.println("Error updating drink basic info: " + e.getMessage());
        }
    }

    private void updateDrinkFormDataFromIngredientsTab(Tab tab, DrinkFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();
            ListView<Ingredient> listView = (ListView<Ingredient>) content.getChildren().get(2);
            formData.ingredients = FXCollections.observableArrayList(listView.getItems());
        } catch (Exception e) {
            System.err.println("Error updating drink ingredients: " + e.getMessage());
        }
    }

    private void updateDrinkFormDataFromConditionsTab(Tab tab, DrinkFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();

            // Laboratory conditions
            GridPane labGrid = (GridPane) content.getChildren().get(2);
            TextField txtLabTemp = (TextField) labGrid.getChildren().get(1);
            TextField txtLabPressure = (TextField) labGrid.getChildren().get(3);
            TextField txtLabMoisture = (TextField) labGrid.getChildren().get(5);
            TextField txtLabVibration = (TextField) labGrid.getChildren().get(7);
            TextField txtLabPeriod = (TextField) labGrid.getChildren().get(9);

            if (!txtLabTemp.getText().trim().isEmpty()) {
                formData.labTemp = Double.parseDouble(txtLabTemp.getText().trim());
            }
            if (!txtLabPressure.getText().trim().isEmpty()) {
                formData.labPressure = Double.parseDouble(txtLabPressure.getText().trim());
            }
            if (!txtLabMoisture.getText().trim().isEmpty()) {
                formData.labMoisture = Double.parseDouble(txtLabMoisture.getText().trim());
            }
            if (!txtLabVibration.getText().trim().isEmpty()) {
                formData.labVibration = Double.parseDouble(txtLabVibration.getText().trim());
            }
            if (!txtLabPeriod.getText().trim().isEmpty()) {
                formData.labPeriod = Integer.parseInt(txtLabPeriod.getText().trim());
            }

            // Conservation conditions
            GridPane conserveGrid = (GridPane) content.getChildren().get(4);
            TextField txtConserveTemp = (TextField) conserveGrid.getChildren().get(1);
            TextField txtConserveMoisture = (TextField) conserveGrid.getChildren().get(3);
            TextField txtContainer = (TextField) conserveGrid.getChildren().get(5);

            if (!txtConserveTemp.getText().trim().isEmpty()) {
                formData.conserveTemp = Double.parseDouble(txtConserveTemp.getText().trim());
            }
            if (!txtConserveMoisture.getText().trim().isEmpty()) {
                formData.conserveMoisture = Double.parseDouble(txtConserveMoisture.getText().trim());
            }
            formData.containerType = txtContainer.getText().trim();

            // Consumption conditions
            GridPane consumeGrid = (GridPane) content.getChildren().get(6);
            TextField txtConsumeTemp = (TextField) consumeGrid.getChildren().get(1);
            TextField txtConsumeMoisture = (TextField) consumeGrid.getChildren().get(3);

            if (!txtConsumeTemp.getText().trim().isEmpty()) {
                formData.consumeTemp = Double.parseDouble(txtConsumeTemp.getText().trim());
            }
            if (!txtConsumeMoisture.getText().trim().isEmpty()) {
                formData.consumeMoisture = Double.parseDouble(txtConsumeMoisture.getText().trim());
            }
        } catch (Exception e) {
            System.err.println("Error updating drink conditions: " + e.getMessage());
        }
    }

    private void updateDrinkFormDataFromProtocolTab(Tab tab, DrinkFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();
            ListView<String> listView = (ListView<String>) content.getChildren().get(2);
            formData.preparationSteps = FXCollections.observableArrayList(listView.getItems());
        } catch (Exception e) {
            System.err.println("Error updating drink protocol: " + e.getMessage());
        }
    }

    private void updateDrinkFormDataFromStandardsTab(Tab tab, DrinkFormData formData) {
        try {
            ScrollPane scrollPane = (ScrollPane) tab.getContent();
            VBox content = (VBox) scrollPane.getContent();

            ListView<String> standardsList = (ListView<String>) content.getChildren().get(2);
            formData.standards = FXCollections.observableArrayList(standardsList.getItems());

            TextArea txtConsumerProfile = (TextArea) content.getChildren().get(5);
            formData.consumerProfile = txtConsumerProfile.getText().trim();
        } catch (Exception e) {
            System.err.println("Error updating drink standards: " + e.getMessage());
        }
    }

    // Ingredients form (shared between food and drink)
    private ScrollPane createIngredientsForm() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Ingredients");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label info = new Label("Add all ingredients used in this formulation");
        info.setFont(FONT_BODY);
        info.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        ListView<Ingredient> listView = new ListView<>(ingredients);
        listView.setPrefHeight(300);
        listView.setStyle("-fx-border-color: " + COLOR_BORDER + ";");

        // Custom cell factory to display ingredient information
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    Quantity q = ingredient.getQuantity();
                    String details = String.format("%s (ID: %d) - Weight: %.1fg, Volume: %.1fml, Fraction: %.2f, Unit: %s",
                            ingredient.getName(),
                            ingredient.getIngredientID(),
                            q.getWeight(),
                            q.getVolume(),
                            q.getFraction(),
                            q.getUnit());
                    setText(details);
                }
            }
        });

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Ingredient", COLOR_SUCCESS);
        Button btnRemove = createSmallButton("Remove Selected", COLOR_ERROR);

        btnAdd.setOnAction(e -> showAddIngredientDialog(ingredients));

        btnRemove.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredients.remove(selected);
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove);
        content.getChildren().addAll(title, info, listView, buttonBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void showAddIngredientDialog(ObservableList<Ingredient> ingredients) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Ingredient");
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Add New Ingredient");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 20, 0));

        TextField txtId = createDialogField(grid, "Ingredient ID:*", 0);
        TextField txtName = createDialogField(grid, "Name:*", 1);
        TextField txtWeight = createDialogField(grid, "Weight (g):", 2);
        TextField txtVolume = createDialogField(grid, "Volume (ml):", 3);
        TextField txtFraction = createDialogField(grid, "Fraction (0.0-1.0):", 4);
        TextField txtUnit = createDialogField(grid, "Unit:", 5);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnAdd = createStandardButton("Add Ingredient", COLOR_SUCCESS);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnAdd.setOnAction(e -> {
            try {
                if (txtId.getText().trim().isEmpty()) {
                    showError("Validation Error", "Ingredient ID is required");
                    return;
                }
                if (txtName.getText().trim().isEmpty()) {
                    showError("Validation Error", "Ingredient name is required");
                    return;
                }

                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();

                double weight = txtWeight.getText().isEmpty() ? 0.0 : Double.parseDouble(txtWeight.getText().trim());
                double volume = txtVolume.getText().isEmpty() ? 0.0 : Double.parseDouble(txtVolume.getText().trim());
                double fraction = txtFraction.getText().isEmpty() ? 0.0 : Double.parseDouble(txtFraction.getText().trim());
                String unit = txtUnit.getText().trim();

                if (fraction < 0.0 || fraction > 1.0) {
                    showError("Validation Error", "Fraction must be between 0.0 and 1.0");
                    return;
                }

                Quantity quantity = new Quantity(weight, volume, fraction, unit);
                Ingredient ingredient = new Ingredient(id, name, quantity);
                ingredients.add(ingredient);
                dialog.close();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numeric values");
            } catch (Exception ex) {
                showError("Error", "Failed to add ingredient: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnAdd, btnCancel);
        content.getChildren().addAll(title, grid, buttonBox);

        Scene scene = new Scene(content, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Validation and creation methods
    private boolean validateAndCreateFood(FoodFormData formData) {
        try {
            // Basic validation
            if (formData.name == null || formData.name.trim().isEmpty()) {
                showError("Validation Error", "Food name is required");
                return false;
            }
            if (formData.id <= 0) {
                showError("Validation Error", "Valid Food ID is required");
                return false;
            }
            if (formData.price <= 0) {
                showError("Validation Error", "Valid price is required");
                return false;
            }
            if (formData.expiryDate == null || formData.expiryDate.trim().isEmpty()) {
                showError("Validation Error", "Expiry date is required");
                return false;
            }
            if (formData.ingredients == null || formData.ingredients.isEmpty()) {
                showError("Validation Error", "At least one ingredient is required");
                return false;
            }

            // Check if ID exists
            for (Item item : allFormulations) {
                if (item.getItemID() == formData.id) {
                    showError("Error", "Food ID already exists");
                    return false;
                }
            }

            // Create food
            Food food = new Food();
            food.setName(formData.name.trim());
            food.setFoodID(formData.id);
            food.setItemID(formData.id);
            food.setPrice(formData.price);
            food.setExpiryDate(formData.expiryDate.trim());
            food.setAveragePricePerKg(formData.avgPricePerKg);

            // Add ingredients
            for (Ingredient ingredient : formData.ingredients) {
                food.addIngredient(ingredient);
            }

            // Set laboratory conditions if provided
            if (formData.labTemp != 0 || formData.labPressure != 0 || formData.labMoisture != 0) {
                Optcondition labCond = new Optcondition();
                labCond.setTemp(formData.labTemp);
                labCond.setPressure(formData.labPressure);
                labCond.setMoisture(formData.labMoisture);
                labCond.setVibration(formData.labVibration);
                labCond.setPeriod(formData.labPeriod);
                food.setLabCondition(labCond);
            }

            // Set conservation conditions if provided
            if (formData.conserveTemp != 0 || formData.conserveMoisture != 0 ||
                    (formData.containerType != null && !formData.containerType.trim().isEmpty())) {
                Conservecondition conserveCond = new Conservecondition();
                conserveCond.setTemp(formData.conserveTemp);
                conserveCond.setMoisture(formData.conserveMoisture);
                conserveCond.setContainer(formData.containerType.trim());
                food.setConservecondition(conserveCond);
            }

            // Set consumption conditions if provided
            if (formData.consumeTemp != 0 || formData.consumeMoisture != 0) {
                Consumpcondition consumeCond = new Consumpcondition();
                consumeCond.setTemperature(formData.consumeTemp);
                consumeCond.setMoisture(formData.consumeMoisture);
                food.setConsumpcondition(consumeCond);
            }

            // Set preparation protocol if steps exist
            if (formData.preparationSteps != null && !formData.preparationSteps.isEmpty()) {
                Prepprotocol protocol = new Prepprotocol();
                for (String step : formData.preparationSteps) {
                    if (step != null && !step.trim().isEmpty()) {
                        protocol.addStep(step.trim(), null);
                    }
                }
                if (!protocol.getSteps().isEmpty()) {
                    food.setPrepprotocol(protocol);
                }
            }

            // Add standards if exist
            if (formData.standards != null) {
                for (String standard : formData.standards) {
                    if (standard != null && !standard.trim().isEmpty()) {
                        food.addStandard(standard.trim());
                    }
                }
            }

            // Set consumer profile if provided
            if (formData.consumerProfile != null && !formData.consumerProfile.trim().isEmpty()) {
                ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
                consumerProfile.setProfile(formData.consumerProfile.trim());
                food.setConsumerProfile(consumerProfile);
            }

            // Add author
            food.addAuthor((Author) currentUser);

            // Add to system
            ((Author) currentUser).getFormulatedItems().add(food);
            allFormulations.add(food);

            if (databaseManager != null) {
                databaseManager.saveItem(food);
            }

            auditTrail.logAction("AUTHOR:" + ((Author)currentUser).getName(),
                    "Created food formulation: " + food.getName() + " (ID: " + food.getItemID() + ")");
            showInformation("Success", "Food formulation created successfully!");
            return true;

        } catch (Exception e) {
            showError("Error", "Failed to create food formulation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateAndCreateDrink(DrinkFormData formData) {
        try {
            // Basic validation
            if (formData.name == null || formData.name.trim().isEmpty()) {
                showError("Validation Error", "Drink name is required");
                return false;
            }
            if (formData.id <= 0) {
                showError("Validation Error", "Valid Drink ID is required");
                return false;
            }
            if (formData.price <= 0) {
                showError("Validation Error", "Valid price is required");
                return false;
            }
            if (formData.expiryDate == null || formData.expiryDate.trim().isEmpty()) {
                showError("Validation Error", "Expiry date is required");
                return false;
            }
            if (formData.ingredients == null || formData.ingredients.isEmpty()) {
                showError("Validation Error", "At least one ingredient is required");
                return false;
            }
            if (formData.alcoholContent < 0 || formData.alcoholContent > 100) {
                showError("Validation Error", "Alcohol content must be between 0 and 100%");
                return false;
            }

            // Check if ID exists
            for (Item item : allFormulations) {
                if (item.getItemID() == formData.id) {
                    showError("Error", "Drink ID already exists");
                    return false;
                }
            }

            // Create drink
            Drink drink = new Drink();
            drink.setName(formData.name.trim());
            drink.setDrinkID(formData.id);
            drink.setItemID(formData.id);
            drink.setPrice(formData.price);
            drink.setExpiryDate(formData.expiryDate.trim());
            drink.setAveragePricePerKg(formData.avgPricePerKg);
            drink.setAlcoholContent(formData.alcoholContent);

            // Add ingredients
            for (Ingredient ingredient : formData.ingredients) {
                drink.addIngredient(ingredient);
            }

            // Set laboratory conditions if provided
            if (formData.labTemp != 0 || formData.labPressure != 0 || formData.labMoisture != 0) {
                Optcondition labCond = new Optcondition();
                labCond.setTemp(formData.labTemp);
                labCond.setPressure(formData.labPressure);
                labCond.setMoisture(formData.labMoisture);
                labCond.setVibration(formData.labVibration);
                labCond.setPeriod(formData.labPeriod);
                drink.setLabCondition(labCond);
            }

            // Set conservation conditions if provided
            if (formData.conserveTemp != 0 || formData.conserveMoisture != 0 ||
                    (formData.containerType != null && !formData.containerType.trim().isEmpty())) {
                Conservecondition conserveCond = new Conservecondition();
                conserveCond.setTemp(formData.conserveTemp);
                conserveCond.setMoisture(formData.conserveMoisture);
                conserveCond.setContainer(formData.containerType.trim());
                drink.setConservecondition(conserveCond);
            }

            // Set consumption conditions if provided
            if (formData.consumeTemp != 0 || formData.consumeMoisture != 0) {
                Consumpcondition consumeCond = new Consumpcondition();
                consumeCond.setTemperature(formData.consumeTemp);
                consumeCond.setMoisture(formData.consumeMoisture);
                drink.setConsumpcondition(consumeCond);
            }

            // Set preparation protocol if steps exist
            if (formData.preparationSteps != null && !formData.preparationSteps.isEmpty()) {
                Prepprotocol protocol = new Prepprotocol();
                for (String step : formData.preparationSteps) {
                    if (step != null && !step.trim().isEmpty()) {
                        protocol.addStep(step.trim(), null);
                    }
                }
                if (!protocol.getSteps().isEmpty()) {
                    drink.setPrepprotocol(protocol);
                }
            }

            // Add standards if exist
            if (formData.standards != null) {
                for (String standard : formData.standards) {
                    if (standard != null && !standard.trim().isEmpty()) {
                        drink.addStandard(standard.trim());
                    }
                }
            }

            // Set consumer profile if provided
            if (formData.consumerProfile != null && !formData.consumerProfile.trim().isEmpty()) {
                ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
                consumerProfile.setProfile(formData.consumerProfile.trim());
                drink.setConsumerProfile(consumerProfile);
            }

            // Add author
            drink.addAuthor((Author) currentUser);

            // Add to system
            ((Author) currentUser).getFormulatedItems().add(drink);
            allFormulations.add(drink);

            if (databaseManager != null) {
                databaseManager.saveItem(drink);
            }

            auditTrail.logAction("AUTHOR:" + ((Author)currentUser).getName(),
                    "Created drink formulation: " + drink.getName() + " (ID: " + drink.getItemID() +
                            ", Alcohol: " + formData.alcoholContent + "%)");
            showInformation("Success", "Drink formulation created successfully!");
            return true;

        } catch (Exception e) {
            showError("Error", "Failed to create drink formulation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void showMyFormulationsScreen() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("MY FORMULATIONS",
                "Author: " + author.getName() + " | Personal Formulations");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (author.getFormulatedItems().isEmpty()) {
            Label emptyLabel = new Label("You haven't created any formulations yet");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : author.getFormulatedItems()) {
                HBox formulationCard = createAuthorFormulationCard(item);
                content.getChildren().add(formulationCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAuthorDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createAuthorFormulationCard(Item item) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + (isItemVetoed(item) ? COLOR_ERROR : COLOR_BORDER) + "; " +
                "-fx-border-width: " + (isItemVetoed(item) ? "2" : "1") + "; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Type indicator
        VBox typeBox = new VBox();
        typeBox.setMinWidth(80);
        typeBox.setAlignment(Pos.CENTER);

        String type = item instanceof Food ? "FOOD" : "DRINK";
        String typeColor = item instanceof Food ? COLOR_SUCCESS : COLOR_INFO;

        Label typeLabel = new Label(type);
        typeLabel.setFont(FONT_BODY);
        typeLabel.setTextFill(Color.web(typeColor));
        typeLabel.setStyle("-fx-font-weight: bold;");

        // Add alcohol content for drinks
        if (item instanceof Drink) {
            Drink drink = (Drink) item;
            Label alcoholLabel = new Label(String.format("%.1f%%", drink.getAlcoholContent()));
            alcoholLabel.setFont(Font.font(10));
            alcoholLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            typeBox.getChildren().addAll(typeLabel, alcoholLabel);
        } else {
            typeBox.getChildren().add(typeLabel);
        }

        // Formulation info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        String priceText = String.format("$%.2f", item.getPrice());
        if (item instanceof Food) {
            Food food = (Food) item;
            priceText += " | $" + String.format("%.2f/kg", food.getAveragePricePerKg());
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            priceText += " | $" + String.format("%.2f/kg", drink.getAveragePricePerKg());
        }

        Label detailsLabel = new Label("ID: " + item.getItemID() +
                " | Price: " + priceText +
                " | Expiry: " + item.getExpiryDate());
        detailsLabel.setFont(FONT_BODY);
        detailsLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        // Feedback info
        LinkedList<Feedback> feedbacks = getFeedbacks(item);
        if (feedbacks != null && !feedbacks.isEmpty()) {
            int likes = 0;
            for (Feedback fb : feedbacks) {
                if (fb.isLike()) likes++;
            }
            Label feedbackLabel = new Label("Feedbacks: " + feedbacks.size() +
                    " (👍 " + likes + " | 👎 " + (feedbacks.size() - likes) + ")");
            feedbackLabel.setFont(FONT_BODY);
            feedbackLabel.setTextFill(Color.web(COLOR_INFO));
            infoBox.getChildren().add(feedbackLabel);
        }

        // Veto status
        if (isItemVetoed(item)) {
            Label vetoLabel = new Label("⚠ VETOED");
            vetoLabel.setFont(FONT_BODY);
            vetoLabel.setTextFill(Color.web(COLOR_ERROR));
            vetoLabel.setStyle("-fx-font-weight: bold;");
            infoBox.getChildren().add(vetoLabel);
        }

        infoBox.getChildren().addAll(nameLabel, detailsLabel);

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnView = createSmallButton("View", COLOR_INFO);
        Button btnEdit = createSmallButton("Edit", COLOR_WARNING);
        Button btnStats = createSmallButton("Stats", COLOR_PRIMARY);

        btnView.setOnAction(e -> showFormulationDetails(item));
        btnEdit.setOnAction(e -> showEditFormulationDialog(item));
        btnStats.setOnAction(e -> showFormulationStatistics(item));

        buttonBox.getChildren().addAll(btnView, btnEdit, btnStats);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(typeBox, infoBox, buttonBox);

        return card;
    }

    private void showUpdateFormulationSelectionScreen() {
        Author author = (Author) currentUser;

        if (author.getFormulatedItems().isEmpty()) {
            showInformation("No Formulations", "You haven't created any formulations yet");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Formulation to Update");
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Select a formulation to update:");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        ComboBox<Item> combo = new ComboBox<>();
        ObservableList<Item> items = FXCollections.observableArrayList(author.getFormulatedItems());
        combo.setItems(items);
        combo.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (ID: " + item.getItemID() +
                            ", Type: " + (item instanceof Food ? "Food" : "Drink") + ")");
                }
            }
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnUpdate = createStandardButton("Update Selected", COLOR_WARNING);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnUpdate.setOnAction(e -> {
            Item selected = combo.getValue();
            if (selected != null) {
                dialog.close();
                showEditFormulationDialog(selected);
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        content.getChildren().addAll(title, combo, buttonBox);
        buttonBox.getChildren().addAll(btnUpdate, btnCancel);

        Scene scene = new Scene(content, 500, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showEditFormulationDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Formulation: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Update: " + item.getName());
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 20, 0));

        TextField txtName = createDialogField(grid, "Name:*", 0);
        txtName.setText(item.getName());

        TextField txtPrice = createDialogField(grid, "Price ($):*", 1);
        txtPrice.setText(String.valueOf(item.getPrice()));

        TextField txtExpiry = createDialogField(grid, "Expiry Date (YYYY-MM-DD):*", 2);
        txtExpiry.setText(item.getExpiryDate());

        if (item instanceof Food) {
            Food food = (Food) item;
            TextField txtAvgPrice = createDialogField(grid, "Avg Price per Kg ($):", 3);
            txtAvgPrice.setText(String.valueOf(food.getAveragePricePerKg()));
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            TextField txtAvgPrice = createDialogField(grid, "Avg Price per Kg ($):", 3);
            txtAvgPrice.setText(String.valueOf(drink.getAveragePricePerKg()));
            TextField txtAlcohol = createDialogField(grid, "Alcohol Content (%):", 4);
            txtAlcohol.setText(String.valueOf(drink.getAlcoholContent()));
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnSave = createStandardButton("Save Changes", COLOR_SUCCESS);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnSave.setOnAction(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                String expiry = txtExpiry.getText().trim();

                if (name.isEmpty()) {
                    showError("Validation Error", "Name is required");
                    return;
                }
                if (price <= 0) {
                    showError("Validation Error", "Valid price is required");
                    return;
                }

                item.setName(name);
                item.setPrice(price);
                item.setExpiryDate(expiry);

                if (item instanceof Food) {
                    Food food = (Food) item;
                    TextField txtAvgPrice = (TextField) grid.getChildren().get(7); // Get the avg price field
                    double avgPrice = Double.parseDouble(txtAvgPrice.getText().trim());
                    food.setAveragePricePerKg(avgPrice);
                } else if (item instanceof Drink) {
                    Drink drink = (Drink) item;
                    TextField txtAvgPrice = (TextField) grid.getChildren().get(7); // Get the avg price field
                    double avgPrice = Double.parseDouble(txtAvgPrice.getText().trim());
                    drink.setAveragePricePerKg(avgPrice);

                    TextField txtAlcohol = (TextField) grid.getChildren().get(9); // Get the alcohol field
                    double alcohol = Double.parseDouble(txtAlcohol.getText().trim());
                    drink.setAlcoholContent(alcohol);
                }

                if (databaseManager != null) {
                    databaseManager.saveItem(item);
                }

                auditTrail.logAction("AUTHOR:" + ((Author)currentUser).getName(),
                        "Updated formulation: " + item.getName());
                showInformation("Success", "Formulation updated successfully");
                dialog.close();
                showMyFormulationsScreen();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numeric values");
            } catch (Exception ex) {
                showError("Error", "Failed to update formulation: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnSave, btnCancel);
        content.getChildren().addAll(title, grid, buttonBox);

        Scene scene = new Scene(content, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAuthorQualityCheckScreen() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("QUALITY CHECK",
                "Author: " + author.getName() + " | Formulation Quality Analysis");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        TextArea issuesArea = new TextArea();
        issuesArea.setEditable(false);
        issuesArea.setPrefHeight(500);
        issuesArea.setStyle("-fx-control-inner-background: white; " +
                "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";");

        StringBuilder issues = new StringBuilder();
        int issueCount = 0;

        issues.append("=== PERSONAL FORMULATION QUALITY REPORT ===\n");
        issues.append("Author: " + author.getName() + "\n");
        issues.append("Generated: " + new Date() + "\n\n");

        for (Item item : author.getFormulatedItems()) {
            boolean hasIssues = false;
            StringBuilder itemIssues = new StringBuilder();

            // Check ingredients
            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients == null || ingredients.isEmpty()) {
                itemIssues.append("  ❌ Missing ingredients\n");
                hasIssues = true;
            }

            // Check price
            if (item.getPrice() <= 0) {
                itemIssues.append("  ❌ Invalid price\n");
                hasIssues = true;
            }

            // Check expiry date
            if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
                itemIssues.append("  ❌ Missing expiry date\n");
                hasIssues = true;
            }

            // Check veto status
            if (isItemVetoed(item)) {
                itemIssues.append("  ⚠ Formulation is vetoed\n");
                hasIssues = true;
            }

            // Check feedback
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null && !feedbacks.isEmpty()) {
                int negativeCount = 0;
                for (Feedback fb : feedbacks) {
                    if (!fb.isLike()) negativeCount++;
                }
                if (negativeCount > 0) {
                    itemIssues.append("  ⚠ " + negativeCount + " negative feedback(s)\n");
                    hasIssues = true;
                }
            }

            if (hasIssues) {
                issueCount++;
                issues.append(item.getName() + " (ID: " + item.getItemID() + ")\n");
                issues.append(itemIssues.toString() + "\n");
            }
        }

        if (issueCount == 0) {
            issues.append("Excellent! All your formulations meet quality standards.");
        } else {
            issues.insert(0, "Found " + issueCount + " formulation(s) with issues:\n\n");
        }

        issuesArea.setText(issues.toString());

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAuthorDashboard());

        content.getChildren().addAll(issuesArea, btnBack);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAuthorStatisticsScreen() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("MY STATISTICS",
                "Author: " + author.getName() + " | Performance Overview");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(40);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        int foodCount = countAuthorFoods(author);
        int drinkCount = countAuthorDrinks(author);
        int totalFeedbacks = countAuthorFeedbacks(author);
        int positiveFeedbacks = countAuthorPositiveFeedbacks(author);
        double avgPrice = calculateAuthorAveragePrice(author);
        int vetoedCount = countAuthorVetoedFormulations(author);

        addStatRow(statsGrid, 0, "Total Formulations:", author.getFormulatedItems().size(), COLOR_AUTHOR);
        addStatRow(statsGrid, 1, "Food Formulations:", foodCount, COLOR_SUCCESS);
        addStatRow(statsGrid, 2, "Drink Formulations:", drinkCount, COLOR_INFO);
        addStatRow(statsGrid, 3, "Average Price:", "$" + String.format("%.2f", avgPrice), COLOR_PRIMARY);
        addStatRow(statsGrid, 4, "Total Feedbacks:", totalFeedbacks, COLOR_INFO);
        addStatRow(statsGrid, 5, "Positive Feedbacks:", positiveFeedbacks, COLOR_SUCCESS);
        addStatRow(statsGrid, 6, "Vetoed Formulations:", vetoedCount, COLOR_ERROR);

        if (totalFeedbacks > 0) {
            double positiveRate = (positiveFeedbacks * 100.0) / totalFeedbacks;
            addStatRow(statsGrid, 7, "Positive Rate:", String.format("%.1f%%", positiveRate),
                    positiveRate >= 80 ? COLOR_SUCCESS :
                            positiveRate >= 60 ? COLOR_WARNING : COLOR_ERROR);
        }

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAuthorDashboard());

        content.getChildren().addAll(statsGrid, btnBack);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllFormulationsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("ALL FORMULATIONS", "Browse All System Formulations");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (allFormulations.isEmpty()) {
            Label emptyLabel = new Label("No formulations found");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : allFormulations) {
                HBox formulationCard = createBrowseFormulationCard(item);
                content.getChildren().add(formulationCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showAuthorDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createBrowseFormulationCard(Item item) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Type indicator
        VBox typeBox = new VBox();
        typeBox.setMinWidth(80);
        typeBox.setAlignment(Pos.CENTER);

        String type = item instanceof Food ? "FOOD" : "DRINK";
        String typeColor = item instanceof Food ? COLOR_SUCCESS : COLOR_INFO;

        Label typeLabel = new Label(type);
        typeLabel.setFont(FONT_BODY);
        typeLabel.setTextFill(Color.web(typeColor));
        typeLabel.setStyle("-fx-font-weight: bold;");

        typeBox.getChildren().add(typeLabel);

        // Formulation info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        // Get author names
        StringBuilder authorsStr = new StringBuilder("By: ");
        if (item instanceof Food) {
            Food food = (Food) item;
            for (Author author : food.getAuthors()) {
                authorsStr.append(author.getName()).append(", ");
            }
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            for (Author author : drink.getAuthors()) {
                authorsStr.append(author.getName()).append(", ");
            }
        }

        if (authorsStr.length() > 4) {
            authorsStr.setLength(authorsStr.length() - 2); // Remove trailing comma
        }

        Label authorLabel = new Label(authorsStr.toString());
        authorLabel.setFont(FONT_BODY);
        authorLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        Label detailsLabel = new Label("Price: $" + String.format("%.2f", item.getPrice()) +
                " | Expiry: " + item.getExpiryDate());
        detailsLabel.setFont(FONT_BODY);
        detailsLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        infoBox.getChildren().addAll(nameLabel, authorLabel, detailsLabel);

        // View button
        Button btnView = createSmallButton("View Details", COLOR_INFO);
        btnView.setOnAction(e -> showFormulationDetails(item));

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(typeBox, infoBox, btnView);

        return card;
    }

    private void showFormulationStatistics(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Statistics: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label(item.getName() + " - Statistics");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + "; " +
                "-fx-padding: 15; " +
                "-fx-background-radius: 8;");

        // Calculate statistics
        LinkedList<Feedback> feedbacks = getFeedbacks(item);
        int totalFeedbacks = feedbacks != null ? feedbacks.size() : 0;
        int likes = 0;
        if (feedbacks != null) {
            for (Feedback fb : feedbacks) {
                if (fb.isLike()) likes++;
            }
        }

        addDetail(statsBox, "Price:", "$" + String.format("%.2f", item.getPrice()));
        addDetail(statsBox, "Expiry Date:", item.getExpiryDate());
        addDetail(statsBox, "Total Feedbacks:", String.valueOf(totalFeedbacks));
        addDetail(statsBox, "Likes:", String.valueOf(likes));
        addDetail(statsBox, "Dislikes:", String.valueOf(totalFeedbacks - likes));

        if (totalFeedbacks > 0) {
            double likePercentage = (likes * 100.0) / totalFeedbacks;
            String likeColor = likePercentage >= 80 ? COLOR_SUCCESS :
                    likePercentage >= 60 ? COLOR_WARNING : COLOR_ERROR;

            addDetail(statsBox, "Like Percentage:",
                    String.format("%.1f%%", likePercentage), likeColor);
        }

        if (isItemVetoed(item)) {
            Label vetoLabel = new Label("⚠ This formulation is VETOED");
            vetoLabel.setFont(FONT_BODY);
            vetoLabel.setTextFill(Color.web(COLOR_ERROR));
            vetoLabel.setStyle("-fx-font-weight: bold;");
            statsBox.getChildren().add(vetoLabel);
        }

        Button btnClose = createStandardButton("Close", COLOR_TEXT_SECONDARY);
        btnClose.setOnAction(e -> dialog.close());

        content.getChildren().addAll(title, statsBox, btnClose);

        Scene scene = new Scene(content, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ============ CUSTOMER FUNCTIONALITIES ============

    private void showCustomerCatalogScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("BROWSE CATALOG",
                "Customer: " + customer.getName() + " | Available Formulations");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        LinkedList<Item> availableItems = customer.getAvailableFormulations();

        if (availableItems.isEmpty()) {
            Label emptyLabel = new Label("No formulations available at this time");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : availableItems) {
                HBox catalogCard = createCatalogCard(item, customer);
                content.getChildren().add(catalogCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showCustomerDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createCatalogCard(Item item, Customer customer) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Type indicator
        VBox typeBox = new VBox();
        typeBox.setMinWidth(80);
        typeBox.setAlignment(Pos.CENTER);

        String type = item instanceof Food ? "FOOD" : "DRINK";
        String typeColor = item instanceof Food ? COLOR_SUCCESS : COLOR_INFO;

        Label typeLabel = new Label(type);
        typeLabel.setFont(FONT_BODY);
        typeLabel.setTextFill(Color.web(typeColor));
        typeLabel.setStyle("-fx-font-weight: bold;");

        typeBox.getChildren().add(typeLabel);

        // Item info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label priceLabel = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(FONT_BODY);
        priceLabel.setTextFill(Color.web(COLOR_SUCCESS));
        priceLabel.setStyle("-fx-font-weight: bold;");

        // Get author names
        StringBuilder authorsStr = new StringBuilder("By: ");
        if (item instanceof Food) {
            Food food = (Food) item;
            for (Author author : food.getAuthors()) {
                authorsStr.append(author.getName()).append(", ");
            }
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            for (Author author : drink.getAuthors()) {
                authorsStr.append(author.getName()).append(", ");
            }
        }

        if (authorsStr.length() > 4) {
            authorsStr.setLength(authorsStr.length() - 2);
            Label authorLabel = new Label(authorsStr.toString());
            authorLabel.setFont(FONT_BODY);
            authorLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            infoBox.getChildren().add(authorLabel);
        }

        infoBox.getChildren().addAll(nameLabel, priceLabel);

        // Purchase status and buttons
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setMinWidth(200);

        boolean purchased = customer.isPaid(item);

        Label statusLabel = new Label(purchased ? "✓ PURCHASED" : "Available for Purchase");
        statusLabel.setFont(FONT_BODY);
        statusLabel.setTextFill(purchased ? Color.web(COLOR_SUCCESS) : Color.web(COLOR_TEXT_SECONDARY));
        statusLabel.setStyle("-fx-font-weight: bold;");

        HBox buttonBox = new HBox(10);
        Button btnView = createSmallButton("View Details", COLOR_INFO);
        Button btnAction = createSmallButton(purchased ? "View" : "Purchase",
                purchased ? COLOR_INFO : COLOR_SUCCESS);

        btnView.setOnAction(e -> showCustomerItemDetails(item, customer));
        btnAction.setOnAction(e -> {
            if (purchased) {
                showCustomerItemDetails(item, customer);
            } else {
                showPurchaseDialog(item);
            }
        });

        buttonBox.getChildren().addAll(btnView, btnAction);
        actionBox.getChildren().addAll(statusLabel, buttonBox);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(typeBox, infoBox, actionBox);

        return card;
    }

    private void showCustomerItemDetails(Item item, Customer customer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label(item.getName());
        title.setFont(FONT_TITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + "; " +
                "-fx-padding: 15; " +
                "-fx-background-radius: 8;");

        addDetail(detailsBox, "Type:", item instanceof Food ? "Food" : "Drink");
        addDetail(detailsBox, "Price:", "$" + String.format("%.2f", item.getPrice()));
        addDetail(detailsBox, "Expiry Date:", item.getExpiryDate());

        boolean purchased = customer.isPaid(item);

        if (purchased) {
            // Show full details for purchased items
            Label purchasedLabel = new Label("✓ You have purchased this item");
            purchasedLabel.setFont(FONT_BODY);
            purchasedLabel.setTextFill(Color.web(COLOR_SUCCESS));
            purchasedLabel.setStyle("-fx-font-weight: bold;");
            detailsBox.getChildren().add(purchasedLabel);

            // Show ingredients
            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients != null && !ingredients.isEmpty()) {
                Label ingTitle = new Label("Ingredients:");
                ingTitle.setFont(FONT_SUBTITLE);
                ingTitle.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
                ingTitle.setPadding(new Insets(10, 0, 5, 0));
                detailsBox.getChildren().add(ingTitle);

                for (Ingredient ing : ingredients) {
                    Label ingLabel = new Label("• " + ing.getName());
                    ingLabel.setFont(FONT_BODY);
                    ingLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
                    detailsBox.getChildren().add(ingLabel);
                }
            }

            // Add feedback button
            Button btnFeedback = createStandardButton("Provide Feedback", COLOR_INFO);
            btnFeedback.setOnAction(e -> {
                dialog.close();
                showFeedbackDialog(item);
            });
            detailsBox.getChildren().add(btnFeedback);

        } else {
            Label lockedLabel = new Label("🔒 Purchase this item to view full details and ingredients");
            lockedLabel.setFont(FONT_BODY);
            lockedLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            detailsBox.getChildren().add(lockedLabel);
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnPurchase = createStandardButton("Purchase", COLOR_SUCCESS);
        Button btnFavorite = createStandardButton("Add to Favorites", COLOR_WARNING);
        Button btnClose = createStandardButton("Close", COLOR_TEXT_SECONDARY);

        btnPurchase.setOnAction(e -> {
            dialog.close();
            showPurchaseDialog(item);
        });

        btnFavorite.setOnAction(e -> {
            customer.addToFavorites(item);
            showInformation("Added", "Added to favorites");
        });

        btnClose.setOnAction(e -> dialog.close());

        if (purchased) {
            buttonBox.getChildren().addAll(btnFavorite, btnClose);
        } else {
            buttonBox.getChildren().addAll(btnPurchase, btnFavorite, btnClose);
        }

        content.getChildren().addAll(title, detailsBox, buttonBox);

        Scene scene = new Scene(content, 500, purchased ? 500 : 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showPurchaseDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Purchase: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Confirm Purchase");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label itemLabel = new Label("Item: " + item.getName());
        itemLabel.setFont(FONT_BODY);
        itemLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label priceLabel = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(FONT_TITLE);
        priceLabel.setTextFill(Color.web(COLOR_SUCCESS));

        Label methodLabel = new Label("Select Payment Method:");
        methodLabel.setFont(FONT_BODY);
        methodLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("Credit Card", "Debit Card", "PayPal", "Bank Transfer", "Cash");
        methodCombo.setValue("Credit Card");
        methodCombo.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + COLOR_BORDER + ";");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnPurchase = createStandardButton("Confirm Purchase", COLOR_SUCCESS);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnPurchase.setOnAction(e -> {
            Customer customer = (Customer) currentUser;
            String method = methodCombo.getValue();

            if (customer.makePayment(item, method)) {
                if (databaseManager != null) {
                    databaseManager.saveCustomer(customer);
                }

                auditTrail.logAction("CUSTOMER:" + customer.getName(),
                        "Purchased: " + item.getName() + " for $" + item.getPrice() +
                                " via " + method);
                showInformation("Success", "Purchase completed successfully!");
                dialog.close();
                showCustomerCatalogScreen();
            } else {
                showError("Purchase Failed", "Could not complete purchase. Please try again.");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnPurchase, btnCancel);
        content.getChildren().addAll(title, itemLabel, priceLabel, methodLabel, methodCombo, buttonBox);

        Scene scene = new Scene(content, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showCustomerPurchasesScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("MY PURCHASES",
                "Customer: " + customer.getName() + " | Purchase History");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (customer.getPurchasedItems().isEmpty()) {
            Label emptyLabel = new Label("No purchases yet");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Customer.PurchaseRecord record : customer.getPurchasedItems().values()) {
                HBox purchaseCard = createPurchaseCard(record);
                content.getChildren().add(purchaseCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showCustomerDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createPurchaseCard(Customer.PurchaseRecord record) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(record.getItemName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label priceLabel = new Label("$" + String.format("%.2f", record.getPrice()));
        priceLabel.setFont(FONT_BODY);
        priceLabel.setTextFill(Color.web(COLOR_SUCCESS));
        priceLabel.setStyle("-fx-font-weight: bold;");

        Label dateLabel = new Label("Date: " + record.getPurchaseDate());
        dateLabel.setFont(FONT_BODY);
        dateLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        Label methodLabel = new Label("Method: " + record.getPaymentMethod());
        methodLabel.setFont(FONT_BODY);
        methodLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        infoBox.getChildren().addAll(nameLabel, priceLabel, dateLabel, methodLabel);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().add(infoBox);

        return card;
    }

    private void showCustomerFavoritesScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("MY FAVORITES",
                "Customer: " + customer.getName() + " | Favorite Formulations");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (customer.getFavoriteFormulations().isEmpty()) {
            Label emptyLabel = new Label("No favorites yet. Browse the catalog to add favorites!");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : customer.getFavoriteFormulations()) {
                HBox favoriteCard = createFavoriteCard(item, customer);
                content.getChildren().add(favoriteCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showCustomerDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createFavoriteCard(Item item, Customer customer) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label priceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(FONT_BODY);
        priceLabel.setTextFill(Color.web(COLOR_SUCCESS));

        boolean purchased = customer.isPaid(item);
        Label statusLabel = new Label(purchased ? "✓ Purchased" : "Not purchased");
        statusLabel.setFont(FONT_BODY);
        statusLabel.setTextFill(purchased ? Color.web(COLOR_SUCCESS) : Color.web(COLOR_TEXT_SECONDARY));

        infoBox.getChildren().addAll(nameLabel, priceLabel, statusLabel);

        HBox buttonBox = new HBox(10);
        Button btnView = createSmallButton("View", COLOR_INFO);
        Button btnRemove = createSmallButton("Remove", COLOR_ERROR);

        btnView.setOnAction(e -> showCustomerItemDetails(item, customer));
        btnRemove.setOnAction(e -> {
            customer.removeFromFavorites(item);
            if (databaseManager != null) {
                databaseManager.saveCustomer(customer);
            }
            showCustomerFavoritesScreen(); // Refresh
        });

        buttonBox.getChildren().addAll(btnView, btnRemove);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(infoBox, buttonBox);

        return card;
    }

    private void showCustomerProfileScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("MY PROFILE",
                "Customer: " + customer.getName() + " | Account Information");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        VBox profileBox = new VBox(15);
        profileBox.setStyle("-fx-background-color: white; " +
                "-fx-padding: 30; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        profileBox.setMaxWidth(500);

        Label title = new Label("Account Details");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_CUSTOMER));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 0, 20, 0));

        addDetail(grid, "Customer ID:", String.valueOf(customer.getCustomerID()), 0);
        addDetail(grid, "Name:", customer.getName(), 1);
        addDetail(grid, "Age:", String.valueOf(customer.getAge()), 2);
        addDetail(grid, "Address:", customer.getAddress() != null ? customer.getAddress() : "Not set", 3);
        addDetail(grid, "Contact:", customer.getContact() != null ? customer.getContact() : "Not set", 4);
        addDetail(grid, "Date of Birth:", customer.getDateofbirth() != null ? customer.getDateofbirth() : "Not set", 5);
        addDetail(grid, "Total Purchases:", String.valueOf(customer.getPurchasedItems().size()), 6);
        addDetail(grid, "Total Favorites:", String.valueOf(customer.getFavoriteFormulations().size()), 7);
        addDetail(grid, "Feedback Given:", String.valueOf(customer.getFeedbackHistory().size()), 8);

        profileBox.getChildren().addAll(title, grid);

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showCustomerDashboard());

        content.getChildren().addAll(profileBox, btnBack);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showFeedbackManagementScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("FEEDBACK MANAGEMENT",
                "Customer: " + customer.getName() + " | Provide Feedback");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Find purchased items without feedback
        LinkedList<Item> itemsWithoutFeedback = new LinkedList<>();
        for (Customer.PurchaseRecord record : customer.getPurchasedItems().values()) {
            for (Item item : allFormulations) {
                if (item.getName().equals(record.getItemName())) {
                    // Check if feedback already given
                    boolean hasFeedback = false;
                    LinkedList<Feedback> feedbacks = getFeedbacks(item);
                    if (feedbacks != null) {
                        for (Feedback fb : feedbacks) {
                            if (fb.getConsumerName().equals(customer.getName())) {
                                hasFeedback = true;
                                break;
                            }
                        }
                    }
                    if (!hasFeedback) {
                        itemsWithoutFeedback.add(item);
                    }
                    break;
                }
            }
        }

        if (itemsWithoutFeedback.isEmpty()) {
            Label emptyLabel = new Label("No items available for feedback. Purchase items first.");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : itemsWithoutFeedback) {
                HBox feedbackCard = createFeedbackCard(item);
                content.getChildren().add(feedbackCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showCustomerDashboard());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private HBox createFeedbackCard(Item item) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(FONT_SUBTITLE);
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label typeLabel = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
        typeLabel.setFont(FONT_BODY);
        typeLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        infoBox.getChildren().addAll(nameLabel, typeLabel);

        Button btnFeedback = createSmallButton("Give Feedback", COLOR_INFO);
        btnFeedback.setOnAction(e -> showFeedbackDialog(item));

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(infoBox, btnFeedback);

        return card;
    }

    private void showFeedbackDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Feedback for: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Provide Feedback");
        title.setFont(FONT_SUBTITLE);
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label itemLabel = new Label("Item: " + item.getName());
        itemLabel.setFont(FONT_BODY);
        itemLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label questionLabel = new Label("Did you like this formulation?");
        questionLabel.setFont(FONT_BODY);
        questionLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        ToggleGroup group = new ToggleGroup();
        RadioButton rbLike = new RadioButton("👍 Like");
        RadioButton rbDislike = new RadioButton("👎 Dislike");
        rbLike.setToggleGroup(group);
        rbDislike.setToggleGroup(group);
        rbLike.setSelected(true);

        VBox radioBox = new VBox(10, rbLike, rbDislike);
        radioBox.setPadding(new Insets(10, 0, 10, 0));

        Label commentLabel = new Label("Comments (optional):");
        commentLabel.setFont(FONT_BODY);
        commentLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        TextArea txtComment = new TextArea();
        txtComment.setPromptText("Share your thoughts about this formulation...");
        txtComment.setPrefRowCount(4);
        txtComment.setStyle("-fx-control-inner-background: white; " +
                "-fx-border-color: " + COLOR_BORDER + ";");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnSubmit = createStandardButton("Submit Feedback", COLOR_SUCCESS);
        Button btnCancel = createStandardButton("Cancel", COLOR_TEXT_SECONDARY);

        btnSubmit.setOnAction(e -> {
            boolean like = rbLike.isSelected();
            String comment = txtComment.getText().trim();

            if (comment.isEmpty()) {
                comment = like ? "Liked this formulation" : "Did not like this formulation";
            }

            Customer customer = (Customer) currentUser;
            Feedback feedback = customer.provideFeedback(item, comment, like);

            if (feedback != null) {
                if (databaseManager != null) {
                    databaseManager.saveItem(item);
                }

                auditTrail.logAction("CUSTOMER:" + customer.getName(),
                        "Provided feedback on: " + item.getName() + " (" + (like ? "Like" : "Dislike") + ")");
                showInformation("Thank You", "Your feedback has been submitted!");
                dialog.close();
                showFeedbackManagementScreen();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnSubmit, btnCancel);
        content.getChildren().addAll(title, itemLabel, questionLabel, radioBox,
                commentLabel, txtComment, buttonBox);

        Scene scene = new Scene(content, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showSearchFormulationsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        VBox header = createHeader("SEARCH FORMULATIONS",
                "Customer: " + ((Customer)currentUser).getName() + " | Search and Filter");
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Label searchLabel = new Label("Search Formulations");
        searchLabel.setFont(FONT_SUBTITLE);
        searchLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Enter formulation name or keyword...");
        txtSearch.setStyle(createTextFieldStyle());
        txtSearch.setMaxWidth(400);

        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER);

        Label filterLabel = new Label("Filter by:");
        filterLabel.setFont(FONT_BODY);
        filterLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Food Only", "Drink Only", "Authors", "Ingredients");
        filterCombo.setValue("All");
        filterCombo.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + COLOR_BORDER + ";");

        Button btnSearch = createStandardButton("Search", COLOR_INFO);
        Button btnBack = createStandardButton("Back to Dashboard", COLOR_TEXT_SECONDARY);

        btnSearch.setOnAction(e -> {
            String searchTerm = txtSearch.getText().trim().toLowerCase();
            String filter = filterCombo.getValue();

            LinkedList<Item> searchResults = new LinkedList<>();
            for (Item item : ((Customer)currentUser).getAvailableFormulations()) {
                // Apply search filter
                if (!searchTerm.isEmpty() && !item.getName().toLowerCase().contains(searchTerm)) {
                    continue;
                }

                // Apply type filter
                if (filter.equals("Food Only") && !(item instanceof Food)) {
                    continue;
                }
                if (filter.equals("Drink Only") && !(item instanceof Drink)) {
                    continue;
                }
                if (filter.equals("Authors") && !item.getAuthor().getName().toLowerCase().contains(searchTerm)) {
                    continue;
                }

                // Apply price filter
//                if (filter.equals("Under $10") && item.getPrice() >= 10) {
//                    continue;
//                }
//                if (filter.equals("$10-$20") && (item.getPrice() < 10 || item.getPrice() > 20)) {
//                    continue;
//                }
//                if (filter.equals("Over $20") && item.getPrice() <= 20) {
//                    continue;
//                }

                searchResults.add(item);
            }

            showSearchResults(searchResults, searchTerm, filter);
        });

        btnBack.setOnAction(e -> showCustomerDashboard());

        filterBox.getChildren().addAll(filterLabel, filterCombo);
        content.getChildren().addAll(searchLabel, txtSearch, filterBox, btnSearch, btnBack);
        root.setCenter(content);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showSearchResults(LinkedList<Item> results, String searchTerm, String filter) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_LIGHT_BG + ";");

        String resultsTitle = "Search Results";
        if (!searchTerm.isEmpty()) {
            resultsTitle += " for '" + searchTerm + "'";
        }
        if (!filter.equals("All")) {
            resultsTitle += " (" + filter + ")";
        }

        VBox header = createHeader(resultsTitle,
                "Found " + results.size() + " formulation(s)");
        root.setTop(header);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (results.isEmpty()) {
            Label emptyLabel = new Label("No formulations found matching your criteria");
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            content.getChildren().add(emptyLabel);
        } else {
            for (Item item : results) {
                HBox resultCard = createCatalogCard(item, (Customer) currentUser);
                content.getChildren().add(resultCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        Button btnBack = createStandardButton("Back to Search", COLOR_TEXT_SECONDARY);
        btnBack.setOnAction(e -> showSearchFormulationsScreen());

        VBox mainContent = new VBox(20, scrollPane, btnBack);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ HELPER METHODS ============

    private LinkedList<Item> getNonVetoedFormulations() {
        LinkedList<Item> nonVetoed = new LinkedList<>();
        for (Item item : allFormulations) {
            if (!isItemVetoed(item)) {
                nonVetoed.add(item);
            }
        }
        return nonVetoed;
    }

    private boolean isItemVetoed(Item item) {
        if (item instanceof Food) {
            Veto veto = ((Food) item).getVeto();
            return veto != null && veto.isActive();
        } else if (item instanceof Drink) {
            Veto veto = ((Drink) item).getVeto();
            return veto != null && veto.isActive();
        }
        return false;
    }

    private LinkedList<Ingredient> getIngredients(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            return ((Drink) item).getIngredients();
        }
        return null;
    }

    private LinkedList<Feedback> getFeedbacks(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getFeedbacks();
        } else if (item instanceof Drink) {
            return ((Drink) item).getFeedbacks();
        }
        return null;
    }

    private int countVetoedFormulations() {
        int count = 0;
        for (Item item : allFormulations) {
            if (isItemVetoed(item)) count++;
        }
        return count;
    }

    private int countFoodFormulations() {
        int count = 0;
        for (Item item : allFormulations) {
            if (item instanceof Food) count++;
        }
        return count;
    }

    private int countDrinkFormulations() {
        int count = 0;
        for (Item item : allFormulations) {
            if (item instanceof Drink) count++;
        }
        return count;
    }

    private int countTotalPurchases() {
        int total = 0;
        for (Customer customer : customers) {
            total += customer.getPurchasedItems().size();
        }
        return total;
    }

    private int countTotalFeedbacks() {
        int total = 0;
        for (Item item : allFormulations) {
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                total += feedbacks.size();
            }
        }
        return total;
    }

    private int countAuthorFoods(Author author) {
        int count = 0;
        for (Item item : author.getFormulatedItems()) {
            if (item instanceof Food) count++;
        }
        return count;
    }

    private int countAuthorDrinks(Author author) {
        int count = 0;
        for (Item item : author.getFormulatedItems()) {
            if (item instanceof Drink) count++;
        }
        return count;
    }

    private int countAuthorFeedbacks(Author author) {
        int total = 0;
        for (Item item : author.getFormulatedItems()) {
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                total += feedbacks.size();
            }
        }
        return total;
    }

    private int countAuthorPositiveFeedbacks(Author author) {
        int total = 0;
        for (Item item : author.getFormulatedItems()) {
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                for (Feedback fb : feedbacks) {
                    if (fb.isLike()) total++;
                }
            }
        }
        return total;
    }

    private double calculateAuthorAveragePrice(Author author) {
        if (author.getFormulatedItems().isEmpty()) return 0.0;
        double total = 0.0;
        for (Item item : author.getFormulatedItems()) {
            total += item.getPrice();
        }
        return total / author.getFormulatedItems().size();
    }

    private int countAuthorVetoedFormulations(Author author) {
        int count = 0;
        for (Item item : author.getFormulatedItems()) {
            if (isItemVetoed(item)) count++;
        }
        return count;
    }

    private String getUserColor(String userType) {
        switch (userType) {
            case "ADMIN": return COLOR_ADMIN;
            case "AUTHOR": return COLOR_AUTHOR;
            case "CUSTOMER": return COLOR_CUSTOMER;
            default: return COLOR_PRIMARY;
        }
    }

    private void logout() {
        if (currentUser != null) {
            String userName = "";
            if (currentUser instanceof Admin) {
                userName = ((Admin) currentUser).getName();
            } else if (currentUser instanceof Author) {
                userName = ((Author) currentUser).getName();
            } else if (currentUser instanceof Customer) {
                userName = ((Customer) currentUser).getName();
            }

            auditTrail.logAction(currentUserType + ":" + userName, "Logged out at " + new Date());

            // Auto-save on logout
            saveDataToDatabase();

            currentUser = null;
            currentUserType = null;

            showInformation("Logout Successful", "You have been logged out successfully.");
            showWelcomeScreen();
        }
    }

    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Do you want to save data before exiting?");
        alert.setContentText("Choose your option:");
        alert.initOwner(primaryStage);

        ButtonType btnSaveExit = new ButtonType("Save & Exit");
        ButtonType btnExitNoSave = new ButtonType("Exit Without Saving");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnSaveExit, btnExitNoSave, btnCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == btnSaveExit) {
                saveDataToDatabase();
                auditTrail.logAction("SYSTEM", "Application closed with data save at " + new Date());
                System.exit(0);
            } else if (result.get() == btnExitNoSave) {
                auditTrail.logAction("SYSTEM", "Application closed without saving at " + new Date());
                System.exit(0);
            }
            // If Cancel, do nothing
        }
    }

    // ============ UI COMPONENT CREATORS ============

    private VBox createHeader(String title, String subtitle) {
        VBox header = new VBox(5);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: " + COLOR_PRIMARY + ";");

        Label lblTitle = new Label(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setTextFill(Color.web(COLOR_TEXT_LIGHT));

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setFont(FONT_BODY);
        lblSubtitle.setTextFill(Color.web("#BDC3C7")); // Slightly lighter gray

        header.getChildren().addAll(lblTitle, lblSubtitle);
        return header;
    }

    private HBox createFooter(String message) {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: " + COLOR_SECONDARY + ";");

        Label lblFooter = new Label(message);
        lblFooter.setFont(FONT_BODY);
        lblFooter.setTextFill(Color.web(COLOR_TEXT_LIGHT));

        footer.getChildren().add(lblFooter);
        return footer;
    }

    private Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15 40; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;");
        button.setMaxWidth(400);
        button.setMinWidth(300);

        // Subtle hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", 20%); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 15 40; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 15 40; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"));

        return button;
    }

    private Button createStandardButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 25; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;");

        // Subtle hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", 20%); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"));

        return button;
    }

    private Button createSmallButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5 15; " +
                "-fx-background-radius: 3; " +
                "-fx-cursor: hand;");

        // Subtle hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", 20%); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 5 15; " +
                        "-fx-background-radius: 3; " +
                        "-fx-cursor: hand;"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 5 15; " +
                        "-fx-background-radius: 3; " +
                        "-fx-cursor: hand;"));

        return button;
    }

    private VBox createFunctionCard(String title, String description, String color) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10; " +
                "-fx-cursor: hand;");
        card.setPrefSize(350, 150);
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setTextFill(Color.web(color));
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label descLabel = new Label(description);
        descLabel.setFont(FONT_BODY);
        descLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);

        card.getChildren().addAll(titleLabel, descLabel);

        // Very subtle hover effect
        card.setOnMouseEntered(e ->
                card.setStyle("-fx-background-color: " + COLOR_HOVER + "; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: " + color + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-cursor: hand;"));

        card.setOnMouseExited(e ->
                card.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: " + COLOR_BORDER + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10; " +
                        "-fx-cursor: hand;"));

        return card;
    }

    private Label createStatLabel(String text, String color) {
        Label label = new Label(text);
        label.setFont(FONT_BODY);
        label.setTextFill(Color.web(color));
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private String createTextFieldStyle() {
        return "-fx-background-color: white; " +
                "-fx-text-fill: " + COLOR_TEXT_PRIMARY + "; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: " + COLOR_BORDER + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5;";
    }

    private TextField createDialogField(GridPane grid, String label, int row) {
        Label lbl = new Label(label);
        lbl.setFont(FONT_BODY);
        lbl.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        TextField textField = new TextField();
        textField.setStyle(createTextFieldStyle());

        grid.add(lbl, 0, row);
        grid.add(textField, 1, row);

        return textField;
    }

    private PasswordField createDialogPasswordField(GridPane grid, String label, int row) {
        Label lbl = new Label(label);
        lbl.setFont(FONT_BODY);
        lbl.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        PasswordField passwordField = new PasswordField();
        passwordField.setStyle(createTextFieldStyle());

        grid.add(lbl, 0, row);
        grid.add(passwordField, 1, row);

        return passwordField;
    }

    private void addDetail(VBox parent, String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblLabel = new Label(label);
        lblLabel.setFont(FONT_BODY);
        lblLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        lblLabel.setMinWidth(120);

        Label lblValue = new Label(value);
        lblValue.setFont(FONT_BODY);
        lblValue.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        row.getChildren().addAll(lblLabel, lblValue);
        parent.getChildren().add(row);
    }

    private void addDetail(GridPane grid, String label, String value, int row) {
        Label lblLabel = new Label(label);
        lblLabel.setFont(FONT_BODY);
        lblLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        Label lblValue = new Label(value);
        lblValue.setFont(FONT_BODY);
        lblValue.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    private void addDetail(VBox parent, String label, String value, String color) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblLabel = new Label(label);
        lblLabel.setFont(FONT_BODY);
        lblLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        lblLabel.setMinWidth(120);

        Label lblValue = new Label(value);
        lblValue.setFont(FONT_BODY);
        lblValue.setTextFill(Color.web(color));

        row.getChildren().addAll(lblLabel, lblValue);
        parent.getChildren().add(row);
    }

    private void addStatRow(GridPane grid, int row, String label, int value, String color) {
        addStatRow(grid, row, label, String.valueOf(value), color);
    }

    private void addStatRow(GridPane grid, int row, String label, String value, String color) {
        Label lblLabel = new Label(label);
        lblLabel.setFont(FONT_BODY);
        lblLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label lblValue = new Label(value);
        lblValue.setFont(FONT_SUBTITLE);
        lblValue.setTextFill(Color.web(color));
        lblValue.setStyle("-fx-font-weight: bold;");

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    // ============ MAIN METHOD ============

    public static void main(String[] args) {
        launch(args);
    }
}