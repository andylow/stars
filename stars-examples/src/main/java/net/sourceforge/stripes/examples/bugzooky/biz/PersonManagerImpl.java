package net.sourceforge.stripes.examples.bugzooky.biz;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siberhus.stars.ServiceBean;

import net.sourceforge.stripes.examples.bugzooky.model.Authority;
import net.sourceforge.stripes.examples.bugzooky.model.Person;

/**
 * Manager class that is used to access a "database" of people that is tracked
 * in memory.
 */
@ServiceBean
@Service("personManager")
@Stateless
public class PersonManagerImpl implements PersonManager {
	
	@PersistenceContext
	private EntityManager em;

	/**
	 * Returns the person with the specified ID, or null if no such person
	 * exists.
	 */
	public Person getPerson(int id) {
		return em.find(Person.class, id);
	}

	/** Returns a person with the specified username, if one exists. */
	public Person getPerson(String username) {
		try{
			return (Person) em.createQuery("from Person p where p.username=?")
				.setParameter(1, username).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

	/** Gets a list of all the people in the system. */
	public List<Person> getAllPeople() {
		return em.createQuery("from Person").getResultList();
	}

	/**
	 * Updates the person if the ID matches an existing person, otherwise saves
	 * a new person.
	 */
	@Transactional
	public void saveOrUpdate(Person person) {
		if(person.getId()==null){
			em.persist(new Authority(person.getUsername(), "ROLE_USER"));
		}
		em.merge(person);
	}
	
	/**
	 * Deletes a person from the system...doesn't do anything fancy to clean up
	 * where the person is used.
	 */
	@Transactional
	public void deletePerson(int id) {
		em.remove(getPerson(id));
	}
	
}
