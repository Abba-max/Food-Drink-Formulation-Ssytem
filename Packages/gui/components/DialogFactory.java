package gui.components;

import MyClasses.Consumables.Item;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Ingredients.Quantity;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Factory for creating common dialogs
 */
public class DialogFactory {

    /**
     * Create a dialog for adding/editing ingredients
     */
    public static Stage createIngredientDialog(ObservableList<Ingredient> ingredientsList) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Ingredient");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label titleLabel = new Label("Add New Ingredient");
        titleLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));
        titleLabel.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));
        vbox.getChildren().add(titleLabel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtId = createGridTextField(grid, "Ingredient ID:", 0);
        TextField txtName = createGridTextField(grid, "Name:", 1);
        TextField txtWeight = createGridTextField(grid, "Weight (g):", 2);
        TextField txtVolume = createGridTextField(grid, "Volume (ml):", 3);
        TextField txtFraction = createGridTextField(grid, "Fraction (0.0-1.0):", 4);
        TextField txtUnit = createGridTextField(grid, "Unit (e.g., grams, ml):", 5);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnAdd = UIComponents.createSmallButton("Add Ingredient", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createSmallButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnAdd.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String name = txtName.getText().trim();

                if (name.isEmpty()) {
                    // Show error would be handled by caller
                    return;
                }

                double weight = txtWeight.getText().isEmpty() ? 0.0 : Double.parseDouble(txtWeight.getText().trim());
                double volume = txtVolume.getText().isEmpty() ? 0.0 : Double.parseDouble(txtVolume.getText().trim());
                double fraction = txtFraction.getText().isEmpty() ? 0.0 : Double.parseDouble(txtFraction.getText().trim());
                String unit = txtUnit.getText().trim();

                if (fraction < 0.0 || fraction > 1.0) {
                    // Show error would be handled by caller
                    return;
                }

                Quantity quantity = new Quantity(weight, volume, fraction, unit);
                Ingredient ingredient = new Ingredient(id, name, quantity);
                ingredientsList.add(ingredient);
                dialog.close();

            } catch (NumberFormatException ex) {
                // Show error would be handled by caller
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnAdd, btnCancel);
        vbox.getChildren().addAll(grid, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 400, 450);
        dialog.setScene(scene);
        return dialog;
    }

    /**
     * Create a dialog for setting veto on item
     */
    public static Stage createVetoDialog(Item item, javafx.event.EventHandler<javafx.event.ActionEvent> onConfirm) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Set Veto on " + item.getName());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label lblReason = new Label("Enter veto reason:");
        lblReason.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));

        TextArea txtReason = new TextArea();
        txtReason.setPrefRowCount(3);
        txtReason.setWrapText(true);
        txtReason.setStyle("-fx-control-inner-background: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnSet = UIComponents.createSmallButton("Set Veto", UIComponents.COLOR_ERROR);
        Button btnCancel = UIComponents.createSmallButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnSet.setOnAction(e -> {
            String reason = txtReason.getText().trim();
            if (reason.isEmpty()) {
                // Show error would be handled by caller
                return;
            }
            onConfirm.handle(e);
            dialog.close();
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnSet, btnCancel);
        vbox.getChildren().addAll(lblReason, txtReason, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 400, 300);
        dialog.setScene(scene);
        return dialog;
    }

    /**
     * Create a dialog for purchase confirmation
     */
    public static Stage createPurchaseDialog(Item item, javafx.event.EventHandler<javafx.event.ActionEvent> onPurchase) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Purchase: " + item.getName());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label lblItem = new Label("Item: " + item.getName());
        lblItem.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));

        Label lblPrice = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        lblPrice.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));
        lblPrice.setTextFill(javafx.scene.paint.Color.web(UIComponents.COLOR_SUCCESS));

        Label lblMethod = new Label("Select Payment Method:");
        ComboBox<String> comboMethod = new ComboBox<>();
        comboMethod.getItems().addAll("Credit Card", "Debit Card", "Mobile Payment", "Cash");
        comboMethod.setValue("Credit Card");
        comboMethod.setStyle("-fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnPurchase = UIComponents.createSmallButton("Confirm Purchase", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createSmallButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnPurchase.setOnAction(e -> {
            onPurchase.handle(e);
            dialog.close();
        });

        btnCancel.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(btnPurchase, btnCancel);
        vbox.getChildren().addAll(lblItem, lblPrice, lblMethod, comboMethod, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 300, 250);
        dialog.setScene(scene);
        return dialog;
    }

    /**
     * Create a dialog for feedback
     */
    public static Stage createFeedbackDialog(Item item,
                                             javafx.event.EventHandler<javafx.event.ActionEvent> onSubmit) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Feedback for: " + item.getName());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setStyle("-fx-background-color: " + UIComponents.COLOR_LIGHT + ";");

        Label lblQuestion = new Label("Did you like this formulation?");
        lblQuestion.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));

        ToggleGroup group = new ToggleGroup();
        RadioButton rbLike = new RadioButton("👍 Like");
        RadioButton rbDislike = new RadioButton("👎 Dislike");
        rbLike.setToggleGroup(group);
        rbDislike.setToggleGroup(group);
        rbLike.setSelected(true);

        Label lblComment = new Label("Comments:");
        TextArea txtComment = new TextArea();
        txtComment.setPrefRowCount(3);
        txtComment.setWrapText(true);
        txtComment.setStyle("-fx-control-inner-background: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnSubmit = UIComponents.createSmallButton("Submit Feedback", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createSmallButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnSubmit.setOnAction(e -> {
            onSubmit.handle(e);
            dialog.close();
        });

        btnCancel.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(lblQuestion, rbLike, rbDislike, lblComment, txtComment, buttonBox);
        buttonBox.getChildren().addAll(btnSubmit, btnCancel);

        javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 400, 300);
        dialog.setScene(scene);
        return dialog;
    }

    private static TextField createGridTextField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        label.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 12));
        grid.add(label, 0, row);

        TextField textField = new TextField();
        textField.setStyle("-fx-font-size: 12px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        grid.add(textField, 1, row);
        return textField;
    }
}