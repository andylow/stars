package com.siberhus.stars.core;

import net.sourceforge.stripes.config.ConfigurableComponent;

public interface LifecycleMethodManager extends ConfigurableComponent{
	
	
	public void inspectMethods(Class<?> targetClass);
	
	public void invokePostConstructMethod(Object object);
	
	public void invokePreDestroyMethod(Object object);
	
	
}
