package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

/**
 * Simple ActionBean implementation that all ActionBeans in the Bugzooky example
 * will extend.
 *
 * @author Tim Fennell
 * @author Hussachai Puripunpinyo
 */
public abstract class BugzookyActionBean implements ActionBean {
    private BugzookyActionBeanContext context;
    
    public void setContext(ActionBeanContext context) {
        this.context = (BugzookyActionBeanContext) context;
    }

    /** Gets the ActionBeanContext set by Stripes during initialization. */
    public BugzookyActionBeanContext getContext() {
        return this.context;
    }
    
}
