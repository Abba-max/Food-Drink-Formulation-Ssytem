package MyClasses.Restrictions;

import java.io.Serializable;
import java.util.Date;

public class Trademarkinfo implements Serializable {
    public String date;
    public String authority;
    public int authorizationnumber;
    public Date issueDate;
    private static final long serialVersionUID = 1L;

    public Trademarkinfo(){}
}
