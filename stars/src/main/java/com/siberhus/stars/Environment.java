package com.siberhus.stars;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum Environment {
	
	PRODUCTION, TEST, DEVELOPMENT;
	
	private static final Logger log = LoggerFactory.getLogger(Environment.class);
	
	private static Environment environment = PRODUCTION;
	
	private static Map<String, Boolean> reloadMap = new HashMap<String, Boolean>();
	
	/**
	 * Initialize environment in static block to prevent value changing 
	 * at runtime because System's properties is writable.
	 */
	static{
		String envString = System.getProperty("Stars.Env");
		if(envString==null) envString = "PRODUCTION";
		if("PROD".equalsIgnoreCase(envString) || 
			"PRODUCTION".equalsIgnoreCase(envString)){
			environment = PRODUCTION;
		}else if("TEST".equalsIgnoreCase(envString)){
			environment = TEST;
		}else if("DEV".equalsIgnoreCase(envString) ||
			"DEVELOPMENT".equalsIgnoreCase(envString)){
			environment = DEVELOPMENT;
		}
	}
	
	public synchronized static void initReloadable(String name){
		if(environment==DEVELOPMENT){
			reloadMap.put(name, false);
		}
	}
	
	public synchronized static boolean isReloadingRequested(String name){
		if(environment==DEVELOPMENT){
			boolean reload = reloadMap.get(name);
			if(reload) log.debug("Reloading: {}", name);
			reloadMap.put(name, false);
			return reload;
		}
		return false;
	}
	
	public synchronized static void requestReloading(){
		if(environment==DEVELOPMENT){
			for(String name : reloadMap.keySet()){
				reloadMap.put(name, true);
			}
		}
	}
	
	public static Environment current(){
		return environment;
	}
	
	public static boolean isProduction(){
		return environment == PRODUCTION;
	}
	
	public static boolean isTest(){
		return environment == TEST;
	}
	
	public static boolean isDevelopment(){
		return environment == DEVELOPMENT;
	}
	
}
