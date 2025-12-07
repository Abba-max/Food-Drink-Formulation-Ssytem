package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Feedback;
import MyClasses.Formulation;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Date;

/**
 * Customer class with payment verification
 * Can only view full details after payment
 */
public class Customer extends Person implements Formulation {
    private int customerID;
    private int age;
    private ConsumerSpecificInfo info;
    private LinkedList<Item> favoriteFormulations;
    private LinkedList<Feedback> feedbackHistory;
    private LinkedList<Item> availableFormulations;

    // Payment tracking
    private HashMap<Integer, PurchaseRecord> purchasedItems; // itemID -> PurchaseRecord

    private Keypad pad = new Keypad();
    private Screen screen = new Screen();

    public Customer() {
        super();
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
        this.availableFormulations = new LinkedList<>();
        this.purchasedItems = new HashMap<>();
    }

    public Customer(int customerID, int age) {
        super();
        this.customerID = customerID;
        this.age = age;
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
        this.availableFormulations = new LinkedList<>();
        this.purchasedItems = new HashMap<>();
    }

    // ============ PAYMENT METHODS ============

    /**
     * Checks if customer has paid for a specific formulation
     */
    public boolean isPaid(Item item) {
        if (item == null) return false;
        return purchasedItems.containsKey(item.getItemID());
    }

    /**
     * Process payment for an item
     */
    public boolean makePayment(Item item, String paymentMethod) {
        if (item == null) {
            screen.display("Invalid item!");
            return false;
        }

        if (isPaid(item)) {
            screen.display("You have already purchased this item!");
            return true;
        }

        screen.display("\n=== PAYMENT PROCESSING ===");
        screen.display("Item: " + item.getName());
        screen.display("Price: $" + item.getPrice());
        screen.display("Payment Method: " + paymentMethod);
        screen.display("\nConfirm payment? (1=Yes, 0=No)");

        int confirm = pad.getInt();

        if (confirm == 1) {
            PurchaseRecord record = new PurchaseRecord(
                    item.getItemID(),
                    item.getName(),
                    item.getPrice(),
                    new Date(),
                    paymentMethod
            );

            purchasedItems.put(item.getItemID(), record);
            screen.display("\n✓ Payment successful!");
            screen.display("You can now view full details of this formulation.");
            return true;
        } else {
            screen.display("Payment cancelled.");
            return false;
        }
    }

    /**
     * View purchase history
     */
    public void viewPurchaseHistory() {
        screen.display("\n=== MY PURCHASE HISTORY ===");

        if (purchasedItems.isEmpty()) {
            screen.display("No purchases yet.");
            return;
        }

        screen.display("Total purchases: " + purchasedItems.size() + "\n");

        int count = 1;
        for (PurchaseRecord record : purchasedItems.values()) {
            screen.display(count + ". " + record.toString());
            count++;
        }
    }

    // ============ FORMULATION INTERFACE ============

    @Override
    public Item Formulate() {
        screen.display("Customers cannot create formulations.");
        screen.display("You can only browse and purchase available items.");
        return null;
    }

