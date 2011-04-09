package test.di;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.junit.Test;

import test.SpringServiceBaseTestCase;

import com.siberhus.stars.test.action.JpaAction;
import com.siberhus.stars.test.action.spring.SpringCalculatorAction;
import com.siberhus.stars.test.action.spring.SpringNoOperationAction;

public class SpringDependencyInjectionTest extends SpringServiceBaseTestCase {
	
	@Test
	public void testSimpleService() throws Exception{
		SpringCalculatorAction actionBean = new SpringCalculatorAction();
		actionBean.setNumberOne(new Double(20));
		actionBean.setNumberTwo(new Double(30));
		dependencyManager.inject(mockServletRequest, actionBean);
		actionBean.add();
		Assert.assertEquals(new Double(50), actionBean.getResult());
	}
	
	@Test
	public void testNestedService() throws Exception {
		SpringNoOperationAction actionBean = new SpringNoOperationAction();
		dependencyManager.inject(mockServletRequest, actionBean);
		actionBean.nop();
	}
	
	@Test
	public void testJpa() throws Exception {
		JpaAction actionBean = new JpaAction();
		dependencyManager.inject(mockServletRequest, actionBean);
		Assert.assertNotNull(actionBean.getEntityManager());
		Assert.assertNotNull(actionBean.getMovieEm());
		
		Assert.assertNotNull(actionBean.getEntityManagerFactory());
		Assert.assertNotNull(actionBean.getMovieEmf());
		Assert.assertTrue(actionBean.getMovieEmf()==actionBean.getMovieEmf());
		Assert.assertTrue(actionBean.getEntityManagerFactory()!=actionBean.getMovieEmf());
		
		Assert.assertTrue(actionBean.getEntityManager() instanceof EntityManager);
		Assert.assertTrue(actionBean.getEntityManagerFactory() instanceof EntityManagerFactory);
	}
}
