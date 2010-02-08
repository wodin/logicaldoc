package com.logicaldoc.web.setup;

/**
 * Simple value object for SMTP server configuration
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class SmtpData {

	public static final int SECURITY_NONE = 0;

	public static final int SECURITY_TLS_IF_AVAILABLE = 1;

	public static final int SECURITY_TLS = 2;

	public static final int SECURITY_SSL = 3;

	private String host = "localhost";

	private Integer port = new Integer(25);

	private String username = "";

	private String password = "";

	private String sender = "logicaldoc@acme.com";

	private boolean authEncripted = false;

	private int connectionSecurity = SECURITY_NONE;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAuthEncripted() {
		return authEncripted;
	}

	public void setAuthEncripted(boolean authEncripted) {
		this.authEncripted = authEncripted;
	}

	public int getConnectionSecurity() {
		return connectionSecurity;
	}

	public void setConnectionSecurity(int connectionSecurity) {
		this.connectionSecurity = connectionSecurity;
	}
}
