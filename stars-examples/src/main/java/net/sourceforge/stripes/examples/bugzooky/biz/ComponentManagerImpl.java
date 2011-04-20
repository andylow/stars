package net.sourceforge.stripes.examples.bugzooky.biz;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siberhus.stars.ServiceBean;

import net.sourceforge.stripes.examples.bugzooky.model.Component;

/**
 * Maintains a list of components in an in memory "database".
 *
 * @author Tim Fennell
 */
@ServiceBean
@Service("componentManager")
@Stateless
public class ComponentManagerImpl implements ComponentManager{

	@PersistenceContext
	private EntityManager em;
	
    /** Gets the component with the specified ID, or null if no such component exists. */
    public Component getComponent(int id) {
        return em.find(Component.class, id);
    }
    
    /** Returns a sorted list of all components in the system. */
    public List<Component> getAllComponents() {
        return em.createQuery("from Component").getResultList();
    }
    
    @Transactional
    /** Updates an existing component if the ID matches, or saves a new one otherwise. */
    public void saveOrUpdate(Component component) {
        em.merge(component);
    }
    
    @Transactional
    /** Deletes an existing Components.  May leave dangling references. */
    public void deleteComponent(int componentId) {
        em.remove(getComponent(componentId));
    }
    
}
