package com.siberhus.stars.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;

import com.siberhus.stars.Environment;
import com.siberhus.stars.stripes.StarsConfiguration;


public abstract class AbstractSpringSecurityInterceptor implements Interceptor, ConfigurableComponent {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String NAME = AbstractSpringSecurityInterceptor.class.getName();
	
	protected StarsConfiguration configuration;
	
	protected Map<Class<? extends ActionBean>, AuthzDetail> authzDetailMap 
		= new HashMap<Class<? extends ActionBean>, AuthzDetail>();
	
	protected static class AuthzDetail {
		String[] defaultAuthorities;
		//Map of eventName and Authorities
		Map<String, String[]> authoritiesMap = new HashMap<String, String[]>();
	}
	
	public AbstractSpringSecurityInterceptor(){
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
			ActionBean actionBean = context.getActionBean();
			Class<? extends ActionBean> actionBeanClass = actionBean.getClass();
			String eventName = context.getActionBeanContext().getEventName();
			log.debug("Checking authoriztion detail for {}.{}() ",
				new Object[]{actionBeanClass,eventName});
			AuthzDetail authzDetail = authzDetailMap.get(actionBeanClass);
			
			String authorities[] = authzDetail.authoritiesMap.get(eventName);
			if(authorities==null || authorities.length==0){
				authorities = authzDetail.defaultAuthorities;
				if(authorities==null || authorities.length==0){
					log.debug("Authorities not found!!!");
					return resolution;//TODO: throws exception or grant access?
				}
			}
			
			checkAuthorization(actionBean, eventName, authorities);
		}
		return resolution;
	}
	
	abstract void checkAuthorization(ActionBean actionBean, String eventName, String[] authorities);
	
}
