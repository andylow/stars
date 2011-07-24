package net.sourceforge.stripes.examples.bugzooky;

import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import net.sourceforge.stripes.examples.bugzooky.model.Authority;
import net.sourceforge.stripes.examples.bugzooky.model.Bug;
import net.sourceforge.stripes.examples.bugzooky.model.Component;
import net.sourceforge.stripes.examples.bugzooky.model.Person;
import net.sourceforge.stripes.examples.bugzooky.model.Priority;
import net.sourceforge.stripes.examples.bugzooky.model.Status;

import com.siberhus.stars.SkipInjectionError;
import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsBootstrap;

/**
 * 
 * Bootstrap class to generate a test data for Bugzooky demo application
 * To enable bootstrap you have to add below snippet to web.xml
 * <pre>
 * &lt;init-param&gt;
 * 	&lt;param-name&gt;Bootstrap.Classes&lt;/param-name&gt;
 * 	&lt;param-value&gt;net.sourceforge.stripes.examples.bugzooky.BugzookyBootstrap&lt;/param-value&gt;
 * &lt;/init-param&gt;
 * </pre>
 * You can use dependency injection in boostrap class. By the way, there are no transaction associate
 * to bootstrap lifecycle method; so you have to manage transaction by yourself. 
 * @author Hussachai Puripunpinyo
 *
 */
@SkipInjectionError
public class BugzookyBootstrap implements StarsBootstrap {

	@PersistenceContext
	private EntityManager em;
	
	
	@Resource
	private UserTransaction userTx;
	
	@Override
	public void execute(ServletContext servletContext) throws Exception {
		
		if(ServiceProvider.isEjb(servletContext)){
			userTx.begin();
		}else{
			if(!em.getTransaction().isActive()) 
				em.getTransaction().begin();
		}
		try{
			
			generateTestData(servletContext);
			
			if(ServiceProvider.isEjb(servletContext)){
				userTx.commit();
			}else{
				em.getTransaction().commit();
			}
		}catch(Exception e){
			if(ServiceProvider.isEjb(servletContext)){
				userTx.rollback();
			}else{
				em.getTransaction().rollback();
			}
		}
	}
	
	private void generateTestData(ServletContext servletContext){
		
		Component components[] = new Component[]{
				new Component("Component 0"),new Component("Component 1"),
				new Component("Component 2"),new Component("Component 3"),
				new Component("Component 4")
			};
			for(Component comp : components){
				em.persist(comp);
			}
			
			Person persons[] = new Person[]{
				new Person("scooby", "scooby", "Scooby", "Doo", "scooby@mystery.machine.tv"),
				new Person("shaggy", "shaggy", "Shaggy", "Rogers", "shaggy@mystery.machine.tv"),
				new Person("scrappy", "scrappy", "Scrappy", "Doo", "scrappy@mystery.machine.tv"),
				new Person("daphne", "daphne", "Daphne", "Blake", "daphne@mystery.machine.tv"),
				new Person("velma", "velma", "Velma", "Dinkly", "velma@mystery.machine.tv"),
				new Person("fred", "fred", "Fred", "Jones", "fred@mystery.machine.tv")
			};
			for(Person person : persons){
				if(ServiceProvider.isSpring(servletContext)){
					person.setPassword(new Md5PasswordEncoder()
						.encodePassword(person.getPassword(), null));
				}
				em.persist(person);
			}
			if(ServiceProvider.isSpring(servletContext)){
				for(Person person: persons){
					em.persist(new Authority(person.getUsername(), "ROLE_USER"));
					if("scooby".equals(person.getUsername())){
						em.persist(new Authority(person.getUsername(), "ROLE_ADMIN"));
					}
				}
			}
			em.flush();
			
			
			Bug bug = new Bug();
	        bug.setShortDescription("First ever bug in the system.");
	        bug.setLongDescription("This is a test bug, and is the first one ever made.");
	        bug.setOpenDate( new Date() );
	        bug.setStatus( Status.Resolved );
	        bug.setPriority( Priority.High );
	        bug.setComponent( components[0]);
	        bug.setOwner( persons[3] );
	        em.persist(bug);
	        
	        bug = new Bug();
	        bug.setShortDescription("Another bug!  Oh no!.");
	        bug.setLongDescription("How terrible - I found another bug.");
	        bug.setOpenDate( new Date() );
	        bug.setStatus( Status.Assigned );
	        bug.setPriority( Priority.Blocker );
	        bug.setComponent( components[2] );
	        bug.setOwner( persons[4] );
	        em.persist(bug);
	        
	        bug = new Bug();
	        bug.setShortDescription("Three bugs?  This is just getting out of hand.");
	        bug.setLongDescription("What kind of system has three bugs?  Egads.");
	        bug.setOpenDate( new Date() );
	        bug.setStatus( Status.New );
	        bug.setPriority( Priority.High );
	        bug.setComponent( components[0] );
	        bug.setOwner( persons[1] );
	        em.persist(bug);
	        
	        bug = new Bug();
	        bug.setShortDescription("Oh good lord - I found a fourth bug.");
	        bug.setLongDescription("That's it, you're all fired.  I need some better developers.");
	        bug.setOpenDate( new Date() );
	        bug.setStatus( Status.New );
	        bug.setPriority( Priority.Critical );
	        bug.setComponent( components[3] );
	        bug.setOwner( persons[0] );
	        em.persist(bug);
	        
	        bug = new Bug();
	        bug.setShortDescription("Development team gone missing.");
	        bug.setLongDescription("No, wait! I didn't mean it!  Please come back and fix the bugs!!");
	        bug.setOpenDate( new Date() );
	        bug.setStatus( Status.New );
	        bug.setPriority( Priority.Blocker );
	        bug.setComponent( components[2] );
	        bug.setOwner( persons[5] );
	        em.persist(bug);	
	}

}
