package MyClasses;

import java.util.LinkedList;


public class Customer extends Person implements Formulation {
    private int customerID;
    private int age;
    private ConsumerSpecificInfo info;
    private LinkedList<Item> favoriteFormulations;
    private LinkedList<Feedback> feedbackHistory;

    public Customer() {
        super();
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
    }

    public Customer(int customerID, String name, String address, String contact, String dob, int age) {
        super(name, address, contact, dob);
        this.customerID = customerID;
        this.age = age;
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
    }

    /**
     * Customer creates their own formulation suggestion
     */
    @Override
    public void Formulate() {
        System.out.println("Customer " + getName() + " is creating a formulation suggestion");
        // Implementation would involve creating a new Food/Drink object
        // with customer's preferences and sending to admin for review
    }

    /**
     * Customer consults available formulations
     * Can search by ingredients to check for allergies
     */
    @Override
    public void consultFormulation() {
        System.out.println("Customer " + getName() + " is consulting formulations");

        if (info != null && info.allergies != null) {
            System.out.println("Checking for allergens: " + info.allergies);
        }
    }

    /**
     * Customer checks formulation for potential issues based on their profile
     * Includes allergy checking and side effects
     */
    @Override
    public void checkFormulationissues() {
        System.out.println("Customer " + getName() + " is checking formulation issues");

        if (info != null && info.allergies != null && !info.allergies.isEmpty()) {
            System.out.println("Warning: Customer has " + info.allergies.size() + " known allergies");
        }
    }

    /**
     * Checks if a formulation contains allergens for this customer
     */
    public boolean hasAllergenIn(Item item) {
        if (info == null || info.allergies == null || info.allergies.isEmpty()) {
            return false;
        }

        LinkedList<Ingredient> ingredients = null;

        if (item instanceof Food) {
            ingredients = ((Food) item).getIngredients();
        } else if (item instanceof Drink) {
            ingredients = ((Drink) item).getIngredients();
        }

        if (ingredients == null) {
            return false;
        }

        for (Ingredient ing : ingredients) {
            for (String allergen : info.allergies) {
                if (ing.name != null &&
                        ing.name.toLowerCase().contains(allergen.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Customer provides feedback on a formulation
     */
    public Feedback provideFeedback(Item item, String comment, boolean like) {
        Feedback feedback = new Feedback(comment, like, getName());
        feedbackHistory.add(feedback);

        if (item instanceof Food) {
            ((Food) item).addFeedback(feedback);
        } else if (item instanceof Drink) {
            ((Drink) item).addFeedback(feedback);
        }

        System.out.println("Feedback submitted for: " + item.name);
        return feedback;
    }

    /**
     * Adds formulation to customer's favorites
     */
    public void addToFavorites(Item item) {
        if (item != null && !favoriteFormulations.contains(item)) {
            favoriteFormulations.add(item);
            System.out.println("Added " + item.name + " to favorites");
        }
    }

    /**
     * Removes formulation from favorites
     */
    public boolean removeFromFavorites(Item item) {
        boolean removed = favoriteFormulations.remove(item);
        if (removed) {
            System.out.println("Removed " + item.name + " from favorites");
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

    // Getters and Setters
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
                ", favoritesCount=" + favoriteFormulations.size() +
                ", feedbackCount=" + feedbackHistory.size() +
                '}';
    }
}