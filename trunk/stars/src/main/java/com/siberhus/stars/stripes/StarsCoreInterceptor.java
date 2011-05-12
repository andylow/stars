package com.siberhus.stars.stripes;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContextAware;

import com.siberhus.stars.ActionBeanNotFoundException;
import com.siberhus.stars.ServiceProvider;

@Intercepts({
	LifecycleStage.ActionBeanResolution, 
	LifecycleStage.HandlerResolution,
	LifecycleStage.RequestComplete
})
public class StarsCoreInterceptor implements Interceptor, ConfigurableComponent {
	
	private final Logger log = LoggerFactory.getLogger(StarsCoreInterceptor.class);
	
	public static final String VIEW_PATH_PREFIX = "View.PathPrefix";
	
	public static final String VIEW_PATH_SUFFIX = "View.PathSuffix";
	
	private String viewPathPrefix;
	private String viewPathSuffix;
	
	private StarsConfiguration configuration;
	
	@Override
	public void init(Configuration configuration) throws Exception {
		this.configuration = (StarsConfiguration)configuration;
		viewPathPrefix = configuration.getBootstrapPropertyResolver().getProperty(VIEW_PATH_PREFIX);
		viewPathPrefix = viewPathPrefix!=null?viewPathPrefix:"";
		viewPathSuffix = configuration.getBootstrapPropertyResolver().getProperty(VIEW_PATH_SUFFIX);
		viewPathSuffix = viewPathSuffix!=null?viewPathSuffix:"";
	}
	
	@Override
	public Resolution intercept(ExecutionContext context) throws Exception {
		ActionBeanContext actionBeanContext = context.getActionBeanContext();
		Resolution resolution = context.proceed();
		ActionBean actionBean = context.getActionBean();
		if(actionBean==null){
			HttpServletRequest req = actionBeanContext.getRequest();
			String uri = req.getContextPath() + req.getServletPath();
			throw new ActionBeanNotFoundException("ActionBean not found for url "+uri);
		}
		Class<? extends ActionBean> actionBeanClass = actionBean.getClass();
		String urlBinding = configuration.getActionResolver().getUrlBinding(actionBean.getClass());
		log.debug("URL Binding for class: {} is {}",new Object[]{actionBeanClass,urlBinding});
		actionBeanContext.getRequest().setAttribute("actionBeanClass", actionBeanClass.getName());
		actionBeanContext.getRequest().setAttribute("actionBeanUrl", urlBinding);
		
		switch (context.getLifecycleStage()) {
		case ActionBeanResolution:
			//Inject
			configuration.getDependencyManager()
				.inject(actionBeanContext.getRequest(), actionBean);
			if(ServiceProvider.SPRING==configuration.getServiceProvider()){
				if(actionBean instanceof ApplicationContextAware){
					((ApplicationContextAware)actionBean).setApplicationContext(
						configuration.getSpringBeanHolder().getApplicationContext());
				}else if(actionBean instanceof BeanFactoryAware){
					((BeanFactoryAware)actionBean).setBeanFactory(
						configuration.getSpringBeanHolder().getApplicationContext());
				}
			}
			if(resolution instanceof ForwardResolution){
				ForwardResolution fwdRes = (ForwardResolution)resolution;
				fwdRes.setPath(viewPathPrefix+fwdRes+viewPathSuffix);
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
