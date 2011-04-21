package com.siberhus.stars.ejb.glassfish;

import javax.transaction.UserTransaction;

import net.sourceforge.stripes.config.Configuration;

import com.siberhus.stars.ejb.DefaultJndiLocator;

public class Glassfish3JndiLocator extends DefaultJndiLocator {

	@Override
	public void init(Configuration configuration) throws Exception {
		super.init(configuration);
		getLocalJndiMap().put(UserTransaction.class, "java:comp/UserTransaction");
	}
	
	
	
}
