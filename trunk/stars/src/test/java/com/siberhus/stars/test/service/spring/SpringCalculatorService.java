package com.siberhus.stars.test.service.spring;

import com.siberhus.stars.test.service.Calculator;

public class SpringCalculatorService implements Calculator{
	
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
