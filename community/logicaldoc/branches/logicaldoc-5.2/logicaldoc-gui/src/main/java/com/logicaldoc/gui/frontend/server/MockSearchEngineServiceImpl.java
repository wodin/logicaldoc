package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;

/**
 * Implementation of the SearchEngineService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MockSearchEngineServiceImpl extends RemoteServiceServlet implements SearchEngineService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUISearchEngine getInfo(String sid) {
		GUISearchEngine searchEngine = new GUISearchEngine();
		searchEngine.setEntries(556);
		searchEngine.setLocked(true);

		return searchEngine;
	}

	@Override
	public void rescheduleAll(String sid) {
		return;
	}

	@Override
	public void unlocks(String sid) {
		return;
	}

	@Override
	public void save(String sid, GUISearchEngine searchEngine) {

	}
}
