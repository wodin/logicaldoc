package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIEmailAccount;

/**
 * The client side stub for the EmailAccount Service. This service gives all
 * needed methods to handle templates.
 */
@RemoteServiceRelativePath("emailaccount")
public interface EmailAccountService extends RemoteService {
	/**
	 * Deletes a given account
	 */
	public void delete(String sid, long id) throws ServerException;

	/**
	 * Creates or updates an account
	 */
	public GUIEmailAccount save(String sid, GUIEmailAccount account) throws ServerException;

	/**
	 * Loads a given account from the database
	 */
	public GUIEmailAccount get(String sid, long id) throws ServerException;

	/**
	 * Test the connection to the given account
	 */
	public boolean test(String sid, long id) throws ServerException;

	/**
	 * Changes an account enabled/disabled status
	 */
	public void changeStatus(String sid, long id, boolean enabled) throws ServerException;

	/**
	 * Cleans the cache
	 */
	public void resetCache(String sid, long id) throws ServerException;
}