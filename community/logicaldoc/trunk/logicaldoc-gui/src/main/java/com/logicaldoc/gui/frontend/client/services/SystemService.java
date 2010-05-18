package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

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
	 * </ol>
	 */
	public GUIParameter[][] getStatistics(String sid);

	/**
	 * Performs a search over the last changes.
	 * 
	 * @param sid The current user session
	 * @param username The user name that must be associated to the history
	 * @param from The starting date to search the histories
	 * @param till The ending date to search the histories
	 * @param maxResult The maximum number of histoty results
	 * @param historySid The history session identifier
	 * @param event The history events
	 * @return Result hits and statistics
	 */
	public GUIHistory[] search(String sid, String userName, Date from, Date till, int maxResult, String historySid, String[] event);
}
