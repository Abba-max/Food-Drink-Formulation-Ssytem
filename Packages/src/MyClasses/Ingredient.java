package MyClasses;

public class Ingredient {
    public int ingredientID;
    public String name;
    public Quantity quantity;

    public Ingredient(int ingredientID, String name, Quantity q){
        this.ingredientID = ingredientID;
        this.name = name;
        this.quantity = q;
    }

    public Ingredient() {

    }
}
