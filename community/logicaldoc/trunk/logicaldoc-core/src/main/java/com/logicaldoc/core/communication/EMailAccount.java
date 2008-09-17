package com.logicaldoc.core.communication;

import java.util.StringTokenizer;

import com.logicaldoc.core.security.Menu;

/**
 * E-Mail account
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public class EMailAccount {
	private static final long serialVersionUID = 1L;

	private int accountId;

	private String userName;

	private String mailAddress;

	private String provider;

	private String host;

	private String port;

	private String accountUser;

	private String accountPassword;

	private Menu targetFolder;

	// Used only by Struts
	private Integer targetFolderId;

	// Comma separated list of allowed extesions
	private String allowedTypes = "pdf,doc,txt";

	private String language = "";

	private int deleteFromMailbox = 0;

	private int enabled = 1;

	/** Creates a new instance of EMailAccount */
	public EMailAccount() {
		accountId = 0;
		userName = "";
		mailAddress = "";
		provider = "";
		host = "";
		port = "";
		accountUser = "";
		accountPassword = "";
		targetFolder = null;
		targetFolderId = null;
	}

	public int getDeleteFromMailbox() {
		return deleteFromMailbox;
	}

	public void setDeleteFromMailbox(int deleteFromMailbox) {
		if ((deleteFromMailbox < 0) || (deleteFromMailbox > 1)) {
			this.deleteFromMailbox = 0;
		} else {
			this.deleteFromMailbox = deleteFromMailbox;
		}
	}

	public String getAllowedTypes() {
		return allowedTypes;
	}

	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getTargetFolderId() {
		return targetFolderId;
	}

	public void setTargetFolderId(Integer targetFolderId) {
		this.targetFolderId = targetFolderId;
	}

	public int getAccountId() {
		return accountId;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public String getProvider() {
		return provider;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getAccountUser() {
		return accountUser;
	}

	public String getAccountPassword() {
		return accountPassword;
	}

	public void setAccountId(int id) {
		accountId = id;
	}

	public void setMailAddress(String address) {
		mailAddress = address;
	}

	public void setProvider(String prov) {
		provider = prov;
	}

	public void setHost(String hst) {
		host = hst;
	}

	public void setPort(String prt) {
		port = prt;
	}

	public void setAccountUser(String auser) {
		accountUser = auser;
	}

	public void setAccountPassword(String apwd) {
		accountPassword = apwd;
	}

	public Menu getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(Menu targetFolder) {
		this.targetFolder = targetFolder;
		if (targetFolder != null)
			this.targetFolderId = targetFolder.getMenuId();
		else
			this.targetFolderId = null;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	/**
	 * Check if the specified type is allowed or not.
	 * 
	 * @param type The type to check
	 * @return True if <code>type</code> is included in
	 *         <code>allowedTypes</code>
	 */
	public boolean isAllowed(String type) {
		StringTokenizer st = new StringTokenizer(allowedTypes, ",", false);
		while (st.hasMoreTokens()) {
			if (st.nextToken().equalsIgnoreCase(type))
				return true;

		}
		return false;
	}

	public void reset() {
		accountId = 0;
		mailAddress = "";
		provider = "";
		host = "";
		port = "";
		userName = "";
		accountUser = "";
		accountPassword = "";
		targetFolder = null;
		targetFolderId = null;
		language = "";
		deleteFromMailbox = 0;
	}

	@Override
	public String toString() {
		return mailAddress;
	}
}