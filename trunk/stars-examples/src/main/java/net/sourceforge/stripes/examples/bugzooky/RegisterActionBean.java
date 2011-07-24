package net.sourceforge.stripes.examples.bugzooky;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.action.Wizard;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManager;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Person;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceProvider;

/**
 * ActionBean that handles the registration of new users.
 *
 * @author Tim Fennell
 */
@Wizard
@UrlBinding("/action/bugzooky/register/{$event}")
public class RegisterActionBean extends BugzookyActionBean {
	
	@Service(impl=PersonManagerImpl.class)
	@Autowired
	@EJB
	private PersonManager personManager;
	
    @ValidateNestedProperties({
        @Validate(field="username", required=true, minlength=5, maxlength=20),
        @Validate(field="password", required=true, minlength=5, maxlength=20),
        @Validate(field="firstName", required=true, maxlength=50),
        @Validate(field="lastName", required=true, maxlength=50)
    })
    private Person user;

    @Validate(required=true, minlength=5, maxlength=20, expression="this == user.password")
    private String confirmPassword;

    /** The user being registered. */
    public void setUser(Person user) { this.user = user; }

    /** The user being registered. */
    public Person getUser() { return user; }

    /** The 2nd/confirmation password entered by the user. */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /** The 2nd/confirmation password entered by the user. */
    public String getConfirmPassword() { return confirmPassword; }

    /**
     * Validates that the two passwords entered match each other, and that the
     * username entered is not already taken in the system.
     */
    @ValidationMethod
    public void validate(ValidationErrors errors) {
        if ( personManager.getPerson(this.user.getUsername()) != null ) {
            errors.add("user.username", new LocalizableError("usernameTaken"));
        }
    }

    @DontBind
    @DefaultHandler
    public Resolution index(){
    	
    	return new ForwardResolution("/bugzooky/register.jsp");
    }
    
    public Resolution gotoStep2() throws Exception {
    	
        return new ForwardResolution("/bugzooky/register2.jsp");
    }
    
    /**
     * Registers a new user, logs them in, and redirects them to the bug list page.
     */
    public Resolution register() {
    	if(ServiceProvider.isSpring(getContext().getServletContext())){
    		user.setPassword(new Md5PasswordEncoder()
				.encodePassword(user.getPassword(), null));
		}
    	personManager.saveOrUpdate(this.user);
        getContext().setUser(this.user);
        getContext().getMessages().add(
                new LocalizableMessage(getClass().getName() + ".successMessage",
                                       this.user.getFirstName(),
                                       this.user.getUsername()));
        
        return new RedirectResolution(MultiBugActionBean.class);
    }
}
