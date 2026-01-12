package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Persons.Author;
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

import java.util.LinkedList;

/**
 * Author Formulations Screen
 */
public class AuthorFormulationsScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Author author;

    public AuthorFormulationsScreen(ScreenManager screenManager, Author author) {
        this.screenManager = screenManager;
        this.author = author;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_AUTHOR + ", #21618C);");

        VBox header = UIComponents.createHeader("MY FORMULATIONS", "Author: " + author.getName());
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));

        LinkedList<Item> formulations = author.getFormulatedItems();

        if (formulations.isEmpty()) {
            Label lblEmpty = new Label("You haven't created any formulations yet.");
            lblEmpty.setTextFill(javafx.scene.paint.Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            contentBox.getChildren().add(lblEmpty);
        } else {
            for (Item item : formulations) {
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

                // Check if vetoed
                if (isItemVetoed(item)) {
                    Label lblVeto = new Label("⚠ VETOED");
                    lblVeto.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_ERROR));
                    lblVeto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    lblVeto.setStyle("-fx-background-color: rgba(231, 76, 60, 0.2); -fx-padding: 2 5; -fx-background-radius: 3;");
                    itemBox.getChildren().add(lblVeto);
                }

                contentBox.getChildren().add(itemBox);
            }
        }

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAuthorDashboard());
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

    public Scene getScene() {
        return scene;
    }
}