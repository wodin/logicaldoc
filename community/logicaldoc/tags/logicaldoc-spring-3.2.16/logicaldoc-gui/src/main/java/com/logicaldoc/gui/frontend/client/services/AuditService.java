package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;

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
	public void subscribeFolder(String sid, long folderId, boolean currentOnly, String[] events, Long user, Long group)
			throws ServerException;

	/**
	 * Subscribes a selection of documents
	 */
	public void subscribeDocuments(String sid, long[] docIds, String[] events, Long userId, Long groupId)
			throws ServerException;

	/**
	 * Changes a set of subscriptions
	 */
	public void update(String sid, long[] ids, boolean currentOnly, String[] events) throws ServerException;

	/**
	 * Deletes a list of Subscriptions
	 */
	public void deleteSubscriptions(String sid, long[] ids) throws ServerException;
}
