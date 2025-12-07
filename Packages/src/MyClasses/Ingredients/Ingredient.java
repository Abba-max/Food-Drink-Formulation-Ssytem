package MyClasses.Ingredients;

import java.io.Serializable;

public class Ingredient implements Serializable {
    public int ingredientID;
    public String name;
    public Quantity quantity;
    private static final long serialVersionUID = 1L;

    public Ingredient (int ingredientID, String name, Quantity q){
        this.ingredientID = ingredientID;
        this.name = name;
        this.quantity = q;

    }

    public Ingredient() {

    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getIngredientID() {
        return ingredientID;
    }

}
