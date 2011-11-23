package com.logicaldoc.gui.common.client.beans;

/**
 * Active Directory Settings bean as used in the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUIADSettings extends GUILdapSettings {

	private static final long serialVersionUID = 1L;

	private String domain;

	private String host;

	private int port;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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
}
