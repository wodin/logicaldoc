package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * The client side stub for the Form Service. This service gives all needed
 * methods to handle the forms.
 */
@RemoteServiceRelativePath("form")
public interface FormService extends RemoteService {
	/**
	 * Creates a new form
	 */
	public GUIDocument create(String sid, GUIDocument form) throws ServerException;

	/**
	 * Creates a form
	 */
	public void delete(String sid, long formId) throws ServerException;
}