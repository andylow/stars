package test;

import net.sourceforge.stripes.mock.MockFilterConfig;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.ejb.openejb.OpenEjbLocalJndiLocator;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.test.action.ejb.EjbCalculatorAction;
import com.siberhus.stars.test.service.ejb.EjbCalculatorService;

public abstract class EjbServiceBaseTestCase extends AbstractBaseTestCase {

	@Override
	protected void initFilterParams(MockFilterConfig filterConfig) {
		filterConfig.addInitParameter(StarsConfiguration.SERVICE_PROVIDER, ServiceProvider.EJB.name());
		
//		filterConfig.addInitParameter(StarsConfiguration.JNDI_DEFAULT_LOOKUP_TABLE, "");
		filterConfig.addInitParameter(StarsConfiguration.JNDI_LOCATOR, 
				OpenEjbLocalJndiLocator.class.getName());
	}
	
	@Override
	protected Class[] getServiceBeanClasses() {
		return new Class[]{
			EjbCalculatorService.class
		};
	}
	
	@Override
	protected Class[] getActionBeanClasses() {
		return new Class[]{
			EjbCalculatorAction.class
		};
	}
}