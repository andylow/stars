package com.siberhus.stars.core;

import java.lang.reflect.Modifier;

import javax.servlet.ServletContext;

import net.sourceforge.stripes.mock.MockHttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stripesstuff.stripersist.Stripersist;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsBootstrap;
import com.siberhus.stars.stripes.StarsConfiguration;

public class BootstrapInvoker {
	
	private Logger log = LoggerFactory.getLogger(BootstrapInvoker.class);
	
	private StarsConfiguration configuration;
	
	private MockHttpServletRequest mockRequest;
	
	public BootstrapInvoker(StarsConfiguration configuration){
		this.configuration = configuration;
		mockRequest = new MockHttpServletRequest(configuration.getServletContext()
			.getContextPath(), "dummy");
	}
	
	public void invoke(Class<? extends StarsBootstrap> boostrapClass){
		
		DependencyManager dependencyManager = configuration.getDependencyManager();
		ServiceProvider serviceProvider = configuration.getServiceProvider();
		ServletContext servletContext = configuration.getServletContext();
		try {
			if (!boostrapClass.isInterface() && ((boostrapClass.getModifiers() & Modifier.ABSTRACT) == 0)) {
				log.debug("Found StarsBootstrap class: {}  - instanciating and calling init()", boostrapClass);
				
				StarsBootstrap bootstrap = boostrapClass.newInstance();
				
				dependencyManager.inspectAttributes(boostrapClass);
				
				if(serviceProvider==ServiceProvider.STARS){
					Stripersist.requestInit();
					try{
						dependencyManager.inject(mockRequest, bootstrap);
						bootstrap.execute(servletContext);
						Stripersist.requestComplete(null);
					}catch(Exception e){
						Stripersist.requestComplete(e);
					}
					
				}else if(serviceProvider==ServiceProvider.SPRING){
					
					dependencyManager.inject(mockRequest, bootstrap);
					bootstrap.execute(servletContext);
					
//					Exception error = null;
//					try{
//						dependencyManager.inject(mockRequest, bootstrap);
//						bootstrap.init(servletContext);
//					}catch(Exception e){
//						error = e;
//					}finally{
//						Collection<PlatformTransactionManager> txManagers = 
//							configuration.getSpringBeanHolder().getTransactionManagers();
//						log.debug("All registered transaction manager: {}",txManagers);
//						for(PlatformTransactionManager txManager: txManagers){
//							TransactionDefinition txDef = new DefaultTransactionDefinition();
//							TransactionStatus txStatus = txManager.getTransaction(txDef);
//							if(error instanceof RuntimeException){
//								txManager.rollback(txStatus);
//							}else{
//								txManager.commit(txStatus);
//							}
//						}
//					}
					
				}else{
					//EJB
					
					dependencyManager.inject(mockRequest, bootstrap);
					bootstrap.execute(servletContext);
					
//					UserTransaction userTx = (UserTransaction)configuration
//						.getJndiLocator().lookup(UserTransaction.class);
//					try{
//						dependencyManager.inject(mockRequest, bootstrap);
//						bootstrap.init(servletContext);
//						userTx.commit();
//					}catch(Exception e){
//						if(e instanceof RuntimeException){
//							userTx.rollback();
//						}else{
//							userTx.commit();
//						}
//					}
					
				}
			}
		} catch (Exception e) {
             log.error("Error occurred while calling init() on "+boostrapClass, e);
		}
	}
}
