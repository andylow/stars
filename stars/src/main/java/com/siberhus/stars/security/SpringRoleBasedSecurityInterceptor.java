package com.siberhus.stars.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@Intercepts(LifecycleStage.HandlerResolution)
public class SpringRoleBasedSecurityInterceptor extends AbstractSpringSecurityInterceptor {
	
	@Override
	protected void checkAuthorization(ActionBean actionBean, String eventName, String[] authorities) throws AccessDeniedException{
		
		final Collection<GrantedAuthority> granted = getPrincipalAuthorities();
		log.debug("Granted authorities: {}", granted);
		
		final Set<GrantedAuthority> requiredAuthorities = new HashSet<GrantedAuthority>();
		requiredAuthorities.addAll(AuthorityUtils.createAuthorityList(authorities));
		Set<GrantedAuthority> grantedCopy = retainAll(granted, requiredAuthorities);
		if (grantedCopy.isEmpty()) {
			throw new AccessDeniedException("You don't have permission to invoke : "
				+actionBean.getClass().getName()+"."+eventName+"()");
		}
	}
	
	private Collection<GrantedAuthority> getPrincipalAuthorities() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        
        if (null == currentUser) {
            return Collections.emptyList();
        }
        
        return currentUser.getAuthorities();
    }
	
	private Set<GrantedAuthority> retainAll(final Collection<GrantedAuthority> granted, final Set<GrantedAuthority> required) {
		Set<String> grantedRoles = authoritiesToRoles(granted);
		Set<String> requiredRoles = authoritiesToRoles(required);
		grantedRoles.retainAll(requiredRoles);
		return rolesToAuthorities(grantedRoles, granted);
	}
	
	private Set<String> authoritiesToRoles(Collection<GrantedAuthority> c) {
		Set<String> target = new HashSet<String>();
		for (GrantedAuthority authority : c) {
			if (null == authority.getAuthority()) {
				throw new IllegalArgumentException(
						"Cannot process GrantedAuthority objects which return null from getAuthority() - attempting to process "
						+ authority.toString());
			}
			target.add(authority.getAuthority());
		}
		return target;
    }
	
	private Set<GrantedAuthority> rolesToAuthorities(Set<String> grantedRoles, Collection<GrantedAuthority> granted) {
		Set<GrantedAuthority> target = new HashSet<GrantedAuthority>();
		for (String role : grantedRoles) {
			for (GrantedAuthority authority : granted) {
				if (authority.getAuthority().equals(role)) {
					target.add(authority);
					break;
				}
			}
		}
		return target;
	}
	
}
