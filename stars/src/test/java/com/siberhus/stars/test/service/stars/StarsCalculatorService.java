package com.siberhus.stars.test.service.stars;

import com.siberhus.stars.ServiceBean;
import com.siberhus.stars.test.service.Calculator;

@ServiceBean
public class StarsCalculatorService implements Calculator{
	
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
