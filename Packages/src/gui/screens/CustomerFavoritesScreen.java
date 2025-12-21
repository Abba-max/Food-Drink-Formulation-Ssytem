package gui.screens;

import MyClasses.Consumables.Item;
import MyClasses.Persons.Customer;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.LinkedList;

/**
 * Customer Favorites Screen
 */
public class CustomerFavoritesScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Customer customer;

    public CustomerFavoritesScreen(ScreenManager screenManager, Customer customer) {
        this.screenManager = screenManager;
        this.customer = customer;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_CUSTOMER + ", #117864);");

        VBox header = UIComponents.createHeader("MY FAVORITES", "Customer: " + customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        LinkedList<Item> favorites = customer.getFavoriteFormulations();

        if (favorites.isEmpty()) {
            Label lblEmpty = new Label("No favorites yet. Browse the catalog to add favorites!");
            lblEmpty.setTextFill(javafx.scene.paint.Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : favorites) {
                VBox itemBox = new VBox(5);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(javafx.scene.paint.Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));

                boolean purchased = customer.isPaid(item);
                Label lblStatus = new Label(purchased ? "✓ Purchased" : "🔒 Not Purchased");
                lblStatus.setTextFill(purchased ? javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS) :
                        javafx.scene.paint.Color.web(UIComponents.COLOR_ERROR));
                lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                HBox buttonBox = new HBox(10);
                Button btnView = UIComponents.createSmallButton("View", UIComponents.COLOR_INFO);
                Button btnRemove = UIComponents.createSmallButton("Remove", UIComponents.COLOR_ERROR);

                btnView.setOnAction(e -> screenManager.showFormulationDetailsDialog(item));
                btnRemove.setOnAction(e -> {
                    customer.removeFromFavorites(item);
                    screenManager.showInformation("Success", "Removed from favorites!");
                    screenManager.showCustomerFavoritesScreen(); // Refresh
                });

                buttonBox.getChildren().addAll(btnView, btnRemove);
                itemBox.getChildren().addAll(lblName, lblPrice, lblStatus, buttonBox);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showCustomerDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    public Scene getScene() {
        return scene;
    }
}