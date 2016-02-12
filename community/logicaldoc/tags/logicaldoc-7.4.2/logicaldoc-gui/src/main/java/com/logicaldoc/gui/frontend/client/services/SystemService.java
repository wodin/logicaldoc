package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIValue;

/**
 * The client side stub for the System Service. This service allows the
 * management of various system settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("system")
public interface SystemService extends RemoteService {

	/**
	 * Retrieves all the statistics parameters.
	 * 
	 * <ol>
	 * <li>The first array is the Repository statistics.</li>
	 * <li>The second array is the Documents statistics.</li>
	 * <li>The third array is the Folders statistics.</li>
	 * <li>The fourth array contains the last run date.</li>
	 * </ol>
	 * 
	 * @param sid The current user session
	 * @param locale The current user locale
	 */
	public GUIParameter[][] getStatistics(String sid, String locale) throws ServerException;

	/**
	 * Performs a search over the last changes.
	 * 
	 * @param sid The current user session
	 * @param username The user name that must be associated to the history
	 * @param from The starting date to search the histories
	 * @param till The ending date to search the histories
	 * @param maxResult The maximum number of history results
	 * @param historySid The history session identifier
	 * @param event The history events
	 * @return Result hits and statistics
	 */
	public GUIHistory[] search(String sid, String userName, Date from, Date till, int maxResult, String historySid,
			String[] event) throws ServerException;

	/**
	 * Retrieves all tasks.
	 * 
	 * @param sid The current user session
	 * @param locale The current user locale
	 */
	public GUITask[] loadTasks(String sid, String locale) throws ServerException;

	/**
	 * Starts the task execution.
	 * 
	 * @param taskName The task name
	 * @return True, if the task is correctly started.
	 */
	public boolean startTask(String taskName);

	/**
	 * Stops the task execution.
	 * 
	 * @param taskName The task name
	 * @return True, if the task is correctly stopped.
	 */
	public boolean stopTask(String taskName);

	/**
	 * Retrieves a specific task by its name
	 * 
	 * @param sid The current user session
	 * @param taskName The task name
	 * @param locale The current user locale
	 */
	public GUITask getTaskByName(String sid, String taskName, String locale) throws ServerException;

	/**
	 * Enables the task.
	 * 
	 * @param sid The current user session
	 * @param taskName The task name
	 * @return True, if the task is correctly enabled.
	 */
	public boolean enableTask(String sid, String taskName) throws ServerException;

	/**
	 * Disables the task.
	 * 
	 * @param sid The current user session
	 * @param taskName The task name
	 * @return True, if the task is correctly disabled.
	 */
	public boolean disableTask(String sid, String taskName) throws ServerException;

	/**
	 * Saves the task.
	 * 
	 * @param sid The current user session
	 * @param task The task to be saved
	 * @param locale The current user locale
	 * @return True, if the task is correctly saved.
	 */
	public GUITask saveTask(String sid, GUITask task, String locale) throws ServerException;

	/**
	 * Changes the activation status of a language
	 */
	public void setGUILanguageStatus(String sid, String language, boolean active) throws ServerException;

	/**
	 * Marks as read a list of Feed Messages
	 */
	public void markFeedMsgAsRead(String sid, long[] ids) throws ServerException;

	/**
	 * Marks as not read a list of Feed Messages
	 */
	public void markFeedMsgAsNotRead(String sid, long[] ids) throws ServerException;

	/**
	 * Deletes a list of Feed Messages
	 */
	public void deleteFeedMessages(String sid, long[] ids) throws ServerException;

	/**
	 * Retrieves all plugins.
	 * 
	 * @param sid The current user session
	 */
	public GUIValue[] getPlugins(String sid) throws ServerException;
}