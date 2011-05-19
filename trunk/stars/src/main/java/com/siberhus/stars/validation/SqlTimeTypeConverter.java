package com.siberhus.stars.validation;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;

import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class SqlTimeTypeConverter implements TypeConverter<Time>, DateFormatDiscloser{
	
	private Locale locale;
	private DateFormat[] formats;
	
	/**
	 * The default set of date patterns used to parse dates with
	 * SimpleDateFormat.
	 */
	public static final String[] formatStrings = new String[] { 
		"HH:mm",
		"hh:mm aa", 
		"HH:mm:ss", 
		"HH:mm:ss:SSS" 
	};

	/**
	 * The key used to look up the localized format strings from the resource
	 * bundle.
	 */
	public static final String KEY_FORMAT_STRINGS = "stripes.timeTypeConverter.formatStrings";

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
		this.formats = getDateFormats();
	}

	/**
	 * Returns an array of format strings that will be used, in order, to try
	 * and parse the date. This method can be overridden to make
	 * DateTypeConverter use a different set of format Strings. Given that
	 * pre-processing converts most common separator characters into spaces,
	 * patterns should be expressed with spaces as separators, not slashes,
	 * hyphens etc.
	 */
	@Override
	public String[] getFormatStrings() {
		try {
			return getResourceString(KEY_FORMAT_STRINGS).split(", *");
		} catch (MissingResourceException mre) {
			return formatStrings;
		}
	}

	/**
	 * Returns an array of DateFormat objects that will be used in sequence to
	 * try and parse the date String. This method will be called once when the
	 * DateTypeConverter instance is initialized. It first calls
	 * getFormatStrings() to obtain the format strings that are used to
	 * construct SimpleDateFormat instances.
	 */
	protected DateFormat[] getDateFormats() {
		String[] formatStrings = getFormatStrings();
		SimpleDateFormat[] dateFormats = new SimpleDateFormat[formatStrings.length];

		for (int i = 0; i < formatStrings.length; i++) {
			dateFormats[i] = new SimpleDateFormat(formatStrings[i], locale);
			dateFormats[i].setLenient(false);
		}

		return dateFormats;
	}

	 /**
     * Attempts to convert a String to a Time object.  Uses an ordered list 
     * of DateFormat objects (supplied by getDateFormats()) to try and parse 
     * the String into a Date and then convert to Time object.
     */
	@Override
	public Time convert(String input, Class<? extends Time> targetType,
			Collection<ValidationError> errors) {

		input = input.trim();

		Date date = null;

		for (DateFormat format : this.formats) {
			try {
				date = format.parse(input);
				break;
			} catch (ParseException pe) {} /* Do nothing, we'll get lots of these. */
		}
		
		// If we successfully parsed, convert date to time and return, 
		// otherwise send back an error
		if (date != null) {
			return new Time(date.getTime());
		} else {
			errors.add(new ScopedLocalizableError("converter.time","invalidTime"));
			return null;
		}
	}
	
	/** Convenience method to fetch a property from the resource bundle. */
	protected String getResourceString(String key)
			throws MissingResourceException {
		return StripesFilter.getConfiguration().getLocalizationBundleFactory()
				.getErrorMessageBundle(locale).getString(key);
		
	}
}
