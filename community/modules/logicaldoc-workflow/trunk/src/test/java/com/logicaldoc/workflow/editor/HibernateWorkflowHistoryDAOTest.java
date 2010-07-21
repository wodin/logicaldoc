package com.logicaldoc.workflow.editor;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.logicaldoc.workflow.AbstractWorkflowTCase;
import com.logicaldoc.workflow.WorkflowHistory;

public class HibernateWorkflowHistoryDAOTest extends AbstractWorkflowTCase {

	// Instance under test
	private WorkflowHistoryDAO dao;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateWorkflowHistoryDAO
		dao = (WorkflowHistoryDAO) context.getBean("WorkflowHistoryDAO");
	}

	@Test
	public void testFindByTemplateIdAndInstanceId() {
		List<WorkflowHistory> histories = dao.findByTemplateIdAndInstanceId(1, "1");
		Assert.assertNotNull(histories);
		Assert.assertEquals(2, histories.size());

		histories = dao.findByTemplateIdAndInstanceId(1, "2");
		Assert.assertNotNull(histories);
		Assert.assertEquals(0, histories.size());
	}

	@Test
	public void testFindTemplateIds() {
		Collection<Long> ids = dao.findTemplateIds();
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertTrue(ids.contains(new Long(1)));
		Assert.assertFalse(ids.contains(new Long(2)));
	}

	@Test
	public void testFindInstanceIds() {
		Collection<String> ids = dao.findInstanceIds();
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertTrue(ids.contains("1"));
		Assert.assertFalse(ids.contains("2"));
	}

	@Test
	public void testCleanOldHistories() {
		WorkflowHistory history = dao.findById(1);
		Assert.assertNotNull(history);

		Collection<WorkflowHistory> histories = dao.findAll();
		Assert.assertNotNull(histories);
		Assert.assertEquals(2, histories.size());

		dao.cleanOldHistories(5);

		history = dao.findById(1);
		Assert.assertNull(history);
		histories = dao.findAll();
		Assert.assertEquals(0, histories.size());
	}
}
