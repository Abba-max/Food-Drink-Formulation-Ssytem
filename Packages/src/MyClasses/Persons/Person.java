package MyClasses.Persons;

import MyClasses.Role;

public class Person {
    public int personID;
    public String name;
    private String address;
    private String contact;
    private String dateofbirth;
    private String password;
    private Role role;


    public Person(String name, String address, String contact, String dob, String password, Role role) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.dateofbirth = dob;
        this.password = password;
        this.role = role;
    }

    public Person() {

    }

    public String getRoleName() {
        return this.role.getDisplayName();
    }

  public int getRoleAccessLevel(){
        return this.role.getAccessLevel();
  }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personID=" + personID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contact='" + contact + '\'' +
                ", dateofbirth='" + dateofbirth + '\'' +
                '}';
    }


}
