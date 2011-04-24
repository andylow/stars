package test.di;

import org.junit.Test;

import test.SpringServiceBaseTestCase;

import com.siberhus.stars.test.action.FooBarAction1;
import com.siberhus.stars.test.action.FooBarAction2;
import com.siberhus.stars.test.action.FooBarAction3;
import com.siberhus.stars.test.action.FooBarAction4;

public class SkipInjectionErrorTest extends SpringServiceBaseTestCase{
	
	@Override
	protected Class<?>[] getActionBeanClasses() {
		return new Class[]{
			FooBarAction1.class,FooBarAction2.class,
			FooBarAction3.class,FooBarAction4.class
		};
	}
	
	@Test(expected=Exception.class)
	public void testFooBarAction1()throws Exception{
		FooBarAction1 actionBean = new FooBarAction1();
		dependencyManager.inject(mockServletRequest, actionBean);
	}
	
	@Test
	public void testFooBarAction2()throws Exception{
		FooBarAction2 actionBean = new FooBarAction2();
		dependencyManager.inject(mockServletRequest, actionBean);
	}
	
	@Test
	public void testFooBarAction3()throws Exception{
		FooBarAction3 actionBean = new FooBarAction3();
		dependencyManager.inject(mockServletRequest, actionBean);
	}
	
	@Test(expected=Exception.class)
	public void testFooBarAction4()throws Exception{
		FooBarAction4 actionBean = new FooBarAction4();
		dependencyManager.inject(mockServletRequest, actionBean);
	}
}
