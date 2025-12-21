package gui.screens;

import MyClasses.Consumables.Item;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Drink;
import MyClasses.Feedback;
import MyClasses.Persons.Author;
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

import java.util.LinkedList;

/**
 * Author Statistics Screen
 */
public class AuthorStatisticsScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Author author;

    public AuthorStatisticsScreen(ScreenManager screenManager, Author author) {
        this.screenManager = screenManager;
        this.author = author;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_AUTHOR + ", #21618C);");

        VBox header = UIComponents.createHeader("MY STATISTICS", "Author: " + author.getName());
        root.setTop(header);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(50);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        addStatRow(statsGrid, 0, "Total Formulations:",
                String.valueOf(author.getFormulatedItems().size()),
                UIComponents.COLOR_INFO);

        int foodCount = 0;
        int drinkCount = 0;
        for (Item item : author.getFormulatedItems()) {
            if (item instanceof Food) foodCount++;
            else if (item instanceof Drink) drinkCount++;
        }
        addStatRow(statsGrid, 1, "Food Items:", String.valueOf(foodCount), UIComponents.COLOR_SUCCESS);
        addStatRow(statsGrid, 2, "Drink Items:", String.valueOf(drinkCount), UIComponents.COLOR_INFO);

        // Calculate total feedbacks
        int totalFeedbacks = 0;
        int positiveFeedbacks = 0;
        for (Item item : author.getFormulatedItems()) {
            LinkedList<Feedback> feedbacks = getFeedbacks(item);
            if (feedbacks != null) {
                totalFeedbacks += feedbacks.size();
                for (Feedback fb : feedbacks) {
                    if (fb.isLike()) positiveFeedbacks++;
                }
            }
        }
        addStatRow(statsGrid, 3, "Total Feedbacks:", String.valueOf(totalFeedbacks), "#8E44AD");
        addStatRow(statsGrid, 4, "Positive Feedbacks:", String.valueOf(positiveFeedbacks), UIComponents.COLOR_SUCCESS);

        // Calculate average rating
        double avgRating = totalFeedbacks > 0 ? (double) positiveFeedbacks / totalFeedbacks * 100 : 0;
        addStatRow(statsGrid, 5, "Positive Rating:", String.format("%.1f%%", avgRating),
                avgRating >= 80 ? UIComponents.COLOR_SUCCESS :
                        avgRating >= 60 ? UIComponents.COLOR_WARNING : UIComponents.COLOR_ERROR);

        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAuthorDashboard());

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