package com.logicaldoc.core.communication;

import java.util.StringTokenizer;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.security.Menu;

/**
 * E-Mail account
 * 
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 */
public class EMailAccount extends PersistentObject {
	private static final long serialVersionUID = 1L;

	private long userId;

	private String mailAddress;

	private String provider;

	private String host;

	private String port;

	private String user;

	private String password;

	private Menu targetFolder;

	private Long targetFolderId;

	// Comma separated list of allowed extesions
	private String allowedTypes = "pdf,doc,txt";

	private String language = "";

	private int deleteFromMailbox = 0;

	private int enabled = 1;

	/** Creates a new instance of EMailAccount */
	public EMailAccount() {
		userId = -1;
		mailAddress = "";
		provider = "";
		host = "";
		port = "";
		user = "";
		password = "";
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

	public Long getTargetFolderId() {
		return targetFolderId;
	}

	public void setTargetFolderId(Long targetFolderId) {
		this.targetFolderId = targetFolderId;
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

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
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

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Menu getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(Menu targetFolder) {
		this.targetFolder = targetFolder;
		if (targetFolder != null)
			this.targetFolderId = targetFolder.getId();
		else
			this.targetFolderId = null;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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
		mailAddress = "";
		provider = "";
		host = "";
		port = "";
		userId = -1;
		user = "";
		password = "";
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