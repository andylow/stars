package com.siberhus.stars.validation;

import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.siberhus.org.apache.commons.validator.routines.UrlValidator;

public class UrlTypeConverter implements TypeConverter<String> {
    /** Accepts the Locale provided, but does nothing with it since URL are Locale-less. */
    public void setLocale(Locale locale) { /** Doesn't matter for URL. */}
    
    public String convert(String input,
                          Class<? extends String> targetType,
                          Collection<ValidationError> errors) {
    	
    	if(UrlValidator.getInstance().isValid(input)){
    		return input;
    	}
    	errors.add( new ScopedLocalizableError("converter.url", "invalidUrl") );
    	return null;
    }
}
