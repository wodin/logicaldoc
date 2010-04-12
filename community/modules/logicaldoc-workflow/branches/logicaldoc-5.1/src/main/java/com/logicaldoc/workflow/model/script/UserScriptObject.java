package com.logicaldoc.workflow.model.script;

import java.util.LinkedList;
import java.util.List;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;

public class UserScriptObject {

	private String firstName = "";

	private String name = "";

	private String street = "";

	private String postalcode = "";

	private String city = "";

	private String country = "";

	private List<String> groups;

	public UserScriptObject(User user) {
		this.firstName = user.getFirstName();
		this.name = user.getName();
		this.street = user.getStreet();
		this.postalcode = user.getPostalcode();
		this.city = user.getCity();
		this.country = user.getCountry();

		this.groups = new LinkedList<String>();

		for (Group group : user.getGroups())
			this.groups.add(group.getName());

	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public String getStreet() {
		return street;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getName() {
		return name;
	}

	public List<String> getGroups() {
		return this.groups;
	}
}
