package com.siberhus.stars;

import javax.servlet.ServletContext;

import com.siberhus.stars.stripes.StarsConfiguration;

public enum ServiceProvider {

	SPRING, EJB, STARS;
	
	public static ServiceProvider get(ServletContext servletContext){
		StarsConfiguration starsConfig = StarsConfiguration.get(servletContext);
		return starsConfig.getServiceProvider();
	}
	
	public static boolean isSpring(ServletContext servletContext){
		return get(servletContext)==SPRING;
	}
	
	public static boolean isEjb(ServletContext servletContext){
		return get(servletContext)==EJB;
	}
	
	public static boolean isStars(ServletContext servletContext){
		return get(servletContext)==STARS;
	}
	
}
