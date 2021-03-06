package net.sourceforge.stripes.examples.bugzooky;

import java.util.List;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.bugzooky.biz.ComponentManager;
import net.sourceforge.stripes.examples.bugzooky.biz.ComponentManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Component;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.siberhus.stars.Service;

/**
 * Manages the administration of Components, from the Administer Bugzooky page. Receives a List
 * of Components, which may include a new component and persists the changes. Also receives an
 * Array of IDs for components that are to be deleted, and deletes those.
 *
 * @author Tim Fennell
 * @author Hussachai Puripunpinyo
 */
@Secured("ROLE_USER")
@UrlBinding("/action/bugzooky/administerComponents/{$event}")
public class AdministerComponentsActionBean extends BugzookyActionBean {
    private int[] deleteIds;
    
    @Service(impl=ComponentManagerImpl.class)
    @Autowired
    @EJB
    private ComponentManager componentManager;
    
    @ValidateNestedProperties ({
        @Validate(field="name", required=true, minlength=3, maxlength=25)
    })
    private List<Component> components;

    public int[] getDeleteIds() { return deleteIds; }
    public void setDeleteIds(int[] deleteIds) { this.deleteIds = deleteIds; }

    public List<Component> getComponents() { return components; }
    public void setComponents(List<Component> components) { this.components = components; }
    
    @DontBind
    @DefaultHandler
    public Resolution index(){
    	return new ForwardResolution("/bugzooky/administer-bugzooky.jsp");
    }
    
    @Secured("ROLE_ADMIN")
    public Resolution save() {

        // Apply any changes to existing people (and create new ones)
        for (Component component : components) {
            Component realComponent;
            if (component.getId() == null) {
                realComponent = new Component();
            }else {
                realComponent = componentManager.getComponent(component.getId());
            }

            realComponent.setName(component.getName());
            componentManager.saveOrUpdate(realComponent);
        }

        // Then, if the user checked anyone off to be deleted, delete them
        if (deleteIds != null) {
            for (int id : deleteIds) {
            	componentManager.deleteComponent(id);
            }
        }

        return new RedirectResolution(getClass());
    }
}
