package MyClasses.Persons;

import java.util.LinkedList;
import java.io.Serializable;

/**
 * Consumer specific information including profile and allergies
 */
public class ConsumerSpecificInfo implements Serializable {
    private String profile;
    private LinkedList<String> allergies;
    private String ageRange;
    private LinkedList<String> positiveImpacts;
    private static final long serialVersionUID = 1L;

    public ConsumerSpecificInfo() {
        this.allergies = new LinkedList<>();
        this.positiveImpacts = new LinkedList<>();
    }

    public ConsumerSpecificInfo(String profile) {
        this();
        this.profile = profile;
    }

    /**
     * Adds an allergy to the list
     */
    public void addAllergy(String allergy) {
        if (allergy != null && !allergy.trim().isEmpty()) {
            if (allergies == null) {
                allergies = new LinkedList<>();
            }
            allergies.add(allergy);
        }
    }

    /**
     * Removes an allergy from the list
     */
    public boolean removeAllergy(String allergy) {
        if (allergies != null) {
            return allergies.remove(allergy);
        }
        return false;
    }

    /**
     * Checks if consumer has specific allergy
     */
    public boolean hasAllergy(String allergy) {
        if (allergies == null || allergy == null) {
            return false;
        }
        return allergies.stream()
                .anyMatch(a -> a.equalsIgnoreCase(allergy));
    }

    /**
     * Adds a positive impact
     */
    public void addPositiveImpact(String impact) {
        if (impact != null && !impact.trim().isEmpty()) {
            if (positiveImpacts == null) {
                positiveImpacts = new LinkedList<>();
            }
            positiveImpacts.add(impact);
        }
    }

    // Getters and Setters
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public LinkedList<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(LinkedList<String> allergies) {
        this.allergies = allergies;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public LinkedList<String> getPositiveImpacts() {
        return positiveImpacts;
    }

    public void setPositiveImpacts(LinkedList<String> positiveImpacts) {
        this.positiveImpacts = positiveImpacts;
    }

    @Override
    public String toString() {
        return "ConsumerSpecificInfo{" +
                "profile='" + profile + '\'' +
                ", allergies=" + (allergies != null ? allergies.size() : 0) +
                ", ageRange='" + ageRange + '\'' +
                ", positiveImpacts=" + (positiveImpacts != null ? positiveImpacts.size() : 0) +
                '}';
    }
}