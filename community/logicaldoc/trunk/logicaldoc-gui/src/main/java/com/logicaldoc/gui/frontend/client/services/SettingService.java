package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

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
	 * Saves web services and webDav settings
	 */
	public void saveClientSettings(String sid, GUIParameter[] settings) throws InvalidSessionException;

	/**
	 * Loads web services and webDav settings
	 */
	public GUIParameter[] loadClientSettings(String sid) throws InvalidSessionException;

	/**
	 * Loads the complete settings set
	 */
	public GUIParameter[] loadSettings(String sid) throws InvalidSessionException;

	/**
	 * Loads a set of settings values
	 * 
	 * @param sid The current session identifier
	 * @param names The setting names to be retrieved
	 * @return The array of settings
	 * @throws InvalidSessionException
	 */
	public GUIParameter[] loadSettingsByNames(String sid, String[] names) throws InvalidSessionException;

	/**
	 * Saves settings
	 */
	public void saveSettings(String sid, GUIParameter[] settings) throws InvalidSessionException;

	/**
	 * Loads email settings (SMTP connection)
	 */
	public GUIEmailSettings loadEmailSettings(String sid) throws InvalidSessionException;

	/**
	 * Load the repositories paths.
	 * 
	 * <ol>
	 * <li>The first array contains the folders paths.</li>
	 * <li>The second array contains the available repositories paths.</li>
	 * </ol>
	 * 
	 */
	public GUIParameter[][] loadRepositories(String sid) throws InvalidSessionException;

	/**
	 * Saves folders path
	 */
	public void saveRepositories(String sid, GUIParameter[][] repos) throws InvalidSessionException;

	/**
	 * Saves email settings (SMTP connection)
	 */
	public void saveEmailSettings(String sid, GUIEmailSettings settings) throws InvalidSessionException;

	/**
	 * Loads the OCR settings
	 */
	public GUIParameter[] loadOcrSettings(String sid) throws InvalidSessionException;

	/**
	 * Load the system quota setting
	 */
	public GUIParameter[] loadQuotaSettings(String sid) throws InvalidSessionException;

	/**
	 * Load the GUI settings
	 */
	public GUIParameter[] loadGUISettings(String sid) throws InvalidSessionException;

	/**
	 * Saves system quota setting
	 */
	public void saveQuotaSettings(String sid, GUIParameter[] quotaSettings) throws InvalidSessionException;

	/**
	 * Retrieves the size of all saved storages.
	 * 
	 * @param sid The current user session
	 */
	public GUIParameter[] computeStoragesSize(String sid) throws InvalidSessionException;

	/**
	 * Saves the dashlets configuration for the current user
	 */
	public void saveDashlets(String sid, GUIDashlet[] dashlets) throws InvalidSessionException;
}