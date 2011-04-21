package net.sourceforge.stripes.examples.bugzooky.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Class that represents a compopnent of a software system against which bugs can be
 * filed.
 *
 * @author Tim Fennell
 */
@Entity
public class Component {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
	@Column(unique=true)
    private String name;

    /** Default constructor. */
    public Component() { }
    
    /** Constructs a new component with the supplied name. */
    public Component(String name) {
        this.name = name;
    }

    /** Gets the ID of the Component. */
    public Integer getId() { return id; }

    /** Sets the ID of the Component. */
    public void setId(Integer id) { this.id = id; }

    /** Gets the name of the Component - may be null if one is not set. */
    public String getName() { return name; }

    /** Sets the name of the Component. */
    public void setName(String name) { this.name = name; }

    /** Perform equality checks based on identity. */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Component) && this.id == ((Component) obj).id;
    }
}
