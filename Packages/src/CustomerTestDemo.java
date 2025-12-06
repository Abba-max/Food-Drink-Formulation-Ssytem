import MyClasses.Consumables.Food;
import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Item;
import MyClasses.Ingredient;
import MyClasses.Persons.ConsumerSpecificInfo;
import MyClasses.Persons.Customer;
import MyClasses.Quantity;
import MyClasses.Feedback;

import java.util.LinkedList;

/**
 * Demonstration of Customer functionality:
 * 1. Consult all formulations
 * 2. Provide feedback
 * 3. Check for allergens
 * 4. Manage favorites
 */
public class CustomerTestDemo {

    public static void main(String[] args) {
        System.out.println("=== CUSTOMER FUNCTIONALITY DEMONSTRATION ===\n");

        // Setup: Create sample formulations
        LinkedList<Item> availableFormulations = createSampleFormulations();

        // Create customer with allergies
        Customer customer = new Customer(1, "Alice Johnson", "New York",
                "+1-555-0001", "1990-05-15", 28);

        // Set up customer allergies
        ConsumerSpecificInfo info = new ConsumerSpecificInfo();
        info.setProfile("Health-conscious, vegetarian");
        info.addAllergy("peanuts");
        info.addAllergy("shellfish");
        info.addAllergy("dairy");
        customer.setInfo(info);

        // Provide customer access to all formulations
        customer.setAvailableFormulations(availableFormulations);

        System.out.println("Customer: " + customer.getName());
        System.out.println("Allergies: " + info.getAllergies());
        System.out.println("Available formulations: " + availableFormulations.size());
        System.out.println();

        // ===== TEST 1: VIEW ALL FORMULATIONS =====
        System.out.println("--- Test 1: Browse All Formulations ---");
        testBrowseFormulations(customer, availableFormulations);

        // ===== TEST 2: ALLERGEN CHECKING =====
        System.out.println("\n--- Test 2: Check for Allergens ---");
        testAllergenChecking(customer, availableFormulations);

        // ===== TEST 3: SAFE FORMULATIONS =====
        System.out.println("\n--- Test 3: Find Safe Formulations ---");
        testSafeFormulations(customer);

        // ===== TEST 4: PROVIDE FEEDBACK =====
        System.out.println("\n--- Test 4: Provide Feedback ---");
        testProvideFeedback(customer, availableFormulations);

        // ===== TEST 5: FAVORITES MANAGEMENT =====
        System.out.println("\n--- Test 5: Manage Favorites ---");
        testFavoritesManagement(customer, availableFormulations);

        // ===== TEST 6: SEARCH FUNCTIONALITY =====
        System.out.println("\n--- Test 6: Search Formulations ---");
        testSearchFunctionality(customer, availableFormulations);

        // ===== FINAL STATISTICS =====
        System.out.println("\n--- Customer Statistics ---");
        System.out.println("Total formulations viewed: " + availableFormulations.size());
        System.out.println("Feedback provided: " + customer.getFeedbackHistory().size());
        System.out.println("Favorites: " + customer.getFavoriteFormulations().size());
        System.out.println("Safe formulations: " +
                customer.getSafeFormulations(availableFormulations).size());

        System.out.println("\n" + "=".repeat(50));
        System.out.println("✓ All customer functionality tests completed!");
        System.out.println("=".repeat(50));
    }

    /**
     * Test 1: Browse formulations
     */
    private static void testBrowseFormulations(Customer customer, LinkedList<Item> formulations) {
        System.out.println("Browsing available formulations...\n");

        int count = 1;
        for (Item item : formulations) {
            System.out.println(count + ". " + item.getName());
            System.out.println("   Type: " + (item instanceof Food ? "Food" : "Drink"));
            System.out.println("   Price: $" + item.getPrice());

            // Check allergen status
            if (customer.hasAllergenIn(item)) {
                System.out.println("   ⚠ WARNING: Contains allergens!");
            } else {
                System.out.println("   ✓ Safe for you");
            }

            count++;
        }

        System.out.println("\nTotal formulations: " + formulations.size());
    }

    /**
     * Test 2: Allergen checking
     */
    private static void testAllergenChecking(Customer customer, LinkedList<Item> formulations) {
        System.out.println("Checking formulations for allergens...\n");

        for (Item item : formulations) {
            System.out.println("Checking: " + item.getName());
            boolean hasAllergen = customer.hasAllergenIn(item);

            if (hasAllergen) {
                System.out.println("  ⚠ UNSAFE - Contains:");

                // Show which allergens
                LinkedList<Ingredient> ingredients = null;
                if (item instanceof Food) {
                    ingredients = ((Food) item).getIngredients();
                } else if (item instanceof Drink) {
                    ingredients = ((Drink) item).getIngredients();
                }

                if (ingredients != null) {
                    for (Ingredient ing : ingredients) {
                        for (String allergen : customer.getInfo().getAllergies()) {
                            if (ing.getName() != null &&
                                    ing.getName().toLowerCase().contains(allergen.toLowerCase())) {
                                System.out.println("    - " + ing.getName() + " (allergen: " + allergen + ")");
                            }
                        }
                    }
                }
            } else {
                System.out.println("  ✓ SAFE");
            }
            System.out.println();
        }
    }

