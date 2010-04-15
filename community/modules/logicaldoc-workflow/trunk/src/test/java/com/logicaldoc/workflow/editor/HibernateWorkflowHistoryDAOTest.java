package com.logicaldoc.workflow.editor;

import java.util.Collection;
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

	public void testFindTemplateIds() {
		Collection<Long> ids = dao.findTemplateIds();
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertTrue(ids.contains(new Long(1)));
		assertFalse(ids.contains(new Long(2)));
	}

	public void testFindInstanceIds() {
		Collection<String> ids = dao.findInstanceIds();
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertTrue(ids.contains("1"));
		assertFalse(ids.contains("2"));
	}

	@SuppressWarnings("unchecked")
	public void testCleanOldHistories() {
		WorkflowHistory history = dao.findById(1);
		assertNotNull(history);

		Collection histories = dao.findAll();
		assertNotNull(histories);
		assertEquals(2, histories.size());

		dao.cleanOldHistories(5);

		history = dao.findById(1);
		assertNull(history);
		histories = dao.findAll();
		assertEquals(0, histories.size());
	}
}
