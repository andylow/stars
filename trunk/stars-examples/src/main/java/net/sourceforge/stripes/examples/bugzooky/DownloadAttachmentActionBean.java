package net.sourceforge.stripes.examples.bugzooky;

import java.io.ByteArrayInputStream;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManager;
import net.sourceforge.stripes.examples.bugzooky.biz.BugManagerImpl;
import net.sourceforge.stripes.examples.bugzooky.model.Attachment;
import net.sourceforge.stripes.examples.bugzooky.model.Bug;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.Service;

/**
 * Action that responds to a user's request to download an attachment to a bug.
 *
 * @author Tim Fennell
 */
@UrlBinding("/action/bugzooky/downloadAttachment/${event}/{bugId}")
public class DownloadAttachmentActionBean extends BugzookyActionBean {
	
	@Service(impl=BugManagerImpl.class)
	@Autowired
	@EJB
	private BugManager bugManager;
	
    private Integer bugId;
    private Integer attachmentIndex;

    public Integer getBugId() { return bugId; }
    public void setBugId(Integer bugId) { this.bugId = bugId; }

    public Integer getAttachmentIndex() { return attachmentIndex; }
    public void setAttachmentIndex(Integer attachmentIndex) { this.attachmentIndex = attachmentIndex; }

    @DefaultHandler
    public Resolution getAttachment() {
        Bug bug = bugManager.getBug(this.bugId);
        Attachment attachment = bug.getAttachments().get(this.attachmentIndex);

        // Uses a StreamingResolution to send the file contents back to the user.
        // Note the use of the chained .setFilename() method, which causes the
        // browser to [prompt to] save the "file" instead of displaying it in browser
        return new StreamingResolution
                (attachment.getContentType(), new ByteArrayInputStream(attachment.getData()))
                    .setFilename(attachment.getName());
    }
}
