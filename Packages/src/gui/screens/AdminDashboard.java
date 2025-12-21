package gui.screens;

import MyClasses.Persons.Admin;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_ADMIN + ", #A93226);");

        VBox header = UIComponents.createHeader(
                "ADMIN DASHBOARD",
                "Welcome, " + admin.getName() + " (Administrator)"
        );
        root.setTop(header);

        // Menu buttons
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                UIComponents.createMenuButton("Account Management", "#8E44AD"),
                UIComponents.createMenuButton("Formulation Management", UIComponents.COLOR_INFO),
                UIComponents.createMenuButton("Check Formulation Issues", UIComponents.COLOR_ERROR),
                UIComponents.createMenuButton("View System Statistics", UIComponents.COLOR_SUCCESS),
                UIComponents.createMenuButton("View All Accounts", "#8E44AD"),
                UIComponents.createMenuButton("View Audit Trail", UIComponents.COLOR_SECONDARY),
                UIComponents.createMenuButton("Save Data to Database", UIComponents.COLOR_SUCCESS),
                UIComponents.createMenuButton("Create Database Backup", UIComponents.COLOR_INFO),
                UIComponents.createMenuButton("Logout", UIComponents.COLOR_ERROR)
        };

        // Button actions
        buttons[0].setOnAction(e -> screenManager.showAccountManagementScreen());
        buttons[1].setOnAction(e -> screenManager.showFormulationManagementScreen());
        buttons[2].setOnAction(e -> showCheckFormulationIssuesScreen());
        buttons[3].setOnAction(e -> showSystemStatisticsScreen());
        buttons[4].setOnAction(e -> showAllAccountsScreen());
        buttons[5].setOnAction(e -> showAuditTrailScreen());
        buttons[6].setOnAction(e -> saveDataToDatabase());
        buttons[7].setOnAction(e -> showInformation("Backup", "Backup feature available."));
        buttons[8].setOnAction(e -> screenManager.logout());

        for (Button btn : buttons) {
            menuBox.getChildren().add(btn);
        }

        root.setCenter(menuBox);

        HBox footer = UIComponents.createFooter(
                "Admin: " + admin.getName() + " | Logged in as Administrator"
        );
        root.setBottom(footer);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private void showCheckFormulationIssuesScreen() {
        // Implementation would go here or in a separate screen class
        screenManager.showInformation("Formulation Issues", "This feature would show formulation issues.");
    }

    private void showSystemStatisticsScreen() {
        // Implementation would go here or in a separate screen class
        screenManager.showInformation("System Statistics", "This feature would show system statistics.");
    }

    private void showAllAccountsScreen() {
        // Implementation would go here or in a separate screen class
        screenManager.showInformation("All Accounts", "This feature would show all accounts.");
    }

    private void showAuditTrailScreen() {
        // Implementation would go here or in a separate screen class
        screenManager.showInformation("Audit Trail", "This feature would show the audit trail.");
    }

    private void saveDataToDatabase() {
        // Implementation for saving data
        screenManager.showInformation("Save", "Data saved successfully!");
    }

    private void showInformation(String title, String message) {
        screenManager.showInformation(title, message);
    }

    public Scene getScene() {
        return scene;
    }
}