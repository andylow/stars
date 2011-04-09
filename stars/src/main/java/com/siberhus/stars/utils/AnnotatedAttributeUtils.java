package com.siberhus.stars.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author hussachai
 *
 */
public class AnnotatedAttributeUtils {
	
	private static final Map<Class<?>, List<AnnotatedAttribute>> ANNOT_ATTRIB_CACHE = new Hashtable<Class<?>, List<AnnotatedAttribute>>();
	
	private static final Map<Class<?>, List<AnnotatedField>> ANNOT_FIELD_CACHE = new Hashtable<Class<?>, List<AnnotatedField>>();
	
	private static final Map<Class<?>, List<AnnotatedMethod>> ANNOT_METHOD_CACHE = new Hashtable<Class<?>, List<AnnotatedMethod>>();
	
	public abstract static class AnnotatedJavaElement {
		
		private String id;
		private Annotation annotation;
		
		public AnnotatedJavaElement(Annotation annotation, String javaElement){
			this.annotation = annotation;
			this.id = javaElement+"@"+annotation.annotationType().getName();
		}
		
		public abstract void set(Object bean, Object value)throws Exception;
		public abstract Class<?> getType();
		
		public Annotation getAnnotation() {
			return annotation;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AnnotatedJavaElement other = (AnnotatedJavaElement) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
		
		@Override
		public String toString(){
			return id;
		}
	}
	
	public static class AnnotatedAttribute extends AnnotatedJavaElement{
		
		private String attributeName;
		private Object attribute;
		
		public AnnotatedAttribute(Annotation annotation, Field field){
			super(annotation, field.getName());
			this.attribute = field;
			this.attributeName = field.getName();
		}
		public AnnotatedAttribute(Annotation annotation, Method method){
			super(annotation, method.getName());
			String methodName = method.getName();
			if(methodName.startsWith("set") && methodName.length()>3 
					&& method.getParameterTypes().length==1){
				this.attribute = method;
				this.attributeName = String.valueOf(methodName.charAt(3))
					.toLowerCase() + methodName.substring(4);
			}else{
				throw new IllegalArgumentException("Method: "+method+" is not getter method");
			}
		}
		
		public Object getAttribute(){
			return attribute;
		}
		
		public String getAttributeName(){
			return attributeName;
		}
		
		public void set(Object bean, Object value) throws Exception {
			
			if(attribute instanceof Field){
				((Field)attribute).set(bean, value);
			}else{
				((Method)attribute).invoke(bean, value);
			}
		}
		
		@Override
		public Class<?> getType() {
			if(attribute instanceof Field){
				return ((Field)attribute).getType();
			}else{
				return ((Method)attribute).getParameterTypes()[0];
			}
		}
	}
	
	public static class AnnotatedField extends AnnotatedJavaElement{
		
		private Field field;
		public AnnotatedField(Annotation annotation, Field field){
			super(annotation, field.getName());
			this.field = field;
		}
		public Field getField() {
			return field;
		}
		@Override
		public void set(Object bean, Object value) throws Exception {
			((Field)field).set(bean, value);
		}
		@Override
		public Class<?> getType() {
			return ((Field)field).getType();
		}
	}
	
	public static class AnnotatedMethod extends AnnotatedJavaElement{
		
		private Method method;
		public AnnotatedMethod(Annotation annotation, Method method){
			super(annotation, method.getName());
			if(method.getName().startsWith("set") && method.getName().length()>3 
					&& method.getParameterTypes().length==1){
				this.method = method;
			}else{
				throw new IllegalArgumentException("Method: "+method+" is not getter method");
			}
		}
		public Method getMethod() {
			return method;
		}
		@Override
		public void set(Object bean, Object value) throws Exception {
			((Method)method).invoke(bean, value);
		}
		@Override
		public Class<?> getType() {
			return ((Method)method).getParameterTypes()[0];
		}
	}
	
	public static List<AnnotatedField> getAnnotatedFields(Class<?> targetClass){
		List<AnnotatedField> annotFieldList = ANNOT_FIELD_CACHE.get(targetClass);
		if(annotFieldList==null) throw new IllegalStateException("The target class "
				+targetClass+" has never been inspected.");
		return annotFieldList;
	}
	
	public static List<AnnotatedMethod> getAnnotatedMethods(Class<?> targetClass){
		List<AnnotatedMethod> annotMethodList = ANNOT_METHOD_CACHE.get(targetClass);
		if(annotMethodList==null) throw new IllegalStateException("The target class "
				+targetClass+" has never been inspected.");
		return annotMethodList;
	}
	
