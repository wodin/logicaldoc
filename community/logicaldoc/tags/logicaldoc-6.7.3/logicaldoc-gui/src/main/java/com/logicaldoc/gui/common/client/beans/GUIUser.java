package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.UserObserver;

/**
 * User bean as used in the GUI
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIUser implements Serializable {

	public static final String ALL_TASKS = "tasks";

	public static final String EVENTS = "events";

	public static final String UNREAD_MESSAGES = "unreadMessages";

	public static final String ALL_MESSAGES = "messages";

	public static final String LOCKED_DOCS = "lockedDocs";

	public static final String CHECKED_OUT_DOCS = "checkedOutDocs";

	public static final String ALL_SUBSCRIPTIONS = "subscriptions";

	private static final long serialVersionUID = 1L;

	private String userName = "";

	private long id = 0;

	private GUIGroup[] groups = new GUIGroup[0];

	private String firstName = "";

	private String name = "";

	private String language = "en";

	private boolean notifyCredentials = true;

	private boolean passwordExpired = true;

	private int passwordMinLenght = 0;

	private boolean passwordExpires = false;

	private String address = "";

	private String postalCode = "";

	private String city = "";

	private String country = "";

	private String state = "";

	private String phone = "";

	private String email = "";

	private String cell = "";

	private boolean enabled = true;

	private int checkedOutDocs = 0;

	private int lockedDocs = 0;

	private int unreadMessages = 0;

	private int tasks = 0;

	private int messages = 0;

	private int subscriptions = 0;

	private int upcomingEvents = 0;

	private Long[] menues = new Long[0];

	private long quota = -1;

	private long quotaCount = 0;

	private String signatureId = "";

	private String signatureInfo = "";

	private Integer welcomeScreen = 1500;

	private Set<UserObserver> observers = new HashSet<UserObserver>();

	private String ipWhitelist;

	private String ipBlacklist;

	private GUIDashlet[] dashlets = new GUIDashlet[0];

	public void setUserName(String userName) {
		this.userName = userName;
		notifyObservers("userName");
	}

	public String getUserName() {
		return userName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public GUIGroup[] getGroups() {
		return groups;
	}

	public void setGroups(GUIGroup[] groups) {
		this.groups = groups;
	}

	public boolean isMemberOf(String group) {
		for (GUIGroup g : groups) {
			if (group.equals(g.getName()))
				return true;
		}
		return false;
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

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPasswordMinLenght() {
		return passwordMinLenght;
	}

	public void setPasswordMinLenght(int passwordMinLenght) {
		this.passwordMinLenght = passwordMinLenght;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isPasswordExpires() {
		return passwordExpires;
	}

	public void setPasswordExpires(boolean passwordExpires) {
		this.passwordExpires = passwordExpires;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void addGroup(GUIGroup group) {
		if (!isMemberOf(group.getName())) {
			GUIGroup[] tmp = new GUIGroup[groups.length + 1];
			for (int i = 0; i < groups.length; i++) {
				tmp[i] = groups[i];
			}
			tmp[groups.length] = group;
			groups = tmp;
		}
	}

	public void addDashlet(GUIDashlet dashlet) {
		GUIDashlet[] tmp = new GUIDashlet[dashlets.length + 1];
		for (int i = 0; i < dashlets.length; i++) {
			tmp[i] = dashlets[i];
		}
		tmp[groups.length] = dashlet;
		dashlets = tmp;
	}

	public void removeGroup(String groupName) {
		if (groups.length == 0)
			return;

		if (isMemberOf(groupName)) {
			GUIGroup[] tmp = new GUIGroup[groups.length - 1];
			int i = 0;
			for (GUIGroup g : groups) {
				if (!g.getName().equals(groupName))
					tmp[i++] = g;
			}
			groups = tmp;
		}
	}

	public void removeDashlet(int id) {
		if (dashlets.length == 0)
			return;

		GUIDashlet[] tmp = new GUIDashlet[dashlets.length - 1];
		int i = 0;
		for (GUIDashlet g : dashlets) {
			if (g.getId() != id)
				tmp[i++] = g;
		}
		dashlets = tmp;

	}

	public int getCheckedOutDocs() {
		return checkedOutDocs;
	}

	public void setCheckedOutDocs(int checkedOutDocs) {
		if (this.checkedOutDocs != checkedOutDocs) {
			this.checkedOutDocs = checkedOutDocs;
			notifyObservers(CHECKED_OUT_DOCS);
		}
	}

	public int getLockedDocs() {
		return lockedDocs;
	}

	public void setLockedDocs(int lockedDocs) {
		if (this.lockedDocs != lockedDocs) {
			this.lockedDocs = lockedDocs;
			notifyObservers(LOCKED_DOCS);
		}
	}

	public int getMessages() {
		return messages;
	}

	public void setMessages(int messages) {
		if (this.messages != messages) {
			this.messages = messages;
			notifyObservers(ALL_MESSAGES);
		}
	}

	public void notifyObservers(String attribute) {
		for (UserObserver listener : observers) {
			listener.onUserChanged(this, attribute);
		}
	}

	public void addObserver(UserObserver observer) {
		observers.add(observer);
	}

	public int getUnreadMessages() {
		return unreadMessages;
	}

	public void setUnreadMessages(int unreadMessages) {
		if (this.unreadMessages != unreadMessages) {
			this.unreadMessages = unreadMessages;
			notifyObservers(UNREAD_MESSAGES);
		}
	}

	public int getActiveTasks() {
		return tasks;
	}

	public void setActiveTasks(int tasks) {
		if (this.tasks != tasks) {
			this.tasks = tasks;
			notifyObservers(ALL_TASKS);
		}
	}

	public Long[] getMenues() {
		return menues;
	}

	public void setMenues(Long[] menues) {
		this.menues = menues;
	}

	public int getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(int subscriptions) {
		if (this.subscriptions != subscriptions) {
			this.subscriptions = subscriptions;
			notifyObservers(ALL_SUBSCRIPTIONS);
		}
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

	public String getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(String signatureId) {
		this.signatureId = signatureId;
	}

	public String getSignatureInfo() {
		return signatureInfo;
	}

	public void setSignatureInfo(String signatureInfo) {
		this.signatureInfo = signatureInfo;
	}

	public Integer getWelcomeScreen() {
		return welcomeScreen;
	}

	public void setWelcomeScreen(Integer welcomeScreen) {
		this.welcomeScreen = welcomeScreen;
	}

	public String getIpWhitelist() {
		return ipWhitelist;
	}

	public void setIpWhitelist(String ipWhitelist) {
		this.ipWhitelist = ipWhitelist;
	}

	public String getIpBlacklist() {
		return ipBlacklist;
	}

	public void setIpBlacklist(String ipBlacklist) {
		this.ipBlacklist = ipBlacklist;
	}

	public GUIDashlet[] getDashlets() {
		return dashlets;
	}

	public void setDashlets(GUIDashlet[] dashlets) {
		this.dashlets = dashlets;
	}

	public boolean isNotifyCredentials() {
		return notifyCredentials;
	}

	public void setNotifyCredentials(boolean notifyCredentials) {
		this.notifyCredentials = notifyCredentials;
	}

	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public int getUpcomingEvents() {
		return upcomingEvents;
	}

	public void setUpcomingEvents(int events) {
		if (this.upcomingEvents != events) {
			this.upcomingEvents = events;
			notifyObservers(EVENTS);
		}
	}

	@Override
	public String toString() {
		return (getFirstName() != null ? getFirstName() : "") + " " + (getName() != null ? getName() : "");
	}
}