package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Feedback;
import MyClasses.Ingredient;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.Formulation;

import java.util.LinkedList;

/**
 * Customer class - represents consumers who can:
 * - Consult all available formulations
 * - Provide feedback on formulations
 * - Check for allergens
 * - Manage favorite formulations
 */
public class Customer extends Person implements Formulation {
    private int customerID;
    private int age;
    private boolean payed;
    private ConsumerSpecificInfo info;
    private LinkedList<Item> favoriteFormulations;
    private LinkedList<Feedback> feedbackHistory;

    // For interaction
    private Keypad pad = new Keypad();
    private Screen screen = new Screen();

    // Reference to all available formulations (set by system)
    private LinkedList<Item> availableFormulations;

    public Customer() {
        super();
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
        this.availableFormulations = new LinkedList<>();
    }

    public Customer(int customerID, int age) {
        super();
        this.customerID = customerID;
        this.age = age;
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
        this.availableFormulations = new LinkedList<>();
    }

    public Customer(int customerID, String name, String address, String contact, String dob, int age) {
        super(name, address, contact, dob);
        this.customerID = customerID;
        this.age = age;
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
        this.availableFormulations = new LinkedList<>();
    }

    // ============ FORMULATION INTERFACE METHODS ============

    /**
     * Customers suggest formulations (not create full ones like authors)
     */
    @Override
    public Item Formulate() {
        screen.display("\n=== CUSTOMER FORMULATION SUGGESTION ===");
        screen.display("As a customer, you can suggest new formulations to authors.");
        screen.display("This is a simplified suggestion form.");

        screen.display("\nWhat type would you like to suggest?");
        screen.display("1. Food");
        screen.display("2. Drink");

        int choice = pad.getInt();

        screen.display("\nEnter suggested name:");
        String name = pad.getString();

        screen.display("Enter description/details:");
        String description = pad.getString();

        screen.display("Enter key ingredients (comma-separated):");
        String ingredients = pad.getString();

        screen.display("\n✓ Your suggestion has been recorded!");
        screen.display("An author will review your suggestion.");
        screen.display("Name: " + name);
        screen.display("Description: " + description);
        screen.display("Key Ingredients: " + ingredients);

        // Note: Actual implementation would save this suggestion to a database
        return null;
    }

