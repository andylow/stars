package com.siberhus.stars.security;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Intercepts(LifecycleStage.HandlerResolution)
public class SpringAccessDecisionManagerSecurityInterceptor extends AbstractSpringSecurityInterceptor {
	
	private AccessDecisionManager accessDecisionManager;
	
	@Override
	public void init(Configuration configuration) {
		super.init(configuration);
		accessDecisionManager = super.configuration.getSpringBeanHolder()
			.getApplicationContext().getBean(AccessDecisionManager.class);
	}
	
	@Override
	protected void checkAuthorization(ActionBean actionBean, String eventName, String[] authorities) throws AccessDeniedException{
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		accessDecisionManager.decide(auth, actionBean, 
			SecurityConfig.createList(authorities));
	}
	
}
