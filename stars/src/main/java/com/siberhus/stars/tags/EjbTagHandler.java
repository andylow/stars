package com.siberhus.stars.tags;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import com.siberhus.stars.stripes.StarsConfiguration;

public class EjbTagHandler extends ScopedBeanTagSupport{
	
	private Class<?> beanInterface;
	
	private String beanName;
	
	private String lookup;
	
	private String name;
	
	private String mappedName;
	
	@Override
	public int doStartTag() throws JspException {
		ServletContext servletContext = getPageContext().getServletContext();
		StarsConfiguration starsConfig = StarsConfiguration.get(servletContext);
		Object bean = getBean();
		if(bean==null){
			String contextPath = servletContext.getContextPath();
			try {
				starsConfig.getEjbLocator().lookup(contextPath, beanInterface, 
						beanName, lookup, name, mappedName);
			} catch (NamingException e) {
				throw new JspException(e);
			}
			setBean(bean);
		}
		return super.doStartTag();
	}
	
	
	public Class<?> getBeanInterface() {
		return beanInterface;
	}


	public void setBeanInterface(Class<?> beanInterface) {
		this.beanInterface = beanInterface;
	}


	public String getBeanName() {
		return beanName;
	}


	public void setBeanName(String beanName) {
		if (getId() == null) {
			setId(beanName);
		}
		this.beanName = beanName;
	}


	public String getLookup() {
		return lookup;
	}


	public void setLookup(String lookup) {
		this.lookup = lookup;
	}


	public String getMappedName() {
		return mappedName;
	}


	public void setMappedName(String mappedName) {
		this.mappedName = mappedName;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
