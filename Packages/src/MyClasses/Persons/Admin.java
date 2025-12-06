package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Formulation;
import MyClasses.Ingredient;
import MyClasses.Keyboard.Keypad;
import MyClasses.Keyboard.Screen;
import MyClasses.Role;
import MyClasses.Veto;
import MyClasses.Feedback;

import java.util.LinkedList;
import java.util.Date;

/**
 * Admin class - highest access level
 * Can:
 * - Create Author and Admin accounts
 * - View and manage all formulations
 * - Check formulation issues
 * - Set veto on formulations
 * - Manage system
 */
public class Admin extends Person implements Formulation {
    private int adminID;

    // System references
    private LinkedList<Author> authors;
    private LinkedList<Admin> admins;
    private LinkedList<Customer> customers;
    private LinkedList<Item> allFormulations;

    private Keypad pad = new Keypad();
    private Screen screen = new Screen();

    public Admin() {
        super();
        this.authors = new LinkedList<>();
        this.admins = new LinkedList<>();
        this.customers = new LinkedList<>();
        this.allFormulations = new LinkedList<>();
    }

    public Admin(int adminID, String name, String address, String contact, String dob, String password) {
        super(name, address, contact, dob, password, Role.ADMIN);
        this.adminID = adminID;
        this.authors = new LinkedList<>();
        this.admins = new LinkedList<>();
        this.customers = new LinkedList<>();
        this.allFormulations = new LinkedList<>();
    }

    // ============ ACCOUNT MANAGEMENT ============

    /**
     * Create a new Author account
     */
    public Author createAuthorAccount() {
        screen.display("\n=== CREATE AUTHOR ACCOUNT ===");

        screen.display("Enter Author ID:");
        int authorID = pad.getInt();

        // Check if ID already exists
        for (Author a : authors) {
            if (a.getAuthorID() == authorID) {
                screen.display("⚠ Author ID already exists!");
                return null;
            }
        }

        screen.display("Enter name:");
        String name = pad.getString();

        screen.display("Enter address:");
        String address = pad.getString();

        screen.display("Enter contact:");
        String contact = pad.getString();

        screen.display("Enter date of birth (YYYY-MM-DD):");
        String dob = pad.getString();

        screen.display("Enter password:");
        String password = pad.getString();

        Author author = new Author(authorID, name, address, contact, dob);
        author.setPassword(password);

        authors.add(author);

        screen.display("\n✓ Author account created successfully!");
        screen.display("Author ID: " + authorID);
        screen.display("Name: " + name);
        screen.display("Login credentials have been set.");

        return author;
    }

    /**
     * Create a new Admin account
     */
    public Admin createAdminAccount() {
        screen.display("\n=== CREATE ADMIN ACCOUNT ===");
        screen.display("⚠ WARNING: Creating an administrator account");
        screen.display("Admins have full system access.");
        screen.display("\nContinue? (1=Yes, 0=No)");

        int confirm = pad.getInt();
        if (confirm != 1) {
            screen.display("Admin creation cancelled.");
            return null;
        }

        screen.display("Enter Admin ID:");
        int adminID = pad.getInt();

        // Check if ID already exists
        for (Admin a : admins) {
            if (a.getAdminID() == adminID) {
                screen.display("⚠ Admin ID already exists!");
                return null;
            }
        }

        screen.display("Enter name:");
        String name = pad.getString();

        screen.display("Enter address:");
        String address = pad.getString();

        screen.display("Enter contact:");
        String contact = pad.getString();

        screen.display("Enter date of birth (YYYY-MM-DD):");
        String dob = pad.getString();

        screen.display("Enter password:");
        String password = pad.getString();

        Admin admin = new Admin(adminID, name, address, contact, dob, password);
        admins.add(admin);

        screen.display("\n✓ Admin account created successfully!");
        screen.display("Admin ID: " + adminID);
        screen.display("Name: " + name);
        screen.display("Access Level: ADMINISTRATOR");

        return admin;
    }

