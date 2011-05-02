package com.siberhus.stars.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.siberhus.stars.StarsRuntimeException;

public class MethodUtils {
	
	public static final Method getMethod(Object obj, String methodName, Object... args){
		if(args!=null){
			List<Class<?>> typeList = new ArrayList<Class<?>>();
			for(Object arg : args){
				typeList.add(arg.getClass());
			}
			try {
				return obj.getClass().getMethod(methodName, typeList.toArray(new Class[0]));
			} catch (Exception e) {
				throw new StarsRuntimeException(e);
			}
		}else{
			try {
				return obj.getClass().getMethod(methodName);
			} catch (Exception e) {
				throw new StarsRuntimeException(e);
			}
		}
	}
	
	public static final Object invokeMethod(Object obj, String methodName, Object... args){
		if(args!=null){
			List<Class<?>> typeList = new ArrayList<Class<?>>();
			for(Object arg : args){
				typeList.add(arg.getClass());
			}
			try {
				Method method = obj.getClass().getMethod(methodName, typeList.toArray(new Class[0]));
				if(!method.isAccessible()){
					method.setAccessible(true);
				}
				return method.invoke(obj, args);
			} catch (InvocationTargetException e){
				throw new StarsRuntimeException(e.getTargetException());
			} catch (Exception e) {
				throw new StarsRuntimeException(e);
			}
		}else{
			try {
				Method method = obj.getClass().getMethod(methodName);
				if(!method.isAccessible()){
					method.setAccessible(true);
				}
				return method.invoke(obj);
			} catch (InvocationTargetException e){
				throw new StarsRuntimeException(e.getTargetException());
			} catch (Exception e) {
				throw new StarsRuntimeException(e);
			}
		}
	}
}
