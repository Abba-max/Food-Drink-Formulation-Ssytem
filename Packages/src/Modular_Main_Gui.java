//import MyClasses.Database.DatabaseConfig;
//import MyClasses.Database.DatabaseManager;
//import javafx.application.Application;
//import javafx.stage.Stage;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import gui.components.ScreenManager;
//
///**
// * Main entry point for the Food & Drink Formulation Management System
// */
//public class Modular_Main_GUI extends Application {
//
//    // System components accessible to all screens
//    public static DatabaseManager databaseManager;
//    public static ScreenManager screenManager;
//
//    @Override
//    public void start(Stage primaryStage) {
//        try {
//            // Initialize database FIRST
//            if (DatabaseConfig.testConnection()) {
//                databaseManager = new DatabaseManager();
//
//                // THEN create ScreenManager with databaseManager
//                screenManager = new ScreenManager(primaryStage, databaseManager);
//
//                // Show welcome screen
//                screenManager.showWelcomeScreen();
//
//                // Configure primary stage
//                primaryStage.setTitle("Food & Drink Formulation Management System");
//                primaryStage.setWidth(1200);
//                primaryStage.setHeight(700);
//                primaryStage.show();
//
//            } else {
//                showAlert(AlertType.ERROR, "Database Connection Failed",
//                        "Could not connect to database. Please check your configuration.");
//                System.exit(1);
//            }
//        } catch (Exception e) {
//            showAlert(AlertType.ERROR, "Initialization Error",
//                    "Failed to initialize system: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    private void showAlert(AlertType type, String title, String message) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//
//        try {
//            alert.showAndWait();
//        } catch (Exception e) {
//            // Fallback if there's an issue showing the alert
//            alert.show();
//        }
//    }
//
////    public static void main(String[] args) {
////        launch(args);
////    }
//}