package gui.screens;

import MyClasses.Persons.Admin;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

/**
 * Admin Dashboard Screen
 */
public class AdminDashboard {
    private Scene scene;
    private ScreenManager screenManager;
    private Admin admin;

    public AdminDashboard(ScreenManager screenManager, Admin admin) {
        this.screenManager = screenManager;
        this.admin = admin;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " +
                UIComponents.COLOR_ADMIN + " 0%, #A93226 100%);");

        // Enhanced Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(25));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 0 0 1 0;");

        Label lblTitle = new Label("ADMIN DASHBOARD");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0.5, 0, 1);");

        Label lblWelcome = new Label("Welcome, " + admin.getName());
        lblWelcome.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        lblWelcome.setTextFill(Color.LIGHTGRAY);

        Label lblRole = new Label("System Administrator");
        lblRole.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblRole.setTextFill(Color.web(UIComponents.COLOR_ADMIN));
        lblRole.setStyle("-fx-background-color: rgba(192, 57, 43, 0.2); -fx-padding: 5 15; -fx-background-radius: 15;");

        header.getChildren().addAll(lblTitle, lblWelcome, lblRole);
        root.setTop(header);

        // Dashboard stats overview
        HBox statsBar = createStatsBar();
        root.setCenter(createMainContent());
        root.setBottom(statsBar);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private HBox createStatsBar() {
        HBox statsBar = new HBox(30);
        statsBar.setPadding(new Insets(15));
        statsBar.setAlignment(Pos.CENTER);
        statsBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");

        // Get statistics from data manager
        int totalUsers = screenManager.getDataManager().getAllUsers().size();
        int totalFormulations = screenManager.getDataManager().getAllFormulations().size();
        int vetoedFormulations = screenManager.getDataManager().getVetoedFormulations().size();
        int auditEntries = screenManager.getAuditTrail().getRecords().size();

        Label lblUsers = createStatItem("👥 Total Users",
                String.valueOf(totalUsers), "#8E44AD");
        Label lblFormulations = createStatItem("📋 Formulations",
                String.valueOf(totalFormulations), UIComponents.COLOR_INFO);
        Label lblVetoed = createStatItem("⚠ Vetoed Items",
                String.valueOf(vetoedFormulations), UIComponents.COLOR_WARNING);
        Label lblAudit = createStatItem("📊 Audit Logs",
                String.valueOf(auditEntries), UIComponents.COLOR_SECONDARY);

        statsBar.getChildren().addAll(lblUsers, lblFormulations, lblVetoed, lblAudit);
        return statsBar;
    }

    private Label createStatItem(String label, String value, String color) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblValue.setTextFill(Color.web(color));
        lblValue.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 0, 1);");

        Label lblLabel = new Label(label);
        lblLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblLabel.setTextFill(Color.LIGHTGRAY);

        statBox.getChildren().addAll(lblValue, lblLabel);

        Label container = new Label();
        container.setGraphic(statBox);
        return container;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.CENTER);

        // Section: User Management
        VBox userSection = createSection("👥 USER MANAGEMENT", "Manage system users and accounts");
        HBox userButtons = new HBox(15);
        userButtons.setAlignment(Pos.CENTER);

        Button btnAccountMgmt = createFeatureButton("Account Management", "#8E44AD",
                "Create and manage user accounts");
        Button btnViewAllAccounts = createFeatureButton("View All Accounts", "#8E44AD",
                "Browse all system users");
        Button btnCreateAuthor = createFeatureButton("Create Author", UIComponents.COLOR_INFO,
                "Create new author account");

        btnAccountMgmt.setOnAction(e -> screenManager.showAccountManagementScreen());
        btnViewAllAccounts.setOnAction(e -> showAllAccountsScreen());
        btnCreateAuthor.setOnAction(e -> screenManager.showCreateAuthorScreen());

        userButtons.getChildren().addAll(btnAccountMgmt, btnViewAllAccounts, btnCreateAuthor);
        userSection.getChildren().add(userButtons);

        // Section: Formulation Management
        VBox formulationSection = createSection("📚 FORMULATION MANAGEMENT", "Manage all formulations in the system");
        HBox formulationButtons = new HBox(15);
        formulationButtons.setAlignment(Pos.CENTER);

        Button btnFormulationMgmt = createFeatureButton("Formulation Management", UIComponents.COLOR_INFO,
                "View and manage all formulations");
        Button btnCheckIssues = createFeatureButton("Check Issues", UIComponents.COLOR_WARNING,
                "Review formulation quality issues");
        Button btnSetVeto = createFeatureButton("Manage Vetoes", UIComponents.COLOR_ERROR,
                "Set or remove vetoes on formulations");

        btnFormulationMgmt.setOnAction(e -> showFormulationManagementScreen());
        btnCheckIssues.setOnAction(e -> showCheckFormulationIssuesScreen());
        btnSetVeto.setOnAction(e -> showFormulationManagementScreen());

        formulationButtons.getChildren().addAll(btnFormulationMgmt, btnCheckIssues, btnSetVeto);
        formulationSection.getChildren().add(formulationButtons);

        // Section: System Monitoring
        VBox monitoringSection = createSection("📊 SYSTEM MONITORING", "Monitor system performance and activity");
        HBox monitoringButtons = new HBox(15);
        monitoringButtons.setAlignment(Pos.CENTER);

        Button btnStatistics = createFeatureButton("System Statistics", UIComponents.COLOR_SUCCESS,
                "View comprehensive system statistics");
        Button btnAuditTrail = createFeatureButton("Audit Trail", UIComponents.COLOR_SECONDARY,
                "Review system activity logs");
        Button btnDatabase = createFeatureButton("Database Tools", "#16A085",
                "Database maintenance tools");

        btnStatistics.setOnAction(e -> showSystemStatisticsScreen());
        btnAuditTrail.setOnAction(e -> showAuditTrailScreen());
        btnDatabase.setOnAction(e -> showDatabaseTools());

        monitoringButtons.getChildren().addAll(btnStatistics, btnAuditTrail, btnDatabase);
        monitoringSection.getChildren().add(monitoringButtons);

        // Section: System Tools
        VBox toolsSection = createSection("⚙ SYSTEM TOOLS", "Administrative tools and utilities");
        HBox toolsButtons = new HBox(15);
        toolsButtons.setAlignment(Pos.CENTER);

        Button btnSaveData = createFeatureButton("Save Data", "#27AE60",
                "Save all system data to database");
        Button btnBackup = createFeatureButton("Create Backup", UIComponents.COLOR_INFO,
                "Create database backup");
        Button btnLogout = createFeatureButton("Logout", UIComponents.COLOR_ERROR,
                "Exit admin session");

        btnSaveData.setOnAction(e -> saveDataToDatabase());
        btnBackup.setOnAction(e -> createDatabaseBackup());
        btnLogout.setOnAction(e -> screenManager.logout());

        toolsButtons.getChildren().addAll(btnSaveData, btnBackup, btnLogout);
        toolsSection.getChildren().add(toolsButtons);

        mainContent.getChildren().addAll(userSection, formulationSection, monitoringSection, toolsSection);
        return mainContent;
    }

    private VBox createSection(String title, String description) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(0, 0, 15, 0));

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.WHITE);

        Label lblDesc = new Label(description);
        lblDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblDesc.setTextFill(Color.LIGHTGRAY);

        section.getChildren().addAll(lblTitle, lblDesc);
        return section;
    }

    private Button createFeatureButton(String text, String color, String tooltip) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0.4, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", -15%); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 0, 3);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0.4, 0, 2);"));

        javafx.scene.control.Tooltip tooltipObj = new javafx.scene.control.Tooltip(tooltip);
        tooltipObj.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.8);");
        button.setTooltip(tooltipObj);

        return button;
    }

    private void showCheckFormulationIssuesScreen() {
        screenManager.showInformation("Formulation Issues",
                "This feature shows formulation quality issues and potential problems.\n\n" +
                        "Available checks:\n" +
                        "• Missing ingredients\n" +
                        "• Invalid pricing\n" +
                        "• Vetoed formulations\n" +
                        "• Negative feedback analysis\n" +
                        "• Expired formulations");
    }

    private void showSystemStatisticsScreen() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== SYSTEM STATISTICS ===\n\n");

        stats.append("📊 USER STATISTICS:\n");
        stats.append("• Total Admins: ").append(screenManager.getDataManager().getAllAdmins().size()).append("\n");
        stats.append("• Total Authors: ").append(screenManager.getDataManager().getAllAuthors().size()).append("\n");
        stats.append("• Total Customers: ").append(screenManager.getDataManager().getAllCustomers().size()).append("\n");
        stats.append("• Total Users: ").append(screenManager.getDataManager().getAllUsers().size()).append("\n\n");

        stats.append("📚 FORMULATION STATISTICS:\n");
        stats.append("• Total Formulations: ").append(screenManager.getDataManager().getAllFormulations().size()).append("\n");
        stats.append("• Vetoed Formulations: ").append(screenManager.getDataManager().getVetoedFormulations().size()).append("\n");
        stats.append("• Active Formulations: ").append(screenManager.getDataManager().getActiveFormulations().size()).append("\n\n");

        stats.append("📈 ACTIVITY STATISTICS:\n");
        stats.append("• Total Purchases: ").append(screenManager.getDataManager().getTotalPurchases()).append("\n");
        stats.append("• Total Feedbacks: ").append(screenManager.getDataManager().getTotalFeedbacks()).append("\n");
        stats.append("• Audit Log Entries: ").append(screenManager.getAuditTrail().getRecords().size()).append("\n");

        screenManager.showInformation("System Statistics", stats.toString());
    }

    private void showAllAccountsScreen() {
        StringBuilder accounts = new StringBuilder();
        accounts.append("=== ALL SYSTEM ACCOUNTS ===\n\n");

        accounts.append("👑 ADMINISTRATORS:\n");
        for (MyClasses.Persons.Admin a : screenManager.getDataManager().getAllAdmins()) {
            accounts.append("• ").append(a.getName()).append(" (ID: ").append(a.getAdminID()).append(")\n");
        }
        accounts.append("\n");

        accounts.append("✍️ AUTHORS:\n");
        for (MyClasses.Persons.Author a : screenManager.getDataManager().getAllAuthors()) {
            accounts.append("• ").append(a.getName()).append(" (ID: ").append(a.getAuthorID()).append(")")
                    .append(" - Formulations: ").append(a.getFormulatedItems().size()).append("\n");
        }
        accounts.append("\n");

        accounts.append("👤 CUSTOMERS:\n");
        for (MyClasses.Persons.Customer c : screenManager.getDataManager().getAllCustomers()) {
            accounts.append("• ").append(c.getName()).append(" (ID: ").append(c.getCustomerID()).append(")")
                    .append(" - Purchases: ").append(c.getPurchasedItems().size()).append("\n");
        }

        screenManager.showInformation("All Accounts", accounts.toString());
    }

    private void showAuditTrailScreen() {
        StringBuilder auditLog = new StringBuilder();
        auditLog.append("=== AUDIT TRAIL (Last 50 entries) ===\n\n");

        java.util.List<String> records = screenManager.getAuditTrail().getRecords();
        int startIndex = Math.max(0, records.size() - 50);

        for (int i = startIndex; i < records.size(); i++) {
            auditLog.append(records.get(i)).append("\n");
        }

        if (records.size() > 50) {
            auditLog.insert(0, "Showing last 50 of " + records.size() + " entries\n\n");
        }

        screenManager.showInformation("Audit Trail", auditLog.toString());
    }

    private void saveDataToDatabase() {
        if (screenManager.getDataManager().saveAllData()) {
            screenManager.getAuditTrail().logAction("ADMIN:" + admin.getName(),
                    "Saved all system data to database");
            screenManager.showInformation("Save Successful", "All system data has been successfully saved to the database.");
        } else {
            screenManager.showError("Save Failed", "Failed to save data to database. Please check database connection.");
        }
    }

    private void createDatabaseBackup() {
        screenManager.showInformation("Database Backup",
                "Database backup feature would create a complete backup of the system.\n\n" +
                        "Backup includes:\n" +
                        "• All user accounts\n" +
                        "• All formulations\n" +
                        "• Audit trail\n" +
                        "• Feedback data\n\n" +
                        "Use mysqldump for full database backup:\n" +
                        "mysqldump -u [username] -p [database] > backup.sql");
    }

    private void showDatabaseTools() {
        screenManager.showInformation("Database Tools",
                "Available database maintenance tools:\n\n" +
                        "1. Database Backup\n" +
                        "2. Data Integrity Check\n" +
                        "3. Performance Optimization\n" +
                        "4. Cleanup Old Records\n" +
                        "5. Export to CSV\n\n" +
                        "These tools help maintain database health and performance.");
    }

    private void showFormulationManagementScreen() {
        screenManager.showInformation("Formulation Management",
                "Full formulation management interface would show:\n\n" +
                        "• List of all formulations\n" +
                        "• Formulation details\n" +
                        "• Veto management\n" +
                        "• Quality checks\n" +
                        "• Author information\n\n" +
                        "This allows administrators to review and manage all formulations in the system.");
    }

    public Scene getScene() {
        return scene;
    }
}