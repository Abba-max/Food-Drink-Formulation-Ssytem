package gui.screens;

import MyClasses.Consumables.Item;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Drink;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Feedback;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedList;

/**
 * Formulation Details Dialog
 */
public class FormulationDetailsDialog {
    private Stage dialog;

    public FormulationDetailsDialog(ScreenManager screenManager, Object itemObj) {
        if (!(itemObj instanceof Item)) {
            return;
        }

        Item item = (Item) itemObj;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Formulation Details: " + item.getName());
        dialog.initOwner(screenManager.getPrimaryStage());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Basic info
        Label lblName = new Label(item.getName());
        lblName.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        lblName.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));

        Label lblType = new Label("Type: " + (item instanceof Food ? "Food" : "Drink"));

        Label lblId = new Label("ID: " + item.getItemID());

        Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        lblPrice.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        lblPrice.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));

        Label lblVeto = new Label("Veto Status: " + (isItemVetoed(item) ? "VETOED" : "Not Vetoed"));
        lblVeto.setTextFill(isItemVetoed(item) ?
                javafx.scene.paint.Color.web(UIComponents.COLOR_ERROR) :
                javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        lblVeto.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 12));

        vbox.getChildren().addAll(lblName, lblType, lblId, lblPrice, lblVeto);

        // Ingredients
        LinkedList<Ingredient> ingredients = getIngredients(item);
        if (ingredients != null && !ingredients.isEmpty()) {
            Label lblIngredients = new Label("\nIngredients:");
            lblIngredients.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
            lblIngredients.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));
            vbox.getChildren().add(lblIngredients);

            for (Ingredient ing : ingredients) {
                Label lblIng = new Label("  • " + ing.getName());
                if (ing.getQuantity() != null) {
                    lblIng.setText(lblIng.getText() + " - Weight: " + ing.getQuantity().getWeight() + "g, Volume: " +
                            ing.getQuantity().getVolume() + "ml");
                }
                vbox.getChildren().add(lblIng);
            }
        }

        // Feedbacks
        LinkedList<Feedback> feedbacks = getFeedbacks(item);
        if (feedbacks != null && !feedbacks.isEmpty()) {
            Label lblFeedbacks = new Label("\nFeedbacks (" + feedbacks.size() + "):");
            lblFeedbacks.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
            lblFeedbacks.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));
            vbox.getChildren().add(lblFeedbacks);

            for (Feedback fb : feedbacks) {
                Label lblFb = new Label("  " + (fb.isLike() ? "👍" : "👎") + " " +
                        fb.getConsumerName() + ": " + fb.getComment());
                lblFb.setTextFill(fb.isLike() ?
                        javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS) :
                        javafx.scene.paint.Color.web(UIComponents.COLOR_ERROR));
                vbox.getChildren().add(lblFb);
            }
        }

        Button btnClose = UIComponents.createSmallButton("Close", UIComponents.COLOR_NEUTRAL);
        btnClose.setOnAction(e -> dialog.close());
        vbox.getChildren().add(btnClose);

        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 500, 400);
        dialog.setScene(scene);
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

    public void show() {
        dialog.showAndWait();
    }
}