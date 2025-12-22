package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Persons.Author;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Update Formulation Screen
 */
public class UpdateFormulationScreen {
    private Scene scene;
    private ScreenManager screenManager;
    private Author author;

    public UpdateFormulationScreen(ScreenManager screenManager, Author author) {
        this.screenManager = screenManager;
        this.author = author;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                UIComponents.COLOR_AUTHOR + ", #21618C);");

        VBox header = UIComponents.createHeader("UPDATE FORMULATION", "Author: " + author.getName());
        root.setTop(header);

        if (author.getFormulatedItems().isEmpty()) {
            VBox contentBox = new VBox(20);
            contentBox.setAlignment(Pos.CENTER);
            contentBox.setPadding(new Insets(30));

            Label lblEmpty = new Label("You haven't created any formulations yet.");
            lblEmpty.setTextFill(javafx.scene.paint.Color.WHITE);
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));

            Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
            btnBack.setOnAction(e -> screenManager.showAuthorDashboard());

            contentBox.getChildren().addAll(lblEmpty, btnBack);
            root.setCenter(contentBox);
        } else {
            VBox contentBox = new VBox(20);
            contentBox.setAlignment(Pos.CENTER);
            contentBox.setPadding(new Insets(30));

            Label lblSelect = new Label("Select formulation to update:");
            lblSelect.setTextFill(javafx.scene.paint.Color.WHITE);
            lblSelect.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

            ComboBox<Item> comboFormulations = new ComboBox<>();
            ObservableList<Item> items = FXCollections.observableArrayList(author.getFormulatedItems());
            comboFormulations.setItems(items);
            comboFormulations.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
            comboFormulations.setCellFactory(param -> new ListCell<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " (ID: " + item.getItemID() + ", Type: " +
                                (item instanceof Food ? "Food" : "Drink") + ")");
                    }
                }
            });

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);

            Button btnUpdate = UIComponents.createMenuButton("Update Selected", UIComponents.COLOR_INFO);
            Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);

            btnUpdate.setOnAction(e -> {
                Item selected = comboFormulations.getValue();
                if (selected != null) {
                    showUpdateDialog(selected);
                } else {
                    screenManager.showError("Selection Error", "Please select a formulation to update.");
                }
            });

            btnBack.setOnAction(e -> screenManager.showAuthorDashboard());

            buttonBox.getChildren().addAll(btnUpdate, btnBack);
            contentBox.getChildren().addAll(lblSelect, comboFormulations, buttonBox);
            root.setCenter(contentBox);
        }

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private void showUpdateDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update: " + item.getName());
        dialog.initOwner(screenManager.getPrimaryStage());

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Tab tabBasic = new Tab("Basic Info");
        tabBasic.setClosable(false);
        tabBasic.setContent(createBasicUpdateForm(item));

        Tab tabIngredients = new Tab("Ingredients");
        tabIngredients.setClosable(false);
        tabIngredients.setContent(createIngredientsUpdateForm(item));

        Tab tabStandards = new Tab("Standards");
        tabStandards.setClosable(false);
        tabStandards.setContent(createStandardsUpdateForm(item));

        tabPane.getTabs().addAll(tabBasic, tabIngredients, tabStandards);

        Button btnSave = UIComponents.createMenuButton("Save Changes", UIComponents.COLOR_SUCCESS);
        btnSave.setOnAction(e -> {
            screenManager.getDataManager().updateFormulation(item);
            screenManager.getAuditTrail().logAction(
                    "AUTHOR:" + author.getName(),
                    "Updated formulation: " + item.getName()
            );
            screenManager.showInformation("Success", "Formulation updated successfully!");
            dialog.close();
            screenManager.showUpdateFormulationScreen(); // Refresh
        });

        VBox vbox = new VBox(10, tabPane, btnSave);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Scene dialogScene = new Scene(vbox, 600, 500);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private VBox createBasicUpdateForm(Item item) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField txtName = createFormField("Name:", item.getName());
        TextField txtPrice = createFormField("Price ($):", String.valueOf(item.getPrice()));
        TextField txtExpiry = createFormField("Expiry Date (YYYY-MM-DD):", item.getExpiryDate());

        txtName.textProperty().addListener((obs, oldVal, newVal) -> item.setName(newVal));
        txtPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                item.setPrice(Double.parseDouble(newVal));
            } catch (Exception e) {}
        });
        txtExpiry.textProperty().addListener((obs, oldVal, newVal) -> item.setExpiryDate(newVal));

        if (item instanceof Food) {
            Food food = (Food) item;
            TextField txtAvgPrice = createFormField("Avg Price per Kg ($):", String.valueOf(food.getAveragePricePerKg()));
            txtAvgPrice.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    food.setAveragePricePerKg(Double.parseDouble(newVal));
                } catch (Exception e) {}
            });
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            TextField txtAvgPrice = createFormField("Avg Price per Kg ($):", String.valueOf(drink.getAveragePricePerKg()));
            txtAvgPrice.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    drink.setAveragePricePerKg(Double.parseDouble(newVal));
                } catch (Exception e) {}
            });
        }

        return vbox;
    }

    private VBox createIngredientsUpdateForm(Item item) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        LinkedList<Ingredient> ingredients = getIngredients(item);
        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        if (ingredients != null) {
            ingredientsList.addAll(ingredients);
        }

        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    setText(ingredient.getName() + " (ID: " + ingredient.getIngredientID() + ")");
                }
            }
        });

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnRemove = createSmallButton("Remove Selected", UIComponents.COLOR_ERROR);
        Button btnEdit = createSmallButton("Edit Selected", UIComponents.COLOR_INFO);

        btnAdd.setOnAction(e -> showAddIngredientDialog(ingredientsList, item));
        btnRemove.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
                // Update the actual item
                if (item instanceof Food) {
                    ((Food) item).removeIngredient(selected.getIngredientID());
                } else if (item instanceof Drink) {
                    ((Drink) item).removeIngredient(selected.getIngredientID());
                }
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove, btnEdit);
        vbox.getChildren().addAll(listView, buttonBox);

        return vbox;
    }

    private VBox createStandardsUpdateForm(Item item) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        LinkedList<String> standards = null;
        if (item instanceof Food) {
            standards = ((Food) item).getStandards();
        } else if (item instanceof Drink) {
            standards = ((Drink) item).getStandards();
        }

        ObservableList<String> standardsList = FXCollections.observableArrayList();
        if (standards != null) {
            standardsList.addAll(standards);
        }

        ListView<String> listView = new ListView<>(standardsList);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        Button btnAdd = createSmallButton("Add Standard", UIComponents.COLOR_INFO);
        Button btnRemove = createSmallButton("Remove Selected", UIComponents.COLOR_ERROR);

        btnAdd.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Standard");
            dialog.setHeaderText("Enter standard:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(standard -> {
                standardsList.add(standard);
                // Update the actual item
                if (item instanceof Food) {
                    ((Food) item).addStandard(standard);
                } else if (item instanceof Drink) {
                    ((Drink) item).addStandard(standard);
                }
            });
        });

        btnRemove.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                standardsList.remove(selected);
                // Update the actual item
                if (item instanceof Food) {
                    ((Food) item).getStandards().remove(selected);
                } else if (item instanceof Drink) {
                    ((Drink) item).getStandards().remove(selected);
                }
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnRemove);
        vbox.getChildren().addAll(listView, buttonBox);

        return vbox;
    }

    private void showAddIngredientDialog(ObservableList<Ingredient> ingredientsList, Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Ingredient");
        dialog.initOwner(screenManager.getPrimaryStage());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label titleLabel = new Label("Add New Ingredient");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        vbox.getChildren().add(titleLabel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblId = new Label("Ingredient ID:");
        TextField txtId = new TextField();
        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);

        Label lblName = new Label("Name:");
        TextField txtName = new TextField();
        grid.add(lblName, 0, 1);
        grid.add(txtName, 1, 1);

        Label lblWeight = new Label("Weight (g):");
        TextField txtWeight = new TextField();
        grid.add(lblWeight, 0, 2);
        grid.add(txtWeight, 1, 2);

        Label lblVolume = new Label("Volume (ml):");
        TextField txtVolume = new TextField();
        grid.add(lblVolume, 0, 3);
        grid.add(txtVolume, 1, 3);

        Label lblFraction = new Label("Fraction (0.0-1.0):");
        TextField txtFraction = new TextField();
        grid.add(lblFraction, 0, 4);
        grid.add(txtFraction, 1, 4);

        Label lblUnit = new Label("Unit (e.g., grams, ml):");
        TextField txtUnit = new TextField();
        grid.add(lblUnit, 0, 5);
        grid.add(txtUnit, 1, 5);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnAdd = createSmallButton("Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnCancel = createSmallButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnAdd.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();

                if (name.isEmpty()) {
                    screenManager.showError("Validation Error", "Ingredient name is required!");
                    return;
                }

                double weight = txtWeight.getText().isEmpty() ? 0.0 : Double.parseDouble(txtWeight.getText().trim());
                double volume = txtVolume.getText().isEmpty() ? 0.0 : Double.parseDouble(txtVolume.getText().trim());
                double fraction = txtFraction.getText().isEmpty() ? 0.0 : Double.parseDouble(txtFraction.getText().trim());
                String unit = txtUnit.getText().trim();

                if (fraction < 0.0 || fraction > 1.0) {
                    screenManager.showError("Validation Error", "Fraction must be between 0.0 and 1.0!");
                    return;
                }

                MyClasses.Ingredients.Quantity quantity = new MyClasses.Ingredients.Quantity(weight, volume, fraction, unit);
                Ingredient ingredient = new Ingredient(id, name, quantity);
                ingredientsList.add(ingredient);

                // Add to the actual item
                if (item instanceof Food) {
                    ((Food) item).addIngredient(ingredient);
                } else if (item instanceof Drink) {
                    ((Drink) item).addIngredient(ingredient);
                }

                dialog.close();

            } catch (NumberFormatException ex) {
                screenManager.showError("Input Error", "Please enter valid numeric values for weight, volume, and fraction!");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnAdd, btnCancel);
        vbox.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(vbox, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private LinkedList<Ingredient> getIngredients(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            return ((Drink) item).getIngredients();
        }
        return null;
    }

    private TextField createFormField(String labelText, String value) {
        VBox fieldBox = new VBox(5);

        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        TextField textField = new TextField(value);
        textField.setStyle("-fx-font-size: 14px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        fieldBox.getChildren().addAll(label, textField);

        return textField;
    }

    private Button createSmallButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; " +
                "-fx-background-radius: 3; -fx-cursor: hand;");

        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; " +
                        "-fx-background-radius: 3; -fx-cursor: hand;"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; " +
                        "-fx-background-radius: 3; -fx-cursor: hand;"));

        return button;
    }

    public Scene getScene() {
        return scene;
    }
}