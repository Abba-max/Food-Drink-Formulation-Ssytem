package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.*;
import MyClasses.Conditions.*;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.NotificationSystem;

import java.util.LinkedList;

/**
 * Enhanced Author class with:
 * - Notification viewing
 * - Formulation modification
 * - Issue resolution
 */
public class Author extends Person implements Formulation {
    private int authorID;
    private LinkedList<Item> formulatedItems;
    private NotificationSystem notificationSystem;

    private Keypad pad = new Keypad();
    private Screen screen = new Screen();

    public Author() {
        super();
        this.formulatedItems = new LinkedList<>();
    }

    public Author(int authorID, String name, String address, String contact, String dob) {
        super(name, address, contact, dob);
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

    // ============ ORIGINAL FORMULATION INTERFACE METHODS ============
    // (Include all original methods from the previous Author class here)
    // For brevity, I'm showing the structure:

    @Override


























}