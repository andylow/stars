package com.siberhus.stars.core;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.config.ConfigurableComponent;

public interface ServiceBeanRegistry extends ConfigurableComponent{
	
	public void register(Class<?> serviceClass);
	
	public Object get(HttpServletRequest request, Class<?> infClass, Class<?> implClass);
	
}
