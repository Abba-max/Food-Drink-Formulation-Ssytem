package MyClasses;

import Persons.ConsumerSpecificInfo;

import java.util.LinkedList;


public class SideEffects {
    private ConsumerSpecificInfo exposedConsumerProfile;
    private LinkedList<String> symptoms;
    private LinkedList<String> remedies;

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