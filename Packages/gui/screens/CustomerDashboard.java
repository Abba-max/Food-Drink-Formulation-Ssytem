package gui.screens;

import MyClasses.Persons.Customer;
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
 * Customer Dashboard Screen
 */
public class CustomerDashboard {
    private Scene scene;
    private ScreenManager screenManager;
    private Customer customer;

    public CustomerDashboard(ScreenManager screenManager, Customer customer) {
        this.screenManager = screenManager;
        this.customer = customer;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_CUSTOMER + ", #117864);");

        VBox header = UIComponents.createHeader("CUSTOMER DASHBOARD", "Welcome, " + customer.getName() + " (Customer)");
        root.setTop(header);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(30));

        Button[] buttons = {
                UIComponents.createMenuButton("Browse Catalog", UIComponents.COLOR_INFO),
                UIComponents.createMenuButton("My Purchases", UIComponents.COLOR_SUCCESS),
                UIComponents.createMenuButton("My Favorites", "#8E44AD"),
                UIComponents.createMenuButton("My Profile", UIComponents.COLOR_INFO),
                UIComponents.createMenuButton("Save Data to Database", UIComponents.COLOR_SECONDARY),
                UIComponents.createMenuButton("Logout", UIComponents.COLOR_ERROR)
        };

        // Button actions
        buttons[0].setOnAction(e -> screenManager.showCustomerBrowseCatalogScreen());
        buttons[1].setOnAction(e -> screenManager.showCustomerPurchasesScreen());
        buttons[2].setOnAction(e -> screenManager.showCustomerFavoritesScreen());
        buttons[3].setOnAction(e -> screenManager.showCustomerProfileScreen());
        buttons[4].setOnAction(e -> screenManager.saveDataToDatabase());
        buttons[5].setOnAction(e -> screenManager.logout());

        for (Button btn : buttons) {
            menuBox.getChildren().add(btn);
        }

        root.setCenter(menuBox);

        HBox footer = UIComponents.createFooter(
                "Customer: " + customer.getName() +
                        " | Purchases: " + customer.getPurchasedItems().size() +
                        " | Favorites: " + customer.getFavoriteFormulations().size()
        );
        root.setBottom(footer);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    public Scene getScene() {
        return scene;
    }
}