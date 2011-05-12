package com.siberhus.stars.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class AbstractLinkableAuditableModel <ID extends Serializable, USER extends Model<ID>>
	extends AbstractModel<ID> implements LinkableAuditableModel<ID,USER> {
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="CREATED_BY",referencedColumnName="ID")
	private USER createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_AT")
	private Date createdAt;
	
	@ManyToOne
	@JoinColumn(name="LAST_MODIFIED_BY",referencedColumnName="ID")
	private USER lastModifiedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_MODIFIED_AT")
	private Date lastModifiedAt;
	
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public USER getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(USER createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public USER getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(USER lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	
}
