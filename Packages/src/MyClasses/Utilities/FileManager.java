package MyClasses.Utilities;

import MyClasses.Consumables.Item;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Drink;
import MyClasses.Ingredients.Ingredient;
import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Persons.Customer;

import java.io.*;
import java.util.LinkedList;


public class FileManager {

    // File paths
    private static final String DATA_DIR = "data/";
    private static final String ADMINS_FILE = DATA_DIR + "admins.txt";
    private static final String AUTHORS_FILE = DATA_DIR + "authors.txt";
    private static final String CUSTOMERS_FILE = DATA_DIR + "customers.txt";
    private static final String FORMULATIONS_FILE = DATA_DIR + "formulations.txt";
    private static final String AUDIT_FILE = DATA_DIR + "audit_trail.txt";

    public FileManager() {
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("✓ Data directory created: " + DATA_DIR);
        }
    }

    // ============ ADMIN OPERATIONS ============

    public void saveAdmins(LinkedList<Admin> admins) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADMINS_FILE))) {
            oos.writeObject(admins);
            System.out.println("✓ Admins saved to file: " + ADMINS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving admins: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public LinkedList<Admin> loadAdmins() {
        File file = new File(ADMINS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ No admins file found. Starting fresh.");
            return new LinkedList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ADMINS_FILE))) {
            LinkedList<Admin> admins = (LinkedList<Admin>) ois.readObject();
            System.out.println("✓ Loaded " + admins.size() + " admin(s) from file");
            return admins;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading admins: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    // ============ AUTHOR OPERATIONS ============


    public void saveAuthors(LinkedList<Author> authors) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUTHORS_FILE))) {
            oos.writeObject(authors);
            System.out.println("✓ Authors saved to file: " + AUTHORS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving authors: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public LinkedList<Author> loadAuthors() {
        File file = new File(AUTHORS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ No authors file found. Starting fresh.");
            return new LinkedList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AUTHORS_FILE))) {
            LinkedList<Author> authors = (LinkedList<Author>) ois.readObject();
            System.out.println("✓ Loaded " + authors.size() + " author(s) from file");
            return authors;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading authors: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    // ============ CUSTOMER OPERATIONS ============


    public void saveCustomers(LinkedList<Customer> customers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CUSTOMERS_FILE))) {
            oos.writeObject(customers);
            System.out.println("✓ Customers saved to file: " + CUSTOMERS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving customers: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public LinkedList<Customer> loadCustomers() {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ No customers file found. Starting fresh.");
            return new LinkedList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CUSTOMERS_FILE))) {
            LinkedList<Customer> customers = (LinkedList<Customer>) ois.readObject();
            System.out.println("✓ Loaded " + customers.size() + " customer(s) from file");
            return customers;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading customers: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    // ============ FORMULATION OPERATIONS ============

    public void saveFormulations(LinkedList<Item> formulations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FORMULATIONS_FILE))) {
            oos.writeObject(formulations);
            System.out.println("✓ Formulations saved to file: " + FORMULATIONS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving formulations: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public LinkedList<Item> loadFormulations() {
        File file = new File(FORMULATIONS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ No formulations file found. Starting fresh.");
            return new LinkedList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FORMULATIONS_FILE))) {
            LinkedList<Item> formulations = (LinkedList<Item>) ois.readObject();
            System.out.println("✓ Loaded " + formulations.size() + " formulation(s) from file");
            return formulations;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading formulations: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    // ============ AUDIT TRAIL OPERATIONS ============


    public void saveAuditTrail(AuditTrail auditTrail) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUDIT_FILE))) {
            oos.writeObject(auditTrail);
            System.out.println("✓ Audit trail saved to file: " + AUDIT_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving audit trail: " + e.getMessage());
        }
    }

    public AuditTrail loadAuditTrail() {
        File file = new File(AUDIT_FILE);
        if (!file.exists()) {
            System.out.println("ℹ No audit trail file found. Creating new.");
            return new AuditTrail();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AUDIT_FILE))) {
            AuditTrail auditTrail = (AuditTrail) ois.readObject();
            System.out.println("✓ Audit trail loaded from file");
            return auditTrail;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading audit trail: " + e.getMessage());
            return new AuditTrail();
        }
    }

    // ============ BACKUP OPERATIONS ============


    public void createBackup(String backupName) {
        String backupDir = DATA_DIR + "backups/" + backupName + "/";
        File backup = new File(backupDir);
        backup.mkdirs();

        try {
            // Copy all data files to backup
            copyFile(ADMINS_FILE, backupDir + "admins.txt");
            copyFile(AUTHORS_FILE, backupDir + "authors.txt");
            copyFile(CUSTOMERS_FILE, backupDir + "customers.txt");
            copyFile(FORMULATIONS_FILE, backupDir + "formulations.txt");
            copyFile(AUDIT_FILE, backupDir + "audit_trail.txt");

            System.out.println("✓ Backup created: " + backupDir);
        } catch (IOException e) {
            System.err.println("❌ Error creating backup: " + e.getMessage());
        }
    }


    private void copyFile(String source, String dest) throws IOException {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            return; // Skip if source doesn't exist
        }

        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    // ============ EXPORT TO TEXT ============


    public void exportFormulationsToText(LinkedList<Item> formulations, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(70));
            writer.println("   FORMULATION CATALOG EXPORT");
            writer.println("   Export Date: " + new java.util.Date());
            writer.println("=".repeat(70));
            writer.println();

            for (Item item : formulations) {
                writer.println("-".repeat(70));
                writer.println("Name: " + item.getName());
                writer.println("Type: " + (item instanceof Food ? "Food" : "Drink"));
                writer.println("ID: " + item.getItemID());
                writer.println("Price: $" + item.getPrice());

                // Ingredients
                if (item instanceof Food) {
                    Food food = (Food) item;
                    if (food.getIngredients() != null) {
                        writer.println("\nIngredients:");
                        for (Ingredient ing : food.getIngredients()) {
                            writer.println("  - " + ing.getName());
                        }
                    }
                } else if (item instanceof Drink) {
                    Drink drink = (Drink) item;
                    if (drink.getIngredients() != null) {
                        writer.println("\nIngredients:");
                        for (Ingredient ing : drink.getIngredients()) {
                            writer.println("  - " + ing.getName());
                        }
                    }
                }

                writer.println();
            }

            writer.println("=".repeat(70));
            writer.println("Total Formulations: " + formulations.size());
            writer.println("=".repeat(70));

            System.out.println("✓ Formulations exported to: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Error exporting formulations: " + e.getMessage());
        }
    }

    // ============ UTILITY METHODS ============


    public boolean dataFilesExist() {
        return new File(ADMINS_FILE).exists() ||
                new File(AUTHORS_FILE).exists() ||
                new File(CUSTOMERS_FILE).exists() ||
                new File(FORMULATIONS_FILE).exists();
    }

    public void displayFileInfo() {
        System.out.println("\n=== FILE SYSTEM INFORMATION ===");
        System.out.println("Data Directory: " + DATA_DIR);
        displaySingleFileInfo("Admins", ADMINS_FILE);
        displaySingleFileInfo("Authors", AUTHORS_FILE);
        displaySingleFileInfo("Customers", CUSTOMERS_FILE);
        displaySingleFileInfo("Formulations", FORMULATIONS_FILE);
        displaySingleFileInfo("Audit Trail", AUDIT_FILE);
    }

    private void displaySingleFileInfo(String name, String path) {
        File file = new File(path);
        if (file.exists()) {
            System.out.println(name + ": " + file.length() + " bytes, Last modified: " +
                    new java.util.Date(file.lastModified()));
        } else {
            System.out.println(name + ": Not found");
        }
    }

    public void clearAllData() {
        System.out.println("⚠ WARNING: Clearing all data files...");
        deleteFile(ADMINS_FILE);
        deleteFile(AUTHORS_FILE);
        deleteFile(CUSTOMERS_FILE);
        deleteFile(FORMULATIONS_FILE);
        deleteFile(AUDIT_FILE);
        System.out.println("✓ All data files cleared");
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}