package com.siberhus.stars.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.siberhus.stars.StarsRuntimeException;

public class MethodUtils {
	
	public static final Method getMethod(Object obj, String methodName, Object... args){
		Class<?> objClass = obj.getClass();
		Method method = null;
		try {
			do{
				try{
					if(args!=null){
						List<Class<?>> typeList = new ArrayList<Class<?>>();
						for (Object arg : args) {
							typeList.add(arg.getClass());
						}
						method = objClass.getDeclaredMethod(methodName, typeList.toArray(new Class[0]));
					}else{
						method = objClass.getDeclaredMethod(methodName);
					}
				}catch(NoSuchMethodException e){}
			}while( (objClass=objClass.getSuperclass())!=Object.class );
			if(method!=null){
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
			}else{
				throw new NoSuchMethodException(obj.getClass().getName()+"."+methodName+"()");
			}
			return method;
		} catch (Exception e) {
			throw new StarsRuntimeException(e);
		}
	}
	
	public static final Object invokeMethod(Object obj, String methodName, Object... args){
		try {
			Method method = getMethod(obj, methodName, args);
			return method.invoke(obj, args);
		} catch (InvocationTargetException e) {
			throw new StarsRuntimeException(e.getTargetException());
		} catch (Exception e) {
			throw new StarsRuntimeException(e);
		}
	}
}
