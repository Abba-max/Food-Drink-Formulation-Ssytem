package gui.components;

import MyClasses.Database.DatabaseManager;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;
import gui.screens.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Manages screen transitions and shared data
 */
public class ScreenManager {
    private Stage primaryStage;
    public DataManager dataManager;

    // Current user session
    private Object currentUser;
    private String currentUserType;

    // Screen dimensions
    public static final double WINDOW_WIDTH = 1200;
    public static final double WINDOW_HEIGHT = 700;

    public ScreenManager(Stage primaryStage, DatabaseManager databaseManager) {
        this.primaryStage = primaryStage;
        this.dataManager = new DataManager(databaseManager);
        this.dataManager.loadAllData();
    }

    // ============ SCREEN NAVIGATION METHODS ============

    public void showWelcomeScreen() {
        WelcomeScreen screen = new WelcomeScreen(this);
        setScene(screen.getScene());
    }

    public void showLoginScreen(String userType) {
        LoginScreen screen = new LoginScreen(this, userType);
        setScene(screen.getScene());
    }

    public void showRegistrationScreen() {
        RegistrationScreen screen = new RegistrationScreen(this);
        setScene(screen.getScene());
    }

    public void showUserDashboard() {
        if ("ADMIN".equals(currentUserType)) {
            showAdminDashboard();
        } else if ("AUTHOR".equals(currentUserType)) {
            showAuthorDashboard();
        } else if ("CUSTOMER".equals(currentUserType)) {
            showCustomerDashboard();
        }
    }

    public void showAdminDashboard() {
        AdminDashboard screen = new AdminDashboard(this, (Admin) currentUser);
        setScene(screen.getScene());
    }

    public void showAuthorDashboard() {
        AuthorDashboard screen = new AuthorDashboard(this, (Author) currentUser);
        setScene(screen.getScene());
    }

    public void showCustomerDashboard() {
        CustomerDashboard screen = new CustomerDashboard(this, (Customer) currentUser);
        setScene(screen.getScene());
    }

    public void showAccountManagementScreen() {
        AccountManagementScreen screen = new AccountManagementScreen(this);
        setScene(screen.getScene());
    }

    public void showFormulationManagementScreen() {
        FormulationManagementScreen screen = new FormulationManagementScreen(this);
        setScene(screen.getScene());
    }

    public void showSystemStatisticsScreen() {
        SystemStatisticsScreen screen = new SystemStatisticsScreen(this);
        setScene(screen.getScene());
    }

    public void showAllAccountsScreen() {
        AllAccountsScreen screen = new AllAccountsScreen(this);
        setScene(screen.getScene());
    }

    public void showCheckFormulationIssuesScreen() {
        CheckFormulationIssuesScreen screen = new CheckFormulationIssuesScreen(this);
        setScene(screen.getScene());
    }

    public void showAuditTrailScreen() {
        AuditTrailScreen screen = new AuditTrailScreen(this);
        setScene(screen.getScene());
    }

    public void showCreateFormulationScreen() {
        CreateFormulationScreen screen = new CreateFormulationScreen(this, (Author) currentUser);
        setScene(screen.getScene());
    }

    public void showUpdateFormulationScreen() {
        UpdateFormulationScreen screen = new UpdateFormulationScreen(this, (Author) currentUser);
        setScene(screen.getScene());
    }

    public void showAuthorFormulationsScreen() {
        AuthorFormulationsScreen screen = new AuthorFormulationsScreen(this, (Author) currentUser);
        setScene(screen.getScene());
    }

    public void showAuthorCheckIssuesScreen() {
        AuthorCheckIssuesScreen screen = new AuthorCheckIssuesScreen(this, (Author) currentUser);
        setScene(screen.getScene());
    }

    public void showAuthorStatisticsScreen() {
        AuthorStatisticsScreen screen = new AuthorStatisticsScreen(this, (Author) currentUser);
        setScene(screen.getScene());
    }

    public void showCustomerBrowseCatalogScreen() {
        CustomerBrowseCatalogScreen screen = new CustomerBrowseCatalogScreen(this, (Customer) currentUser);
        setScene(screen.getScene());
    }

    public void showCustomerPurchasesScreen() {
        CustomerPurchasesScreen screen = new CustomerPurchasesScreen(this, (Customer) currentUser);
        setScene(screen.getScene());
    }

    public void showCustomerFavoritesScreen() {
        CustomerFavoritesScreen screen = new CustomerFavoritesScreen(this, (Customer) currentUser);
        setScene(screen.getScene());
    }

    public void showCustomerProfileScreen() {
        CustomerProfileScreen screen = new CustomerProfileScreen(this, (Customer) currentUser);
        setScene(screen.getScene());
    }

    public void showFormulationDetailsDialog(Object item) {
        FormulationDetailsDialog dialog = new FormulationDetailsDialog(this, item);
        dialog.show();
    }

    // ============ DATA MANAGEMENT ============

    public void setCurrentUser(Object user, String userType) {
        this.currentUser = user;
        this.currentUserType = userType;
    }

    public Object getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserType() {
        return currentUserType;
    }

    public void logout() {
        if (currentUser != null) {
            String userName = "";
            if (currentUser instanceof Admin) {
                userName = ((Admin) currentUser).getName();
            } else if (currentUser instanceof Author) {
                userName = ((Author) currentUser).getName();
            } else if (currentUser instanceof Customer) {
                userName = ((Customer) currentUser).getName();
            }

            getDataManager().getAuditTrail().logAction(currentUserType + ":" + userName, "Logged out at " + new java.util.Date());
            saveDataToDatabase();
        }

        this.currentUser = null;
        this.currentUserType = null;
        showWelcomeScreen();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    // Convenience method to get audit trail
    public MyClasses.Utilities.AuditTrail getAuditTrail() {
        return getDataManager().getAuditTrail();
    }

    // ============ UTILITY METHODS ============

    private void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    public void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void saveDataToDatabase() {
        if (getDataManager().saveAllData()) {
            showInformation("Success", "Data saved successfully!");
        } else {
            showError("Error", "Failed to save data!");
        }
    }

    public void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Do you want to save data before exiting?");
        alert.setContentText("Choose your option:");
        alert.initOwner(primaryStage);

        ButtonType btnSaveExit = new ButtonType("Save & Exit");
        ButtonType btnExitNoSave = new ButtonType("Exit Without Saving");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnSaveExit, btnExitNoSave, btnCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == btnSaveExit) {
                saveDataToDatabase();
                getDataManager().getAuditTrail().logAction("SYSTEM", "Application closed with data save at " + new java.util.Date());
                System.exit(0);
            } else if (result.get() == btnExitNoSave) {
                getDataManager().getAuditTrail().logAction("SYSTEM", "Application closed without saving at " + new java.util.Date());
                System.exit(0);
            }
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}