    /**
     * View all system accounts
     */
    public void viewAllAccounts() {
        screen.display("\n=== SYSTEM ACCOUNTS ===");

        screen.display("\n--- ADMINISTRATORS (" + admins.size() + ") ---");
        for (Admin admin : admins) {
            screen.display("  ID: " + admin.getAdminID() + " - " + admin.getName());
        }

        screen.display("\n--- AUTHORS (" + authors.size() + ") ---");
        for (Author author : authors) {
            screen.display("  ID: " + author.getAuthorID() + " - " + author.getName() +
                    " (Formulations: " + author.getFormulatedItems().size() + ")");
        }

        screen.display("\n--- CUSTOMERS (" + customers.size() + ") ---");
        for (Customer customer : customers) {
            screen.display("  ID: " + customer.getCustomerID() + " - " + customer.getName() +
                    " (Purchases: " + customer.getPurchasedItems().size() + ")");
        }
    }

    // ============ FORMULATION MANAGEMENT ============

    @Override
    public Item Formulate() {
        screen.display("Admins typically don't create formulations.");
        screen.display("Authors are responsible for creating formulations.");
        screen.display("However, you can manage and review all formulations.");
        return null;
    }

    /**
     * Consult ALL formulations in the system
     */
    @Override
    public void consultFormulation() {
        screen.display("\n=== CONSULT ALL FORMULATIONS (ADMIN) ===");

        if (allFormulations.isEmpty()) {
            screen.display("No formulations in the system.");
            return;
        }

        screen.display("Total formulations: " + allFormulations.size());
        screen.display("\nSelect option:");
        screen.display("1. View all formulations");
        screen.display("2. Search by name");
        screen.display("3. Search by author");
        screen.display("4. View formulation details");
        screen.display("5. View vetoed formulations");
        screen.display("0. Return");

        int choice = pad.getInt();

        switch (choice) {
            case 1:
                viewAllFormulations();
                break;
            case 2:
                searchByName();
                break;
            case 3:
                searchByAuthor();
                break;
            case 4:
                viewFormulationDetails();
                break;
            case 5:
                viewVetoedFormulations();
                break;
        }
    }

