package com.siberhus.stars.validation;

import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.siberhus.org.apache.commons.validator.routines.EmailValidator;


public class EmailTypeConverter implements TypeConverter<String> {
    /** Accepts the Locale provided, but does nothing with it since emails are Locale-less. */
    public void setLocale(Locale locale) { /** Doesn't matter for email. */}
	
    /**
     * Validates the user input to ensure that it is a valid email address.
     *
     * @param input the String input, always a non-null non-empty String
     * @param targetType realistically always String since java.lang.String is final
     * @param errors a non-null collection of errors to populate in case of error
     * @return the parsed address, or null if there are no errors. Note that the parsed address
     *         may be different from the input if extraneous characters were removed.
     */
    public String convert(String input,
                          Class<? extends String> targetType,
                          Collection<ValidationError> errors) {

    	if(EmailValidator.getInstance().isValid(input)){
    		return input;
    	}
    	errors.add( new ScopedLocalizableError("converter.email", "invalidEmail") );
    	return null;
    }
}
