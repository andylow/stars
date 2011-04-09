package test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import net.sourceforge.stripes.mock.MockFilterConfig;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.test.action.JpaAction;
import com.siberhus.stars.test.action.spring.SpringCalculatorAction;
import com.siberhus.stars.test.action.spring.SpringNoOperationAction;
import com.siberhus.stars.test.service.spring.SpringCalculatorService;
import com.siberhus.stars.test.service.spring.SpringCalleeService;
import com.siberhus.stars.test.service.spring.SpringCallerService;


public abstract class SpringServiceBaseTestCase extends AbstractBaseTestCase {
	
	@Override
	protected void initFilterParams(MockFilterConfig filterConfig) {
		filterConfig.addInitParameter(StarsConfiguration.SERVICE_PROVIDER, ServiceProvider.SPRING.name());
		
		ApplicationContext springCtx = new ClassPathXmlApplicationContext("applicationContext.xml");
		springCtx = new MockWebApplicationContext(springCtx, mockServletContext);
		mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, springCtx );
		
	}
	
	@Override
	protected Class[] getServiceBeanClasses() {
		return new Class[]{
//			SpringCalculatorService.class,
//			SpringCalleeService.class, SpringCallerService.class
		};
	}
	
	@Override
	protected Class[] getActionBeanClasses() {
		return new Class[]{
			SpringCalculatorAction.class,
			SpringNoOperationAction.class,
			JpaAction.class
		};
	}
}
