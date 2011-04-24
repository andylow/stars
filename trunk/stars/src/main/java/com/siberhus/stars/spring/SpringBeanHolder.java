package com.siberhus.stars.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringBeanHolder {
	
	private ApplicationContext applicationContext;
	
	private Map<String, AbstractEntityManagerFactoryBean> entityManagerFactoryMap;
	
	private AbstractEntityManagerFactoryBean defaultEntityManagerBean;
	
	private Map<String, PlatformTransactionManager> transactionManagerMap;
	
	public SpringBeanHolder(ServletContext context){
		
		applicationContext = WebApplicationContextUtils
			.getRequiredWebApplicationContext(context);
		entityManagerFactoryMap = new HashMap<String, AbstractEntityManagerFactoryBean>();
		Map<String, AbstractEntityManagerFactoryBean> map 
			= applicationContext.getBeansOfType(AbstractEntityManagerFactoryBean.class);
		for(String key: map.keySet()){
			AbstractEntityManagerFactoryBean emfBean = 
				(AbstractEntityManagerFactoryBean)applicationContext.getBean(key);
			entityManagerFactoryMap.put(emfBean.getPersistenceUnitName(), emfBean);
			if(map.keySet().size()==1){
				this.defaultEntityManagerBean = emfBean;
			}else{
				if("default".equals(emfBean.getPersistenceUnitName())){
					this.defaultEntityManagerBean = emfBean;
				}
			}
		}
		
		transactionManagerMap = applicationContext.getBeansOfType(PlatformTransactionManager.class);
		
		
	}
	
	public EntityManagerFactory getEntityManagerFactory(String unitName){
		
		return entityManagerFactoryMap.get(unitName).getObject();
	}
	
	
	public EntityManagerFactory getEntityManagerFactory(){
		if(defaultEntityManagerBean!=null){
			return defaultEntityManagerBean.getObject();
		}
		throw new NoSuchBeanDefinitionException(AbstractEntityManagerFactoryBean.class);
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public Collection<PlatformTransactionManager> getTransactionManagers(){
		return transactionManagerMap.values();
	}
	
	
}
