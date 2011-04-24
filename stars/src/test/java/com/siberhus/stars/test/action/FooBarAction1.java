package com.siberhus.stars.test.action;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;


public class FooBarAction1 extends BaseAction {
	
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
