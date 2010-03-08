package com.logicaldoc.workflow.editor;

import java.util.List;

import com.logicaldoc.workflow.AbstractWorkflowTestCase;
import com.logicaldoc.workflow.WorkflowHistory;

public class HibernateWorkflowHistoryDAOTest extends AbstractWorkflowTestCase {

	// Instance under test
	private WorkflowHistoryDAO dao;

	public HibernateWorkflowHistoryDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateWorkflowHistoryDAO
		dao = (WorkflowHistoryDAO) context.getBean("WorkflowHistoryDAO");
	}

	public void testFindByTemplateIdAndInstanceId() {
		List<WorkflowHistory> histories = dao.findByTemplateIdAndInstanceId(1, "1");
		assertNotNull(histories);
		assertEquals(2, histories.size());

		histories = dao.findByTemplateIdAndInstanceId(1, "2");
		assertNotNull(histories);
		assertEquals(0, histories.size());
	}

}
