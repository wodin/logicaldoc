package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;

/**
 * The client side stub for the Settings Service. This service allows the
 * management of various application settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("setting")
public interface SettingService extends RemoteService {
	/**
	 * Saves web services and web service settings
	 */
	public void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings)
			throws InvalidSessionException;

	/**
	 * Loads web services and web service settings
	 */
	public GUIWebServiceSettings[] loadWSSettings(String sid) throws InvalidSessionException;

	/**
	 * Loads settings
	 */
	public GUIParameter[] loadSettings(String sid) throws InvalidSessionException;

	/**
	 * Saves settings
	 */
	public void saveSettings(String sid, GUIParameter[] settings) throws InvalidSessionException;

	/**
	 * Loads email settings (SMTP connection)
	 */
	public GUIEmailSettings loadEmailSettings(String sid) throws InvalidSessionException;

	/**
	 * Saves email settings (SMTP connection)
	 */
	public void saveEmailSettings(String sid, GUIEmailSettings settings) throws InvalidSessionException;

	/**
	 * Loads a set of settings values
	 * 
	 * @param sid The current session identifier
	 * @param names The setting names to be retrieved
	 * @return The array of values
	 * @throws InvalidSessionException
	 */
	public String[] loadValues(String sid, String[] names) throws InvalidSessionException;
}
