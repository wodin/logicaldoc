package com.logicaldoc.core.searchengine;

import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.task.Task;

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
		log.info("Start indexing of all documents");
		errors = 0;
		indexed = 0;
		try {
			// First of all find documents to be indexed
			List<Document> documents = documentDao.findByIndexed(0);
			size = documents.size();
			log.info("Found a total of " + size + " documents to be indexed");

			for (Document document : documents) {
				try {
					documentDao.initialize(document);
					documentManager.reindex(document, document.getLocale());
					indexed++;
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					errors++;
				} finally {
					next();
				}
			}
		} finally {
			log.info("Indexing finished");
			log.info("Documents indexed: " + indexed);
			log.info("Errors: " + errors);
		}
	}
}