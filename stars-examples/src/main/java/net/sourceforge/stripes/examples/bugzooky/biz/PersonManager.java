package net.sourceforge.stripes.examples.bugzooky.biz;

import java.util.List;

import net.sourceforge.stripes.examples.bugzooky.model.Person;

public interface PersonManager {
	
	public Person getPerson(int id);
	
	public Person getPerson(String username);
	
	public List<Person> getAllPeople();
	
	public void saveOrUpdate(Person person);
	 
	public void deletePerson(int id);
	
	
}
