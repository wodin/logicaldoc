package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.util.io.CryptUtil;

/**
 * This class represents a user. A user can be member of any number of groups,
 * but it is always member of a special group named '_user_'+id. When a new user
 * is created this special group of type 'user' is also created.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class User extends PersistentObject implements Serializable {

	public static int TYPE_DEFAULT = 0;

	public static int TYPE_SYSTEM = 1;

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

	private int type = TYPE_DEFAULT;

	private Set<Group> groups = new HashSet<Group>();

	private long[] groupIds;

	private String[] groupNames;

	// Only for GUI
	private String repass;

	public User() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	
	public String getFullName() {
		String fullName = getFirstName();
		if (fullName != null && getName() != null)
			fullName += " " + getName();
		if (fullName == null && getName() != null)
			fullName = getName();
		if (fullName == null)
			fullName = getUserName();
		return fullName;
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
		if (org.apache.commons.lang.StringUtils.isNotEmpty(pwd)) {
			password = CryptUtil.cryptString(pwd);
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

	/**
	 * The name of the group associated to this user, that is '_user_'+id
	 */
	public String getUserGroupName() {
		return "_user_" + getId();
	}

	/**
	 * Retrieves this user's group
	 */
	public Group getUserGroup() {
		if (getGroups() != null)
			for (Group grp : getGroups()) {
				if (grp.getName().equals(getUserGroupName()))
					return grp;
			}
		return null;
	}
}