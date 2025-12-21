package gui.screens;

import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * All Accounts Screen
 */
public class AllAccountsScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public AllAccountsScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("ALL ACCOUNTS", "System Users");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));

        // Admins
        Label lblAdmins = new Label("ADMINISTRATORS (" + screenManager.getDataManager().getAdmins().size() + ")");
        lblAdmins.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_ADMIN));
        lblAdmins.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtAdmins = new TextArea();
        txtAdmins.setEditable(false);
        txtAdmins.setPrefRowCount(5);
        txtAdmins.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        StringBuilder adminText = new StringBuilder();
        screenManager.getDataManager().getAdmins().forEach(admin ->
                adminText.append("ID: ").append(admin.getAdminID()).append(" - ").append(admin.getName()).append("\n")
        );
        txtAdmins.setText(adminText.toString());

        // Authors
        Label lblAuthors = new Label("AUTHORS (" + screenManager.getDataManager().getAuthors().size() + ")");
        lblAuthors.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_INFO));
        lblAuthors.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtAuthors = new TextArea();
        txtAuthors.setEditable(false);
        txtAuthors.setPrefRowCount(5);
        txtAuthors.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        StringBuilder authorText = new StringBuilder();
        screenManager.getDataManager().getAuthors().forEach(author ->
                authorText.append("ID: ").append(author.getAuthorID()).append(" - ").append(author.getName())
                        .append(" (Formulations: ").append(author.getFormulatedItems().size()).append(")\n")
        );
        txtAuthors.setText(authorText.toString());

        // Customers
        Label lblCustomers = new Label("CUSTOMERS (" + screenManager.getDataManager().getCustomers().size() + ")");
        lblCustomers.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        lblCustomers.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtCustomers = new TextArea();
        txtCustomers.setEditable(false);
        txtCustomers.setPrefRowCount(5);
        txtCustomers.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        StringBuilder customerText = new StringBuilder();
        screenManager.getDataManager().getCustomers().forEach(customer ->
                customerText.append("ID: ").append(customer.getCustomerID()).append(" - ").append(customer.getName())
                        .append(" (Purchases: ").append(customer.getPurchasedItems().size()).append(")\n")
        );
        txtCustomers.setText(customerText.toString());

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAdminDashboard());

        contentBox.getChildren().addAll(
                lblAdmins, txtAdmins,
                lblAuthors, txtAuthors,
                lblCustomers, txtCustomers,
                btnBack
        );

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    public Scene getScene() {
        return scene;
    }
}