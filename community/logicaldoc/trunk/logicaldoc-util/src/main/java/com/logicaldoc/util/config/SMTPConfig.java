package com.logicaldoc.util.config;




/**
 * SMTP server configuration
 * 
 * @author Marco Meschieri
 * @version 3.0
 */
public class SMTPConfig extends ContextConfigurator  {
	private static final String EMAIL_SENDER = "EMailSender";

	public SMTPConfig(String resource) {
		super(resource);
	}

	public SMTPConfig() {
		super("com/logicaldoc/core/context.xml");
	}

	public String getDefaultAddress() {
		return getProperty(EMAIL_SENDER, "defaultAddress");
	}

	public void setDefaultAddress(String defaultAddress) {
		setProperty(EMAIL_SENDER, "defaultAddress", defaultAddress);
	}

	public String getHost() {
		return getProperty(EMAIL_SENDER, "host");
	}

	public void setHost(String host) {
		setProperty(EMAIL_SENDER, "host", host);
	}

	public String getPassword() {
		return getProperty(EMAIL_SENDER, "password");
	}

	public void setPassword(String password) {
		setProperty(EMAIL_SENDER, "password", password);
	}

	public String getPort() {
		return getProperty(EMAIL_SENDER, "port");
	}

	public void setPort(String port) {
		setProperty(EMAIL_SENDER, "port", port);
	}

	public String getUsername() {
		return getProperty(EMAIL_SENDER, "username");
	}

	public void setUsername(String username) {
		setProperty(EMAIL_SENDER, "username", username);
	}
}
