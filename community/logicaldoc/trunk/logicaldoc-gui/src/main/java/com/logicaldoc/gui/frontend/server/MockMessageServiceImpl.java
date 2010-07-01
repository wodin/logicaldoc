package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.frontend.client.services.MessageService;

/**
 * Mock service implementation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockMessageServiceImpl extends RemoteServiceServlet implements MessageService {

	@Override
	public void delete(String sid, long[] ids) throws InvalidSessionException {
		return;
	}

	@Override
	public GUIMessage getMessage(String sid, long messageId, boolean markAsRead) throws InvalidSessionException {
		GUIMessage message = new GUIMessage();
		message.setId(messageId);
		message.setMessage("Message text " + messageId);
		// The message must be marked as read in the DB
		return message;
	}

	@Override
	public void save(String sid, GUIMessage message) throws InvalidSessionException {
		return;
	}
}
