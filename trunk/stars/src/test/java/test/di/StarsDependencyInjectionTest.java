package test.di;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.junit.Test;

import test.StarsServiceBaseTestCase;

import com.siberhus.org.stripesstuff.stripersist.Stripersist;
import com.siberhus.stars.core.ServiceBeanProxy;
import com.siberhus.stars.test.action.JpaAction;
import com.siberhus.stars.test.action.stars.StarsCalculatorAction;
import com.siberhus.stars.test.action.stars.StarsNoOperationAction;
import com.siberhus.stars.test.service.NoOperation;
import com.siberhus.stars.test.service.stars.StarsCallerService;

public class StarsDependencyInjectionTest extends StarsServiceBaseTestCase {
	
	@Test
	public void testSimpleService() throws Exception{
		StarsCalculatorAction actionBean = new StarsCalculatorAction();
		actionBean.setNumberOne(new Double(20));
		actionBean.setNumberTwo(new Double(30));
		dependencyManager.inject(mockServletRequest, actionBean);
		actionBean.add();
		Assert.assertEquals(new Double(50), actionBean.getResult());
	}
	
	@Test
	public void testNestedService() throws Exception {
		StarsNoOperationAction actionBean = new StarsNoOperationAction();
		dependencyManager.inject(mockServletRequest, actionBean);
		actionBean.nop();
	}
	
	@Test
	public void getEm(){
		System.out.println(Stripersist.getEntityManager());
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
	
	public static void main(String[] args) {
		Object obj = ServiceBeanProxy.newInstance(new StarsCallerService());
		NoOperation nop = (NoOperation)obj;
		nop.nop();
	}
}
