package com.siberhus.stars.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@MappedSuperclass
public abstract class SpringAuditableModel<ID extends Serializable> extends AbstractAuditableModel<ID> {
	
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
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication auth = securityContext.getAuthentication();
		if(auth!=null){
			return auth.getName();
		}
		return null;
	}
}
