package com.siberhus.stars.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

import net.sourceforge.stripes.util.ReflectUtil;

import org.stripesstuff.stripersist.Stripersist;

import com.siberhus.stars.StarsRuntimeException;

public class StarsExceptionHandlerProxy implements InvocationHandler{
	
	private Object object;
	
	public static Object newInstance(Object object) {
		return Proxy.newProxyInstance(object.getClass().getClassLoader(),
				ReflectUtil.getImplementedInterfaces(object.getClass())
						.toArray(new Class[0]), new StarsExceptionHandlerProxy(object));
	}
	
	public StarsExceptionHandlerProxy(Object object){
		this.object = object;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		try{
			if(args.length != 3){
				method.invoke(object, args);
			}else{
				Throwable e = (Throwable)args[0];
				try{
					method.invoke(object, args);
				}finally{
					Stripersist.requestComplete(e);
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
