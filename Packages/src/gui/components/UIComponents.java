package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Factory for creating reusable UI components
 */
public class UIComponents {

    // Color constants
    public static final String COLOR_PRIMARY = "#2C3E50";
    public static final String COLOR_SECONDARY = "#34495E";
    public static final String COLOR_SUCCESS = "#27AE60";
    public static final String COLOR_ERROR = "#E74C3C";
    public static final String COLOR_INFO = "#3498DB";
    public static final String COLOR_LIGHT = "#ECF0F1";
    public static final String COLOR_NEUTRAL = "#95A5A6";
    public static final String COLOR_ADMIN = "#C0392B";
    public static final String COLOR_AUTHOR = "#2980B9";
    public static final String COLOR_CUSTOMER = "#16A085";
    public static final String COLOR_WARNING = "#C0392B";

    /**
     * Create a menu button with hover effects
     */
    public static Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                "-fx-background-radius: 4; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        button.setMaxWidth(400);
        button.setMinWidth(300);

        // Hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                        "-fx-background-radius: 4; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"));

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 40; " +
                        "-fx-background-radius: 4; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));

        return button;
    }

    /**
     * Create a small button
     */
    public static Button createSmallButton(String text, String color) {
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

    /**
     * Create header section
     */
    public static VBox createHeader(String title, String subtitle) {
        VBox header = new VBox(5);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.WHITE);

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubtitle.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(lblTitle, lblSubtitle);
        return header;
    }

    /**
     * Create footer section
     */
    public static HBox createFooter(String message) {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Label lblFooter = new Label(message);
        lblFooter.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblFooter.setTextFill(Color.LIGHTGRAY);

        footer.getChildren().add(lblFooter);
        return footer;
    }
}