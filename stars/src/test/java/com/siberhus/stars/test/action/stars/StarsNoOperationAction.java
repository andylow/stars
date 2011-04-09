package com.siberhus.stars.test.action.stars;

import com.siberhus.stars.Service;
import com.siberhus.stars.test.action.BaseAction;
import com.siberhus.stars.test.service.NoOperation;
import com.siberhus.stars.test.service.stars.StarsCallerService;

public class StarsNoOperationAction extends BaseAction {
	
	@Service(impl=StarsCallerService.class)
	private NoOperation callerService; 
	
	public void nop(){
		System.out.println("Action");
		callerService.nop();
	}
}
