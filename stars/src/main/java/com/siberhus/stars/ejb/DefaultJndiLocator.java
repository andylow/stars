package com.siberhus.stars.ejb;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sourceforge.stripes.config.Configuration;

import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.stripes.StarsConfiguration;

public class DefaultJndiLocator implements JndiLocator {
	
	protected StarsConfiguration configuration;
	
	protected Map<Class<?>,String> localJndiMap;
	
	protected Context context;
	
	@Override
	public void init(Configuration configuration) throws Exception {
		this.configuration = (StarsConfiguration)configuration;
		localJndiMap = this.configuration.getDefaultJndiMap();
	}
	
	@Override
	public Context getContext() {
		return context;
	}
	
	@Override
	public void initialContext(Properties props) throws NamingException {
		context = new InitialContext(props);
	}
	
	@Override
	public Object lookup(String jndiName) throws NamingException {
		try{
			return context.lookup(jndiName);
		}catch(NamingException e){
			throw new StarsRuntimeException("Unable to find a resource with name [" + jndiName
				+ "] in the initial context.");
		}
	}
	
	@Override
	public Object lookup(Class<?> clazz) throws NamingException {
		String jndi = localJndiMap.get(clazz);
		if(jndi!=null){
			return lookup(jndi); 
		}else{
			return lookup(clazz.getName());
		}
	}

	protected Map<Class<?>,String> getLocalJndiMap(){
		return localJndiMap;
	}
	
}
