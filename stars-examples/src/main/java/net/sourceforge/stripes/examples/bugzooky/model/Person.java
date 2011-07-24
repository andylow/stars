package net.sourceforge.stripes.examples.bugzooky.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a person to whom bugs can be assigned.
 *
 * @author Tim Fennell
 */
@Entity
public class Person {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
	@Column(name="username", length=16, unique=true, nullable=false)
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    @Column(name="password", length=32, nullable=false)
    private String password;

    /** Default constructor. */
    public Person() { }
    
    /** Constructs a well formed person. */
    public Person(String username, String password, String first, String last, String email) {
        this.username = username;
        this.password = password;
        this.firstName = first;
        this.lastName = last;
        this.email = email;
    }

    /** Gets the ID of the person. */
    public Integer getId() { return id; }

    /** Sets the ID of the person. */
    public void setId(Integer id) { this.id = id; }

    /** Gets the username of the person. */
    public String getUsername() { return username; }

    /** Sets the username of the user. */
    public void setUsername(String username) { this.username = username; }

    /** Gets the first name of the person. */
    public String getFirstName() { return firstName;  }

    /** Sets the first name of the user. */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** Gets the last name of the person. */
    public String getLastName() { return lastName; }

    /** Sets the last name of the user. */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** Gets the person's email address. */
    public String getEmail() { return email; }

    /** Sets the person's email address. */
    public void setEmail(String email) { this.email = email; }

    /** Gets the person's unencrypted password. */
    public String getPassword() {
        return password;
    }

    /** Sets the person's unencrypted password. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Equality is determined to be when the ID numbers match. */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Person) && this.id == ((Person) obj).id;
    }
}
