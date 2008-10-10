package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import com.logicaldoc.core.CryptBean;

/**
 * This class represents a user.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class User implements Serializable {

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

	public String[] getGroupNames() {
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

	public void setName(String nm) {
		name = nm;
	}

	public void setFirstName(String fname) {
		firstName = fname;
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

	public void setGroupNames(String[] grp) {
		groupNames = grp;
	}

	public void initGroupNames() {
		try {
			groupNames = new String[groups.size()];

			Iterator iter = groups.iterator();
			int i = 0;

			while (iter.hasNext()) {
				Group ug = (Group) iter.next();
				groupNames[i] = ug.getGroupName();
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
		groupNames = null;
	}

	public String toString() {
		// return ReflectionToStringBuilder.toString(this);
		return (new ReflectionToStringBuilder(this) {
			protected boolean accept(java.lang.reflect.Field f) {
				return super.accept(f);
			} // end method accept
		}).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
			return false;

		User other = (User) obj;
		return other.getUserName().equals(this.getUserName());
	}

	@Override
	public int hashCode() {
		return userName.hashCode();
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
}