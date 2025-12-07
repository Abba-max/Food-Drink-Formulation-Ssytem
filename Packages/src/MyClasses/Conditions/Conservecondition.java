package MyClasses.Conditions;

import java.io.Serializable;

public class Conservecondition implements Conditions,Serializable {
    private double temp;
    private double moisture;
    private String container;
    private static final long serialVersionUID = 1L;
    //constructors
    public Conservecondition() {}

    public Conservecondition(double temp, double moisture, String container) {
        this.temp = temp;
        this.moisture = moisture;
        this.container = container;
    }

    @Override
    public void Create() {
        // Implementation for creating conservation conditions
    }

    // Getters and Setters
    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    @Override
    public String toString() {
        return "Conservecondition{" +
                "temp=" + temp + "°C" +
                ", moisture=" + moisture + "%" +
                ", container='" + container + '\'' +
                '}';
    }
}
