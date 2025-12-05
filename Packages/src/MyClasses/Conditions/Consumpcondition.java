package MyClasses.Conditions;

public class Consumpcondition implements Conditions {
    private double temperature;
    private double moisture;

    public Consumpcondition() {}

    public Consumpcondition(double temperature, double moisture) {
        this.temperature = temperature;
        this.moisture = moisture;
    }

    @Override
    public void Create() {
        // Implementation for creating consumption conditions
    }

    // Getters and Setters
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    @Override
    public String toString() {
        return "Consumpcondition{" +
                "temperature=" + temperature + "°C" +
                ", moisture=" + moisture + "%" +
                '}';
    }
}