    /**
     * Test 3: Safe formulations
     */
    private static void testSafeFormulations(Customer customer) {
        System.out.println("Finding safe formulations (no allergens)...\n");

        LinkedList<Item> safeItems = customer.getSafeFormulations(
                customer.getAvailableFormulations()
        );

        if (safeItems.isEmpty()) {
            System.out.println("⚠ No safe formulations found!");
        } else {
            System.out.println("✓ Found " + safeItems.size() + " safe formulation(s):\n");

            for (Item item : safeItems) {
                System.out.println("  - " + item.getName());
                System.out.println("    Price: $" + item.getPrice());
                System.out.println("    ✓ No allergens detected\n");
            }
        }
    }

    /**
     * Test 4: Provide feedback
     */
    private static void testProvideFeedback(Customer customer, LinkedList<Item> formulations) {
        System.out.println("Providing feedback on formulations...\n");

        // Positive feedback on safe item
        Item safeItem = null;
        for (Item item : formulations) {
            if (!customer.hasAllergenIn(item)) {
                safeItem = item;
                break;
            }
        }

        if (safeItem != null) {
            System.out.println("Feedback on: " + safeItem.getName());
            Feedback feedback1 = customer.provideFeedback(
                    safeItem,
                    "Absolutely delicious! Love the taste and it's safe for me.",
                    true
            );
            System.out.println("  Type: " + (feedback1.isLike() ? "Positive 👍" : "Negative 👎"));
            System.out.println("  Comment: " + feedback1.getComment());
            System.out.println();
        }

        // Negative feedback on item with allergens
        Item unsafeItem = null;
        for (Item item : formulations) {
            if (customer.hasAllergenIn(item)) {
                unsafeItem = item;
                break;
            }
        }

        if (unsafeItem != null) {
            System.out.println("Feedback on: " + unsafeItem.getName());
            Feedback feedback2 = customer.provideFeedback(
                    unsafeItem,
                    "Unfortunately contains ingredients I'm allergic to. Would love a dairy-free version!",
                    false
            );
            System.out.println("  Type: " + (feedback2.isLike() ? "Positive 👍" : "Negative 👎"));
            System.out.println("  Comment: " + feedback2.getComment());
            System.out.println();
        }

        System.out.println("Total feedback provided: " + customer.getFeedbackHistory().size());
    }

    /**
     * Test 5: Favorites management
     */
    private static void testFavoritesManagement(Customer customer, LinkedList<Item> formulations) {
        System.out.println("Managing favorite formulations...\n");

        // Add safe items to favorites
        LinkedList<Item> safeItems = customer.getSafeFormulations(formulations);

        System.out.println("Adding safe items to favorites:");
        for (Item item : safeItems) {
            customer.addToFavorites(item);
            System.out.println("  ✓ Added: " + item.getName());
        }

        System.out.println("\nCurrent favorites: " + customer.getFavoriteFormulations().size());

        // Display favorites
        System.out.println("\nMy Favorite Formulations:");
        int count = 1;
        for (Item fav : customer.getFavoriteFormulations()) {
            System.out.println("  " + count + ". " + fav.getName() + " ($" + fav.getPrice() + ")");
            count++;
        }

        // Remove one favorite
        if (!customer.getFavoriteFormulations().isEmpty()) {
            Item toRemove = customer.getFavoriteFormulations().get(0);
            System.out.println("\nRemoving: " + toRemove.getName());
            customer.removeFromFavorites(toRemove);
            System.out.println("Remaining favorites: " + customer.getFavoriteFormulations().size());
        }
    }

