package com.logicaldoc.webservice.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.crypt.CryptUtil;
import com.logicaldoc.webservice.AbstractService;

/**
 * Web Service User. Useful class to create repository Users.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class WSUser {
	public static int TYPE_DEFAULT = 0;

	public static int TYPE_SYSTEM = 1;

	public static int SOURCE_DEFAULT = 0;

	public static int SOURCE_LDAP = 1;

	public static int SOURCE_ACTIVE_DIRECTORY = 2;

	public static final long USERID_ADMIN = 1;

	private long id;

	private String userName = "";

	private String password = "";

	private String passwordmd4 = "";

	private String name = "";

	private String firstName = "";

	private String street = "";

	private String postalcode = "";

	private String city = "";

	private String country = "";

	private String state = "";

	private String language = "";

	private String email = "";

	private String telephone = "";

	private String telephone2 = "";

	private int type = TYPE_DEFAULT;

	private long[] groupIds = new long[0];

	private int enabled = 1;

	// The last time the password was changed
	private String passwordChanged = "";

	// If the password expires or not
	private int passwordExpires = 0;

	private int source = 0;

	private long quota = -1;

	private long quotaCount = 0;

	private String lastModified;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public void setUserName(String uname) {
		userName = uname;
	}

	/**
	 * Sets the password and encode it
	 * 
	 * @param pwd The password in readable format
	 */
	public void setPassword(String pwd) {
		if (org.apache.commons.lang.StringUtils.isNotEmpty(pwd)) {
			password = CryptUtil.cryptString(pwd);
		}
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

	public long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(long[] groupIds) {
		this.groupIds = groupIds;
	}

	public String toString() {
		return getUserName();
	}

	/**
	 * The name of the group associated to this user, that is '_user_'+id
	 */
	public String getUserGroupName() {
		return "_user_" + getId();
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	/**
	 * When the password was modified
	 */
	public String getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(String passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	/**
	 * If the password expires or not
	 */
	public int getPasswordExpires() {
		return passwordExpires;
	}

	public void setPasswordExpires(int passwordExpires) {
		this.passwordExpires = passwordExpires;
	}

	/**
	 * The source from which the user has been created
	 * 
	 * @see User#SOURCE_DEFAULT
	 * @see User#SOURCE_LDAP
	 * @see User#SOURCE_ACTIVE_DIRECTORY
	 */
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public long getQuotaCount() {
		return quotaCount;
	}

	public void setQuotaCount(long quotaCount) {
		this.quotaCount = quotaCount;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public User toUser() {
		User user = new User();

		try {
			user.setId(getId());
			user.setCity(getCity());
			user.setCountry(getCountry());
			user.setEmail(getEmail());
			user.setFirstName(getFirstName());
			user.setName(getName());
			user.setLanguage(getLanguage());
			user.setPostalcode(getPostalcode());
			user.setState(getState());
			user.setStreet(getStreet());
			user.setTelephone(getTelephone());
			user.setTelephone2(getTelephone2());
			user.setUserName(getUserName());
			user.setEnabled(getEnabled());
			user.setPasswordExpires(getPasswordExpires());
			user.setQuota(getQuota());
			user.setType(getType());
			user.setSource(getSource());
			user.setPassword(getPassword());
			user.setPasswordChanged(new Date());

			if (getGroupIds().length > 0) {
				GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				Set<Group> groups = new HashSet<Group>();
				for (long groupId : getGroupIds()) {
					Group group = groupDao.findById(groupId);
					if (group != null)
						groups.add(group);
				}
				if (groups.size() > 0)
					user.setGroups(groups);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return user;
	}

	public static WSUser fromUser(User user) {
		WSUser wsUser = new WSUser();

		try {
			wsUser.setId(user.getId());
			wsUser.setCity(user.getCity());
			wsUser.setCountry(user.getCountry());
			wsUser.setEmail(user.getEmail());
			wsUser.setFirstName(user.getFirstName());
			wsUser.setName(user.getName());
			wsUser.setLanguage(user.getLanguage());
			wsUser.setPostalcode(user.getPostalcode());
			wsUser.setState(user.getState());
			wsUser.setStreet(user.getStreet());
			wsUser.setTelephone(user.getTelephone());
			wsUser.setTelephone2(user.getTelephone2());
			wsUser.setUserName(user.getUserName());
			wsUser.setEnabled(user.getEnabled());
			wsUser.setPasswordExpires(user.getPasswordExpires());
			wsUser.setQuota(user.getQuota());
			wsUser.setType(user.getType());
			wsUser.setSource(user.getSource());
			wsUser.setPassword(user.getPassword());
			wsUser.setPasswordmd4(user.getPasswordmd4());
			wsUser.setPasswordChanged(AbstractService.convertDateToString(user.getPasswordChanged()));
			wsUser.setLastModified(AbstractService.convertDateToString(user.getLastModified()));

			if (user.getGroups() != null && user.getGroups().size() > 0) {
				long[] groupIds = new long[user.getGroups().size()];
				int i = 0;
				for (Group group : user.getGroups()) {
					if (group.getType() == Group.TYPE_DEFAULT) {
						groupIds[i] = group.getId();
						i++;
					}
				}
				wsUser.setGroupIds(groupIds);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return wsUser;
	}

	public String getPasswordmd4() {
		return passwordmd4;
	}

	public void setPasswordmd4(String passwordmd4) {
		this.passwordmd4 = passwordmd4;
	}
}