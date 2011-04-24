package com.siberhus.stars.ejb;

import java.lang.annotation.Annotation;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siberhus.stars.core.ResourceInjector;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

public class EjbResourceInjector implements ResourceInjector {

	private final Logger log = LoggerFactory.getLogger(EjbResourceInjector.class);
	
	private StarsConfiguration configuration;
	
	@Override
	public void init(StarsConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public void inject(HttpServletRequest request, AnnotatedAttribute annotAttr, Object targetObj)throws Exception {
		
		Annotation annot = annotAttr.getAnnotation();
		Class<?> annotType = annot.annotationType();
		Class<?> attrType = annotAttr.getType();
		
		if(EJB.class == annotType){
			EJB ejbAnnot = ((EJB)annot);
			Class<?> ejbInfClass = ejbAnnot.beanInterface();
			if(ejbInfClass==Object.class){
				ejbInfClass = attrType;//EJB Home/Remote interface or No-interface bean
			}
			Object ejbBean = configuration.getEjbLocator().lookup(request.getContextPath(), ejbInfClass, ejbAnnot.name(), 
					ejbAnnot.lookup(), ejbAnnot.mappedName(), ejbAnnot.name());
			log.debug("Injecting EJB Session Bean: {} to {}",new Object[]{ejbBean,targetObj});
			annotAttr.set(targetObj, ejbBean);
		}else if(PersistenceContext.class == annotType){
			PersistenceContext pc = (PersistenceContext)annot;
			EntityManager em;
			if("".equals(pc.unitName())){
				em = (EntityManager)configuration.getJndiLocator()
					.lookup(configuration.getJndiNameRefMap().getEntityManagerJndiName());
			}else{
				em = (EntityManager)configuration.getJndiLocator()
				.lookup(configuration.getJndiNameRefMap().getEntityManagerJndiName(pc.unitName()));
			}
			log.debug("Injecting EntityManager: {} to {}",new Object[]{em,targetObj});
			annotAttr.set(targetObj, em);
		}else if(PersistenceUnit.class == annotType){
			PersistenceUnit pu = (PersistenceUnit)annot;
			EntityManagerFactory emf;
			if("".equals(pu.unitName())){
				emf = (EntityManagerFactory)configuration.getJndiLocator()
					.lookup(configuration.getJndiNameRefMap().getEntityManagerFactoryJndiName());
			}else{
				emf = (EntityManagerFactory)configuration.getJndiLocator()
					.lookup(configuration.getJndiNameRefMap().getEntityManagerFactoryJndiName(pu.unitName()));
			}
			log.debug("Injecting EntityManager: {} to {}",new Object[]{emf,targetObj});
			annotAttr.set(targetObj, emf);
		}
	}
	
	
}
