package ch.martinelli.sakila.endpoints;

public class Customer {

    private Integer id;
    private String firstName;
    private String lastName;
    private boolean activebool;

    public Customer(Integer id, String firstName, String lastName, boolean activebool) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.activebool = activebool;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActivebool() {
        return activebool;
    }

    public void setActivebool(boolean activebool) {
        this.activebool = activebool;
    }
}
