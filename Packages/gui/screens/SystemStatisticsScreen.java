package gui.screens;

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
 * System Statistics Screen
 */
public class SystemStatisticsScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public SystemStatisticsScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("SYSTEM STATISTICS", "Overview");
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(30));

        // Statistics display
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(50);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        addStatRow(statsGrid, 0, "Total Admins:",
                String.valueOf(screenManager.getDataManager().getAdmins().size()),
                UIComponents.COLOR_ADMIN);

        addStatRow(statsGrid, 1, "Total Authors:",
                String.valueOf(screenManager.getDataManager().getAuthors().size()),
                UIComponents.COLOR_INFO);

        addStatRow(statsGrid, 2, "Total Customers:",
                String.valueOf(screenManager.getDataManager().getCustomers().size()),
                UIComponents.COLOR_SUCCESS);

        addStatRow(statsGrid, 3, "Total Formulations:",
                String.valueOf(screenManager.getDataManager().getTotalFormulations()),
                "#8E44AD");

        addStatRow(statsGrid, 4, "Vetoed Formulations:",
                String.valueOf(screenManager.getDataManager().getVetoedFormulationsCount()),
                UIComponents.COLOR_ERROR);

        addStatRow(statsGrid, 5, "Total Purchases:",
                String.valueOf(screenManager.getDataManager().getTotalPurchases()),
                UIComponents.COLOR_SUCCESS);

        addStatRow(statsGrid, 6, "Audit Log Entries:",
                String.valueOf(screenManager.getDataManager().getAuditTrail().records.size()),
                UIComponents.COLOR_SECONDARY);

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAdminDashboard());

        contentBox.getChildren().addAll(statsGrid, btnBack);
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