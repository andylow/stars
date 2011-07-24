package net.sourceforge.stripes.examples.bugzooky;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManager;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Bug;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.Service;

/**
 * ActionBean that deals with setting up and saving edits to multiple bugs at
 * once. Can also deal with adding multiple new bugs at once.
 * 
 * @author Tim Fennell
 */
@UrlBinding("/action/bugzooky/multiBug/{$event}")
public class MultiBugActionBean extends BugzookyActionBean {
	
	@Service(impl=BugManagerImpl.class)
	@Autowired
	@EJB
	private BugManager bugManager;

	/** Populated during bulk add/edit operations. */
	@ValidateNestedProperties({
			@Validate(field = "shortDescription", required = true, maxlength = 75),
			@Validate(field = "longDescription", required = true, minlength = 25),
			@Validate(field = "component.id", required = true),
			@Validate(field = "owner.id", required = true),
			@Validate(field = "priority", required = true) })
	private List<Bug> bugs = new ArrayList<Bug>();

	/** Populated by the form submit on the way into bulk edit. */
	private int[] bugIds;

	/** Gets the array of bug IDs the user selected for edit. */
	public int[] getBugIds() {
		return bugIds;
	}

	/** Sets the array of bug IDs the user selected for edit. */
	public void setBugIds(int[] bugIds) {
		this.bugIds = bugIds;
	}

	/**
	 * Simple getter that returns the List of Bugs. Not the use of generics
	 * syntax - this is necessary to let Stripes know what type of object to
	 * create and insert into the list.
	 */
	public List<Bug> getBugs() {
		return bugs;
	}

	/** Setter for the list of bugs. */
	public void setBugs(List<Bug> bugs) {
		this.bugs = bugs;
	}

	@DontValidate
	@DefaultHandler
	public Resolution index() {
		setBugs(bugManager.getAllBugs());
		return new ForwardResolution("/bugzooky/bug-list.jsp");
	}

	@DontBind
	public Resolution create(){
		return new ForwardResolution("/bugzooky/bulk-add-edit-bugs.jsp");
	}
	 
	public Resolution save() {
		for (Bug bug : bugs) {
			// Bug newBug = populateBug(bug);
			bugManager.saveOrUpdate(bug);
		}

		return index();
	}

	@DontValidate
	public Resolution preEdit() {
		if (this.bugIds == null) {
			getContext().getValidationErrors()
				.addGlobalError(new SimpleError("You must select at least one bug to edit."));
			return index();
		}
		
		for (int id : this.bugIds) {
			this.bugs.add(bugManager.getBug(id));
		}

		return new ForwardResolution("/bugzooky/bulk-add-edit-bugs.jsp");
	}
}
