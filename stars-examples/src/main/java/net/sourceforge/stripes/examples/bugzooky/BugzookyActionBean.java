package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.biz.ComponentManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Bug;
import net.sourceforge.stripes.examples.bugzooky.model.Status;

import java.util.Date;

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

    /**
     * Determines whether the bug provided is a new bug, or an existing bug. If it is an
     * existing bug, the bug is retrieved from the bug manager and changes are merged in.
     * If it is a new bug, fields that need defaults are filled in (such as creation date).
     * The newly formed bug is then returned - the bug passed in as an argument is
     * unaltered.
     *
     * @param bug a bug from which to create a fully populated bug
     * @return a fully populated bug
     */
    protected Bug populateBug2(Bug bug) {
        BugManagerImpl bm = new BugManagerImpl();
        ComponentManagerImpl cm = new ComponentManagerImpl();
        PersonManagerImpl pm = new PersonManagerImpl();
        Bug newBug;

        if (bug.getId() == null) {
            newBug = new Bug();
            newBug.setOpenDate(new Date());
        }
        else {
            newBug = bm.getBug((bug.getId()));
        }

        // Populate the fields from the bug on the form
        newBug.setLongDescription( bug.getLongDescription() );
        newBug.setPriority( bug.getPriority());
        newBug.setShortDescription( bug.getShortDescription() );
        newBug.setDueDate( bug.getDueDate() );
        newBug.setPercentComplete( bug.getPercentComplete() );

        // If it's a new bug, status isn't mandatory, so default it
        if (bug.getStatus() == null) {
            newBug.setStatus(Status.New);
        }
        else {
            newBug.setStatus( bug.getStatus() );
        }

        // Link in the full component and person based on their IDs
        newBug.setComponent( cm.getComponent(bug.getComponent().getId()) );
        newBug.setOwner( pm.getPerson(bug.getOwner().getId()) );
        return newBug;
    }
}
