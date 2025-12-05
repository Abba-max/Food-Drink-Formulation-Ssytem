package MyClasses.Persons;


import MyClasses.Consumables.Food;
import MyClasses.Consumables.Item;
import MyClasses.Formulation;
import MyClasses.Keyboard.Keypad;

import java.util.LinkedList;

public class Author extends Person implements Formulation {
    private int authorID;
    private LinkedList<Item> formulatedItems;

    Keypad pad = new Keypad();


    public Author(int authorID ){ //dob for dateOfBirth
        super();
        this.authorID = authorID;
        this.formulatedItems = new LinkedList<>();
    }

    public LinkedList<Item> getFormulatedItems(){
        return this.formulatedItems;
    }

    public int getAuthorID(){
        return this.authorID;
    }


    @Override
    public Item Formulate() {
        System.out.println("Enter the Information for the item to formulate");


    }

    @Override
    public void consultFormulation() {

    }

    @Override
    public void checkFormulationissues() {

    }
}
