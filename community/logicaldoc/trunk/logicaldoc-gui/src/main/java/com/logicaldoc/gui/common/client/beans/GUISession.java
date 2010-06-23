package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Informations about the session created on the server side.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUISession implements Serializable {

	private static final long serialVersionUID = 1L;

	private GUIUser user;

	private boolean loggedIn = false;

	private String sid;

	private GUIValuePair[] bundle;

	public GUIUser getUser() {
		return user;
	}

	public void setUser(GUIUser user) {
		this.user = user;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public GUIValuePair[] getBundle() {
		return bundle;
	}

	public void setBundle(GUIValuePair[] bundle) {
		this.bundle = bundle;
	}
}
