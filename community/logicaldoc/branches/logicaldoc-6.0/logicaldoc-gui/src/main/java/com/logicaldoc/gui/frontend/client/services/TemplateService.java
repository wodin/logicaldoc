package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUITemplate;

/**
 * The client side stub for the Template Service. This service gives all needed
 * methods to handle templates.
 */
@RemoteServiceRelativePath("template")
public interface TemplateService extends RemoteService {
	/**
	 * Deletes a given template
	 */
	public void delete(String sid, long templateId) throws InvalidSessionException;

	/**
	 * Creates or updates a template
	 */
	public GUITemplate save(String sid, GUITemplate template) throws InvalidSessionException;

	/**
	 * Loads a given template from the database
	 */
	public GUITemplate getTemplate(String sid, long templateId) throws InvalidSessionException;
}