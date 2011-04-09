package com.siberhus.stars.stripes;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.config.RuntimeConfiguration;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.util.ReflectUtil;
import net.sourceforge.stripes.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowire;
import org.stripesstuff.stripersist.EntityFormatter;
import org.stripesstuff.stripersist.EntityTypeConverter;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsRuntimeException;
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

public class StarsConfiguration extends RuntimeConfiguration {
	
	private static final Log logger = Log.getInstance(StarsConfiguration.class);
	
	/** The Configuration Key for looking up the name of the DependencyManager class. */
	public static final String DEPENDECY_MANAGER = "DependencyManager.Class";
	
	/** The Configuration Key for looking up the name of the LifecycleMethodManager class. */
	public static final String LIFECYCLE_METHOD_MANAGER = "LifecycleMethodManager.Class";
	
	/** The Configuration Key for looking up the name of the LifecycleMethodManager class. */
	public static final String SERVICE_BEAN_REGISTRY = "ServiceBeanRegistry.Class";
	
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
	
	private JndiLocator jndiLocator;
	
	private EjbLocator ejbLocator;
	
	private ResourceLocator resourceLocator;
	
	private ServiceProvider serviceProvider = ServiceProvider.STARS;
	
	private Map<Class<?>, String> defaultJndiMap = new ConcurrentHashMap<Class<?>, String>();
	
	private Properties jndiProperties = new Properties();
	
	private Autowire springAutowire;
	
	static {
		Package pkg = StarsConfiguration.class.getPackage();
		logger.info("\r\n##################################################"
				+ "\r\n# Stripersist Version: {}, Build: {}"
				+ "\r\n# Ngai Version: {}"
				+ "\r\n##################################################",
				new Object[] { 1.0, 105, pkg.getImplementationVersion() });
	}
	
	private StarsCoreInterceptor coreInterceptor;

	@Override
	public void init() {
		
		super.init();
		
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
		if(serviceProvider==ServiceProvider.SPRING){
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
		}
		
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
		
		
//		initTypeFormatterAndConverter();
		
		scanActionBeans();
		
	}
	
	
	@Override
	protected Map<LifecycleStage, Collection<Interceptor>> initCoreInterceptors() {
		Map<LifecycleStage, Collection<Interceptor>> map = super
				.initCoreInterceptors();
		coreInterceptor = new StarsCoreInterceptor();
		addInterceptor(map, coreInterceptor);
		return map;
	}
	
	// Uses stripersist
	protected void initTypeFormatterAndConverter() {

		getFormatterFactory().add(Entity.class, EntityFormatter.class);
		getFormatterFactory().add(MappedSuperclass.class, EntityFormatter.class);

		getTypeConverterFactory().add(Entity.class, EntityTypeConverter.class);
		getTypeConverterFactory().add(MappedSuperclass.class,EntityTypeConverter.class);
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
	
	public JndiLocator getJndiLocator() {
		return jndiLocator;
	}

	public EjbLocator getEjbLocator() {
		return ejbLocator;
	}
	
	public ResourceLocator getResourceLocator() {
		return resourceLocator;
	}
	
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}
	
	public Map<Class<?>, String> getDefaultJndiMap(){
		return defaultJndiMap;
	}
	
	public Autowire getSpringAutowire(){
		return springAutowire;
	}
	
}














