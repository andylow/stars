package com.siberhus.stars.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siberhus.org.stripesstuff.stripersist.Stripersist;
import com.siberhus.stars.Service;
import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

public class StarsResourceInjector implements ResourceInjector {
	
	private final Logger log = LoggerFactory.getLogger(StarsResourceInjector.class);
	
	private StarsConfiguration configuration;
	
	@Override
	public void init(StarsConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public void inject(HttpServletRequest request, AnnotatedAttribute annotAttr, Object targetObj) throws Exception {
		
		Annotation annot = annotAttr.getAnnotation();
		Class<?> annotType = annot.annotationType();
		Class<?> attrType = annotAttr.getType();
		
		if(Service.class == annotType){
			Class<?> serviceInfClass = attrType;//Service Bean interface
			Object serviceBean = configuration.getServiceBeanRegistry()
				.get(request, ((Service)annot).impl());
			
			//Inject proxy object to target attribute
			log.debug("Injecting Stars ServiceBean: {} to {}",new Object[]{serviceBean,targetObj});
			annotAttr.set(targetObj, serviceBean);
			
			if(serviceBean==null){
				throw new StarsRuntimeException("ServiceBean class: "+serviceInfClass+ " has not been registered!");
			}
			if(serviceBean instanceof Proxy){
				//Deproxifies
				serviceBean = ServiceBeanProxy.getRealObject((Proxy)serviceBean);
			}
			configuration.getDependencyManager().inject(request, serviceBean);
		}else if(PersistenceContext.class == annotType){
			PersistenceContext pc = (PersistenceContext)annot;
			EntityManager em;
			if("".equals(pc.unitName())){
				em = Stripersist.getEntityManager();
			}else{
				em = Stripersist.getEntityManager(pc.unitName());
			}
			log.debug("Injecting EntityManager: {} to {}",new Object[]{em,targetObj});
			annotAttr.set(targetObj, em);
		}else if(PersistenceUnit.class == annotType){
			PersistenceUnit pu = (PersistenceUnit)annot;
			EntityManagerFactory emf;
			if("".equals(pu.unitName())){
				emf = Stripersist.getEntityManagerFactory();
			}else{
				emf = Stripersist.getEntityManagerFactory(pu.unitName());
			}
			log.debug("Injecting EntityManagerFactory: {} to {}",new Object[]{emf,targetObj});
			annotAttr.set(targetObj, emf);
		}
	}
}
