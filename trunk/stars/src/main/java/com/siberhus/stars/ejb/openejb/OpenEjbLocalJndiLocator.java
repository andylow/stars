package com.siberhus.stars.ejb.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.siberhus.stars.ejb.DefaultJndiLocator;

public class OpenEjbLocalJndiLocator extends DefaultJndiLocator {
	
	@Override
	public void initialContext(Properties props) throws NamingException {
		
		System.getProperties().put("openejb.jndiname.format", getDefaultJndiNameFormat());
		
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		context = new InitialContext(props);
		
	}
	
	protected String getDefaultJndiNameFormat(){
		//"{interfaceClass}/{ejbClass}"
		return "{interfaceClass}";
	}
	
	
}
