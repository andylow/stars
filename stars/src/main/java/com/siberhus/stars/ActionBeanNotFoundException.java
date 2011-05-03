package com.siberhus.stars;

public class ActionBeanNotFoundException extends StarsRuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	
	public ActionBeanNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ActionBeanNotFoundException(String message) {
		super(message);
	}
	
	public ActionBeanNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
