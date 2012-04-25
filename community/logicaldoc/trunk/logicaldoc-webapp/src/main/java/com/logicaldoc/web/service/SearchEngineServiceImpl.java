package com.logicaldoc.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.parser.ParserFactory;
import com.logicaldoc.core.searchengine.SearchEngine;
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

			SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
			searchEngine.setLocked(indexer.isLocked());
			searchEngine.setEntries(indexer.getCount());

			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			searchEngine.setExcludePatters(conf.getProperty("index.excludes"));
			searchEngine.setIncludePatters(conf.getProperty("index.includes"));

			if (StringUtils.isNotEmpty(conf.getProperty("index.batch")))
				searchEngine.setBatch(new Integer(conf.getProperty("index.batch")));
			else
				searchEngine.setBatch(0);

			if (StringUtils.isNotEmpty(conf.getProperty("parser.timeout")))
				searchEngine.setParsingTimeout(new Integer(conf.getProperty("parser.timeout")));
			else
				searchEngine.setParsingTimeout(0);

			if (StringUtils.isNotEmpty(conf.getProperty("index.maxtext")))
				searchEngine.setMaxText(new Integer(conf.getProperty("index.maxtext")));
			else
				searchEngine.setMaxText(0);

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
			SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
			indexer.dropIndexes();
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
			SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
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
			SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
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
			conf.setProperty("index.excludes",
					searchEngine.getExcludePatters() != null ? searchEngine.getExcludePatters() : "");
			conf.setProperty("index.includes",
					searchEngine.getIncludePatters() != null ? searchEngine.getIncludePatters() : "");
			conf.setProperty("index.batch", Integer.toString(searchEngine.getBatch()));
			conf.setProperty("index.maxtext", Integer.toString(searchEngine.getMaxText()));
			conf.setProperty("parser.timeout", Integer.toString(searchEngine.getParsingTimeout()));
			conf.write();
		} catch (Exception t) {
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
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void setAliases(String sid, String extension, String aliases) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			StringTokenizer st = new StringTokenizer(aliases, ",", false);
			List<String> buf = new ArrayList<String>();
			while (st.hasMoreElements())
				buf.add(((String) st.nextElement()).trim());

			ParserFactory.setAliases(extension, buf.toArray(new String[0]));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}
}