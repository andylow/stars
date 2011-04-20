package net.sourceforge.stripes.examples.quickstart;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

public class AbstractActionBean implements ActionBean {

	private ActionBeanContext context;
	
	@Override
	public void setContext(ActionBeanContext context) {
		this.context = context;
	}

	@Override
	public ActionBeanContext getContext() {
		
		return context;
	}

}
