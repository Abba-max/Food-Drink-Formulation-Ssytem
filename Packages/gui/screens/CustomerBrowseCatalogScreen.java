package gui.screens;

import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Persons.Customer;
import gui.components.DialogFactory;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.LinkedList;

/**
 * Customer Browse Catalog Screen
 */
public class CustomerBrowseCatalogScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Customer customer;

    public CustomerBrowseCatalogScreen(ScreenManager screenManager, Customer customer) {
        this.screenManager = screenManager;
        this.customer = customer;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_CUSTOMER + ", #117864);");

        VBox header = UIComponents.createHeader("BROWSE CATALOG", "Customer: " + customer.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        // Search bar
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(5, 0, 10, 0));

        TextField txtAuthor = new TextField();
        txtAuthor.setPromptText("Search by author");
        txtAuthor.setPrefWidth(250);

        TextField txtIngredients = new TextField();
        txtIngredients.setPromptText("Ingredients (comma-separated)");
        txtIngredients.setPrefWidth(400);

        Button btnSearch = UIComponents.createSmallButton("Search", UIComponents.COLOR_INFO);
        Button btnClear = UIComponents.createSmallButton("Clear", UIComponents.COLOR_NEUTRAL);

        searchBar.getChildren().addAll(txtAuthor, txtIngredients, btnSearch, btnClear);

        // Container for item boxes (will be re-rendered on search)
        VBox itemsContainer = new VBox(15);

        // Default items (for this customer)
        LinkedList<Item> availableItems = screenManager.getDataManager().getFormulationsForCustomer(customer);

        // initial render
        contentBox.getChildren().addAll(searchBar, itemsContainer);
        renderItems(itemsContainer, availableItems);

        // Search action
        btnSearch.setOnAction(e -> {
            String authorQuery = txtAuthor.getText();
            String ingText = txtIngredients.getText();
            java.util.List<String> ingList = new java.util.ArrayList<>();
            if (ingText != null && !ingText.trim().isEmpty()) {
                for (String part : ingText.split(",")) {
                    String p = part.trim();
                    if (!p.isEmpty()) ingList.add(p);
                }
            }

            java.util.LinkedList<Item> results = new java.util.LinkedList<>();

            boolean hasAuthor = authorQuery != null && !authorQuery.trim().isEmpty();
            boolean hasIngs = !ingList.isEmpty();

            if (!hasAuthor && !hasIngs) {
                results = screenManager.getDataManager().getFormulationsForCustomer(customer);
            } else {
                if (hasAuthor) {
                    results = screenManager.getDataManager().findFormulationsByAuthorName(authorQuery);
                }

                if (hasIngs) {
                    java.util.LinkedList<Item> byIng = screenManager.getDataManager().findFormulationsByIngredientNames(ingList, true);
                    if (hasAuthor) {
                        // intersection
                        results.retainAll(byIng);
                    } else {
                        results = byIng;
                    }
                }

                // If both present but intersection empty, it's fine - will show empty
            }

            renderItems(itemsContainer, results);
        });

        btnClear.setOnAction(e -> {
            txtAuthor.clear();
            txtIngredients.clear();
            renderItems(itemsContainer, screenManager.getDataManager().getFormulationsForCustomer(customer));
        });

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

    /**
     * Render a list of items into the provided container (clears previous content)
     */
    private void renderItems(VBox itemsContainer, java.util.LinkedList<Item> items) {
        itemsContainer.getChildren().clear();

        if (items == null || items.isEmpty()) {
            Label lblEmpty = new Label("No formulations available.");
            lblEmpty.setTextFill(javafx.scene.paint.Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            itemsContainer.getChildren().add(lblEmpty);
            return;
        }

        for (Item item : items) {
            VBox itemBox = new VBox(8);
            itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                    "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

            Label lblName = new Label(item.getName());
            lblName.setTextFill(javafx.scene.paint.Color.WHITE);
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

            Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
            lblType.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
            lblType.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

            Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
            lblPrice.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
            lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            boolean purchased = customer.isPaid(item);
            Label lblStatus = new Label(purchased ? "✓ PURCHASED" : "🔒 Not Purchased");
            lblStatus.setTextFill(purchased ? javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS) :
                    javafx.scene.paint.Color.web(UIComponents.COLOR_ERROR));
            lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

            HBox buttonBox = new HBox(10);
            Button btnView = UIComponents.createSmallButton("View Details", UIComponents.COLOR_INFO);
            Button btnPurchase = UIComponents.createSmallButton(purchased ? "Purchased" : "Purchase",
                    purchased ? UIComponents.COLOR_NEUTRAL : UIComponents.COLOR_SUCCESS);
            btnPurchase.setDisable(purchased);

            btnView.setOnAction(e -> screenManager.showFormulationDetailsDialog(item));
            btnPurchase.setOnAction(e -> {
                javafx.stage.Stage dialog = DialogFactory.createPurchaseDialog(item, ev -> {
                    // Simulate purchase
                    if (customer.makePayment(item, "Credit Card")) {
                        screenManager.getDataManager().getAuditTrail().logAction(
                                "CUSTOMER:" + customer.getName(),
                                "Purchased: " + item.getName() + " for $" + item.getPrice()
                        );
                        screenManager.showInformation("Success", "Purchase completed successfully!");
                        screenManager.showCustomerBrowseCatalogScreen(); // Refresh
                    }
                });
                dialog.initOwner(screenManager.getPrimaryStage());
                dialog.showAndWait();
            });

            buttonBox.getChildren().addAll(btnView, btnPurchase);
            itemBox.getChildren().addAll(lblName, lblType, lblPrice, lblStatus, buttonBox);
            itemsContainer.getChildren().add(itemBox);
        }
    }
}