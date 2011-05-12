package com.siberhus.stars.model;

import java.io.Serializable;
import java.util.Date;


public interface LinkableAuditableModel <ID extends Serializable, USER extends Model<ID>> extends Model<ID>{
	
	USER getCreatedBy();
	
	void setCreatedBy(final USER createdBy);
	
	Date getCreatedAt();
	
	void setCreatedAt(final Date creationDate);
	
	USER getLastModifiedBy();
	
	void setLastModifiedBy(final USER lastModifiedBy);
	
	Date getLastModifiedAt();
	
	void setLastModifiedAt(final Date lastModifiedDate);
	
}
