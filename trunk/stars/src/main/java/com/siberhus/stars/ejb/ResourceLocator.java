package com.siberhus.stars.ejb;

import javax.annotation.Resource.AuthenticationType;
import javax.naming.NamingException;

import net.sourceforge.stripes.config.ConfigurableComponent;

/**
 * <b>JBOSS</b>
 * <p>@Resource(mappedName="java:/DefaultDS") DataSource ds;</p>
 * <p>@Resource(mappedName="java:/ConnectionFactory") ConnectionFactory factory;</p>
 * @author hussachai
 *
 */
public interface ResourceLocator extends ConfigurableComponent{
	
	/**
	 * 
	 * @param name The JNDI name of the resource. For field annotations, the default is 
	 * the field name. For method annotations, the default is the JavaBeans property name 
	 * corresponding to the method. For class annotations, there is no default and 
	 * this must be specified. 
	 * @param mappedName
	 * @param type
	 * @param authType
	 * @param shareable
	 * @return
	 * @throws NamingException
	 */
	public Object lookup(String name, String mappedName, Class<?> type, 
			AuthenticationType authType, boolean shareable) throws NamingException;
}
