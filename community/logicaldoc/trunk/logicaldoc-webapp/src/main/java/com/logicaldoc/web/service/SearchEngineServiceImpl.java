package com.logicaldoc.web.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
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
		try {
			GUISearchEngine searchEngine = new GUISearchEngine();

			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			searchEngine.setLocked(indexer.isLocked());
			searchEngine.setEntries(indexer.getCount());

			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			searchEngine.setExcludePatters(conf.getProperty("index.excludes"));
			searchEngine.setIncludePatters(conf.getProperty("index.includes"));
			
			if (StringUtils.isNotEmpty(conf.getProperty("index.batch")))
				searchEngine.setBatch(new Integer(conf.getProperty("index.batch")));
			else
				searchEngine.setBatch(0);

			// Populate the list of supported languages
			searchEngine.setLanguages("");
			LanguageManager lm = LanguageManager.getInstance();
			List<String> langs = lm.getLanguagesAsString();
			for (String lang : langs) {
				searchEngine.setLanguages(searchEngine.getLanguages() + "," + lang);
			}
			if (searchEngine.getLanguages().startsWith(","))
				searchEngine.setLanguages(searchEngine.getLanguages().substring(1));

			return searchEngine;
		} catch (Exception t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void rescheduleAll(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			indexer.recreateIndexes();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		Runnable task = new Runnable() {
			public void run() {
				try {
					DocumentDAO documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					documentDao.bulkUpdate("set ld_indexed=0 where ld_indexed=1", null);
				} catch (Exception t) {
					log.error(t.getMessage(), t);
					throw new RuntimeException(t.getMessage(), t);
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
		} catch (Exception t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public String check(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			return indexer.check();
		} catch (Exception t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void save(String sid, GUISearchEngine searchEngine) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			conf.setProperty("index.excludes", searchEngine.getExcludePatters());
			conf.setProperty("index.includes", searchEngine.getIncludePatters());
			conf.setProperty("index.batch", Integer.toString(searchEngine.getBatch()));
			conf.write();
		} catch (IOException t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void setLanguageStatus(String sid, String language, boolean active) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			conf.setProperty("lang." + language, active ? "enabled" : "disabled");
			conf.write();
		} catch (IOException t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}
}