package test.di;

import junit.framework.Assert;

import org.junit.Test;

import test.EjbServiceBaseTestCase;

import com.siberhus.stars.test.action.ejb.EjbCalculatorAction;

public class EjbDependencyInjectionTest extends EjbServiceBaseTestCase {
	
	@Test
	public void testSimpleService() throws Exception{
		EjbCalculatorAction actionBean = new EjbCalculatorAction();
		actionBean.setNumberOne(new Double(20));
		actionBean.setNumberTwo(new Double(30));
		dependencyManager.inject(mockServletRequest, actionBean);
		actionBean.add();
		Assert.assertEquals(new Double(50), actionBean.getResult());
	}
	
//	@Test
//	public void testNestedService() throws Exception {
//		StarsNoOperationAction actionBean = new StarsNoOperationAction();
//		dependencyManager.inject(mockServletRequest, actionBean);
//		actionBean.nop();
//	}
	
}
