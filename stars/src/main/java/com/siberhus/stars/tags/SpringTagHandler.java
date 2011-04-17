package com.siberhus.stars.tags;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.siberhus.stars.stripes.StarsConfiguration;

public class SpringTagHandler extends ScopedBeanTagSupport {

	private String name;

	private Class<?> type;
	
	@Override
	public int doStartTag() throws JspException {
		ServletContext servletContext = getPageContext().getServletContext();
		StarsConfiguration starsConfig = StarsConfiguration.get(servletContext);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (getId() == null) {
			setId(name);
		}
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

}
