package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;

/**
 * The client side stub for the Search Engine Service.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("searchengine")
public interface SearchEngineService extends RemoteService {
	/**
	 * Loads a search engine that contains all search engine infos.
	 */
	public GUISearchEngine getInfos(String sid);

	/**
	 * Unlocks the indexer.
	 */
	public GUISearchEngine unlocks(String sid, GUISearchEngine searchEngine);

	/**
	 * Reschedule all entries for indexing.
	 */
	public GUISearchEngine rescheduleAll(String sid, GUISearchEngine searchEngine);

	/**
	 * Saves search engine settings
	 */
	public void save(String sid, GUISearchEngine searchEngine);
}