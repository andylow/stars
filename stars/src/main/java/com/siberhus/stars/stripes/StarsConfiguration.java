package com.siberhus.stars.stripes;

import java.util.Collection;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.servlet.ServletContext;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.config.RuntimeConfiguration;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.exception.ExceptionHandler;
import net.sourceforge.stripes.util.ReflectUtil;
import net.sourceforge.stripes.util.ResolverUtil;
import net.sourceforge.stripes.util.StringUtil;
import net.sourceforge.stripes.validation.TypeConverterFactory;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import com.siberhus.org.stripesstuff.stripersist.EntityFormatter;
import com.siberhus.org.stripesstuff.stripersist.EntityTypeConverter;
import com.siberhus.org.stripesstuff.stripersist.Stripersist;
import com.siberhus.stars.Environment;
import com.siberhus.stars.ServiceBean;
import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsBootstrap;
import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.core.BootstrapInvoker;
import com.siberhus.stars.core.CoreExceptionHandler;
import com.siberhus.stars.core.CoreExceptionHandlerProxy;
import com.siberhus.stars.core.DefaultDependencyManager;
import com.siberhus.stars.core.DefaultLifecycleMethodManager;
import com.siberhus.stars.core.DefaultServiceBeanRegistry;
import com.siberhus.stars.core.DependencyManager;
import com.siberhus.stars.core.LifecycleMethodManager;
import com.siberhus.stars.core.ServiceBeanRegistry;
import com.siberhus.stars.ejb.DefaultEjbLocator;
import com.siberhus.stars.ejb.DefaultJndiLocator;
import com.siberhus.stars.ejb.DefaultResourceLocator;
import com.siberhus.stars.ejb.EjbLocator;
import com.siberhus.stars.ejb.JndiLocator;
import com.siberhus.stars.ejb.ResourceLocator;
import com.siberhus.stars.security.SpringAccessDecisionManagerSecurityInterceptor;
import com.siberhus.stars.spring.SpringBeanHolder;
import com.siberhus.stars.validation.StarsTypeConverterFactory;

