package MyClasses;

import java.util.LinkedList;

public class Drink extends Item {
    public int drinkID;
    public LinkedList<Ingredient> ingredients;
    public Optcondition labCondition;
    public LinkedList<String> standards;
    public Prepprotocol prepprotocol;
    public LinkedList<Author> authors;
    public LinkedList<String> feedbacks;
    public Conservecondition conservecondition;
    public Trademarkinfo trademarkInfo;
    public boolean vetoed;
    public double averagePricePerKg;
    public Drink(){

    }

    public void updateDrinkInfo(){

    }
    public void addIngredient(Ingredient ingredient) {
        if (ingredients == null) ingredients = new LinkedList<>();
        ingredients.add(ingredient);
    }

    public void addFeedback(String feedback) {
        if (feedbacks == null) feedbacks = new LinkedList<>();
        feedbacks.add(feedback);
    }
}