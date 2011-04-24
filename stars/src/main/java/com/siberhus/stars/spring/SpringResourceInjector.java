package com.siberhus.stars.spring;

import java.lang.annotation.Annotation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.core.ResourceInjector;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

public class SpringResourceInjector implements ResourceInjector {
	
	private final Logger log = LoggerFactory.getLogger(SpringResourceInjector.class);
	
	private StarsConfiguration configuration;
	
	public void init(StarsConfiguration configuration){
		this.configuration = configuration;
	}
	
	public void inject(HttpServletRequest request, AnnotatedAttribute annotAttr, Object targetObj)throws Exception{
		
		Annotation annot = annotAttr.getAnnotation();
		Class<?> annotType = annot.annotationType();
		Class<?> attrType = annotAttr.getType();
		SpringBeanHolder springBeanHolder = configuration.getSpringBeanHolder();
		
		if(Autowired.class == annotType){
			if(configuration.getSpringAutowire()==Autowire.BY_NAME){
				String attrName = annotAttr.getAttributeName();
				Object springBean = springBeanHolder.getApplicationContext().getBean(attrName);
				annotAttr.set(targetObj, springBean);
			}else if(configuration.getSpringAutowire()==Autowire.BY_TYPE){
				Object springBean = springBeanHolder.getApplicationContext().getBean(attrType);
				log.debug("Injecting Spring Bean: {} to {}",new Object[]{springBean,targetObj});
				annotAttr.set(targetObj, springBean);
			}
		}else if(PersistenceContext.class == annotType){
			PersistenceContext pc = (PersistenceContext)annot;
			EntityManager em;
			if("".equals(pc.unitName())){
				em = springBeanHolder.getEntityManagerFactory().createEntityManager();
			}else{
				em = springBeanHolder.getEntityManagerFactory(
						pc.unitName()).createEntityManager();
			}
			log.debug("Injecting EntityManager: {} to {}",new Object[]{em,targetObj});
			annotAttr.set(targetObj, em);
		}else if(PersistenceUnit.class == annotType){
			PersistenceUnit pu = (PersistenceUnit)annot;
			EntityManagerFactory emf;
			if("".equals(pu.unitName())){
				emf = springBeanHolder.getEntityManagerFactory();
			}else{
				emf = springBeanHolder.getEntityManagerFactory(pu.unitName());
			}
			log.debug("Injecting EntityManagerFactory: {} to {}",new Object[]{emf,targetObj});
			annotAttr.set(targetObj, emf);
		}
		
	}
	
}
