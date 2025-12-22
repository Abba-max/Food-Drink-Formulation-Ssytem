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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Complete JavaFX GUI Application for Food & Drink Formulation Management System
 * Professional Color Theme: Blue/Gray Business Theme
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
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 700;

    // Professional Color Theme - Blue/Gray Business Theme
    private static final String COLOR_PRIMARY = "#2C3E50";     // Dark Blue/Gray - Primary
    private static final String COLOR_SECONDARY = "#34495E";   // Medium Blue/Gray - Secondary
    private static final String COLOR_ACCENT = "#2980B9";      // Corporate Blue - Accent
    private static final String COLOR_SUCCESS = "#27AE60";     // Green - Success/Actions
    private static final String COLOR_WARNING = "#F39C12";     // Orange - Warning
    private static final String COLOR_ERROR = "#E74C3C";       // Red - Error/Danger
    private static final String COLOR_INFO = "#3498DB";        // Light Blue - Information
    private static final String COLOR_LIGHT = "#ECF0F1";       // Light Gray - Backgrounds
    private static final String COLOR_NEUTRAL = "#95A5A6";     // Gray - Neutral/Disabled
    private static final String COLOR_DARK = "#2C3E50";        // Dark - Text
    private static final String COLOR_PURPLE = "#8E44AD";      // Purple - Special Actions

    // User-specific colors for dashboards
    private static final String COLOR_ADMIN = "#C0392B";       // Dark Red - Admin
    private static final String COLOR_AUTHOR = "#2980B9";      // Blue - Author
    private static final String COLOR_CUSTOMER = "#16A085";    // Teal - Customer

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Set application icon
        try {
            // You can add an icon file if available
            // primaryStage.getIcons().add(new Image("icon.png"));
        } catch (Exception e) {
            // Icon not available, continue without it
        }

        // Initialize data structures
        initializeSystem();

        // Show welcome screen
        showWelcomeScreen();

        // Configure primary stage
        primaryStage.setTitle("Food & Drink Formulation Management System");
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
     * Initialize database and load data
     */
    private void initializeSystem() {
        admins = new LinkedList<>();
        authors = new LinkedList<>();
        customers = new LinkedList<>();
        allFormulations = new LinkedList<>();
        auditTrail = new AuditTrail();

        try {
            // Initialize database connection
            if (DatabaseConfig.testConnection()) {
                databaseManager = new DatabaseManager();

                // Load data from database
                loadDataFromDatabase();

                // Create default admin if none exists
                if (admins.isEmpty()) {
                    Admin defaultAdmin = new Admin(1, "System Admin", "HQ", "+1-000-0000", "1980-01-01", "admin123");
                    admins.add(defaultAdmin);
                    databaseManager.saveAdmin(defaultAdmin);
                    auditTrail.logAction("SYSTEM", "Default admin account created");
                }
            } else {
                showError("Database Connection Failed",
                        "Could not connect to database. Please check your configuration.");
                System.exit(1);
            }
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize system: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Load data from database
     */
    private void loadDataFromDatabase() {
        try {
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

            LinkedList<Item> loadedFormulations = databaseManager.loadFormulations();
            if (loadedFormulations != null && !loadedFormulations.isEmpty()) {
                allFormulations = loadedFormulations;
            }

            AuditTrail loadedAudit = databaseManager.loadAuditTrail();
            if (loadedAudit != null) {
                auditTrail = loadedAudit;
            }

            auditTrail.logAction("SYSTEM", "Data loaded from database at " + new Date());

        } catch (Exception e) {
            showError("Data Loading Error", "Error loading data: " + e.getMessage());
        }
    }

    /**
     * Save data to database
     */
    private void saveDataToDatabase() {
        try {
            databaseManager.saveAdmins(admins);
            databaseManager.saveAuthors(authors);
            databaseManager.saveCustomers(customers);
            databaseManager.saveFormulations(allFormulations);
            databaseManager.saveAuditTrail(auditTrail);

            showInformation("Save Successful", "All data saved successfully to database");
            auditTrail.logAction("SYSTEM", "Data saved to database at " + new Date());

        } catch (Exception e) {
            showError("Save Error", "Error saving data: " + e.getMessage());
        }
    }

    // ============ WELCOME SCREEN ============

    /**
     * Show welcome screen (not logged in)
     */
    private void showWelcomeScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_PRIMARY + ", " + COLOR_SECONDARY + ");");

        // Header
        VBox header = createHeader("FOOD & DRINK FORMULATION MANAGEMENT SYSTEM",
                "Professional Formulation Management Platform");
        root.setTop(header);

        // Center content - menu buttons
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Please select an option:");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        titleLabel.setTextFill(Color.WHITE);

        Button btnLoginAdmin = createMenuButton("Login as Admin", COLOR_ADMIN);
        Button btnLoginAuthor = createMenuButton("Login as Author", COLOR_AUTHOR);
        Button btnLoginCustomer = createMenuButton("Login as Customer", COLOR_CUSTOMER);
        Button btnRegister = createMenuButton("Register as Customer", COLOR_PURPLE);
        Button btnSave = createMenuButton("Save Data", COLOR_SUCCESS);
        Button btnExit = createMenuButton("Exit", COLOR_ERROR);

        // Button actions
        btnLoginAdmin.setOnAction(e -> showLoginScreen("ADMIN"));
        btnLoginAuthor.setOnAction(e -> showLoginScreen("AUTHOR"));
        btnLoginCustomer.setOnAction(e -> showLoginScreen("CUSTOMER"));
        btnRegister.setOnAction(e -> showRegistrationScreen());
        btnSave.setOnAction(e -> saveDataToDatabase());
        btnExit.setOnAction(e -> handleExit());

        centerBox.getChildren().addAll(titleLabel, btnLoginAdmin, btnLoginAuthor,
                btnLoginCustomer, btnRegister, btnSave, btnExit);

        root.setCenter(centerBox);

        // Footer
        HBox footer = createFooter("System initialized. Default admin: Name=System Admin, Password=admin123");
        root.setBottom(footer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        if (primaryStage.getScene() != null) {
            primaryStage.setScene(currentScene);
        }
    }

    // ============ LOGIN SCREEN ============

    /**
     * Show login screen for Admin, Author, or Customer
     */
    private void showLoginScreen(String userType) {
        BorderPane root = new BorderPane();

        // Set gradient background based on user type
        String gradientColor = COLOR_SECONDARY;
        if ("ADMIN".equals(userType)) gradientColor = COLOR_ADMIN;
        else if ("AUTHOR".equals(userType)) gradientColor = COLOR_AUTHOR;
        else if ("CUSTOMER".equals(userType)) gradientColor = COLOR_CUSTOMER;

        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + gradientColor + ", " + COLOR_PRIMARY + ");");

        // Header
        VBox header = createHeader("LOGIN", userType + " Login");
        root.setTop(header);

        // Center content - login form
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 25; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 8;");

        Label lblId = new Label(userType + " Name:");
        lblId.setTextFill(Color.WHITE);
        lblId.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        TextField txtName = new TextField();
        txtName.setPromptText("Enter your Name");
        txtName.setStyle("-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8; " +
                "-fx-background-color: white; -fx-border-color: " + COLOR_NEUTRAL + ";");

        Label lblPassword = new Label("Password:");
        lblPassword.setTextFill(Color.WHITE);
        lblPassword.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setStyle("-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8; " +
                "-fx-background-color: white; -fx-border-color: " + COLOR_NEUTRAL + ";");

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 40; -fx-background-radius: 4;");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        Button btnBack = new Button("Back to Welcome");
        btnBack.setStyle("-fx-background-color: " + COLOR_NEUTRAL + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 8 30; -fx-background-radius: 4;");
        btnBack.setMaxWidth(Double.MAX_VALUE);

        // Login action
        btnLogin.setOnAction(e -> {
            String name = txtName.getText().trim();
            String password = txtPassword.getText();

            if (name.isEmpty() || password.isEmpty()) {
                showError("Validation Error", "Please fill in all fields!");
                return;
            }

            performLogin(userType, name, password);
        });

        btnBack.setOnAction(e -> showWelcomeScreen());

        formBox.getChildren().addAll(lblId, txtName, lblPassword, txtPassword, btnLogin, btnBack);

        // Center the form
        HBox centerContainer = new HBox(formBox);
        centerContainer.setAlignment(Pos.CENTER);
        root.setCenter(centerContainer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Perform login authentication
     */
    private void performLogin(String userType, String name, String password) {
        boolean loginSuccessful = false;
        String userName = "";

        switch (userType) {
            case "ADMIN":
                for (Admin admin : admins) {
                    if (admin.getName().equals(name) && admin.getPassword().equals(password)) {
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
                    if (author.getName().equals(name) && author.getPassword().equals(password)) {
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
                    if (customer.getName().equals(name) && customer.getPassword().equals(password)) {
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
            auditTrail.logAction(userType + ":" + userName, "Logged in at " + new Date());
            showInformation("Login Successful", "Welcome, " + userName + "!");
            showUserDashboard();
        } else {
            auditTrail.logAction("SYSTEM", "Failed " + userType + " login attempt for name: " + name);
            showError("Login Failed", "Invalid credentials. Please try again.");
        }
    }

    // ============ REGISTRATION SCREEN ============

    /**
     * Show customer registration screen
     */
    private void showRegistrationScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("REGISTRATION", "New Customer Registration");
        root.setTop(header);

        // Center content - registration form
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.TOP_CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(500);
        formBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 8;");

        // Form fields
        TextField txtId = createFormField(formBox, "Customer ID:", "Enter unique ID");
        TextField txtName = createFormField(formBox, "Name:", "Enter your name");
        TextField txtAddress = createFormField(formBox, "Address:", "Enter your address");
        TextField txtContact = createFormField(formBox, "Contact:", "Enter phone/email");
        TextField txtDob = createFormField(formBox, "Date of Birth:", "YYYY-MM-DD");
        TextField txtAge = createFormField(formBox, "Age:", "Enter your age");
        PasswordField txtPassword = createPasswordField(formBox, "Password:", "Minimum 4 characters");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnRegister = new Button("Register");
        btnRegister.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 4;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: " + COLOR_NEUTRAL + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 4;");

        btnRegister.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText().trim();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                int age = Integer.parseInt(txtAge.getText());
                String password = txtPassword.getText();

                // Validation
                if (name.isEmpty()) {
                    showError("Validation Error", "Name cannot be empty!");
                    return;
                }
                if (password.length() < 4) {
                    showError("Validation Error", "Password must be at least 4 characters!");
                    return;
                }
                if (age < 0 || age > 150) {
                    showError("Validation Error", "Invalid age!");
                    return;
                }

                // Check if ID exists
                for (Customer c : customers) {
                    if (c.getCustomerID() == id) {
                        showError("Registration Failed", "Customer ID already exists!");
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
                databaseManager.saveCustomer(customer);

                auditTrail.logAction("SYSTEM", "New customer registered: " + name + " (ID: " + id + ")");
                showInformation("Registration Successful",
                        "Welcome! You can now login with Name: " + name);
                showWelcomeScreen();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numbers for ID and Age");
            } catch (Exception ex) {
                showError("Registration Error", "Error during registration: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> showWelcomeScreen());

        buttonBox.getChildren().addAll(btnRegister, btnCancel);
        formBox.getChildren().add(buttonBox);

        HBox centerContainer = new HBox(formBox);
        centerContainer.setAlignment(Pos.CENTER);
        scrollPane.setContent(centerContainer);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ USER DASHBOARDS ============

    /**
     * Show dashboard based on user type
     */
    private void showUserDashboard() {
        if ("ADMIN".equals(currentUserType)) {
            showAdminDashboard();
        } else if ("AUTHOR".equals(currentUserType)) {
            showAuthorDashboard();
        } else if ("CUSTOMER".equals(currentUserType)) {
            showCustomerDashboard();
        }
    }

    /**
     * Show Admin Dashboard
     */
    private void showAdminDashboard() {
        Admin admin = (Admin) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_ADMIN + ", #A93226);");

        VBox header = createHeader("ADMIN DASHBOARD", "Welcome, " + admin.getName() + " (Administrator)");
        root.setTop(header);

        // Menu buttons
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Account Management", COLOR_PURPLE),
                createMenuButton("Formulation Management", COLOR_INFO),
                createMenuButton("Check Formulation Issues", COLOR_WARNING),
                createMenuButton("View System Statistics", COLOR_SUCCESS),
                createMenuButton("View All Accounts", COLOR_PURPLE),
                createMenuButton("View Audit Trail", COLOR_SECONDARY),
                createMenuButton("Save Data to Database", COLOR_SUCCESS),
                createMenuButton("Create Database Backup", COLOR_INFO),
                createMenuButton("Logout", COLOR_ERROR)
        };

        // Button actions
        buttons[0].setOnAction(e -> showAccountManagementScreen());
        buttons[1].setOnAction(e -> showFormulationManagementScreen());
        buttons[2].setOnAction(e -> showCheckFormulationIssuesScreen());
        buttons[3].setOnAction(e -> showSystemStatisticsScreen());
        buttons[4].setOnAction(e -> showAllAccountsScreen());
        buttons[5].setOnAction(e -> showAuditTrailScreen());
        buttons[6].setOnAction(e -> saveDataToDatabase());
        buttons[7].setOnAction(e -> showInformation("Backup", "Backup feature available. Use mysqldump for full backup."));
        buttons[8].setOnAction(e -> logout());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        HBox footer = createFooter("Admin: " + admin.getName() + " | Total Formulations: " + allFormulations.size() +
                " | Total Users: " + (admins.size() + authors.size() + customers.size()));
        root.setBottom(footer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Author Dashboard
     */
    private void showAuthorDashboard() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_AUTHOR + ", #21618C);");

        VBox header = createHeader("AUTHOR DASHBOARD", "Welcome, " + author.getName() + " (Author)");
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Create New Formulation", COLOR_SUCCESS),
                createMenuButton("Update Existing Formulation", COLOR_INFO),
                createMenuButton("View My Formulations", COLOR_PURPLE),
                createMenuButton("Check Formulation Issues", COLOR_WARNING),
                createMenuButton("View My Statistics", COLOR_SUCCESS),
                createMenuButton("Save Data to Database", COLOR_SECONDARY),
                createMenuButton("Logout", COLOR_ERROR)
        };

        // Button actions
        buttons[0].setOnAction(e -> showCreateFormulationScreen());
        buttons[1].setOnAction(e -> showUpdateFormulationScreen());
        buttons[2].setOnAction(e -> showAuthorFormulationsScreen());
        buttons[3].setOnAction(e -> showAuthorCheckIssuesScreen());
        buttons[4].setOnAction(e -> showAuthorStatisticsScreen());
        buttons[5].setOnAction(e -> saveDataToDatabase());
        buttons[6].setOnAction(e -> logout());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        HBox footer = createFooter("Author: " + author.getName() + " | My Formulations: " + author.getFormulatedItems().size());
        root.setBottom(footer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Customer Dashboard
     */
    private void showCustomerDashboard() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_CUSTOMER + ", #117864);");

        VBox header = createHeader("CUSTOMER DASHBOARD", "Welcome, " + customer.getName() + " (Customer)");
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Browse Catalog", COLOR_INFO),
                createMenuButton("My Purchases", COLOR_SUCCESS),
                createMenuButton("My Favorites", COLOR_PURPLE),
                createMenuButton("My Profile", COLOR_INFO),
                createMenuButton("Save Data to Database", COLOR_SECONDARY),
                createMenuButton("Logout", COLOR_ERROR)
        };

        // Button actions
        buttons[0].setOnAction(e -> showCustomerBrowseCatalogScreen());
        buttons[1].setOnAction(e -> showCustomerPurchasesScreen());
        buttons[2].setOnAction(e -> showCustomerFavoritesScreen());
        buttons[3].setOnAction(e -> showCustomerProfileScreen());
        buttons[4].setOnAction(e -> saveDataToDatabase());
        buttons[5].setOnAction(e -> logout());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        HBox footer = createFooter("Customer: " + customer.getName() + " | Purchases: " + customer.getPurchasedItems().size() +
                " | Favorites: " + customer.getFavoriteFormulations().size());
        root.setBottom(footer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ ADMIN FUNCTIONALITIES ============

    private void showAccountManagementScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("ACCOUNT MANAGEMENT", "Manage System Users");
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button btnCreateAuthor = createMenuButton("Create Author Account", COLOR_INFO);
        Button btnCreateAdmin = createMenuButton("Create Admin Account", COLOR_ADMIN);
        Button btnViewAuthors = createMenuButton("View All Authors", COLOR_INFO);
        Button btnViewAdmins = createMenuButton("View All Admins", COLOR_ADMIN);
        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);

        btnCreateAuthor.setOnAction(e -> showCreateAuthorScreen());
        btnCreateAdmin.setOnAction(e -> showCreateAdminScreen());
        btnViewAuthors.setOnAction(e -> showAllAuthorsScreen());
        btnViewAdmins.setOnAction(e -> showAllAdminsScreen());
        btnBack.setOnAction(e -> showAdminDashboard());

        menuBox.getChildren().addAll(btnCreateAuthor, btnCreateAdmin, btnViewAuthors, btnViewAdmins, btnBack);
        root.setCenter(menuBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showCreateAuthorScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Author Account");
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label titleLabel = new Label("Create Author Account");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(COLOR_INFO));
        vbox.getChildren().add(titleLabel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblId = new Label("Author ID:");
        TextField txtId = new TextField();
        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);

        Label lblName = new Label("Name:");
        TextField txtName = new TextField();
        grid.add(lblName, 0, 1);
        grid.add(txtName, 1, 1);

        Label lblAddress = new Label("Address:");
        TextField txtAddress = new TextField();
        grid.add(lblAddress, 0, 2);
        grid.add(txtAddress, 1, 2);

        Label lblContact = new Label("Contact:");
        TextField txtContact = new TextField();
        grid.add(lblContact, 0, 3);
        grid.add(txtContact, 1, 3);

        Label lblDob = new Label("Date of Birth:");
        TextField txtDob = new TextField();
        grid.add(lblDob, 0, 4);
        grid.add(txtDob, 1, 4);

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        grid.add(lblPassword, 0, 5);
        grid.add(txtPassword, 1, 5);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnCreate = new Button("Create");
        btnCreate.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 4;");
        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: " + COLOR_NEUTRAL + "; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 4;");

        btnCreate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText().trim();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                String password = txtPassword.getText();

                if (name.isEmpty()) {
                    showError("Error", "Author name is required!");
                    return;
                }

                // Check if ID exists
                for (Author a : authors) {
                    if (a.getAuthorID() == id) {
                        showError("Error", "Author ID already exists!");
                        return;
                    }
                }

                // Create author
                Author author = new Author(id, name, address, contact, dob);
                author.setName(name);
                author.setPassword(password);
                authors.add(author);
                databaseManager.saveAuthor(author);

                auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                        "Created author account: " + name + " (ID: " + id + ")");
                showInformation("Success", "Author account created successfully!");
                dialog.close();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numeric ID");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(vbox, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showCreateAdminScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Admin Account");
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label titleLabel = new Label("Create Admin Account");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(COLOR_ADMIN));
        vbox.getChildren().add(titleLabel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblId = new Label("Admin ID:");
        TextField txtId = new TextField();
        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);

        Label lblName = new Label("Name:");
        TextField txtName = new TextField();
        grid.add(lblName, 0, 1);
        grid.add(txtName, 1, 1);

        Label lblAddress = new Label("Address:");
        TextField txtAddress = new TextField();
        grid.add(lblAddress, 0, 2);
        grid.add(txtAddress, 1, 2);

        Label lblContact = new Label("Contact:");
        TextField txtContact = new TextField();
        grid.add(lblContact, 0, 3);
        grid.add(txtContact, 1, 3);

        Label lblDob = new Label("Date of Birth:");
        TextField txtDob = new TextField();
        grid.add(lblDob, 0, 4);
        grid.add(txtDob, 1, 4);

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        grid.add(lblPassword, 0, 5);
        grid.add(txtPassword, 1, 5);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnCreate = new Button("Create");
        btnCreate.setStyle("-fx-background-color: " + COLOR_ADMIN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 4;");
        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: " + COLOR_NEUTRAL + "; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 4;");

        btnCreate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText().trim();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                String password = txtPassword.getText();

                if (name.isEmpty()) {
                    showError("Error", "Admin name is required!");
                    return;
                }

                // Check if ID exists
                for (Admin a : admins) {
                    if (a.getAdminID() == id) {
                        showError("Error", "Admin ID already exists!");
                        return;
                    }
                }

                // Create admin
                Admin admin = new Admin(id, name, address, contact, dob, password);
                admins.add(admin);
                databaseManager.saveAdmin(admin);

                auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                        "Created admin account: " + name + " (ID: " + id + ")");
                showInformation("Success", "Admin account created successfully!");
                dialog.close();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numeric ID");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(vbox, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showFormulationManagementScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("FORMULATION MANAGEMENT", "View and Manage All Formulations");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        if (allFormulations.isEmpty()) {
            Label lblEmpty = new Label("No formulations in the system.");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : allFormulations) {
                VBox itemBox = new VBox(8);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
                lblType.setTextFill(Color.LIGHTGRAY);
                lblType.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblId = new Label("ID: " + item.getItemID());
                lblId.setTextFill(Color.LIGHTGRAY);
                lblId.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(Color.web(COLOR_SUCCESS));
                lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                // Show veto status
                boolean vetoed = isItemVetoed(item);
                if (vetoed) {
                    Label lblVeto = new Label("⚠ VETOED");
                    lblVeto.setTextFill(Color.web(COLOR_ERROR));
                    lblVeto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    lblVeto.setStyle("-fx-background-color: rgba(231, 76, 60, 0.2); -fx-padding: 2 5; -fx-background-radius: 3;");
                    itemBox.getChildren().add(lblVeto);
                }

                // Add action buttons
                HBox buttonBox = new HBox(10);
                Button btnView = createSmallButton("View Details", COLOR_INFO);
                Button btnVeto = createSmallButton(vetoed ? "Remove Veto" : "Set Veto", vetoed ? COLOR_SUCCESS : COLOR_ERROR);

                btnView.setOnAction(e -> showItemDetailsDialog(item));
                btnVeto.setOnAction(e -> {
                    if (vetoed) {
                        removeVeto(item);
                    } else {
                        setVetoOnItem(item);
                    }
                    showFormulationManagementScreen(); // Refresh
                });

                buttonBox.getChildren().addAll(btnView, btnVeto);
                itemBox.getChildren().addAll(lblName, lblType, lblId, lblPrice, buttonBox);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAdminDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showItemDetailsDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Formulation Details: " + item.getName());
        dialog.initOwner(primaryStage);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Basic info
        Label lblName = new Label(item.getName());
        lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblName.setTextFill(Color.web(COLOR_PRIMARY));
        Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
        Label lblId = new Label("ID: " + item.getItemID());
        Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        lblPrice.setTextFill(Color.web(COLOR_SUCCESS));
        lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Label lblVeto = new Label("Veto Status: " + (isItemVetoed(item) ? "VETOED" : "Not Vetoed"));
        lblVeto.setTextFill(isItemVetoed(item) ? Color.web(COLOR_ERROR) : Color.web(COLOR_SUCCESS));
        lblVeto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        vbox.getChildren().addAll(lblName, lblType, lblId, lblPrice, lblVeto);

        // Ingredients
        LinkedList<Ingredient> ingredients = getIngredients(item);
        if (ingredients != null && !ingredients.isEmpty()) {
            Label lblIngredients = new Label("\nIngredients:");
            lblIngredients.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            lblIngredients.setTextFill(Color.web(COLOR_PRIMARY));
            vbox.getChildren().add(lblIngredients);

            for (Ingredient ing : ingredients) {
                Label lblIng = new Label("  • " + ing.getName());
                if (ing.getQuantity() != null) {
                    lblIng.setText(lblIng.getText() + " - Weight: " + ing.getQuantity().getWeight() + "g, Volume: " +
                            ing.getQuantity().getVolume() + "ml");
                }
                vbox.getChildren().add(lblIng);
            }
        }

        // Feedbacks
        LinkedList<Feedback> feedbacks = getFeedbacks(item);
        if (feedbacks != null && !feedbacks.isEmpty()) {
            Label lblFeedbacks = new Label("\nFeedbacks (" + feedbacks.size() + "):");
            lblFeedbacks.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            lblFeedbacks.setTextFill(Color.web(COLOR_PRIMARY));
            vbox.getChildren().add(lblFeedbacks);

            for (Feedback fb : feedbacks) {
                Label lblFb = new Label("  " + (fb.isLike() ? "👍" : "👎") + " " +
                        fb.getConsumerName() + ": " + fb.getComment());
                lblFb.setTextFill(fb.isLike() ? Color.web(COLOR_SUCCESS) : Color.web(COLOR_ERROR));
                vbox.getChildren().add(lblFb);
            }
        }

        Button btnClose = createSmallButton("Close", COLOR_NEUTRAL);
        btnClose.setOnAction(e -> dialog.close());
        vbox.getChildren().add(btnClose);

        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 500, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void setVetoOnItem(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Set Veto on " + item.getName());
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label lblReason = new Label("Enter veto reason:");
        lblReason.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        TextArea txtReason = new TextArea();
        txtReason.setPrefRowCount(3);
        txtReason.setWrapText(true);
        txtReason.setStyle("-fx-control-inner-background: white; -fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnSet = createSmallButton("Set Veto", COLOR_ERROR);
        Button btnCancel = createSmallButton("Cancel", COLOR_NEUTRAL);

        btnSet.setOnAction(e -> {
            String reason = txtReason.getText().trim();
            if (reason.isEmpty()) {
                showError("Validation Error", "Please enter a veto reason!");
                return;
            }

            // Set veto
            MyClasses.Restrictions.Veto veto = new MyClasses.Restrictions.Veto(true, reason, new Date(), (Admin)currentUser);
            if (item instanceof Food) {
                ((Food) item).setVeto(veto);
            } else if (item instanceof Drink) {
                ((Drink) item).setVeto(veto);
            }

            databaseManager.saveItem(item);
            auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                    "Set veto on formulation: " + item.getName() + " (Reason: " + reason + ")");
            showInformation("Success", "Veto set successfully!");
            dialog.close();
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnSet, btnCancel);
        vbox.getChildren().addAll(lblReason, txtReason, buttonBox);

        Scene scene = new Scene(vbox, 400, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void removeVeto(Item item) {
        // Remove veto
        if (item instanceof Food) {
            ((Food) item).setVeto(null);
        } else if (item instanceof Drink) {
            ((Drink) item).setVeto(null);
        }

        databaseManager.saveItem(item);
        auditTrail.logAction("ADMIN:" + ((Admin)currentUser).getName(),
                "Removed veto from formulation: " + item.getName());
        showInformation("Success", "Veto removed successfully!");
    }

    private void showSystemStatisticsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("SYSTEM STATISTICS", "Overview");
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(30));

        // Statistics display
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(50);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        addStatRow(statsGrid, 0, "Total Admins:", String.valueOf(admins.size()), COLOR_ADMIN);
        addStatRow(statsGrid, 1, "Total Authors:", String.valueOf(authors.size()), COLOR_INFO);
        addStatRow(statsGrid, 2, "Total Customers:", String.valueOf(customers.size()), COLOR_SUCCESS);
        addStatRow(statsGrid, 3, "Total Formulations:", String.valueOf(allFormulations.size()), COLOR_PURPLE);

        // Count vetoed
        int vetoedCount = 0;
        for (Item item : allFormulations) {
            if (isItemVetoed(item)) vetoedCount++;
        }
        addStatRow(statsGrid, 4, "Vetoed Formulations:", String.valueOf(vetoedCount), COLOR_ERROR);

        // Count purchases
        int totalPurchases = 0;
        for (Customer customer : customers) {
            totalPurchases += customer.getPurchasedItems().size();
        }
        addStatRow(statsGrid, 5, "Total Purchases:", String.valueOf(totalPurchases), COLOR_SUCCESS);
        addStatRow(statsGrid, 6, "Audit Log Entries:", String.valueOf(auditTrail.records.size()), COLOR_SECONDARY);

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(statsGrid, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllAccountsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("ALL ACCOUNTS", "System Users");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));

        // Admins
        Label lblAdmins = new Label("ADMINISTRATORS (" + admins.size() + ")");
        lblAdmins.setTextFill(Color.web(COLOR_ADMIN));
        lblAdmins.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtAdmins = new TextArea();
        txtAdmins.setEditable(false);
        txtAdmins.setPrefRowCount(5);
        txtAdmins.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + COLOR_NEUTRAL + ";");
        StringBuilder adminText = new StringBuilder();
        for (Admin a : admins) {
            adminText.append("ID: ").append(a.getAdminID()).append(" - ").append(a.getName()).append("\n");
        }
        txtAdmins.setText(adminText.toString());

        // Authors
        Label lblAuthors = new Label("AUTHORS (" + authors.size() + ")");
        lblAuthors.setTextFill(Color.web(COLOR_INFO));
        lblAuthors.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtAuthors = new TextArea();
        txtAuthors.setEditable(false);
        txtAuthors.setPrefRowCount(5);
        txtAuthors.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + COLOR_NEUTRAL + ";");
        StringBuilder authorText = new StringBuilder();
        for (Author a : authors) {
            authorText.append("ID: ").append(a.getAuthorID()).append(" - ").append(a.getName())
                    .append(" (Formulations: ").append(a.getFormulatedItems().size()).append(")\n");
        }
        txtAuthors.setText(authorText.toString());

        // Customers
        Label lblCustomers = new Label("CUSTOMERS (" + customers.size() + ")");
        lblCustomers.setTextFill(Color.web(COLOR_SUCCESS));
        lblCustomers.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtCustomers = new TextArea();
        txtCustomers.setEditable(false);
        txtCustomers.setPrefRowCount(5);
        txtCustomers.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + COLOR_NEUTRAL + ";");
        StringBuilder customerText = new StringBuilder();
        for (Customer c : customers) {
            customerText.append("ID: ").append(c.getCustomerID()).append(" - ").append(c.getName())
                    .append(" (Purchases: ").append(c.getPurchasedItems().size()).append(")\n");
        }
        txtCustomers.setText(customerText.toString());

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(
                lblAdmins, txtAdmins,
                lblAuthors, txtAuthors,
                lblCustomers, txtCustomers,
                btnBack
        );

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllAuthorsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("ALL AUTHORS", "Author Directory");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        if (authors.isEmpty()) {
            Label lblEmpty = new Label("No authors registered yet.");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Author author : authors) {
                VBox authorBox = new VBox(5);
                authorBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label("Name: " + author.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                Label lblId = new Label("ID: " + author.getAuthorID());
                lblId.setTextFill(Color.LIGHTGRAY);
                lblId.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblFormulations = new Label("Formulations: " + author.getFormulatedItems().size());
                lblFormulations.setTextFill(Color.LIGHTGRAY);
                lblFormulations.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                authorBox.getChildren().addAll(lblName, lblId, lblFormulations);
                contentBox.getChildren().add(authorBox);
            }
        }

        Button btnBack = createMenuButton("Back", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAccountManagementScreen());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAllAdminsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("ALL ADMINISTRATORS", "Admin Directory");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        for (Admin admin : admins) {
            VBox adminBox = new VBox(5);
            adminBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                    "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

            Label lblName = new Label("Name: " + admin.getName());
            lblName.setTextFill(Color.WHITE);
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

            Label lblId = new Label("ID: " + admin.getAdminID());
            lblId.setTextFill(Color.LIGHTGRAY);
            lblId.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

            adminBox.getChildren().addAll(lblName, lblId);
            contentBox.getChildren().add(adminBox);
        }

        Button btnBack = createMenuButton("Back", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAccountManagementScreen());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showCheckFormulationIssuesScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("CHECK FORMULATION ISSUES", "Quality Control");
        root.setTop(header);

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        // Get issues
        int issueCount = 0;
        StringBuilder issuesText = new StringBuilder();
        issuesText.append("=== FORMULATION ISSUES REPORT ===\n\n");

        for (Item item : allFormulations) {
            boolean hasIssues = false;
            StringBuilder itemIssues = new StringBuilder();

            // Check ingredients
            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients == null || ingredients.isEmpty()) {
                itemIssues.append("  ⚠ Missing ingredients\n");
                hasIssues = true;
            }

            // Check price
            if (item.getPrice() <= 0) {
                itemIssues.append("  ⚠ Invalid price: $").append(item.getPrice()).append("\n");
                hasIssues = true;
            }

            // Check veto
            if (isItemVetoed(item)) {
                itemIssues.append("  ⚠ Formulation is vetoed\n");
                hasIssues = true;
            }

            // Check feedback
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                int negativeCount = 0;
                for (Feedback fb : feedbacks) {
                    if (!fb.isLike()) negativeCount++;
                }
                if (negativeCount > 0) {
                    itemIssues.append("  ⚠ Has ").append(negativeCount).append(" negative feedback(s)\n");
                    hasIssues = true;
                }
            }

            if (hasIssues) {
                issueCount++;
                issuesText.append(item.getName()).append(" (ID: ").append(item.getItemID()).append(")\n");
                issuesText.append(itemIssues.toString()).append("\n");
            }
        }

        Label lblInfo = new Label("Found " + issueCount + " formulation(s) with issues");
        lblInfo.setTextFill(issueCount > 0 ? Color.web(COLOR_WARNING) : Color.web(COLOR_SUCCESS));
        lblInfo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtIssues = new TextArea();
        txtIssues.setEditable(false);
        txtIssues.setPrefRowCount(15);
        txtIssues.setWrapText(true);
        txtIssues.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + COLOR_NEUTRAL + ";");
        txtIssues.setText(issuesText.toString());

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(lblInfo, txtIssues, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAuditTrailScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_SECONDARY + ", " + COLOR_PRIMARY + ");");

        VBox header = createHeader("AUDIT TRAIL", "System Activity Log");
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20));

        Label lblTotal = new Label("Total Log Entries: " + auditTrail.records.size());
        lblTotal.setTextFill(Color.WHITE);
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtAudit = new TextArea();
        txtAudit.setEditable(false);
        txtAudit.setPrefRowCount(20);
        txtAudit.setWrapText(true);
        txtAudit.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + COLOR_NEUTRAL + ";");

        if (auditTrail.records.isEmpty()) {
            txtAudit.setText("No audit records available.");
        } else {
            StringBuilder auditText = new StringBuilder();
            int startIndex = Math.max(0, auditTrail.records.size() - 50);

            for (int i = startIndex; i < auditTrail.records.size(); i++) {
                auditText.append(auditTrail.records.get(i)).append("\n");
            }

            if (auditTrail.records.size() > 50) {
                auditText.insert(0, "Showing last 50 of " + auditTrail.records.size() + " entries\n\n");
            }

            txtAudit.setText(auditText.toString());
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(lblTotal, txtAudit, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ AUTHOR FUNCTIONALITIES ============

    private void showCreateFormulationScreen() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Formulation");
        dialog.initOwner(primaryStage);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Tab tabFood = new Tab("Food Formulation");
        tabFood.setClosable(false);
        tabFood.setContent(createFoodFormulationForm());

        Tab tabDrink = new Tab("Drink Formulation");
        tabDrink.setClosable(false);
        tabDrink.setContent(createDrinkFormulationForm());

        tabPane.getTabs().addAll(tabFood, tabDrink);

        Scene scene = new Scene(tabPane, 800, 700);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private ScrollPane createFoodFormulationForm() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        scrollPane.setContent(vbox);

        // Title
        Label titleLabel = new Label("Create Food Formulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(COLOR_SUCCESS));
        vbox.getChildren().add(titleLabel);

        // Basic Information Section
        vbox.getChildren().add(createSectionSeparator("BASIC INFORMATION", COLOR_SUCCESS));

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(10);
        basicGrid.setVgap(10);
        basicGrid.setPadding(new Insets(10));

        TextField txtName = createGridField(basicGrid, "Food Name:", 0, true);
        TextField txtId = createGridField(basicGrid, "Food ID:", 1, true);
        TextField txtPrice = createGridField(basicGrid, "Price ($):", 2, true);
        TextField txtAvgPrice = createGridField(basicGrid, "Avg Price per Kg ($):", 3, true);
        TextField txtExpiry = createGridField(basicGrid, "Expiry Date:", 4, true);

        vbox.getChildren().add(basicGrid);

        // Ingredients Section
        vbox.getChildren().add(createSectionSeparator("INGREDIENTS", COLOR_INFO));

        VBox ingredientsBox = new VBox(5);
        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ingredient.getName()).append(" (ID: ").append(ingredient.getIngredientID()).append(")");
                    if (ingredient.getQuantity() != null) {
                        Quantity q = ingredient.getQuantity();
                        sb.append(" - Weight: ").append(q.getWeight()).append("g, ");
                        sb.append("Volume: ").append(q.getVolume()).append("ml, ");
                        sb.append("Unit: ").append(q.getUnit());
                    }
                    setText(sb.toString());
                }
            }
        });

        HBox ingredientControls = new HBox(10);
        Button btnAddIngredient = createSmallButton("Add Ingredient", COLOR_SUCCESS);
        Button btnRemoveIngredient = createSmallButton("Remove Selected", COLOR_ERROR);

        btnAddIngredient.setOnAction(e -> showAddIngredientDialog(ingredientsList));
        btnRemoveIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
            }
        });

        ingredientControls.getChildren().addAll(btnAddIngredient, btnRemoveIngredient);
        vbox.getChildren().addAll(listView, ingredientControls);

        // Lab Conditions Section
        vbox.getChildren().add(createSectionSeparator("LAB CONDITIONS", COLOR_PURPLE));

        GridPane labGrid = new GridPane();
        labGrid.setHgap(10);
        labGrid.setVgap(10);
        labGrid.setPadding(new Insets(10));

        TextField txtTemp = createGridField(labGrid, "Temperature (°C):", 0, true);
        TextField txtPressure = createGridField(labGrid, "Pressure (kPa):", 1, true);
        TextField txtMoisture = createGridField(labGrid, "Moisture (%):", 2, true);
        TextField txtVibration = createGridField(labGrid, "Vibration Level:", 3, true);
        TextField txtPeriod = createGridField(labGrid, "Time Period (min):", 4, true);

        vbox.getChildren().add(labGrid);

        // Preparation Protocol Section
        vbox.getChildren().add(createSectionSeparator("PREPARATION PROTOCOL", COLOR_INFO));

        ObservableList<String> stepsList = FXCollections.observableArrayList();
        ListView<String> stepsListView = new ListView<>(stepsList);
        stepsListView.setPrefHeight(100);
        stepsListView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox protocolControls = new HBox(10);
        Button btnAddStep = createSmallButton("Add Step", COLOR_INFO);
        Button btnRemoveStep = createSmallButton("Remove Step", COLOR_ERROR);

        btnAddStep.setOnAction(e -> {
            TextInputDialog stepDialog = new TextInputDialog();
            stepDialog.setTitle("Add Preparation Step");
            stepDialog.setHeaderText("Enter step description:");
            stepDialog.setContentText("Step:");
            Optional<String> result = stepDialog.showAndWait();
            result.ifPresent(step -> stepsList.add(step));
        });

        btnRemoveStep.setOnAction(e -> {
            String selected = stepsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stepsList.remove(selected);
            }
        });

        protocolControls.getChildren().addAll(btnAddStep, btnRemoveStep);
        vbox.getChildren().addAll(stepsListView, protocolControls);

        // Conservation Conditions Section
        vbox.getChildren().add(createSectionSeparator("CONSERVATION CONDITIONS", COLOR_SUCCESS));

        GridPane conserveGrid = new GridPane();
        conserveGrid.setHgap(10);
        conserveGrid.setVgap(10);
        conserveGrid.setPadding(new Insets(10));

        TextField txtConserveTemp = createGridField(conserveGrid, "Temperature (°C):", 0, true);
        TextField txtConserveMoisture = createGridField(conserveGrid, "Moisture (%):", 1, true);
        TextField txtContainer = createGridField(conserveGrid, "Container Type:", 2, true);

        vbox.getChildren().add(conserveGrid);

        // Consumption Conditions Section
        vbox.getChildren().add(createSectionSeparator("CONSUMPTION CONDITIONS", COLOR_PURPLE));

        GridPane consumeGrid = new GridPane();
        consumeGrid.setHgap(10);
        consumeGrid.setVgap(10);
        consumeGrid.setPadding(new Insets(10));

        TextField txtConsumeTemp = createGridField(consumeGrid, "Serving Temperature (°C):", 0, true);
        TextField txtConsumeMoisture = createGridField(consumeGrid, "Serving Moisture (%):", 1, true);

        vbox.getChildren().add(consumeGrid);

        // Standards Section
        vbox.getChildren().add(createSectionSeparator("STANDARDS", COLOR_INFO));

        ObservableList<String> standardsList = FXCollections.observableArrayList();
        ListView<String> standardsListView = new ListView<>(standardsList);
        standardsListView.setPrefHeight(100);
        standardsListView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox standardsControls = new HBox(10);
        Button btnAddStandard = createSmallButton("Add Standard", COLOR_INFO);
        Button btnRemoveStandard = createSmallButton("Remove Standard", COLOR_ERROR);

        btnAddStandard.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Standard");
            dialog.setHeaderText("Enter standard:");
            dialog.setContentText("Standard:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(standard -> standardsList.add(standard));
        });

        btnRemoveStandard.setOnAction(e -> {
            String selected = standardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                standardsList.remove(selected);
            }
        });

        standardsControls.getChildren().addAll(btnAddStandard, btnRemoveStandard);
        vbox.getChildren().addAll(standardsListView, standardsControls);

        // Consumer Profile Section
        vbox.getChildren().add(createSectionSeparator("CONSUMER PROFILE", COLOR_SUCCESS));

        TextArea txtConsumerProfile = new TextArea();
        txtConsumerProfile.setPromptText("Enter target consumer profile (e.g., 'Adults 18-65, Health-conscious, Active lifestyle')");
        txtConsumerProfile.setPrefRowCount(3);
        txtConsumerProfile.setWrapText(true);
        txtConsumerProfile.setStyle("-fx-control-inner-background: white; -fx-border-color: " + COLOR_NEUTRAL + ";");
        vbox.getChildren().add(txtConsumerProfile);

        // Create Button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnCreate = new Button("Create Food Formulation");
        btnCreate.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 4;");
        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: " + COLOR_NEUTRAL + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 4;");

        btnCreate.setOnAction(e -> {
            try {
                // Validate required fields
                if (txtName.getText().trim().isEmpty()) {
                    showError("Validation Error", "Food name is required!");
                    return;
                }

                if (txtId.getText().trim().isEmpty()) {
                    showError("Validation Error", "Food ID is required!");
                    return;
                }

                // Create food
                Food food = new Food();
                food.setName(txtName.getText().trim());
                food.setFoodID(Integer.parseInt(txtId.getText().trim()));
                food.setItemID(food.getFoodID());
                food.setPrice(Double.parseDouble(txtPrice.getText().trim()));
                food.setExpiryDate(txtExpiry.getText().trim());
                food.setAveragePricePerKg(Double.parseDouble(txtAvgPrice.getText().trim()));

                // Add ingredients
                for (Ingredient ingredient : ingredientsList) {
                    food.addIngredient(ingredient);
                }

                // Set lab conditions
                Optcondition labCond = new Optcondition();
                labCond.setTemp(Double.parseDouble(txtTemp.getText().trim()));
                labCond.setPressure(Double.parseDouble(txtPressure.getText().trim()));
                labCond.setMoisture(Double.parseDouble(txtMoisture.getText().trim()));
                labCond.setVibration(Double.parseDouble(txtVibration.getText().trim()));
                labCond.setPeriod(Integer.parseInt(txtPeriod.getText().trim()));
                food.setLabCondition(labCond);

                // Set preparation protocol
                Prepprotocol protocol = new Prepprotocol();
                for (String step : stepsList) {
                    protocol.addStep(step, null);
                }
                food.setPrepprotocol(protocol);

                // Set conservation conditions
                Conservecondition conserveCond = new Conservecondition();
                conserveCond.setTemp(Double.parseDouble(txtConserveTemp.getText().trim()));
                conserveCond.setMoisture(Double.parseDouble(txtConserveMoisture.getText().trim()));
                conserveCond.setContainer(txtContainer.getText().trim());
                food.setConservecondition(conserveCond);

                // Set consumption conditions
                Consumpcondition consumeCond = new Consumpcondition();
                consumeCond.setTemperature(Double.parseDouble(txtConsumeTemp.getText().trim()));
                consumeCond.setMoisture(Double.parseDouble(txtConsumeMoisture.getText().trim()));
                food.setConsumpcondition(consumeCond);

                // Add standards
                for (String standard : standardsList) {
                    food.addStandard(standard);
                }

                // Set consumer profile
                ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
                consumerProfile.setProfile(txtConsumerProfile.getText().trim());
                food.setConsumerProfile(consumerProfile);

                // Add author
                food.addAuthor((Author) currentUser);

                // Add to system
                ((Author) currentUser).getFormulatedItems().add(food);
                allFormulations.add(food);
                databaseManager.saveItem(food);

                auditTrail.logAction("AUTHOR:" + ((Author)currentUser).getName(),
                        "Created food formulation: " + food.getName() + " (ID: " + food.getItemID() + ")");
                showInformation("Success", "Food formulation created successfully!");
                ((Stage) btnCreate.getScene().getWindow()).close();

            } catch (NumberFormatException ex) {
                showError("Input Error", "Please enter valid numeric values for numeric fields!");
            } catch (Exception ex) {
                showError("Error", "Failed to create food formulation: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().add(buttonBox);

        return scrollPane;
    }

    private ScrollPane createDrinkFormulationForm() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        scrollPane.setContent(vbox);

        // Title
        Label titleLabel = new Label("Create Drink Formulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(COLOR_INFO));
        vbox.getChildren().add(titleLabel);

        // Basic Information Section
        vbox.getChildren().add(createSectionSeparator("BASIC INFORMATION", COLOR_INFO));

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(10);
        basicGrid.setVgap(10);
        basicGrid.setPadding(new Insets(10));

        TextField txtName = createGridField(basicGrid, "Drink Name:", 0, true);
        TextField txtId = createGridField(basicGrid, "Drink ID:", 1, true);
        TextField txtPrice = createGridField(basicGrid, "Price ($):", 2, true);
        TextField txtAvgPrice = createGridField(basicGrid, "Avg Price per Kg ($):", 3, true);
        TextField txtExpiry = createGridField(basicGrid, "Expiry Date:", 4, true);

        vbox.getChildren().add(basicGrid);

        // Ingredients Section
        vbox.getChildren().add(createSectionSeparator("INGREDIENTS", COLOR_SUCCESS));

        VBox ingredientsBox = new VBox(5);
        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ingredient.getName()).append(" (ID: ").append(ingredient.getIngredientID()).append(")");
                    if (ingredient.getQuantity() != null) {
                        Quantity q = ingredient.getQuantity();
                        sb.append(" - Weight: ").append(q.getWeight()).append("g, ");
                        sb.append("Volume: ").append(q.getVolume()).append("ml, ");
                        sb.append("Unit: ").append(q.getUnit());
                    }
                    setText(sb.toString());
                }
            }
        });

        HBox ingredientControls = new HBox(10);
        Button btnAddIngredient = createSmallButton("Add Ingredient", COLOR_SUCCESS);
        Button btnRemoveIngredient = createSmallButton("Remove Selected", COLOR_ERROR);

        btnAddIngredient.setOnAction(e -> showAddIngredientDialog(ingredientsList));
        btnRemoveIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
            }
        });

        ingredientControls.getChildren().addAll(btnAddIngredient, btnRemoveIngredient);
        vbox.getChildren().addAll(listView, ingredientControls);

        // Lab Conditions Section
        vbox.getChildren().add(createSectionSeparator("LAB CONDITIONS", COLOR_PURPLE));

        GridPane labGrid = new GridPane();
        labGrid.setHgap(10);
        labGrid.setVgap(10);
        labGrid.setPadding(new Insets(10));

        TextField txtTemp = createGridField(labGrid, "Temperature (°C):", 0, true);
        TextField txtPressure = createGridField(labGrid, "Pressure (kPa):", 1, true);
        TextField txtMoisture = createGridField(labGrid, "Moisture (%):", 2, true);
        TextField txtVibration = createGridField(labGrid, "Vibration Level:", 3, true);
        TextField txtPeriod = createGridField(labGrid, "Time Period (min):", 4, true);

        vbox.getChildren().add(labGrid);

        // Preparation Protocol Section
        vbox.getChildren().add(createSectionSeparator("PREPARATION PROTOCOL", COLOR_INFO));

        ObservableList<String> stepsList = FXCollections.observableArrayList();
        ListView<String> stepsListView = new ListView<>(stepsList);
        stepsListView.setPrefHeight(100);
        stepsListView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox protocolControls = new HBox(10);
        Button btnAddStep = createSmallButton("Add Step", COLOR_INFO);
        Button btnRemoveStep = createSmallButton("Remove Step", COLOR_ERROR);

        btnAddStep.setOnAction(e -> {
            TextInputDialog stepDialog = new TextInputDialog();
            stepDialog.setTitle("Add Preparation Step");
            stepDialog.setHeaderText("Enter step description:");
            stepDialog.setContentText("Step:");
            Optional<String> result = stepDialog.showAndWait();
            result.ifPresent(step -> stepsList.add(step));
        });

        btnRemoveStep.setOnAction(e -> {
            String selected = stepsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stepsList.remove(selected);
            }
        });

        protocolControls.getChildren().addAll(btnAddStep, btnRemoveStep);
        vbox.getChildren().addAll(stepsListView, protocolControls);

        // Conservation Conditions Section
        vbox.getChildren().add(createSectionSeparator("CONSERVATION CONDITIONS", COLOR_SUCCESS));

        GridPane conserveGrid = new GridPane();
        conserveGrid.setHgap(10);
        conserveGrid.setVgap(10);
        conserveGrid.setPadding(new Insets(10));

        TextField txtConserveTemp = createGridField(conserveGrid, "Temperature (°C):", 0, true);
        TextField txtConserveMoisture = createGridField(conserveGrid, "Moisture (%):", 1, true);
        TextField txtContainer = createGridField(conserveGrid, "Container Type:", 2, true);

        vbox.getChildren().add(conserveGrid);

        // Consumption Conditions Section
        vbox.getChildren().add(createSectionSeparator("CONSUMPTION CONDITIONS", COLOR_PURPLE));

        GridPane consumeGrid = new GridPane();
        consumeGrid.setHgap(10);
        consumeGrid.setVgap(10);
        consumeGrid.setPadding(new Insets(10));

        TextField txtConsumeTemp = createGridField(consumeGrid, "Serving Temperature (°C):", 0, true);
        TextField txtConsumeMoisture = createGridField(consumeGrid, "Serving Moisture (%):", 1, true);

        vbox.getChildren().add(consumeGrid);

        // Standards Section
        vbox.getChildren().add(createSectionSeparator("STANDARDS", COLOR_INFO));

        ObservableList<String> standardsList = FXCollections.observableArrayList();
        ListView<String> standardsListView = new ListView<>(standardsList);
        standardsListView.setPrefHeight(100);
        standardsListView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox standardsControls = new HBox(10);
        Button btnAddStandard = createSmallButton("Add Standard", COLOR_INFO);
        Button btnRemoveStandard = createSmallButton("Remove Standard", COLOR_ERROR);

        btnAddStandard.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Standard");
            dialog.setHeaderText("Enter standard:");
            dialog.setContentText("Standard:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(standard -> standardsList.add(standard));
        });

        btnRemoveStandard.setOnAction(e -> {
            String selected = standardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                standardsList.remove(selected);
            }
        });

        standardsControls.getChildren().addAll(btnAddStandard, btnRemoveStandard);
        vbox.getChildren().addAll(standardsListView, standardsControls);

        // Consumer Profile Section
        vbox.getChildren().add(createSectionSeparator("CONSUMER PROFILE", COLOR_SUCCESS));

        TextArea txtConsumerProfile = new TextArea();
        txtConsumerProfile.setPromptText("Enter target consumer profile (e.g., 'Adults 18-65, Health-conscious, Active lifestyle')");
        txtConsumerProfile.setPrefRowCount(3);
        txtConsumerProfile.setWrapText(true);
        txtConsumerProfile.setStyle("-fx-control-inner-background: white; -fx-border-color: " + COLOR_NEUTRAL + ";");
        vbox.getChildren().add(txtConsumerProfile);

        // Create Button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnCreate = new Button("Create Drink Formulation");
        btnCreate.setStyle("-fx-background-color: " + COLOR_INFO + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 4;");
        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: " + COLOR_NEUTRAL + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 4;");

        btnCreate.setOnAction(e -> {
            try {
                // Validate required fields
                if (txtName.getText().trim().isEmpty()) {
                    showError("Validation Error", "Drink name is required!");
                    return;
                }

                if (txtId.getText().trim().isEmpty()) {
                    showError("Validation Error", "Drink ID is required!");
                    return;
                }

                // Create drink
                Drink drink = new Drink();
                drink.setName(txtName.getText().trim());
                drink.setDrinkID(Integer.parseInt(txtId.getText().trim()));
                drink.setItemID(drink.getDrinkID());
                drink.setPrice(Double.parseDouble(txtPrice.getText().trim()));
                drink.setExpiryDate(txtExpiry.getText().trim());
                drink.setAveragePricePerKg(Double.parseDouble(txtAvgPrice.getText().trim()));

                // Add ingredients
                for (Ingredient ingredient : ingredientsList) {
                    drink.addIngredient(ingredient);
                }

                // Set lab conditions
                Optcondition labCond = new Optcondition();
                labCond.setTemp(Double.parseDouble(txtTemp.getText().trim()));
                labCond.setPressure(Double.parseDouble(txtPressure.getText().trim()));
                labCond.setMoisture(Double.parseDouble(txtMoisture.getText().trim()));
                labCond.setVibration(Double.parseDouble(txtVibration.getText().trim()));
                labCond.setPeriod(Integer.parseInt(txtPeriod.getText().trim()));
                drink.setLabCondition(labCond);

                // Set preparation protocol
                Prepprotocol protocol = new Prepprotocol();
                for (String step : stepsList) {
                    protocol.addStep(step, null);
                }
                drink.setPrepprotocol(protocol);

                // Set conservation conditions
                Conservecondition conserveCond = new Conservecondition();
                conserveCond.setTemp(Double.parseDouble(txtConserveTemp.getText().trim()));
                conserveCond.setMoisture(Double.parseDouble(txtConserveMoisture.getText().trim()));
                conserveCond.setContainer(txtContainer.getText().trim());
                drink.setConservecondition(conserveCond);

                // Set consumption conditions
                Consumpcondition consumeCond = new Consumpcondition();
                consumeCond.setTemperature(Double.parseDouble(txtConsumeTemp.getText().trim()));
                consumeCond.setMoisture(Double.parseDouble(txtConsumeMoisture.getText().trim()));
                drink.setConsumpcondition(consumeCond);

                // Add standards
                for (String standard : standardsList) {
                    drink.addStandard(standard);
                }

                // Set consumer profile
                ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
                consumerProfile.setProfile(txtConsumerProfile.getText().trim());
                drink.setConsumerProfile(consumerProfile);

                // Add author
                drink.addAuthor((Author) currentUser);

                // Add to system
                ((Author) currentUser).getFormulatedItems().add(drink);
                allFormulations.add(drink);
                databaseManager.saveItem(drink);

                auditTrail.logAction("AUTHOR:" + ((Author)currentUser).getName(),
                        "Created drink formulation: " + drink.getName() + " (ID: " + drink.getItemID() + ")");
                showInformation("Success", "Drink formulation created successfully!");
                ((Stage) btnCreate.getScene().getWindow()).close();

            } catch (NumberFormatException ex) {
                showError("Input Error", "Please enter valid numeric values for numeric fields!");
            } catch (Exception ex) {
                showError("Error", "Failed to create drink formulation: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().add(buttonBox);

        return scrollPane;
    }

    private void showAddIngredientDialog(ObservableList<Ingredient> ingredientsList) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Ingredient");
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label titleLabel = new Label("Add New Ingredient");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(COLOR_SUCCESS));
        vbox.getChildren().add(titleLabel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtId = createGridField(grid, "Ingredient ID:", 0, true);
        TextField txtName = createGridField(grid, "Name:", 1, true);
        TextField txtWeight = createGridField(grid, "Weight (g):", 2, true);
        TextField txtVolume = createGridField(grid, "Volume (ml):", 3, true);
        TextField txtFraction = createGridField(grid, "Fraction (0.0-1.0):", 4, true);
        TextField txtUnit = createGridField(grid, "Unit (e.g., grams, ml):", 5, true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnAdd = createSmallButton("Add Ingredient", COLOR_SUCCESS);
        Button btnCancel = createSmallButton("Cancel", COLOR_NEUTRAL);

        btnAdd.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();

                if (name.isEmpty()) {
                    showError("Validation Error", "Ingredient name is required!");
                    return;
                }

                double weight = txtWeight.getText().isEmpty() ? 0.0 : Double.parseDouble(txtWeight.getText().trim());
                double volume = txtVolume.getText().isEmpty() ? 0.0 : Double.parseDouble(txtVolume.getText().trim());
                double fraction = txtFraction.getText().isEmpty() ? 0.0 : Double.parseDouble(txtFraction.getText().trim());
                String unit = txtUnit.getText().trim();

                if (fraction < 0.0 || fraction > 1.0) {
                    showError("Validation Error", "Fraction must be between 0.0 and 1.0!");
                    return;
                }

                Quantity quantity = new Quantity(weight, volume, fraction, unit);
                Ingredient ingredient = new Ingredient(id, name, quantity);
                ingredientsList.add(ingredient);
                dialog.close();

            } catch (NumberFormatException ex) {
                showError("Input Error", "Please enter valid numeric values for weight, volume, and fraction!");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnAdd, btnCancel);
        vbox.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(vbox, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showUpdateFormulationScreen() {
        Author author = (Author) currentUser;

        if (author.getFormulatedItems().isEmpty()) {
            showInformation("No Formulations", "You haven't created any formulations yet.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Formulation");
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label titleLabel = new Label("Select formulation to update:");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(COLOR_INFO));
        vbox.getChildren().add(titleLabel);

        ComboBox<Item> comboFormulations = new ComboBox<>();
        ObservableList<Item> items = FXCollections.observableArrayList(author.getFormulatedItems());
        comboFormulations.setItems(items);
        comboFormulations.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");
        comboFormulations.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (ID: " + item.getItemID() + ", Type: " +
                            (item instanceof Food ? "Food" : "Drink") + ")");
                }
            }
        });

        Button btnUpdate = createSmallButton("Update Selected", COLOR_INFO);
        Button btnCancel = createSmallButton("Cancel", COLOR_NEUTRAL);

        btnUpdate.setOnAction(e -> {
            Item selected = comboFormulations.getValue();
            if (selected != null) {
                dialog.close();
                showFormulationUpdateDialog(selected);
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(comboFormulations, btnUpdate, btnCancel);
        Scene scene = new Scene(vbox, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showFormulationUpdateDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update: " + item.getName());
        dialog.initOwner(primaryStage);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Tab tabBasic = new Tab("Basic Info");
        tabBasic.setClosable(false);
        tabBasic.setContent(createBasicUpdateForm(item));

        Tab tabIngredients = new Tab("Ingredients");
        tabIngredients.setClosable(false);
        tabIngredients.setContent(createIngredientsUpdateForm(item));

        Tab tabStandards = new Tab("Standards");
        tabStandards.setClosable(false);
        tabStandards.setContent(createStandardsUpdateForm(item));

        tabPane.getTabs().addAll(tabBasic, tabIngredients, tabStandards);

        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 4;");
        btnSave.setOnAction(e -> {
            databaseManager.saveItem(item);
            auditTrail.logAction("AUTHOR:" + ((Author)currentUser).getName(),
                    "Updated formulation: " + item.getName());
            showInformation("Success", "Formulation updated successfully!");
            dialog.close();
        });

        VBox vbox = new VBox(10, tabPane, btnSave);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Scene scene = new Scene(vbox, 600, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private VBox createBasicUpdateForm(Item item) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField txtName = createFormField(vbox, "Name:", item.getName());
        TextField txtPrice = createFormField(vbox, "Price ($):", String.valueOf(item.getPrice()));
        TextField txtExpiry = createFormField(vbox, "Expiry Date (YYYY-MM-DD):", item.getExpiryDate());

        if (item instanceof Food) {
            Food food = (Food) item;
            TextField txtAvgPrice = createFormField(vbox, "Avg Price per Kg ($):", String.valueOf(food.getAveragePricePerKg()));
            txtAvgPrice.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    food.setAveragePricePerKg(Double.parseDouble(newVal));
                } catch (Exception e) {}
            });
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            TextField txtAvgPrice = createFormField(vbox, "Avg Price per Kg ($):", String.valueOf(drink.getAveragePricePerKg()));
            txtAvgPrice.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    drink.setAveragePricePerKg(Double.parseDouble(newVal));
                } catch (Exception e) {}
            });
        }

        txtName.textProperty().addListener((obs, oldVal, newVal) -> item.setName(newVal));
        txtPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                item.setPrice(Double.parseDouble(newVal));
            } catch (Exception e) {}
        });
        txtExpiry.textProperty().addListener((obs, oldVal, newVal) -> item.setExpiryDate(newVal));

        return vbox;
    }

    private VBox createIngredientsUpdateForm(Item item) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        LinkedList<Ingredient> ingredients = getIngredients(item);
        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        if (ingredients != null) {
            ingredientsList.addAll(ingredients);
        }

        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    setText(ingredient.getName() + " (ID: " + ingredient.getIngredientID() + ")");
                }
            }
        });

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Ingredient", COLOR_SUCCESS);
        Button btnRemove = createSmallButton("Remove Selected", COLOR_ERROR);
        Button btnEdit = createSmallButton("Edit Selected", COLOR_INFO);

        btnAdd.setOnAction(e -> showAddIngredientDialog(ingredientsList));
        btnRemove.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
                // Update the actual item
                if (item instanceof Food) {
                    ((Food) item).removeIngredient(selected.getIngredientID());
                } else if (item instanceof Drink) {
                    ((Drink) item).removeIngredient(selected.getIngredientID());
                }
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove, btnEdit);
        vbox.getChildren().addAll(listView, buttonBox);

        return vbox;
    }

    private VBox createStandardsUpdateForm(Item item) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        LinkedList<String> standards = null;
        if (item instanceof Food) {
            standards = ((Food) item).getStandards();
        } else if (item instanceof Drink) {
            standards = ((Drink) item).getStandards();
        }

        ObservableList<String> standardsList = FXCollections.observableArrayList();
        if (standards != null) {
            standardsList.addAll(standards);
        }

        ListView<String> listView = new ListView<>(standardsList);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Standard", COLOR_INFO);
        Button btnRemove = createSmallButton("Remove Selected", COLOR_ERROR);

        btnAdd.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Standard");
            dialog.setHeaderText("Enter standard:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(standard -> {
                standardsList.add(standard);
                // Update the actual item
                if (item instanceof Food) {
                    ((Food) item).addStandard(standard);
                } else if (item instanceof Drink) {
                    ((Drink) item).addStandard(standard);
                }
            });
        });

        btnRemove.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                standardsList.remove(selected);
                // Update the actual item
                if (item instanceof Food) {
                    ((Food) item).getStandards().remove(selected);
                } else if (item instanceof Drink) {
                    ((Drink) item).getStandards().remove(selected);
                }
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove);
        vbox.getChildren().addAll(listView, buttonBox);

        return vbox;
    }

    private void showAuthorFormulationsScreen() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_AUTHOR + ", #21618C);");

        VBox header = createHeader("MY FORMULATIONS", "Author: " + author.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        if (author.getFormulatedItems().isEmpty()) {
            Label lblEmpty = new Label("You haven't created any formulations yet.");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : author.getFormulatedItems()) {
                VBox itemBox = new VBox(8);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
                lblType.setTextFill(Color.LIGHTGRAY);
                lblType.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblId = new Label("ID: " + item.getItemID());
                lblId.setTextFill(Color.LIGHTGRAY);
                lblId.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(Color.web(COLOR_SUCCESS));
                lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                // Feedbacks
                LinkedList<Feedback> feedbacks = getFeedbacks(item);
                if (feedbacks != null && !feedbacks.isEmpty()) {
                    Label lblFeedbacks = new Label("Feedbacks: " + feedbacks.size());
                    lblFeedbacks.setTextFill(Color.LIGHTGRAY);
                    lblFeedbacks.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
                    itemBox.getChildren().add(lblFeedbacks);
                }

                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAuthorDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAuthorCheckIssuesScreen() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_AUTHOR + ", #21618C);");

        VBox header = createHeader("CHECK MY FORMULATION ISSUES", "Author: " + author.getName());
        root.setTop(header);

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        if (author.getFormulatedItems().isEmpty()) {
            Label lblEmpty = new Label("No formulations to check.");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            int issueCount = 0;
            StringBuilder issuesText = new StringBuilder();
            issuesText.append("=== MY FORMULATION ISSUES ===\n\n");

            for (Item item : author.getFormulatedItems()) {
                boolean hasIssues = false;
                StringBuilder itemIssues = new StringBuilder();

                // Check ingredients
                LinkedList<Ingredient> ingredients = getIngredients(item);
                if (ingredients == null || ingredients.isEmpty()) {
                    itemIssues.append("  ⚠ Missing ingredients\n");
                    hasIssues = true;
                }

                // Check price
                if (item.getPrice() <= 0) {
                    itemIssues.append("  ⚠ Invalid price\n");
                    hasIssues = true;
                }

                // Check veto
                if (isItemVetoed(item)) {
                    itemIssues.append("  ⚠ Formulation is vetoed\n");
                    hasIssues = true;
                }

                // Check feedback
                LinkedList<Feedback> feedbacks = getFeedbacks(item);
                if (feedbacks != null) {
                    int negativeCount = 0;
                    for (Feedback fb : feedbacks) {
                        if (!fb.isLike()) negativeCount++;
                    }
                    if (negativeCount > 0) {
                        itemIssues.append("  ⚠ Has ").append(negativeCount).append(" negative feedback(s)\n");
                        hasIssues = true;
                    }
                }

                if (hasIssues) {
                    issueCount++;
                    issuesText.append(item.getName()).append("\n");
                    issuesText.append(itemIssues.toString()).append("\n");
                }
            }

            Label lblInfo = new Label("Found " + issueCount + " formulation(s) with issues");
            lblInfo.setTextFill(issueCount > 0 ? Color.web(COLOR_WARNING) : Color.web(COLOR_SUCCESS));
            lblInfo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

            TextArea txtIssues = new TextArea();
            txtIssues.setEditable(false);
            txtIssues.setPrefRowCount(15);
            txtIssues.setWrapText(true);
            txtIssues.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + COLOR_NEUTRAL + ";");
            txtIssues.setText(issuesText.toString());

            contentBox.getChildren().addAll(lblInfo, txtIssues);
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAuthorDashboard());
        contentBox.getChildren().add(btnBack);

        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showAuthorStatisticsScreen() {
        Author author = (Author) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_AUTHOR + ", #21618C);");

        VBox header = createHeader("MY STATISTICS", "Author: " + author.getName());
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(50);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        addStatRow(statsGrid, 0, "Total Formulations:", String.valueOf(author.getFormulatedItems().size()), COLOR_INFO);

        int foodCount = 0;
        int drinkCount = 0;
        for (Item item : author.getFormulatedItems()) {
            if (item instanceof Food) foodCount++;
            else if (item instanceof Drink) drinkCount++;
        }
        addStatRow(statsGrid, 1, "Food Items:", String.valueOf(foodCount), COLOR_SUCCESS);
        addStatRow(statsGrid, 2, "Drink Items:", String.valueOf(drinkCount), COLOR_INFO);

        // Calculate total feedbacks
        int totalFeedbacks = 0;
        int positiveFeedbacks = 0;
        for (Item item : author.getFormulatedItems()) {
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                totalFeedbacks += feedbacks.size();
                for (Feedback fb : feedbacks) {
                    if (fb.isLike()) positiveFeedbacks++;
                }
            }
        }
        addStatRow(statsGrid, 3, "Total Feedbacks:", String.valueOf(totalFeedbacks), COLOR_PURPLE);
        addStatRow(statsGrid, 4, "Positive Feedbacks:", String.valueOf(positiveFeedbacks), COLOR_SUCCESS);

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showAuthorDashboard());

        contentBox.getChildren().addAll(statsGrid, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ CUSTOMER FUNCTIONALITIES ============

    private void showCustomerBrowseCatalogScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_CUSTOMER + ", #117864);");

        VBox header = createHeader("BROWSE CATALOG", "Customer: " + customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        LinkedList<Item> availableItems = customer.getAvailableFormulations();

        if (availableItems == null || availableItems.isEmpty()) {
            Label lblEmpty = new Label("No formulations available at this time.");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : availableItems) {
                VBox itemBox = new VBox(8);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
                lblType.setTextFill(Color.LIGHTGRAY);
                lblType.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(Color.web(COLOR_SUCCESS));
                lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                boolean purchased = customer.isPaid(item);
                Label lblStatus = new Label(purchased ? "✓ PURCHASED" : "🔒 Not Purchased");
                lblStatus.setTextFill(purchased ? Color.web(COLOR_SUCCESS) : Color.web(COLOR_ERROR));
                lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                HBox buttonBox = new HBox(10);
                Button btnView = createSmallButton("View Details", COLOR_INFO);
                Button btnPurchase = createSmallButton(purchased ? "Purchased" : "Purchase", purchased ? COLOR_NEUTRAL : COLOR_SUCCESS);
                btnPurchase.setDisable(purchased);

                btnView.setOnAction(e -> showCustomerItemDetails(item, purchased));
                btnPurchase.setOnAction(e -> {
                    showPurchaseDialog(item);
                    showCustomerBrowseCatalogScreen(); // Refresh
                });

                buttonBox.getChildren().addAll(btnView, btnPurchase);
                itemBox.getChildren().addAll(lblName, lblType, lblPrice, lblStatus, buttonBox);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showCustomerDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showCustomerItemDetails(Item item, boolean purchased) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(item.getName());
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label lblName = new Label(item.getName());
        lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblName.setTextFill(Color.web(COLOR_INFO));
        Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
        Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblPrice.setTextFill(Color.web(COLOR_SUCCESS));

        vbox.getChildren().addAll(lblName, lblType, lblPrice);

        if (purchased) {
            // Show full details for purchased items
            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients != null && !ingredients.isEmpty()) {
                Label lblIngredients = new Label("\nIngredients:");
                lblIngredients.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                lblIngredients.setTextFill(Color.web(COLOR_PRIMARY));
                vbox.getChildren().add(lblIngredients);

                for (Ingredient ing : ingredients) {
                    Label lblIng = new Label("  • " + ing.getName());
                    vbox.getChildren().add(lblIng);
                }
            }

            // Add feedback button
            Button btnFeedback = createSmallButton("Provide Feedback", COLOR_INFO);
            btnFeedback.setOnAction(e -> {
                dialog.close();
                showFeedbackDialog(item);
            });
            vbox.getChildren().add(btnFeedback);
        } else {
            Label lblLocked = new Label("\n🔒 Full details available after purchase");
            lblLocked.setTextFill(Color.web(COLOR_ERROR));
            vbox.getChildren().add(lblLocked);
        }

        Button btnClose = createSmallButton("Close", COLOR_NEUTRAL);
        btnClose.setOnAction(e -> dialog.close());
        vbox.getChildren().add(btnClose);

        Scene scene = new Scene(vbox, 400, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showPurchaseDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Purchase: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label lblItem = new Label("Item: " + item.getName());
        lblItem.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblPrice.setTextFill(Color.web(COLOR_SUCCESS));

        Label lblMethod = new Label("Select Payment Method:");
        ComboBox<String> comboMethod = new ComboBox<>();
        comboMethod.getItems().addAll("Credit Card", "Debit Card", "Mobile Payment", "Cash");
        comboMethod.setValue("Credit Card");
        comboMethod.setStyle("-fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnPurchase = createSmallButton("Confirm Purchase", COLOR_SUCCESS);
        Button btnCancel = createSmallButton("Cancel", COLOR_NEUTRAL);

        btnPurchase.setOnAction(e -> {
            Customer customer = (Customer) currentUser;
            String paymentMethod = comboMethod.getValue();

            // Simulate payment processing
            if (customer.makePayment(item, paymentMethod)) {
                databaseManager.saveCustomer(customer);
                auditTrail.logAction("CUSTOMER:" + customer.getName(),
                        "Purchased: " + item.getName() + " for $" + item.getPrice());
                showInformation("Success", "Purchase completed successfully!");
                dialog.close();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnPurchase, btnCancel);
        vbox.getChildren().addAll(lblItem, lblPrice, lblMethod, comboMethod, buttonBox);

        Scene scene = new Scene(vbox, 300, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showFeedbackDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Feedback for: " + item.getName());
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");

        Label lblQuestion = new Label("Did you like this formulation?");
        lblQuestion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        ToggleGroup group = new ToggleGroup();
        RadioButton rbLike = new RadioButton("👍 Like");
        RadioButton rbDislike = new RadioButton("👎 Dislike");
        rbLike.setToggleGroup(group);
        rbDislike.setToggleGroup(group);
        rbLike.setSelected(true);

        Label lblComment = new Label("Comments:");
        TextArea txtComment = new TextArea();
        txtComment.setPrefRowCount(3);
        txtComment.setWrapText(true);
        txtComment.setStyle("-fx-control-inner-background: white; -fx-border-color: " + COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnSubmit = createSmallButton("Submit Feedback", COLOR_SUCCESS);
        Button btnCancel = createSmallButton("Cancel", COLOR_NEUTRAL);

        btnSubmit.setOnAction(e -> {
            boolean like = rbLike.isSelected();
            String comment = txtComment.getText().trim();

            if (comment.isEmpty()) {
                comment = like ? "Liked this formulation" : "Did not like this formulation";
            }

            Customer customer = (Customer) currentUser;
            Feedback feedback = customer.provideFeedback(item, comment, like);

            if (feedback != null) {
                databaseManager.saveItem(item);
                auditTrail.logAction("CUSTOMER:" + customer.getName(),
                        "Provided feedback on: " + item.getName() + " (" + (like ? "Like" : "Dislike") + ")");
                showInformation("Thank You", "Your feedback has been submitted!");
                dialog.close();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(lblQuestion, rbLike, rbDislike, lblComment, txtComment, buttonBox);
        buttonBox.getChildren().addAll(btnSubmit, btnCancel);

        Scene scene = new Scene(vbox, 400, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showCustomerPurchasesScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_CUSTOMER + ", #117864);");

        VBox header = createHeader("MY PURCHASES", "Customer: " + customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        if (customer.getPurchasedItems().isEmpty()) {
            Label lblEmpty = new Label("You haven't purchased any items yet.");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Customer.PurchaseRecord record : customer.getPurchasedItems().values()) {
                VBox recordBox = new VBox(5);
                recordBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblItem = new Label(record.getItemName());
                lblItem.setTextFill(Color.WHITE);
                lblItem.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", record.getPrice()));
                lblPrice.setTextFill(Color.web(COLOR_SUCCESS));

                Label lblDate = new Label("Date: " + record.getPurchaseDate());
                lblDate.setTextFill(Color.LIGHTGRAY);
                lblDate.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblMethod = new Label("Method: " + record.getPaymentMethod());
                lblMethod.setTextFill(Color.LIGHTGRAY);
                lblMethod.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                recordBox.getChildren().addAll(lblItem, lblPrice, lblDate, lblMethod);
                contentBox.getChildren().add(recordBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showCustomerDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showCustomerFavoritesScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_CUSTOMER + ", #117864);");

        VBox header = createHeader("MY FAVORITES", "Customer: " + customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        LinkedList<Item> favorites = customer.getFavoriteFormulations();

        if (favorites.isEmpty()) {
            Label lblEmpty = new Label("No favorites yet. Browse the catalog to add favorites!");
            lblEmpty.setTextFill(Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : favorites) {
                VBox itemBox = new VBox(5);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(Color.web(COLOR_SUCCESS));

                boolean purchased = customer.isPaid(item);
                Label lblStatus = new Label(purchased ? "✓ Purchased" : "🔒 Not Purchased");
                lblStatus.setTextFill(purchased ? Color.web(COLOR_SUCCESS) : Color.web(COLOR_ERROR));
                lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                HBox buttonBox = new HBox(10);
                Button btnView = createSmallButton("View", COLOR_INFO);
                Button btnRemove = createSmallButton("Remove", COLOR_ERROR);

                btnView.setOnAction(e -> showCustomerItemDetails(item, purchased));
                btnRemove.setOnAction(e -> {
                    customer.removeFromFavorites(item);
                    databaseManager.saveCustomer(customer);
                    showCustomerFavoritesScreen(); // Refresh
                });

                buttonBox.getChildren().addAll(btnView, btnRemove);
                itemBox.getChildren().addAll(lblName, lblPrice, lblStatus, buttonBox);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showCustomerDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    private void showCustomerProfileScreen() {
        Customer customer = (Customer) currentUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " + COLOR_CUSTOMER + ", #117864);");

        VBox header = createHeader("MY PROFILE", "Customer: " + customer.getName());
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(30);
        profileGrid.setVgap(15);
        profileGrid.setAlignment(Pos.CENTER);

        addStatRow(profileGrid, 0, "Customer ID:", String.valueOf(customer.getCustomerID()), COLOR_INFO);
        addStatRow(profileGrid, 1, "Name:", customer.getName(), COLOR_INFO);
        addStatRow(profileGrid, 2, "Age:", String.valueOf(customer.getAge()), COLOR_SUCCESS);
        addStatRow(profileGrid, 3, "Contact:", customer.getContact() != null ? customer.getContact() : "N/A", COLOR_INFO);
        addStatRow(profileGrid, 4, "Total Purchases:", String.valueOf(customer.getPurchasedItems().size()), COLOR_SUCCESS);
        addStatRow(profileGrid, 5, "Total Favorites:", String.valueOf(customer.getFavoriteFormulations().size()), COLOR_PURPLE);
        addStatRow(profileGrid, 6, "Feedback Given:", String.valueOf(customer.getFeedbackHistory().size()), COLOR_INFO);

        Button btnBack = createMenuButton("Back to Dashboard", COLOR_NEUTRAL);
        btnBack.setOnAction(e -> showCustomerDashboard());

        contentBox.getChildren().addAll(profileGrid, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ HELPER METHODS ============

    /**
     * Get non-vetoed formulations for customers
     */
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
            return ((Food) item).isVetoed();
        } else if (item instanceof Drink) {
            return ((Drink) item).isVetoed();
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

    /**
     * Logout current user
     */
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

    /**
     * Handle application exit
     */
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
            // If Cancel, do nothing (alert closes)
        }
    }

    // ============ UI COMPONENT CREATORS ============

    /**
     * Create header section
     */
    private VBox createHeader(String title, String subtitle) {
        VBox header = new VBox(5);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.WHITE);

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubtitle.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(lblTitle, lblSubtitle);
        return header;
    }

    /**
     * Create footer section
     */
    private HBox createFooter(String message) {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Label lblFooter = new Label(message);
        lblFooter.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblFooter.setTextFill(Color.LIGHTGRAY);

        footer.getChildren().add(lblFooter);
        return footer;
    }

    /**
     * Create menu button
     */
    private Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                "-fx-background-radius: 4; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        button.setMaxWidth(400);
        button.setMinWidth(300);

        // Hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                        "-fx-background-radius: 4; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                        "-fx-background-radius: 4; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));

        return button;
    }

    private Button createSmallButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; " +
                "-fx-background-radius: 3; -fx-cursor: hand;");

        // Hover effect for small buttons
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; " +
                        "-fx-background-radius: 3; -fx-cursor: hand;"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; " +
                        "-fx-background-radius: 3; -fx-cursor: hand;"));

        return button;
    }

    /**
     * Create form field
     */
    private TextField createFormField(VBox parent, String labelText, String promptText) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web(COLOR_DARK));
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        TextField textField = new TextField(promptText);
        textField.setStyle("-fx-font-size: 14px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + COLOR_NEUTRAL + ";");

        parent.getChildren().addAll(label, textField);
        return textField;
    }

    private TextField createGridField(GridPane grid, String labelText, int row, boolean withLabel) {
        if (withLabel) {
            Label label = new Label(labelText);
            label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            grid.add(label, 0, row);
        }
        TextField textField = new TextField();
        textField.setStyle("-fx-font-size: 12px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + COLOR_NEUTRAL + ";");
        grid.add(textField, 1, row);
        return textField;
    }

    /**
     * Create password field
     */
    private PasswordField createPasswordField(VBox parent, String labelText, String promptText) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web(COLOR_DARK));
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + COLOR_NEUTRAL + ";");

        parent.getChildren().addAll(label, passwordField);
        return passwordField;
    }

    /**
     * Add statistic row to grid with color
     */
    private void addStatRow(GridPane grid, int row, String label, String value, String color) {
        Label lblLabel = new Label(label);
        lblLabel.setTextFill(Color.web(COLOR_DARK));
        lblLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label lblValue = new Label(value);
        lblValue.setTextFill(Color.web(color));
        lblValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    /**
     * Create section separator
     */
    private VBox createSectionSeparator(String title, String color) {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        Label label = new Label(title);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.setTextFill(Color.web(color));

        VBox section = new VBox(5, label, separator);
        return section;
    }

    /**
     * Show information dialog
     */
    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    /**
     * Show error dialog
     */
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
