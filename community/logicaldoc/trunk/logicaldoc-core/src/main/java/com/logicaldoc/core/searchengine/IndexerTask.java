package com.logicaldoc.core.searchengine;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * This task enlists all non-indexed documents and performs the indexing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class IndexerTask extends Task {
	public static final String NAME = "IndexerTask";

	private DocumentManager documentManager;

	private DocumentDAO documentDao;

	private Indexer indexer;

	private long indexed = 0;

	private long errors = 0;

	public IndexerTask() {
		super(NAME);
		log = LogFactory.getLog(IndexerTask.class);
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}

	@Override
	protected void runTask() throws Exception {
		if (indexer.isLocked()) {
			log.warn("Index locked, skipping indexing");
			return;
		}

		log.info("Start indexing of all documents");
		errors = 0;
		indexed = 0;
		try {
			// First of all find documents to be indexed
			size = documentDao.countByIndexed(0);

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			Integer max = config.getProperty("index.batch") != null ? new Integer(config.getProperty("index.batch"))
					: null;

			if (max != null && max.intValue() < size && max.intValue() > 0)
				size = max.intValue();

			if (max != null && max.intValue() < 1)
				max = null;

			log.info("Found a total of " + size + " documents to be indexed");

			List<Long> ids = documentDao.findIdsByWhere("_entity.indexed = " + AbstractDocument.INDEX_TO_INDEX, null,
					max);
			for (Long id : ids) {
				try {
					log.debug("Indexing document " + id);
					documentManager.reindex(id);
					log.debug("Indexed document " + id);
					indexed++;
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					errors++;
				} finally {
					next();
				}
				if (interruptRequested)
					return;
			}
		} finally {
			log.info("Indexing finished");
			log.info("Indexed documents: " + indexed);
			log.info("Errors: " + errors);

			indexer.unlock();
		}
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	@Override
	protected String prepareReport(Locale locale) {
		StringBuffer sb = new StringBuffer();
		sb.append(I18N.message("indexeddocs", locale) + ": ");
		sb.append(indexed);
		sb.append("\n");
		sb.append(I18N.message("errors", locale) + ": ");
		sb.append(errors);
		return sb.toString();
	}
}