package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.beans.GUITag;

/**
 * Service responsible of Searches
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {

	/**
	 * Performs a search against the database
	 * 
	 * @param sid The current user session
	 * @param options The search options
	 * @return Result hits and statistics
	 */
	public GUIResult search(String sid, GUISearchOptions options) throws InvalidSessionException;

	/**
	 * Saves the search options in the user's working dir
	 * 
	 * @param sid The current user session
	 * @param options The search options
	 * @return true if the operation was successful and there were no duplicates
	 */
	public boolean save(String sid, GUISearchOptions options) throws InvalidSessionException;

	/**
	 * Deletes a previously saved search
	 * 
	 * @param sid The current user session
	 * @param names The saved search names
	 */
	public void delete(String sid, String[] names) throws InvalidSessionException;

	/**
	 * Loads a saved search
	 * 
	 * @param sid The current user session
	 * @param name The saved search name
	 * @return
	 */
	public GUISearchOptions load(String sid, String name) throws InvalidSessionException;

	/**
	 * Loads the tag cloud from the server
	 */
	public GUITag[] getTagCloud();

    /**
     * Computes the options for a similarity search
     */
	public GUISearchOptions getSimilarityOptions(String sid, long docId, String locale) throws InvalidSessionException;
}