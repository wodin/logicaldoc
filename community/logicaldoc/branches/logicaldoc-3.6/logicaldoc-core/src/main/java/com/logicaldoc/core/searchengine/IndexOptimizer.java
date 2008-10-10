package com.logicaldoc.core.searchengine;

import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.task.Task;

/**
 * This task optimizes all indexes
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5.0
 */
public class IndexOptimizer extends Task {
	public static final String NAME = "IndexOptimizer";

	private Indexer indexer;

	public IndexOptimizer() {
		super(NAME);
		log = LogFactory.getLog(IndexOptimizer.class);
	}

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start index optimization");
		indexer.unlock();
		indexer.optimize();
		log.info("End of index optimization");
	}

	@Override
	public boolean isIndeterminate() {
		return true;
	}
}