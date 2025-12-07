//import MyClasses.Ingredients.Ingredient;
//import MyClasses.Persons.Author;
//import MyClasses.Consumables.Food;
//import MyClasses.Consumables.Item;
//import MyClasses.Ingredients.Quantity;
//
///**
// * Example demonstrating how the Author class is used
// * Shows the three main functionalities:
// * 1. Formulate - Create new food/drink
// * 2. Consult Formulation - View and search formulations
// * 3. Check Formulation Issues - Identify problems
// */
//public class AuthorTestExample {
//
//    public static void main(String[] args) {
//        System.out.println("=== AUTHOR FUNCTIONALITY DEMONSTRATION ===\n");
//
//        // Create an author
//        Author author = new Author(101, "Chef Gordon", "London, UK",
//                "+44-123-456", "1966-11-08");
//
//        System.out.println("Author created: " + author.getName());
//        System.out.println("Author ID: " + author.getAuthorID());
//
//        // ===== SCENARIO 1: INTERACTIVE FORMULATION =====
//        System.out.println("\n--- SCENARIO 1: Create New Formulation ---");
//        System.out.println("In real application, this would prompt user for input:");
//        System.out.println("  - Item type (Food/Drink)");
//        System.out.println("  - Basic info (name, ID, price, dates)");
//        System.out.println("  - Ingredients (with quantities)");
//        System.out.println("  - Lab conditions");
//        System.out.println("  - Preparation steps");
//        System.out.println("  - Conservation/Consumption conditions");
//        System.out.println("  - Standards");
//
//        // Simulate what happens internally when author.Formulate() is called
//        // (Without actual Keypad input for demonstration)
//        System.out.println("\nSimulating formulation creation...");
//        Food simulatedFood = createSampleFood(author);
//        author.getFormulatedItems().add(simulatedFood);
//        System.out.println("✓ Food formulation created: " + simulatedFood.getName());
//
//        // ===== SCENARIO 2: CONSULT FORMULATIONS =====
//        System.out.println("\n--- SCENARIO 2: Consult Formulations ---");
//        System.out.println("Author can:");
//        System.out.println("  1. View all formulations");
//        System.out.println("  2. Search by name");
//        System.out.println("  3. Search by ingredient");
//        System.out.println("  4. View detailed information");
//
//        System.out.println("\nCurrent formulations by " + author.getName() + ":");
//        for (Item item : author.getFormulatedItems()) {
//            System.out.println("  - " + item.getName() + " (ID: " + item.getItemID() + ")");
//        }
//
//        // In real application: author.consultFormulation() would be called
//        // This provides interactive menu for searching and viewing
//
//        // ===== SCENARIO 3: CHECK FORMULATION ISSUES =====
//        System.out.println("\n--- SCENARIO 3: Check Formulation Issues ---");
//        System.out.println("Author checks formulation for:");
//        System.out.println("  ⚠ Missing ingredients");
//        System.out.println("  ⚠ Missing expiry date");
//        System.out.println("  ⚠ Negative feedback");
//        System.out.println("  ⚠ Veto status");
//        System.out.println("  ⚠ Missing standards");
//        System.out.println("  ⚠ Invalid price");
//
//        System.out.println("\nManual check for sample food:");
//        checkSampleFood(simulatedFood);
//
//        // In real application: author.checkFormulationissues() would be called
//        // This provides interactive issue checking with detailed report
//
//        // ===== STATISTICS =====
//        System.out.println("\n--- Author Statistics ---");
//        System.out.println("Total Formulations: " + author.getFormulatedItems().size());
//        System.out.println("Food Items: " + author.getFoodCount());
//        System.out.println("Drink Items: " + author.getDrinkCount());
//
//        System.out.println("\n" + "=".repeat(50));
//        System.out.println("✓ Author functionality demonstration complete!");
//        System.out.println("=".repeat(50));
//
//        // ===== USAGE FLOW =====
//        System.out.println("\n=== TYPICAL USAGE FLOW ===");
//        System.out.println("\n1. CREATE FORMULATION:");
//        System.out.println("   Author author = new Author(1, \"Chef Name\", ...");
//        System.out.println("   Item newItem = author.Formulate();");
//        System.out.println("   // Interactive prompts guide through creation");
//
//        System.out.println("\n2. CONSULT FORMULATIONS:");
//        System.out.println("   author.consultFormulation();");
//        System.out.println("   // Interactive menu for viewing/searching");
//
//        System.out.println("\n3. CHECK FOR ISSUES:");
//        System.out.println("   author.checkFormulationissues();");
//        System.out.println("   // Analyzes formulation and reports problems");
//
//        System.out.println("\n=== KEY FEATURES ===");
//        System.out.println("✓ Complete ingredient management with quantities");
//        System.out.println("✓ Lab, conservation, and consumption conditions");
//        System.out.println("✓ Step-by-step preparation protocols");
//        System.out.println("✓ Standards compliance tracking");
//        System.out.println("✓ Multi-criteria search (name, ingredient)");
//        System.out.println("✓ Comprehensive issue detection");
//        System.out.println("✓ Feedback monitoring");
//        System.out.println("✓ Veto status checking");
//    }
//
//    /**
//     * Creates a sample food for demonstration
//     */
//    private static Food createSampleFood(Author author) {
//        Food food = new Food(1, "Classic Margherita Pizza");
//        food.setItemID(1001);
//        food.setPrice(12.99);
////        food.setEntryDate("2024-12-05");
//        food.setExpiryDate("2024-12-07");
//        food.setAveragePricePerKg(8.50);
//
//        // Add ingredients
//        Ingredient flour = new Ingredient(1, "Flour",
//                new Quantity(500, 0, 0.5, "grams"));
//        Ingredient tomato = new Ingredient(2, "Tomato Sauce",
//                new Quantity(200, 200, 0.2, "ml"));
//        Ingredient cheese = new Ingredient(3, "Mozzarella",
//                new Quantity(250, 0, 0.25, "grams"));
//
//        food.addIngredient(flour);
//        food.addIngredient(tomato);
//        food.addIngredient(cheese);
//
//        // Add standards
//        food.addStandard("ISO 22000 - Food Safety");
//        food.addStandard("FDA Approved");
//
//        // Add author
//        food.addAuthor(author);
//
//        return food;
//    }
//
//    /**
//     * Manually checks a food item for issues (demonstration)
//     */
//    private static void checkSampleFood(Food food) {
//        int issueCount = 0;
//
//        System.out.println("Checking: " + food.getName());
//
//        // Check ingredients
//        if (food.getIngredients() == null || food.getIngredients().isEmpty()) {
//            System.out.println("  ⚠ No ingredients!");
//            issueCount++;
//        } else {
//            System.out.println("  ✓ Has " + food.getIngredients().size() + " ingredients");
//        }
//
////        // Check expiry date
////        if (food.getExpiryDate() == null || food.getExpiryDate().isEmpty()) {
////            System.out.println("  ⚠ No expiry date!");
////            issueCount++;
////        } else {
////            System.out.println("  ✓ Expiry date set: " + food.getExpiryDate());
////        }
//
//        // Check price
//        if (food.getPrice() <= 0) {
//            System.out.println("  ⚠ Invalid price!");
//            issueCount++;
//        } else {
//            System.out.println("  ✓ Price is valid: $" + food.getPrice());
//        }
//
//        // Check standards
//        if (food.getStandards() == null || food.getStandards().isEmpty()) {
//            System.out.println("  ℹ No standards specified");
//        } else {
//            System.out.println("  ✓ Respects " + food.getStandards().size() + " standards");
//        }
//
//        System.out.println("\nResult: " + (issueCount == 0 ?
//                "✓ No issues found!" :
//                "⚠ Found " + issueCount + " issue(s)"));
//    }
//}
//
///* ============================================================
// * EXPECTED OUTPUT WHEN RUNNING WITH INTERACTIVE INPUT:
// * ============================================================
// *
// * When author.Formulate() is called with Keypad input:
// *
// * === NEW FORMULATION ===
// * Select item type:
// * 1. Food
// * 2. Drink
// * [User enters: 1]
// *
// * --- FOOD FORMULATION ---
// * Enter food name:
// * [User enters: Chocolate Cake]
// * Enter food ID:
// * [User enters: 2001]
// * Enter price:
// * [User enters: 25.99]
// * ...and so on for all fields
// *
// * --- ADD INGREDIENTS ---
// * How many ingredients?
// * [User enters: 3]
// *
// * Ingredient 1:
// * Enter ingredient ID: [User enters: 101]
// * Enter ingredient name: [User enters: Flour]
// * Enter weight (grams): [User enters: 300]
// * ...
// *
// * ✓ Ingredient added: Flour
// * ...continues for all ingredients and conditions
// *
// * Food formulation completed!
// * ✓ Formulation created successfully: Chocolate Cake
// *
// * ============================================================
// */