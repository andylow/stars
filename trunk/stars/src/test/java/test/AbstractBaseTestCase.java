package test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.config.BootstrapPropertyResolver;
import net.sourceforge.stripes.config.RuntimeConfiguration;
import net.sourceforge.stripes.controller.AnnotatedClassActionResolver;
import net.sourceforge.stripes.controller.NameBasedActionResolver;
import net.sourceforge.stripes.mock.MockFilterConfig;
import net.sourceforge.stripes.mock.MockHttpServletRequest;
import net.sourceforge.stripes.mock.MockHttpServletResponse;
import net.sourceforge.stripes.mock.MockServletContext;

import org.junit.Before;

import com.siberhus.stars.core.DependencyManager;
import com.siberhus.stars.core.LifecycleMethodManager;
import com.siberhus.stars.core.ServiceBeanRegistry;
import com.siberhus.stars.stripes.StarsConfiguration;

public abstract class AbstractBaseTestCase {
	
	public static final String CONTEXT_PATH = "/stars";
	
	protected MockHttpServletRequest mockServletRequest;
	
	protected MockHttpServletResponse mockServletResponse;
	
	protected final MockFilterConfig mockFilterConfig;
	
	protected final MockServletContext mockServletContext;
	
	protected final DependencyManager dependencyManager;
	
	protected final LifecycleMethodManager lifecycleMethodManager;
	
	protected final ServiceBeanRegistry serviceBeanRegistry;
	
	public AbstractBaseTestCase(){
		
		this.mockServletContext = new MockServletContext(CONTEXT_PATH);
		StarsConfiguration starsConfig = new StarsConfiguration();
		this.mockFilterConfig = new MockFilterConfig();
		mockFilterConfig.setServletContext(mockServletContext);
		
		mockFilterConfig.addInitParameter(RuntimeConfiguration.ACTION_RESOLVER, NameBasedActionResolver.class.getName());
		mockFilterConfig.addInitParameter(AnnotatedClassActionResolver.PACKAGES, "com.siberhus.stars.action");
		initFilterParams(mockFilterConfig);
		
		starsConfig.setBootstrapPropertyResolver(new BootstrapPropertyResolver(mockFilterConfig));
		
		starsConfig.init();
		
		this.dependencyManager = starsConfig.getDependencyManager();
		this.lifecycleMethodManager = starsConfig.getLifecycleMethodManager();
		this.serviceBeanRegistry = starsConfig.getServiceBeanRegistry();
		
		for(Class<?> serviceBeanClass: getServiceBeanClasses()){
			this.serviceBeanRegistry.register(serviceBeanClass);
		}
		for(Class<? extends ActionBean> actionBeanClass: getActionBeanClasses()){
			this.dependencyManager.inspectAttributes(actionBeanClass);
			this.lifecycleMethodManager.inspectMethods(actionBeanClass);
		}
		
	}
	
	@Before
	public void initMockRequestResponse(){
		mockServletRequest = new MockHttpServletRequest(CONTEXT_PATH, "dummy"){
			@Override
			public ServletContext getServletContext(){
				return mockServletContext;
			}
		};
		mockServletResponse = new MockHttpServletResponse();
	}
	
	protected abstract void initFilterParams(MockFilterConfig filterConfig);
	
	protected abstract Class[] getServiceBeanClasses();
	
	protected abstract Class[] getActionBeanClasses();
	
	protected HttpServletRequest createMockRequest(String servletPath){
		return new MockHttpServletRequest(CONTEXT_PATH, servletPath);
	}
	
	protected ActionBeanContext createActionBeanContext(String servletPath, String eventName){
		ActionBeanContext ctx = new ActionBeanContext();
		ctx.setRequest(createMockRequest(servletPath));
		ctx.setResponse(new MockHttpServletResponse());
		ctx.setServletContext(new MockServletContext(CONTEXT_PATH));
		ctx.setEventName(eventName);
		return ctx;
	}
	
}
