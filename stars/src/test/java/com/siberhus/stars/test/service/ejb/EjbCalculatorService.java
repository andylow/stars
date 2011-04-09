package com.siberhus.stars.test.service.ejb;

import javax.ejb.Stateless;

@Stateless
public class EjbCalculatorService implements EjbCalculatorLocal, EjbCalculatorRemote {
	
	public String message;
	
	public void showMessage(){
		System.out.println(message);
	}
	
	@Override
	public Double add(Double a, Double b) {
		return a+b;
	}
	
	@Override
	public Double minus(Double a, Double b) {
		return a-b;
	}
	
}
