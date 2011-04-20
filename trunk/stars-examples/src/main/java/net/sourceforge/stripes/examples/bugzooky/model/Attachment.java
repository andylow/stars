package net.sourceforge.stripes.examples.bugzooky.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Very simple wrapper for file attachments uploaded for bugs.  Assumes that the attachment
 * contains some type of textual data.
 *
 * @author Tim Fennell
 */
@Entity
public class Attachment {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
    private String name;
    private long size;
    private byte[] data;
    private String contentType;
    @ManyToOne
    private Bug bug;
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getPreview() {
        if (contentType.startsWith("text")) {
            int amount = Math.min(data.length, 30);
            return new String(data, 0, amount);
        }
        else {
            return "[Binary File]";
        }
    }
	public Bug getBug() {
		return bug;
	}
	public void setBug(Bug bug) {
		this.bug = bug;
	}
    
    
}
