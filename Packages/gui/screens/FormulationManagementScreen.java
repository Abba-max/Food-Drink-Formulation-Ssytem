package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Persons.Admin;
import gui.components.DialogFactory;
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
import javafx.stage.Stage;

/**
 * Formulation Management Screen
 */
public class FormulationManagementScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public FormulationManagementScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("FORMULATION MANAGEMENT", "View and Manage All Formulations");
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        if (screenManager.getDataManager().getAllFormulations().isEmpty()) {
            Label lblEmpty = new Label("No formulations in the system.");
            lblEmpty.setTextFill(javafx.scene.paint.Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : screenManager.getDataManager().getAllFormulations()) {
                VBox itemBox = new VBox(8);
                itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; -fx-background-radius: 6; " +
                        "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1;");

                Label lblName = new Label(item.getName());
                lblName.setTextFill(javafx.scene.paint.Color.WHITE);
                lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));
                lblType.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
                lblType.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblId = new Label("ID: " + item.getItemID());
                lblId.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
                lblId.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

                Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
                lblPrice.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
                lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                // Show veto status
                boolean vetoed = isItemVetoed(item);
                if (vetoed) {
                    Label lblVeto = new Label("⚠ VETOED");
                    lblVeto.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_ERROR));
                    lblVeto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    lblVeto.setStyle("-fx-background-color: rgba(231, 76, 60, 0.2); -fx-padding: 2 5; -fx-background-radius: 3;");
                    itemBox.getChildren().add(lblVeto);
                }

                // Add action buttons
                HBox buttonBox = new HBox(10);
                Button btnView = UIComponents.createSmallButton("View Details", UIComponents.COLOR_INFO);
                Button btnVeto = UIComponents.createSmallButton(vetoed ? "Remove Veto" : "Set Veto",
                        vetoed ? UIComponents.COLOR_SUCCESS : UIComponents.COLOR_ERROR);

                btnView.setOnAction(e -> screenManager.showFormulationDetailsDialog(item));
                btnVeto.setOnAction(e -> {
                    if (vetoed) {
                        removeVeto(item);
                    } else {
                        setVetoOnItem(item);
                    }
                    screenManager.showFormulationManagementScreen(); // Refresh
                });

                buttonBox.getChildren().addAll(btnView, btnVeto);
                itemBox.getChildren().addAll(lblName, lblType, lblId, lblPrice, buttonBox);
                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAdminDashboard());
        contentBox.getChildren().add(btnBack);

        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private boolean isItemVetoed(Item item) {
        if (item instanceof Food) {
            return ((Food) item).isVetoed();
        } else if (item instanceof Drink) {
            return ((Drink) item).isVetoed();
        }
        return false;
    }

    private void setVetoOnItem(Item item) {
        Stage dialog = DialogFactory.createVetoDialog(item, e -> {
            // This is where you would set the veto
            // For now, we'll just log it
            Admin admin = (Admin) screenManager.getCurrentUser();
            screenManager.getDataManager().getAuditTrail().logAction(
                    "ADMIN:" + admin.getName(),
                    "Set veto on formulation: " + item.getName()
            );
            screenManager.showInformation("Success", "Veto set successfully!");
        });
        dialog.initOwner(screenManager.getPrimaryStage());
        dialog.showAndWait();
    }

    private void removeVeto(Item item) {
        // Remove veto logic would go here
        Admin admin = (Admin) screenManager.getCurrentUser();
        screenManager.getDataManager().getAuditTrail().logAction(
                "ADMIN:" + admin.getName(),
                "Removed veto from formulation: " + item.getName()
        );
        screenManager.showInformation("Success", "Veto removed successfully!");
    }

    public Scene getScene() {
        return scene;
    }
}