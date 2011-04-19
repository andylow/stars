package com.siberhus.stars.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceRef;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.util.Log;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.stripesstuff.stripersist.Stripersist;

import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.spring.SpringBeanHolder;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

public class DefaultDependencyManager implements DependencyManager {
	
	private final Log log = Log.getInstance(DefaultDependencyManager.class);
	
	private SpringBeanHolder springBeanHolder;
	
	private StarsConfiguration configuration;
	
	private boolean inspected = false;
	
	@Override
	public void init(Configuration configuration) throws Exception {
		this.configuration = (StarsConfiguration)configuration;
		
		if(this.configuration.getServiceProvider()==ServiceProvider.SPRING){
			springBeanHolder = new SpringBeanHolder(configuration.getServletContext());
		}
	}
	
	@Override
	public void inspectAttributes(Class<?> targetClass){
		
		inspected = true;
		
		//STARS
		if(configuration.getServiceProvider()==ServiceProvider.STARS){
			AnnotatedAttributeUtils.inspectAttribute(Service.class, targetClass);
		}
		
		//EJB
		if(configuration.getServiceProvider()==ServiceProvider.EJB){
			AnnotatedAttributeUtils.inspectAttribute(EJB.class, targetClass);
		}
		
		//SPRING
		if(configuration.getServiceProvider()==ServiceProvider.SPRING){
			AnnotatedAttributeUtils.inspectAttribute(Autowired.class, targetClass);
		}
		
		//SHARED
		AnnotatedAttributeUtils.inspectAttribute(Resource.class, targetClass);
		AnnotatedAttributeUtils.inspectAttribute(WebServiceRef.class, targetClass);
		AnnotatedAttributeUtils.inspectAttribute(PersistenceContext.class, targetClass); //requires persistence.jar
		AnnotatedAttributeUtils.inspectAttribute(PersistenceUnit.class, targetClass); //requires persistence.jar
		
	}
	
	@Override
	public void inject(HttpServletRequest request, Object targetObj) throws Exception {
		Class<?> targetClass = targetObj.getClass();
		if(!inspected){
			throw new IllegalStateException(targetClass.getName()+" has not been inspected yet");
		}
		List<AnnotatedAttribute> annotAttrList = AnnotatedAttributeUtils.getAnnotatedAttributes(targetClass);
		if(annotAttrList==null){
			return;
		}
		
		for(AnnotatedAttribute annotAttr : annotAttrList){
			
			Annotation annot = annotAttr.getAnnotation();
			Class<?> annotType = annot.annotationType();
			Class<?> attrType = annotAttr.getType();
			
			//STARS SERVICE *************************************************************//
			if(ServiceProvider.STARS == configuration.getServiceProvider())
			if(Service.class == annotType){
				Class<?> serviceInfClass = attrType;//Service Bean interface
				Object serviceBean = configuration.getServiceBeanRegistry()
					.get(request, ((Service)annot).impl());
				
				//Inject proxy object to target attribute
				log.debug("Injecting Stars ServiceBean: ",serviceBean, " to ",targetObj);
				annotAttr.set(targetObj, serviceBean);
				
				if(serviceBean==null){
					throw new StarsRuntimeException("ServiceBean class: "+serviceInfClass+ " has not been registered!");
				}
				if(serviceBean instanceof Proxy){
					//Deproxifies
					serviceBean = ServiceBeanProxy.getRealObject((Proxy)serviceBean);
				}
				this.inject(request, serviceBean);
			}else if(PersistenceContext.class == annotType){
				PersistenceContext pc = (PersistenceContext)annot;
				EntityManager em;
				if("".equals(pc.unitName())){
					em = Stripersist.getEntityManager();
				}else{
					em = Stripersist.getEntityManager(pc.unitName());
				}
				log.debug("Injecting EntityManager: ",em," to ",targetObj);
				annotAttr.set(targetObj, em);
			}else if(PersistenceUnit.class == annotType){
				PersistenceUnit pu = (PersistenceUnit)annot;
				EntityManagerFactory emf;
				if("".equals(pu.unitName())){
					emf = Stripersist.getEntityManagerFactory();
				}else{
					emf = Stripersist.getEntityManagerFactory(pu.unitName());
				}
				log.debug("Injecting EntityManagerFactory: ",emf," to ",targetObj);
				annotAttr.set(targetObj, emf);
			}
			
			//EJB SERVICE *************************************************************//
			if(ServiceProvider.EJB == configuration.getServiceProvider())
			if(EJB.class == annotType){
				EJB ejbAnnot = ((EJB)annot);
				Class<?> ejbInfClass = ejbAnnot.beanInterface();
				if(ejbInfClass==Object.class){
					ejbInfClass = attrType;//EJB Home/Remote interface or No-interface bean
				}
				Object ejbBean = configuration.getEjbLocator().lookup(request.getContextPath(), ejbInfClass, ejbAnnot.name(), 
						ejbAnnot.lookup(), ejbAnnot.mappedName(), ejbAnnot.name());
				log.debug("Injecting EJB Session Bean: ",ejbBean," to ",targetObj);
				annotAttr.set(targetObj, ejbBean);
			}
			
			//SPRING SERVICE *************************************************************//
			if(ServiceProvider.SPRING == configuration.getServiceProvider())
			if(Autowired.class == annotType){
				if(configuration.getSpringAutowire()==Autowire.BY_NAME){
					String attrName = annotAttr.getAttributeName();
					Object springBean = springBeanHolder.getApplicationContext().getBean(attrName);
					annotAttr.set(targetObj, springBean);
				}else if(configuration.getSpringAutowire()==Autowire.BY_TYPE){
					Object springBean = springBeanHolder.getApplicationContext().getBean(attrType);
					log.debug("Injecting Spring Bean: ",springBean," to ",targetObj);
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
				log.debug("Injecting EntityManager: ",em," to ",targetObj);
				annotAttr.set(targetObj, em);
			}else if(PersistenceUnit.class == annotType){
				PersistenceUnit pu = (PersistenceUnit)annot;
				EntityManagerFactory emf;
				if("".equals(pu.unitName())){
					emf = springBeanHolder.getEntityManagerFactory();
				}else{
					emf = springBeanHolder.getEntityManagerFactory(pu.unitName());
				}
				log.debug("Injecting EntityManagerFactory: ",emf," to ",targetObj);
				annotAttr.set(targetObj, emf);
			}
			
			
			if(Resource.class == annotType){
				Resource resAnnot = ((Resource)annot);
				Class<?> resType = resAnnot.type();
				if(resType==Object.class){
					resType = attrType;
				}
				String resName = "".equals(resAnnot.name())?annotAttr.getAttributeName():resAnnot.name();
				Object resBean = configuration.getResourceLocator().lookup(resName, resAnnot.mappedName(), 
						resType, resAnnot.authenticationType(), resAnnot.shareable());
				
				log.debug("Injecting Resource: ",resBean," to ",targetObj);
				annotAttr.set(targetObj, resBean);
				
			}else if(WebServiceRef.class == annotType){
				
			}
		}
	}
	
}
