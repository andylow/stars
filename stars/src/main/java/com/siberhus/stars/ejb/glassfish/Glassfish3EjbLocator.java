package com.siberhus.stars.ejb.glassfish;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.stripes.config.Configuration;

import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.ejb.DefaultEjbLocator;

public class Glassfish3EjbLocator extends DefaultEjbLocator {

	private static final Map<Class<?>, String> GLOBAL_EJB_JNDI_MAP = new ConcurrentHashMap<Class<?>, String>();
	
	@Override
	public void init(Configuration configuration) throws Exception {
		super.init(configuration);
		updateLocalJndiMap(jndiLocator.getContext(), "");
	}
	
	@Override
	public Object lookup(String contextPath, Class<?> beanInterface,
			String beanName, String lookup, String name, String mappedName)
			throws NamingException {
		if(!"".equals(lookup)){
			return jndiLocator.lookup(lookup);
		}
		return GLOBAL_EJB_JNDI_MAP.get(beanInterface);
	}
	
	private void updateLocalJndiMap(Context ctx, String parent) {
		try {
			NamingEnumeration<Binding> list = ctx.listBindings("");
			while (list.hasMore()) {
				Binding item = list.next();
				String className = item.getClassName();
				String name = item.getName();
				if ("com.sun.enterprise.naming.impl.TransientContext"
						.equals(className)) {
					parent = name;
				} else if ("com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy".equals(className)
						|| "javax.naming.Reference".equals(className)) {
					int startIdx = name.indexOf("!");
					if(startIdx!=-1){
						name = name.substring(startIdx+1, name.length());
						GLOBAL_EJB_JNDI_MAP.put(Class.forName(name), "java:global/"+name);
					}
				}
				Object o = item.getObject();
				if (o instanceof javax.naming.Context) {
					updateLocalJndiMap((Context) o, parent + "/");
				}
			}
		} catch (NamingException e) {
			throw new StarsRuntimeException(e);
		} catch (ClassNotFoundException e){
			throw new StarsRuntimeException(e);
		}
	}
	
}
