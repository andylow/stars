package com.siberhus.stars.validation;

import java.util.Date;
import java.util.Map;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.validation.DefaultTypeConverterFactory;
import net.sourceforge.stripes.validation.TypeConverter;

public class StarsTypeConverterFactory extends DefaultTypeConverterFactory{

	@Override
	public void init(Configuration configuration) {
		super.init(configuration);
		Map<Class<?>, Class<? extends TypeConverter<?>>> converters = getTypeConverters();
		converters.put(Date.class, DateTypeConverter.class);
		converters.put(java.sql.Date.class, SqlDateTypeConverter.class);
		converters.put(java.sql.Time.class, SqlTimeTypeConverter.class);
		converters.put(java.sql.Timestamp.class, SqlTimestampTypeConverter.class);
	}
	
}
