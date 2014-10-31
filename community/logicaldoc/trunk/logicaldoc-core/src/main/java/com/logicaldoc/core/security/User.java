package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.crypt.CryptUtil;

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

	public static int SOURCE_DEFAULT = 0;

	public static int SOURCE_LDAP = 1;

	public static int SOURCE_ACTIVE_DIRECTORY = 2;

	public static final long USERID_ADMIN = 1;

	private static final long serialVersionUID = 8093874904302301982L;

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

	private Set<Group> groups = new HashSet<Group>();

	private long[] groupIds;

	private String[] groupNames;

	private int enabled = 1;

	// The last time the password was changed
	private Date passwordChanged = new Date();

	// If the password expires or not
	private int passwordExpires = 0;

	// If the password already expired
	private int passwordExpired = 0;

	// Only for GUI
	private String repass;

	private int source = 0;

	private long quota = -1;

	private Integer welcomeScreen = 1520;

	private String ipWhiteList;

	private String ipBlackList;

	private String decodedPassword;

	private String cert;

	private String certSubject;

	private String certDigest;

	private String key;

	private String keyDigest;

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

	public boolean isInGroup(String groupName) {
		String[] names = getGroupNames();
		for (int i = 0; i < names.length; i++) {
			if (groupName.equals(names[i]))
				return true;
		}
		return false;
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
			decodedPassword = pwd;
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

	public void initGroupIdsAndNames() {
		try {
			groupIds = new long[groups.size()];
			groupNames = new String[groups.size()];

			Iterator<Group> iter = groups.iterator();
			int i = 0;

			while (iter.hasNext()) {
				Group ug = iter.next();
				if (ug.getDeleted() == 1)
					continue;
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
		passwordmd4 = "";
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
		passwordExpires = 0;
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

	public Locale getLocale() {
		return LocaleUtil.toLocale(getLanguage());
	}

	public void setLocale(Locale locale) {
		setLanguage(locale.toString());
	}

	/**
	 * When the password was modified
	 */
	public Date getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(Date passwordChanged) {
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

	public Integer getWelcomeScreen() {
		return welcomeScreen;
	}

	public void setWelcomeScreen(Integer welcomeScreen) {
		this.welcomeScreen = welcomeScreen;
	}

	public String getIpWhiteList() {
		return ipWhiteList;
	}

	public void setIpWhiteList(String ipWhiteList) {
		this.ipWhiteList = ipWhiteList;
	}

	public String getIpBlackList() {
		return ipBlackList;
	}

	public void setIpBlackList(String ipBlackList) {
		this.ipBlackList = ipBlackList;
	}

	public int getPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(int passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public String getPasswordmd4() {
		return passwordmd4;
	}

	public void setPasswordmd4(String passwordmd4) {
		this.passwordmd4 = passwordmd4;
	}

	public String getDecodedPassword() {
		return decodedPassword;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCertSubject() {
		return certSubject;
	}

	public void setCertSubject(String certSubject) {
		this.certSubject = certSubject;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public String getKeyDigest() {
		return keyDigest;
	}

	public void setKeyDigest(String keyDigest) {
		this.keyDigest = keyDigest;
	}

	public String getCertDigest() {
		return certDigest;
	}

	public void setCertDigest(String certDigest) {
		this.certDigest = certDigest;
	}
}