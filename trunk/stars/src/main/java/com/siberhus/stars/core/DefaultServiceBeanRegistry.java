package com.siberhus.stars.core;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.config.Configuration;

import com.siberhus.stars.Scope;
import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceBean;
import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils;


public class DefaultServiceBeanRegistry implements ServiceBeanRegistry {
	
	private StarsConfiguration configuration;
	
	public static final String ATTRIBUTE_PREFIX = "_StarsService:";
	
	private final Map<Class<?>, Object> SINGLETON_SERVICE_MAP = new HashMap<Class<?>, Object>();
	
	private final Set<Class<?>> REGISTERED_SERVICE_SET = new HashSet<Class<?>>();
	
	@Override
	public void init(Configuration configuration){
		this.configuration = (StarsConfiguration)configuration;  
	}
	
	@Override
	public synchronized void register(Class<?> serviceClass) {
		if(serviceClass.isInterface() && Modifier.isAbstract(serviceClass.getModifiers())){
			return;
		}
		//Stars
		AnnotatedAttributeUtils.inspectAttribute(Service.class, serviceClass);
		//Persistence
		AnnotatedAttributeUtils.inspectAttribute(PersistenceContext.class, serviceClass);
		AnnotatedAttributeUtils.inspectAttribute(PersistenceUnit.class, serviceClass);
		
		//Standard Annot
		configuration.getLifecycleMethodManager().inspectMethods(serviceClass);
		
		//EJB
//		AnnotatedAttributeUtils.inspectAttribute(Remove.class, serviceClass);
//		AnnotatedAttributeUtils.inspectAttribute(PrePassivate.class, serviceClass);
//		AnnotatedAttributeUtils.inspectAttribute(PostActivate.class, serviceClass);
		
		REGISTERED_SERVICE_SET.add(serviceClass);
		
	}
	
	@Override
	public Object get(HttpServletRequest request, Class<?> implClass)  {
		
		Object service = null;
		
		Scope scope = Scope.Singleton;
		ServiceBean serviceBeanAnnot = implClass.getAnnotation(ServiceBean.class);
		if(serviceBeanAnnot!=null){
			scope = serviceBeanAnnot.scope();
		}
		
		try{
//			if(!infClass.isInterface()){
//				throw new StarsRuntimeException("Service should be referenced via its interface");
//			}
			if(implClass.isInterface() 
					|| Modifier.isAbstract(implClass.getModifiers())){
				// Implementation class must be able to instantiate.
				throw new StarsRuntimeException("Implementation class "
						+implClass.getName()+" must be a concrete class");
			}
//			if(!infClass.isAssignableFrom(implClass)){
//				throw new StarsRuntimeException("Implementation class "
//						+implClass.getName()+" must be a type of "+infClass.getName());
//			}
			
			service = getServiceInScope(request, implClass, scope);
			service = ServiceBeanProxy.newInstance(service);
//			DependencyManager.getInstance().inject(request, implClass, service);
		}catch(StarsRuntimeException e){
			throw e;
		}catch(Exception e){
			throw new StarsRuntimeException(e.toString(), e);
		}
		return service;
	}
	
	private Object getServiceInScope(HttpServletRequest request, Class<?> serviceClass, Scope scope){
		
		if(!REGISTERED_SERVICE_SET.contains(serviceClass)){
			throw new StarsRuntimeException("Service : "+serviceClass.getName()+" has not been registered!!");
		}
		
		Object service = null;
		try{
			if(Scope.Prototype==scope){
				service = createServiceInstance(serviceClass);
			}else if(Scope.Singleton==scope){
				synchronized(DefaultServiceBeanRegistry.class){
					service = SINGLETON_SERVICE_MAP.get(serviceClass);
					if(service == null){
						service = createServiceInstance(serviceClass);
						SINGLETON_SERVICE_MAP.put(serviceClass, service);
					}
				}
			}else{
				String attribName = DefaultServiceBeanRegistry.ATTRIBUTE_PREFIX+serviceClass.getName();
				if(Scope.Request==scope){
					RequestScopeBeanWrapper serviceWrapper = (RequestScopeBeanWrapper)
						request.getAttribute(attribName);
					if(serviceWrapper != null){
						service = serviceWrapper.getBean();
					}else{
						service = createServiceInstance(serviceClass);
						serviceWrapper = new RequestScopeBeanWrapper(service);
						request.setAttribute(attribName, serviceWrapper);
					}
				}else if(Scope.Session==scope){
					SessionScopeBeanWrapper serviceWrapper = (SessionScopeBeanWrapper)
						request.getSession().getAttribute(attribName);
					if(serviceWrapper != null){
						service = serviceWrapper.getBean();
					}else{
						service = createServiceInstance(serviceClass);
						serviceWrapper = new SessionScopeBeanWrapper(service);
						request.getSession().setAttribute(attribName, serviceWrapper);
					}
					
				}
			}
			
		}catch(Exception e){
			throw new StarsRuntimeException("Cannot instantiate service class: "+serviceClass+
					" due to " + e.toString());
		}
		
		if(service==null){
			throw new StarsRuntimeException("Service : "+serviceClass.getName()+" was not found in scope: "+scope);
		}
		
		return service;
	}
	
	private static Object createServiceInstance(Class<?> serviceClass) throws InstantiationException, IllegalAccessException{
		Object service = serviceClass.newInstance();
		
		return service;
	}
}
