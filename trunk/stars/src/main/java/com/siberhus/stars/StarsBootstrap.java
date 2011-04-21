package com.siberhus.stars;

import javax.servlet.ServletContext;

/**
 * 
 * @author hussachai
 *
 */
public interface StarsBootstrap {
	
	public void init(ServletContext servletContext) throws Exception;
	
	public void destroy();
	
}
