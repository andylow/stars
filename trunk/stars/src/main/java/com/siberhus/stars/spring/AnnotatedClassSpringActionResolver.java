package com.siberhus.stars.spring;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.AnnotatedClassActionResolver;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AnnotatedClassSpringActionResolver extends AnnotatedClassActionResolver {
	
	private ApplicationContext applicationContext;
	
	public void init(Configuration configuration) throws Exception{
		applicationContext = WebApplicationContextUtils
			.getRequiredWebApplicationContext(configuration.getServletContext());
		super.init(configuration);
	}
	
	protected ActionBean makeNewActionBean(Class<? extends ActionBean> type, ActionBeanContext context) throws Exception {
		
		return applicationContext.getBean(type);
	}
	
}
