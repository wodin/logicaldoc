package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;

/**
 * The client side stub for the Audit Service. This service allows folders and
 * documents subscription.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("audit")
public interface AuditService extends RemoteService {

	/**
	 * Subscribes a folder
	 */
	public void subscribeFolder(String sid, long folderId, boolean currentOnly) throws InvalidSessionException;

	/**
	 * Subscribes a selection of documents
	 */
	public void subscribeDocuments(String sid, long[] docIds) throws InvalidSessionException;

	/**
	 * Deletes a list of Subscriptions
	 */
	public void deleteSubscriptions(String sid, long[] ids) throws InvalidSessionException;
}