	public static List<AnnotatedAttribute> getAnnotatedAttributes(Class<?> targetClass){
		List<AnnotatedAttribute> annotAttrList = ANNOT_ATTRIB_CACHE.get(targetClass);
		if(annotAttrList==null) throw new IllegalStateException("The target class "
				+targetClass+" has never been inspected.");
		return annotAttrList;
	}
	
	public synchronized static boolean inspectField(Class<? extends Annotation> annotClass, Class<?> targetClass){
		boolean hasAnnotatedField = false;
		List<AnnotatedField> annotFieldList = ANNOT_FIELD_CACHE.get(targetClass);
		if(annotFieldList==null){
			annotFieldList = new ArrayList<AnnotatedField>();
			ANNOT_FIELD_CACHE.put(targetClass, annotFieldList);
		}
		do{
			for(Field field : targetClass.getDeclaredFields()){
				
				Annotation annotObject = field.getAnnotation(annotClass); 
				
				if(annotObject!=null){
					hasAnnotatedField = true;
					if(!field.isAccessible()){
						field.setAccessible(true);
					}
					
					AnnotatedField annotField = new AnnotatedField(annotObject,field);
					if(!annotFieldList.contains(annotField)){
						annotFieldList.add(annotField);
					}
				}
			}
		}while( (targetClass=targetClass.getSuperclass())!= Object.class);
		return hasAnnotatedField;
	}
	
	public synchronized static boolean inspectMethod(Class<? extends Annotation> annotClass, Class<?> targetClass){
		boolean hasAnnotatedMethod = false;
		List<AnnotatedMethod> annotMethodList = ANNOT_METHOD_CACHE.get(targetClass);
		if(annotMethodList==null){
			annotMethodList = new ArrayList<AnnotatedMethod>();
			ANNOT_METHOD_CACHE.put(targetClass, annotMethodList);
		}
		do{
			for(Method method : targetClass.getDeclaredMethods()){
				
				Annotation annotObject = method.getAnnotation(annotClass);
				
				if(annotObject!=null){
					hasAnnotatedMethod = true;
					if(!method.isAccessible()){
						method.setAccessible(true);
					}
					
					AnnotatedMethod annotMethod = new AnnotatedMethod(annotObject,method);
					if(!annotMethodList.contains(annotMethod)){
						annotMethodList.add(annotMethod);
					}
				}
			}
		}while( (targetClass=targetClass.getSuperclass())!= Object.class);
		return hasAnnotatedMethod;
	}
	
	public synchronized static boolean inspectAttribute(Class<? extends Annotation> annotClass, Class<?> targetClass){
		
		boolean hasAnnotatedAttrib = false;
		
		List<AnnotatedAttribute> annotAttribList = ANNOT_ATTRIB_CACHE.get(targetClass);
		if(annotAttribList==null){
			annotAttribList = new ArrayList<AnnotatedAttribute>();
			ANNOT_ATTRIB_CACHE.put(targetClass, annotAttribList);
		}
		
		Class<?> parentClass = targetClass;
		do{
			for(Field field : parentClass.getDeclaredFields()){
				
				Annotation annotObject = field.getAnnotation(annotClass); 
				
				if(annotObject!=null){
					hasAnnotatedAttrib = true;
					if(!field.isAccessible()){
						field.setAccessible(true);
					}
					
					AnnotatedAttribute annotAttrib = new AnnotatedAttribute(annotObject,field);
					if(!annotAttribList.contains(annotAttrib)){
						annotAttribList.add(annotAttrib);
					}
				}
			}
		}while( (parentClass=parentClass.getSuperclass())!= Object.class);
		
		parentClass = targetClass;
		do{
			for(Method method : parentClass.getDeclaredMethods()){
				
				Annotation annotObject = method.getAnnotation(annotClass);
				
				if(annotObject!=null){
					hasAnnotatedAttrib = true;
					if(!method.isAccessible()){
						method.setAccessible(true);
					}
					
					AnnotatedAttribute annotAttrib = new AnnotatedAttribute(annotObject,method);
					if(!annotAttribList.contains(annotAttrib)){
						annotAttribList.add(annotAttrib);
					}
				}
			}
		}while( (parentClass=parentClass.getSuperclass())!= Object.class);
		
		return hasAnnotatedAttrib;
	}
	
}
