package MyClasses.Utilities;

import java.util.LinkedList;

public class AuditTrail {
    public LinkedList<String> records;

    public AuditTrail(){
        records = new LinkedList<>();
    }
    public void logAction(String user, String action){
        records.add(user + "performed" + action);
    }
}
