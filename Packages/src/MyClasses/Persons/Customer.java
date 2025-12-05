package MyClasses.Persons;

import MyClasses.Consumables.Drink;
import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Feedback;
import MyClasses.Ingredient;

import java.util.LinkedList;


public class Customer extends Person {
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

    public Customer(int customerID, int age) {
        super();
        this.customerID = customerID;
        this.age = age;
        this.favoriteFormulations = new LinkedList<>();
        this.feedbackHistory = new LinkedList<>();
    }

    public void consultFormulation() {
        System.out.println("Customer " + getName() + " is consulting formulations");

        if (info != null && info.getAllergies() != null) {
            System.out.println("Checking for allergens: " + info.getAllergies());
        }
    }



    public boolean hasAllergenIn(Item item) {
        if (info == null || info.getAllergies() == null || info.getAllergies().isEmpty()) {
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
            for (String allergen : info.getAllergies()) {
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
    public void Formulate(){
        return;
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