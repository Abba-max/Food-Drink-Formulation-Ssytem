package gui.screens;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Conditions.*;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Ingredients.Quantity;
import MyClasses.Persons.Author;
import MyClasses.Persons.ConsumerSpecificInfo;
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
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.Optional;

/**
 * Complete Create Formulation Screen
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

        Tab tabFood = new Tab("🍕 Food Formulation");
        tabFood.setClosable(false);
        tabFood.setContent(createFoodFormulationForm());

        Tab tabDrink = new Tab("🥤 Drink Formulation");
        tabDrink.setClosable(false);
        tabDrink.setContent(createDrinkFormulationForm());

        tabPane.getTabs().addAll(tabFood, tabDrink);

        // Back button at the bottom
        Button btnBack = UIComponents.createMenuButton("Back to Dashboard", UIComponents.COLOR_NEUTRAL);
        btnBack.setOnAction(e -> screenManager.showAuthorDashboard());

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(tabPane, btnBack);

        scene = new Scene(mainLayout, 1000, 750);
    }

    private ScrollPane createFoodFormulationForm() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(25));
        scrollPane.setContent(vbox);

        // Title
        Label titleLabel = new Label("Create Food Formulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        vbox.getChildren().add(titleLabel);

        // ============ BASIC INFORMATION SECTION ============
        vbox.getChildren().add(createSectionSeparator("📋 BASIC INFORMATION", UIComponents.COLOR_SUCCESS));

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(15);
        basicGrid.setVgap(12);
        basicGrid.setPadding(new Insets(15));
        basicGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtName = createGridField(basicGrid, "Food Name:", 0, true, "Enter food name (required)");
        TextField txtId = createGridField(basicGrid, "Food ID:", 1, true, "Enter unique ID (required)");
        TextField txtPrice = createGridField(basicGrid, "Price ($):", 2, true, "e.g., 9.99");
        TextField txtAvgPrice = createGridField(basicGrid, "Avg Price per Kg ($):", 3, true, "e.g., 15.50");
        TextField txtExpiry = createGridField(basicGrid, "Expiry Date:", 4, true, "YYYY-MM-DD");

        vbox.getChildren().add(basicGrid);

        // ============ INGREDIENTS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🥗 INGREDIENTS", UIComponents.COLOR_INFO));

        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        ListView<Ingredient> listView = createIngredientsListView(ingredientsList);

        HBox ingredientControls = new HBox(10);
        ingredientControls.setAlignment(Pos.CENTER);
        ingredientControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnAddIngredient = UIComponents.createSmallButton("➕ Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnRemoveIngredient = UIComponents.createSmallButton("🗑️ Remove Selected", UIComponents.COLOR_ERROR);
        Button btnEditIngredient = UIComponents.createSmallButton("✏️ Edit Selected", UIComponents.COLOR_INFO);

        btnAddIngredient.setOnAction(e -> showAddIngredientDialog(ingredientsList));
        btnRemoveIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
            }
        });
        btnEditIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditIngredientDialog(ingredientsList, selected);
            }
        });

        ingredientControls.getChildren().addAll(btnAddIngredient, btnEditIngredient, btnRemoveIngredient);
        vbox.getChildren().addAll(listView, ingredientControls);

        // ============ LAB CONDITIONS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🔬 LAB CONDITIONS", UIComponents.COLOR_PURPLE));

        GridPane labGrid = new GridPane();
        labGrid.setHgap(15);
        labGrid.setVgap(12);
        labGrid.setPadding(new Insets(15));
        labGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtTemp = createGridField(labGrid, "Temperature (°C):", 0, true, "e.g., 25.0");
        TextField txtPressure = createGridField(labGrid, "Pressure (kPa):", 1, true, "e.g., 101.3");
        TextField txtMoisture = createGridField(labGrid, "Moisture (%):", 2, true, "e.g., 65.0");
        TextField txtVibration = createGridField(labGrid, "Vibration Level:", 3, true, "e.g., 0.5");
        TextField txtPeriod = createGridField(labGrid, "Time Period (min):", 4, true, "e.g., 120");

        vbox.getChildren().add(labGrid);

        // ============ PREPARATION PROTOCOL SECTION ============
        vbox.getChildren().add(createSectionSeparator("👨‍🍳 PREPARATION PROTOCOL", UIComponents.COLOR_INFO));

        ObservableList<String> stepsList = FXCollections.observableArrayList();
        ListView<String> stepsListView = createStepsListView(stepsList);

        HBox protocolControls = new HBox(10);
        protocolControls.setAlignment(Pos.CENTER);
        protocolControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnAddStep = UIComponents.createSmallButton("➕ Add Step", UIComponents.COLOR_INFO);
        Button btnRemoveStep = UIComponents.createSmallButton("🗑️ Remove Step", UIComponents.COLOR_ERROR);
        Button btnEditStep = UIComponents.createSmallButton("✏️ Edit Step", UIComponents.COLOR_INFO);

        btnAddStep.setOnAction(e -> showAddStepDialog(stepsList));
        btnRemoveStep.setOnAction(e -> {
            String selected = stepsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stepsList.remove(selected);
            }
        });
        btnEditStep.setOnAction(e -> {
            String selected = stepsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditStepDialog(stepsList, selected);
            }
        });

        protocolControls.getChildren().addAll(btnAddStep, btnEditStep, btnRemoveStep);
        vbox.getChildren().addAll(stepsListView, protocolControls);

        // ============ CONSERVATION CONDITIONS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🧊 CONSERVATION CONDITIONS", UIComponents.COLOR_SUCCESS));

        GridPane conserveGrid = new GridPane();
        conserveGrid.setHgap(15);
        conserveGrid.setVgap(12);
        conserveGrid.setPadding(new Insets(15));
        conserveGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtConserveTemp = createGridField(conserveGrid, "Temperature (°C):", 0, true, "e.g., 4.0");
        TextField txtConserveMoisture = createGridField(conserveGrid, "Moisture (%):", 1, true, "e.g., 75.0");
        TextField txtContainer = createGridField(conserveGrid, "Container Type:", 2, true, "e.g., Glass Jar, Plastic");

        vbox.getChildren().add(conserveGrid);

        // ============ CONSUMPTION CONDITIONS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🍽️ CONSUMPTION CONDITIONS", UIComponents.COLOR_PURPLE));

        GridPane consumeGrid = new GridPane();
        consumeGrid.setHgap(15);
        consumeGrid.setVgap(12);
        consumeGrid.setPadding(new Insets(15));
        consumeGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtConsumeTemp = createGridField(consumeGrid, "Serving Temperature (°C):", 0, true, "e.g., 18.0");
        TextField txtConsumeMoisture = createGridField(consumeGrid, "Serving Moisture (%):", 1, true, "e.g., 60.0");

        vbox.getChildren().add(consumeGrid);

        // ============ STANDARDS SECTION ============
        vbox.getChildren().add(createSectionSeparator("📜 STANDARDS", UIComponents.COLOR_INFO));

        ObservableList<String> standardsList = FXCollections.observableArrayList();
        ListView<String> standardsListView = createStandardsListView(standardsList);

        HBox standardsControls = new HBox(10);
        standardsControls.setAlignment(Pos.CENTER);
        standardsControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnAddStandard = UIComponents.createSmallButton("➕ Add Standard", UIComponents.COLOR_INFO);
        Button btnRemoveStandard = UIComponents.createSmallButton("🗑️ Remove Standard", UIComponents.COLOR_ERROR);
        Button btnEditStandard = UIComponents.createSmallButton("✏️ Edit Standard", UIComponents.COLOR_INFO);

        btnAddStandard.setOnAction(e -> showAddStandardDialog(standardsList));
        btnRemoveStandard.setOnAction(e -> {
            String selected = standardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                standardsList.remove(selected);
            }
        });
        btnEditStandard.setOnAction(e -> {
            String selected = standardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditStandardDialog(standardsList, selected);
            }
        });

        standardsControls.getChildren().addAll(btnAddStandard, btnEditStandard, btnRemoveStandard);
        vbox.getChildren().addAll(standardsListView, standardsControls);

        // ============ CONSUMER PROFILE SECTION ============
        vbox.getChildren().add(createSectionSeparator("👥 CONSUMER PROFILE", UIComponents.COLOR_SUCCESS));

        TextArea txtConsumerProfile = new TextArea();
        txtConsumerProfile.setPromptText("Enter target consumer profile (e.g., 'Adults 18-65, Health-conscious, Active lifestyle, Allergies: None')");
        txtConsumerProfile.setPrefRowCount(4);
        txtConsumerProfile.setWrapText(true);
        txtConsumerProfile.setStyle("-fx-control-inner-background: white; -fx-border-color: " +
                UIComponents.COLOR_NEUTRAL + "; -fx-border-radius: 5; -fx-font-size: 13px;");
        vbox.getChildren().add(txtConsumerProfile);

        // ============ CREATE BUTTONS ============
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(25, 0, 10, 0));

        Button btnCreate = UIComponents.createMenuButton("✅ Create Food Formulation", UIComponents.COLOR_SUCCESS);
        btnCreate.setStyle(btnCreate.getStyle() + "-fx-font-size: 16px; -fx-padding: 12 40;");

        Button btnCancel = UIComponents.createMenuButton("❌ Cancel", UIComponents.COLOR_NEUTRAL);

        btnCreate.setOnAction(e -> createFoodFormulation(
                txtName.getText().trim(),
                txtId.getText().trim(),
                txtPrice.getText().trim(),
                txtAvgPrice.getText().trim(),
                txtExpiry.getText().trim(),
                ingredientsList,
                txtTemp.getText().trim(),
                txtPressure.getText().trim(),
                txtMoisture.getText().trim(),
                txtVibration.getText().trim(),
                txtPeriod.getText().trim(),
                stepsList,
                txtConserveTemp.getText().trim(),
                txtConserveMoisture.getText().trim(),
                txtContainer.getText().trim(),
                txtConsumeTemp.getText().trim(),
                txtConsumeMoisture.getText().trim(),
                standardsList,
                txtConsumerProfile.getText().trim()
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

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(25));
        scrollPane.setContent(vbox);

        // Title
        Label titleLabel = new Label("Create Drink Formulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_INFO));
        vbox.getChildren().add(titleLabel);

        // ============ BASIC INFORMATION SECTION ============
        vbox.getChildren().add(createSectionSeparator("📋 BASIC INFORMATION", UIComponents.COLOR_INFO));

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(15);
        basicGrid.setVgap(12);
        basicGrid.setPadding(new Insets(15));
        basicGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtName = createGridField(basicGrid, "Drink Name:", 0, true, "Enter drink name (required)");
        TextField txtId = createGridField(basicGrid, "Drink ID:", 1, true, "Enter unique ID (required)");
        TextField txtPrice = createGridField(basicGrid, "Price ($):", 2, true, "e.g., 4.99");
        TextField txtAvgPrice = createGridField(basicGrid, "Avg Price per Kg ($):", 3, true, "e.g., 8.50");
        TextField txtExpiry = createGridField(basicGrid, "Expiry Date:", 4, true, "YYYY-MM-DD");

        vbox.getChildren().add(basicGrid);

        // ============ INGREDIENTS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🥗 INGREDIENTS", UIComponents.COLOR_SUCCESS));

        ObservableList<Ingredient> ingredientsList = FXCollections.observableArrayList();
        ListView<Ingredient> listView = createIngredientsListView(ingredientsList);

        HBox ingredientControls = new HBox(10);
        ingredientControls.setAlignment(Pos.CENTER);
        ingredientControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnAddIngredient = UIComponents.createSmallButton("➕ Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnRemoveIngredient = UIComponents.createSmallButton("🗑️ Remove Selected", UIComponents.COLOR_ERROR);
        Button btnEditIngredient = UIComponents.createSmallButton("✏️ Edit Selected", UIComponents.COLOR_INFO);

        btnAddIngredient.setOnAction(e -> showAddIngredientDialog(ingredientsList));
        btnRemoveIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ingredientsList.remove(selected);
            }
        });
        btnEditIngredient.setOnAction(e -> {
            Ingredient selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditIngredientDialog(ingredientsList, selected);
            }
        });

        ingredientControls.getChildren().addAll(btnAddIngredient, btnEditIngredient, btnRemoveIngredient);
        vbox.getChildren().addAll(listView, ingredientControls);

        // ============ LAB CONDITIONS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🔬 LAB CONDITIONS", UIComponents.COLOR_PURPLE));

        GridPane labGrid = new GridPane();
        labGrid.setHgap(15);
        labGrid.setVgap(12);
        labGrid.setPadding(new Insets(15));
        labGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtTemp = createGridField(labGrid, "Temperature (°C):", 0, true, "e.g., 5.0");
        TextField txtPressure = createGridField(labGrid, "Pressure (kPa):", 1, true, "e.g., 101.3");
        TextField txtMoisture = createGridField(labGrid, "Moisture (%):", 2, true, "e.g., 70.0");
        TextField txtVibration = createGridField(labGrid, "Vibration Level:", 3, true, "e.g., 0.3");
        TextField txtPeriod = createGridField(labGrid, "Time Period (min):", 4, true, "e.g., 90");

        vbox.getChildren().add(labGrid);

        // ============ PREPARATION PROTOCOL SECTION ============
        vbox.getChildren().add(createSectionSeparator("👨‍🍳 PREPARATION PROTOCOL", UIComponents.COLOR_INFO));

        ObservableList<String> stepsList = FXCollections.observableArrayList();
        ListView<String> stepsListView = createStepsListView(stepsList);

        HBox protocolControls = new HBox(10);
        protocolControls.setAlignment(Pos.CENTER);
        protocolControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnAddStep = UIComponents.createSmallButton("➕ Add Step", UIComponents.COLOR_INFO);
        Button btnRemoveStep = UIComponents.createSmallButton("🗑️ Remove Step", UIComponents.COLOR_ERROR);
        Button btnEditStep = UIComponents.createSmallButton("✏️ Edit Step", UIComponents.COLOR_INFO);

        btnAddStep.setOnAction(e -> showAddStepDialog(stepsList));
        btnRemoveStep.setOnAction(e -> {
            String selected = stepsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stepsList.remove(selected);
            }
        });
        btnEditStep.setOnAction(e -> {
            String selected = stepsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditStepDialog(stepsList, selected);
            }
        });

        protocolControls.getChildren().addAll(btnAddStep, btnEditStep, btnRemoveStep);
        vbox.getChildren().addAll(stepsListView, protocolControls);

        // ============ CONSERVATION CONDITIONS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🧊 CONSERVATION CONDITIONS", UIComponents.COLOR_SUCCESS));

        GridPane conserveGrid = new GridPane();
        conserveGrid.setHgap(15);
        conserveGrid.setVgap(12);
        conserveGrid.setPadding(new Insets(15));
        conserveGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtConserveTemp = createGridField(conserveGrid, "Temperature (°C):", 0, true, "e.g., 3.0");
        TextField txtConserveMoisture = createGridField(conserveGrid, "Moisture (%):", 1, true, "e.g., 80.0");
        TextField txtContainer = createGridField(conserveGrid, "Container Type:", 2, true, "e.g., Plastic Bottle, Can");

        vbox.getChildren().add(conserveGrid);

        // ============ CONSUMPTION CONDITIONS SECTION ============
        vbox.getChildren().add(createSectionSeparator("🍽️ CONSUMPTION CONDITIONS", UIComponents.COLOR_PURPLE));

        GridPane consumeGrid = new GridPane();
        consumeGrid.setHgap(15);
        consumeGrid.setVgap(12);
        consumeGrid.setPadding(new Insets(15));
        consumeGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtConsumeTemp = createGridField(consumeGrid, "Serving Temperature (°C):", 0, true, "e.g., 8.0");
        TextField txtConsumeMoisture = createGridField(consumeGrid, "Serving Moisture (%):", 1, true, "e.g., 85.0");

        vbox.getChildren().add(consumeGrid);

        // ============ STANDARDS SECTION ============
        vbox.getChildren().add(createSectionSeparator("📜 STANDARDS", UIComponents.COLOR_INFO));

        ObservableList<String> standardsList = FXCollections.observableArrayList();
        ListView<String> standardsListView = createStandardsListView(standardsList);

        HBox standardsControls = new HBox(10);
        standardsControls.setAlignment(Pos.CENTER);
        standardsControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnAddStandard = UIComponents.createSmallButton("➕ Add Standard", UIComponents.COLOR_INFO);
        Button btnRemoveStandard = UIComponents.createSmallButton("🗑️ Remove Standard", UIComponents.COLOR_ERROR);
        Button btnEditStandard = UIComponents.createSmallButton("✏️ Edit Standard", UIComponents.COLOR_INFO);

        btnAddStandard.setOnAction(e -> showAddStandardDialog(standardsList));
        btnRemoveStandard.setOnAction(e -> {
            String selected = standardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                standardsList.remove(selected);
            }
        });
        btnEditStandard.setOnAction(e -> {
            String selected = standardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditStandardDialog(standardsList, selected);
            }
        });

        standardsControls.getChildren().addAll(btnAddStandard, btnEditStandard, btnRemoveStandard);
        vbox.getChildren().addAll(standardsListView, standardsControls);

        // ============ CONSUMER PROFILE SECTION ============
        vbox.getChildren().add(createSectionSeparator("👥 CONSUMER PROFILE", UIComponents.COLOR_SUCCESS));

        TextArea txtConsumerProfile = new TextArea();
        txtConsumerProfile.setPromptText("Enter target consumer profile (e.g., 'Adults 18-65, Health-conscious, Active lifestyle, Allergies: None')");
        txtConsumerProfile.setPrefRowCount(4);
        txtConsumerProfile.setWrapText(true);
        txtConsumerProfile.setStyle("-fx-control-inner-background: white; -fx-border-color: " +
                UIComponents.COLOR_NEUTRAL + "; -fx-border-radius: 5; -fx-font-size: 13px;");
        vbox.getChildren().add(txtConsumerProfile);

        // ============ CREATE BUTTONS ============
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(25, 0, 10, 0));

        Button btnCreate = UIComponents.createMenuButton("✅ Create Drink Formulation", UIComponents.COLOR_INFO);
        btnCreate.setStyle(btnCreate.getStyle() + "-fx-font-size: 16px; -fx-padding: 12 40;");

        Button btnCancel = UIComponents.createMenuButton("❌ Cancel", UIComponents.COLOR_NEUTRAL);

        btnCreate.setOnAction(e -> createDrinkFormulation(
                txtName.getText().trim(),
                txtId.getText().trim(),
                txtPrice.getText().trim(),
                txtAvgPrice.getText().trim(),
                txtExpiry.getText().trim(),
                ingredientsList,
                txtTemp.getText().trim(),
                txtPressure.getText().trim(),
                txtMoisture.getText().trim(),
                txtVibration.getText().trim(),
                txtPeriod.getText().trim(),
                stepsList,
                txtConserveTemp.getText().trim(),
                txtConserveMoisture.getText().trim(),
                txtContainer.getText().trim(),
                txtConsumeTemp.getText().trim(),
                txtConsumeMoisture.getText().trim(),
                standardsList,
                txtConsumerProfile.getText().trim()
        ));

        btnCancel.setOnAction(e -> screenManager.showAuthorDashboard());

        buttonBox.getChildren().addAll(btnCreate, btnCancel);
        vbox.getChildren().add(buttonBox);

        return scrollPane;
    }

    // ============ CREATE FOOD FORMULATION ============
    private void createFoodFormulation(String name, String id, String price,
                                       String avgPrice, String expiry,
                                       ObservableList<Ingredient> ingredientsList,
                                       String temp, String pressure, String moisture,
                                       String vibration, String period,
                                       ObservableList<String> stepsList,
                                       String conserveTemp, String conserveMoisture, String container,
                                       String consumeTemp, String consumeMoisture,
                                       ObservableList<String> standardsList,
                                       String consumerProfile) {
        try {
            // Validate required fields
            if (name.isEmpty()) {
                screenManager.showError("Validation Error", "Food name is required!");
                return;
            }
            if (id.isEmpty()) {
                screenManager.showError("Validation Error", "Food ID is required!");
                return;
            }
            if (price.isEmpty()) {
                screenManager.showError("Validation Error", "Price is required!");
                return;
            }

            // Create food
            Food food = new Food();
            food.setName(name);
            food.setFoodID(Integer.parseInt(id));
            food.setItemID(food.getFoodID());
            food.setPrice(Double.parseDouble(price));
            food.setExpiryDate(expiry.isEmpty() ? "N/A" : expiry);
            food.setAveragePricePerKg(Double.parseDouble(avgPrice.isEmpty() ? "0.0" : avgPrice));

            // Add ingredients
            for (Ingredient ingredient : ingredientsList) {
                food.addIngredient(ingredient);
            }

            // Set lab conditions
            if (!temp.isEmpty() || !pressure.isEmpty() || !moisture.isEmpty() ||
                    !vibration.isEmpty() || !period.isEmpty()) {
                Optcondition labCond = new Optcondition();
                if (!temp.isEmpty()) labCond.setTemp(Double.parseDouble(temp));
                if (!pressure.isEmpty()) labCond.setPressure(Double.parseDouble(pressure));
                if (!moisture.isEmpty()) labCond.setMoisture(Double.parseDouble(moisture));
                if (!vibration.isEmpty()) labCond.setVibration(Double.parseDouble(vibration));
                if (!period.isEmpty()) labCond.setPeriod(Integer.parseInt(period));
                food.setLabCondition(labCond);
            }

            // Set preparation protocol
            if (!stepsList.isEmpty()) {
                Prepprotocol protocol = new Prepprotocol();
                for (String step : stepsList) {
                    protocol.addStep(step, null);
                }
                food.setPrepprotocol(protocol);
            }

            // Set conservation conditions
            if (!conserveTemp.isEmpty() || !conserveMoisture.isEmpty() || !container.isEmpty()) {
                Conservecondition conserveCond = new Conservecondition();
                if (!conserveTemp.isEmpty()) conserveCond.setTemp(Double.parseDouble(conserveTemp));
                if (!conserveMoisture.isEmpty()) conserveCond.setMoisture(Double.parseDouble(conserveMoisture));
                if (!container.isEmpty()) conserveCond.setContainer(container);
                food.setConservecondition(conserveCond);
            }

            // Set consumption conditions
            if (!consumeTemp.isEmpty() || !consumeMoisture.isEmpty()) {
                Consumpcondition consumeCond = new Consumpcondition();
                if (!consumeTemp.isEmpty()) consumeCond.setTemperature(Double.parseDouble(consumeTemp));
                if (!consumeMoisture.isEmpty()) consumeCond.setMoisture(Double.parseDouble(consumeMoisture));
                food.setConsumpcondition(consumeCond);
            }

            // Add standards
            for (String standard : standardsList) {
                food.addStandard(standard);
            }

            // Set consumer profile
            if (!consumerProfile.isEmpty()) {
                ConsumerSpecificInfo consumerInfo = new ConsumerSpecificInfo();
                consumerInfo.setProfile(consumerProfile);
                food.setConsumerProfile(consumerInfo);
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
            ex.printStackTrace();
        } catch (Exception ex) {
            screenManager.showError("Error", "Failed to create food formulation: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ============ CREATE DRINK FORMULATION ============
    private void createDrinkFormulation(String name, String id, String price,
                                        String avgPrice, String expiry,
                                        ObservableList<Ingredient> ingredientsList,
                                        String temp, String pressure, String moisture,
                                        String vibration, String period,
                                        ObservableList<String> stepsList,
                                        String conserveTemp, String conserveMoisture, String container,
                                        String consumeTemp, String consumeMoisture,
                                        ObservableList<String> standardsList,
                                        String consumerProfile) {
        try {
            // Validate required fields
            if (name.isEmpty()) {
                screenManager.showError("Validation Error", "Drink name is required!");
                return;
            }
            if (id.isEmpty()) {
                screenManager.showError("Validation Error", "Drink ID is required!");
                return;
            }
            if (price.isEmpty()) {
                screenManager.showError("Validation Error", "Price is required!");
                return;
            }

            // Create drink
            Drink drink = new Drink();
            drink.setName(name);
            drink.setDrinkID(Integer.parseInt(id));
            drink.setItemID(drink.getDrinkID());
            drink.setPrice(Double.parseDouble(price));
            drink.setExpiryDate(expiry.isEmpty() ? "N/A" : expiry);
            drink.setAveragePricePerKg(Double.parseDouble(avgPrice.isEmpty() ? "0.0" : avgPrice));

            // Add ingredients
            for (Ingredient ingredient : ingredientsList) {
                drink.addIngredient(ingredient);
            }

            // Set lab conditions
            if (!temp.isEmpty() || !pressure.isEmpty() || !moisture.isEmpty() ||
                    !vibration.isEmpty() || !period.isEmpty()) {
                Optcondition labCond = new Optcondition();
                if (!temp.isEmpty()) labCond.setTemp(Double.parseDouble(temp));
                if (!pressure.isEmpty()) labCond.setPressure(Double.parseDouble(pressure));
                if (!moisture.isEmpty()) labCond.setMoisture(Double.parseDouble(moisture));
                if (!vibration.isEmpty()) labCond.setVibration(Double.parseDouble(vibration));
                if (!period.isEmpty()) labCond.setPeriod(Integer.parseInt(period));
                drink.setLabCondition(labCond);
            }

            // Set preparation protocol
            if (!stepsList.isEmpty()) {
                Prepprotocol protocol = new Prepprotocol();
                for (String step : stepsList) {
                    protocol.addStep(step, null);
                }
                drink.setPrepprotocol(protocol);
            }

            // Set conservation conditions
            if (!conserveTemp.isEmpty() || !conserveMoisture.isEmpty() || !container.isEmpty()) {
                Conservecondition conserveCond = new Conservecondition();
                if (!conserveTemp.isEmpty()) conserveCond.setTemp(Double.parseDouble(conserveTemp));
                if (!conserveMoisture.isEmpty()) conserveCond.setMoisture(Double.parseDouble(conserveMoisture));
                if (!container.isEmpty()) conserveCond.setContainer(container);
                drink.setConservecondition(conserveCond);
            }

            // Set consumption conditions
            if (!consumeTemp.isEmpty() || !consumeMoisture.isEmpty()) {
                Consumpcondition consumeCond = new Consumpcondition();
                if (!consumeTemp.isEmpty()) consumeCond.setTemperature(Double.parseDouble(consumeTemp));
                if (!consumeMoisture.isEmpty()) consumeCond.setMoisture(Double.parseDouble(consumeMoisture));
                drink.setConsumpcondition(consumeCond);
            }

            // Add standards
            for (String standard : standardsList) {
                drink.addStandard(standard);
            }

            // Set consumer profile
            if (!consumerProfile.isEmpty()) {
                ConsumerSpecificInfo consumerInfo = new ConsumerSpecificInfo();
                consumerInfo.setProfile(consumerProfile);
                drink.setConsumerProfile(consumerInfo);
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
            ex.printStackTrace();
        } catch (Exception ex) {
            screenManager.showError("Error", "Failed to create drink formulation: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ============ HELPER METHODS ============

    private ListView<Ingredient> createIngredientsListView(ObservableList<Ingredient> ingredientsList) {
        ListView<Ingredient> listView = new ListView<>(ingredientsList);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-radius: 5; " +
                "-fx-background-color: white;");
        listView.setCellFactory(param -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(3);

                    Label nameLabel = new Label("• " + ingredient.getName() + " (ID: " + ingredient.getIngredientID() + ")");
                    nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));

                    vbox.getChildren().add(nameLabel);

                    if (ingredient.getQuantity() != null) {
                        Quantity q = ingredient.getQuantity();
                        HBox detailsBox = new HBox(10);
                        detailsBox.setPadding(new Insets(2, 0, 0, 15));

                        if (q.getWeight() > 0) {
                            Label weightLabel = new Label("⚖️ " + q.getWeight() + "g");
                            weightLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                            weightLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SECONDARY));
                            detailsBox.getChildren().add(weightLabel);
                        }

                        if (q.getVolume() > 0) {
                            Label volumeLabel = new Label("💧 " + q.getVolume() + "ml");
                            volumeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                            volumeLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SECONDARY));
                            detailsBox.getChildren().add(volumeLabel);
                        }

                        if (q.getFraction() > 0) {
                            Label fractionLabel = new Label("📊 " + String.format("%.2f", q.getFraction()));
                            fractionLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                            fractionLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SECONDARY));
                            detailsBox.getChildren().add(fractionLabel);
                        }

                        if (q.getUnit() != null && !q.getUnit().isEmpty()) {
                            Label unitLabel = new Label("📐 " + q.getUnit());
                            unitLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                            unitLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SECONDARY));
                            detailsBox.getChildren().add(unitLabel);
                        }

                        vbox.getChildren().add(detailsBox);
                    }

                    setGraphic(vbox);
                }
            }
        });
        return listView;
    }

    private ListView<String> createStepsListView(ObservableList<String> stepsList) {
        ListView<String> listView = new ListView<>(stepsList);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-radius: 5; " +
                "-fx-background-color: white;");
        listView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String step, boolean empty) {
                super.updateItem(step, empty);
                if (empty || step == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    Label numberLabel = new Label((getIndex() + 1) + ".");
                    numberLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    numberLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));
                    numberLabel.setMinWidth(30);

                    Label stepLabel = new Label(step);
                    stepLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
                    stepLabel.setWrapText(true);
                    stepLabel.setMaxWidth(600);

                    hbox.getChildren().addAll(numberLabel, stepLabel);
                    setGraphic(hbox);
                }
            }
        });
        return listView;
    }

    private ListView<String> createStandardsListView(ObservableList<String> standardsList) {
        ListView<String> listView = new ListView<>(standardsList);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-radius: 5; " +
                "-fx-background-color: white;");
        listView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String standard, boolean empty) {
                super.updateItem(standard, empty);
                if (empty || standard == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    Label iconLabel = new Label("📜");
                    iconLabel.setFont(Font.font("Segoe UI", 14));

                    Label standardLabel = new Label(standard);
                    standardLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
                    standardLabel.setWrapText(true);
                    standardLabel.setMaxWidth(600);

                    hbox.getChildren().addAll(iconLabel, standardLabel);
                    setGraphic(hbox);
                }
            }
        });
        return listView;
    }

    private void showAddIngredientDialog(ObservableList<Ingredient> ingredientsList) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Ingredient");
        dialog.initOwner(screenManager.getPrimaryStage());

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label titleLabel = new Label("➕ Add New Ingredient");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        TextField txtId = createGridField(grid, "Ingredient ID:", 0, true, "Unique number");
        TextField txtName = createGridField(grid, "Name:", 1, true, "Ingredient name");
        TextField txtWeight = createGridField(grid, "Weight (g):", 2, true, "0 if not applicable");
        TextField txtVolume = createGridField(grid, "Volume (ml):", 3, true, "0 if not applicable");
        TextField txtFraction = createGridField(grid, "Fraction (0.0-1.0):", 4, true, "e.g., 0.25");
        TextField txtUnit = createGridField(grid, "Unit:", 5, true, "e.g., grams, ml, cups");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnAdd = UIComponents.createSmallButton("➕ Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createSmallButton("❌ Cancel", UIComponents.COLOR_NEUTRAL);

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

                Quantity quantity = new Quantity(weight, volume, fraction, unit);
                Ingredient ingredient = new Ingredient(id, name, quantity);
                ingredientsList.add(ingredient);

                screenManager.showInformation("Success", "Ingredient added successfully!");
                dialog.close();

            } catch (NumberFormatException ex) {
                screenManager.showError("Input Error", "Please enter valid numeric values for ID, weight, volume, and fraction!");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnAdd, btnCancel);
        vbox.getChildren().addAll(titleLabel, grid, buttonBox);

        Scene scene = new Scene(vbox, 500, 550);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showEditIngredientDialog(ObservableList<Ingredient> ingredientsList, Ingredient ingredient) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Ingredient");
        dialog.initOwner(screenManager.getPrimaryStage());

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label titleLabel = new Label("✏️ Edit Ingredient");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_INFO));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + UIComponents.COLOR_NEUTRAL + "; -fx-border-width: 1;");

        // Pre-populate fields with existing values
        TextField txtId = createGridField(grid, "Ingredient ID:", 0, true, String.valueOf(ingredient.getIngredientID()));
        txtId.setText(String.valueOf(ingredient.getIngredientID()));
        txtId.setDisable(true); // ID should not be editable

        TextField txtName = createGridField(grid, "Name:", 1, true, ingredient.getName());
        txtName.setText(ingredient.getName());

        double weight = ingredient.getQuantity() != null ? ingredient.getQuantity().getWeight() : 0.0;
        double volume = ingredient.getQuantity() != null ? ingredient.getQuantity().getVolume() : 0.0;
        double fraction = ingredient.getQuantity() != null ? ingredient.getQuantity().getFraction() : 0.0;
        String unit = ingredient.getQuantity() != null ? ingredient.getQuantity().getUnit() : "";

        TextField txtWeight = createGridField(grid, "Weight (g):", 2, true, String.valueOf(weight));
        txtWeight.setText(String.valueOf(weight));

        TextField txtVolume = createGridField(grid, "Volume (ml):", 3, true, String.valueOf(volume));
        txtVolume.setText(String.valueOf(volume));

        TextField txtFraction = createGridField(grid, "Fraction (0.0-1.0):", 4, true, String.valueOf(fraction));
        txtFraction.setText(String.valueOf(fraction));

        TextField txtUnit = createGridField(grid, "Unit:", 5, true, unit);
        txtUnit.setText(unit);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnUpdate = UIComponents.createSmallButton("💾 Update", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createSmallButton("❌ Cancel", UIComponents.COLOR_NEUTRAL);

        btnUpdate.setOnAction(e -> {
            try {
                String name = txtName.getText().trim();

                if (name.isEmpty()) {
                    screenManager.showError("Validation Error", "Ingredient name is required!");
                    return;
                }

                weight = txtWeight.getText().isEmpty() ? 0.0 : Double.parseDouble(txtWeight.getText().trim());
                volume = txtVolume.getText().isEmpty() ? 0.0 : Double.parseDouble(txtVolume.getText().trim());
                fraction = txtFraction.getText().isEmpty() ? 0.0 : Double.parseDouble(txtFraction.getText().trim());
                unit = txtUnit.getText().trim();

                if (fraction < 0.0 || fraction > 1.0) {
                    screenManager.showError("Validation Error", "Fraction must be between 0.0 and 1.0!");
                    return;
                }

                // Update ingredient
                ingredient.setName(name);
                Quantity quantity = new Quantity(weight, volume, fraction, unit);
                // Note: Ingredient class might need a setQuantity method
                // If not available, you may need to create a new ingredient

                screenManager.showInformation("Success", "Ingredient updated successfully!");
                dialog.close();

            } catch (NumberFormatException ex) {
                screenManager.showError("Input Error", "Please enter valid numeric values for weight, volume, and fraction!");
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnUpdate, btnCancel);
        vbox.getChildren().addAll(titleLabel, grid, buttonBox);

        Scene scene = new Scene(vbox, 500, 550);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAddStepDialog(ObservableList<String> stepsList) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Preparation Step");
        dialog.setHeaderText("Enter step description:");
        dialog.setContentText("Step:");
        dialog.initOwner(screenManager.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(step -> {
            if (!step.trim().isEmpty()) {
                stepsList.add(step.trim());
            }
        });
    }

    private void showEditStepDialog(ObservableList<String> stepsList, String oldStep) {
        TextInputDialog dialog = new TextInputDialog(oldStep);
        dialog.setTitle("Edit Preparation Step");
        dialog.setHeaderText("Edit step description:");
        dialog.setContentText("Step:");
        dialog.initOwner(screenManager.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStep -> {
            if (!newStep.trim().isEmpty()) {
                int index = stepsList.indexOf(oldStep);
                if (index != -1) {
                    stepsList.set(index, newStep.trim());
                }
            }
        });
    }

    private void showAddStandardDialog(ObservableList<String> standardsList) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Standard");
        dialog.setHeaderText("Enter standard:");
        dialog.setContentText("Standard:");
        dialog.initOwner(screenManager.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(standard -> {
            if (!standard.trim().isEmpty()) {
                standardsList.add(standard.trim());
            }
        });
    }

    private void showEditStandardDialog(ObservableList<String> standardsList, String oldStandard) {
        TextInputDialog dialog = new TextInputDialog(oldStandard);
        dialog.setTitle("Edit Standard");
        dialog.setHeaderText("Edit standard:");
        dialog.setContentText("Standard:");
        dialog.initOwner(screenManager.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStandard -> {
            if (!newStandard.trim().isEmpty()) {
                int index = standardsList.indexOf(oldStandard);
                if (index != -1) {
                    standardsList.set(index, newStandard.trim());
                }
            }
        });
    }

    private TextField createGridField(GridPane grid, String labelText, int row, boolean withLabel, String placeholder) {
        if (withLabel) {
            Label label = new Label(labelText);
            label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            label.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_PRIMARY));
            grid.add(label, 0, row);
        }
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setStyle("-fx-font-size: 13px; -fx-background-radius: 4; -fx-padding: 8; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        grid.add(textField, 1, row);
        return textField;
    }

    private VBox createSectionSeparator(String title, String color) {
        VBox section = new VBox(5);

        Label label = new Label(title);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        label.setTextFill(javafx.scene.paint.Color.web(color));

        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 10, 0));
        separator.setStyle("-fx-background-color: " + color + ";");

        section.getChildren().addAll(label, separator);
        return section;
    }

    public Scene getScene() {
        return scene;
    }
}