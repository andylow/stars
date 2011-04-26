package com.siberhus.stars.core;

import javax.servlet.http.HttpServletRequest;

import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

public interface ResourceInjector {

	public void init(StarsConfiguration configuration);
	
	public void inspectAttributes(Class<?> targetClass);
	
	public void inject(HttpServletRequest request, AnnotatedAttribute annotAttr, Object targetObj) throws Exception;
	
}
