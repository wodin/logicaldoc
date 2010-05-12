package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

import com.logicaldoc.gui.frontend.client.Log;

public class GUISecuritySettings implements Serializable {

	private static final long serialVersionUID = 1L;

	private int pwdSize;

	private int pwdExpiration;

	private GUIUser[] notifiedUsers = new GUIUser[0];

	public GUIUser[] getNotifiedUsers() {
		return notifiedUsers;
	}

	public void setNotifiedUsers(GUIUser[] notifiedUsers) {
		this.notifiedUsers = notifiedUsers;
	}

	public int getPwdSize() {
		return pwdSize;
	}

	public void setPwdSize(int pwdSize) {
		this.pwdSize = pwdSize;
	}

	public int getPwdExpiration() {
		return pwdExpiration;
	}

	public void setPwdExpiration(int pwdExpiration) {
		this.pwdExpiration = pwdExpiration;
	}

	public void clearNotifiedUsers() {
		this.notifiedUsers = new GUIUser[] {};
	}

	public void addNotifiedUser(GUIUser user) {
		GUIUser[] tmp = new GUIUser[notifiedUsers.length + 1];
		int i = 0;
		for (GUIUser u : notifiedUsers) {
			// Skip if the user already exists
			if (u.getUserName().equals(user.getUserName()))
				return;
			tmp[i++] = u;
		}
		tmp[i] = user;
		notifiedUsers = tmp;
	}

	public void removeNotifiedUser(String username) {
		if (notifiedUsers.length == 0)
			return;

		Log.debug("***notifiedUsers: " + notifiedUsers.length);

		GUIUser[] tmp = new GUIUser[notifiedUsers.length - 1];
		int i = 0;
		for (GUIUser u : notifiedUsers) {
			Log.debug("***u: " + u.getUserName());
			Log.debug("***username: " + username);

			if (!u.getUserName().equals(username)) {
				Log.debug("add!!!");
				tmp[i] = u;
				i++;
			}
			Log.debug("i: " + i);
		}
		notifiedUsers = tmp;
	}
}
