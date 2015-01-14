package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIShare;

/**
 * The client side stub for the ImportFolder Service. This service gives all
 * needed methods to handle templates.
 */
@RemoteServiceRelativePath("importfolders")
public interface ImportFoldersService extends RemoteService {
	/**
	 * Deletes a given folder
	 */
	public void delete(String sid, long id) throws ServerException;

	/**
	 * Creates or updates an import folder
	 */
	public GUIShare save(String sid, GUIShare share) throws ServerException;

	/**
	 * Loads a given import folder from the database
	 */
	public GUIShare getShare(String sid, long id) throws ServerException;

	/**
	 * Test the connection to the given import folder
	 */
	public boolean test(String sid, long id) throws ServerException;

	/**
	 * Changes a share enabled/disabled status
	 */
	public void changeStatus(String sid, long id, boolean enabled) throws ServerException;

	/**
	 * Cleans the cache
	 */
	public void resetCache(String sid, long id) throws ServerException;
}