    /**
     * Browse catalog - limited view without payment
     */
    @Override
    public void consultFormulation() {
        screen.display("\n=== BROWSE CATALOG ===");

        if (availableFormulations == null || availableFormulations.isEmpty()) {
            screen.display("No formulations available at this time.");
            return;
        }

        screen.display("Total items available: " + availableFormulations.size());
        screen.display("\nSelect option:");
        screen.display("1. View catalog (basic info)");
        screen.display("2. View purchased items (full details)");
        screen.display("3. Search by name");
        screen.display("4. Purchase an item");
        screen.display("5. View purchase history");
        screen.display("0. Return to main menu");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                viewCatalog();
                break;
            case 2:
                viewPurchasedItems();
                break;
            case 3:
                searchCatalog();
                break;
            case 4:
                purchaseItem();
                break;
            case 5:
                viewPurchaseHistory();
                break;
            case 0:
                return;
            default:
                screen.display("Invalid choice!");
        }
    }

    /**
     * CUSTOMERS CANNOT CHECK FORMULATION ISSUES
     * Only Authors and Admins can do this
     */
    @Override
    public void checkFormulationissues() {
        screen.display("\n⚠ ACCESS DENIED");
        screen.display("Only Authors and Administrators can check formulation issues.");
        screen.display("As a customer, you can:");
        screen.display("  - Browse the catalog");
        screen.display("  - Purchase items");
        screen.display("  - View full details after purchase");
        screen.display("  - Provide feedback on purchased items");
    }

    // ============ CATALOG BROWSING ============

    /**
     * View catalog with basic information only (no payment required)
     */
    private void viewCatalog() {
        screen.display("\n=== CATALOG (PUBLIC VIEW) ===");
        screen.display("Browse available formulations\n");

        int count = 1;
        for (Item item : availableFormulations) {
            screen.display(count + ". " + item.getName());
            screen.display("   Type: " + (item instanceof Food ? "Food" : "Drink"));
            screen.display("   ID: " + item.getItemID());
            screen.display("   Price: $" + item.getPrice());

            // Show creation date if available
            if (item.entry_date != null) {
                screen.display("   Created: " + item.entry_date);
            }

            // Show if already purchased
            if (isPaid(item)) {
                screen.display("   ✓ PURCHASED - Full details available");
            } else {
                screen.display("   🔒 Purchase to view full details");
            }

            screen.display("");
            count++;
        }

        screen.display("\nNote: Purchase items to view ingredients, preparation, and full details.");
    }

    /**
     * View only purchased items with full details
     */
    private void viewPurchasedItems() {
        screen.display("\n=== MY PURCHASED ITEMS ===");

        LinkedList<Item> purchased = new LinkedList<>();
        for (Item item : availableFormulations) {
            if (isPaid(item)) {
                purchased.add(item);
            }
        }

        if (purchased.isEmpty()) {
            screen.display("You haven't purchased any items yet.");
            screen.display("Purchase items from the catalog to view full details.");
            return;
        }

        screen.display("Total purchased: " + purchased.size() + "\n");

        int count = 1;
        for (Item item : purchased) {
            screen.display(count + ". " + item.getName() + " (ID: " + item.getItemID() + ")");
            count++;
        }

        screen.display("\nEnter item ID to view full details (0 to return):");
        int id = pad.getInt();

        if (id == 0) return;

        Item selected = findItemById(id);
        if (selected != null && isPaid(selected)) {
            viewFullDetails(selected);
        } else if (selected != null) {
            screen.display("⚠ You must purchase this item to view full details!");
        } else {
            screen.display("Item not found!");
        }
    }

    /**
     * View FULL details of a purchased item
     */
    private void viewFullDetails(Item item) {
        screen.display("\n=== FULL DETAILS: " + item.getName() + " ===");
        screen.display("Type: " + (item instanceof Food ? "Food" : "Drink"));
        screen.display("ID: " + item.getItemID());
        screen.display("Price: $" + item.getPrice());

        // Show ingredients (only for paid items)
        LinkedList<Ingredient> ingredients = getIngredients(item);
        if (ingredients != null && !ingredients.isEmpty()) {
            screen.display("\n--- INGREDIENTS ---");
            for (Ingredient ing : ingredients) {
                screen.display("  - " + ing.getName());
                if (ing.getQuantity() != null) {
                    screen.display("    Weight: " + ing.getQuantity().getWeight() + "g");
                    screen.display("    Volume: " + ing.getQuantity().getVolume() + "ml");
                }
            }
        }

        // Show preparation protocol
        if (item instanceof Food) {
            Food food = (Food) item;
            if (food.getPrepprotocol() != null) {
                screen.display("\n--- PREPARATION ---");
                food.getPrepprotocol().displayProtocol();
            }
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            if (drink.getPrepprotocol() != null) {
                screen.display("\n--- PREPARATION ---");
                drink.getPrepprotocol().displayProtocol();
            }
        }

        // Show standards
        LinkedList<String> standards = null;
        if (item instanceof Food) {
            standards = ((Food) item).getStandards();
        } else if (item instanceof Drink) {
            standards = ((Drink) item).getStandards();
        }

        if (standards != null && !standards.isEmpty()) {
            screen.display("\n--- STANDARDS ---");
            for (String standard : standards) {
                screen.display("  ✓ " + standard);
            }
        }

        // Options for purchased items
        screen.display("\n--- OPTIONS ---");
        screen.display("1. Provide feedback");
        screen.display("2. Add to favorites");
        screen.display("0. Return");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                provideFeedbackForItem(item);
                break;
            case 2:
                addToFavorites(item);
                break;
        }
    }

    /**
     * Search catalog by name
     */
    private void searchCatalog() {
        screen.display("\nEnter search term:");
        String searchTerm = pad.getString().toLowerCase();

        screen.display("\n=== SEARCH RESULTS ===");
        boolean found = false;

        for (Item item : availableFormulations) {
            if (item.getName() != null &&
                    item.getName().toLowerCase().contains(searchTerm)) {
                screen.display("\n✓ " + item.getName());
                screen.display("  ID: " + item.getItemID());
                screen.display("  Price: $" + item.getPrice());
                screen.display("  Status: " + (isPaid(item) ? "PURCHASED" : "NOT PURCHASED"));
                found = true;
            }
        }

        if (!found) {
            screen.display("No items found matching '" + searchTerm + "'");
        }
    }

    /**
     * Purchase an item
     */
    private void purchaseItem() {
        screen.display("\nEnter item ID to purchase:");
        int id = pad.getInt();

        Item item = findItemById(id);

        if (item == null) {
            screen.display("Item not found with ID: " + id);
            return;
        }

        if (isPaid(item)) {
            screen.display("You have already purchased this item!");
            screen.display("You can view its full details from 'My Purchased Items'");
            return;
        }

        screen.display("\nSelect payment method:");
        screen.display("1. Credit Card");
        screen.display("2. Debit Card");
        screen.display("3. Mobile Payment");
        screen.display("4. Cash");

        int paymentChoice = pad.getInt();
        String paymentMethod;

        switch (paymentChoice) {
            case 1: paymentMethod = "Credit Card"; break;
            case 2: paymentMethod = "Debit Card"; break;
            case 3: paymentMethod = "Mobile Payment"; break;
            case 4: paymentMethod = "Cash"; break;
            default: paymentMethod = "Unknown"; break;
        }

        makePayment(item, paymentMethod);
    }

    // ============ FEEDBACK (ONLY FOR PURCHASED ITEMS) ============

    /**
     * Provide feedback for a purchased item
     */
    private void provideFeedbackForItem(Item item) {
        if (!isPaid(item)) {
            screen.display("⚠ You must purchase this item before providing feedback!");
            return;
        }

        screen.display("\n--- PROVIDE FEEDBACK ---");
        screen.display("Did you like this formulation? (1=Yes, 0=No):");
        int likeChoice = pad.getInt();
        boolean like = (likeChoice == 1);

        screen.display("Enter your comment:");
        String comment = pad.getString();

        provideFeedback(item, comment, like);
    }

    public Feedback provideFeedback(Item item, String comment, boolean like) {
        if (!isPaid(item)) {
            System.out.println("⚠ Must purchase item before providing feedback!");
            return null;
        }

        Feedback feedback = new Feedback(comment, like, getName());
        feedbackHistory.add(feedback);

        if (item instanceof Food) {
            ((Food) item).addFeedback(feedback);
        } else if (item instanceof Drink) {
            ((Drink) item).addFeedback(feedback);
        }

        screen.display("\n✓ Thank you for your feedback!");
        return feedback;
    }

    // ============ FAVORITES ============

    public void addToFavorites(Item item) {
        if (item != null && !favoriteFormulations.contains(item)) {
            favoriteFormulations.add(item);
            screen.display("✓ Added " + item.getName() + " to favorites");
        }
    }

    public boolean removeFromFavorites(Item item) {
        boolean removed = favoriteFormulations.remove(item);
        if (removed) {
            screen.display("✓ Removed " + item.getName() + " from favorites");
        }
        return removed;
    }

    // ============ HELPER METHODS ============

    private Item findItemById(int id) {
        for (Item item : availableFormulations) {
            if (item.getItemID() == id) {
                return item;
            }
        }
        return null;
    }

    private LinkedList<Ingredient> getIngredients(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            return ((Drink) item).getIngredients();
        }
        return null;
    }

    public boolean hasAllergenIn(Item item) {
        if (info == null || info.getAllergies() == null || info.getAllergies().isEmpty()) {
            return false;
        }

        LinkedList<Ingredient> ingredients = getIngredients(item);
        if (ingredients == null) return false;

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

    public LinkedList<Item> getSafeFormulations(LinkedList<Item> allFormulations) {
        LinkedList<Item> safe = new LinkedList<>();
        for (Item item : allFormulations) {
            if (!hasAllergenIn(item)) {
                safe.add(item);
            }
        }
        return safe;
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

    public void setAvailableFormulations(LinkedList<Item> formulations) {
        this.availableFormulations = formulations;
    }

    public LinkedList<Item> getAvailableFormulations() {
        return availableFormulations;
    }

    public HashMap<Integer, PurchaseRecord> getPurchasedItems() {
        return purchasedItems;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", name='" + getName() + '\'' +
                ", age=" + age +
                ", purchases=" + purchasedItems.size() +
                ", favoritesCount=" + favoriteFormulations.size() +
                ", feedbackCount=" + feedbackHistory.size() +
                '}';
    }

    // ============ INNER CLASS: PURCHASE RECORD ============

    public static class PurchaseRecord {
        private int itemID;
        private String itemName;
        private double price;
        private Date purchaseDate;
        private String paymentMethod;

        public PurchaseRecord(int itemID, String itemName, double price, Date purchaseDate, String paymentMethod) {
            this.itemID = itemID;
            this.itemName = itemName;
            this.price = price;
            this.purchaseDate = purchaseDate;
            this.paymentMethod = paymentMethod;
        }

        @Override
        public String toString() {
            return itemName + " - $" + price + " (Paid: " + purchaseDate + ", Method: " + paymentMethod + ")";
        }

        // Getters
        public int getItemID() { return itemID; }
        public String getItemName() { return itemName; }
        public double getPrice() { return price; }
        public Date getPurchaseDate() { return purchaseDate; }
        public String getPaymentMethod() { return paymentMethod; }
    }
}