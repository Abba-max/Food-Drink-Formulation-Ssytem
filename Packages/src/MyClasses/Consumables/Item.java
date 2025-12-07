package MyClasses.Consumables;

import MyClasses.Conditions.Prepprotocol;
import MyClasses.Persons.Author;
import java.io.Serializable;

import java.util.Date;

public abstract class Item implements Serializable{
    public String name;
    public int itemID;
    public double price;
    public Date entry_date;
    public String expiry_date;
    public Author author;
    private static final long serialVersionUID = 1L;

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

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getItemID() {
        return this.itemID;
    }

    public Date getEntry_date() {
        return this.entry_date;
    }

    public String getExpiry_date() {
        return this.expiry_date;
    }

    // Getters and Setters

    public void setExpiryDate(String string) {
    }
}
