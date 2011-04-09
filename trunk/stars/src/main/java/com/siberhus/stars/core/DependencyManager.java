package com.siberhus.stars.core;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.config.ConfigurableComponent;

public interface DependencyManager extends ConfigurableComponent{
	
	public void inspectAttributes(Class<?> targetClass);
	
	public void inject(HttpServletRequest request, Object targetObj) throws Exception;
	
}
