package com.siberhus.stars.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.exception.StripesRuntimeException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.StarsRuntimeException;
import com.siberhus.stars.stripes.StarsConfiguration;

public class DefaultLifecycleMethodManager implements LifecycleMethodManager {
	
	private final Map<Class<?>, Method> I_METHOD_CACHE = new Hashtable<Class<?>, Method>();
	private final Map<Class<?>, Method> D_METHOD_CACHE = new Hashtable<Class<?>, Method>();
	
	private StarsConfiguration configuration;
	
	private boolean inspected = false;
	
	public DefaultLifecycleMethodManager(){}
	
	@Override
	public void init(Configuration configuration) throws Exception {
		this.configuration = (StarsConfiguration)configuration;
	}
	
	@Override
	public void inspectMethods(Class<?> targetClass){
		inspected = true;
		inspectMethod(PostConstruct.class, targetClass);
		inspectMethod(PreDestroy.class, targetClass);
	}
	
	protected synchronized boolean inspectMethod(Class<? extends Annotation> annotClass, Class<?> targetClass){
		boolean hasAnnotatedMethod = false;
		
		do{
			for(Method method : targetClass.getDeclaredMethods()){
				
				Annotation annotObject = method.getAnnotation(annotClass);
				if(annotObject instanceof PostConstruct 
					|| annotObject instanceof PreDestroy){
					/*
					 * Only one method can be annotated with this annotation.
					 *  - The method MUST NOT have any parameters.
					 *  - The return type of the method MUST be void.
					 *  - The method MUST NOT throw a checked exception.
					 *  - The method on which PostConstruct is applied MAY be public, protected, package private or private.
					 *  - The method MUST NOT be static except for the application client.
					 *  - The method MAY be final. 
					 *  - If the method throws an unchecked exception the class MUST NOT be put 
					 *  	into service except in the case of EJBs where the EJB can handle 
					 *  	exceptions and even recover from them. 
					 */
					boolean flag = true;
					if(method.getParameterTypes().length!=0) {
						flag = false;
					}
					if(!method.getReturnType().toString().equals("void")){
						flag = false;
					}
					if(method.getExceptionTypes().length!=0){
						for(Class<?> eClass : method.getExceptionTypes()){
							if(!RuntimeException.class.isAssignableFrom(eClass)){
								flag = false; break;
							}
						}
					}
					if(Modifier.isStatic(method.getModifiers())){
						flag = false;
					}
					if(!flag){
						throw new StripesRuntimeException("Method "+method.getName()+" does not match specification");
					}
				}
				if(annotObject instanceof PostConstruct){
					I_METHOD_CACHE.put(targetClass, method);
				}else if(annotObject instanceof PreDestroy){
					D_METHOD_CACHE.put(targetClass, method);
				}else{
					continue;
				}
				if(annotObject!=null){
					hasAnnotatedMethod = true;
					if(!method.isAccessible()){
						method.setAccessible(true);
					}
				}
			}
		}while( (targetClass=targetClass.getSuperclass())!= Object.class);
		return hasAnnotatedMethod;
	}
	
	@Override
	public void invokePostConstructMethod(Object object){
		checkState(object.getClass());
		Method method= I_METHOD_CACHE.get(object.getClass());
		try {
			if(method!=null) method.invoke(object);
			if(configuration.getServiceProvider()==ServiceProvider.SPRING){
				if(object instanceof InitializingBean){
					((InitializingBean)object).afterPropertiesSet();
				}
			}
		} catch (InvocationTargetException e) {
			throw new StarsRuntimeException(e.getTargetException());
		} catch (Exception e){
			throw new StarsRuntimeException(e);
		}
	}
	
	@Override
	public void invokePreDestroyMethod(Object object){
		checkState(object.getClass());
		Method method = D_METHOD_CACHE.get(object.getClass());
		try {
			if(method!=null) method.invoke(object);
			if(configuration.getServiceProvider()==ServiceProvider.SPRING){
				if(object instanceof DisposableBean){
					((DisposableBean)object).destroy();
				}
			}
		} catch (InvocationTargetException e) {
			throw new StarsRuntimeException(e.getTargetException());
		} catch (Exception e){
			throw new StarsRuntimeException(e);
		}
	}
	
	private void checkState(Class<?> clazz){
		if(!inspected){
			throw new IllegalStateException("inspectMethods(Class<?> targetClass) method " +
					"should be called first for class "+clazz.getName());
		}
	}
	
	public static void main(String[] args) {
		System.out.println(RuntimeException.class.isAssignableFrom(StripesRuntimeException.class));
		System.out.println(StripesRuntimeException.class.isAssignableFrom(RuntimeException.class));
	}
}
