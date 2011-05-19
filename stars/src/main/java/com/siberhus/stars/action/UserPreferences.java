package com.siberhus.stars.action;

import java.util.Locale;

public interface UserPreferences {
	
	public Locale getLocale();
	
	public String getTheme();
	
	public String getDateFormat();
	
	public String getTimeFormat();
	
	public String getTimestampFormat();
	
	public String getMoneyFormat();
	
	public String getIntegerFormat();
	
	public String getDecimalFormat();
	
}
