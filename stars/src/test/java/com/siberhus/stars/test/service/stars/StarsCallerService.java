package com.siberhus.stars.test.service.stars;

import com.siberhus.stars.Service;
import com.siberhus.stars.ServiceBean;
import com.siberhus.stars.test.service.NoOperation;

@ServiceBean
public class StarsCallerService implements NoOperation{
	
	@Service(impl=StarsCalleeService.class)
	private NoOperation calleeService;
	
	@Override
	public void nop() {
		System.out.println("Caller");
		calleeService.nop();
	}
	
}
