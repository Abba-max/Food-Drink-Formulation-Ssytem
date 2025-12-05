package MyClasses.Conditions;

// ========== Optcondition.java ==========
public class Optcondition implements Conditions {
    private double temp;
    private double pressure;
    private double moisture;
    private double vibration;
    private int period;

    public Optcondition() {}

    public Optcondition(double temp, double pressure, double moisture, double vibration, int period) {
        this.temp = temp;
        this.pressure = pressure;
        this.moisture = moisture;
        this.vibration = vibration;
        this.period = period;
    }

    @Override
    public void Create() {
        // Implementation for creating optimal conditions
    }

    // Getters and Setters
    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    public double getVibration() {
        return vibration;
    }

    public void setVibration(double vibration) {
        this.vibration = vibration;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "Optcondition{" +
                "temp=" + temp + "°C" +
                ", pressure=" + pressure + "kPa" +
                ", moisture=" + moisture + "%" +
                ", vibration=" + vibration +
                ", period=" + period + "min" +
                '}';
    }
}
