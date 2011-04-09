package test.lifecycle;

import junit.framework.Assert;

import org.junit.Test;

import test.SpringServiceBaseTestCase;

import com.siberhus.stars.test.action.spring.SpringCalculatorAction;


public class SpringActionBeanLifecycleTest extends SpringServiceBaseTestCase {
	
	@Test
	public void testIt(){
		SpringCalculatorAction actionBean = new SpringCalculatorAction();
		Assert.assertNull(actionBean.getMessage());
		lifecycleMethodManager.invokePostConstructMethod(actionBean);
		Assert.assertEquals("Hello", actionBean.getMessage());
		lifecycleMethodManager.invokePreDestroyMethod(actionBean);
		Assert.assertEquals("Goodbye", actionBean.getMessage());
	}
	
}
