package net.sourceforge.stripes.examples.bugzooky.biz;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sourceforge.stripes.examples.bugzooky.model.Bug;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siberhus.stars.ServiceBean;

/**
 * Maintains an in memory list of bugs in the system.
 * 
 * @author Tim Fennell
 * @author Hussachai Puripunpinyo
 */
@ServiceBean
@Service("bugManager")
@Stateless
public class BugManagerImpl implements BugManager {

	@PersistenceContext
	private EntityManager em;
	
	/** Gets the bug with the corresponding ID, or null if it does not exist. */
	public Bug getBug(int id) {
		return em.find(Bug.class, id);
	}

	/** Returns a sorted list of all bugs in the system. */
	public List<Bug> getAllBugs() {
		return em.createQuery("from Bug").getResultList();
	}

	@Transactional
	/** Updates an existing bug, or saves a new bug if the bug is a new one. */
	public void saveOrUpdate(Bug bug) {
		if(bug.getId()==null){
			bug.setOpenDate(new Date());
		}
		em.merge(bug);
	}
	
}
