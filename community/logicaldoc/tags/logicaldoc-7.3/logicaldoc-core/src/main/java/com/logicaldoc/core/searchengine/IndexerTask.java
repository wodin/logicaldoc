package com.logicaldoc.core.searchengine;

import java.util.List;
import java.util.Locale;

import org.slf4j.LoggerFactory;

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

	private SearchEngine indexer;

	private long indexed = 0;

	private long errors = 0;

	public IndexerTask() {
		super(NAME);
		log = LoggerFactory.getLogger(IndexerTask.class);
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
	public boolean isConcurrent() {
		return true;
	}

	@Override
	protected void runTask() throws Exception {
		if (indexer.isLocked()) {
			log.warn("Index locked, skipping indexing");
			return;
		}

		if (!lockManager.get(getName(), transactionId)) {
			log.warn("Unable to acquire lock " + getName() + ", skipping indexing");
			return;
		}

		log.info("Start indexing of all documents");
		errors = 0;
		indexed = 0;
		try {
			/*
			 * Cleanup all references to expired transactions
			 */
			documentDao.cleanExpiredTransactions();

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			Integer max = config.getProperty("index.batch") != null ? new Integer(config.getProperty("index.batch"))
					: null;

			if (max != null && max.intValue() < size && max.intValue() > 0)
				size = max.intValue();

			if (max != null && max.intValue() < 1)
				max = null;

			// First of all find documents to be indexed and not already
			// involved into a transaction
			List<Long> ids = documentDao.findIdsByWhere("_entity.indexed = " + AbstractDocument.INDEX_TO_INDEX
					+ " and _entity.transactionId is null and not _entity.status=" + AbstractDocument.DOC_ARCHIVED,
					"order by _entity.date asc", max);
			size = ids.size();
			log.info("Found a total of " + size + " documents to be indexed");

			// Must take into account start and end of the transaction
			size += 2;

			if (!ids.isEmpty()) {
				// Mark all these documents as belonging to the current
				// transaction. This may require time
				String idsStr = ids.toString().replace('[', '(').replace(']', ')');
				documentDao.bulkUpdate(" set ld_transactionid = ?1 where ld_transactionid is null and ld_id in "
						+ idsStr, new Object[] { transactionId });
			}
			log.info("Documents marked for indexing in transaction " + transactionId);

			// First step done
			next();

			// Now we can release the lock
			lockManager.release(getName(), transactionId);

			for (Long id : ids) {
				try {
					log.debug("Indexing document " + id);
					documentManager.reindex(id, null);
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

			// To be safer always release the lock
			lockManager.release(getName(), transactionId);

			// Remove the transaction reference
			documentDao.bulkUpdate("set ld_transactionid = null where ld_transactionId = ?1",
					new Object[] { transactionId });

			// Last step done
			next();
			log.info("Documents released from transaction " + transactionId);
		}
	}

	public void setIndexer(SearchEngine indexer) {
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