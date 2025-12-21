package gui.screens;

import MyClasses.Persons.Customer;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Customer Profile Screen
 */
public class CustomerProfileScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Customer customer;

    public CustomerProfileScreen(ScreenManager screenManager, Customer customer) {
        this.screenManager = screenManager;
        this.customer = customer;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_CUSTOMER + ", #117864);");

        VBox header = UIComponents.createHeader("MY PROFILE", "Customer: " + customer.getName());
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(30);
        profileGrid.setVgap(15);
        profileGrid.setAlignment(Pos.CENTER);

        addStatRow(profileGrid, 0, "Customer ID:",
                String.valueOf(customer.getCustomerID()), UIComponents.COLOR_INFO);

        addStatRow(profileGrid, 1, "Name:", customer.getName(), UIComponents.COLOR_INFO);

        addStatRow(profileGrid, 2, "Age:", String.valueOf(customer.getAge()), UIComponents.COLOR_SUCCESS);

        addStatRow(profileGrid, 3, "Contact:",
                customer.getContact() != null ? customer.getContact() : "N/A",
                UIComponents.COLOR_INFO);

        addStatRow(profileGrid, 4, "Total Purchases:",
                String.valueOf(customer.getPurchasedItems().size()), UIComponents.COLOR_SUCCESS);

        addStatRow(profileGrid, 5, "Total Favorites:",
                String.valueOf(customer.getFavoriteFormulations().size()), "#8E44AD");

        addStatRow(profileGrid, 6, "Feedback Given:",
                String.valueOf(customer.getFeedbackHistory().size()), UIComponents.COLOR_INFO);

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showCustomerDashboard());

        contentBox.getChildren().addAll(profileGrid, btnBack);
        root.setCenter(contentBox);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private void addStatRow(GridPane grid, int row, String label, String value, String color) {
        Label lblLabel = new Label(label);
        lblLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));
        lblLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));

        Label lblValue = new Label(value);
        lblValue.setTextFill(javafx.scene.paint.Color.web(color));
        lblValue.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    public Scene getScene() {
        return scene;
    }
}