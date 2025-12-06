package MyClasses.Consumables;

import MyClasses.Conditions.Prepprotocol;
import MyClasses.Persons.Author;

import java.util.Date;

public class Item {
    public String name;
    public int itemID;
    public double price;
    public Date entry_date;
//    public String expiry_date;
    public Author author;

    public Item(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setItemID(int foodID) {
    }

    public int getItemID() {
        return this.itemID;
    }


    public void setExpiryDate(String string) {
    }
}
