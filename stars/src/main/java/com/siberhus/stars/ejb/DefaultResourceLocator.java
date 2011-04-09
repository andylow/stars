package com.siberhus.stars.ejb;

import javax.annotation.Resource.AuthenticationType;
import javax.naming.NamingException;

import com.siberhus.stars.stripes.StarsConfiguration;

import net.sourceforge.stripes.config.Configuration;

public class DefaultResourceLocator implements ResourceLocator {

	protected StarsConfiguration configuration;
	
	protected JndiLocator jndiLocator;
	
	@Override
	public void init(Configuration configuration) throws Exception {
		this.configuration = (StarsConfiguration)configuration;
		this.jndiLocator = this.configuration.getJndiLocator();
	}
	
	@Override
	public Object lookup(String name, String mappedName, Class<?> type,
			AuthenticationType authType, boolean shareable)
			throws NamingException {
		
		if(!"".equals(mappedName)){
			return jndiLocator.lookup(mappedName);
		}
		return jndiLocator.lookup(type);
	}
	
}
