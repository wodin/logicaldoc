package com.logicaldoc.web.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SearchEngineService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SearchEngineServiceImpl extends RemoteServiceServlet implements SearchEngineService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUISearchEngine getInfo(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		GUISearchEngine searchEngine = new GUISearchEngine();
		searchEngine.setEntries((int) dao.count(true));
		Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
		searchEngine.setLocked(indexer.isLocked());

		return searchEngine;
	}

	@Override
	public GUISearchEngine rescheduleAll(String sid, GUISearchEngine searchEngine) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		
		

		return new GUISearchEngine();
	}

	@Override
	public GUISearchEngine unlocks(String sid, GUISearchEngine searchEngine) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			if (searchEngine.isLocked()) {
				indexer.unlock();
			}
			searchEngine.setLocked(indexer.isLocked());
		} catch (Exception e) {
		}

		return searchEngine;
	}

	@Override
	public void save(String sid, GUISearchEngine searchEngine) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		
		

	}
}