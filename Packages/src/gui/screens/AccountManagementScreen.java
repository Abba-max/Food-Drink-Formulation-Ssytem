package gui.screens;

import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Account Management Screen
 */
public class AccountManagementScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public AccountManagementScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("ACCOUNT MANAGEMENT", "Manage System Users");
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button btnCreateAuthor = UIComponents.createMenuButton("Create Author Account", UIComponents.COLOR_INFO);
        Button btnCreateAdmin = UIComponents.createMenuButton("Create Admin Account", UIComponents.COLOR_ADMIN);
        Button btnViewAuthors = UIComponents.createMenuButton("View All Authors", UIComponents.COLOR_INFO);
        Button btnViewAdmins = UIComponents.createMenuButton("View All Admins", UIComponents.COLOR_ADMIN);
        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);

        btnCreateAuthor.setOnAction(e -> showCreateAuthorScreen());
        btnCreateAdmin.setOnAction(e -> showCreateAdminScreen());
        btnViewAuthors.setOnAction(e -> showAllAuthorsScreen());
        btnViewAdmins.setOnAction(e -> showAllAdminsScreen());
        btnBack.setOnAction(e -> screenManager.showAdminDashboard());

        menuBox.getChildren().addAll(btnCreateAuthor, btnCreateAdmin, btnViewAuthors, btnViewAdmins, btnBack);
        root.setCenter(menuBox);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private void showCreateAuthorScreen() {
        screenManager.showInformation("Create Author", "This feature would create an author account.");
    }

    private void showCreateAdminScreen() {
        screenManager.showInformation("Create Admin", "This feature would create an admin account.");
    }

    private void showAllAuthorsScreen() {
        screenManager.showInformation("All Authors", "This feature would show all authors.");
    }

    private void showAllAdminsScreen() {
        screenManager.showInformation("All Admins", "This feature would show all admins.");
    }

    public Scene getScene() {
        return scene;
    }
}