package com.siberhus.stars.test.service.stars;

import com.siberhus.stars.ServiceBean;
import com.siberhus.stars.test.service.NoOperation;

@ServiceBean
public class StarsCalleeService implements NoOperation{
	
	public void nop(){
		System.out.println("Callee");
	}
}
