package MyClasses;

public class Quantity extends Ingredient {
    public double weight;
    public double volume;
    public double fraction;
    public String unit;



    public  Quantity(double weight, double volume){
        this.weight = weight;
        this.volume = volume;
    }
}
