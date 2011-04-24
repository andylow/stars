package com.siberhus.stars.core;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceRef;

import net.sourceforge.stripes.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.SkipInjectionError;
import com.siberhus.stars.ejb.EjbResourceInjector;
import com.siberhus.stars.spring.SpringResourceInjector;
import com.siberhus.stars.stripes.StarsConfiguration;
import com.siberhus.stars.utils.AnnotatedAttributeUtils;
import com.siberhus.stars.utils.AnnotatedAttributeUtils.AnnotatedAttribute;

public class DefaultDependencyManager implements DependencyManager {
	
	private final Logger log = LoggerFactory.getLogger(DefaultDependencyManager.class);
	
	private StarsConfiguration configuration;
	
	private ResourceInjector resourceInjector;
	
	private ResourceInjector commonResourceInjector;
	
	private boolean inspected = false;
	
	@Override
	public void init(Configuration configuration) {
		this.configuration = (StarsConfiguration)configuration;
		//STARS
		if(this.configuration.getServiceProvider()==ServiceProvider.STARS){
			resourceInjector = new StarsResourceInjector();
		}
		//SPRING
		if(this.configuration.getServiceProvider()==ServiceProvider.SPRING){
			resourceInjector = new SpringResourceInjector();
		}
		//EJB
		if(this.configuration.getServiceProvider()==ServiceProvider.EJB){
			resourceInjector = new EjbResourceInjector();
		}
		
		if(resourceInjector!=null){ 
			resourceInjector.init(this.configuration);
		}
		
		commonResourceInjector = new CommonResourceInjector();
		commonResourceInjector.init(this.configuration);
		
	}
	
	@Override
	public void inspectAttributes(Class<?> targetClass){
		
		inspected = true;
		
		//STARS
		if(configuration.getServiceProvider()==ServiceProvider.STARS){
			AnnotatedAttributeUtils.inspectAttribute(Service.class, targetClass);
		}
		//SPRING
		if(configuration.getServiceProvider()==ServiceProvider.SPRING){
			AnnotatedAttributeUtils.inspectAttribute(Autowired.class, targetClass);
		}
		//EJB
		if(configuration.getServiceProvider()==ServiceProvider.EJB){
			AnnotatedAttributeUtils.inspectAttribute(EJB.class, targetClass);
		}
		
		//COMMON
		AnnotatedAttributeUtils.inspectAttribute(Resource.class, targetClass);
		AnnotatedAttributeUtils.inspectAttribute(WebServiceRef.class, targetClass);
		AnnotatedAttributeUtils.inspectAttribute(PersistenceContext.class, targetClass); //requires persistence.jar
		AnnotatedAttributeUtils.inspectAttribute(PersistenceUnit.class, targetClass); //requires persistence.jar
		
		AnnotatedAttributeUtils.inspectAttribute(SkipInjectionError.class, targetClass);
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
			
			try{
				
				resourceInjector.inject(request, annotAttr, targetObj);
				
				commonResourceInjector.inject(request, annotAttr, targetObj);
				
			}catch(Exception e){
				SkipInjectionError skipInjectionError = targetObj.getClass()
					.getAnnotation(SkipInjectionError.class);
				if(skipInjectionError!=null){
					String attributeName = annotAttr.getAttributeName();
					String attributes[] = skipInjectionError.attributes();
					if(attributes.length>0){
						if(!Arrays.asList(attributes).contains(attributeName)){
							throw e;
						}
					}
					log.info("Unable to inject resource to attribute: {} of bean: {} due to {}", 
						new Object[]{annotAttr.getAttributeName(), targetObj, e});
				}else{
					throw e;
				}
			}
		}
	}
	
	public static class CommonResourceInjector implements ResourceInjector{

		private final Logger log = LoggerFactory.getLogger(CommonResourceInjector.class);
		
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
			
			if(Resource.class == annotType){
				Resource resAnnot = ((Resource)annot);
				Class<?> resType = resAnnot.type();
				if(resType==Object.class){
					resType = attrType;
				}
				String resName = "".equals(resAnnot.name())?annotAttr.getAttributeName():resAnnot.name();
				Object resBean = configuration.getResourceLocator().lookup(resName, resAnnot.mappedName(), 
						resType, resAnnot.authenticationType(), resAnnot.shareable());
				
				log.debug("Injecting Resource: {} to {}",new Object[]{resBean,targetObj});
				annotAttr.set(targetObj, resBean);
				
			}else if(WebServiceRef.class == annotType){
				
			}
		}
		
	}
	
}
