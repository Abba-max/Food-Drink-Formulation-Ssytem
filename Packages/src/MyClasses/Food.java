package MyClasses;

import java.util.LinkedList;

public class Food extends Item {
    public int foodID;
    public LinkedList<Ingredient> ingredients;       // List of Ingredient objects
    public Optcondition labCondition;                // Lab conditions
    public LinkedList<String> standards;             // List of standards respected
    public Prepprotocol prepprotocol;                // Preparation protocol (steps)
    public LinkedList<Author> authors;               // List of authors
    public LinkedList<String> feedbacks;             // Feedback from users
    public Conservecondition conservecondition;      // Conservation condition
    public Trademarkinfo trademarkInfo;              // Trademark details
    public boolean vetoed;                           // Veto about formulation
    public double averagePricePerKg;


    public Food(){

    }

    public void updateFoodInfo(){}

    public void addingredient(Ingredient ingredient){
        if (ingredients == null){
            ingredients = new LinkedList<>();
        }
        ingredients.add(ingredient);
    }
    public void addFeedback(String feedback){
        if (feedbacks == null){
            feedbacks = new LinkedList<>();
        }
        feedbacks.add(feedback);
    }

}
