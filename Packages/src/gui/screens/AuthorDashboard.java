package gui.screens;

import MyClasses.Persons.Author;
import MyClasses.Consumables.Item;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.LinkedList;

/**
 * Complete Author Dashboard Screen
 */
public class AuthorDashboard {
    private Scene scene;
    private ScreenManager screenManager;
    private Author author;

    public AuthorDashboard(ScreenManager screenManager, Author author) {
        this.screenManager = screenManager;
        this.author = author;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " +
                UIComponents.COLOR_AUTHOR + " 0%, #21618C 100%);");

        // Header with author info
        VBox header = new VBox(10);
        header.setPadding(new Insets(25, 25, 25, 25));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 0 0 1 0;");

        Label lblTitle = new Label("AUTHOR DASHBOARD");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0.5, 0, 1);");

        Label lblWelcome = new Label("Welcome, " + author.getName());
        lblWelcome.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        lblWelcome.setTextFill(Color.LIGHTGRAY);

        Label lblRole = new Label("Formulation Author");
        lblRole.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblRole.setTextFill(Paint.valueOf(UIComponents.COLOR_AUTHOR));
        lblRole.setStyle("-fx-background-color: rgba(41, 128, 185, 0.2); -fx-padding: 5 15; -fx-background-radius: 15;");

        header.getChildren().addAll(lblTitle, lblWelcome, lblRole);
        root.setTop(header);

        // Dashboard stats overview
        HBox statsBar = createStatsBar();
        root.setCenter(createMainContent());
        root.setBottom(statsBar);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private HBox createStatsBar() {
        HBox statsBar = new HBox(30);
        statsBar.setPadding(new Insets(15));
        statsBar.setAlignment(Pos.CENTER);
        statsBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");

        LinkedList<Item> authorFormulations = author.getFormulatedItems();
        int foodCount = 0;
        int drinkCount = 0;
        int vetoedCount = 0;

        for (Item item : authorFormulations) {
            if (item instanceof MyClasses.Consumables.Food) foodCount++;
            else if (item instanceof MyClasses.Consumables.Drink) drinkCount++;

            if ((item instanceof MyClasses.Consumables.Food && ((MyClasses.Consumables.Food) item).isVetoed()) ||
                    (item instanceof MyClasses.Consumables.Drink && ((MyClasses.Consumables.Drink) item).isVetoed())) {
                vetoedCount++;
            }
        }

        Label lblTotal = createStatItem("📋 Total Formulations",
                String.valueOf(authorFormulations.size()), UIComponents.COLOR_INFO);
        Label lblFood = createStatItem("🍕 Food Items",
                String.valueOf(foodCount), UIComponents.COLOR_SUCCESS);
        Label lblDrink = createStatItem("🥤 Drink Items",
                String.valueOf(drinkCount), "#3498DB");
        Label lblVetoed = createStatItem("⚠ Vetoed Items",
                String.valueOf(vetoedCount), UIComponents.COLOR_WARNING);

        statsBar.getChildren().addAll(lblTotal, lblFood, lblDrink, lblVetoed);
        return statsBar;
    }

    private Label createStatItem(String label, String value, String color) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblValue.setTextFill(Color.web(color));
        lblValue.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 0, 1);");

        Label lblLabel = new Label(label);
        lblLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblLabel.setTextFill(Color.LIGHTGRAY);

        statBox.getChildren().addAll(lblValue, lblLabel);

        Label container = new Label();
        container.setGraphic(statBox);
        return container;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.CENTER);

        // Section: Formulation Creation
        VBox creationSection = createSection("✨ FORMULATION CREATION", "Create new food and drink formulations");
        HBox creationButtons = new HBox(15);
        creationButtons.setAlignment(Pos.CENTER);

        Button btnCreateFood = createFeatureButton("Create Food", UIComponents.COLOR_SUCCESS, "Create new food formulation");
        Button btnCreateDrink = createFeatureButton("Create Drink", UIComponents.COLOR_INFO, "Create new drink formulation");
        Button btnUpdateForm = createFeatureButton("Update Existing", "#9B59B6", "Modify existing formulations");

        btnCreateFood.setOnAction(e -> screenManager.showCreateFormulationScreen());
        btnCreateDrink.setOnAction(e -> screenManager.showCreateFormulationScreen());
        btnUpdateForm.setOnAction(e -> screenManager.showUpdateFormulationScreen());

        creationButtons.getChildren().addAll(btnCreateFood, btnCreateDrink, btnUpdateForm);
        creationSection.getChildren().add(creationButtons);

        // Section: My Formulations
        VBox myFormulationsSection = createSection("📚 MY FORMULATIONS", "Manage your created formulations");
        HBox formulationButtons = new HBox(15);
        formulationButtons.setAlignment(Pos.CENTER);

        Button btnViewFormulations = createFeatureButton("View All", UIComponents.COLOR_INFO, "Browse all your formulations");
        Button btnCheckIssues = createFeatureButton("Check Issues", UIComponents.COLOR_WARNING, "Review formulation quality issues");
        Button btnStatistics = createFeatureButton("My Statistics", UIComponents.COLOR_SUCCESS, "View your performance statistics");

        btnViewFormulations.setOnAction(e -> screenManager.showAuthorFormulationsScreen());
        btnCheckIssues.setOnAction(e -> screenManager.showAuthorCheckIssuesScreen());
        btnStatistics.setOnAction(e -> screenManager.showAuthorStatisticsScreen());

        formulationButtons.getChildren().addAll(btnViewFormulations, btnCheckIssues, btnStatistics);
        myFormulationsSection.getChildren().add(formulationButtons);

        // Section: System Tools
        VBox toolsSection = createSection("⚙ SYSTEM TOOLS", "Additional tools and options");
        HBox toolsButtons = new HBox(15);
        toolsButtons.setAlignment(Pos.CENTER);

        Button btnSaveData = createFeatureButton("Save Data", "#27AE60", "Save all your work to database");
        Button btnProfile = createFeatureButton("My Profile", "#8E44AD", "View and update your profile");
        Button btnLogout = createFeatureButton("Logout", UIComponents.COLOR_ERROR, "Exit author session");

        btnSaveData.setOnAction(e -> {
            if (screenManager.getDataManager().saveAllData()) {
                screenManager.showInformation("Data Saved", "All your formulations have been successfully saved.");
            } else {
                screenManager.showError("Save Failed", "Failed to save data to database.");
            }
        });
        btnProfile.setOnAction(e -> showAuthorProfileDialog());
        btnLogout.setOnAction(e -> screenManager.logout());

        toolsButtons.getChildren().addAll(btnSaveData, btnProfile, btnLogout);
        toolsSection.getChildren().add(toolsButtons);

        mainContent.getChildren().addAll(creationSection, myFormulationsSection, toolsSection);
        return mainContent;
    }

    private VBox createSection(String title, String description) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(0, 0, 15, 0));

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.WHITE);

        Label lblDesc = new Label(description);
        lblDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblDesc.setTextFill(Color.LIGHTGRAY);

        section.getChildren().addAll(lblTitle, lblDesc);
        return section;
    }

    private Button createFeatureButton(String text, String color, String tooltip) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0.4, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", -15%); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 0, 3);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0.4, 0, 2);"));

        // Add tooltip
        javafx.scene.control.Tooltip tooltipObj = new javafx.scene.control.Tooltip(tooltip);
        tooltipObj.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.8);");
        button.setTooltip(tooltipObj);

        return button;
    }

    private void showAuthorProfileDialog() {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Author Profile");
        dialog.setHeaderText("Your Author Profile Information");
        dialog.initOwner(screenManager.getPrimaryStage());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        // Create the profile display
        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(20));
        profileBox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label lblTitle = new Label("Author Profile");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblTitle.setTextFill(Color.web(UIComponents.COLOR_AUTHOR));

        // Profile details
        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8;");

        addProfileDetail(detailsBox, "👤 Name:", author.getName());
        addProfileDetail(detailsBox, "🆔 Author ID:", String.valueOf(author.getAuthorID()));
        addProfileDetail(detailsBox, "📅 Date of Birth:", author.getDateofbirth());
        addProfileDetail(detailsBox, "📞 Contact:", author.getContact() != null ? author.getContact() : "Not specified");
        addProfileDetail(detailsBox, "🏠 Address:", author.getAddress() != null ? author.getAddress() : "Not specified");
        addProfileDetail(detailsBox, "📋 Total Formulations:", String.valueOf(author.getFormulatedItems().size()));

        // Formulation breakdown
        int foodCount = 0;
        int drinkCount = 0;
        for (MyClasses.Consumables.Item item : author.getFormulatedItems()) {
            if (item instanceof MyClasses.Consumables.Food) foodCount++;
            else if (item instanceof MyClasses.Consumables.Drink) drinkCount++;
        }

        addProfileDetail(detailsBox, "🍕 Food Items:", String.valueOf(foodCount));
        addProfileDetail(detailsBox, "🥤 Drink Items:", String.valueOf(drinkCount));

        profileBox.getChildren().addAll(lblTitle, detailsBox);
        dialog.getDialogPane().setContent(profileBox);

        // Show dialog
        dialog.showAndWait();
    }

    private void addProfileDetail(VBox container, String label, String value) {
        HBox detailRow = new HBox(10);
        detailRow.setAlignment(Pos.CENTER_LEFT);

        Label lblLabel = new Label(label);
        lblLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblLabel.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));
        lblLabel.setMinWidth(150);

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblValue.setTextFill(Color.web(UIComponents.COLOR_SECONDARY));

        detailRow.getChildren().addAll(lblLabel, lblValue);
        container.getChildren().add(detailRow);
    }

    public Scene getScene() {
        return scene;
    }
}