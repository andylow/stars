package net.sourceforge.stripes.examples.bugzooky;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManager;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Person;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceProvider;

/**
 * An example of an ActionBean that uses validation annotations on fields instead of
 * on methods.  Logs the user in using a conventional username/password combo and
 * validates the password in the action method.
 *
 * @author Tim Fennell
 */
@UrlBinding("/action/bugzooky/authc/{$event}")
public class AuthcActionBean extends BugzookyActionBean {
	
	@Service(impl=PersonManagerImpl.class)
	@Autowired
	@EJB
	private PersonManager personManager;
	
    @Validate(required=true)
    private String username;

    @Validate(required=true)
    private String password;

    private String targetUrl;

    /** The username of the user trying to log in. */
    public void setUsername(String username) { this.username = username; }

    /** The username of the user trying to log in. */
    public String getUsername() { return username; }

    /** The password of the user trying to log in. */
    public void setPassword(String password) { this.password = password; }

    /** The password of the user trying to log in. */
    public String getPassword() { return password; }

    /** The URL the user was trying to access (null if the login page was accessed directly). */
    public String getTargetUrl() { return targetUrl; }

    /** The URL the user was trying to access (null if the login page was accessed directly). */
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    
    @DontValidate
    @DefaultHandler
    public Resolution index(){
    	if(ServiceProvider.isSpring(getContext().getServletContext())){
    		
    		return new ForwardResolution("/bugzooky/login-spring.jsp");
    	}
    	return new ForwardResolution("/bugzooky/login.jsp");
    }
    
    public Resolution login() {
        Person person = personManager.getPerson(this.username);

        if (person == null) {
            ValidationError error = new LocalizableError("usernameDoesNotExist");
            getContext().getValidationErrors().add("username", error);
            return getContext().getSourcePageResolution();
        }else if (!person.getPassword().equals(password)) {
            ValidationError error = new LocalizableError("incorrectPassword");
            getContext().getValidationErrors().add("password", error);
            return getContext().getSourcePageResolution();
        }else {
            getContext().setUser(person);
            if (this.targetUrl != null) {
                return new RedirectResolution(this.targetUrl);
            }else {
                return new RedirectResolution("/bugzooky/bug-list.jsp");
            }
        }
    }
    
    @DontBind
    public Resolution logout() throws Exception {
        getContext().logout();
        return new RedirectResolution("/bugzooky/exit.jsp");
    }
    
}
