package com.siberhus.stars.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stripesstuff.stripersist.Stripersist;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.exception.ExceptionHandler;

public class StarsExceptionHandler implements ExceptionHandler {

	@Override
	public void init(Configuration configuration) throws Exception {
		
	}
	
	@Override
	public void handle(Throwable throwable, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		Stripersist.requestComplete(throwable);
		
	}
	
}
