package net.sourceforge.stripes.examples.bugzooky.biz;

import java.util.List;

import net.sourceforge.stripes.examples.bugzooky.model.Bug;

public interface BugManager {

	public Bug getBug(int id);
	
	public List<Bug> getAllBugs();
	
	public void saveOrUpdate(Bug bug);
	
}
