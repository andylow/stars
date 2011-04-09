package test.lifecycle;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.junit.Test;

import test.StarsServiceBaseTestCase;

import com.siberhus.stars.stripes.StarsCoreInterceptor;
import com.siberhus.stars.test.action.stars.StarsCalculatorAction;
import com.siberhus.stars.test.util.ReflectionUtils;


public class StarsActionBeanLifecycleTest extends StarsServiceBaseTestCase {
	
	@Test
	public void testIt(){
		StarsCalculatorAction actionBean = new StarsCalculatorAction();
		Assert.assertNull(actionBean.getMessage());
		lifecycleMethodManager.invokePostConstructMethod(actionBean);
		Assert.assertEquals("Hello", actionBean.getMessage());
		lifecycleMethodManager.invokePreDestroyMethod(actionBean);
		Assert.assertEquals("Goodbye", actionBean.getMessage());
	}
	
//	@Test
	public void testPostConstruct() throws Exception{
		StarsCalculatorAction calcAction = new StarsCalculatorAction();
		List<Interceptor> interceptorList = new ArrayList<Interceptor>();
		StarsCoreInterceptor coreInterceptor = new StarsCoreInterceptor();
		interceptorList.add(coreInterceptor);
		
		ExecutionContext context = new ExecutionContext();
		
		context.setActionBean(calcAction);
		context.setActionBeanContext(createActionBeanContext("/add", "add"));
		context.setHandler(ReflectionUtils.getMethod(calcAction, "add"));
		context.setInterceptors(interceptorList);
		context.setLifecycleStage(LifecycleStage.HandlerResolution);
		
		Resolution resolution = context.proceed();
		
		
	}

}
