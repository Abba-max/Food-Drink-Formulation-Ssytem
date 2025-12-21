package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Feedback;
import MyClasses.Ingredients.Ingredient;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.LinkedList;

/**
 * Check Formulation Issues Screen
 */
public class CheckFormulationIssuesScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public CheckFormulationIssuesScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("CHECK FORMULATION ISSUES", "Quality Control");
        root.setTop(header);

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        // Get issues
        int issueCount = 0;
        StringBuilder issuesText = new StringBuilder();
        issuesText.append("=== FORMULATION ISSUES REPORT ===\n\n");

        for (Item item : screenManager.getDataManager().getAllFormulations()) {
            boolean hasIssues = false;
            StringBuilder itemIssues = new StringBuilder();

            // Check ingredients
            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients == null || ingredients.isEmpty()) {
                itemIssues.append("  ⚠ Missing ingredients\n");
                hasIssues = true;
            }

            // Check price
            if (item.getPrice() <= 0) {
                itemIssues.append("  ⚠ Invalid price: $").append(item.getPrice()).append("\n");
                hasIssues = true;
            }

            // Check veto
            if (isItemVetoed(item)) {
                itemIssues.append("  ⚠ Formulation is vetoed\n");
                hasIssues = true;
            }

            // Check feedback
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                int negativeCount = 0;
                for (Feedback fb : feedbacks) {
                    if (!fb.isLike()) negativeCount++;
                }
                if (negativeCount > 0) {
                    itemIssues.append("  ⚠ Has ").append(negativeCount).append(" negative feedback(s)\n");
                    hasIssues = true;
                }
            }

            if (hasIssues) {
                issueCount++;
                issuesText.append(item.getName()).append(" (ID: ").append(item.getItemID()).append(")\n");
                issuesText.append(itemIssues.toString()).append("\n");
            }
        }

        Label lblInfo = new Label("Found " + issueCount + " formulation(s) with issues");
        lblInfo.setTextFill(issueCount > 0 ? javafx.scene.paint.Color.web(UIComponents.COLOR_WARNING) :
                javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        lblInfo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea txtIssues = new TextArea();
        txtIssues.setEditable(false);
        txtIssues.setPrefRowCount(15);
        txtIssues.setWrapText(true);
        txtIssues.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        txtIssues.setText(issuesText.toString());

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAdminDashboard());

        contentBox.getChildren().addAll(lblInfo, txtIssues, btnBack);
        root.setCenter(contentBox);

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

    private LinkedList<Ingredient> getIngredients(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            return ((Drink) item).getIngredients();
        }
        return null;
    }

    private LinkedList<Feedback> getFeedbacks(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getFeedbacks();
        } else if (item instanceof Drink) {
            return ((Drink) item).getFeedbacks();
        }
        return null;
    }

    public Scene getScene() {
        return scene;
    }
}