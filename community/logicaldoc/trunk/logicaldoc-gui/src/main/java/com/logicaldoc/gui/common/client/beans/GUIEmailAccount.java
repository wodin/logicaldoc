package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Model of an email account.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIEmailAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	public static String PROVIDER_POP3 = "pop3";

	public static String PROVIDER_IMAP = "imap";

	public static int FORMAT_MULTIPLE = 0;

	public static int FORMAT_EML = 1;

	private String mailAddress;

	private String provider = PROVIDER_POP3;

	private String host;

	private int port = 110;

	private String userName;

	private String password;

	private String mailFolder;

	// Comma separated list of allowed extensions
	private String includes = "*.pdf,*.doc,*.txt";

	private String excludes = "";

	private String language = "";

	private boolean deleteFromMailbox = false;

	private int enabled = 1;

	private boolean ssl = false;

	/**
	 * This attribute defines the EmailAccount storage format.
	 * 
	 * @see EmailAccount#FORMAT_MULTIPLE
	 * @see EmailAccount#FORMAT_EML
	 */
	private int format = FORMAT_EML;

	private GUIFolder target = new GUIFolder();

	private GUIEmailRule[] rules;

	public GUIEmailAccount() {
		super();
		target.setId(5);
		target.setName("/");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMailFolder() {
		return mailFolder;
	}

	public void setMailFolder(String mailFolder) {
		this.mailFolder = mailFolder;
	}

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public String getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public GUIFolder getTarget() {
		return target;
	}

	public void setTarget(GUIFolder target) {
		this.target = target;
	}

	public boolean isDeleteFromMailbox() {
		return deleteFromMailbox;
	}

	public void setDeleteFromMailbox(boolean deleteFromMailbox) {
		this.deleteFromMailbox = deleteFromMailbox;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public GUIEmailRule[] getRules() {
		return rules;
	}

	public void setRules(GUIEmailRule[] rules) {
		this.rules = rules;
	}
}