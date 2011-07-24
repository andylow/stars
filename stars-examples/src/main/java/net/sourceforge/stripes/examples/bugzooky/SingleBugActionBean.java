package net.sourceforge.stripes.examples.bugzooky;

import java.io.IOException;
import java.io.InputStream;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManager;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Attachment;
import net.sourceforge.stripes.examples.bugzooky.model.Bug;
import net.sourceforge.stripes.validation.PercentageTypeConverter;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.Service;

/**
 * ActionBean that provides method for editing a single bug in detail. Includes an
 * event for pre-populating the ActionBean on the way in to an edit screen, and a
 * single event for saving an existing or new bug.  Uses a FileBean property to
 * support the uploading of a File concurrent with other edits.
 *
 * @author Tim Fennell
 */
@UrlBinding("/action/bugzooky/singleBug/{$event}")
public class SingleBugActionBean extends BugzookyActionBean {
	
	@Service(impl=BugManagerImpl.class)
	@Autowired
	@EJB
	private BugManager bugManager;
	
    @ValidateNestedProperties({
        @Validate(field="shortDescription", required=true),
        @Validate(field="longDescription", required=true),
        @Validate(field="percentComplete", minvalue=0, maxvalue=1,
                  converter=PercentageTypeConverter.class)
    })
    private Bug bug;
    private FileBean newAttachment;
    
    public Bug getBug() { return bug; }
    public void setBug(Bug bug) { this.bug = bug; }

    public FileBean getNewAttachment() { return newAttachment; }
    public void setNewAttachment(FileBean newAttachment) { this.newAttachment = newAttachment; }
    
    
    @DontBind
    @DefaultHandler
    public Resolution index(){
    	 return new ForwardResolution("/bugzooky/add-edit-bug.jsp");
    }
    
    @DontBind
    public Resolution create(){
    	return index();
    }
    
    /** Loads a bug on to the form ready for editing. */
    @DontValidate
    public Resolution preEdit() {
    	if(this.bug!=null){
    		this.bug = bugManager.getBug( this.bug.getId() );
    		System.out.println("Attachment"+this.bug.getAttachments());
    	}
        return index();
    }
    
    /** Saves (or updates) a bug, and then returns the user to the bug list. */
    public Resolution save() throws IOException {

//        Bug newBug = populateBug(this.bug);
        Bug newBug = this.bug;
        if (this.newAttachment != null) {
            Attachment attachment = new Attachment();
            attachment.setName(this.newAttachment.getFileName());
            attachment.setSize(this.newAttachment.getSize());
            attachment.setContentType(this.newAttachment.getContentType());

            byte[] data = new byte[(int) this.newAttachment.getSize()];
            InputStream in = this.newAttachment.getInputStream();
            in.read(data);
            attachment.setData(data);
            newBug.addAttachment(attachment);
        }
        
        bugManager.saveOrUpdate(newBug);
        
        return new RedirectResolution(MultiBugActionBean.class);
    }

    /** Saves or updates a bug, and then returns to the edit page to add another just like it. */
    public Resolution saveAndAgain() throws IOException {
        save();
        return new RedirectResolution(getClass());
    }
}
