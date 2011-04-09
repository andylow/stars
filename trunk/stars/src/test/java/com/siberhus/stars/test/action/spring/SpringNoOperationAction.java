package com.siberhus.stars.test.action.spring;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.test.action.BaseAction;
import com.siberhus.stars.test.service.NoOperation;

public class SpringNoOperationAction extends BaseAction {
	
	@Autowired
	private NoOperation callerService; 
	
	public void nop(){
		System.out.println("Action");
		callerService.nop();
	}
}
