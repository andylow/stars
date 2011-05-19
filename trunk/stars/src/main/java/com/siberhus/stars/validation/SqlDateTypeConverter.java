package com.siberhus.stars.validation;

import java.sql.Date;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class SqlDateTypeConverter implements TypeConverter<Date>, DateFormatDiscloser{
	
	private DateTypeConverter deligate = new DateTypeConverter();
	
	@Override
	public void setLocale(Locale locale) {
		deligate.setLocale(locale);
	}
	
	@Override
	public Date convert(String input, Class<? extends Date> targetType,
			Collection<ValidationError> errors) {
		java.util.Date date = deligate.convert(input, targetType, errors);
		if(date!=null){
			return new Date(date.getTime());
		}
		return null; 
	}
	
	@Override
	public String[] getFormatStrings(){
		return deligate.getFormatStrings();
	}
	
}
