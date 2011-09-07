package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Security Settings bean as used in the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUISecuritySettings implements Serializable {

	private static final long serialVersionUID = 1L;

	private int pwdSize;

	private int pwdExpiration;

	private boolean saveLogin = false;

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

		GUIUser[] tmp = new GUIUser[notifiedUsers.length - 1];
		int i = 0;
		for (GUIUser u : notifiedUsers) {
			if (!u.getUserName().equals(username)) {
				tmp[i] = u;
				i++;
			}
		}
		notifiedUsers = tmp;
	}

	public boolean isSaveLogin() {
		return saveLogin;
	}

	public void setSaveLogin(boolean saveLogin) {
		this.saveLogin = saveLogin;
	}
}
