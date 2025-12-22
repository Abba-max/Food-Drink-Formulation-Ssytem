package gui.components;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Database.DatabaseManager;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;
import MyClasses.Utilities.AuditTrail;

import java.util.Date;
import java.util.LinkedList;

/**
 * Centralized data management for the application
 */
public class DataManager {
    private DatabaseManager databaseManager;
    private AuditTrail auditTrail;

    // Data structures
    private LinkedList<Admin> admins;
    private LinkedList<Author> authors;
    private LinkedList<Customer> customers;
    private LinkedList<Item> allFormulations;

    public DataManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.auditTrail = new AuditTrail();
        this.admins = new LinkedList<>();
        this.authors = new LinkedList<>();
        this.customers = new LinkedList<>();
        this.allFormulations = new LinkedList<>();
    }

    // ============ LOAD DATA METHODS ============

    public void loadAllData() {
        loadAdmins();
        loadAuthors();
        loadCustomers();
        loadFormulations();
        loadAuditTrail();

        auditTrail.logAction("SYSTEM", "Data loaded from database at " + new Date());
    }

    private void loadAdmins() {
        LinkedList<Admin> loadedAdmins = databaseManager.loadAdmins();
        if (loadedAdmins != null && !loadedAdmins.isEmpty()) {
            admins = loadedAdmins;
        } else {
            // Create default admin
            Admin defaultAdmin = new Admin(1, "System Admin", "HQ", "+1-000-0000", "1980-01-01", "admin123");
            admins.add(defaultAdmin);
            databaseManager.saveAdmin(defaultAdmin);
            auditTrail.logAction("SYSTEM", "Default admin account created");
        }
    }

    private void loadAuthors() {
        LinkedList<Author> loadedAuthors = databaseManager.loadAuthors();
        if (loadedAuthors != null && !loadedAuthors.isEmpty()) {
            authors = loadedAuthors;
        }
    }

    private void loadCustomers() {
        LinkedList<Customer> loadedCustomers = databaseManager.loadCustomers();
        if (loadedCustomers != null && !loadedCustomers.isEmpty()) {
            customers = loadedCustomers;
        }
    }

    private void loadFormulations() {
        LinkedList<Item> loadedFormulations = databaseManager.loadFormulations();
        if (loadedFormulations != null && !loadedFormulations.isEmpty()) {
            allFormulations = loadedFormulations;
        }
    }

    private void loadAuditTrail() {
        AuditTrail loadedAudit = databaseManager.loadAuditTrail();
        if (loadedAudit != null) {
            auditTrail = loadedAudit;
        }
    }

    // ============ SAVE DATA METHODS ============

    public boolean saveAllData() {
        try {
            databaseManager.saveAdmins(admins);
            databaseManager.saveAuthors(authors);
            databaseManager.saveCustomers(customers);
            databaseManager.saveFormulations(allFormulations);
            databaseManager.saveAuditTrail(auditTrail);

            auditTrail.logAction("SYSTEM", "All data saved to database at " + new Date());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============ FORMULATION MANAGEMENT ============

    public void addFormulation(Item formulation) {
        allFormulations.add(formulation);
        databaseManager.saveItem(formulation);
    }

    public void updateFormulation(Item formulation) {
        databaseManager.saveItem(formulation);
    }

    public void removeFormulation(Item formulation) {
        allFormulations.remove(formulation);
        // Note: You might want to add a method to delete from database
    }

    public LinkedList<Item> getFormulationsForCustomer(Customer customer) {
        LinkedList<Item> availableItems = new LinkedList<>();
        for (Item item : allFormulations) {
            if (!isItemVetoed(item)) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    public LinkedList<Item> getFormulationsForAuthor(Author author) {
        LinkedList<Item> authorFormulations = new LinkedList<>();
        for (Item item : allFormulations) {
            // Assuming you have a way to check if author created this item
            // This is a placeholder - you need to implement based on your data structure
            authorFormulations.add(item);
        }
        return authorFormulations;
    }

    private boolean isItemVetoed(Item item) {
        if (item instanceof Food) {
            return ((Food) item).isVetoed();
        } else if (item instanceof Drink) {
            return ((Drink) item).isVetoed();
        }
        return false;
    }

    // ============ USER MANAGEMENT ============

    public boolean addAdmin(Admin admin) {
        // Check if ID exists
        for (Admin a : admins) {
            if (a.getAdminID() == admin.getAdminID()) {
                return false;
            }
        }
        admins.add(admin);
        databaseManager.saveAdmin(admin);
        return true;
    }

    public boolean addAuthor(Author author) {
        // Check if ID exists
        for (Author a : authors) {
            if (a.getAuthorID() == author.getAuthorID()) {
                return false;
            }
        }
        authors.add(author);
        databaseManager.saveAuthor(author);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        // Check if ID exists
        for (Customer c : customers) {
            if (c.getCustomerID() == customer.getCustomerID()) {
                return false;
            }
        }
        customers.add(customer);
        databaseManager.saveCustomer(customer);
        return true;
    }

    public Admin authenticateAdmin(String name, String password) {
        for (Admin admin : admins) {
            if (admin.getName().equals(name) && admin.getPassword().equals(password)) {
                return admin;
            }
        }
        return null;
    }

    public Author authenticateAuthor(String name, String password) {
        for (Author author : authors) {
            if (author.getName().equals(name) && author.getPassword().equals(password)) {
                return author;
            }
        }
        return null;
    }

    public Customer authenticateCustomer(String name, String password) {
        for (Customer customer : customers) {
            if (customer.getName().equals(name) && customer.getPassword().equals(password)) {
                return customer;
            }
        }
        return null;
    }

    // ============ GETTERS ============

    public LinkedList<Admin> getAdmins() {
        return new LinkedList<>(admins);
    }

    public LinkedList<Author> getAuthors() {
        return new LinkedList<>(authors);
    }

    public LinkedList<Customer> getCustomers() {
        return new LinkedList<>(customers);
    }

    public LinkedList<Item> getAllFormulations() {
        return new LinkedList<>(allFormulations);
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public int getTotalFormulations() {
        return allFormulations.size();
    }

    public int getTotalUsers() {
        return admins.size() + authors.size() + customers.size();
    }

    public int getVetoedFormulationsCount() {
        int count = 0;
        for (Item item : allFormulations) {
            if (isItemVetoed(item)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalPurchases() {
        int total = 0;
        for (Customer customer : customers) {
            total += customer.getPurchasedItems().size();
        }
        return total;
    }
}