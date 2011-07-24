package com.siberhus.stars.localization;

import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.config.BootstrapPropertyResolver;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.localization.DefaultLocalePicker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionLocalePicker extends DefaultLocalePicker {
	
	private final Logger log = LoggerFactory.getLogger(SessionLocalePicker.class);  
	
	public static final String LOCALE_SESSION_ATTR = SessionLocalePicker.class.getName()+".Locale";
	
	public static final String ENABLED = "LocalePicker.Enabled";
	public static final String CHARACTER_ENCODING = "LocalePicker.CharacterEncoding";
	
	private boolean enabled = true;
	private String characterEncoding = "UTF-8";
	
	public Locale setUserPreferredLocale(HttpServletRequest request, String language){
		Locale preferredLocale = request.getLocale();;
		if(language!=null){
			preferredLocale = new Locale(language);
		}
		request.getSession().setAttribute(LOCALE_SESSION_ATTR, preferredLocale);
		return preferredLocale;
	}
	
	@Override
	public void init(Configuration configuration) throws Exception {
		BootstrapPropertyResolver propResolver = configuration.getBootstrapPropertyResolver();
		String enabledStr = propResolver.getProperty(ENABLED);
		if(enabledStr!=null){
			if("true".equalsIgnoreCase(enabledStr)){ this.enabled = true;
			}else if("false".equalsIgnoreCase(enabledStr)){ this.enabled = false;
			}else{ throw new IllegalArgumentException("Cannot convert "+enabled+" to boolean");}
		}
		if(propResolver.getProperty(CHARACTER_ENCODING)!=null){
			characterEncoding = propResolver.getProperty(CHARACTER_ENCODING);
			if(!Charset.isSupported(characterEncoding)){
				throw new IllegalArgumentException("Unsupported character encoding: "+characterEncoding);
			}
		}
		
		if(!this.enabled){
			log.info("SessionLocalePicker is disabled. Activating DefaultLocalePicker instead.");
			super.init(configuration);
		}else{
			log.info("SessionLocalePicker is enabled");
			log.info("Character encoding: {}", characterEncoding);
		}
	}
	
	@Override
	public Locale pickLocale(HttpServletRequest request) {
		
		if(!enabled){
			return super.pickLocale(request);
		}
		Locale locale = (Locale)request.getSession()
			.getAttribute(LOCALE_SESSION_ATTR);		
		if(locale!=null){
			return locale;
		}
		return request.getLocale();
	}
	
	@Override
	public String pickCharacterEncoding(HttpServletRequest request,
			Locale locale) {
		if(!enabled){
			return super.pickCharacterEncoding(request, locale);
		}
		return characterEncoding;
	}
	
}
