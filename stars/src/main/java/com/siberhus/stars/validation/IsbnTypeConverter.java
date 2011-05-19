package com.siberhus.stars.validation;

import java.util.Collection;
import java.util.Locale;

import com.siberhus.org.apache.commons.validator.routines.ISBNValidator;

import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class IsbnTypeConverter implements TypeConverter<String> {
    /** Accepts the Locale provided, but does nothing with it since ISBN are Locale-less. */
    public void setLocale(Locale locale) { /** Doesn't matter for ISBN. */}
    
    public String convert(String input,
                          Class<? extends String> targetType,
                          Collection<ValidationError> errors) {
    	ISBNValidator validator = ISBNValidator.getInstance();
    	if(validator.isValid(input)){
    		return input;
    	}
    	errors.add( new ScopedLocalizableError("converter.isbn", "invalidIsbn") );
    	return null;
    }
}
