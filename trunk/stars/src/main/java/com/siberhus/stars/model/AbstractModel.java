package com.siberhus.stars.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractModel<ID extends Serializable> implements Model<ID>{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private ID id;
	
	public String[] toNameKeys(){
		return new String[0];
	}
	
	public String[] toNames(){
		return reflectToNames(getClass());
	}
	
	public Object[] toValues(){
		return reflectToValues(getClass(), this);
	}
	
	private static String[] reflectToNames(Class<?> entityClass){
		List<String> fieldNameList = new ArrayList<String>();
		for(Field field : entityClass.getDeclaredFields()){
			if(field.isAnnotationPresent(Column.class)){
				fieldNameList.add(field.getName());
			}
		}
		return fieldNameList.toArray(new String[0]);
	}
	
	private static Object[] reflectToValues(Class<?> entityClass, Object entity){
		List<Object> fieldValueList = new ArrayList<Object>();
		try{
			for(Field field : entityClass.getDeclaredFields()){
				if(field.isAnnotationPresent(Column.class)){
					if(!field.isAccessible()){
						field.setAccessible(true);
					}
					Object value = field.get(entity);
//					if(value instanceof Date){
//						value = DefaultDataFormat.formatDate((Date)value);
//					}else if(value instanceof Number){
//						value = DefaultDataFormat.formatNumber((Number)value);
//					}
					fieldValueList.add(value);
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return fieldValueList.toArray(new Object[0]);
	}
	
	public ID getId() {
		
		return id;
	}

	public void setId(final ID id) {

		this.id = id;
	}

	public boolean isNew() {
		return null == getId();
	}
	
	@Override
	public String toString() {

		return String.format("Entity of type %s with id: %s", this.getClass()
				.getName(), getId());
	}

	@Override
	public boolean equals(Object obj) {

		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		AbstractModel<?> that = (AbstractModel<?>) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}
}
