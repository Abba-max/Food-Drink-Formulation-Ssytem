package gui.screens;

import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Login Screen for all user types
 */
public class LoginScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private String userType;

    public LoginScreen(ScreenManager screenManager, String userType) {
        this.screenManager = screenManager;
        this.userType = userType;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();

        // Set gradient background based on user type
        String gradientColor = UIComponents.COLOR_SECONDARY;
        if ("ADMIN".equals(userType)) gradientColor = UIComponents.COLOR_ADMIN;
        else if ("AUTHOR".equals(userType)) gradientColor = UIComponents.COLOR_AUTHOR;
        else if ("CUSTOMER".equals(userType)) gradientColor = UIComponents.COLOR_CUSTOMER;

        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                gradientColor + ", " + UIComponents.COLOR_PRIMARY + ");");

        // Header
        VBox header = UIComponents.createHeader("LOGIN", userType + " Login");
        root.setTop(header);

        // Center content - login form
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 25; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 8;");

        Label lblName = new Label(userType + " Name:");
        lblName.setTextFill(Color.WHITE);
        lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        TextField txtName = new TextField();
        txtName.setPromptText("Enter your Name");
        txtName.setStyle("-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        Label lblPassword = new Label("Password:");
        lblPassword.setTextFill(Color.WHITE);
        lblPassword.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setStyle("-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        Button btnLogin = UIComponents.createMenuButton("Login", UIComponents.COLOR_SUCCESS);
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        Button btnBack = UIComponents.createMenuButton("Back to Welcome", UIComponents.COLOR_NEUTRAL);
        btnBack.setMaxWidth(Double.MAX_VALUE);

        // Login action
        btnLogin.setOnAction(e -> {
            String name = txtName.getText().trim();
            String password = txtPassword.getText();

            if (name.isEmpty() || password.isEmpty()) {
                screenManager.showError("Validation Error", "Please fill in all fields!");
                return;
            }

            performLogin(name, password);
        });

        btnBack.setOnAction(e -> screenManager.showWelcomeScreen());

        formBox.getChildren().addAll(lblName, txtName, lblPassword, txtPassword, btnLogin, btnBack);

        // Center the form
        HBox centerContainer = new HBox(formBox);
        centerContainer.setAlignment(Pos.CENTER);
        root.setCenter(centerContainer);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private void performLogin(String name, String password) {
        boolean loginSuccessful = false;
        Object authenticatedUser = null;

        try {
            switch (userType) {
                case "ADMIN":
                    authenticatedUser = screenManager.getDataManager().authenticateAdmin(name, password);
                    break;
                case "AUTHOR":
                    authenticatedUser = screenManager.getDataManager().authenticateAuthor(name, password);
                    break;
                case "CUSTOMER":
                    authenticatedUser = screenManager.getDataManager().authenticateCustomer(name, password);
                    break;
            }

            if (authenticatedUser != null) {
                screenManager.setCurrentUser(authenticatedUser, userType);
                screenManager.getAuditTrail().logAction(userType + ":" + name, "Logged in");
                screenManager.showInformation("Login Successful", "Welcome, " + name + "!");
                screenManager.showUserDashboard();
                loginSuccessful = true;
            }
        } catch (Exception e) {
            screenManager.showError("Login Error", "Error during login: " + e.getMessage());
        }

        if (!loginSuccessful) {
            screenManager.getAuditTrail().logAction("SYSTEM", "Failed " + userType + " login attempt for name: " + name);
            screenManager.showError("Login Failed", "Invalid credentials. Please try again.");
        }
    }

    public Scene getScene() {
        return scene;
    }
}