package MyClasses.Consumables;


import MyClasses.*;
import MyClasses.Conditions.Conservecondition;
import MyClasses.Conditions.Consumpcondition;
import MyClasses.Conditions.Optcondition;
import MyClasses.Conditions.Prepprotocol;
import MyClasses.Persons.Author;
import MyClasses.Persons.ConsumerSpecificInfo;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Food extends Item {
    private int foodID;
    private LinkedList<Ingredient> ingredients;
    private Optcondition labCondition;
    private LinkedList<String> standards;
    private Prepprotocol prepprotocol;
    private LinkedList<Author> authors;
    private LinkedList<Feedback> feedbacks;
    private Conservecondition conservecondition;
    private Consumpcondition consumpcondition;
    private Trademarkinfo trademarkInfo;
    private Veto veto;
    private double averagePricePerKg;
    private ConsumerSpecificInfo consumerProfile;
    private ConsumerSpecificInfo positiveImpacts;
    private SideEffects sideEffects;

    public Food() {
        this.ingredients = new LinkedList<>();
        this.standards = new LinkedList<>();
        this.authors = new LinkedList<>();
        this.feedbacks = new LinkedList<>();
    }

    public Food(int foodID, String name) {
        this();
        this.foodID = foodID;
        this.name = name;
    }


    public void updateFoodInfo(String name, double price) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (price >= 0) {
            this.price = price;
        }
    }


    public void addIngredient(Ingredient ingredient) {
        if (ingredient != null) {
            if (ingredients == null) {
                ingredients = new LinkedList<>();
            }
            ingredients.add(ingredient);
            recalculatePrice();
        }
    }


    public boolean removeIngredient(int ingredientID) {
        boolean removed = ingredients.removeIf(ing -> ing.ingredientID == ingredientID);
        if (removed) {
            recalculatePrice();
        }
        return removed;
    }


    public boolean modifyIngredientQuantity(int ingredientID, Quantity newQuantity) {
        for (Ingredient ing : ingredients) {
            if (ing.ingredientID == ingredientID) {
                ing.quantity = newQuantity;
                recalculatePrice();
                return true;
            }
        }
        return false;
    }

    /**
     * Adds consumer feedback
     */
    public void addFeedback(Feedback feedback) {
        if (feedback != null) {
            if (feedbacks == null) {
                feedbacks = new LinkedList<>();
            }
            feedbacks.add(feedback);
        }
    }



    public void addStandard(String standard) {
        if (standard != null && !standard.trim().isEmpty()) {
            if (standards == null) {
                standards = new LinkedList<>();
            }
            standards.add(standard);
        }
    }


    public void addAuthor(Author author) {
        if (author != null) {
            if (authors == null) {
                authors = new LinkedList<>();
            }
            authors.add(author);
        }
    }


    public void setVeto(Veto veto) {
        this.veto = veto;
    }


    public boolean isVetoed() {
        return veto != null && veto.isVetoed;
    }


    private void recalculatePrice() {
        // Simple implementation - can be enhanced with actual ingredient costs
        if (ingredients != null && !ingredients.isEmpty()) {
            this.averagePricePerKg = ingredients.size() * 10.0; // Placeholder calculation
        }
    }

    public Ingredient findIngredientByName(String name) {
        if (ingredients == null || name == null) return null;

        return ingredients.stream()
                .filter(ing -> ing.name != null && ing.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


    public LinkedList<Feedback> getPositiveFeedbacks() {
        if (feedbacks == null) return new LinkedList<>();

        return feedbacks.stream()
                .filter(f -> f.isLike())
                .collect(Collectors.toCollection(LinkedList::new));
    }


    public LinkedList<Feedback> getNegativeFeedbacks() {
        if (feedbacks == null) return new LinkedList<>();

        return feedbacks.stream()
                .filter(f -> !f.isLike())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    // Getters and Setters
    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public LinkedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Optcondition getLabCondition() {
        return labCondition;
    }

    public void setLabCondition(Optcondition labCondition) {
        this.labCondition = labCondition;
    }

    public LinkedList<String> getStandards() {
        return standards;
    }

    public Prepprotocol getPrepprotocol() {
        return prepprotocol;
    }

    public void setPrepprotocol(Prepprotocol prepprotocol) {
        this.prepprotocol = prepprotocol;
    }

    public LinkedList<Author> getAuthors() {
        return authors;
    }

    public LinkedList<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public Conservecondition getConservecondition() {
        return conservecondition;
    }

    public void setConservecondition(Conservecondition conservecondition) {
        this.conservecondition = conservecondition;
    }

    public Consumpcondition getConsumpcondition() {
        return consumpcondition;
    }

    public void setConsumpcondition(Consumpcondition consumpcondition) {
        this.consumpcondition = consumpcondition;
    }

    public Trademarkinfo getTrademarkInfo() {
        return trademarkInfo;
    }

    public void setTrademarkInfo(Trademarkinfo trademarkInfo) {
        this.trademarkInfo = trademarkInfo;
    }

    public Veto getVeto() {
        return veto;
    }

    public double getAveragePricePerKg() {
        return averagePricePerKg;
    }

    public void setAveragePricePerKg(double averagePricePerKg) {
        this.averagePricePerKg = averagePricePerKg;
    }

    public ConsumerSpecificInfo getConsumerProfile() {
        return consumerProfile;
    }

    public void setConsumerProfile(ConsumerSpecificInfo consumerProfile) {
        this.consumerProfile = consumerProfile;
    }

    public ConsumerSpecificInfo getPositiveImpacts() {
        return positiveImpacts;
    }

    public void setPositiveImpacts(ConsumerSpecificInfo positiveImpacts) {
        this.positiveImpacts = positiveImpacts;
    }

    public SideEffects getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(SideEffects sideEffects) {
        this.sideEffects = sideEffects;
    }

    @Override
    public String toString() {
        return "Food{" +
                "foodID=" + foodID +
                ", name='" + name + '\'' +
                ", ingredientsCount=" + (ingredients != null ? ingredients.size() : 0) +
                ", averagePricePerKg=" + averagePricePerKg +
                ", vetoed=" + isVetoed() +
                '}';
    }

    public void setExpiryDate(String string) {
    }
}