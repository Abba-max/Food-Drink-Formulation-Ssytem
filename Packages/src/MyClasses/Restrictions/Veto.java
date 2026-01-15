package MyClasses.Restrictions;

import MyClasses.Persons.Person;

import java.io.Serializable;
import java.util.Date;

public class Veto implements Serializable {
    public boolean isVetoed;
    public String reason;
    public Date date;
    public Person initiator;
    private static final long serialVersionUID = 1L;


    public Veto(boolean isVetoed, String reason, Date date,Person initiator ){
        this.isVetoed = isVetoed;
        this.reason = reason;
        this.date = date;
        this.initiator = initiator;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean isActive() {
        return this.isVetoed;
    }
}
