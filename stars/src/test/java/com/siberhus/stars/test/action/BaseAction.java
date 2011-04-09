package com.siberhus.stars.test.action;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

public class BaseAction implements ActionBean {
	
	private ActionBeanContext actionBeanContext;
	
	@Override
	public void setContext(ActionBeanContext context) {
		this.actionBeanContext = context;
	}

	@Override
	public ActionBeanContext getContext() {
		
		return actionBeanContext;
	}
	
}