package com.siberhus.stars.ejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import net.sourceforge.stripes.config.ConfigurableComponent;

public interface JndiLocator extends ConfigurableComponent{
	
	public Context getContext();
	
	public Context initialContext(Properties properties) throws NamingException;
	
	public Object lookup(String jndiName) throws NamingException;
	
	public Object lookup(Class<?> clazz) throws NamingException;
}
