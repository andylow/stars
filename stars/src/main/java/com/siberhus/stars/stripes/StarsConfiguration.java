package com.siberhus.stars.stripes;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.servlet.ServletContext;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.config.RuntimeConfiguration;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.exception.ExceptionHandler;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.ReflectUtil;
import net.sourceforge.stripes.util.ResolverUtil;
import net.sourceforge.stripes.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.stripesstuff.stripersist.EntityFormatter;
import org.stripesstuff.stripersist.EntityTypeConverter;
import org.stripesstuff.stripersist.Stripersist;

import com.siberhus.stars.ServiceBean;
import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsBootstrap;
import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.core.BootstrapInvoker;
import com.siberhus.stars.core.DefaultDependencyManager;
import com.siberhus.stars.core.DefaultLifecycleMethodManager;
import com.siberhus.stars.core.DefaultServiceBeanRegistry;
import com.siberhus.stars.core.DependencyManager;
import com.siberhus.stars.core.LifecycleMethodManager;
import com.siberhus.stars.core.ServiceBeanRegistry;
import com.siberhus.stars.core.StarsExceptionHandler;
import com.siberhus.stars.core.StarsExceptionHandlerProxy;
import com.siberhus.stars.ejb.DefaultEjbLocator;
import com.siberhus.stars.ejb.DefaultJndiLocator;
import com.siberhus.stars.ejb.DefaultResourceLocator;
import com.siberhus.stars.ejb.EjbLocator;
import com.siberhus.stars.ejb.JndiLocator;
import com.siberhus.stars.ejb.JndiNameRefMap;
import com.siberhus.stars.ejb.ResourceLocator;
import com.siberhus.stars.spring.SpringBeanHolder;

