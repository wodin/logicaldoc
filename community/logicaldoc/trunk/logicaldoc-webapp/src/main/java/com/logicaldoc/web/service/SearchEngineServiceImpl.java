package com.logicaldoc.web.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SearchEngineService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SearchEngineServiceImpl extends RemoteServiceServlet implements SearchEngineService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SearchEngineServiceImpl.class);

	@Override
	public GUISearchEngine getInfo(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		GUISearchEngine searchEngine = new GUISearchEngine();
		searchEngine.setEntries((int) dao.count(true));
		Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
		searchEngine.setLocked(indexer.isLocked());

		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		searchEngine.setExcludePatters(conf.getPropertyWithSubstitutions("index.excludes"));
		searchEngine.setIncludePatters(conf.getPropertyWithSubstitutions("index.includes"));

		return searchEngine;
	}

	@Override
	public void rescheduleAll(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			indexer.recreateIndexes();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Runnable task = new Runnable() {
			public void run() {
				try {
					DocumentDAO documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					Collection<Document> documents = documentDao.findAll();
					Iterator<Document> iter = documents.iterator();
					while (iter.hasNext()) {
						Document document = iter.next();
						documentDao.initialize(document);
						document.setIndexed(AbstractDocument.INDEX_TO_INDEX);
						documentDao.store(document);
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		};

		Thread recreateThread = new Thread(task);
		recreateThread.start();

		return;
	}

	@Override
	public void unlocks(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			indexer.unlock();
		} catch (Exception e) {
		}
	}

	@Override
	public void save(String sid, GUISearchEngine searchEngine) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		try {
			conf.setProperty("index.excludes", searchEngine.getExcludePatters());
			conf.setProperty("index.includes", searchEngine.getIncludePatters());

			conf.write();
		} catch (IOException e) {
		}
	}
}