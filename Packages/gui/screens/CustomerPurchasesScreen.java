package gui.screens;

import MyClasses.Persons.Customer;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

/**
 * Customer Purchases Screen
 */
public class CustomerPurchasesScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Customer customer;

    public CustomerPurchasesScreen(ScreenManager screenManager, Customer customer) {
        this.screenManager = screenManager;
        this.customer = customer;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_CUSTOMER + ", #117864);");

        VBox header = UIComponents.createHeader("MY PURCHASES", "Customer: " + customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        if (customer.getPurchasedItems().isEmpty()) {
            Label lblEmpty = new Label("You haven't purchased any items yet.");
            lblEmpty.setTextFill(javafx.scene.paint.Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Map.Entry<Integer, Customer.PurchaseRecord> entry : customer.getPurchasedItems().entrySet()) {
                Customer.PurchaseRecord record = entry.getValue();

                VBox recordBox = new VBox(5);
                recordBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblItem = new Label(record.getItemName());
                lblItem.setTextFill(javafx.scene.paint.Color.WHITE);
                lblItem.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", record.getPrice()));
                lblPrice.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));

                Label lblDate = new Label("Date: " + record.getPurchaseDate());
                lblDate.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
                lblDate.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblMethod = new Label("Method: " + record.getPaymentMethod());
                lblMethod.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
                lblMethod.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                recordBox.getChildren().addAll(lblItem, lblPrice, lblDate, lblMethod);
                contentBox.getChildren().add(recordBox);
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