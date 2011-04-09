package com.siberhus.stars.ejb;

import javax.naming.NamingException;

import net.sourceforge.stripes.config.ConfigurableComponent;

public interface EjbLocator extends ConfigurableComponent{
	
	/**
	 * @param contextPath
	 * EJB/Web context path
	 * @param beanInterface
	 * Holds one of the following types of the target EJB : [ Local business interface, bean class (for no-interface view), Remote business interface, Local Home interface, Remote Home interface ]
	 * @param beanName
	 * The ejb-name of the Enterprise Java Bean to which this reference is mapped.
	 * @param lookup
	 * A portable lookup string containing the JNDI name for the target EJB component.
	 * @param name
	 * The logical name of the ejb reference within the declaring component's (e.g., java:comp/env) environment
	 * @param mappedName
	 * The product specific name of the EJB component to which this ejb reference should be mapped.
	 * 
	 * @return
	 * @throws NamingException
	 */
	public Object lookup(String contextPath, Class<?> beanInterface, String beanName, String lookup, String name, String mappedName) throws NamingException;
	
}
