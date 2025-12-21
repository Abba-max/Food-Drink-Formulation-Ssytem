package gui.screens;

import gui.components.ScreenManager;
import gui.components.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Customer Registration Screen
 */
public class RegistrationScreen {
    private Scene scene;
    private ScreenManager screenManager;

    public RegistrationScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        createScreen();
    }

    private void createScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                UIComponents.COLOR_SECONDARY + ", " + UIComponents.COLOR_PRIMARY + ");");

        VBox header = UIComponents.createHeader("REGISTRATION", "New Customer Registration");
        root.setTop(header);

        // Center content - registration form
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.TOP_CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(500);
        formBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 8;");

        // Form fields
        TextField txtId = createFormField("Customer ID:", "Enter unique ID");
        TextField txtName = createFormField("Name:", "Enter your name");
        TextField txtAddress = createFormField("Address:", "Enter your address");
        TextField txtContact = createFormField("Contact:", "Enter phone/email");
        TextField txtDob = createFormField("Date of Birth:", "YYYY-MM-DD");
        TextField txtAge = createFormField("Age:", "Enter your age");
        PasswordField txtPassword = createPasswordField("Password:", "Minimum 4 characters");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnRegister = UIComponents.createMenuButton("Register", UIComponents.COLOR_SUCCESS);
        Button btnCancel = UIComponents.createMenuButton("Cancel", UIComponents.COLOR_NEUTRAL);

        btnRegister.setOnAction(e -> handleRegistration(
                txtId.getText(), txtName.getText(), txtAddress.getText(),
                txtContact.getText(), txtDob.getText(), txtAge.getText(),
                txtPassword.getText()
        ));

        btnCancel.setOnAction(e -> screenManager.showWelcomeScreen());

        buttonBox.getChildren().addAll(btnRegister, btnCancel);
        formBox.getChildren().addAll(txtId, txtName, txtAddress, txtContact, txtDob, txtAge, txtPassword, buttonBox);

        HBox centerContainer = new HBox(formBox);
        centerContainer.setAlignment(Pos.CENTER);
        scrollPane.setContent(centerContainer);
        root.setCenter(scrollPane);

        scene = new Scene(root, ScreenManager.WINDOW_WIDTH, ScreenManager.WINDOW_HEIGHT);
    }

    private TextField createFormField(String labelText, String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-font-size: 14px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        return field;
    }

    private PasswordField createPasswordField(String labelText, String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setStyle("-fx-font-size: 14px; -fx-background-radius: 3; -fx-padding: 5; " +
                "-fx-background-color: white; -fx-border-color: " + UIComponents.COLOR_NEUTRAL + ";");
        return field;
    }

    private void handleRegistration(String id, String name, String address,
                                    String contact, String dob, String age, String password) {
        try {
            if (name.isEmpty()) {
                screenManager.showError("Validation Error", "Name cannot be empty!");
                return;
            }
            if (password.length() < 4) {
                screenManager.showError("Validation Error", "Password must be at least 4 characters!");
                return;
            }

            screenManager.showInformation("Registration Successful",
                    "Welcome! You can now login with Name: " + name);
            screenManager.showWelcomeScreen();

        } catch (NumberFormatException ex) {
            screenManager.showError("Invalid Input", "Please enter valid numbers for ID and Age");
        } catch (Exception ex) {
            screenManager.showError("Registration Error", "Error during registration: " + ex.getMessage());
        }
    }

    public Scene getScene() {
        return scene;
    }
}