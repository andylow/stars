package com.siberhus.stars.test.service.spring;

import com.siberhus.stars.test.service.NoOperation;

public class SpringCalleeService implements NoOperation{
	
	public void nop(){
		System.out.println("Callee");
	}
}
