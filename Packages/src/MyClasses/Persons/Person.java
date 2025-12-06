package MyClasses.Persons;

import MyClasses.Role;

public class Person {
    private int personID;
    private String name;
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

    public Person(String name, String address, String contact, String dob) {
    }

    public String getRoleName() {
        return this.role.getDisplayName();
    }

  public int getRoleAccessLevel(){
        return this.role.getAccessLevel();
  }

    public int getPersonID() {
        return this.personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public String getName() {
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======

>>>>>>> 9897472ca87149afea826c88f3d94fd21d74ce33
>>>>>>> 0999755a29d9a35c0c46ef7750adb60d49b6c7e5
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDateofbirth() {
        return this.dateofbirth;
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
