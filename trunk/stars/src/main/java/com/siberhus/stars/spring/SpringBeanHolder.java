package com.siberhus.stars.spring;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringBeanHolder {
	
	private ApplicationContext applicationContext;
	
	private final Map<String, AbstractEntityManagerFactoryBean> entityManagerFactoryMap 
		= new HashMap<String, AbstractEntityManagerFactoryBean>();
	
	private AbstractEntityManagerFactoryBean defaultEntityManagerBean;
	
	public SpringBeanHolder(ServletContext context){
		
		applicationContext = WebApplicationContextUtils
			.getRequiredWebApplicationContext(context);
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
	
	
}
