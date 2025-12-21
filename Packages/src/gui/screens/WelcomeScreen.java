package gui.screens;

import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Welcome/Home Screen
 */
public class WelcomeScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public WelcomeScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_PRIMARY + ", " + UIComponents.COLOR_SECONDARY + ");");

        // Header
        VBox header = UIComponents.createHeader(
                "FOOD & DRINK FORMULATION MANAGEMENT SYSTEM",
                "Professional Formulation Management Platform"
        );
        root.setTop(header);

        // Center content - menu buttons
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Please select an option:");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        titleLabel.setTextFill(Color.WHITE);

        Button btnLoginAdmin = UIComponents.createMenuButton("Login as Admin", UIComponents.COLOR_ADMIN);
        Button btnLoginAuthor = UIComponents.createMenuButton("Login as Author", UIComponents.COLOR_AUTHOR);
        Button btnLoginCustomer = UIComponents.createMenuButton("Login as Customer", UIComponents.COLOR_CUSTOMER);
        Button btnRegister = UIComponents.createMenuButton("Register as Customer", "#8E44AD");
        Button btnSave = UIComponents.createMenuButton("Save Data", UIComponents.COLOR_SUCCESS);
        Button btnExit = UIComponents.createMenuButton("Exit", UIComponents.COLOR_ERROR);

        // Button actions
        btnLoginAdmin.setOnAction(e -> screenManager.showLoginScreen("ADMIN"));
        btnLoginAuthor.setOnAction(e -> screenManager.showLoginScreen("AUTHOR"));
        btnLoginCustomer.setOnAction(e -> screenManager.showLoginScreen("CUSTOMER"));
        btnRegister.setOnAction(e -> screenManager.showRegistrationScreen());
        btnSave.setOnAction(e -> screenManager.saveDataToDatabase());
        btnExit.setOnAction(e -> screenManager.handleExit());

        centerBox.getChildren().addAll(titleLabel, btnLoginAdmin, btnLoginAuthor,
                btnLoginCustomer, btnRegister, btnSave, btnExit);

        root.setCenter(centerBox);

        // Footer
        HBox footer = UIComponents.createFooter(
                "System initialized. Default admin: Name=System Admin, Password=admin123"
        );
        root.setBottom(footer);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    public Scene getScene() {
        return scene;
    }
}