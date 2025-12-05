package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Formulation;
import MyClasses.Ingredient;
import MyClasses.Quantity;
import MyClasses.Conditions.*;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.Conditions.Prepprotocol;
import MyClasses.Feedback;

import java.util.LinkedList;

/**
 * Author class - responsible for creating, consulting, and checking formulations
 * Implements Formulation interface for food and drink creation
 */
public class Author extends Person implements Formulation {
    private int authorID;
    private LinkedList<Item> formulatedItems;

    private Keypad pad = new Keypad();
    private Screen screen = new Screen();

    public Author() {
        super();
        this.formulatedItems = new LinkedList<>();
    }

    public Author(int authorID) {
        super();
        this.authorID = authorID;
        this.formulatedItems = new LinkedList<>();
    }

    public Author(int authorID, String name, String address, String contact, String dob) {
        super(name, address, contact, dob);
        this.authorID = authorID;
    }
    // ============ FORMULATION METHODS ============

    /**
     * Main formulation method - allows author to create new food or drink
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
     * Creates optimal/lab conditions
     */
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

    // ============ GETTERS AND SETTERS ============

    public LinkedList<Item> getFormulatedItems() {
        return this.formulatedItems;
    }

    public int getAuthorID() {
        return this.authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
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

    @Override
    public String toString() {
        return "Author{" +
                "authorID=" + authorID +
                ", name='" + getName() + '\'' +
                ", formulationsCount=" + formulatedItems.size() +
                ", foodCount=" + getFoodCount() +
                ", drinkCount=" + getDrinkCount() +
                '}';
    }
}