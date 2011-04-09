package com.siberhus.stars.test.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Role {
	
	@Id
	@Column(name="ROLE_ID")
	private Long id;
	
	@Column(name="ROLE_NAME")
	private String name;
	
	@ManyToMany(cascade={CascadeType.PERSIST})
	@JoinTable(name="USER_ROLES", 
		joinColumns=@JoinColumn(name="UR_ROLE_ID", referencedColumnName="ROLE_ID"),
		inverseJoinColumns=@JoinColumn(name="UR_USER_ID", referencedColumnName="USER_ID"))
	private List<User> users = new ArrayList<User>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	
}
