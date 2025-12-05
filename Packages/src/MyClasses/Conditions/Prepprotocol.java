package MyClasses.Conditions;

import java.util.LinkedList;

/**
 * Preparation protocol class for managing step-by-step preparation instructions

 */
public class Prepprotocol implements Conditions {
    private LinkedList<String> steps;
    private LinkedList<Optcondition> stepConditions;


    public Prepprotocol() {
        this.steps = new LinkedList<>();
        this.stepConditions = new LinkedList<>();
    }


    public void addStep(String desc, Optcondition cond) {
        if (desc == null || desc.trim().isEmpty()) {
            throw new IllegalArgumentException("Step description cannot be empty");
        }

        steps.add(desc.trim());

        // Add condition (or null if no specific conditions needed)
        stepConditions.add(cond);
    }

    /**
     * Removes a step at the specified index
     * @param index Index of the step to remove
     * @return true if removal was successful
     */
    public boolean removeStep(int index) {
        if (index >= 0 && index < steps.size()) {
            steps.remove(index);
            stepConditions.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Updates a step description at the specified index
     * @param index Index of the step to update
     * @param newDesc New description
     * @return true if update was successful
     */
    public boolean updateStepDescription(int index, String newDesc) {
        if (index >= 0 && index < steps.size() && newDesc != null && !newDesc.trim().isEmpty()) {
            steps.set(index, newDesc.trim());
            return true;
        }
        return false;
    }

    /**
     * Updates the conditions for a step at the specified index
     * @param index Index of the step
     * @param newCond New conditions
     * @return true if update was successful
     */
    public boolean updateStepCondition(int index, Optcondition newCond) {
        if (index >= 0 && index < stepConditions.size()) {
            stepConditions.set(index, newCond);
            return true;
        }
        return false;
    }

    /**
     * Gets a specific step description by index
     * @param index Step index
     * @return Step description or null if index is invalid
     */
    public String getStep(int index) {
        if (index >= 0 && index < steps.size()) {
            return steps.get(index);
        }
        return null;
    }

    /**
     * Gets the conditions for a specific step by index
     * @param index Step index
     * @return Step conditions or null if index is invalid
     */
    public Optcondition getStepCondition(int index) {
        if (index >= 0 && index < stepConditions.size()) {
            return stepConditions.get(index);
        }
        return null;
    }

    /**
     * Gets the total number of steps
     * @return Number of steps in the protocol
     */
    public int getStepCount() {
        return steps.size();
    }

    /**
     * Checks if the protocol is empty (no steps)
     * @return true if no steps exist
     */
    public boolean isEmpty() {
        return steps.isEmpty();
    }

    /**
     * Clears all steps and conditions
     */
    public void clear() {
        steps.clear();
        stepConditions.clear();
    }

    /**
     * Gets all steps as a list
     * @return LinkedList of step descriptions
     */
    public LinkedList<String> getSteps() {
        return steps;
    }

    /**
     * Gets all step conditions as a list
     * @return LinkedList of step conditions
     */
    public LinkedList<Optcondition> getStepConditions() {
        return stepConditions;
    }

    /**
     * Sets all steps (replaces existing)
     * @param steps New list of steps
     */
    public void setSteps(LinkedList<String> steps) {
        if (steps == null) {
            throw new IllegalArgumentException("Steps list cannot be null");
        }
        this.steps = steps;
    }

    /**
     * Sets all step conditions (replaces existing)
     * @param stepConditions New list of conditions
     */
    public void setStepConditions(LinkedList<Optcondition> stepConditions) {
        if (stepConditions == null) {
            throw new IllegalArgumentException("Step conditions list cannot be null");
        }
        this.stepConditions = stepConditions;
    }

    /**
     * Implementation of Conditions interface
     * Creates/validates the preparation protocol
     */
    @Override
    public void Create() {
        if (steps.isEmpty()) {
            throw new IllegalStateException("Cannot create protocol: No steps defined");
        }

        // Validate that steps and conditions lists are synchronized
        if (steps.size() != stepConditions.size()) {
            throw new IllegalStateException("Steps and conditions lists are out of sync");
        }

        // Additional validation could be added here
        System.out.println("Preparation protocol created with " + steps.size() + " steps");
    }

    /**
     * Displays the complete preparation protocol in a formatted way
     */
    public void displayProtocol() {
        if (isEmpty()) {
            System.out.println("No preparation steps defined.");
            return;
        }

        System.out.println("\n=== PREPARATION PROTOCOL ===");
        for (int i = 0; i < steps.size(); i++) {
            System.out.println("\nStep " + (i + 1) + ": " + steps.get(i));

            Optcondition cond = stepConditions.get(i);
            if (cond != null && hasConditions(cond)) {
                System.out.println("  Conditions:");
                if (cond.getTemp() > 0) {
                    System.out.println("    - Temperature: " + cond.getTemp() + "°C");
                }
                if (cond.getPressure() > 0) {
                    System.out.println("    - Pressure: " + cond.getPressure() + " kPa");
                }
                if (cond.getMoisture() > 0) {
                    System.out.println("    - Moisture: " + cond.getMoisture() + "%");
                }
                if (cond.getPeriod() > 0) {
                    System.out.println("    - Duration: " + cond.getPeriod() + " minutes");
                }
            }
        }
        System.out.println("=".repeat(30));
    }

    /**
     * Helper method to check if conditions are specified
     */
    private boolean hasConditions(Optcondition cond) {
        return cond.getTemp() > 0 ||
                cond.getPressure() > 0 ||
                cond.getMoisture() > 0 ||
                cond.getVibration() > 0 ||
                cond.getPeriod() > 0;
    }

    /**
     * Gets the total estimated time for all steps
     * @return Total time in minutes
     */
    public int getTotalTime() {
        int totalTime = 0;
        for (Optcondition cond : stepConditions) {
            if (cond != null) {
                totalTime += cond.getPeriod();
            }
        }
        return totalTime;
    }

    /**
     * Validates the protocol for completeness
     * @return true if protocol is valid and complete
     */
    public boolean validate() {
        if (isEmpty()) {
            System.out.println("Validation failed: No steps defined");
            return false;
        }

        if (steps.size() != stepConditions.size()) {
            System.out.println("Validation failed: Steps and conditions mismatch");
            return false;
        }

        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i) == null || steps.get(i).trim().isEmpty()) {
                System.out.println("Validation failed: Step " + (i + 1) + " is empty");
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a copy of this protocol
     * @return New Prepprotocol instance with same data
     */
    public Prepprotocol copy() {
        Prepprotocol copy = new Prepprotocol();

        for (int i = 0; i < steps.size(); i++) {
            String stepDesc = steps.get(i);
            Optcondition cond = stepConditions.get(i);

            // Create a copy of the condition
            Optcondition condCopy = null;
            if (cond != null) {
                condCopy = new Optcondition(
                        cond.getTemp(),
                        cond.getPressure(),
                        cond.getMoisture(),
                        cond.getVibration(),
                        cond.getPeriod()
                );
            }

            copy.addStep(stepDesc, condCopy);
        }

        return copy;
    }

    /**
     * Exports protocol as formatted text
     * @return String representation suitable for reports
     */
    public String exportAsText() {
        if (isEmpty()) {
            return "No preparation steps defined.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("PREPARATION PROTOCOL\n");
        sb.append("=".repeat(50)).append("\n\n");

        for (int i = 0; i < steps.size(); i++) {
            sb.append("Step ").append(i + 1).append(": ").append(steps.get(i)).append("\n");

            Optcondition cond = stepConditions.get(i);
            if (cond != null && hasConditions(cond)) {
                sb.append("  Conditions:\n");
                if (cond.getTemp() > 0) {
                    sb.append("    Temperature: ").append(cond.getTemp()).append("°C\n");
                }
                if (cond.getPressure() > 0) {
                    sb.append("    Pressure: ").append(cond.getPressure()).append(" kPa\n");
                }
                if (cond.getMoisture() > 0) {
                    sb.append("    Moisture: ").append(cond.getMoisture()).append("%\n");
                }
                if (cond.getPeriod() > 0) {
                    sb.append("    Duration: ").append(cond.getPeriod()).append(" minutes\n");
                }
            }
            sb.append("\n");
        }

        sb.append("Total estimated time: ").append(getTotalTime()).append(" minutes\n");
        sb.append("=".repeat(50));

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Prepprotocol{" +
                "stepCount=" + steps.size() +
                ", totalTime=" + getTotalTime() + " min" +
                ", valid=" + validate() +
                '}';
    }

    /**
     * Generates a summary of the protocol
     * @return Brief summary string
     */
    public String getSummary() {
        if (isEmpty()) {
            return "Empty protocol";
        }
        return steps.size() + " steps, " + getTotalTime() + " minutes total";
    }
}