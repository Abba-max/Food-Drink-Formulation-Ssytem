package MyClasses;

import java.io.Serializable;
import java.util.Objects;

public class  Quantity {
    public double weight;
    public double volume;
    public double fraction;
    public String unit;



    public Quantity() {}

    public Quantity(double weight, double volume, double fraction, String unit) {
        this.weight = weight;
        this.volume = volume;
        this.fraction = fraction;
        this.unit = unit;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getFraction() {
        return fraction;
    }

    public void setFraction(double fraction) {
        this.fraction = fraction;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return String.format("Quantity{weight=%.2f, volume=%.2f, fraction=%.2f, unit='%s'}",
                weight, volume, fraction, unit);
    }

}
