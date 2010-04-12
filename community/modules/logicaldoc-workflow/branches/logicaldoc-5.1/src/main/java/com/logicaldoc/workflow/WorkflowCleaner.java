package com.logicaldoc.workflow;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.impex.cleaner.Cleaner;

public class WorkflowCleaner extends Cleaner {

	protected static Log log = LogFactory.getLog(WorkflowCleaner.class);

	public WorkflowCleaner() {
		super();
		setDbScript("/sql/clean-workflow.sql");
	}

	public WorkflowCleaner(Connection con) {
		super(con);
		setDbScript("/sql/clean-workflow.sql");
	}

	@Override
	protected void afterDbUpdate() throws Exception {
		// DO NOTHING
	}

	@Override
	protected void beforeDbUpdate() throws Exception {
		// DO NOTHING
	}
}