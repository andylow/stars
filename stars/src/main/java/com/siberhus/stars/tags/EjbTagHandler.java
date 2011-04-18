package com.siberhus.stars.tags;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import net.sourceforge.stripes.util.ReflectUtil;

import com.siberhus.stars.stripes.StarsConfiguration;

public class EjbTagHandler extends ScopedBeanTagSupport{
	
	private String beanInterface;
	
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
				Class<?> beanInterfaceClass = ReflectUtil.findClass(beanInterface) ;
				starsConfig.getEjbLocator().lookup(contextPath, beanInterfaceClass, 
						beanName, lookup, name, mappedName);
			} catch (Exception e) {
				throw new JspException(e);
			}
			setBean(bean);
		}
		return super.doStartTag();
	}
	
	@Override
	public void release(){
		super.release();
		this.beanInterface = null;
		this.beanName = null;
		this.lookup = null;
		this.name = null;
		this.mappedName = null;
	}
	
	
	public String getBeanInterface() {
		return beanInterface;
	}

	public void setBeanInterface(String beanInterface) {
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
