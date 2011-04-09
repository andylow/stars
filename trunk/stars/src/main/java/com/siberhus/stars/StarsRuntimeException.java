package com.siberhus.stars;

import net.sourceforge.stripes.exception.StripesRuntimeException;

public class StarsRuntimeException extends StripesRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StarsRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public StarsRuntimeException(String message) {
		super(message);
	}

	public StarsRuntimeException(Throwable cause) {
		super(cause);
	}

}
