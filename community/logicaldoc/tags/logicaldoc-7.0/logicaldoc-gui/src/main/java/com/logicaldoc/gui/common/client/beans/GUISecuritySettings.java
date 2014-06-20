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

	private boolean enableAnonymousLogin = false;

	private GUIUser anonymousUser = null;

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

	public boolean isSaveLogin() {
		return saveLogin;
	}

	public void setSaveLogin(boolean saveLogin) {
		this.saveLogin = saveLogin;
	}

	public GUIUser getAnonymousUser() {
		return anonymousUser;
	}

	public void setAnonymousUser(GUIUser anonymousUser) {
		this.anonymousUser = anonymousUser;
	}

	public boolean isEnableAnonymousLogin() {
		return enableAnonymousLogin;
	}

	public void setEnableAnonymousLogin(boolean enableAnonymousLogin) {
		this.enableAnonymousLogin = enableAnonymousLogin;
	}

}