    /**
     * Check formulation issues (ADMIN ACCESS)
     */
    @Override
    public void checkFormulationissues() {
        screen.display("\n=== CHECK FORMULATION ISSUES (ADMIN) ===");

        if (allFormulations.isEmpty()) {
            screen.display("No formulations to check.");
            return;
        }

        screen.display("Enter formulation ID to check:");
        int id = pad.getInt();

        Item item = findFormulationById(id);

        if (item == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        screen.display("\n--- ISSUE REPORT FOR: " + item.getName() + " ---");
        screen.display("Type: " + (item instanceof Food ? "Food" : "Drink"));

        int issueCount = 0;

        // Check 1: Missing ingredients
        LinkedList<Ingredient> ingredients = getIngredients(item);
        if (ingredients == null || ingredients.isEmpty()) {
            screen.display("⚠ WARNING: No ingredients specified!");
            issueCount++;
        } else {
            screen.display("✓ Has " + ingredients.size() + " ingredient(s)");
        }

        // Check 2: Check for negative feedbacks
        LinkedList<Feedback> feedbacks = getFeedbacks(item);
        int negativeFeedbackCount = 0;

        if (feedbacks != null) {
            for (Feedback fb : feedbacks) {
                if (!fb.isLike()) {
                    negativeFeedbackCount++;
                }
            }

            if (negativeFeedbackCount > 0) {
                screen.display("⚠ WARNING: " + negativeFeedbackCount + " negative feedback(s)");
                issueCount++;

                for (Feedback fb : feedbacks) {
                    if (!fb.isLike()) {
                        screen.display("  - " + fb.getConsumerName() + ": " + fb.getComment());
                    }
                }
            } else if (feedbacks.size() > 0) {
                screen.display("✓ All feedbacks are positive (" + feedbacks.size() + " total)");
            }
        }

        // Check 3: Veto status
        boolean isVetoed = isItemVetoed(item);
        if (isVetoed) {
            screen.display("⚠ CRITICAL: Formulation is VETOED!");
            issueCount++;
        }

        // Check 4: Missing standards
        LinkedList<String> standards = getStandards(item);
        if (standards == null || standards.isEmpty()) {
            screen.display("⚠ INFO: No standards specified");
        } else {
            screen.display("✓ Respects " + standards.size() + " standard(s)");
        }

        // Check 5: Price validation
        if (item.getPrice() <= 0) {
            screen.display("⚠ WARNING: Invalid price!");
            issueCount++;
        } else {
            screen.display("✓ Price is valid: $" + item.getPrice());
        }

        // Summary
        screen.display("\n--- ISSUE SUMMARY ---");
        if (issueCount == 0) {
            screen.display("✓ No critical issues found!");
        } else {
            screen.display("Total issues found: " + issueCount);
            screen.display("\nAdmin actions available:");
            screen.display("1. Set veto on this formulation");
            screen.display("2. Contact author");
            screen.display("0. Return");

            int action = pad.getInt();
            if (action == 1) {
                setVetoOnFormulation(item);
            }
        }
    }

    /**
     * Set veto on a formulation (ADMIN ONLY)
     */
    public void setVetoOnFormulation(Item item) {
        screen.display("\n=== SET VETO ===");
        screen.display("Formulation: " + item.getName());

        screen.display("Enter veto reason:");
        String reason = pad.getString();

        screen.display("Confirm veto? (1=Yes, 0=No)");
        int confirm = pad.getInt();

        if (confirm == 1) {
            Veto veto = new Veto(true, reason, new Date(), this);

            if (item instanceof Food) {
                ((Food) item).setVeto(veto);
            } else if (item instanceof Drink) {
                ((Drink) item).setVeto(veto);
            }

            screen.display("\n✓ Veto set successfully!");
            screen.display("Reason: " + reason);
            screen.display("This formulation is now blocked from customer access.");
        } else {
            screen.display("Veto cancelled.");
        }
    }

    // ============ VIEW METHODS ============

    private void viewAllFormulations() {
        screen.display("\n--- ALL FORMULATIONS ---");

        int count = 1;
        for (Item item : allFormulations) {
            screen.display("\n" + count + ". " + item.getName());
            screen.display("   Type: " + (item instanceof Food ? "Food" : "Drink"));
            screen.display("   ID: " + item.getItemID());
            screen.display("   Price: $" + item.getPrice());
            screen.display("   Vetoed: " + (isItemVetoed(item) ? "YES" : "NO"));

            // Show author
            if (item instanceof Food) {
                LinkedList<Author> itemAuthors = ((Food) item).getAuthors();
                if (itemAuthors != null && !itemAuthors.isEmpty()) {
                    screen.display("   Author: " + itemAuthors.get(0).getName());
                }
            } else if (item instanceof Drink) {
                LinkedList<Author> itemAuthors = ((Drink) item).getAuthors();
                if (itemAuthors != null && !itemAuthors.isEmpty()) {
                    screen.display("   Author: " + itemAuthors.get(0).getName());
                }
            }

            count++;
        }
    }

    private void searchByName() {
        screen.display("\nEnter search term:");
        String searchTerm = pad.getString().toLowerCase();

        screen.display("\n--- SEARCH RESULTS ---");
        boolean found = false;

        for (Item item : allFormulations) {
            if (item.getName() != null &&
                    item.getName().toLowerCase().contains(searchTerm)) {
                screen.display("\n✓ Found: " + item.getName());
                screen.display("  Type: " + (item instanceof Food ? "Food" : "Drink"));
                screen.display("  ID: " + item.getItemID());
                screen.display("  Vetoed: " + (isItemVetoed(item) ? "YES" : "NO"));
                found = true;
            }
        }

        if (!found) {
            screen.display("No formulations found matching '" + searchTerm + "'");
        }
    }

    private void searchByAuthor() {
        screen.display("\nEnter author name:");
        String authorName = pad.getString().toLowerCase();

        screen.display("\n--- FORMULATIONS BY: " + authorName + " ---");
        boolean found = false;

        for (Item item : allFormulations) {
            LinkedList<Author> itemAuthors = null;

            if (item instanceof Food) {
                itemAuthors = ((Food) item).getAuthors();
            } else if (item instanceof Drink) {
                itemAuthors = ((Drink) item).getAuthors();
            }

            if (itemAuthors != null) {
                for (Author author : itemAuthors) {
                    if (author.getName() != null &&
                            author.getName().toLowerCase().contains(authorName)) {
                        screen.display("\n✓ " + item.getName());
                        screen.display("  ID: " + item.getItemID());
                        screen.display("  Author: " + author.getName());
                        found = true;
                        break;
                    }
                }
            }
        }

        if (!found) {
            screen.display("No formulations found by author '" + authorName + "'");
        }
    }

    private void viewFormulationDetails() {
        screen.display("\nEnter formulation ID:");
        int id = pad.getInt();

        Item item = findFormulationById(id);

        if (item == null) {
            screen.display("Formulation not found with ID: " + id);
            return;
        }

        screen.display("\n=== FORMULATION DETAILS (ADMIN VIEW) ===");
        screen.display("Name: " + item.getName());
        screen.display("Type: " + (item instanceof Food ? "Food" : "Drink"));
        screen.display("ID: " + item.getItemID());
        screen.display("Price: $" + item.getPrice());
        screen.display("Vetoed: " + (isItemVetoed(item) ? "YES" : "NO"));

        // Show all details (admin has full access)
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

        LinkedList<Feedback> feedbacks = getFeedbacks(item);
        if (feedbacks != null && !feedbacks.isEmpty()) {
            screen.display("\n--- FEEDBACK (" + feedbacks.size() + " total) ---");
            for (Feedback fb : feedbacks) {
                screen.display("  " + (fb.isLike() ? "👍" : "👎") + " " +
                        fb.getConsumerName() + ": " + fb.getComment());
            }
        }
    }

    private void viewVetoedFormulations() {
        screen.display("\n--- VETOED FORMULATIONS ---");

        boolean found = false;
        for (Item item : allFormulations) {
            if (isItemVetoed(item)) {
                screen.display("\n⚠ " + item.getName());
                screen.display("  ID: " + item.getItemID());

                Veto veto = null;
                if (item instanceof Food) {
                    veto = ((Food) item).getVeto();
                } else if (item instanceof Drink) {
                    veto = ((Drink) item).getVeto();
                }

                if (veto != null) {
                    screen.display("  Reason: " + veto.reason);
                    screen.display("  Date: " + veto.date);
                }

                found = true;
            }
        }

        if (!found) {
            screen.display("No vetoed formulations found.");
        }
    }

    // ============ HELPER METHODS ============

    private Item findFormulationById(int id) {
        for (Item item : allFormulations) {
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

    private LinkedList<Feedback> getFeedbacks(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getFeedbacks();
        } else if (item instanceof Drink) {
            return ((Drink) item).getFeedbacks();
        }
        return null;
    }

    private LinkedList<String> getStandards(Item item) {
        if (item instanceof Food) {
            return ((Food) item).getStandards();
        } else if (item instanceof Drink) {
            return ((Drink) item).getStandards();
        }
        return null;
    }

    private boolean isItemVetoed(Item item) {
        if (item instanceof Food) {
            return ((Food) item).isVetoed();
        } else if (item instanceof Drink) {
            return ((Drink) item).isVetoed();
        }
        return false;
    }

    // ============ SETTERS FOR SYSTEM REFERENCES ============

    public void setAuthors(LinkedList<Author> authors) {
        this.authors = authors;
    }

    public void setAdmins(LinkedList<Admin> admins) {
        this.admins = admins;
    }

    public void setCustomers(LinkedList<Customer> customers) {
        this.customers = customers;
    }

    public void setAllFormulations(LinkedList<Item> allFormulations) {
        this.allFormulations = allFormulations;
    }

    // ============ GETTERS ============

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public LinkedList<Author> getAuthors() {
        return authors;
    }

    public LinkedList<Admin> getAdmins() {
        return admins;
    }

    public LinkedList<Customer> getCustomers() {
        return customers;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminID=" + adminID +
                ", name='" + getName() + '\'' +
                ", managingAuthors=" + authors.size() +
                ", managingFormulations=" + allFormulations.size() +
                '}';
    }
}