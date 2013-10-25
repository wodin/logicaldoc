package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIMessageTemplate;

/**
 * The client side stub for the Message Service. This service allows r/w
 * operations on folders.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("message")
public interface MessageService extends RemoteService {

	/**
	 * Gets the Message
	 */
	public GUIMessage getMessage(String sid, long messageId, boolean markAsRead) throws InvalidSessionException;

	/**
	 * Deletes a list of Messages
	 */
	public void delete(String sid, long[] ids) throws InvalidSessionException;

	/**
	 * Stores a new Message
	 */
	public void save(String sid, GUIMessage message) throws InvalidSessionException;

	/**
	 * Loads the templates configured for a given language.
	 */
	public GUIMessageTemplate[] loadTemplates(String sid, String language) throws InvalidSessionException;

	/**
	 * Saves the given templates
	 */
	public void saveTemplates(String sid, GUIMessageTemplate[] templates) throws InvalidSessionException;

	/**
	 * Deletes a selection of templates
	 */
	public void deleteTemplates(String sid, long[] ids) throws InvalidSessionException;
}
