package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.*;
import MyClasses.Conditions.*;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Ingredients.Quantity;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.Utilities.NotificationSystem;
import java.io.Serializable;

import java.util.LinkedList;

/**
 * Enhanced Author class with:
 * - Notification viewing
 * - Formulation modification
 * - Issue resolution
 */
public class Author extends Person implements Formulation, Serializable {
    private int authorID;
    private LinkedList<Item> formulatedItems;
    private NotificationSystem notificationSystem;
    private static final long serialVersionUID = 1L;

    private transient Keypad pad = new Keypad();
    private transient Screen screen = new Screen();

    public Author() {
        super();
        this.formulatedItems = new LinkedList<>();
    }

    public Author(int authorID, String name, String address, String contact, String dob) {
        super();
        this.authorID = authorID;
        this.formulatedItems = new LinkedList<>();
    }

    public void setNotificationSystem(NotificationSystem notificationSystem) {
        this.notificationSystem = notificationSystem;
    }

    // ============ NOTIFICATION MANAGEMENT ============

    /**
     * View notifications from Admin
     */
    public void viewNotifications() {
        if (notificationSystem == null) {
            screen.display("Notification system not available");
            return;
        }

        screen.display("\n=== MY NOTIFICATIONS ===");

        LinkedList<NotificationSystem.Notification> notifications =
                notificationSystem.getNotificationsForAuthor(authorID);

        if (notifications.isEmpty()) {
            screen.display("No notifications.");
            return;
        }

        int pendingCount = notificationSystem.getPendingCount(authorID);
        screen.display("Total: " + notifications.size() + " | Pending: " + pendingCount);

        for (NotificationSystem.Notification notif : notifications) {
            screen.display(notif.toString());
        }

        screen.display("\nActions:");
        screen.display("1. Fix a reported issue");
        screen.display("2. Mark notification as resolved");
        screen.display("0. Return");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                fixReportedIssue();
                break;
            case 2:
                markNotificationResolved();
                break;
        }
    }

    /**
     * Fix a reported issue by modifying the formulation
     */
    private void fixReportedIssue() {
        screen.display("\nEnter notification ID:");
        int notifId = pad.getInt();

        // Find the notification
        LinkedList<NotificationSystem.Notification> notifications =
                notificationSystem.getNotificationsForAuthor(authorID);

        NotificationSystem.Notification targetNotif = null;
        for (NotificationSystem.Notification notif : notifications) {
            if (notif.getNotificationId() == notifId) {
                targetNotif = notif;
                break;
            }
        }

        if (targetNotif == null) {
            screen.display("Notification not found!");
            return;
        }

        // Find the item
        Item item = null;
        for (Item i : formulatedItems) {
            if (i.getItemID() == targetNotif.getItemID()) {
                item = i;
                break;
            }
        }

        if (item == null) {
            screen.display("Item not found!");
            return;
        }

        // Modify the item
        screen.display("\n=== FIX ISSUE FOR: " + item.getName() + " ===");
        screen.display("Reported Issue: " + targetNotif.getIssueDescription());
        screen.display("\nWhat would you like to modify?");
        screen.display("1. Add/Remove/Modify Ingredients");
        screen.display("2. Update Price");
        screen.display("3. Modify Preparation Protocol");
        screen.display("4. Add Standards");
        screen.display("5. Modify Lab Conditions");
        screen.display("0. Cancel");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                modifyIngredients(item);
                break;
            case 2:
                modifyPrice(item);
                break;
            case 3:
                modifyPreparationProtocol(item);
                break;
            case 4:
                addStandardsToItem(item);
                break;
            case 5:
                modifyLabConditions(item);
                break;
            case 0:
                return;
        }

        screen.display("\n✓ Formulation updated!");
        screen.display("Mark this notification as resolved? (1=Yes, 0=No)");

        if (pad.getInt() == 1) {
            screen.display("Enter resolution notes:");
            String notes = pad.getString();
            notificationSystem.markAsResolved(notifId, notes);
        }
    }

    /**
     * Mark notification as resolved without modification
     */
    private void markNotificationResolved() {
        screen.display("\nEnter notification ID:");
        int notifId = pad.getInt();

        screen.display("Enter resolution notes:");
        String notes = pad.getString();

        notificationSystem.markAsResolved(notifId, notes);
    }

    // ============ MODIFICATION METHODS ============

    /**
     * Modify ingredients of a formulation
     */
    public void modifyIngredients(Item item) {
        screen.display("\n=== MODIFY INGREDIENTS ===");
        screen.display("Current ingredients:");

        LinkedList<Ingredient> ingredients = getIngredients(item);

        if (ingredients != null) {
            int i = 1;
            for (Ingredient ing : ingredients) {
                screen.display(i + ". " + ing.getName() + " (ID: " + ing.getIngredientID() + ")");
                i++;
            }
        }

        screen.display("\nSelect action:");
        screen.display("1. Add new ingredient");
        screen.display("2. Remove ingredient");
        screen.display("3. Modify ingredient quantity");
        screen.display("0. Cancel");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                addIngredientToItem(item);
                break;
            case 2:
                removeIngredientFromItem(item);
                break;
            case 3:
                modifyIngredientQuantity(item);
                break;
        }
    }

    private void addIngredientToItem(Item item) {
        screen.display("\n--- ADD INGREDIENT ---");

        screen.display("Enter ingredient ID:");
        int ingID = pad.getInt();

        screen.display("Enter ingredient name:");
        String ingName = pad.getString();

        screen.display("Enter weight (grams):");
        double weight = pad.getDouble();

        screen.display("Enter volume (ml):");
        double volume = pad.getDouble();

        screen.display("Enter fraction (0.0 to 1.0):");
        double fraction = pad.getDouble();

        screen.display("Enter unit:");
        String unit = pad.getString();

        Quantity quantity = new Quantity(weight, volume, fraction, unit);
        Ingredient ingredient = new Ingredient(ingID, ingName, quantity);

        if (item instanceof Food) {
            ((Food) item).addIngredient(ingredient);
        } else if (item instanceof Drink) {
            ((Drink) item).addIngredient(ingredient);
        }

        screen.display("✓ Ingredient added: " + ingName);
    }

    private void removeIngredientFromItem(Item item) {
        screen.display("\nEnter ingredient ID to remove:");
        int ingID = pad.getInt();

        boolean removed = false;
        if (item instanceof Food) {
            removed = ((Food) item).removeIngredient(ingID);
        } else if (item instanceof Drink) {
            removed = ((Drink) item).removeIngredient(ingID);
        }

        if (removed) {
            screen.display("✓ Ingredient removed");
        } else {
            screen.display("⚠ Ingredient not found");
        }
    }

    private void modifyIngredientQuantity(Item item) {
        screen.display("\nEnter ingredient ID to modify:");
        int ingID = pad.getInt();

        screen.display("Enter new weight (grams):");
        double weight = pad.getDouble();

        screen.display("Enter new volume (ml):");
        double volume = pad.getDouble();

        screen.display("Enter new fraction:");
        double fraction = pad.getDouble();

        screen.display("Enter unit:");
        String unit = pad.getString();

        Quantity newQuantity = new Quantity(weight, volume, fraction, unit);

        boolean modified = false;
        if (item instanceof Food) {
            modified = ((Food) item).modifyIngredientQuantity(ingID, newQuantity);
        } else if (item instanceof Drink) {
            modified = ((Drink) item).modifyIngredientQuantity(ingID, newQuantity);
        }

        if (modified) {
            screen.display("✓ Ingredient quantity updated");
        } else {
            screen.display("⚠ Ingredient not found");
        }
    }

    /**
     * Modify price
     */
    private void modifyPrice(Item item) {
        screen.display("\n=== MODIFY PRICE ===");
        screen.display("Current price: $" + item.getPrice());

        screen.display("Enter new price:");
        double newPrice = pad.getDouble();

        item.setPrice(newPrice);

        if (item instanceof Food) {
            ((Food) item).updateFoodInfo(item.getName(), newPrice);
        } else if (item instanceof Drink) {
            ((Drink) item).updateDrinkInfo(item.getName(), newPrice);
        }

        screen.display("✓ Price updated to: $" + newPrice);
    }

    /**
     * Modify preparation protocol
     */
    private void modifyPreparationProtocol(Item item) {
        screen.display("\n=== MODIFY PREPARATION PROTOCOL ===");

        Prepprotocol protocol = null;
        if (item instanceof Food) {
            protocol = ((Food) item).getPrepprotocol();
        } else if (item instanceof Drink) {
            protocol = ((Drink) item).getPrepprotocol();
        }

        if (protocol == null) {
            screen.display("No preparation protocol found. Create one? (1=Yes, 0=No)");
            if (pad.getInt() == 1) {
                protocol = createNewPrepProtocol();
                if (item instanceof Food) {
                    ((Food) item).setPrepprotocol(protocol);
                } else if (item instanceof Drink) {
                    ((Drink) item).setPrepprotocol(protocol);
                }
            }
            return;
        }

        // Display current protocol
        protocol.displayProtocol();

        screen.display("\nSelect action:");
        screen.display("1. Add new step");
        screen.display("2. Remove step");
        screen.display("3. Modify step");
        screen.display("0. Cancel");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                addStepToProtocol(protocol);
                break;
            case 2:
                removeStepFromProtocol(protocol);
                break;
            case 3:
                modifyStepInProtocol(protocol);
                break;
        }
    }

    private void addStepToProtocol(Prepprotocol protocol) {
        screen.display("\nEnter step description:");
        String stepDesc = pad.getString();

        screen.display("Does this step have specific conditions? (1=Yes, 0=No)");
        int hasConditions = pad.getInt();

        Optcondition stepCond = null;
        if (hasConditions == 1) {
            stepCond = createOptCondition();
        }

        protocol.addStep(stepDesc, stepCond);
        screen.display("✓ Step added");
    }

    private void removeStepFromProtocol(Prepprotocol protocol) {
        screen.display("\nEnter step number to remove:");
        int stepIndex = pad.getInt() - 1; // Convert to 0-based

        if (protocol.removeStep(stepIndex)) {
            screen.display("✓ Step removed");
        } else {
            screen.display("⚠ Invalid step number");
        }
    }

    private void modifyStepInProtocol(Prepprotocol protocol) {
        screen.display("\nEnter step number to modify:");
        int stepIndex = pad.getInt() - 1; // Convert to 0-based

        screen.display("Enter new step description:");
        String newDesc = pad.getString();

        if (protocol.updateStepDescription(stepIndex, newDesc)) {
            screen.display("✓ Step updated");
        } else {
            screen.display("⚠ Invalid step number");
        }
    }

    /**
     * Add standards
     */
    private void addStandardsToItem(Item item) {
        screen.display("\n=== ADD STANDARDS ===");
        screen.display("How many standards to add?");
        int numStandards = pad.getInt();

        for (int i = 0; i < numStandards; i++) {
            screen.display("Enter standard " + (i + 1) + ":");
            String standard = pad.getString();

            if (item instanceof Food) {
                ((Food) item).addStandard(standard);
            } else if (item instanceof Drink) {
                ((Drink) item).addStandard(standard);
            }
        }

        screen.display("✓ Standards added");
    }

    /**
     * Modify lab conditions
     */
    private void modifyLabConditions(Item item) {
        screen.display("\n=== MODIFY LAB CONDITIONS ===");

        Optcondition newConditions = createOptCondition();

        if (item instanceof Food) {
            ((Food) item).setLabCondition(newConditions);
        } else if (item instanceof Drink) {
            ((Drink) item).setLabCondition(newConditions);
        }

        screen.display("✓ Lab conditions updated");
    }
