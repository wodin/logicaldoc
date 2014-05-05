package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIContact;

/**
 * The client side stub for the Contact Service. This service allows r/w
 * operations on contacts.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("contact")
public interface ContactService extends RemoteService {
	/**
	 * Deletes a selection of contacts
	 */
	public void delete(String sid, long[] ids) throws InvalidSessionException;

	/**
	 * Saves a contact in the database
	 */
	public void save(String sid, GUIContact contact) throws InvalidSessionException;

	/**
	 * Loads a contact from the database
	 */
	public GUIContact load(String sid, long id) throws InvalidSessionException;

}
