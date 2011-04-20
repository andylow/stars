package com.siberhus.stars.tags;

import java.lang.reflect.Proxy;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import net.sourceforge.stripes.util.ReflectUtil;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.core.ServiceBeanProxy;
import com.siberhus.stars.stripes.StarsConfiguration;

public class StarsServiceTagHandler extends ScopedBeanTagSupport{
	
	private String serviceBean;
	
	@Override
	public int doStartTag() throws JspException {
		ServletContext servletContext = getPageContext().getServletContext();
		StarsConfiguration starsConfig = StarsConfiguration.get(servletContext);
		if(ServiceProvider.STARS!=starsConfig.getServiceProvider()){
			return SKIP_BODY;
		}
		Object bean = getBean();
		if(bean==null){
			try {
				Class<?> serviceBeanClass = ReflectUtil.findClass(serviceBean) ;
				bean = starsConfig.getServiceBeanRegistry().get(
					(HttpServletRequest)getPageContext().getRequest(), serviceBeanClass);
				bean = ServiceBeanProxy.getRealObject((Proxy)bean);
				
				starsConfig.getDependencyManager().inject(
					(HttpServletRequest)getPageContext().getRequest(), bean);
				
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
