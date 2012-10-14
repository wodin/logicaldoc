package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIMessageTemplate;
import com.logicaldoc.gui.frontend.client.services.MessageService;

/**
 * Mock service implementation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockMessageServiceImpl extends RemoteServiceServlet implements MessageService {

	private static final long serialVersionUID = 1L;

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
		// Send a notification message to the sender if 'Confirmation' is set
		return message;
	}

	@Override
	public void save(String sid, GUIMessage message) throws InvalidSessionException {
		return;
	}

	@Override
	public GUIMessageTemplate[] loadTemplates(String sid, String language) throws InvalidSessionException {
		return null;
	}

	@Override
	public void saveTemplates(String sid, GUIMessageTemplate[] templates) throws InvalidSessionException {

	}

	@Override
	public void deleteTemplates(String sid, long[] ids) throws InvalidSessionException {
		// TODO Auto-generated method stub
		
	}
}