    /**
     * Test 6: Search functionality
     */
    private static void testSearchFunctionality(Customer customer, LinkedList<Item> formulations) {
        System.out.println("Testing search functionality...\n");

        // Search by name
        System.out.println("1. Search by name 'salad':");
        for (Item item : formulations) {
            if (item.getName() != null &&
                    item.getName().toLowerCase().contains("salad")) {
                System.out.println("   Found: " + item.getName());
                System.out.println("   Safe: " + !customer.hasAllergenIn(item));
            }
        }

        // Search by ingredient
        System.out.println("\n2. Search by ingredient 'tomato':");
        for (Item item : formulations) {
            LinkedList<Ingredient> ingredients = null;
            if (item instanceof Food) {
                ingredients = ((Food) item).getIngredients();
            } else if (item instanceof Drink) {
                ingredients = ((Drink) item).getIngredients();
            }

            if (ingredients != null) {
                for (Ingredient ing : ingredients) {
                    if (ing.getName() != null &&
                            ing.getName().toLowerCase().contains("tomato")) {
                        System.out.println("   " + item.getName() + " contains " + ing.getName());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Creates sample formulations for testing
     */
    private static LinkedList<Item> createSampleFormulations() {
        LinkedList<Item> formulations = new LinkedList<>();

        // Food 1: Caesar Salad (contains dairy - unsafe)
        Food salad = new Food();
        salad.setName("Caesar Salad");
        salad.setItemID(1001);
        salad.setPrice(8.99);
        salad.addIngredient(new Ingredient(1, "Romaine Lettuce",
                new Quantity(200, 0, 0.2, "grams")));
        salad.addIngredient(new Ingredient(2, "Parmesan Cheese",
                new Quantity(50, 0, 0.05, "grams")));
        salad.addIngredient(new Ingredient(3, "Croutons",
                new Quantity(30, 0, 0.03, "grams")));
        formulations.add(salad);

        // Food 2: Vegetable Stir-Fry (safe)
        Food stirfry = new Food();
        stirfry.setName("Vegetable Stir-Fry");
        stirfry.setItemID(1002);
        stirfry.setPrice(10.99);
        stirfry.addIngredient(new Ingredient(4, "Broccoli",
                new Quantity(150, 0, 0.15, "grams")));
        stirfry.addIngredient(new Ingredient(5, "Carrots",
                new Quantity(100, 0, 0.1, "grams")));
        stirfry.addIngredient(new Ingredient(6, "Soy Sauce",
                new Quantity(0, 30, 0.03, "ml")));
        formulations.add(stirfry);

        // Food 3: Peanut Butter Cookies (contains peanuts - unsafe)
        Food cookies = new Food();
        cookies.setName("Peanut Butter Cookies");
        cookies.setItemID(1003);
        cookies.setPrice(6.99);
        cookies.addIngredient(new Ingredient(7, "Peanut Butter",
                new Quantity(200, 0, 0.2, "grams")));
        cookies.addIngredient(new Ingredient(8, "Flour",
                new Quantity(150, 0, 0.15, "grams")));
        cookies.addIngredient(new Ingredient(9, "Sugar",
                new Quantity(100, 0, 0.1, "grams")));
        formulations.add(cookies);

        // Drink 1: Fresh Orange Juice (safe)
        Drink orangeJuice = new Drink();
        orangeJuice.setName("Fresh Orange Juice");
        orangeJuice.setItemID(2001);
        orangeJuice.setPrice(4.99);
        orangeJuice.addIngredient(new Ingredient(10, "Fresh Oranges",
                new Quantity(0, 250, 0.25, "ml")));
        formulations.add(orangeJuice);

        // Drink 2: Milkshake (contains dairy - unsafe)
        Drink milkshake = new Drink();
        milkshake.setName("Chocolate Milkshake");
        milkshake.setItemID(2002);
        milkshake.setPrice(5.99);
        milkshake.addIngredient(new Ingredient(11, "Milk",
                new Quantity(0, 200, 0.2, "ml")));
        milkshake.addIngredient(new Ingredient(12, "Ice Cream",
                new Quantity(100, 0, 0.1, "grams")));
        milkshake.addIngredient(new Ingredient(13, "Chocolate Syrup",
                new Quantity(0, 30, 0.03, "ml")));
        formulations.add(milkshake);

        // Food 4: Tomato Soup (safe)
        Food soup = new Food();
        soup.setName("Tomato Soup");
        soup.setItemID(1004);
        soup.setPrice(7.99);
        soup.addIngredient(new Ingredient(14, "Tomatoes",
                new Quantity(300, 0, 0.3, "grams")));
        soup.addIngredient(new Ingredient(15, "Onions",
                new Quantity(50, 0, 0.05, "grams")));
        soup.addIngredient(new Ingredient(16, "Vegetable Broth",
                new Quantity(0, 200, 0.2, "ml")));
        formulations.add(soup);

        return formulations;
    }
}

/* ============================================================
 * EXPECTED OUTPUT SUMMARY:
 * ============================================================
 *
 * The demo shows:
 *
 * 1. ✓ Customer can browse all formulations
 * 2. ✓ Automatic allergen detection and warnings
 * 3. ✓ Filter for safe formulations only
 * 4. ✓ Provide positive and negative feedback
 * 5. ✓ Add/remove favorites
 * 6. ✓ Search by name and ingredient
 *
 * Customer with allergies to peanuts, shellfish, and dairy:
 *
 * SAFE Formulations:
 * - Vegetable Stir-Fry
 * - Fresh Orange Juice
 * - Tomato Soup
 *
 * UNSAFE Formulations (contain allergens):
 * - Caesar Salad (contains Parmesan - dairy)
 * - Peanut Butter Cookies (contains peanuts)
 * - Chocolate Milkshake (contains milk - dairy)
 *
 * ============================================================
 */