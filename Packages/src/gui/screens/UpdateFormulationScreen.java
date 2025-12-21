package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Persons.Author;
import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

        tabPane.getTabs().add(tabBasic);

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

        Scene dialogScene = new Scene(vbox, 600, 400);
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

    public Scene getScene() {
        return scene;
    }
}