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
	 * Saves web services and webdav settings
	 */
	public void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings) throws InvalidSessionException;

	/**
	 * Loads web services and webdav settings
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
	 * Loads email settings
	 */
	public GUIEmailSettings loadEmailSettings(String sid) throws InvalidSessionException;

	/**
	 * Saves email settings
	 */
	public void saveEmailSettings(String sid, GUIEmailSettings settings) throws InvalidSessionException;
}
