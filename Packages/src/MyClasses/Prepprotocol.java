package MyClasses;

import java.util.LinkedList;

public class Prepprotocol implements  Conditions{
    public LinkedList<String> steps;
    public LinkedList<Optcondition> stepConditions;

    public Prepprotocol(){
        steps = new LinkedList<>();
        stepConditions = new LinkedList<>();
    }

    public void addStep(String desc, Optcondition cond){
        steps.add(desc); //desc is for the description of the step of preparation
        stepConditions.add(cond);
    }




    public void Create(){
        return;
    }
}
