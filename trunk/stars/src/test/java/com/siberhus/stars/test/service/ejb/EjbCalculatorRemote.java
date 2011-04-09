package com.siberhus.stars.test.service.ejb;

import javax.ejb.Remote;


@Remote
public interface EjbCalculatorRemote {
	
	public Double add(Double a, Double b);
	
	public Double minus(Double a, Double b);
	
	public void showMessage();
	
}
