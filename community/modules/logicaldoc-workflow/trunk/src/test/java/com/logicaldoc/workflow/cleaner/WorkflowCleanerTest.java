package com.logicaldoc.workflow.cleaner;

import java.sql.Connection;
import java.sql.ResultSet;

import org.junit.Before;

import com.logicaldoc.workflow.AbstractWorkflowTestCase;
import com.logicaldoc.workflow.WorkflowCleaner;

public class WorkflowCleanerTest extends AbstractWorkflowTestCase {

	// Instance under test
	private WorkflowCleaner cleaner;

	public WorkflowCleanerTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		cleaner = new WorkflowCleaner(ds.getConnection());
	}

	public void testDelete() throws Exception {
		Connection con = ds.getConnection();
		con.commit();
		ResultSet rs = con.createStatement().executeQuery("select count(*) from ld_workflowtemplate");
		rs.next();
		assertEquals(4, rs.getInt(1));

		cleaner.clean();
		con.commit();

		// Check database cleanup
		rs = con.createStatement().executeQuery("select count(*) from ld_workflowtemplate");
		rs.next();
		assertEquals(3, rs.getInt(1));
	}
}
