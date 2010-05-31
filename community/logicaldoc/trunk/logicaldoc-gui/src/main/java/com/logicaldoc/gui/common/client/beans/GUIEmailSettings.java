package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

public class GUIEmailSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int SECURITY_NONE = 0;

	public static final int SECURITY_TLS_IF_AVAILABLE = 1;

	public static final int SECURITY_TLS = 2;

	public static final int SECURITY_SSL = 3;

	private String smtpServer;

	private boolean secureAuth = false;

	private int port;

	private String username;

	private String pwd;

	private int connSecurity = SECURITY_NONE;

	private String senderEmail;

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public boolean isSecureAuth() {
		return secureAuth;
	}

	public void setSecureAuth(boolean secureAuth) {
		this.secureAuth = secureAuth;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getConnSecurity() {
		return connSecurity;
	}

	public void setConnSecurity(int connSecurity) {
		this.connSecurity = connSecurity;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
}
