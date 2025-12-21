package gui.screens;

import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Audit Trail Screen
 */
public class AuditTrailScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public AuditTrailScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("AUDIT TRAIL", "System Activity Log");
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20));

        Label lblTotal = new Label("Total Log Entries: " + screenManager.getDataManager().getAuditTrail().records.size());
        lblTotal.setTextFill(javafx.scene.paint.Color.WHITE);
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtAudit = new TextArea();
        txtAudit.setEditable(false);
        txtAudit.setPrefRowCount(20);
        txtAudit.setWrapText(true);
        txtAudit.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        if (screenManager.getDataManager().getAuditTrail().records.isEmpty()) {
            txtAudit.setText("No audit records available.");
        } else {
            StringBuilder auditText = new StringBuilder();
            int startIndex = Math.max(0, screenManager.getDataManager().getAuditTrail().records.size() - 50);

            for (int i = startIndex; i < screenManager.getDataManager().getAuditTrail().records.size(); i++) {
                auditText.append(screenManager.getDataManager().getAuditTrail().records.get(i)).append("\n");
            }

            if (screenManager.getDataManager().getAuditTrail().records.size() > 50) {
                auditText.insert(0, "Showing last 50 of " + screenManager.getDataManager().getAuditTrail().records.size() + " entries\n\n");
            }

            txtAudit.setText(auditText.toString());
        }

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAdminDashboard());

        contentBox.getChildren().addAll(lblTotal, txtAudit, btnBack);
        root.setCenter(contentBox);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    public Scene getScene() {
        return scene;
    }
}