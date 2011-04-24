package com.siberhus.stars.test.action;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.SkipInjectionError;

@SkipInjectionError(attributes={"exception"})
public class FooBarAction3 extends BaseAction {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private Exception exception;

	public DataSource getDataSource() {
		return dataSource;
	}

	public Exception getException() {
		return exception;
	}
	
}
