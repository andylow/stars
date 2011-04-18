package com.siberhus.stars.tags;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import net.sourceforge.stripes.util.ReflectUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringTagHandler extends ScopedBeanTagSupport {

	private String name;

	private Class<?> type;
	
	@Override
	public int doStartTag() throws JspException {
		ServletContext servletContext = getPageContext().getServletContext();
		Object bean = getBean();
		if(bean==null){
			ApplicationContext springContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			if (name != null) {
				bean = springContext.getBean(name);
			} else if (type != null) {
				bean = springContext.getBean(type);
			} else {
				throw new IllegalArgumentException(
						"name or type attribute is required!");
			}
			setBean(bean);
		}
		return super.doStartTag();
	}

	@Override
	public void release() {
		super.release();
		this.name = null;
		this.type = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}
	
	public void setType(String type) throws ClassNotFoundException{
		this.type = ReflectUtil.findClass(type);
	}
	
}
