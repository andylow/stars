package net.sourceforge.stripes.examples.bugzooky;

import java.util.List;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManager;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Person;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceProvider;

/**
 * Manages the administration of People, from the Administer Bugzooky page. Receives a List
 * of People, which may include a new person and persists the changes. Also receives an
 * Array of IDs for people that are to be deleted, and deletes them.
 *
 * @author Tim Fennell
 * @author Hussachai Puripunpinyo
 */

@Secured("ROLE_ADMIN")
@UrlBinding("/action/bugzooky/administerPeople/{$event}")
public class AdministerPeopleActionBean extends BugzookyActionBean {
    private int[] deleteIds;

    @Service(impl=PersonManagerImpl.class)
    @Autowired
    @EJB
    private PersonManager personManager;
    
    @ValidateNestedProperties ({
        @Validate(field="username", required=true, minlength=3, maxlength=15),
        @Validate(field="password", minlength=6, maxlength=20),
        @Validate(field="firstName", required=true, maxlength=25),
        @Validate(field="lastName", required=true,  maxlength=25),
        @Validate(field="email", converter=EmailTypeConverter.class)
    })
    private List<Person> people;

    public int[] getDeleteIds() { return deleteIds; }
    public void setDeleteIds(int[] deleteIds) { this.deleteIds = deleteIds; }

    public List<Person> getPeople() { return people; }
    public void setPeople(List<Person> people) { this.people = people; }
    
    @DontBind
    @DefaultHandler
    public Resolution index(){
    	return new ForwardResolution("/bugzooky/administer-bugzooky.jsp");
    }
    
    @HandlesEvent("save")
    public Resolution saveChanges() {
        // Apply any changes to existing people (and create new ones)
        for (Person person : people) {
            Person realPerson;
            if (person.getId() == null) {
                realPerson = new Person();
            }else {
                realPerson = personManager.getPerson(person.getId());
            }
            
            realPerson.setEmail(person.getEmail());
            realPerson.setFirstName(person.getFirstName());
            realPerson.setLastName(person.getLastName());
            realPerson.setUsername(person.getUsername());

            if (person.getPassword() != null) {
                realPerson.setPassword(person.getPassword());
                if(ServiceProvider.isSpring(getContext().getServletContext())){
                	realPerson.setPassword(new Md5PasswordEncoder()
        				.encodePassword(realPerson.getPassword(), null));
        		}
            }
            
            personManager.saveOrUpdate(realPerson);
        }

        // Then, if the user checked anyone off to be deleted, delete them
        if (deleteIds != null) {
            for (int id : deleteIds) {
            	personManager.deletePerson(id);
            }
        }
        
        return new RedirectResolution(getClass());
    }
}
