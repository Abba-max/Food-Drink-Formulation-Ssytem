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