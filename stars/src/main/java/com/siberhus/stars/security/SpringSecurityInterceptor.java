package com.siberhus.stars.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.siberhus.stars.Environment;
import com.siberhus.stars.stripes.StarsConfiguration;

@Intercepts(LifecycleStage.HandlerResolution)
public class SpringSecurityInterceptor implements Interceptor, ConfigurableComponent {
	
	private final Logger log = LoggerFactory.getLogger(SpringSecurityInterceptor.class);
	
	private static final String NAME = SpringSecurityInterceptor.class.getName();
	
	private StarsConfiguration configuration;
	
	private Map<Class<? extends ActionBean>, AuthzDetail> authzDetailMap 
		= new HashMap<Class<? extends ActionBean>, AuthzDetail>();
	
	static class AuthzDetail {
		String[] defaultAuthorities;
		//Map of eventName and Authorities
		Map<String, String[]> authoritiesMap = new HashMap<String, String[]>();
	}
	
	public SpringSecurityInterceptor(){
		Environment.initReloadable(NAME);
	}
	
	public void requestReloading(){
		if(Environment.isReloadingRequested(NAME)){
			authzDetailMap.clear();
			init(configuration);
		}
	}
	
	@Override
	public void init(Configuration configuration) {
		this.configuration = (StarsConfiguration)configuration;
		Collection<Class<? extends ActionBean>> actionBeanClasses 
			= configuration.getActionResolver().getActionBeanClasses();
		for(Class<? extends ActionBean> actionBeanClass: actionBeanClasses){
			AuthzDetail authzDetail = new AuthzDetail();
			Secured securedAnnot = (Secured)actionBeanClass.getAnnotation(Secured.class);
			if(securedAnnot!=null){
				authzDetail.defaultAuthorities = securedAnnot.value();
			}
			for(Method method : actionBeanClass.getDeclaredMethods()){
				if(Resolution.class.isAssignableFrom(method.getReturnType())){
					securedAnnot = (Secured)method.getAnnotation(Secured.class);
					if(securedAnnot!=null){
						authzDetail.authoritiesMap
							.put(method.getName(), securedAnnot.value());
					}
				}
			}
			authzDetailMap.put(actionBeanClass, authzDetail);
		}
		
		if(log.isDebugEnabled()){
			log.debug("===========================================");
			for(Class<? extends ActionBean> ab: authzDetailMap.keySet()){
				log.debug("Authorization setting for : {}", ab.getName());
				AuthzDetail authzDetail = authzDetailMap.get(ab);
				log.debug("Default authorities: {}", Arrays.toString(authzDetail.defaultAuthorities));
				for(String event: authzDetail.authoritiesMap.keySet()){
					String authorities[] = authzDetail.authoritiesMap.get(event);
					log.debug("Event: {}, Authorities: {}", 
						new Object[]{event, Arrays.toString(authorities)});
				}
				log.debug("===========================================");
			}
		}
	}
	
	@Override
	public Resolution intercept(ExecutionContext context) throws Exception {
		requestReloading();
		Resolution resolution = context.proceed();
		if(LifecycleStage.HandlerResolution==context.getLifecycleStage()){
			Class<? extends ActionBean> actionBeanClass = context.getActionBean().getClass();
			checkAuthorization(actionBeanClass, 
				context.getActionBeanContext().getEventName());
		}
		return resolution;
	}
	
	
	protected void checkAuthorization(Class<? extends ActionBean> actionBeanClass, String eventName) throws AccessDeniedException{
		log.debug("Checking authoriztion detail for {}.{}() ",
			new Object[]{actionBeanClass,eventName});
		AuthzDetail authzDetail = authzDetailMap.get(actionBeanClass);
		
		String authorities[] = authzDetail.authoritiesMap.get(eventName);
		if(authorities==null || authorities.length==0){
			authorities = authzDetail.defaultAuthorities;
			if(authorities==null || authorities.length==0){
				log.debug("Authorities not found!!!");
				return;//TODO: throws exception or grant access?
			}
		}
		
		final Collection<GrantedAuthority> granted = getPrincipalAuthorities();
		log.debug("Granted authorities: {}", granted);
		
		final Set<GrantedAuthority> requiredAuthorities = new HashSet<GrantedAuthority>();
		requiredAuthorities.addAll(AuthorityUtils.createAuthorityList(authorities));
		Set<GrantedAuthority> grantedCopy = retainAll(granted, requiredAuthorities);
		if (grantedCopy.isEmpty()) {
			throw new AccessDeniedException("You don't have permission to invoke : "
				+actionBeanClass.getName()+"."+eventName+"()");
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
