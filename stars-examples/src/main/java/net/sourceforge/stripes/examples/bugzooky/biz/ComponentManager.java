package net.sourceforge.stripes.examples.bugzooky.biz;

import java.util.List;

import net.sourceforge.stripes.examples.bugzooky.model.Component;

public interface ComponentManager {

	public Component getComponent(int id);
	
	public List<Component> getAllComponents();
	
	public void saveOrUpdate(Component component);
	
	public void deleteComponent(int componentId);
	
	
}
