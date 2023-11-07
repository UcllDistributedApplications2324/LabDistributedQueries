package be.ucll.da.apigateway.cqrs;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Doctor {

    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String address;

    protected Doctor() {}

    public Doctor(Integer id, String firstName, String lastName, Integer age, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }
}
