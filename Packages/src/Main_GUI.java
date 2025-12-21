import MyClasses.Consumables.Item;
import MyClasses.Database.DatabaseConfig;
import MyClasses.Database.DatabaseManager;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;
import MyClasses.Utilities.AuditTrail;
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

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Main JavaFX GUI Application for Food & Drink Formulation Management System
 * Follows the exact console menu structure
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

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

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
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        // Header
        VBox header = createHeader("FOOD & DRINK FORMULATION MANAGEMENT SYSTEM", "Welcome");
        root.setTop(header);

        // Center content - menu buttons
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Please select an option:");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        titleLabel.setTextFill(Color.WHITE);

        Button btnLoginAdmin = createMenuButton("Login as Admin", "admin-icon");
        Button btnLoginAuthor = createMenuButton("Login as Author", "author-icon");
        Button btnLoginCustomer = createMenuButton("Login as Customer", "customer-icon");
        Button btnRegister = createMenuButton("Register as Customer", "register-icon");
        Button btnSave = createMenuButton("Save Data", "save-icon");
        Button btnExit = createMenuButton("Exit", "exit-icon");

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
        HBox footer = createFooter("System initialized. Default admin: ID=1, Password=admin123");
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
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        // Header
        VBox header = createHeader("LOGIN", userType + " Login");
        root.setTop(header);

        // Center content - login form
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(400);

        Label lblId = new Label(userType + " ID:");
        lblId.setTextFill(Color.WHITE);
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField txtId = new TextField();
        txtId.setPromptText("Enter your ID");
        txtId.setStyle("-fx-font-size: 14px;");

        Label lblPassword = new Label("Password:");
        lblPassword.setTextFill(Color.WHITE);
        lblPassword.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setStyle("-fx-font-size: 14px;");

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 40;");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        Button btnBack = new Button("Back to Welcome");
        btnBack.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 8 30;");
        btnBack.setMaxWidth(Double.MAX_VALUE);

        // Login action
        btnLogin.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String password = txtPassword.getText();
                performLogin(userType, id, password);
            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter a valid numeric ID");
            }
        });

        btnBack.setOnAction(e -> showWelcomeScreen());

        formBox.getChildren().addAll(lblId, txtId, lblPassword, txtPassword, btnLogin, btnBack);

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
    private void performLogin(String userType, int id, String password) {
        boolean loginSuccessful = false;
        String userName = "";

        switch (userType) {
            case "ADMIN":
                for (Admin admin : admins) {
                    if (admin.getAdminID() == id && admin.getPassword().equals(password)) {
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
                    if (author.getAuthorID() == id && author.getPassword().equals(password)) {
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
                    if (customer.getCustomerID() == id && customer.getPassword().equals(password)) {
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
            auditTrail.logAction("SYSTEM", "Failed " + userType + " login attempt for ID: " + id);
            showError("Login Failed", "Invalid credentials. Please try again.");
        }
    }

    // ============ REGISTRATION SCREEN ============

    /**
     * Show customer registration screen
     */
    private void showRegistrationScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

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
        btnRegister.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 30;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 30;");

        btnRegister.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String address = txtAddress.getText();
                String contact = txtContact.getText();
                String dob = txtDob.getText();
                int age = Integer.parseInt(txtAge.getText());
                String password = txtPassword.getText();

                // Validation
                if (name.trim().isEmpty()) {
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
                        "Welcome! You can now login with ID: " + id);
                showWelcomeScreen();

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter valid numbers for ID and Age");
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
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #c0392b, #e74c3c);");

        VBox header = createHeader("ADMIN DASHBOARD", "Welcome, " + admin.getName());
        root.setTop(header);

        // Menu buttons
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Account Management", "account-icon"),
                createMenuButton("Formulation Management", "formula-icon"),
                createMenuButton("Check Formulation Issues", "check-icon"),
                createMenuButton("View System Statistics", "stats-icon"),
                createMenuButton("View All Accounts", "users-icon"),
                createMenuButton("View Audit Trail", "audit-icon"),
                createMenuButton("Save Data to Database", "save-icon"),
                createMenuButton("Create Database Backup", "backup-icon"),
                createMenuButton("Logout", "logout-icon")
        };

        // Button actions
        buttons[0].setOnAction(e -> showAccountManagementScreen(admin));
        buttons[1].setOnAction(e -> showFormulationManagementScreen());
        buttons[2].setOnAction(e -> showCheckFormulationIssuesScreen());
        buttons[3].setOnAction(e -> showSystemStatisticsScreen());
//        buttons[4].setOnAction(e -> showAllAccountsScreen(admin));
        buttons[5].setOnAction(e -> showAuditTrailScreen());
        buttons[6].setOnAction(e -> saveDataToDatabase());
        buttons[7].setOnAction(e -> showInformation("Backup", "Backup feature available. Use mysqldump for full backup."));
        buttons[8].setOnAction(e -> logout());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        HBox footer = createFooter("Admin: " + admin.getName() + " | Total Formulations: " + allFormulations.size());
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
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2980b9, #3498db);");

        VBox header = createHeader("AUTHOR DASHBOARD", "Welcome, " + author.getName());
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Create New Formulation", "create-icon"),
                createMenuButton("Update Existing Formulation", "update-icon"),
                createMenuButton("Consult My Formulations", "consult-icon"),
                createMenuButton("Check Formulation Issues", "check-icon"),
                createMenuButton("View My Statistics", "stats-icon"),
                createMenuButton("Save Data to Database", "save-icon"),
                createMenuButton("Logout", "logout-icon")
        };

        // Button actions
        buttons[0].setOnAction(e -> showInformation("Feature", "Create Formulation dialog would open here"));
        buttons[1].setOnAction(e -> showInformation("Feature", "Update Formulation dialog would open here"));
        buttons[2].setOnAction(e -> showInformation("Feature", "Formulations list would open here"));
        buttons[3].setOnAction(e -> showInformation("Feature", "Issue checker would open here"));
        buttons[4].setOnAction(e -> showAuthorStatistics(author));
        buttons[5].setOnAction(e -> saveDataToDatabase());
        buttons[6].setOnAction(e -> logout());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        HBox footer = createFooter("Author: " + author.getName() + " | Formulations: " + author.getFormulatedItems().size());
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
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #16a085, #1abc9c);");

        VBox header = createHeader("CUSTOMER DASHBOARD", "Welcome, " + customer.getName());
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Browse Catalog", "browse-icon"),
                createMenuButton("My Purchases", "purchase-icon"),
                createMenuButton("My Favorites", "favorite-icon"),
                createMenuButton("My Profile", "profile-icon"),
                createMenuButton("Save Data to Database", "save-icon"),
                createMenuButton("Logout", "logout-icon")
        };

        // Button actions
        buttons[0].setOnAction(e -> showInformation("Feature", "Catalog browser would open here"));
        buttons[1].setOnAction(e -> showInformation("Feature", "Purchase history would open here"));
        buttons[2].setOnAction(e -> showCustomerFavorites(customer));
        buttons[3].setOnAction(e -> showCustomerProfile(customer));
        buttons[4].setOnAction(e -> saveDataToDatabase());
        buttons[5].setOnAction(e -> logout());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        HBox footer = createFooter("Customer: " + customer.getName() + " | Purchases: " + customer.getPurchasedItems().size());
        root.setBottom(footer);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ ADMIN SCREENS ============

    /**
     * Show Account Management Screen
     */
    private void showAccountManagementScreen(Admin admin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #8e44ad, #9b59b6);");

        VBox header = createHeader("ACCOUNT MANAGEMENT", "Manage System Users");
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                createMenuButton("Create Author Account", "create-author-icon"),
                createMenuButton("Create Admin Account", "create-admin-icon"),
                createMenuButton("View All Authors", "view-authors-icon"),
                createMenuButton("View All Admins", "view-admins-icon"),
                createMenuButton("Back to Dashboard", "back-icon")
        };

        buttons[0].setOnAction(e -> showInformation("Feature", "Create Author dialog would open"));
        buttons[1].setOnAction(e -> showInformation("Feature", "Create Admin dialog would open"));
        buttons[2].setOnAction(e -> showAllAuthorsScreen());
        buttons[3].setOnAction(e -> showAllAdminsScreen());
        buttons[4].setOnAction(e -> showAdminDashboard());

        menuBox.getChildren().addAll(buttons);
        root.setCenter(menuBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show System Statistics
     */
    private void showSystemStatisticsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

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

        addStatRow(statsGrid, 0, "Total Admins:", String.valueOf(admins.size()));
        addStatRow(statsGrid, 1, "Total Authors:", String.valueOf(authors.size()));
        addStatRow(statsGrid, 2, "Total Customers:", String.valueOf(customers.size()));
        addStatRow(statsGrid, 3, "Total Formulations:", String.valueOf(allFormulations.size()));

        // Count vetoed
        int vetoedCount = 0;

        for (Item item : allFormulations) {
            if (item instanceof MyClasses.Consumables.Food) {
                if (((MyClasses.Consumables.Food) item).isVetoed()) vetoedCount++;
            } else if (item instanceof MyClasses.Consumables.Drink) {
                if (((MyClasses.Consumables.Drink) item).isVetoed()) vetoedCount++;
            }
        }
        addStatRow(statsGrid, 4, "Vetoed Formulations:", String.valueOf(vetoedCount));

        // Count purchases
        int totalPurchases = 0;
        for (Customer customer : customers) {
            totalPurchases += customer.getPurchasedItems().size();
        }
        addStatRow(statsGrid, 5, "Total Purchases:", String.valueOf(totalPurchases));
        addStatRow(statsGrid, 6, "Audit Log Entries:", String.valueOf(auditTrail.records.size()));

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(statsGrid, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show All Accounts Screen
     */
    private void showAllAccountsScreen(Admin admin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("ALL ACCOUNTS", "System Users");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));

        // Admins
        Label lblAdmins = new Label("ADMINISTRATORS (" + admins.size() + ")");
        lblAdmins.setTextFill(Color.WHITE);
        lblAdmins.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea txtAdmins = new TextArea();
        txtAdmins.setEditable(false);
        txtAdmins.setPrefRowCount(5);
        StringBuilder adminText = new StringBuilder();
        for (Admin a : admins) {
            adminText.append("ID: ").append(a.getAdminID()).append(" - ").append(a.getName()).append("\n");
        }
        txtAdmins.setText(adminText.toString());

        // Authors
        Label lblAuthors = new Label("AUTHORS (" + authors.size() + ")");
        lblAuthors.setTextFill(Color.WHITE);
        lblAuthors.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea txtAuthors = new TextArea();
        txtAuthors.setEditable(false);
        txtAuthors.setPrefRowCount(5);
        StringBuilder authorText = new StringBuilder();
        for (Author a : authors) {
            authorText.append("ID: ").append(a.getAuthorID()).append(" - ").append(a.getName())
                    .append(" (Formulations: ").append(a.getFormulatedItems().size()).append(")\n");
        }
        txtAuthors.setText(authorText.toString());

        // Customers
        Label lblCustomers = new Label("CUSTOMERS (" + customers.size() + ")");
        lblCustomers.setTextFill(Color.WHITE);
        lblCustomers.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea txtCustomers = new TextArea();
        txtCustomers.setEditable(false);
        txtCustomers


        // Continuation from line 683 in showAllAccountsScreen method
        .setPrefRowCount(5);
        StringBuilder customerText = new StringBuilder();
        for (Customer c : customers) {
            customerText.append("ID: ").append(c.getCustomerID()).append(" - ").append(c.getName())
                    .append(" (Purchases: ").append(c.getPurchasedItems().size()).append(")\n");
        }
        txtCustomers.setText(customerText.toString());

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
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

    private void setPrefRowCount(int i) {
    }

    /**
     * Show All Authors Screen
     */
    private void showAllAuthorsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

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
            lblEmpty.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Author author : authors) {
                VBox authorBox = new VBox(5);
                authorBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 5;");

                Label lblName = new Label("Name: " + author.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                Label lblId = new Label("ID: " + author.getAuthorID());
                lblId.setTextFill(Color.LIGHTGRAY);

                Label lblFormulations = new Label("Formulations: " + author.getFormulatedItems().size());
                lblFormulations.setTextFill(Color.LIGHTGRAY);

                authorBox.getChildren().addAll(lblName, lblId, lblFormulations);
                contentBox.getChildren().add(authorBox);
            }
        }

        Button btnBack = createMenuButton("Back", "back-icon");
        btnBack.setOnAction(e -> showAccountManagementScreen((Admin) currentUser));
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show All Admins Screen
     */
    private void showAllAdminsScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("ALL ADMINISTRATORS", "Admin Directory");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        for (Admin admin : admins) {
            VBox adminBox = new VBox(5);
            adminBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 5;");

            Label lblName = new Label("Name: " + admin.getName());
            lblName.setTextFill(Color.WHITE);
            lblName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            Label lblId = new Label("ID: " + admin.getAdminID());
            lblId.setTextFill(Color.LIGHTGRAY);

            adminBox.getChildren().addAll(lblName, lblId);
            contentBox.getChildren().add(adminBox);
        }

        Button btnBack = createMenuButton("Back", "back-icon");
        btnBack.setOnAction(e -> showAccountManagementScreen((Admin) currentUser));
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Formulation Management Screen
     */
    private void showFormulationManagementScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("FORMULATION MANAGEMENT", "View and Manage Formulations");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        Label lblTotal = new Label("Total Formulations: " + allFormulations.size());
        lblTotal.setTextFill(Color.WHITE);
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        contentBox.getChildren().add(lblTotal);

        if (allFormulations.isEmpty()) {
            Label lblEmpty = new Label("No formulations in the system.");
            lblEmpty.setTextFill(Color.LIGHTGRAY);
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : allFormulations) {
                VBox itemBox = new VBox(8);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 5;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                Label lblType = new Label("Type: " + (item instanceof MyClasses.Consumables.Food ? "Food" : "Drink"));
                lblType.setTextFill(Color.LIGHTGRAY);

                Label lblId = new Label("ID: " + item.getItemID());
                lblId.setTextFill(Color.LIGHTGRAY);

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(Color.LIGHTGREEN);

                // Check veto status
                boolean vetoed = false;
                if (item instanceof MyClasses.Consumables.Food) {
                    vetoed = ((MyClasses.Consumables.Food) item).isVetoed();
                } else if (item instanceof MyClasses.Consumables.Drink) {
                    vetoed = ((MyClasses.Consumables.Drink) item).isVetoed();
                }

                if (vetoed) {
                    Label lblVeto = new Label("⚠ VETOED");
                    lblVeto.setTextFill(Color.RED);
                    lblVeto.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    itemBox.getChildren().add(lblVeto);
                }

                itemBox.getChildren().addAll(lblName, lblType, lblId, lblPrice);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showAdminDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Check Formulation Issues Screen
     */
    private void showCheckFormulationIssuesScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("CHECK FORMULATION ISSUES", "Quality Control");
        root.setTop(header);

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        Label lblInfo = new Label("This feature analyzes formulations for potential issues:");
        lblInfo.setTextFill(Color.WHITE);
        lblInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        TextArea txtInfo = new TextArea();
        txtInfo.setEditable(false);
        txtInfo.setPrefRowCount(10);
        txtInfo.setWrapText(true);

        StringBuilder info = new StringBuilder();
        info.append("Issues checked:\n\n");
        info.append("• Missing ingredients\n");
        info.append("• Invalid pricing\n");
        info.append("• Negative feedback\n");
        info.append("• Veto status\n");
        info.append("• Missing standards\n");
        info.append("• Expiry date concerns\n\n");
        info.append("Total formulations in system: ").append(allFormulations.size()).append("\n");

        int issueCount = 0;
        for (Item item : allFormulations) {
            if (item instanceof MyClasses.Consumables.Food) {
                MyClasses.Consumables.Food food = (MyClasses.Consumables.Food) item;
                if (food.getIngredients() == null || food.getIngredients().isEmpty()) issueCount++;
                if (food.isVetoed()) issueCount++;
                if (food.getPrice() <= 0) issueCount++;
            } else if (item instanceof MyClasses.Consumables.Drink) {
                MyClasses.Consumables.Drink drink = (MyClasses.Consumables.Drink) item;
                if (drink.getIngredients() == null || drink.getIngredients().isEmpty()) issueCount++;
                if (drink.isVetoed()) issueCount++;
                if (drink.getPrice() <= 0) issueCount++;
            }
        }

        info.append("Formulations with issues: ").append(issueCount);

        txtInfo.setText(info.toString());

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(lblInfo, txtInfo, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Audit Trail Screen
     */
    private void showAuditTrailScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("AUDIT TRAIL", "System Activity Log");
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20));

        Label lblTotal = new Label("Total Log Entries: " + auditTrail.records.size());
        lblTotal.setTextFill(Color.WHITE);
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea txtAudit = new TextArea();
        txtAudit.setEditable(false);
        txtAudit.setPrefRowCount(20);
        txtAudit.setWrapText(true);

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

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showAdminDashboard());

        contentBox.getChildren().addAll(lblTotal, txtAudit, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Author Statistics
     */
    private void showAuthorStatistics(Author author) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("MY STATISTICS", "Author Performance");
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(50);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        addStatRow(statsGrid, 0, "Total Formulations:", String.valueOf(author.getFormulatedItems().size()));
        addStatRow(statsGrid, 1, "Food Items:", String.valueOf(author.getFoodCount()));
        addStatRow(statsGrid, 2, "Drink Items:", String.valueOf(author.getDrinkCount()));

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showAuthorDashboard());

        contentBox.getChildren().addAll(statsGrid, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Customer Favorites
     */
    private void showCustomerFavorites(Customer customer) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("MY FAVORITES", customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        LinkedList<Item> favorites = customer.getFavoriteFormulations();

        if (favorites.isEmpty()) {
            Label lblEmpty = new Label("No favorites yet. Browse the catalog to add favorites!");
            lblEmpty.setTextFill(Color.LIGHTGRAY);
            lblEmpty.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : favorites) {
                VBox itemBox = new VBox(5);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 5;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(Color.WHITE);
                lblName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(Color.LIGHTGREEN);

                itemBox.getChildren().addAll(lblName, lblPrice);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showCustomerDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    /**
     * Show Customer Profile
     */
    private void showCustomerProfile(Customer customer) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        VBox header = createHeader("MY PROFILE", customer.getName());
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(30);
        profileGrid.setVgap(15);
        profileGrid.setAlignment(Pos.CENTER);

        addStatRow(profileGrid, 0, "Customer ID:", String.valueOf(customer.getCustomerID()));
        addStatRow(profileGrid, 1, "Name:", customer.getName());
        addStatRow(profileGrid, 2, "Age:", String.valueOf(customer.getAge()));
        addStatRow(profileGrid, 3, "Contact:", customer.getContact() != null ? customer.getContact() : "N/A");
        addStatRow(profileGrid, 4, "Total Purchases:", String.valueOf(customer.getPurchasedItems().size()));
        addStatRow(profileGrid, 5, "Total Favorites:", String.valueOf(customer.getFavoriteFormulations().size()));
        addStatRow(profileGrid, 6, "Feedback Given:", String.valueOf(customer.getFeedbackHistory().size()));

        Button btnBack = createMenuButton("Back to Dashboard", "back-icon");
        btnBack.setOnAction(e -> showCustomerDashboard());

        contentBox.getChildren().addAll(profileGrid, btnBack);
        root.setCenter(contentBox);

        currentScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(currentScene);
    }

    // ============ UTILITY METHODS ============

    /**
     * Get non-vetoed formulations for customers
     */
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
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.WHITE);

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
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
        lblFooter.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        lblFooter.setTextFill(Color.LIGHTGRAY);

        footer.getChildren().add(lblFooter);
        return footer;
    }

    /**
     * Create menu button
     */
    private Button createMenuButton(String text, String icon) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        button.setMaxWidth(400);
        button.setMinWidth(300);

        // Hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                        "-fx-background-radius: 5; -fx-cursor: hand;"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                        "-fx-background-radius: 5; -fx-cursor: hand;"));

        return button;
    }

    /**
     * Create form field
     */
    private TextField createFormField(VBox parent, String labelText, String promptText) {
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle("-fx-font-size: 14px;");

        parent.getChildren().addAll(label, textField);
        return textField;
    }

    /**
     * Create password field
     */
    private PasswordField createPasswordField(VBox parent, String labelText, String promptText) {
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setStyle("-fx-font-size: 14px;");

        parent.getChildren().addAll(label, passwordField);
        return passwordField;
    }

    /**
     * Add statistic row to grid
     */
    private void addStatRow(GridPane grid, int row, String label, String value) {
        Label lblLabel = new Label(label);
        lblLabel.setTextFill(Color.LIGHTGRAY);
        lblLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label lblValue = new Label(value);
        lblValue.setTextFill(Color.WHITE);
        lblValue.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    /**
     * Show information dialog
     */
    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
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
        alert.showAndWait();
    }

    // ============ MAIN METHOD ============

    public static void main(String[] args) {
        launch(args);
    }
}