public class StarsConfiguration extends RuntimeConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(StarsConfiguration.class);
	
	private static final String NAME = StarsConfiguration.class.getName();
	
	public static final String ROOT_STARS_CONFIG_CONTEXT_ATTRIBUTE = StarsConfiguration.class.getName()+".ROOT";
	
	/** The Configuration Key for looking up the name of the DependencyManager class. */
	public static final String BOOTSTRAPS = "Bootstrap.Classes";
	
	/** The Configuration Key for looking up the name of the DependencyManager class. */
	public static final String DEPENDECY_MANAGER = "DependencyManager.Class";
	
	/** The Configuration Key for looking up the name of the LifecycleMethodManager class. */
	public static final String LIFECYCLE_METHOD_MANAGER = "LifecycleMethodManager.Class";
	
	/** The Configuration Key for looking up the name of the LifecycleMethodManager class. */
	public static final String SERVICE_BEAN_REGISTRY = "ServiceBeanRegistry.Class";
	
	public static final String SERVICE_RESOLVER_PACKAGES = "ServiceResolver.Packages";	
	
	/** The Configuration Key for looking up the name of the JndiLocator class. */
	public static final String JNDI_LOCATOR = "JndiLocator.Class";
	
	/** The Configuration Key for looking up the name of the EjbLocator class. */
	public static final String EJB_LOCATOR = "EjbLocator.Class";
	
	/** The Configuration Key for looking up the name of the EjbLocator class. */
	public static final String RESOURCE_LOCATOR = "ResourceLocator.Class";
	
	public static final String SERVICE_PROVIDER = "Service.Provider";
	
	public static final String PAGE_ENCODING = "Page.Encoding";
	
	private DependencyManager dependencyManager;
	
	private LifecycleMethodManager lifecycleMethodManager;
	
	private ServiceBeanRegistry serviceBeanRegistry;
	
	private ServiceProvider serviceProvider = ServiceProvider.STARS;
	
	//JNDI
	private JndiLocator jndiLocator;
	
	private ResourceLocator resourceLocator;	
	
	//EJB
	private EjbLocator ejbLocator;
	
	//SPRING
	private SpringBeanHolder springBeanHolder;
	
	private String pageEncoding = "UTF-8";
	
	static {
		Package pkg = StarsConfiguration.class.getPackage();
		log.info("\r\n##################################################"+
                "\r\n# Stars Version: {},  Build:  {}"+
                "\r\n##################################################"
                ,new Object[]{pkg.getSpecificationVersion(), pkg.getImplementationVersion()});
		log.info("Starting system in environment: {}", Environment.current().name());
	}
	
	private StarsCoreInterceptor coreInterceptor;
	
	public static StarsConfiguration get(ServletContext servletContext){
		return (StarsConfiguration)servletContext.getAttribute(ROOT_STARS_CONFIG_CONTEXT_ATTRIBUTE);
	}
	
	public StarsConfiguration(){
		Environment.initReloadable(NAME);
	}
	
	public void requestReloading(){
		if(Environment.isReloadingRequested(NAME)){
			if(ServiceProvider.STARS==serviceProvider){
				registerServices();
			}
			scanActionBeans();
		}
	}
	
	@Override
	public void init() {
		
		getServletContext().setAttribute(ROOT_STARS_CONFIG_CONTEXT_ATTRIBUTE, this);
		String pageEncoding = getBootstrapPropertyResolver().getProperty(PAGE_ENCODING); 
		if(pageEncoding!=null){
			this.pageEncoding = pageEncoding;
		}
		String sp = getBootstrapPropertyResolver().getProperty(SERVICE_PROVIDER);
		if(sp!=null){
			if(ServiceProvider.SPRING.name().equalsIgnoreCase(sp)){
				serviceProvider = ServiceProvider.SPRING;
				
				springBeanHolder = new SpringBeanHolder(getServletContext());
				
			}else if(ServiceProvider.EJB.name().equalsIgnoreCase(sp)){
				serviceProvider = ServiceProvider.EJB;
			}else if(ServiceProvider.STARS.name().equalsIgnoreCase(sp)){
				serviceProvider = ServiceProvider.STARS;
			}else{
				throw new StarsRuntimeException("Unknow service provider: "+sp);
			}
		}
		
		super.init();
		
		try{
			this.dependencyManager = initDependencyManager();
			if (this.dependencyManager == null) {
	            this.dependencyManager = new DefaultDependencyManager();
	            this.dependencyManager.init(this);
	        }
			this.lifecycleMethodManager = initLifecycleMethodManager();
			if (this.lifecycleMethodManager == null) {
	            this.lifecycleMethodManager = new DefaultLifecycleMethodManager();
	            this.lifecycleMethodManager.init(this);
	        }
			this.serviceBeanRegistry = initServiceBeanRegistry();
			if (this.serviceBeanRegistry == null) {
	            this.serviceBeanRegistry = new DefaultServiceBeanRegistry();
	            this.serviceBeanRegistry.init(this);
	        }
			this.jndiLocator = initJndiLocator();
			if (this.jndiLocator == null) {
	            this.jndiLocator = new DefaultJndiLocator();
	            this.jndiLocator.init(this);
	        }
	        
			this.ejbLocator = initEjbLocator();
			if (this.ejbLocator == null) {
	            this.ejbLocator = new DefaultEjbLocator();
	            this.ejbLocator.init(this);
	        }
			this.resourceLocator = initResourceLocator();
			if (this.resourceLocator == null) {
	            this.resourceLocator = new DefaultResourceLocator();
	            this.resourceLocator.init(this);
	        }
		}catch (Exception e) {
	        throw new StarsRuntimeException
	                ("Problem instantiating default configuration objects."+ExceptionUtils.getRootCause(e), e);
	    }
		
		if(ServiceProvider.STARS == serviceProvider){
			
			getFormatterFactory().add(Entity.class, EntityFormatter.class);
			getFormatterFactory().add(MappedSuperclass.class, EntityFormatter.class);

			getTypeConverterFactory().add(Entity.class, EntityTypeConverter.class);
			getTypeConverterFactory().add(MappedSuperclass.class,EntityTypeConverter.class);
			
			registerServices();
			
		}
		
		scanActionBeans();
		
		initBootstraps();
		
	}
	
	protected void registerServices(){
		String servicePackagesParam = getBootstrapPropertyResolver()
			.getProperty(SERVICE_RESOLVER_PACKAGES);
		String[] servicePackages = StringUtil.standardSplit(servicePackagesParam);
		
		ResolverUtil<Object> serviceResolver = new ResolverUtil<Object>();
		log.debug("Resolving all service classes that are annotated by NgaiService annotation in packages: {}",
				servicePackagesParam);
		serviceResolver.findAnnotated(ServiceBean.class, servicePackages);
		try {
			for (Class<?> serviceClass : serviceResolver.getClasses()) {
				log.debug("Registering service: {}", serviceClass);
				getServiceBeanRegistry().register(serviceClass);
			}
		} catch (Throwable e) {
			throw new StarsRuntimeException("Service Initializing Failed",e);
		}
	}
	
	@Override
	protected Map<LifecycleStage, Collection<Interceptor>> initInterceptors() {
		Map<LifecycleStage, Collection<Interceptor>> map = super
				.initCoreInterceptors();
		coreInterceptor = new StarsCoreInterceptor();
		addInterceptor(map, coreInterceptor);
		
		if(ServiceProvider.STARS==serviceProvider){
			addInterceptor(map, new Stripersist());
		}else if(ServiceProvider.SPRING==serviceProvider){
			try {
				Class<?> accessDecisionManager = ReflectUtil
					.findClass("org.springframework.security.access.AccessDecisionManager");
				try{
					springBeanHolder.getApplicationContext().getBean(accessDecisionManager);
					addInterceptor(map, new SpringAccessDecisionManagerSecurityInterceptor());
				}catch(BeansException e){}
			} catch (ClassNotFoundException e) {}
		}
		
		return map;
	}
	
	
	@Override
	protected ExceptionHandler initExceptionHandler() {
		ExceptionHandler userExceptionHandler = super.initExceptionHandler();
		CoreExceptionHandler coreExceptionHandler = new CoreExceptionHandler(this);
		if(userExceptionHandler==null){
			return coreExceptionHandler;
		}
		return (ExceptionHandler)CoreExceptionHandlerProxy
				.newInstance(userExceptionHandler, coreExceptionHandler);
	}
	
	
	@Override
	protected TypeConverterFactory initTypeConverterFactory() {
		TypeConverterFactory factory = super.initTypeConverterFactory();
		if(factory!=null){
			return factory;
		}
		factory = new StarsTypeConverterFactory();
		try {
			factory.init(this);
		} catch (Exception e) {
			throw new StarsRuntimeException(e);
		}
		return factory;
	}
	
	
	protected void scanActionBeans() {
		Collection<Class<? extends ActionBean>> actionBeanClasses = getActionResolver().getActionBeanClasses();
		for(Class<? extends ActionBean> actionBeanClass: actionBeanClasses){
			//Lifecyle methods
			lifecycleMethodManager.inspectMethods(actionBeanClass);
			//Attributes
			dependencyManager.inspectAttributes(actionBeanClass);
			//Types
		}
	}
	
	protected void initBootstraps(){
		BootstrapInvoker bootstrapInvoker = new BootstrapInvoker(this);
		for (Class<? extends StarsBootstrap> boostrapClass : getBootstrapPropertyResolver()
			.getClassPropertyList(BOOTSTRAPS, StarsBootstrap.class)) {
			bootstrapInvoker.invoke(boostrapClass);
		}
	}
	
	 /** Looks for a class name in config and uses that to create the component. */
    protected DependencyManager initDependencyManager() {
        return initializeComponent(DependencyManager.class, DEPENDECY_MANAGER);
    }
    
    /** Looks for a class name in config and uses that to create the component. */
    protected LifecycleMethodManager initLifecycleMethodManager() {
        return initializeComponent(LifecycleMethodManager.class, LIFECYCLE_METHOD_MANAGER);
    }
    
    /** Looks for a class name in config and uses that to create the component. */
    protected ServiceBeanRegistry initServiceBeanRegistry() {
        return initializeComponent(ServiceBeanRegistry.class, SERVICE_BEAN_REGISTRY);
    }
    
    /** Looks for a class name in config and uses that to create the component. */
    protected JndiLocator initJndiLocator() {
        return initializeComponent(JndiLocator.class, JNDI_LOCATOR);
    }
    
    /** Looks for a class name in config and uses that to create the component. */
    protected EjbLocator initEjbLocator() {
        return initializeComponent(EjbLocator.class, EJB_LOCATOR);
    }
    
    /** Looks for a class name in config and uses that to create the component. */
    protected ResourceLocator initResourceLocator() {
        return initializeComponent(ResourceLocator.class, RESOURCE_LOCATOR);
    }
    
	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}
	
	public LifecycleMethodManager getLifecycleMethodManager() {
		return lifecycleMethodManager;
	}
	
	public ServiceBeanRegistry getServiceBeanRegistry() {
		return serviceBeanRegistry;
	}
	
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}
	
	public JndiLocator getJndiLocator() {
		return jndiLocator;
	}
		
	public ResourceLocator getResourceLocator() {
		return resourceLocator;
	}
	
	public EjbLocator getEjbLocator() {
		return ejbLocator;
	}
	
	public SpringBeanHolder getSpringBeanHolder(){
		return springBeanHolder;
	}
	
	public String getPageEncoding(){
		return pageEncoding;
	}
	
}


