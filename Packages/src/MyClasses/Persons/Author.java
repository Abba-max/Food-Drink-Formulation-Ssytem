package MyClasses.Persons;

public class Author extends Person {
    public int authorID;

    public Author(){

    }

    public Author(int authorID, String name, String address, String contact, String dob ){ //dob for dateofbirth
        super(name, address, contact,dob);
        this.authorID = authorID;
    }
}
