package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.logicaldoc.core.CryptBean;
import com.logicaldoc.core.PersistentObject;

/**
 * This class represents a user.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class User extends PersistentObject implements Serializable {

	private static final long serialVersionUID = 8093874904302301982L;

	private String userName = "";

	private String password = "";

	private String name = "";

	private String firstName = "";

	private String street = "";

	private String postalcode = "";

	private String city = "";

	private String country = "";

	private String language = "";

	private String email = "";

	private String telephone = "";

	private Set<Group> groups = new HashSet<Group>();

	private long[] groupIds;

	private String[] groupNames;

	// Only for GUI
	private String repass;

	public User() {
	}

	public String getRepass() {
		return repass;
	}

	public void setRepass(String repass) {
		this.repass = repass;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getStreet() {
		return street;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getLanguage() {
		return language;
	}

	public String getEmail() {
		return email;
	}

	public String getTelephone() {
		return telephone;
	}

	public long[] getGroupIds() {
		if (groupIds == null)
			initGroupIdsAndNames();
		return groupIds;
	}

	public String[] getGroupNames() {
		if (groupNames == null)
			initGroupIdsAndNames();
		return groupNames;
	}

	public void setUserName(String uname) {
		userName = uname;
	}

	public void setPassword(String pwd) {
		password = pwd;
	}

	/**
	 * Sets the password and encode it
	 * 
	 * @param pwd The password in readable format
	 */
	public void setDecodedPassword(String pwd) {
		if ((pwd != null) && !pwd.trim().equals("")) {
			password = CryptBean.cryptString(pwd);
		}
	}

	public String getDecodedPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setStreet(String str) {
		street = str;
	}

	public void setPostalcode(String pc) {
		postalcode = pc;
	}

	public void setCity(String ct) {
		city = ct;
	}

	public void setCountry(String cnt) {
		country = cnt;
	}

	public void setLanguage(String lang) {
		language = lang;
	}

	public void setEmail(String mail) {
		email = mail;
	}

	public void setTelephone(String phone) {
		telephone = phone;
	}

	public void initGroupIdsAndNames() {
		try {
			groupIds = new long[groups.size()];
			groupNames = new String[groups.size()];

			Iterator<Group> iter = groups.iterator();
			int i = 0;

			while (iter.hasNext()) {
				Group ug = iter.next();
				groupIds[i] = ug.getId();
				groupNames[i] = ug.getName();
				i++;
			}
		} catch (Exception e) {
			;
		}
	}

	public void reset() {
		userName = "";
		password = "";
		name = "";
		firstName = "";
		street = "";
		postalcode = "";
		city = "";
		country = "";
		language = "";
		email = "";
		telephone = "";
		groups = new HashSet<Group>();
		groupIds = null;
	}

	public String toString() {
		return getUserName();
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
}