package com.siberhus.stars.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.shiro.SecurityUtils;

@MappedSuperclass
public abstract class ShiroAuditableModel <ID extends Serializable> extends AbstractAuditableModel<ID> {
	
	private static final long serialVersionUID = 1L;
	
	@PrePersist
	protected void prePersist(){
		setCreatedBy(getPrincipalName());
		setCreatedAt(new Date());
	}
	
	@PreUpdate
	protected void preUpdate(){
		setLastModifiedBy(getPrincipalName());
		setLastModifiedAt(new Date());
	}
	
	protected String getPrincipalName(){
		Object principal = SecurityUtils.getSubject().getPrincipal();
		if(principal!=null){
			return principal.toString();
		}
		return null;
	}
	
}
