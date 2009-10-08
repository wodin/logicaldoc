package com.logicaldoc.web.setup;

/**
 * Simple value object for SMTP server configuration
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class SmtpData {
	private String host = "localhost";

	private Integer port = new Integer(25);

	private String username="";

	private String password="";

	private String sender="logicaldoc@acme.com";

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

}
