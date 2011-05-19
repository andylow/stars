package com.siberhus.stars.validation;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Locale;
import java.util.MissingResourceException;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class SqlTimestampTypeConverter implements TypeConverter<Timestamp>, DateFormatDiscloser{
	
	/** The key used to look up the localized format strings from the resource bundle. */
    public static final String KEY_FORMAT_STRINGS = "stripes.timestampTypeConverter.formatStrings";
    
    /** The default set of date patterns used to parse dates with SimpleDateFormat. */
    public static final String[] formatStrings = new String[] {
    	"yyyy MM dd HH:mm",
    	"yyyy MM dd HH:mm:ss",
        "yyyy MM dd HH:mm:ss:SSS"
    };
    
	private DateTypeConverter deligate = new DateTypeConverter(){
		@Override
		public String[] getFormatStrings() {
	        try {
	            return getResourceString(SqlTimestampTypeConverter.KEY_FORMAT_STRINGS).split(", *");
	        }catch (MissingResourceException mre) {
	            return formatStrings;
	        }
	    }
	};
	
	@Override
	public void setLocale(Locale locale) {
		deligate.setLocale(locale);
	}
	
	@Override
	public Timestamp convert(String input, Class<? extends Timestamp> targetType,
			Collection<ValidationError> errors) {
		java.util.Date date = deligate.convert(input, targetType, errors);
		if(date!=null){
			return new Timestamp(date.getTime());
		}
		return null; 
	}
	
	public String[] getFormatStrings(){
		return deligate.getFormatStrings();
	}
	
}
