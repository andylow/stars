package com.siberhus.stars.test.service.spring;

import com.siberhus.stars.test.service.NoOperation;


public class SpringCallerService implements NoOperation{
	
	private NoOperation calleeService;
	
	@Override
	public void nop() {
		System.out.println("Caller");
		calleeService.nop();
	}

	public void setCalleeService(NoOperation calleeService) {
		this.calleeService = calleeService;
	}
	
	
}
