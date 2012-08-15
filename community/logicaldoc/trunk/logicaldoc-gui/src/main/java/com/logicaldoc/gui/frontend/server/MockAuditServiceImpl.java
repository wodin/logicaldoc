package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.frontend.client.services.AuditService;

/**
 * Mock service implementation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockAuditServiceImpl extends RemoteServiceServlet implements AuditService {

	private static final long serialVersionUID = 1L;

	@Override
	public void subscribeFolder(String sid, long folderId, boolean currentOnly, String[] events)
			throws InvalidSessionException {

	}

	@Override
	public void subscribeDocuments(String sid, long[] docIds, String[] events) throws InvalidSessionException {

	}

	@Override
	public void deleteSubscriptions(String sid, long[] ids) throws InvalidSessionException {

	}

	@Override
	public void update(String sid, long id, String[] events) throws InvalidSessionException {

	}
}