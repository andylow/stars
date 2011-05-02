package com.siberhus.stars.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.util.ReflectUtil;

import com.siberhus.stars.StarsRuntimeException;

public class CoreExceptionHandlerProxy implements InvocationHandler{
	
	private Object userExceptionHandler;
	
	private CoreExceptionHandler coreExceptionHandler;
	
	public static Object newInstance(Object userExceptionHandler, CoreExceptionHandler coreExceptionHandler) {
		return Proxy.newProxyInstance(userExceptionHandler.getClass().getClassLoader(),
				ReflectUtil.getImplementedInterfaces(userExceptionHandler.getClass()).toArray(new Class[0]), 
					new CoreExceptionHandlerProxy(userExceptionHandler, coreExceptionHandler));
	}
	
	public CoreExceptionHandlerProxy(Object userExceptionHandler, 
			CoreExceptionHandler coreExceptionHandler){
		this.userExceptionHandler = userExceptionHandler;
		this.coreExceptionHandler = coreExceptionHandler;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		try{
			if(args.length != 3){
				method.invoke(userExceptionHandler, args);
			}else{
				try{
					method.invoke(userExceptionHandler, args);
				}finally{
					coreExceptionHandler.handle((Throwable)args[0], 
						(HttpServletRequest)args[1], (HttpServletResponse)args[2]);
				}
			}
		}catch(UndeclaredThrowableException e){
			Throwable cause = e;
			if(e.getUndeclaredThrowable()!=null){
				cause = e.getUndeclaredThrowable();
			}
			throw new StarsRuntimeException(cause.getMessage(), cause);
		}catch(Throwable e){
			throw new StarsRuntimeException(e.getMessage(), e);
		}
		return result;
	}
	
}