    /**
     * Main consultation method - allows customer to browse and search all formulations
     */
    @Override
    public void consultFormulation() {
        screen.display("\n=== CONSULT FORMULATIONS ===");

        if (availableFormulations == null || availableFormulations.isEmpty()) {
            screen.display("No formulations available at this time.");
            return;
        }

        screen.display("Total formulations available: " + availableFormulations.size());
        screen.display("\nSelect consultation option:");
        screen.display("1. View all formulations");
        screen.display("2. Search by name");
        screen.display("3. Search by ingredient");
        screen.display("4. View safe formulations (no allergens)");
        screen.display("5. View formulation details");
        screen.display("6. View my favorites");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                viewAllAvailableFormulations();
                break;
            case 2:
                searchFormulationsByName();
                break;
            case 3:
                searchFormulationsByIngredient();
                break;
            case 4:
                viewSafeFormulations();
                break;
            case 5:
                viewFormulationDetailsInteractive();
                break;
            case 6:
                viewFavorites();
                break;
            default:
                screen.display("Invalid choice!");
        }
    }

    /**
     * Checks formulation for issues relevant to this customer (mainly allergens)
     */
    @Override
    public void checkFormulationissues() {
        screen.display("\n=== CHECK FORMULATION FOR ALLERGENS ===");

        if (availableFormulations == null || availableFormulations.isEmpty()) {
            screen.display("No formulations available to check.");
            return;
        }

        screen.display("Enter formulation ID to check:");
        int id = pad.getInt();

        Item item = findFormulationById(id);

        if (item == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        screen.display("\n--- ALLERGEN CHECK FOR: " + item.getName() + " ---");

        // Check for customer's allergies
        if (info == null || info.getAllergies() == null || info.getAllergies().isEmpty()) {
            screen.display("✓ You have no registered allergies.");
            screen.display("This formulation should be safe for you.");
        } else {
            screen.display("Checking against your allergies: " + info.getAllergies());

            boolean hasAllergen = hasAllergenIn(item);

            if (hasAllergen) {
                screen.display("⚠ WARNING: This formulation contains ingredients you're allergic to!");
                screen.display("⚠ DO NOT CONSUME!");

                // Show which ingredients are problematic
                LinkedList<Ingredient> ingredients = getIngredients(item);
                if (ingredients != null) {
                    screen.display("\nProblematic ingredients:");
                    for (Ingredient ing : ingredients) {
                        for (String allergen : info.getAllergies()) {
                            if (ing.getName() != null &&
                                    ing.getName().toLowerCase().contains(allergen.toLowerCase())) {
                                screen.display("  ⚠ " + ing.getName() + " (matches: " + allergen + ")");
                            }
                        }
                    }
                }
            } else {
                screen.display("✓ SAFE: No allergens detected!");
                screen.display("This formulation appears safe for you.");
            }
        }

        // Ask if they want to see full ingredient list
        screen.display("\nView full ingredient list? (1=Yes, 0=No)");
        int viewIngredients = pad.getInt();

        if (viewIngredients == 1) {
            displayIngredients(item);
        }
    }

    // ============ CONSULTATION METHODS ============

    /**
     * Views all available formulations
     */
    private void viewAllAvailableFormulations() {
        screen.display("\n--- ALL AVAILABLE FORMULATIONS ---");

        if (availableFormulations.isEmpty()) {
            screen.display("No formulations available.");
            return;
        }

        int count = 1;
        for (Item item : availableFormulations) {
            screen.display("\n" + count + ". " + item.getName());
            screen.display("   Type: " + (item instanceof Food ? "Food" : "Drink"));
            screen.display("   ID: " + item.getItemID());
            screen.display("   Price: $" + item.getPrice());

            // Allergen warning
            if (hasAllergenIn(item)) {
                screen.display("   ⚠ WARNING: Contains allergens!");
            } else {
                screen.display("   ✓ Safe for you");
            }

            count++;
        }

        // Offer to view details or provide feedback
        screen.display("\nOptions:");
        screen.display("1. View details of a formulation");
        screen.display("2. Provide feedback on a formulation");
        screen.display("3. Add to favorites");
        screen.display("0. Return to menu");

        int action = pad.getInt();

        switch (action) {
            case 1:
                viewFormulationDetailsInteractive();
                break;
            case 2:
                provideFeedbackInteractive();
                break;
            case 3:
                addToFavoritesInteractive();
                break;
        }
    }

    /**
     * Searches formulations by name
     */
    private void searchFormulationsByName() {
        screen.display("\nEnter search term:");
        String searchTerm = pad.getString().toLowerCase();

        screen.display("\n--- SEARCH RESULTS ---");
        LinkedList<Item> results = new LinkedList<>();

        for (Item item : availableFormulations) {
            if (item.getName() != null &&
                    item.getName().toLowerCase().contains(searchTerm)) {
                results.add(item);
            }
        }

        if (results.isEmpty()) {
            screen.display("No formulations found matching '" + searchTerm + "'");
        } else {
            screen.display("Found " + results.size() + " formulation(s):");

            int count = 1;
            for (Item item : results) {
                screen.display("\n" + count + ". " + item.getName());
                screen.display("   ID: " + item.getItemID());
                screen.display("   Price: $" + item.getPrice());

                if (hasAllergenIn(item)) {
                    screen.display("   ⚠ Contains allergens!");
                } else {
                    screen.display("   ✓ Safe for you");
                }
                count++;
            }
        }
    }

    /**
     * Searches formulations by ingredient
     */
    private void searchFormulationsByIngredient() {
        screen.display("\nEnter ingredient name:");
        String ingredientName = pad.getString().toLowerCase();

        screen.display("\n--- FORMULATIONS CONTAINING: " + ingredientName + " ---");
        boolean found = false;

        for (Item item : availableFormulations) {
            LinkedList<Ingredient> ingredients = getIngredients(item);

            if (ingredients != null) {
                for (Ingredient ing : ingredients) {
                    if (ing.getName() != null &&
                            ing.getName().toLowerCase().contains(ingredientName)) {
                        screen.display("\n✓ " + item.getName());
                        screen.display("  ID: " + item.getItemID());
                        screen.display("  Contains: " + ing.getName());

                        if (hasAllergenIn(item)) {
                            screen.display("  ⚠ Contains allergens!");
                        }

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
     * Views only safe formulations (no allergens)
     */
    private void viewSafeFormulations() {
        screen.display("\n--- SAFE FORMULATIONS (NO ALLERGENS) ---");

        if (info == null || info.getAllergies() == null || info.getAllergies().isEmpty()) {
            screen.display("You have no registered allergies.");
            screen.display("All formulations are safe for you!");
            viewAllAvailableFormulations();
            return;
        }

        screen.display("Checking against your allergies: " + info.getAllergies());

        LinkedList<Item> safeItems = getSafeFormulations(availableFormulations);

        if (safeItems.isEmpty()) {
            screen.display("⚠ No safe formulations found!");
            screen.display("All available formulations contain your allergens.");
        } else {
            screen.display("Found " + safeItems.size() + " safe formulation(s):\n");

            int count = 1;
            for (Item item : safeItems) {
                screen.display(count + ". " + item.getName());
                screen.display("   ID: " + item.getItemID());
                screen.display("   Price: $" + item.getPrice());
                screen.display("   ✓ Safe for you\n");
                count++;
            }
        }
    }

    /**
     * Views detailed information about a formulation
     */
    private void viewFormulationDetailsInteractive() {
        screen.display("\nEnter formulation ID:");
        int id = pad.getInt();

        Item item = findFormulationById(id);

        if (item == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        displayFormulationDetails(item);

        // Offer actions
        screen.display("\nWhat would you like to do?");
        screen.display("1. Provide feedback");
        screen.display("2. Add to favorites");
        screen.display("3. Check for allergens");
        screen.display("0. Return");

        int action = pad.getInt();

        switch (action) {
            case 1:
                provideFeedbackForItem(item);
                break;
            case 2:
                addToFavorites(item);
                break;
            case 3:
                checkItemForAllergens(item);
                break;
        }
    }

    /**
     * Views customer's favorite formulations
     */
    private void viewFavorites() {
        screen.display("\n--- MY FAVORITE FORMULATIONS ---");

        if (favoriteFormulations.isEmpty()) {
            screen.display("You have no favorite formulations yet.");
            return;
        }

        screen.display("Total favorites: " + favoriteFormulations.size() + "\n");

        int count = 1;
        for (Item item : favoriteFormulations) {
            screen.display(count + ". " + item.getName());
            screen.display("   ID: " + item.getItemID());
            screen.display("   Price: $" + item.getPrice());
            count++;
        }

        screen.display("\nOptions:");
        screen.display("1. View details");
        screen.display("2. Remove from favorites");
        screen.display("0. Return");

        int choice = pad.getInt();

        if (choice == 1) {
            viewFormulationDetailsInteractive();
        } else if (choice == 2) {
            screen.display("Enter formulation ID to remove:");
            int id = pad.getInt();
            Item toRemove = findFormulationById(id);
            if (toRemove != null) {
                removeFromFavorites(toRemove);
            }
        }
    }

    // ============ FEEDBACK METHODS ============

    /**
     * Interactive feedback provision
     */
    private void provideFeedbackInteractive() {
        screen.display("\nEnter formulation ID to provide feedback:");
        int id = pad.getInt();

        Item item = findFormulationById(id);

        if (item == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        provideFeedbackForItem(item);
    }

    /**
     * Provides feedback for a specific item
     */
    private void provideFeedbackForItem(Item item) {
        screen.display("\n--- PROVIDE FEEDBACK FOR: " + item.getName() + " ---");

        screen.display("Did you like this formulation? (1=Yes, 0=No):");
        int likeChoice = pad.getInt();
        boolean like = (likeChoice == 1);

        screen.display("Enter your comment:");
        String comment = pad.getString();

        // Rate (optional)
        screen.display("Rate from 1-5 stars (0 to skip):");
        int rating = pad.getInt();

        Feedback feedback = provideFeedback(item, comment, like);

        screen.display("\n✓ Thank you for your feedback!");
        screen.display("Your opinion helps improve our formulations.");
    }

    /**
     * Customer provides feedback on a formulation (programmatic)
     */
    public Feedback provideFeedback(Item item, String comment, boolean like) {
        Feedback feedback = new Feedback(comment, like, getName());
        feedbackHistory.add(feedback);

        if (item instanceof Food) {
            ((Food) item).addFeedback(feedback);
        } else if (item instanceof Drink) {
            ((Drink) item).addFeedback(feedback);
        }

        System.out.println("Feedback submitted for: " + item.getName());
        return feedback;
    }

    // ============ ALLERGEN CHECKING ============

    /**
     * Checks if item contains allergens for this customer
     */
    public boolean hasAllergenIn(Item item) {
        if (info == null || info.getAllergies() == null || info.getAllergies().isEmpty()) {
            return false;
        }

        LinkedList<Ingredient> ingredients = getIngredients(item);

        if (ingredients == null) {
            return false;
        }

        for (Ingredient ing : ingredients) {
            for (String allergen : info.getAllergies()) {
                if (ing.getName() != null &&
                        ing.getName().toLowerCase().contains(allergen.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks specific item for allergens interactively
     */
    private void checkItemForAllergens(Item item) {
        screen.display("\n--- ALLERGEN CHECK ---");

        if (info == null || info.getAllergies() == null || info.getAllergies().isEmpty()) {
            screen.display("You have no registered allergies.");
            screen.display("✓ This formulation is safe for you.");
            return;
        }

        boolean hasAllergen = hasAllergenIn(item);

        if (hasAllergen) {
            screen.display("⚠ WARNING: Contains allergens!");
            screen.display("Your allergies: " + info.getAllergies());

            LinkedList<Ingredient> ingredients = getIngredients(item);
            if (ingredients != null) {
                screen.display("\nProblematic ingredients:");
                for (Ingredient ing : ingredients) {
                    for (String allergen : info.getAllergies()) {
                        if (ing.getName() != null &&
                                ing.getName().toLowerCase().contains(allergen.toLowerCase())) {
                            screen.display("  ⚠ " + ing.getName());
                        }
                    }
                }
            }
        } else {
            screen.display("✓ SAFE: No allergens detected!");
        }
    }

    // ============ FAVORITES MANAGEMENT ============

    /**
     * Interactive add to favorites
     */
    private void addToFavoritesInteractive() {
        screen.display("\nEnter formulation ID to add to favorites:");
        int id = pad.getInt();

        Item item = findFormulationById(id);

        if (item != null) {
            addToFavorites(item);
        } else {
            screen.display("Formulation not found with ID: " + id);
        }
    }

    /**
     * Adds formulation to customer's favorites
     */
    public void addToFavorites(Item item) {
        if (item != null && !favoriteFormulations.contains(item)) {
            favoriteFormulations.add(item);
            screen.display("✓ Added " + item.getName() + " to favorites");
        } else if (favoriteFormulations.contains(item)) {
            screen.display("This formulation is already in your favorites.");
        }
    }

    /**
     * Removes formulation from favorites
     */
    public boolean removeFromFavorites(Item item) {
        boolean removed = favoriteFormulations.remove(item);
        if (removed) {
            screen.display("✓ Removed " + item.getName() + " from favorites");
        }
        return removed;
    }

    /**
     * Gets formulations suitable for this customer (no allergens)
     */
    public LinkedList<Item> getSafeFormulations(LinkedList<Item> allFormulations) {
        LinkedList<Item> safe = new LinkedList<>();

        for (Item item : allFormulations) {
            if (!hasAllergenIn(item)) {
                safe.add(item);
            }
        }

        return safe;
    }

    // ============ HELPER METHODS ============

    /**
     * Finds formulation by ID
     */
    private Item findFormulationById(int id) {
        for (Item item : availableFormulations) {
            if (item.getItemID() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gets ingredients from an item
     */
    private LinkedList<Ingredient> getIngredients(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            return ((Drink) item).getIngredients();
        }
        return null;
    }

    /**
     * Displays detailed formulation information
     */
    private void displayFormulationDetails(Item item) {
        screen.display("\n=== FORMULATION DETAILS ===");
        screen.display("Name: " + item.getName());
        screen.display("Type: " + (item instanceof Food ? "Food" : "Drink"));
        screen.display("ID: " + item.getItemID());
        screen.display("Price: $" + item.getPrice());

        // Allergen status
        if (hasAllergenIn(item)) {
            screen.display("⚠ WARNING: Contains allergens for you!");
        } else {
            screen.display("✓ Safe for you (no allergens)");
        }

        // Ingredients
        displayIngredients(item);

        // Feedbacks
        LinkedList<Feedback> feedbacks = null;
        if (item instanceof Food) {
            feedbacks = ((Food) item).getFeedbacks();
        } else if (item instanceof Drink) {
            feedbacks = ((Drink) item).getFeedbacks();
        }

        if (feedbacks != null && !feedbacks.isEmpty()) {
            screen.display("\nCustomer Reviews (" + feedbacks.size() + "):");
            int count = 1;
            for (Feedback fb : feedbacks) {
                if (count <= 3) { // Show first 3
                    screen.display("  " + (fb.isLike() ? "👍" : "👎") + " " +
                            fb.getConsumerName() + ": " + fb.getComment());
                }
                count++;
            }
            if (feedbacks.size() > 3) {
                screen.display("  ... and " + (feedbacks.size() - 3) + " more reviews");
            }
        }
    }

    /**
     * Displays ingredients list
     */
    private void displayIngredients(Item item) {
        LinkedList<Ingredient> ingredients = getIngredients(item);

        if (ingredients != null && !ingredients.isEmpty()) {
            screen.display("\nIngredients:");
            for (Ingredient ing : ingredients) {
                String allergenMark = "";

                // Mark allergens
                if (info != null && info.getAllergies() != null) {
                    for (String allergen : info.getAllergies()) {
                        if (ing.getName() != null &&
                                ing.getName().toLowerCase().contains(allergen.toLowerCase())) {
                            allergenMark = " ⚠ ALLERGEN";
                            break;
                        }
                    }
                }

                screen.display("  - " + ing.getName() + allergenMark);

                if (ing.getQuantity() != null) {
                    screen.display("    Weight: " + ing.getQuantity().getWeight() + "g, " +
                            "Volume: " + ing.getQuantity().getVolume() + "ml");
                }
            }
        } else {
            screen.display("\nNo ingredients listed.");
        }
    }

    // ============ SYSTEM METHODS ==========

    public void hasPayed(boolean payed){
        this.payed = payed;
    }
    /**
     * Sets available formulations (called by system/manager)
     */
    public void setAvailableFormulations(LinkedList<Item> formulations) {
        this.availableFormulations = formulations;
    }

    /**
     * Gets available formulations
     */
    public LinkedList<Item> getAvailableFormulations() {
        return availableFormulations;
    }

    // ============ GETTERS AND SETTERS ============

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ConsumerSpecificInfo getInfo() {
        return info;
    }

    public void setInfo(ConsumerSpecificInfo info) {
        this.info = info;
    }

    public LinkedList<Item> getFavoriteFormulations() {
        return favoriteFormulations;
    }

    public LinkedList<Feedback> getFeedbackHistory() {
        return feedbackHistory;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", name='" + getName() + '\'' +
                ", age=" + age +
                ", allergies=" + (info != null && info.getAllergies() != null ?
                info.getAllergies().size() : 0) +
                ", favoritesCount=" + favoriteFormulations.size() +
                ", feedbackCount=" + feedbackHistory.size() +
                '}';
    }
}