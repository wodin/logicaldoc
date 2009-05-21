package com.logicaldoc.email;

import java.util.Locale;
import java.util.StringTokenizer;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.util.LocaleUtil;

/**
 * E-Mail account
 * 
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class EmailAccount extends PersistentObject {
	
	private static final long serialVersionUID = 1L;

	private String mailAddress;

	private String provider;

	private String host;

	private int port = 110;

	private String userName;

	private String password;

	private Menu targetFolder;

	// Comma separated list of allowed extesions
	private String allowedTypes = "pdf,doc,txt";

	private String language = "";

	private int deleteFromMailbox = 0;

	private int enabled = 1;

	/**
	 * This attribute defines the EmailAccount security settings. Possible
	 * values: '0'= "No" '1'= "TLS, se disponibile" '2'= "TLS" '3'= "SSL"
	 */
	private int sslModel = 0;

	private int extractTags = 0;

	/** Creates a new instance of EmailAccount */
	public EmailAccount() {
		targetFolder = null;
	}

	public int getExtractTags() {
		return extractTags;
	}

	public void setExtractTags(int extractTags) {
		this.extractTags = extractTags;
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

	public String getMailAddress() {
		return mailAddress;
	}

	public String getProvider() {
		return provider;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
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

	public void setPort(int port) {
		this.port = port;
	}

	public void setUserName(String user) {
		this.userName = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Menu getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(Menu targetFolder) {
		this.targetFolder = targetFolder;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getSslModel() {
		return sslModel;
	}

	public void setSslModel(int sslModel) {
		this.sslModel = sslModel;
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
		userName = "";
		password = "";
		targetFolder = null;
		language = "";
		deleteFromMailbox = 0;
	}

	@Override
	public String toString() {
		return mailAddress;
	}
	
	public Locale getLocale() {
		return LocaleUtil.toLocale(getLanguage());
	}

	public void setLocale(Locale locale) {
		setLanguage(locale.toString());
	}
}