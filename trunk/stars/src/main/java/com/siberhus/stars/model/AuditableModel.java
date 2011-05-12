package com.siberhus.stars.model;

import java.io.Serializable;
import java.util.Date;

public interface AuditableModel  <ID extends Serializable> extends Model<ID>{

	String getCreatedBy();
	
	void setCreatedBy(final String createdBy);
	
	Date getCreatedAt();
	
	void setCreatedAt(final Date creationDate);
	
	String getLastModifiedBy();
	
	void setLastModifiedBy(final String lastModifiedBy);
	
	Date getLastModifiedAt();
	
	void setLastModifiedAt(final Date lastModifiedDate);
	
}
