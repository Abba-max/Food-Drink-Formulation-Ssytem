package MyClasses;

import MyClasses.Persons.Person;

import java.util.Date;

public class Veto {
    public boolean isVetoed;
    public String reason;
    public Date date;
    public Person initiator;

    public Veto(boolean isVetoed, String reason, Date date,Person initiator ){
        this.isVetoed = isVetoed;
        this.reason = reason;
        this.date = date;
        this.initiator = initiator;
    }

}