// Add this method to your Author class after the Formulate() method

    /**
     * Update existing formulation - comprehensive update menu
     */
    public void updateFormulation() {
        screen.display("\n=== UPDATE FORMULATION ===");

        if (formulatedItems.isEmpty()) {
            screen.display("No formulations available to update.");
            return;
        }

        // Display available formulations
        screen.display("\nYour formulations:");
        int count = 1;
        for (Item item : formulatedItems) {
            screen.display(count + ". " + item.getName() +
                    " (ID: " + item.getItemID() +
                    ", Type: " + (item instanceof Food ? "Food" : "Drink") + ")");
            count++;
        }

        screen.display("\nEnter formulation ID to update:");
        int id = pad.getInt();

        // Find the formulation
        Item targetItem = null;
        for (Item item : formulatedItems) {
            if (item.getItemID() == id) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            screen.display("⚠ Formulation not found with ID: " + id);
            return;
        }

        // Show update menu for the selected formulation
        showUpdateMenu(targetItem);
    }

    /**
     * Display comprehensive update menu for a formulation
     */
    private void showUpdateMenu(Item item) {
        boolean updating = true;

        while (updating) {
            screen.display("\n" + "=".repeat(60));
            screen.display("   UPDATING: " + item.getName());
            screen.display("   Type: " + (item instanceof Food ? "Food" : "Drink"));
            screen.display("=".repeat(60));
            screen.display("1. Update Basic Information (Name, Price)");
            screen.display("2. Manage Ingredients");
            screen.display("3. Update Lab Conditions");
            screen.display("4. Update Preparation Protocol");
            screen.display("5. Update Conservation Conditions");
            screen.display("6. Update Consumption Conditions");
            screen.display("7. Manage Standards");
            screen.display("8. Update Consumer Profile");
            screen.display("9. View Current Details");
            screen.display("0. Finish Updating");
            screen.display("\nEnter choice:");

            try {
                int choice = pad.getInt();

                switch (choice) {
                    case 1:
                        updateBasicInformation(item);
                        break;
                    case 2:
                        manageIngredients(item);
                        break;
                    case 3:
                        updateLabConditions(item);
                        break;
                    case 4:
                        updatePreparationProtocol(item);
                        break;
                    case 5:
                        updateConservationConditions(item);
                        break;
                    case 6:
                        updateConsumptionConditions(item);
                        break;
                    case 7:
                        manageStandards(item);
                        break;
                    case 8:
                        updateConsumerProfile(item);
                        break;
                    case 9:
                        viewCurrentDetails(item);
                        break;
                    case 0:
                        screen.display("\n✓ Formulation update completed!");
                        updating = false;
                        break;
                    default:
                        screen.display("⚠ Invalid choice! Please enter 0-9.");
                }
            } catch (NumberFormatException e) {
                screen.display("⚠ Invalid input! Please enter a valid number.");
            } catch (Exception e) {
                screen.display("⚠ Error: " + e.getMessage());
            }
        }
    }

    /**
     * Update basic information (name, price, ID)
     */
    private void updateBasicInformation(Item item) {
        screen.display("\n=== UPDATE BASIC INFORMATION ===");
        screen.display("Current Information:");
        screen.display("  Name: " + item.getName());
        screen.display("  Price: $" + item.getPrice());

        if (item instanceof Food) {
            screen.display("  Food ID: " + ((Food) item).getFoodID());
            screen.display("  Avg Price/Kg: $" + ((Food) item).getAveragePricePerKg());
        } else if (item instanceof Drink) {
            screen.display("  Drink ID: " + ((Drink) item).getDrinkID());
            screen.display("  Avg Price/Kg: $" + ((Drink) item).getAveragePricePerKg());
        }

        screen.display("\nWhat would you like to update?");
        screen.display("1. Name");
        screen.display("2. Price");
        screen.display("3. Average Price per Kg");
        screen.display("4. Expiry Date");
        screen.display("0. Cancel");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                screen.display("Enter new name:");
                String newName = pad.getString();
                if (newName != null && !newName.trim().isEmpty()) {
                    item.setName(newName);
                    screen.display("✓ Name updated to: " + newName);
                } else {
                    screen.display("⚠ Name cannot be empty!");
                }
                break;

            case 2:
                screen.display("Enter new price:");
                double newPrice = pad.getDouble();
                if (newPrice >= 0) {
                    item.setPrice(newPrice);
                    if (item instanceof Food) {
                        ((Food) item).updateFoodInfo(item.getName(), newPrice);
                    } else if (item instanceof Drink) {
                        ((Drink) item).updateDrinkInfo(item.getName(), newPrice);
                    }
                    screen.display("✓ Price updated to: $" + newPrice);
                } else {
                    screen.display("⚠ Price must be non-negative!");
                }
                break;

            case 3:
                screen.display("Enter new average price per kg:");
                double newAvgPrice = pad.getDouble();
                if (newAvgPrice >= 0) {
                    if (item instanceof Food) {
                        ((Food) item).setAveragePricePerKg(newAvgPrice);
                    } else if (item instanceof Drink) {
                        ((Drink) item).setAveragePricePerKg(newAvgPrice);
                    }
                    screen.display("✓ Average price per kg updated to: $" + newAvgPrice);
                } else {
                    screen.display("⚠ Price must be non-negative!");
                }
                break;

            case 4:
                screen.display("Enter new expiry date (YYYY-MM-DD):");
                String newExpiry = pad.getString();
                item.setExpiryDate(newExpiry);
                screen.display("✓ Expiry date updated to: " + newExpiry);
                break;

            case 0:
                return;

            default:
                screen.display("⚠ Invalid choice!");
        }
    }

    /**
     * Comprehensive ingredient management
     */
    private void manageIngredients(Item item) {
        boolean managing = true;

        while (managing) {
            screen.display("\n=== MANAGE INGREDIENTS ===");

            LinkedList<Ingredient> ingredients = getIngredients(item);

            if (ingredients != null && !ingredients.isEmpty()) {
                screen.display("\nCurrent ingredients:");
                int i = 1;
                for (Ingredient ing : ingredients) {
                    screen.display(i + ". " + ing.getName() + " (ID: " + ing.getIngredientID() + ")");
                    if (ing.getQuantity() != null) {
                        screen.display("   Weight: " + ing.getQuantity().getWeight() + "g, " +
                                "Volume: " + ing.getQuantity().getVolume() + "ml");
                    }
                    i++;
                }
            } else {
                screen.display("\nNo ingredients currently.");
            }

            screen.display("\nOptions:");
            screen.display("1. Add new ingredient");
            screen.display("2. Remove ingredient");
            screen.display("3. Modify ingredient quantity");
            screen.display("4. Replace ingredient");
            screen.display("0. Return");

            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    addIngredientToItem(item);
                    break;
                case 2:
                    removeIngredientFromItem(item);
                    break;
                case 3:
                    modifyIngredientQuantity(item);
                    break;
                case 4:
                    replaceIngredient(item);
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    screen.display("⚠ Invalid choice!");
            }
        }
    }

    /**
     * Replace an existing ingredient with a new one
     */
    private void replaceIngredient(Item item) {
        screen.display("\n--- REPLACE INGREDIENT ---");
        screen.display("Enter ingredient ID to replace:");
        int oldIngID = pad.getInt();

        // Check if ingredient exists
        LinkedList<Ingredient> ingredients = getIngredients(item);
        boolean found = false;

        if (ingredients != null) {
            for (Ingredient ing : ingredients) {
                if (ing.getIngredientID() == oldIngID) {
                    found = true;
                    screen.display("Found: " + ing.getName());
                    break;
                }
            }
        }

        if (!found) {
            screen.display("⚠ Ingredient not found!");
            return;
        }

        // Remove old ingredient
        if (item instanceof Food) {
            ((Food) item).removeIngredient(oldIngID);
        } else if (item instanceof Drink) {
            ((Drink) item).removeIngredient(oldIngID);
        }

        // Add new ingredient
        screen.display("\nEnter new ingredient details:");
        screen.display("Enter ingredient ID:");
        int newIngID = pad.getInt();

        screen.display("Enter ingredient name:");
        String ingName = pad.getString();

        screen.display("Enter weight (grams):");
        double weight = pad.getDouble();

        screen.display("Enter volume (ml):");
        double volume = pad.getDouble();

        screen.display("Enter fraction (0.0 to 1.0):");
        double fraction = pad.getDouble();

        screen.display("Enter unit:");
        String unit = pad.getString();

        Quantity quantity = new Quantity(weight, volume, fraction, unit);
        Ingredient newIngredient = new Ingredient(newIngID, ingName, quantity);

        if (item instanceof Food) {
            ((Food) item).addIngredient(newIngredient);
        } else if (item instanceof Drink) {
            ((Drink) item).addIngredient(newIngredient);
        }

        screen.display("✓ Ingredient replaced successfully: " + ingName);
    }

    /**
     * Update lab conditions
     */
    private void updateLabConditions(Item item) {
        screen.display("\n=== UPDATE LAB CONDITIONS ===");

        Optcondition currentCondition = null;
        if (item instanceof Food) {
            currentCondition = ((Food) item).getLabCondition();
        } else if (item instanceof Drink) {
            currentCondition = ((Drink) item).getLabCondition();
        }

        if (currentCondition != null) {
            screen.display("\nCurrent lab conditions:");
            screen.display("  Temperature: " + currentCondition.getTemp() + "°C");
            screen.display("  Pressure: " + currentCondition.getPressure() + " kPa");
            screen.display("  Moisture: " + currentCondition.getMoisture() + "%");
            screen.display("  Vibration: " + currentCondition.getVibration());
            screen.display("  Period: " + currentCondition.getPeriod() + " min");
        } else {
            screen.display("No lab conditions currently set.");
        }

        screen.display("\nEnter new lab conditions:");
        Optcondition newConditions = createOptCondition();

        if (item instanceof Food) {
            ((Food) item).setLabCondition(newConditions);
        } else if (item instanceof Drink) {
            ((Drink) item).setLabCondition(newConditions);
        }

        screen.display("✓ Lab conditions updated successfully!");
    }

    /**
     * Update preparation protocol
     */
    private void updatePreparationProtocol(Item item) {
        screen.display("\n=== UPDATE PREPARATION PROTOCOL ===");

        Prepprotocol protocol = null;
        if (item instanceof Food) {
            protocol = ((Food) item).getPrepprotocol();
        } else if (item instanceof Drink) {
            protocol = ((Drink) item).getPrepprotocol();
        }

        if (protocol == null || protocol.isEmpty()) {
            screen.display("No preparation protocol found.");
            screen.display("Create one? (1=Yes, 0=No)");
            if (pad.getInt() == 1) {
                protocol = createNewPrepProtocol();
                if (item instanceof Food) {
                    ((Food) item).setPrepprotocol(protocol);
                } else if (item instanceof Drink) {
                    ((Drink) item).setPrepprotocol(protocol);
                }
                screen.display("✓ Preparation protocol created!");
            }
            return;
        }

        boolean managing = true;

        while (managing) {
            // Display current protocol
            screen.display("\n--- CURRENT PROTOCOL ---");
            protocol.displayProtocol();

            screen.display("\nOptions:");
            screen.display("1. Add new step");
            screen.display("2. Remove step");
            screen.display("3. Modify step description");
            screen.display("4. Modify step conditions");
            screen.display("5. Clear all steps and start over");
            screen.display("0. Return");

            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    addStepToProtocol(protocol);
                    break;
                case 2:
                    removeStepFromProtocol(protocol);
                    break;
                case 3:
                    modifyStepInProtocol(protocol);
                    break;
                case 4:
                    modifyStepConditions(protocol);
                    break;
                case 5:
                    screen.display("Are you sure you want to clear all steps? (1=Yes, 0=No)");
                    if (pad.getInt() == 1) {
                        protocol.clear();
                        screen.display("✓ All steps cleared.");
                    }
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    screen.display("⚠ Invalid choice!");
            }
        }
    }

    /**
     * Modify conditions for a specific step
     */
    private void modifyStepConditions(Prepprotocol protocol) {
        screen.display("\nEnter step number to modify conditions:");
        int stepIndex = pad.getInt() - 1; // Convert to 0-based

        if (stepIndex < 0 || stepIndex >= protocol.getStepCount()) {
            screen.display("⚠ Invalid step number!");
            return;
        }

        screen.display("Enter new conditions for this step:");
        Optcondition newConditions = createOptCondition();

        if (protocol.updateStepCondition(stepIndex, newConditions)) {
            screen.display("✓ Step conditions updated!");
        } else {
            screen.display("⚠ Failed to update conditions!");
        }
    }

    /**
     * Update conservation conditions
     */
    private void updateConservationConditions(Item item) {
        screen.display("\n=== UPDATE CONSERVATION CONDITIONS ===");

        Conservecondition currentCondition = null;
        if (item instanceof Food) {
            currentCondition = ((Food) item).getConservecondition();
        } else if (item instanceof Drink) {
            currentCondition = ((Drink) item).getConservecondition();
        }

        if (currentCondition != null) {
            screen.display("\nCurrent conservation conditions:");
            screen.display("  Temperature: " + currentCondition.getTemp() + "°C");
            screen.display("  Moisture: " + currentCondition.getMoisture() + "%");
            screen.display("  Container: " + currentCondition.getContainer());
        } else {
            screen.display("No conservation conditions currently set.");
        }

        screen.display("\nEnter new conservation conditions:");
        Conservecondition newConditions = createConserveCondition();

        if (item instanceof Food) {
            ((Food) item).setConservecondition(newConditions);
        } else if (item instanceof Drink) {
            ((Drink) item).setConservecondition(newConditions);
        }

        screen.display("✓ Conservation conditions updated successfully!");
    }

    /**
     * Update consumption conditions
     */
    private void updateConsumptionConditions(Item item) {
        screen.display("\n=== UPDATE CONSUMPTION CONDITIONS ===");

        Consumpcondition currentCondition = null;
        if (item instanceof Food) {
            currentCondition = ((Food) item).getConsumpcondition();
        } else if (item instanceof Drink) {
            currentCondition = ((Drink) item).getConsumpcondition();
        }

        if (currentCondition != null) {
            screen.display("\nCurrent consumption conditions:");
            screen.display("  Temperature: " + currentCondition.getTemperature() + "°C");
            screen.display("  Moisture: " + currentCondition.getMoisture() + "%");
        } else {
            screen.display("No consumption conditions currently set.");
        }

        screen.display("\nEnter new consumption conditions:");
        Consumpcondition newConditions = createConsumpCondition();

        if (item instanceof Food) {
            ((Food) item).setConsumpcondition(newConditions);
        } else if (item instanceof Drink) {
            ((Drink) item).setConsumpcondition(newConditions);
        }

        screen.display("✓ Consumption conditions updated successfully!");
    }

    /**
     * Manage standards (add/remove)
     */
    private void manageStandards(Item item) {
        boolean managing = true;

        while (managing) {
            screen.display("\n=== MANAGE STANDARDS ===");

            LinkedList<String> standards = null;
            if (item instanceof Food) {
                standards = ((Food) item).getStandards();
            } else if (item instanceof Drink) {
                standards = ((Drink) item).getStandards();
            }

            if (standards != null && !standards.isEmpty()) {
                screen.display("\nCurrent standards:");
                int i = 1;
                for (String standard : standards) {
                    screen.display(i + ". " + standard);
                    i++;
                }
            } else {
                screen.display("\nNo standards currently defined.");
            }

            screen.display("\nOptions:");
            screen.display("1. Add standard");
            screen.display("2. Remove standard");
            screen.display("3. Clear all standards");
            screen.display("0. Return");

            int choice = pad.getInt();

            switch (choice) {
                case 1:
                    screen.display("Enter standard to add:");
                    String newStandard = pad.getString();
                    if (item instanceof Food) {
                        ((Food) item).addStandard(newStandard);
                    } else if (item instanceof Drink) {
                        ((Drink) item).addStandard(newStandard);
                    }
                    screen.display("✓ Standard added!");
                    break;

                case 2:
                    if (standards == null || standards.isEmpty()) {
                        screen.display("⚠ No standards to remove!");
                        break;
                    }
                    screen.display("Enter number of standard to remove:");
                    int index = pad.getInt() - 1;
                    if (index >= 0 && index < standards.size()) {
                        String removed = standards.remove(index);
                        screen.display("✓ Removed: " + removed);
                    } else {
                        screen.display("⚠ Invalid number!");
                    }
                    break;

                case 3:
                    if (standards != null) {
                        screen.display("Are you sure? (1=Yes, 0=No)");
                        if (pad.getInt() == 1) {
                            standards.clear();
                            screen.display("✓ All standards cleared!");
                        }
                    }
                    break;

                case 0:
                    managing = false;
                    break;

                default:
                    screen.display("⚠ Invalid choice!");
            }
        }
    }

    /**
     * Update consumer profile
     */
    private void updateConsumerProfile(Item item) {
        screen.display("\n=== UPDATE CONSUMER PROFILE ===");

        ConsumerSpecificInfo currentProfile = null;
        if (item instanceof Food) {
            currentProfile = ((Food) item).getConsumerProfile();
        } else if (item instanceof Drink) {
            currentProfile = ((Drink) item).getConsumerProfile();
        }

        if (currentProfile != null && currentProfile.getProfile() != null) {
            screen.display("\nCurrent profile: " + currentProfile.getProfile());
        } else {
            screen.display("No consumer profile currently set.");
        }

        screen.display("\nEnter new consumer profile:");
        screen.display("(e.g., 'Adults 18-65, Health-conscious, Active lifestyle')");
        String newProfile = pad.getString();

        ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
        consumerProfile.setProfile(newProfile);

        if (item instanceof Food) {
            ((Food) item).setConsumerProfile(consumerProfile);
        } else if (item instanceof Drink) {
            ((Drink) item).setConsumerProfile(consumerProfile);
        }

        screen.display("✓ Consumer profile updated successfully!");
    }

    /**
     * View current complete details of the formulation
     */
    private void viewCurrentDetails(Item item) {
        screen.display("\n" + "=".repeat(70));
        screen.display("   COMPLETE FORMULATION DETAILS");
        screen.display("=".repeat(70));

        // Basic info
        screen.display("\n--- BASIC INFORMATION ---");
        screen.display("Name: " + item.getName());
        screen.display("Type: " + (item instanceof Food ? "Food" : "Drink"));
        screen.display("ID: " + item.getItemID());
        screen.display("Price: $" + item.getPrice());

        if (item instanceof Food) {
            Food food = (Food) item;
            screen.display("Avg Price/Kg: $" + food.getAveragePricePerKg());

            // Ingredients
            if (food.getIngredients() != null && !food.getIngredients().isEmpty()) {
                screen.display("\n--- INGREDIENTS ---");
                for (Ingredient ing : food.getIngredients()) {
                    screen.display("• " + ing.getName());
                    if (ing.getQuantity() != null) {
                        screen.display("  Weight: " + ing.getQuantity().getWeight() + "g, " +
                                "Volume: " + ing.getQuantity().getVolume() + "ml");
                    }
                }
            }

            // Lab conditions
            if (food.getLabCondition() != null) {
                screen.display("\n--- LAB CONDITIONS ---");
                screen.display(food.getLabCondition().toString());
            }

            // Preparation
            if (food.getPrepprotocol() != null) {
                food.getPrepprotocol().displayProtocol();
            }

            // Conservation
            if (food.getConservecondition() != null) {
                screen.display("\n--- CONSERVATION CONDITIONS ---");
                screen.display(food.getConservecondition().toString());
            }

            // Consumption
            if (food.getConsumpcondition() != null) {
                screen.display("\n--- CONSUMPTION CONDITIONS ---");
                screen.display(food.getConsumpcondition().toString());
            }

            // Standards
            if (food.getStandards() != null && !food.getStandards().isEmpty()) {
                screen.display("\n--- STANDARDS ---");
                for (String standard : food.getStandards()) {
                    screen.display("✓ " + standard);
                }
            }

            // Consumer profile
            if (food.getConsumerProfile() != null) {
                screen.display("\n--- CONSUMER PROFILE ---");
                screen.display(food.getConsumerProfile().getProfile());
            }

        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            screen.display("Avg Price/Kg: $" + drink.getAveragePricePerKg());

            // Similar display for Drink...
            if (drink.getIngredients() != null && !drink.getIngredients().isEmpty()) {
                screen.display("\n--- INGREDIENTS ---");
                for (Ingredient ing : drink.getIngredients()) {
                    screen.display("• " + ing.getName());
                    if (ing.getQuantity() != null) {
                        screen.display("  Weight: " + ing.getQuantity().getWeight() + "g, " +
                                "Volume: " + ing.getQuantity().getVolume() + "ml");
                    }
                }
            }

            if (drink.getLabCondition() != null) {
                screen.display("\n--- LAB CONDITIONS ---");
                screen.display(drink.getLabCondition().toString());
            }

            if (drink.getPrepprotocol() != null) {
                drink.getPrepprotocol().displayProtocol();
            }

            if (drink.getStandards() != null && !drink.getStandards().isEmpty()) {
                screen.display("\n--- STANDARDS ---");
                for (String standard : drink.getStandards()) {
                    screen.display("✓ " + standard);
                }
            }
        }

        screen.display("\n" + "=".repeat(70));
        screen.display("Press Enter to continue...");
        pad.getString();
    }
    // ============ HELPER METHODS ============

    private LinkedList<Ingredient> getIngredients(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            return ((Drink) item).getIngredients();
        }
        return null;
    }

    private Optcondition createOptCondition() {
        Optcondition cond = new Optcondition();

        screen.display("Enter temperature (°C):");
        cond.setTemp(pad.getDouble());

        screen.display("Enter pressure (kPa):");
        cond.setPressure(pad.getDouble());

        screen.display("Enter moisture (%):");
        cond.setMoisture(pad.getDouble());

        screen.display("Enter vibration level:");
        cond.setVibration(pad.getDouble());

        screen.display("Enter time period (minutes):");
        cond.setPeriod(pad.getInt());

        return cond;
    }

    private Prepprotocol createNewPrepProtocol() {
        Prepprotocol protocol = new Prepprotocol();

        screen.display("How many preparation steps?");
        int numSteps = pad.getInt();

        for (int i = 0; i < numSteps; i++) {
            screen.display("\nStep " + (i + 1) + ":");
            screen.display("Enter step description:");
            String stepDesc = pad.getString();

            screen.display("Does this step have specific conditions? (1=Yes, 0=No)");
            int hasConditions = pad.getInt();

            Optcondition stepCond = null;
            if (hasConditions == 1) {
                stepCond = createOptCondition();
            }

            protocol.addStep(stepDesc, stepCond);
        }

        return protocol;
    }

    // ============ GETTERS ============

    public int getAuthorID() {
        return authorID;
    }


    public LinkedList<Item> getFormulatedItems() {
        return formulatedItems;
    }

    /**
     * Main formulation method - allows author to create new food or drink
     *
     * @return The newly created Item (Food or Drink)
     */
    @Override
    public Item Formulate() {
        screen.display("=== NEW FORMULATION ===");
        screen.display("Select item type:");
        screen.display("1. Food");
        screen.display("2. Drink");

        int choice = pad.getInt();

        Item item = null;

        switch (choice) {
            case 1:
                item = formulateFood();
                break;
            case 2:
                item = formulateDrink();
                break;
            default:
                screen.display("Invalid choice! Defaulting to Food.");
                item = formulateFood();
        }

        if (item != null) {
            formulatedItems.add(item);
            screen.display("✓ Formulation created successfully: " + item.getName());
            screen.display("Total formulations by this author: " + formulatedItems.size());
        }

        return item;
    }

    /**
     * Creates a new food formulation
     *
     * @return Food object with all specifications
     */
    private Food formulateFood() {
        screen.display("\n--- FOOD FORMULATION ---");

        Food food = new Food();

        // Basic Information
        screen.display("Enter food name:");
        food.setName(pad.getString());

        screen.display("Enter food ID:");
        food.setFoodID(pad.getInt());
        food.setItemID(food.getFoodID());

        screen.display("Enter price:");
        food.setPrice(pad.getDouble());

//        screen.display("Enter entry date (YYYY-MM-DD):");
//        food.setEntryDate(pad.getString());

        screen.display("Enter expiry date (YYYY-MM-DD):");
        food.setExpiryDate(pad.getString());

        screen.display("Enter average price per kg:");
        food.setAveragePricePerKg(pad.getDouble());

        // Add this author
        food.addAuthor(this);

        // Add Ingredients
        addIngredientsToItem(food);

        // Lab Conditions
        screen.display("\n--- LAB CONDITIONS ---");
        food.setLabCondition(createOptCondition());

        // Preparation Protocol
        screen.display("\n--- PREPARATION PROTOCOL ---");
        food.setPrepprotocol(createPrepProtocol());

        // Conservation Conditions
        screen.display("\n--- CONSERVATION CONDITIONS ---");
        food.setConservecondition(createConserveCondition());

        // Consumption Conditions
        screen.display("\n--- CONSUMPTION CONDITIONS ---");
        food.setConsumpcondition(createConsumpCondition());

        // Standards
        addStandards(food);

        // Consumer Profile
        screen.display("\n--- CONSUMER PROFILE ---");
        screen.display("Enter target consumer profile (e.g., 'Adults 18-65, Health-conscious'):");
        String profile = pad.getString();
        ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
        consumerProfile.setProfile(profile);
        food.setConsumerProfile(consumerProfile);

        screen.display("Food formulation completed!");
        return food;
    }

    /**
     * Creates a new drink formulation
     *
     * @return Drink object with all specifications
     */
    private Drink formulateDrink() {
        screen.display("\n--- DRINK FORMULATION ---");

        Drink drink = new Drink();

        // Basic Information
        screen.display("Enter drink name:");
        drink.setName(pad.getString());

        screen.display("Enter drink ID:");
        drink.setDrinkID(pad.getInt());
        drink.setItemID(drink.getDrinkID());

        screen.display("Enter price:");
        drink.setPrice(pad.getDouble());

//        screen.display("Enter entry date (YYYY-MM-DD):");
//        drink.setEntryDate(pad.getString());

        screen.display("Enter expiry date (YYYY-MM-DD):");
        drink.setExpiryDate(pad.getString());

        screen.display("Enter average price per kg:");
        drink.setAveragePricePerKg(pad.getDouble());

        // Add this author
        drink.addAuthor(this);

        // Add Ingredients
        addIngredientsToItem(drink);

        // Lab Conditions
        screen.display("\n--- LAB CONDITIONS ---");
        drink.setLabCondition(createOptCondition());

        // Preparation Protocol
        screen.display("\n--- PREPARATION PROTOCOL ---");
        drink.setPrepprotocol(createPrepProtocol());

        // Conservation Conditions
        screen.display("\n--- CONSERVATION CONDITIONS ---");
        drink.setConservecondition(createConserveCondition());

        // Consumption Conditions
        screen.display("\n--- CONSUMPTION CONDITIONS ---");
        drink.setConsumpcondition(createConsumpCondition());

        // Standards
        addStandards(drink);

        // Consumer Profile
        screen.display("\n--- CONSUMER PROFILE ---");
        screen.display("Enter target consumer profile:");
        String profile = pad.getString();
        ConsumerSpecificInfo consumerProfile = new ConsumerSpecificInfo();
        consumerProfile.setProfile(profile);
        drink.setConsumerProfile(consumerProfile);

        screen.display("Drink formulation completed!");
        return drink;
    }

    /**
     * Adds ingredients to the item (Food or Drink)
     */
    private void addIngredientsToItem(Item item) {
        screen.display("\n--- ADD INGREDIENTS ---");
        screen.display("How many ingredients?");
        int numIngredients = pad.getInt();

        for (int i = 0; i < numIngredients; i++) {
            screen.display("\nIngredient " + (i + 1) + ":");

            screen.display("Enter ingredient ID:");
            int ingID = pad.getInt();

            screen.display("Enter ingredient name:");
            String ingName = pad.getString();

            screen.display("Enter weight (grams):");
            double weight = pad.getDouble();

            screen.display("Enter volume (ml):");
            double volume = pad.getDouble();

            screen.display("Enter fraction (0.0 to 1.0):");
            double fraction = pad.getDouble();

            screen.display("Enter unit (e.g., grams, ml, pieces):");
            String unit = pad.getString();

            Quantity quantity = new Quantity(weight, volume, fraction, unit);
            Ingredient ingredient = new Ingredient(ingID, ingName, quantity);

            if (item instanceof Food) {
                ((Food) item).addIngredient(ingredient);
            } else if (item instanceof Drink) {
                ((Drink) item).addIngredient(ingredient);
            }

            screen.display("✓ Ingredient added: " + ingName);
        }
    }


    /**
     * Creates conservation conditions
     */
    private Conservecondition createConserveCondition() {
        Conservecondition cond = new Conservecondition();

        screen.display("Enter storage temperature (°C):");
        cond.setTemp(pad.getDouble());

        screen.display("Enter storage moisture (%):");
        cond.setMoisture(pad.getDouble());

        screen.display("Enter container type:");
        cond.setContainer(pad.getString());

        return cond;
    }

    /**
     * Creates consumption conditions
     */
    private Consumpcondition createConsumpCondition() {
        Consumpcondition cond = new Consumpcondition();

        screen.display("Enter serving temperature (°C):");
        cond.setTemperature(pad.getDouble());

        screen.display("Enter serving moisture (%):");
        cond.setMoisture(pad.getDouble());

        return cond;
    }

    /**
     * Creates preparation protocol with steps
     */


    /**
     * Adds standards to the formulation
     */
    private void addStandards(Item item) {
        screen.display("\n--- STANDARDS ---");
        screen.display("How many standards does this formulation respect?");
        int numStandards = pad.getInt();

        for (int i = 0; i < numStandards; i++) {
            screen.display("Enter standard " + (i + 1) + ":");
            String standard = pad.getString();

            if (item instanceof Food) {
                ((Food) item).addStandard(standard);
            } else if (item instanceof Drink) {
                ((Drink) item).addStandard(standard);
            }
        }
    }

    /**
     * Consult existing formulations
     * Allows author to view and search their formulations
     */
    @Override
    public void consultFormulation() {
        screen.display("\n=== CONSULT FORMULATIONS ===");

        if (formulatedItems.isEmpty()) {
            screen.display("No formulations found for this author.");
            return;
        }

        screen.display("Total formulations: " + formulatedItems.size());
        screen.display("\nSelect consultation type:");
        screen.display("1. View all formulations");
        screen.display("2. Search by name");
        screen.display("3. Search by ingredient");
        screen.display("4. View specific formulation details");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                viewAllFormulations();
                break;
            case 2:
                searchByName();
                break;
            case 3:
                searchByIngredient();
                break;
            case 4:
                viewFormulationDetails();
                break;
            default:
                screen.display("Invalid choice!");
        }
    }

    /**
     * Views all formulations by this author
     */
    private void viewAllFormulations() {
        screen.display("\n--- ALL FORMULATIONS ---");
        int count = 1;

        for (Item item : formulatedItems) {
            screen.display("\n" + count + ". " + item.getName());
            screen.display("   Type: " + (item instanceof Food ? "Food" : "Drink"));
            screen.display("   ID: " + item.getItemID());
            screen.display("   Price: $" + item.getPrice());

            // Show ingredient count
            int ingredientCount = 0;
            if (item instanceof Food) {
                ingredientCount = ((Food) item).getIngredients() != null ?
                        ((Food) item).getIngredients().size() : 0;
            } else if (item instanceof Drink) {
                ingredientCount = ((Drink) item).getIngredients() != null ?
                        ((Drink) item).getIngredients().size() : 0;
            }
            screen.display("   Ingredients: " + ingredientCount);

            count++;
        }
    }

    /**
     * Searches formulations by name
     */
    private void searchByName() {
        screen.display("\nEnter search term:");
        String searchTerm = pad.getString().toLowerCase();

        screen.display("\n--- SEARCH RESULTS ---");
        boolean found = false;

        for (Item item : formulatedItems) {
            if (item.getName() != null &&
                    item.getName().toLowerCase().contains(searchTerm)) {
                screen.display("\n✓ Found: " + item.getName());
                screen.display("  Type: " + (item instanceof Food ? "Food" : "Drink"));
                screen.display("  ID: " + item.getItemID());
                found = true;
            }
        }

        if (!found) {
            screen.display("No formulations found matching '" + searchTerm + "'");
        }
    }

    /**
     * Searches formulations by ingredient
     */
    private void searchByIngredient() {
        screen.display("\nEnter ingredient name:");
        String ingredientName = pad.getString().toLowerCase();

        screen.display("\n--- FORMULATIONS CONTAINING: " + ingredientName + " ---");
        boolean found = false;

        for (Item item : formulatedItems) {
            LinkedList<Ingredient> ingredients = null;

            if (item instanceof Food) {
                ingredients = ((Food) item).getIngredients();
            } else if (item instanceof Drink) {
                ingredients = ((Drink) item).getIngredients();
            }

            if (ingredients != null) {
                for (Ingredient ing : ingredients) {
                    if (ing.getName() != null &&
                            ing.getName().toLowerCase().contains(ingredientName)) {
                        screen.display("\n✓ " + item.getName());
                        screen.display("  Contains: " + ing.getName());
                        found = true;
                        break;
                    }
                }
            }
        }

        if (!found) {
            screen.display("No formulations found with ingredient '" + ingredientName + "'");
        }
    }

    /**
     * Gets count of food formulations
     */
    public int getFoodCount() {
        int count = 0;
        for (Item item : formulatedItems) {
            if (item instanceof Food) count++;
        }
        return count;
    }

    /**
     * Gets count of drink formulations
     */
    public int getDrinkCount() {
        int count = 0;
        for (Item item : formulatedItems) {
            if (item instanceof Drink) count++;
        }
        return count;
    }

    /**
     * Views detailed information about a specific formulation
     */
    private void viewFormulationDetails() {
        screen.display("\nEnter formulation ID:");
        int id = pad.getInt();

        Item found = null;
        for (Item item : formulatedItems) {
            if (item.getItemID() == id) {
                found = item;
                break;
            }
        }

        if (found == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        // Display detailed information
        screen.display("\n=== FORMULATION DETAILS ===");
        screen.display("Name: " + found.getName());
        screen.display("Type: " + (found instanceof Food ? "Food" : "Drink"));
        screen.display("ID: " + found.getItemID());
        screen.display("Price: $" + found.getPrice());

        // Display ingredients
        LinkedList<Ingredient> ingredients = null;
        if (found instanceof Food) {
            ingredients = ((Food) found).getIngredients();
            screen.display("Avg Price/Kg: $" + ((Food) found).getAveragePricePerKg());
        } else if (found instanceof Drink) {
            ingredients = ((Drink) found).getIngredients();
            screen.display("Avg Price/Kg: $" + ((Drink) found).getAveragePricePerKg());
        }

        if (ingredients != null && !ingredients.isEmpty()) {
            screen.display("\nIngredients:");
            for (Ingredient ing : ingredients) {
                screen.display("  - " + ing.getName());
                if (ing.getQuantity() != null) {
                    screen.display("    Weight: " + ing.getQuantity().getWeight() + "g");
                    screen.display("    Volume: " + ing.getQuantity().getVolume() + "ml");
                }
            }
        }

        // Display feedbacks
        LinkedList<Feedback> feedbacks = null;
        if (found instanceof Food) {
            feedbacks = ((Food) found).getFeedbacks();
        } else if (found instanceof Drink) {
            feedbacks = ((Drink) found).getFeedbacks();
        }

        if (feedbacks != null && !feedbacks.isEmpty()) {
            screen.display("\nFeedbacks: " + feedbacks.size() + " received");
        }
    }

    /**
     * Checks formulation for potential issues
     * Reviews feedback, standards compliance, and other concerns
     */
    @Override
    public void checkFormulationissues() {
        screen.display("\n=== CHECK FORMULATION ISSUES ===");

        if (formulatedItems.isEmpty()) {
            screen.display("No formulations to check.");
            return;
        }

        screen.display("Enter formulation ID to check:");
        int id = pad.getInt();

        Item item = null;
        for (Item i : formulatedItems) {
            if (i.getItemID() == id) {
                item = i;
                break;
            }
        }

        if (item == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        screen.display("\n--- ISSUE REPORT FOR: " + item.getName() + " ---");

        int issueCount = 0;

        // Check 1: Missing ingredients
        LinkedList<Ingredient> ingredients = null;
        if (item instanceof Food) {
            ingredients = ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            ingredients = ((Drink) item).getIngredients();
        }

        if (ingredients == null || ingredients.isEmpty()) {
            screen.display("⚠ WARNING: No ingredients specified!");
            issueCount++;
        }

        // Check 3: Check for negative feedbacks
        LinkedList<Feedback> feedbacks = null;
        int negativeFeedbackCount = 0;

        if (item instanceof Food) {
            feedbacks = ((Food) item).getFeedbacks();
        } else if (item instanceof Drink) {
            feedbacks = ((Drink) item).getFeedbacks();
        }

        if (feedbacks != null) {
            for (Feedback fb : feedbacks) {
                if (!fb.isLike()) {
                    negativeFeedbackCount++;
                }
            }

            if (negativeFeedbackCount > 0) {
                screen.display("⚠ WARNING: " + negativeFeedbackCount + " negative feedback(s)");
                issueCount++;

                // Show negative feedback details
                for (Feedback fb : feedbacks) {
                    if (!fb.isLike()) {
                        screen.display("  - " + fb.getConsumerName() + ": " + fb.getComment());
                    }
                }
            }
        }

        // Check 4: Veto status
        boolean isVetoed = false;
        if (item instanceof Food) {
            isVetoed = ((Food) item).isVetoed();
        } else if (item instanceof Drink) {
            isVetoed = ((Drink) item).isVetoed();
        }

        if (isVetoed) {
            screen.display("⚠ CRITICAL: Formulation is VETOED!");
            issueCount++;
        }

        // Check 5: Missing standards
        LinkedList<String> standards = null;
        if (item instanceof Food) {
            standards = ((Food) item).getStandards();
        } else if (item instanceof Drink) {
            standards = ((Drink) item).getStandards();
        }

        if (standards == null || standards.isEmpty()) {
            screen.display("⚠ INFO: No standards specified");
        }

        // Check 6: Price validation
        if (item.getPrice() <= 0) {
            screen.display("⚠ WARNING: Invalid price!");
            issueCount++;
        }

        // Summary
        screen.display("\n--- ISSUE SUMMARY ---");
        if (issueCount == 0) {
            screen.display("✓ No critical issues found!");
        } else {
            screen.display("Total issues found: " + issueCount);
            screen.display("Recommendation: Review and address the warnings above.");
        }
    }

    /**
     * Creates preparation protocol with steps
     */
    private Prepprotocol createPrepProtocol() {
        Prepprotocol protocol = new Prepprotocol();

        screen.display("How many preparation steps?");
        int numSteps = pad.getInt();

        for (int i = 0; i < numSteps; i++) {
            screen.display("\nStep " + (i + 1) + ":");
            screen.display("Enter step description:");
            String stepDesc = pad.getString();

            screen.display("Does this step have specific conditions? (1=Yes, 0=No)");
            int hasConditions = pad.getInt();

            Optcondition stepCond = null;
            if (hasConditions == 1) {
                screen.display("Enter conditions for this step:");
                stepCond = createOptCondition();
            } else {
                stepCond = new Optcondition();
            }

            protocol.addStep(stepDesc, stepCond);
            screen.display("✓ Step added");
        }

        return protocol;
    }
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.pad = new Keypad();
        this.screen = new Screen();
    }
}