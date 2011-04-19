package com.siberhus.stars.tags;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import net.sourceforge.stripes.util.ReflectUtil;

import com.siberhus.stars.stripes.StarsConfiguration;

public class StarsServiceTagHandler extends ScopedBeanTagSupport{
	
	private String serviceBean;
	
	@Override
	public int doStartTag() throws JspException {
		ServletContext servletContext = getPageContext().getServletContext();
		StarsConfiguration starsConfig = StarsConfiguration.get(servletContext);
		Object bean = getBean();
		if(bean==null){
			try {
				Class<?> serviceBeanClass = ReflectUtil.findClass(serviceBean) ;
				bean = starsConfig.getServiceBeanRegistry().get(
					(HttpServletRequest)getPageContext().getRequest(), serviceBeanClass);
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
		this.serviceBean = null;
	}

	public String getServiceBean() {
		return serviceBean;
	}

	public void setServiceBean(String serviceBean) {
		this.serviceBean = serviceBean;
	}
	
	
}
