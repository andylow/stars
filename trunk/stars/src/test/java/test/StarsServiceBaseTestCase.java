package test;

import net.sourceforge.stripes.config.RuntimeConfiguration;
import net.sourceforge.stripes.mock.MockFilterConfig;

import org.junit.After;
import org.junit.Before;
import org.stripesstuff.stripersist.Stripersist;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.test.action.JpaAction;
import com.siberhus.stars.test.action.stars.StarsCalculatorAction;
import com.siberhus.stars.test.action.stars.StarsNoOperationAction;
import com.siberhus.stars.test.service.stars.StarsCalculatorService;
import com.siberhus.stars.test.service.stars.StarsCalleeService;
import com.siberhus.stars.test.service.stars.StarsCallerService;

public abstract class StarsServiceBaseTestCase extends AbstractBaseTestCase {
	
	@Override
	protected void initFilterParams(MockFilterConfig filterConfig) {
		filterConfig.addInitParameter(RuntimeConfiguration.INTERCEPTOR_LIST, 
			Stripersist.class.getName()
		);
		
//		filterConfig.addInitParameter(BootstrapPropertyResolver.PACKAGES, "org.stripesstuff.stripersist");
		filterConfig.addInitParameter(StarsConfiguration.SERVICE_PROVIDER, ServiceProvider.STARS.name());
	}
	
	@Override
	protected Class[] getServiceBeanClasses() {
		return new Class[]{
			StarsCalculatorService.class,
			StarsCalleeService.class, StarsCallerService.class
		};
	}
	
	@Override
	protected Class[] getActionBeanClasses() {
		return new Class[]{
			StarsCalculatorAction.class,
			StarsNoOperationAction.class,
			JpaAction.class
		};
	}

	@Before
	public void init(){
		Stripersist.requestInit();
	}
	
	@After
	public void destroy(){
		Stripersist.requestComplete();
	}
	
}
