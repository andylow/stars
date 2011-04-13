package com.siberhus.stars.stripes;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.util.Log;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.siberhus.stars.ServiceProvider;

@Intercepts(LifecycleStage.ActionBeanResolution)
public class StarsCoreInterceptor implements Interceptor, ConfigurableComponent {
	
	private Log log = Log.getInstance(getClass());
	
	private StarsConfiguration configuration;
	
	@Override
	public void init(Configuration configuration) throws Exception {
		this.configuration = (StarsConfiguration)configuration;
	}
	
	@Override
	public Resolution intercept(ExecutionContext context) throws Exception {
		ActionBeanContext actionBeanContext = context.getActionBeanContext();
		Resolution resolution = context.proceed();
		ActionBean actionBean = context.getActionBean();
		Class<? extends ActionBean> actionBeanClass = actionBean.getClass();
		String urlBinding = configuration.getActionResolver().getUrlBinding(actionBean.getClass());
		log.debug("URL Binding for class: ",actionBeanClass, " is ",urlBinding);
		actionBeanContext.getRequest().setAttribute("actionBeanClass", actionBeanClass.getName());
		actionBeanContext.getRequest().setAttribute("actionBeanUrl", urlBinding);
		
		switch (context.getLifecycleStage()) {
		case ActionBeanResolution:
			//Inject
			configuration.getDependencyManager()
				.inject(actionBeanContext.getRequest(), actionBean);
			if(configuration.getServiceProvider()==ServiceProvider.SPRING){
				HttpServletRequest request = context.getActionBeanContext().getRequest();
				if(actionBean instanceof ApplicationContextAware){
					((ApplicationContextAware)actionBean).setApplicationContext(WebApplicationContextUtils
							.getRequiredWebApplicationContext(request.getServletContext()));
				}else if(actionBean instanceof BeanFactoryAware){
					((BeanFactoryAware)actionBean).setBeanFactory(WebApplicationContextUtils
						.getRequiredWebApplicationContext(request.getServletContext()));
				}
			}
			break;
		case HandlerResolution:
			//PostConstruct
			configuration.getLifecycleMethodManager()
				.invokePostConstructMethod(actionBean);
			break;
		case RequestComplete:
			//PreDestroy
			configuration.getLifecycleMethodManager()
				.invokePreDestroyMethod(actionBean);
			break;
		}
		
		return resolution;
	}
	
}