public class StarsConfiguration extends RuntimeConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(StarsConfiguration.class);
	
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
	
	public static final String JNDI_DEFAULT_LOOKUP_TABLE = "JNDI.DefaultLookupTable";
	
	public static final String JNDI_PROPERTIES = "JNDI.Properties";
	
	public static final String SPRING_AUTOWIRE = "Spring.Autowire";
	
	private DependencyManager dependencyManager;
	
	private LifecycleMethodManager lifecycleMethodManager;
	
	private ServiceBeanRegistry serviceBeanRegistry;
	
	private ServiceProvider serviceProvider = ServiceProvider.STARS;
	
	//JNDI
	private JndiLocator jndiLocator;
	
	private Map<Class<?>, String> defaultJndiMap = new ConcurrentHashMap<Class<?>, String>();
	
	private Properties jndiProperties = new Properties();
	
	private JndiNameRefMap jndiNameRefMap;
	
	private ResourceLocator resourceLocator;	
	
	//EJB
	private EjbLocator ejbLocator;
	
	//SPRING
	private SpringBeanHolder springBeanHolder;
	
	private Autowire springAutowire;
	
	static {
		Package pkg = StarsConfiguration.class.getPackage();
		log.info("\r\n##################################################"+
                "\r\n# Stars Version: {},  Build:  {}"+
                "\r\n##################################################"
                ,new Object[]{pkg.getSpecificationVersion(), pkg.getImplementationVersion()});
	}
	
	private StarsCoreInterceptor coreInterceptor;
	
	public static StarsConfiguration get(ServletContext servletContext){
		return (StarsConfiguration)servletContext.getAttribute(ROOT_STARS_CONFIG_CONTEXT_ATTRIBUTE);
	}
	
	@Override
	public void init() {
		
		getServletContext().setAttribute(ROOT_STARS_CONFIG_CONTEXT_ATTRIBUTE, this);
		
		String sp = getBootstrapPropertyResolver().getProperty(SERVICE_PROVIDER);
		if(sp!=null){
			if(ServiceProvider.SPRING.name().equalsIgnoreCase(sp)){
				serviceProvider = ServiceProvider.SPRING;
			}else if(ServiceProvider.EJB.name().equalsIgnoreCase(sp)){
				serviceProvider = ServiceProvider.EJB;
			}else if(ServiceProvider.STARS.name().equalsIgnoreCase(sp)){
				serviceProvider = ServiceProvider.STARS;
			}else{
				throw new StarsRuntimeException("Unknow service provider: "+sp);
			}
		}
		
		super.init();
		
		initJndiDefaultLookupTable();
		
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
	        this.jndiLocator.initialContext(jndiProperties);
	        
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
	                ("Problem instantiating default configuration objects.", e);
	    }
		
		if(ServiceProvider.STARS == serviceProvider){
			
			getFormatterFactory().add(Entity.class, EntityFormatter.class);
			getFormatterFactory().add(MappedSuperclass.class, EntityFormatter.class);

			getTypeConverterFactory().add(Entity.class, EntityTypeConverter.class);
			getTypeConverterFactory().add(MappedSuperclass.class,EntityTypeConverter.class);
			
			registerServices();
			
		}else if(ServiceProvider.SPRING == serviceProvider){
			
			springBeanHolder = new SpringBeanHolder(getServletContext());
			
			String aw = getBootstrapPropertyResolver().getProperty(SPRING_AUTOWIRE);
			if(aw!=null){
				if(Autowire.BY_NAME.toString().equalsIgnoreCase(aw)){
					springAutowire = Autowire.BY_NAME;
				}else if(Autowire.BY_TYPE.toString().equalsIgnoreCase(aw)){
					springAutowire = Autowire.BY_TYPE;
				}else if(Autowire.NO.toString().equalsIgnoreCase(aw)){
					springAutowire = Autowire.NO;
				}else{
					throw new StarsRuntimeException("Unknow Spring Autowire value: "+aw);
				}
			}else{
				springAutowire = Autowire.BY_NAME;
			}
			
		}else{
			InputStream webFin = getServletContext().getResourceAsStream("/WEB-INF/web.xml");
			if(webFin!=null){
				try{
					jndiNameRefMap = new JndiNameRefMap(webFin);
				}catch(Exception e){
					throw new StarsRuntimeException(e);
				}
			}
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
		}
		
		return map;
	}
	
	
	@Override
	protected ExceptionHandler initExceptionHandler() {
		ExceptionHandler exceptionHandler = super.initExceptionHandler();
		if(ServiceProvider.STARS==serviceProvider){
			if(exceptionHandler==null){
				return new StarsExceptionHandler();
			}
			return (ExceptionHandler)StarsExceptionHandlerProxy
				.newInstance(exceptionHandler);
		}
		return exceptionHandler;
	}
	
	protected void initJndiDefaultLookupTable(){
		String mapString = getBootstrapPropertyResolver().getProperty(JNDI_DEFAULT_LOOKUP_TABLE);
		if (mapString != null) {
            String[] items = StringUtil.standardSplit(mapString);
            for (String item : items) {
            	item = item.trim();
            	String className = null, lookup = null;
                try {
                	String kv[] = item.split("=");
                	className = kv[0];
                	lookup = kv[1];
                	defaultJndiMap.put(ReflectUtil.findClass(className),lookup);
                }catch (ClassNotFoundException e) {
                    throw new StripesRuntimeException("Could not find class [" + className
                            + "] specified by the configuration parameter [" + item
                            + "]. This value must contain fully qualified class names separated "
                            + " by commas.");
                }
            }
        }
	}
	
	protected void initJndiProperties(){
		String mapString = getBootstrapPropertyResolver().getProperty(JNDI_PROPERTIES);
		if (mapString != null) {
            String[] items = StringUtil.standardSplit(mapString);
            for (String item : items) {
            	item = item.trim();
            	String kv[] = item.split("=");
            	jndiProperties.put(kv[0].trim(), kv[1].trim());
            }
        }
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

	public Map<Class<?>, String> getDefaultJndiMap(){
		return defaultJndiMap;
	}
	
	public JndiNameRefMap getJndiNameRefMap(){
		return jndiNameRefMap;
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
	
	public Autowire getSpringAutowire(){
		return springAutowire;
	}
	
	
}














