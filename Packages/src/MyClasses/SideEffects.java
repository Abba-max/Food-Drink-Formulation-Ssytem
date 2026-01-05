package MyClasses;

import MyClasses.Persons.ConsumerSpecificInfo;

import java.io.Serializable;
import java.util.LinkedList;


public class SideEffects implements Serializable {
    private ConsumerSpecificInfo exposedConsumerProfile;
    private LinkedList<String> symptoms;
    private LinkedList<String> remedies;
    private static final long serialVersionUID = 1L;

    public SideEffects() {
        this.symptoms = new LinkedList<>();
        this.remedies = new LinkedList<>();
    }

    public SideEffects(ConsumerSpecificInfo exposedProfile) {
        this();
        this.exposedConsumerProfile = exposedProfile;
    }

    public void addSymptom(String symptom) {
        if (symptom != null && !symptom.trim().isEmpty()) {
            symptoms.add(symptom);
        }
    }

    public void addRemedy(String remedy) {
        if (remedy != null && !remedy.trim().isEmpty()) {
            remedies.add(remedy);
        }
    }

    // Getters and Setters
    public ConsumerSpecificInfo getExposedConsumerProfile() {
        return exposedConsumerProfile;
    }

    public void setExposedConsumerProfile(ConsumerSpecificInfo exposedConsumerProfile) {
        this.exposedConsumerProfile = exposedConsumerProfile;
    }

    public LinkedList<String> getSymptoms() {
        return symptoms;
    }

    public LinkedList<String> getRemedies() {
        return remedies;
    }

    @Override
    public String toString() {
        return "SideEffects{" +
                "exposedProfile=" + exposedConsumerProfile +
                ", symptoms=" + symptoms +
                ", remedies=" + remedies +
                '}';
    }
}