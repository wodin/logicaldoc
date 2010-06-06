package com.logicaldoc.web.admin;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Informations about full-text indexes and basic administration commands
 * 
 * @author Michael Scholz
 */
public class IndexInfo {
	protected static Log log = LogFactory.getLog(IndexInfo.class);

	private Indexer indexer;

	private String includes = "";

	private String excludes = "";

	public IndexInfo() {
		indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
	}

	public String getIndexDir() {
		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		return conf.getPropertyWithSubstitutions("conf.indexdir");
	}

	public String unlock() {
		if (SessionManagement.isValid()) {
			try {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				indexer.unlock();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Optimises all indexed
	 * 
	 * @return
	 */
	public String optimize() {
		if (SessionManagement.isValid()) {
			try {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				indexer.optimize();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Recreated all indexes in a new Thread
	 */
	public String recreate() {
		if (SessionManagement.isValid()) {
			try {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				indexer.recreateIndexes();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
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
						Messages.addError(e.getMessage());
					}
				}
			};

			Thread recreateThread = new Thread(task);
			recreateThread.start();
		} else {
			return "login";
		}

		return null;
	}

	public int getDocCount() {
		return indexer.getCount();
	}

	public boolean getLocked() {
		return indexer.isLocked();
	}

	public String getIncludes() {
		PropertiesBean config = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		includes = config.getProperty("index.includes");
		return includes;
	}

	public String getExcludes() {
		PropertiesBean config = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		excludes = config.getProperty("index.excludes");
		return excludes;
	}

	public String save() {
		PropertiesBean config = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		config.setProperty("index.includes", includes);
		config.setProperty("index.excludes", excludes);
		try {
			config.write();
		} catch (IOException e) {
		}

		Messages.addLocalizedInfo("msg.action.savesettings");

		return null;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}
}