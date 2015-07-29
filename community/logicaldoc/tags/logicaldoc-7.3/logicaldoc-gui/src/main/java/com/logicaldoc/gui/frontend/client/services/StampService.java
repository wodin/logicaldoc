package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIStamp;

/**
 * The client side stub for the Stamp Service. This service gives all needed
 * methods to handle the stamps.
 */
@RemoteServiceRelativePath("stamp")
public interface StampService extends RemoteService {
	/**
	 * Deletes a given stamp
	 */
	public void delete(String sid, long id) throws ServerException;

	/**
	 * Creates or updates a stamp
	 */
	public GUIStamp save(String sid, GUIStamp stamp) throws ServerException;

	/**
	 * Saves the stamp's image
	 */
	public void saveImage(String sid, long stampId) throws ServerException;

	/**
	 * Loads a given stamp from the database
	 */
	public GUIStamp getStamp(String sid, long id) throws ServerException;

	/**
	 * Changes a stamp enabled/disabled status
	 */
	public void changeStatus(String sid, long id, boolean enabled) throws ServerException;

	/**
	 * Applies a stamp to the given document
	 */
	public void applyStamp(String sid, long[] docIds, long stampId) throws ServerException;

	/**
	 * Remove users from stamp
	 */
	public void removeUsers(String sid, long[] userIds, long stampId) throws ServerException;

	/**
	 * Assignes users to stamp
	 */
	public void addUsers(String sid, long[] userIds, long stampId) throws ServerException;
}