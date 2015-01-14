package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
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
	public void delete(String sid, long templateId) throws ServerException;

	/**
	 * Creates or updates a template
	 */
	public GUITemplate save(String sid, GUITemplate template) throws ServerException;

	/**
	 * Loads a given template from the database
	 */
	public GUITemplate getTemplate(String sid, long templateId) throws ServerException;

	/**
	 * Saves the list of all possible options
	 */
	public void saveOptions(String sid, long templateId, String attribute, String[] values) throws ServerException;

	/**
	 * Delete a selection of options
	 */
	public void deleteOptions(String sid, long templateId, String attribute, String[] values) throws ServerException;

	/**
	 * Reads the contacts that are about to beimported from CSV
	 */
	public String[] parseOptions(String sid, long templateId, String attribute) throws ServerException;
}