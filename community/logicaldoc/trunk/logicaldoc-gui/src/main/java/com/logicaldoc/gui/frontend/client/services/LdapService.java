package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;

/**
 * The client side stub for the LdapService.
 */
@RemoteServiceRelativePath("ldap")
public interface LdapService extends RemoteService {
	/**
	 * Saves external authentication settings
	 */
	public void saveSettings(String sid, GUILdapSettings ldapSettings) throws InvalidSessionException;

	/**
	 * Loads external authentication settings
	 */
	public GUILdapSettings loadSettings(String sid) throws InvalidSessionException;
}
