package MyClasses.Persons;

public enum Role {
    CUSTOMER("Customer", 10),
    AUTHOR("Author", 50),
    ADMIN("Administrator", 100); // Highest access level


    private final String displayName;
    private final int accessLevel;

    Role(String displayName, int accessLevel) {
        this.displayName = displayName;
        this.accessLevel = accessLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public boolean hasSufficientAccess(Role otherRole, int level) {
        return this.accessLevel >= level;
    }
}