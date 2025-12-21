package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Ingredients.Quantity;
import MyClasses.Persons.Author;
import gui.components.DialogFactory;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Create Formulation Screen
 */
public class CreateFormulationScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Author author;

    public CreateFormulationScreen(ScreenManager screenManager, Author author) {
        this.screenManager = screenManager;
        this.author = author;
        createScreen();
    }

    private void createScreen() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Tab tabFood = new Tab("Food Formulation");
        tabFood.setClosable(false);
        tabFood.setContent(createFoodFormulationForm());

        Tab tabDrink = new Tab("Drink Formulation");
        tabDrink.setClosable(false);
        tabDrink.setContent(createDrinkFormulationForm());

        tabPane.getTabs().addAll(tabFood, tabDrink);

        scene = new Scene(tabPane, 800, 700);
    }

    private ScrollPane createFoodFormulationForm() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        scrollPane.setContent(vbox);

        // Title
        Label titleLabel = new Label("Create Food Formulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        vbox.getChildren().add(titleLabel);

        // Basic Information Section
        vbox.getChildren().add(createSectionSeparator("BASIC INFORMATION", UIComponents.COLOR_SUCCESS));

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(10);
        basicGrid.setVgap(10);
        basicGrid.setPadding(new Insets(10));

        TextField txtName = createGridField(basicGrid, "Food Name:", 0, true);
        TextField txtId = createGridField(basicGrid, "Food ID:", 1, true);
        TextField txtPrice = createGridField(basicGrid, "Price ($):", 2, true);
        TextField txtAvgPrice = createGridField(basicGrid, "Avg Price per Kg ($):", 3, true);
        TextField txtExpiry = createGridField(basicGrid, "Expiry Date:", 4, true);

        vbox.getChildren().add(basicGrid);

        // Ingredients Section
        vbox.getChildren().add(createSectionSeparator("INGREDIENTS", UIComponents.COLOR_INFO));

        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ingredient.getName()).append(" (ID: ").append(ingredient.getIngredientID()).append(")");
                    if (ingredient.getQuantity() != null) {
                        Quantity q = ingredient.getQuantity();
                        sb.append(" - Weight: ").append(q.getWeight()).append("g, ");
                        sb.append("Volume: ").append(q.getVolume()).append("ml, ");
                        sb.append("Unit: ").append(q.getUnit());
                    }
                    setText(sb.toString());
                }
            }
        });

        HBox ingredientControls = new HBox(10);
        Button btnAddIngredient = UIComponents.createSmallButton("Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnRemoveIngredient = UIComponents.createSmallButton("Remove Selected", UIComponents.COLOR_ERROR);

        btnAddIngredient.setOnAction(e -> {
            Stage dialog = DialogFactory.createIngredientDialog(ingredientsList);
            dialog.initOwner(screenManager.getPrimaryStage());
            dialog.showAndWait();
        });

        btnRemoveIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
            }
        });

        ingredientControls.getChildren().addAll(btnAddIngredient, btnRemoveIngredient);
        vbox.getChildren().addAll(listView, ingredientControls);

        // Create Button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnCreate = UIComponents.createMenuButton("Create Food Formulation", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createMenuButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnCreate.setOnAction(e -> createFoodFormulation(
                txtName.getText().trim(),
                txtId.getText().trim(),
                txtPrice.getText().trim(),
                txtAvgPrice.getText().trim(),
                txtExpiry.getText().trim(),
                ingredientsList
        ));

        btnCancel.setOnAction(e -> screenManager.showAuthorDashboard());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().add(buttonBox);

        return scrollPane;
    }

    private ScrollPane createDrinkFormulationForm() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        scrollPane.setContent(vbox);

        // Title
        Label titleLabel = new Label("Create Drink Formulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_INFO));
        vbox.getChildren().add(titleLabel);

        // Basic Information Section
        vbox.getChildren().add(createSectionSeparator("BASIC INFORMATION", UIComponents.COLOR_INFO));

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(10);
        basicGrid.setVgap(10);
        basicGrid.setPadding(new Insets(10));

        TextField txtName = createGridField(basicGrid, "Drink Name:", 0, true);
        TextField txtId = createGridField(basicGrid, "Drink ID:", 1, true);
        TextField txtPrice = createGridField(basicGrid, "Price ($):", 2, true);
        TextField txtAvgPrice = createGridField(basicGrid, "Avg Price per Kg ($):", 3, true);
        TextField txtExpiry = createGridField(basicGrid, "Expiry Date:", 4, true);

        vbox.getChildren().add(basicGrid);

        // Ingredients Section
        vbox.getChildren().add(createSectionSeparator("INGREDIENTS", UIComponents.COLOR_SUCCESS));

        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ingredient.getName()).append(" (ID: ").append(ingredient.getIngredientID()).append(")");
                    if (ingredient.getQuantity() != null) {
                        Quantity q = ingredient.getQuantity();
                        sb.append(" - Weight: ").append(q.getWeight()).append("g, ");
                        sb.append("Volume: ").append(q.getVolume()).append("ml, ");
                        sb.append("Unit: ").append(q.getUnit());
                    }
                    setText(sb.toString());
                }
            }
        });

        HBox ingredientControls = new HBox(10);
        Button btnAddIngredient = UIComponents.createSmallButton("Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnRemoveIngredient = UIComponents.createSmallButton("Remove Selected", UIComponents.COLOR_ERROR);

        btnAddIngredient.setOnAction(e -> {
            Stage dialog = DialogFactory.createIngredientDialog(ingredientsList);
            dialog.initOwner(screenManager.getPrimaryStage());
            dialog.showAndWait();
        });

        btnRemoveIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
            }
        });

        ingredientControls.getChildren().addAll(btnAddIngredient, btnRemoveIngredient);
        vbox.getChildren().addAll(listView, ingredientControls);

        // Create Button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnCreate = UIComponents.createMenuButton("Create Drink Formulation", UIComponents.COLOR_INFO);
        Button btnCancel = UIComponents.createMenuButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnCreate.setOnAction(e -> createDrinkFormulation(
                txtName.getText().trim(),
                txtId.getText().trim(),
                txtPrice.getText().trim(),
                txtAvgPrice.getText().trim(),
                txtExpiry.getText().trim(),
                ingredientsList
        ));

        btnCancel.setOnAction(e -> screenManager.showAuthorDashboard());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().add(buttonBox);

        return scrollPane;
    }

    private void createFoodFormulation(String name, String id, String price,
                                       String avgPrice, String expiry,
                                       ObservableList<Ingredient> ingredients) {
        try {
            if (name.isEmpty()) {
                screenManager.showError("Validation Error", "Food name is required!");
                return;
            }

            if (id.isEmpty()) {
                screenManager.showError("Validation Error", "Food ID is required!");
                return;
            }

            Food food = new Food();
            food.setName(name);
            food.setFoodID(Integer.parseInt(id));
            food.setItemID(food.getFoodID());
            food.setPrice(Double.parseDouble(price));
            food.setExpiryDate(expiry);
            food.setAveragePricePerKg(Double.parseDouble(avgPrice));

            // Add ingredients
            for (Ingredient ingredient : ingredients) {
                food.addIngredient(ingredient);
            }

            // Add author
            food.addAuthor(author);

            // Add to system
            author.getFormulatedItems().add(food);
            screenManager.getDataManager().addFormulation(food);

            screenManager.getDataManager().getAuditTrail().logAction(
                    "AUTHOR:" + author.getName(),
                    "Created food formulation: " + food.getName() + " (ID: " + food.getItemID() + ")"
            );

            screenManager.showInformation("Success", "Food formulation created successfully!");
            screenManager.showAuthorDashboard();

        } catch (NumberFormatException ex) {
            screenManager.showError("Input Error", "Please enter valid numeric values!");
        } catch (Exception ex) {
            screenManager.showError("Error", "Failed to create food formulation: " + ex.getMessage());
        }
    }

    private void createDrinkFormulation(String name, String id, String price,
                                        String avgPrice, String expiry,
                                        ObservableList<Ingredient> ingredients) {
        try {
            if (name.isEmpty()) {
                screenManager.showError("Validation Error", "Drink name is required!");
                return;
            }

            if (id.isEmpty()) {
                screenManager.showError("Validation Error", "Drink ID is required!");
                return;
            }

            Drink drink = new Drink();
            drink.setName(name);
            drink.setDrinkID(Integer.parseInt(id));
            drink.setItemID(drink.getDrinkID());
            drink.setPrice(Double.parseDouble(price));
            drink.setExpiryDate(expiry);
            drink.setAveragePricePerKg(Double.parseDouble(avgPrice));

            // Add ingredients
            for (Ingredient ingredient : ingredients) {
                drink.addIngredient(ingredient);
            }

            // Add author
            drink.addAuthor(author);

            // Add to system
            author.getFormulatedItems().add(drink);
            screenManager.getDataManager().addFormulation(drink);

            screenManager.getDataManager().getAuditTrail().logAction(
                    "AUTHOR:" + author.getName(),
                    "Created drink formulation: " + drink.getName() + " (ID: " + drink.getItemID() + ")"
            );

            screenManager.showInformation("Success", "Drink formulation created successfully!");
            screenManager.showAuthorDashboard();

        } catch (NumberFormatException ex) {
            screenManager.showError("Input Error", "Please enter valid numeric values!");
        } catch (Exception ex) {
            screenManager.showError("Error", "Failed to create drink formulation: " + ex.getMessage());
        }
    }

    private TextField createGridField(GridPane grid, String labelText, int row, boolean withLabel) {
        if (withLabel) {
            Label label = new Label(labelText);
            label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            grid.add(label, 0, row);
        }
        TextField textField = new TextField();
        textField.setStyle("-fx-font-size: 12px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        grid.add(textField, 1, row);
        return textField;
    }

    private VBox createSectionSeparator(String title, String color) {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        Label label = new Label(title);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.setTextFill(javafx.scene.paint.Color.web(color));

        VBox section = new VBox(5, label, separator);
        return section;
    }

    public Scene getScene() {
        return scene;
    }
}