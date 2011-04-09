package com.siberhus.stars.stripes;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContexts;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnits;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.utils.AnnotatedAttributeUtils;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

@Intercepts(LifecycleStage.ActionBeanResolution)
public class StarsCoreInterceptor implements Interceptor, ConfigurableComponent {
	
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
		
		Class<? extends ActionBean> actionBeanClass = actionBean.getClass();
		List<AnnotatedAttribute> annotAttrList = AnnotatedAttributeUtils
			.getAnnotatedAttributes(actionBeanClass);
		if (annotAttrList != null) {
			for (AnnotatedAttribute annotAttr : annotAttrList) {
				Annotation annot = annotAttr.getAnnotation();
				Class<?> annotType = annotAttr.getType();
				
			}
		}
		
		return resolution;
	}
	
	static Class[] a = {
		DeclareRoles.class, //Type
		EJB.class, //Type, Field, Method
		EJBs.class, //Type 
		Resource.class, //Type, Field, Method
		Resources.class, //Type
		PersistenceContext.class, //Type, Field, Method
		PersistenceContexts.class, //Type
		PersistenceUnit.class, //Type, Field, Method
		PersistenceUnits.class, //Type
//		PostConstruct.class,
//		PreDestroy.class,
		RunAs.class, //Type
		WebServiceRef.class, //Type, Field, Method
		WebServiceRefs.class //Type
	};
	
	public static void main(String[] args) {
		System.out.println(StarsCoreInterceptor.class.getAnnotation(Intercepts.class).annotationType()==Intercepts.class);
	}

	

	
}
