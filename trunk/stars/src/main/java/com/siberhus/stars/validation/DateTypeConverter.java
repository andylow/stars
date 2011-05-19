package com.siberhus.stars.validation;


public class DateTypeConverter extends
		net.sourceforge.stripes.validation.DateTypeConverter implements DateFormatDiscloser{
	
	//Change visibility of getFormatStrings() from protected to public
	@Override
	public String[] getFormatStrings() {
		return super.getFormatStrings();
	}
	
}
