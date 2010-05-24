package com.logicaldoc.gui.frontend.mock;

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
	public GUISearchEngine getInfos(String sid) {
		return new GUISearchEngine();
	}

	@Override
	public GUISearchEngine rescheduleAll(String sid, GUISearchEngine searchEngine) {
		return new GUISearchEngine();
	}

	@Override
	public GUISearchEngine unlocks(String sid, GUISearchEngine searchEngine) {
		return new GUISearchEngine();
	}
}
