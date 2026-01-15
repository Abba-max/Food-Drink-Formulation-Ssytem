package MyClasses.Database;

import MyClasses.Conditions.Conservecondition;
import MyClasses.Conditions.Consumpcondition;
import MyClasses.Conditions.Optcondition;
import MyClasses.Conditions.Prepprotocol;
import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Feedback;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Ingredients.Quantity;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;
import MyClasses.Restrictions.Veto;
import MyClasses.Utilities.AuditTrail;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;

/**
 * Database Manager - Handles all database operations
 * Replaces FileManager completely
 */
public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() {
        try {
            this.connection = DatabaseConfig.getConnection();
            System.out.println("✓ DatabaseManager initialized");
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize DatabaseManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    // ADMIN OPERATIONS
    // =====================================================

    /**
     * Save admin to database
     */
    public boolean saveAdmin(Admin admin) {
        String sql = "INSERT INTO admins (admin_id, name, address, contact, date_of_birth, password, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name=?, address=?, contact=?, date_of_birth=?, password=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, admin.getAdminID());
            pstmt.setString(2, admin.getName());
            pstmt.setString(3, admin.getAddress());
            pstmt.setString(4, admin.getContact());
            pstmt.setString(5, admin.getDateofbirth());
            pstmt.setString(6, admin.getPassword());
            pstmt.setString(7, "ADMIN");
            // For UPDATE part
            pstmt.setString(8, admin.getName());
            pstmt.setString(9, admin.getAddress());
            pstmt.setString(10, admin.getContact());
            pstmt.setString(11, admin.getDateofbirth());
            pstmt.setString(12, admin.getPassword());

            pstmt.executeUpdate();
            System.out.println("✓ Admin saved: " + admin.getName());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error saving admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save all admins
     */
    public void saveAdmins(LinkedList<Admin> admins) {
        for (Admin admin : admins) {
            saveAdmin(admin);
        }
    }

    /**
     * Load all admins from database
     */
    public LinkedList<Admin> loadAdmins() {
        LinkedList<Admin> admins = new LinkedList<>();
        String sql = "SELECT * FROM admins";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Admin admin = new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact"),
                        rs.getString("date_of_birth"),
                        rs.getString("password")
                );
                admins.add(admin);
            }
            System.out.println("✓ Loaded " + admins.size() + " admin(s)");
        } catch (SQLException e) {
            System.err.println("❌ Error loading admins: " + e.getMessage());
        }

        return admins;
    }

    // =====================================================
    // AUTHOR OPERATIONS
    // =====================================================

    /**
     * Save author to database
     */
    public boolean saveAuthor(Author author) {
        String sql = "INSERT INTO authors (author_id, name, address, contact, date_of_birth, password, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name=?, address=?, contact=?, date_of_birth=?, password=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, author.getAuthorID());
            pstmt.setString(2, author.getName());
            pstmt.setString(3, author.getAddress());
            pstmt.setString(4, author.getContact());
            pstmt.setString(5, author.getDateofbirth());
            pstmt.setString(6, author.getPassword());
            pstmt.setString(7, "AUTHOR");
            // For UPDATE
            pstmt.setString(8, author.getName());
            pstmt.setString(9, author.getAddress());
            pstmt.setString(10, author.getContact());
            pstmt.setString(11, author.getDateofbirth());
            pstmt.setString(12, author.getPassword());

            pstmt.executeUpdate();

            // Save author's formulations
            saveAuthorFormulations(author);

            System.out.println("✓ Author saved: " + author.getName());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error saving author: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save author's formulations reference
     */
    private void saveAuthorFormulations(Author author) {
        for (Item item : author.getFormulatedItems()) {
            String sql = "INSERT IGNORE INTO item_authors (item_id, author_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.setInt(2, author.getAuthorID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving author-item relationship: " + e.getMessage());
            }
        }
    }

    /**
     * Save all authors
     */
    public void saveAuthors(LinkedList<Author> authors) {
        for (Author author : authors) {
            saveAuthor(author);
        }
    }

    /**
     * Load all authors from database
     */
    public LinkedList<Author> loadAuthors() {
        LinkedList<Author> authors = new LinkedList<>();
        String sql = "SELECT * FROM authors";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Author author = new Author(
                        rs.getInt("author_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact"),
                        rs.getString("date_of_birth")
                );
                author.setPassword(rs.getString("password"));

                // Load author's formulations
                LinkedList<Item> formulations = loadAuthorFormulations(author.getAuthorID());
                for (Item item : formulations) {
                    author.getFormulatedItems().add(item);
                }

                authors.add(author);
            }
            System.out.println("✓ Loaded " + authors.size() + " author(s)");
        } catch (SQLException e) {
            System.err.println("❌ Error loading authors: " + e.getMessage());
        }

        return authors;
    }

    /**
     * Load formulations by author
     */
    private LinkedList<Item> loadAuthorFormulations(int authorId) {
        LinkedList<Item> formulations = new LinkedList<>();
        String sql = "SELECT i.* FROM items i " +
                "JOIN item_authors ia ON i.item_id = ia.item_id " +
                "WHERE ia.author_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Item item = loadItemFromResultSet(rs);
                if (item != null) {
                    formulations.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading author formulations: " + e.getMessage());
        }

        return formulations;
    }

    /**
     * Search formulations by author name (partial, case-insensitive)
     */
    public LinkedList<Item> findFormulationsByAuthorName(String authorNamePattern) {
        LinkedList<Item> results = new LinkedList<>();
        if (authorNamePattern == null || authorNamePattern.trim().isEmpty()) {
            return loadFormulations();
        }

        String sql = "SELECT i.* FROM items i " +
                "JOIN item_authors ia ON i.item_id = ia.item_id " +
                "JOIN authors a ON ia.author_id = a.author_id " +
                "WHERE LOWER(a.name) LIKE LOWER(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + authorNamePattern.trim() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Item item = loadItemFromResultSet(rs);
                if (item != null) results.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error searching by author: " + e.getMessage());
        }

        return results;
    }

    /**
     * Search formulations by ingredient names.
     * If matchAll is true, returns items that contain ALL ingredient names (exact lowercased match);
     * otherwise returns items that contain AT LEAST ONE of the ingredient name patterns.
     */
    public LinkedList<Item> findFormulationsByIngredientNames(java.util.List<String> ingredientNames, boolean matchAll) {
        LinkedList<Item> results = new LinkedList<>();
        if (ingredientNames == null || ingredientNames.isEmpty()) {
            return loadFormulations();
        }

        // normalize
        java.util.List<String> names = new java.util.ArrayList<>();
        for (String n : ingredientNames) {
            if (n != null && !n.trim().isEmpty()) names.add(n.trim().toLowerCase());
        }
        if (names.isEmpty()) return loadFormulations();

        try {
            if (!matchAll) {
                // any match: use LIKE for each provided name and collect distinct items
                String sql = "SELECT DISTINCT i.* FROM items i JOIN ingredients ing ON i.item_id = ing.item_id " +
                        "WHERE LOWER(ing.name) LIKE LOWER(?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    for (String name : names) {
                        pstmt.setString(1, "%" + name + "%");
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            Item item = loadItemFromResultSet(rs);
                            if (item != null && !results.contains(item)) results.add(item);
                        }
                    }
                }
            } else {
                // must contain all searched ingredient names (exact lowercased comparison)
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < names.size(); i++) {
                    if (i > 0) placeholders.append(", ");
                    placeholders.append("?");
                }

                String sql = "SELECT i.* FROM items i " +
                        "JOIN ingredients ing ON i.item_id = ing.item_id " +
                        "WHERE LOWER(ing.name) IN (" + placeholders.toString() + ") " +
                        "GROUP BY i.item_id " +
                        "HAVING COUNT(DISTINCT LOWER(ing.name)) = ?";

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    int idx = 1;
                    for (String name : names) {
                        pstmt.setString(idx++, name);
                    }
                    pstmt.setInt(idx, names.size());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        Item item = loadItemFromResultSet(rs);
                        if (item != null) results.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching by ingredients: " + e.getMessage());
        }

        return results;
    }

    // =====================================================
    // CUSTOMER OPERATIONS
    // =====================================================

    /**
     * Save customer to database
     */
    public boolean saveCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, name, address, contact, date_of_birth, age, password, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name=?, address=?, contact=?, date_of_birth=?, age=?, password=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customer.getCustomerID());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getContact());
            pstmt.setString(5, customer.getDateofbirth());
            pstmt.setInt(6, customer.getAge());
            pstmt.setString(7, customer.getPassword());
            pstmt.setString(8, "CUSTOMER");
            // For UPDATE
            pstmt.setString(9, customer.getName());
            pstmt.setString(10, customer.getAddress());
            pstmt.setString(11, customer.getContact());
            pstmt.setString(12, customer.getDateofbirth());
            pstmt.setInt(13, customer.getAge());
            pstmt.setString(14, customer.getPassword());

            pstmt.executeUpdate();

            // Save purchases
            savePurchases(customer);

            // Save favorites
            saveFavorites(customer);

            // Save feedbacks
            saveFeedbacks(customer);

            System.out.println("✓ Customer saved: " + customer.getName());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error saving customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save customer purchases
     */
    private void savePurchases(Customer customer) {
        String sql = "INSERT INTO purchases (customer_id, item_id, item_name, price, purchase_date, payment_method) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE item_id=item_id";

        for (Customer.PurchaseRecord purchase : customer.getPurchasedItems().values()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, customer.getCustomerID());
                pstmt.setInt(2, purchase.getItemID());
                pstmt.setString(3, purchase.getItemName());
                pstmt.setDouble(4, purchase.getPrice());
                pstmt.setTimestamp(5, new Timestamp(purchase.getPurchaseDate().getTime()));
                pstmt.setString(6, purchase.getPaymentMethod());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving purchase: " + e.getMessage());
            }
        }
    }

    /**
     * Save customer favorites
     */
    private void saveFavorites(Customer customer) {
        String sql = "INSERT IGNORE INTO favorites (customer_id, item_id) VALUES (?, ?)";

        for (Item favorite : customer.getFavoriteFormulations()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, customer.getCustomerID());
                pstmt.setInt(2, favorite.getItemID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving favorite: " + e.getMessage());
            }
        }
    }

    /**
     * Save customer feedbacks
     */
    private void saveFeedbacks(Customer customer) {
        String sql = "INSERT INTO feedbacks (item_id, customer_id, customer_name, comment, is_like) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE comment=?, is_like=?";

        for (Feedback feedback : customer.getFeedbackHistory()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // We need to find the item_id for this feedback
                // This is simplified - you may need to track item_id with feedback
                pstmt.setInt(1, 0); // Placeholder
                pstmt.setInt(2, customer.getCustomerID());
                pstmt.setString(3, feedback.getConsumerName());
                pstmt.setString(4, feedback.getComment());
                pstmt.setBoolean(5, feedback.isLike());
                pstmt.setString(6, feedback.getComment());
                pstmt.setBoolean(7, feedback.isLike());
                // Note: You may need to modify Feedback class to track item_id
            } catch (SQLException e) {
                System.err.println("Error saving feedback: " + e.getMessage());
            }
        }
    }

    /**
     * Save all customers
     */
    public void saveCustomers(LinkedList<Customer> customers) {
        for (Customer customer : customers) {
            saveCustomer(customer);
        }
    }

    /**
     * Load all customers from database
     */
    public LinkedList<Customer> loadCustomers() {
        LinkedList<Customer> customers = new LinkedList<>();
        String sql = "SELECT * FROM customers";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getInt("age")
                );
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setContact(rs.getString("contact"));
                customer.setDateofbirth(rs.getString("date_of_birth"));
                customer.setPassword(rs.getString("password"));

                // Load purchases
                loadCustomerPurchases(customer);

                // Load favorites
                loadCustomerFavorites(customer);

                customers.add(customer);
            }
            System.out.println("✓ Loaded " + customers.size() + " customer(s)");
        } catch (SQLException e) {
            System.err.println("❌ Error loading customers: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Load customer purchases
     */
    private void loadCustomerPurchases(Customer customer) {
        String sql = "SELECT * FROM purchases WHERE customer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customer.getCustomerID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer.PurchaseRecord purchase = new Customer.PurchaseRecord(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getDouble("price"),
                        new Date(rs.getTimestamp("purchase_date").getTime()),
                        rs.getString("payment_method")
                );
                customer.getPurchasedItems().put(purchase.getItemID(), purchase);
            }
        } catch (SQLException e) {
            System.err.println("Error loading purchases: " + e.getMessage());
        }
    }

    /**
     * Load customer favorites
     */
    private void loadCustomerFavorites(Customer customer) {
        String sql = "SELECT i.* FROM items i " +
                "JOIN favorites f ON i.item_id = f.item_id " +
                "WHERE f.customer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customer.getCustomerID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Item item = loadItemFromResultSet(rs);
                if (item != null) {
                    customer.getFavoriteFormulations().add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading favorites: " + e.getMessage());
        }
    }

    // =====================================================
    // ITEM/FORMULATION OPERATIONS
    // =====================================================

    /**
     * Save item (Food or Drink) to database
     */
    public boolean saveItem(Item item) {
        try {
            // Start transaction
            connection.setAutoCommit(false);

            // Save base item data
            saveItemBase(item);

            // Save type-specific data
            if (item instanceof Food) {
                saveFood((Food) item);
            } else if (item instanceof Drink) {
                saveDrink((Drink) item);
            }

            // Save related data
            saveIngredients(item);
            saveLabConditions(item);
            savePreparationProtocol(item);
            saveConservationConditions(item);
            saveConsumptionConditions(item);
            saveStandards(item);
            saveVeto(item);
            saveFeedbacksForItem(item);

            // Commit transaction
            connection.commit();
            connection.setAutoCommit(true);

            System.out.println("✓ Item saved: " + item.getName());
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error saving item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save base item data
     */
    private void saveItemBase(Item item) throws SQLException {
        String sql = "INSERT INTO items (item_id, item_type, name, price, entry_date, expiry_date, " +
                "average_price_per_kg, author_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name=?, price=?, expiry_date=?, average_price_per_kg=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            pstmt.setString(2, item instanceof Food ? "FOOD" : "DRINK");
            pstmt.setString(3, item.getName());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setDate(5, item.getEntry_date() != null ? new java.sql.Date(item.getEntry_date().getTime()) : null);
            pstmt.setString(6, item.getExpiry_date());
            String expiryDate = item.getExpiry_date();
            if (expiryDate != null && !expiryDate.isEmpty()) {
                // Check format and convert if needed
                if (expiryDate.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    // Convert DD-MM-YYYY to YYYY-MM-DD
                    String[] parts = expiryDate.split("-");
                    if (parts.length == 3) {
                        expiryDate = parts[2] + "-" + parts[1] + "-" + parts[0];
                    }
                }
                pstmt.setString(6, expiryDate);
            } else {
                pstmt.setNull(6, java.sql.Types.VARCHAR);
            }
            double avgPrice = 0;
            if (item instanceof Food) {
                avgPrice = ((Food) item).getAveragePricePerKg();
            } else if (item instanceof Drink) {
                avgPrice = ((Drink) item).getAveragePricePerKg();
            }
            pstmt.setDouble(7, avgPrice);

            // FIX: Handle author_id properly
            if (item.getAuthor() != null && item.getAuthor() instanceof Author) {
                Author author = (Author) item.getAuthor();
                // Verify author exists in database
                if (authorExistsInDatabase(author.getAuthorID())) {
                    pstmt.setInt(8, author.getAuthorID());
                } else {
                    pstmt.setNull(8, java.sql.Types.INTEGER);
                }
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }

            // For UPDATE
            pstmt.setString(9, item.getName());
            pstmt.setDouble(10, item.getPrice());
            pstmt.setString(11, item.getExpiry_date());
            pstmt.setDouble(12, avgPrice);

            pstmt.executeUpdate();
        }
    }

    /**
     * Check if author exists in database
     */
    private boolean authorExistsInDatabase(int authorId) {
        String sql = "SELECT COUNT(*) FROM authors WHERE author_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Save Food-specific data
     */
    private void saveFood(Food food) throws SQLException {
        String sql = "INSERT INTO foods (food_id, item_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE item_id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, food.getFoodID());
            pstmt.setInt(2, food.getItemID());
            pstmt.setInt(3, food.getItemID());
            pstmt.executeUpdate();
        }
    }

    /**
     * Save Drink-specific data
     */
    private void saveDrink(Drink drink) throws SQLException {
        String sql = "INSERT INTO drinks (drink_id, item_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE item_id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, drink.getDrinkID());
            pstmt.setInt(2, drink.getItemID());
            pstmt.setInt(3, drink.getItemID());
            pstmt.executeUpdate();
        }
    }

    /**
     * Save ingredients
     */
    private void saveIngredients(Item item) throws SQLException {
        // Delete existing ingredients
        String deleteSql = "DELETE FROM ingredients WHERE item_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setInt(1, item.getItemID());
            pstmt.executeUpdate();
        }

        // Insert new ingredients
        LinkedList<Ingredient> ingredients = null;
        if (item instanceof Food) {
            ingredients = ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            ingredients = ((Drink) item).getIngredients();
        }

        if (ingredients != null) {
            String sql = "INSERT INTO ingredients (item_id, name, weight, volume, fraction, unit) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            for (Ingredient ing : ingredients) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, item.getItemID());
                    pstmt.setString(2, ing.getName());

                    if (ing.getQuantity() != null) {
                        pstmt.setDouble(3, ing.getQuantity().getWeight());
                        pstmt.setDouble(4, ing.getQuantity().getVolume());
                        pstmt.setDouble(5, ing.getQuantity().getFraction());
                        pstmt.setString(6, ing.getQuantity().getUnit());
                    } else {
                        pstmt.setDouble(3, 0);
                        pstmt.setDouble(4, 0);
                        pstmt.setDouble(5, 0);
                        pstmt.setString(6, "");
                    }

                    pstmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Save lab conditions
     */
    private void saveLabConditions(Item item) throws SQLException {
        Optcondition labCond = null;
        if (item instanceof Food) {
            labCond = ((Food) item).getLabCondition();
        } else if (item instanceof Drink) {
            labCond = ((Drink) item).getLabCondition();
        }

        if (labCond != null) {
            String deleteSql = "DELETE FROM lab_conditions WHERE item_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.executeUpdate();
            }

            String sql = "INSERT INTO lab_conditions (item_id, temperature, pressure, moisture, vibration, period) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.setDouble(2, labCond.getTemp());
                pstmt.setDouble(3, labCond.getPressure());
                pstmt.setDouble(4, labCond.getMoisture());
                pstmt.setDouble(5, labCond.getVibration());
                pstmt.setInt(6, labCond.getPeriod());
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Save preparation protocol
     */
    private void savePreparationProtocol(Item item) throws SQLException {
        Prepprotocol protocol = null;
        if (item instanceof Food) {
            protocol = ((Food) item).getPrepprotocol();
        } else if (item instanceof Drink) {
            protocol = ((Drink) item).getPrepprotocol();
        }

        if (protocol != null && !protocol.isEmpty()) {
            // Delete existing protocol
            String deleteSql = "DELETE FROM preparation_protocols WHERE item_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.executeUpdate();
            }

            // Insert steps
            String sql = "INSERT INTO preparation_protocols (item_id, step_number, step_description, " +
                    "step_temp, step_pressure, step_moisture, step_vibration, step_period) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            LinkedList<String> steps = protocol.getSteps();
            LinkedList<Optcondition> stepConditions = protocol.getStepConditions();

            for (int i = 0; i < steps.size(); i++) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, item.getItemID());
                    pstmt.setInt(2, i + 1);
                    pstmt.setString(3, steps.get(i));

                    Optcondition cond = stepConditions.get(i);
                    if (cond != null) {
                        pstmt.setDouble(4, cond.getTemp());
                        pstmt.setDouble(5, cond.getPressure());
                        pstmt.setDouble(6, cond.getMoisture());
                        pstmt.setDouble(7, cond.getVibration());
                        pstmt.setInt(8, cond.getPeriod());
                    } else {
                        pstmt.setDouble(4, 0);
                        pstmt.setDouble(5, 0);
                        pstmt.setDouble(6, 0);
                        pstmt.setDouble(7, 0);
                        pstmt.setInt(8, 0);
                    }

                    pstmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Save conservation conditions
     */
    private void saveConservationConditions(Item item) throws SQLException {
        Conservecondition conserveCond = null;
        if (item instanceof Food) {
            conserveCond = ((Food) item).getConservecondition();
        } else if (item instanceof Drink) {
            conserveCond = ((Drink) item).getConservecondition();
        }

        if (conserveCond != null) {
            String deleteSql = "DELETE FROM conservation_conditions WHERE item_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.executeUpdate();
            }

            String sql = "INSERT INTO conservation_conditions (item_id, temperature, moisture, container) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.setDouble(2, conserveCond.getTemp());
                pstmt.setDouble(3, conserveCond.getMoisture());
                pstmt.setString(4, conserveCond.getContainer());
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Save consumption conditions
     */
    private void saveConsumptionConditions(Item item) throws SQLException {
        Consumpcondition consumpCond = null;
        if (item instanceof Food) {
            consumpCond = ((Food) item).getConsumpcondition();
        } else if (item instanceof Drink) {
            consumpCond = ((Drink) item).getConsumpcondition();
        }

        if (consumpCond != null) {
            String deleteSql = "DELETE FROM consumption_conditions WHERE item_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.executeUpdate();
            }

            String sql = "INSERT INTO consumption_conditions (item_id, temperature, moisture) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.setDouble(2, consumpCond.getTemperature());
                pstmt.setDouble(3, consumpCond.getMoisture());
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Save standards
     */
    private void saveStandards(Item item) throws SQLException {
        LinkedList<String> standards = null;
        if (item instanceof Food) {
            standards = ((Food) item).getStandards();
        } else if (item instanceof Drink) {
            standards = ((Drink) item).getStandards();
        }

        if (standards != null && !standards.isEmpty()) {
            // Delete existing
            String deleteSql = "DELETE FROM standards WHERE item_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.executeUpdate();
            }

            // Insert new
            String sql = "INSERT INTO standards (item_id, standard_text) VALUES (?, ?)";
            for (String standard : standards) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, item.getItemID());
                    pstmt.setString(2, standard);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Save veto information
     */
    private void saveVeto(Item item) throws SQLException {
        Veto veto = null;
        if (item instanceof Food) {
            veto = ((Food) item).getVeto();
        } else if (item instanceof Drink) {
            veto = ((Drink) item).getVeto();
        }

        if (veto != null) {
            String deleteSql = "DELETE FROM vetos WHERE item_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.executeUpdate();
            }

            String sql = "INSERT INTO vetos (item_id, is_vetoed, reason, veto_date, initiator_type) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, item.getItemID());
                pstmt.setBoolean(2, veto.isVetoed);
                pstmt.setString(3, veto.reason);
                pstmt.setDate(4, veto.date != null ? new java.sql.Date(veto.date.getTime()) : null);
                pstmt.setString(5, "ADMIN"); // Default
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Save feedbacks for item
     */
    private void saveFeedbacksForItem(Item item) throws SQLException {
        LinkedList<Feedback> feedbacks = null;
        if (item instanceof Food) {
            feedbacks = ((Food) item).getFeedbacks();
        } else if (item instanceof Drink) {
            feedbacks = ((Drink) item).getFeedbacks();
        }

        if (feedbacks != null && !feedbacks.isEmpty()) {
            String sql = "INSERT INTO feedbacks (item_id, customer_name, comment, is_like, timestamp) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE comment=?, is_like=?";

            for (Feedback feedback : feedbacks) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, item.getItemID());
                    pstmt.setString(2, feedback.getConsumerName());
                    pstmt.setString(3, feedback.getComment());
                    pstmt.setBoolean(4, feedback.isLike());
                    pstmt.setTimestamp(5, new Timestamp(feedback.getTimestamp().getTime()));
                    pstmt.setString(6, feedback.getComment());
                    pstmt.setBoolean(7, feedback.isLike());
                    pstmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Save all formulations
     */
    public void saveFormulations(LinkedList<Item> formulations) {
        for (Item item : formulations) {
            saveItem(item);
        }
    }

    /**
     * Load all formulations from database
     */
    public LinkedList<Item> loadFormulations() {
        LinkedList<Item> formulations = new LinkedList<>();
        String sql = "SELECT * FROM items";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = loadItemFromResultSet(rs);
                if (item != null) {
                    formulations.add(item);
                }
            }
            System.out.println("✓ Loaded " + formulations.size() + " formulation(s)");
        } catch (SQLException e) {
            System.err.println("❌ Error loading formulations: " + e.getMessage());
        }

        return formulations;
    }

    /**
     * Load item from ResultSet
     */
    private Item loadItemFromResultSet(ResultSet rs) throws SQLException {
        String itemType = rs.getString("item_type");
        int itemId = rs.getInt("item_id");

        Item item = null;

        if ("FOOD".equals(itemType)) {
            Food food = new Food();
            food.setFoodID(itemId);
            food.setItemID(itemId);
            item = food;
        } else if ("DRINK".equals(itemType)) {
            Drink drink = new Drink();
            drink.setDrinkID(itemId);
            drink.setItemID(itemId);
            item = drink;
        }

        if (item != null) {
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
            item.setExpiryDate(rs.getString("expiry_date"));

            if (item instanceof Food) {
                ((Food) item).setAveragePricePerKg(rs.getDouble("average_price_per_kg"));
            } else if (item instanceof Drink) {
                ((Drink) item).setAveragePricePerKg(rs.getDouble("average_price_per_kg"));
            }

            // Load related data
            loadItemDetails(item);
        }

        return item;
    }

    /**
     * Load all item details
     */
    private void loadItemDetails(Item item) {
        try {
            loadItemIngredients(item);
            loadItemLabConditions(item);
            loadItemPreparationProtocol(item);
            loadItemConservationConditions(item);
            loadItemConsumptionConditions(item);
            loadItemStandards(item);
            loadItemVeto(item);
            loadItemFeedbacks(item);
        } catch (SQLException e) {
            System.err.println("Error loading item details: " + e.getMessage());
        }
    }

    /**
     * Load ingredients for item
     */
    private void loadItemIngredients(Item item) throws SQLException {
        String sql = "SELECT * FROM ingredients WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Quantity quantity = new Quantity(
                        rs.getDouble("weight"),
                        rs.getDouble("volume"),
                        rs.getDouble("fraction"),
                        rs.getString("unit")
                );

                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("name"),
                        quantity
                );

                if (item instanceof Food) {
                    ((Food) item).addIngredient(ingredient);
                } else if (item instanceof Drink) {
                    ((Drink) item).addIngredient(ingredient);
                }
            }
        }
    }

    /**
     * Load lab conditions for item
     */
    private void loadItemLabConditions(Item item) throws SQLException {
        String sql = "SELECT * FROM lab_conditions WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Optcondition labCond = new Optcondition(
                        rs.getDouble("temperature"),
                        rs.getDouble("pressure"),
                        rs.getDouble("moisture"),
                        rs.getDouble("vibration"),
                        rs.getInt("period")
                );

                if (item instanceof Food) {
                    ((Food) item).setLabCondition(labCond);
                } else if (item instanceof Drink) {
                    ((Drink) item).setLabCondition(labCond);
                }
            }
        }
    }

    /**
     * Load preparation protocol for item
     */
    private void loadItemPreparationProtocol(Item item) throws SQLException {
        String sql = "SELECT * FROM preparation_protocols WHERE item_id = ? ORDER BY step_number";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            Prepprotocol protocol = new Prepprotocol();

            while (rs.next()) {
                String stepDesc = rs.getString("step_description");

                Optcondition stepCond = new Optcondition(
                        rs.getDouble("step_temp"),
                        rs.getDouble("step_pressure"),
                        rs.getDouble("step_moisture"),
                        rs.getDouble("step_vibration"),
                        rs.getInt("step_period")
                );

                protocol.addStep(stepDesc, stepCond);
            }

            if (!protocol.isEmpty()) {
                if (item instanceof Food) {
                    ((Food) item).setPrepprotocol(protocol);
                } else if (item instanceof Drink) {
                    ((Drink) item).setPrepprotocol(protocol);
                }
            }
        }
    }

    /**
     * Load conservation conditions for item
     */
    private void loadItemConservationConditions(Item item) throws SQLException {
        String sql = "SELECT * FROM conservation_conditions WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Conservecondition conserveCond = new Conservecondition(
                        rs.getDouble("temperature"),
                        rs.getDouble("moisture"),
                        rs.getString("container")
                );

                if (item instanceof Food) {
                    ((Food) item).setConservecondition(conserveCond);
                } else if (item instanceof Drink) {
                    ((Drink) item).setConservecondition(conserveCond);
                }
            }
        }
    }

    /**
     * Load consumption conditions for item
     */
    private void loadItemConsumptionConditions(Item item) throws SQLException {
        String sql = "SELECT * FROM consumption_conditions WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Consumpcondition consumpCond = new Consumpcondition(
                        rs.getDouble("temperature"),
                        rs.getDouble("moisture")
                );

                if (item instanceof Food) {
                    ((Food) item).setConsumpcondition(consumpCond);
                } else if (item instanceof Drink) {
                    ((Drink) item).setConsumpcondition(consumpCond);
                }
            }
        }
    }

    /**
     * Load standards for item
     */
    private void loadItemStandards(Item item) throws SQLException {
        String sql = "SELECT * FROM standards WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String standard = rs.getString("standard_text");

                if (item instanceof Food) {
                    ((Food) item).addStandard(standard);
                } else if (item instanceof Drink) {
                    ((Drink) item).addStandard(standard);
                }
            }
        }
    }

    /**
     * Load veto for item
     */
    private void loadItemVeto(Item item) throws SQLException {
        String sql = "SELECT * FROM vetos WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Veto veto = new Veto(
                        rs.getBoolean("is_vetoed"),
                        rs.getString("reason"),
                        new Date(rs.getDate("veto_date").getTime()),
                        null // Initiator reference
                );

                if (item instanceof Food) {
                    ((Food) item).setVeto(veto);
                } else if (item instanceof Drink) {
                    ((Drink) item).setVeto(veto);
                }
            }
        }
    }

    /**
     * Load feedbacks for item
     */
    private void loadItemFeedbacks(Item item) throws SQLException {
        String sql = "SELECT * FROM feedbacks WHERE item_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getItemID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getString("comment"),
                        rs.getBoolean("is_like"),
                        rs.getString("customer_name")
                );
                feedback.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));

                if (item instanceof Food) {
                    ((Food) item).addFeedback(feedback);
                } else if (item instanceof Drink) {
                    ((Drink) item).addFeedback(feedback);
                }
            }
        }
    }

    // =====================================================
    // AUDIT TRAIL OPERATIONS
    // =====================================================

    /**
     * Save audit trail
     */
    public void saveAuditTrail(AuditTrail auditTrail) {
        String sql = "INSERT INTO audit_trail (timestamp, user_type, user_name, action) VALUES (?, ?, ?, ?)";

        try {
            // Clear existing records (optional - or append only new ones)
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM audit_trail");

            // Insert all records
            for (String record : auditTrail.records) {
                // Parse record: [timestamp] user: action
                // Format: [2025-12-14 10:30:45] ADMIN:John Doe: Logged in

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    // Simple parsing (you may need to improve this)
                    String[] parts = record.split("] ", 2);
                    String timestampStr = parts[0].substring(1); // Remove '['

                    String[] userAction = parts[1].split(": ", 2);
                    String userPart = userAction[0];
                    String action = userAction.length > 1 ? userAction[1] : "";

                    String[] userTypeName = userPart.split(":", 2);
                    String userType = userTypeName[0];
                    String userName = userTypeName.length > 1 ? userTypeName[1] : "";

                    pstmt.setTimestamp(1, Timestamp.valueOf(timestampStr));
                    pstmt.setString(2, userType);
                    pstmt.setString(3, userName);
                    pstmt.setString(4, action);
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    // Skip malformed records
                    System.err.println("Skipping malformed audit record: " + record);
                }
            }

            System.out.println("✓ Audit trail saved (" + auditTrail.records.size() + " records)");
        } catch (SQLException e) {
            System.err.println("❌ Error saving audit trail: " + e.getMessage());
        }
    }

    /**
     * Load audit trail
     */
    public AuditTrail loadAuditTrail() {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.records.clear(); // Clear default initialization

        String sql = "SELECT * FROM audit_trail ORDER BY timestamp";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Reconstruct record in original format
                String timestamp = rs.getTimestamp("timestamp").toString();
                String userType = rs.getString("user_type");
                String userName = rs.getString("user_name");
                String action = rs.getString("action");

                String record = String.format("[%s] %s:%s: %s",
                        timestamp, userType, userName, action);

                auditTrail.records.add(record);
            }

            System.out.println("✓ Audit trail loaded (" + auditTrail.records.size() + " records)");
        } catch (SQLException e) {
            System.err.println("❌ Error loading audit trail: " + e.getMessage());
        }

        return auditTrail;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Test database connection
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Get connection for external use
     */
    public Connection getConnection() {
        return connection;
    }
}