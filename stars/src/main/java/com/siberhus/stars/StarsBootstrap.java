package com.siberhus.stars;

import javax.servlet.ServletContext;

/**
 * 
 * @author hussachai
 *
 */
public interface StarsBootstrap {
	
	public void execute(ServletContext servletContext) throws Exception;
	
}
