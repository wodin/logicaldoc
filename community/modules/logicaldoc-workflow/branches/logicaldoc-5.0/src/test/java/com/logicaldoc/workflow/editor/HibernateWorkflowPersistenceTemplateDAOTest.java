package com.logicaldoc.workflow.editor;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.logicaldoc.workflow.AbstractWorkflowTestCase;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE;

public class HibernateWorkflowPersistenceTemplateDAOTest extends AbstractWorkflowTestCase {

	// Instance under test
	private WorkflowPersistenceTemplateDAO dao;

	public HibernateWorkflowPersistenceTemplateDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateWorkflowPersistenceTemplateDAO
		dao = (WorkflowPersistenceTemplateDAO) context.getBean("WorkflowPersistenceTemplateDAO");

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		File fileWorkflow = null;
		try {
			fileWorkflow = new File("/1.jbpm");
			if (fileWorkflow.exists()) {
				fileWorkflow.renameTo(new File(tempDir, "1.jbpm"));
			}
		} catch (Exception e) {
		}

		try {
			fileWorkflow = new File("/2.jbpm");
			if (fileWorkflow.exists()) {
				fileWorkflow.renameTo(new File(tempDir, "2.jbpm"));
			}
		} catch (Exception e) {
		}

		try {
			fileWorkflow = new File("/1-deployed.jbpm");
			if (fileWorkflow.exists()) {
				fileWorkflow.renameTo(new File(tempDir, "1-deployed.jbpm"));
			}
			if (fileWorkflow.exists())
				FileUtils.forceDelete(fileWorkflow);
		} catch (Exception e) {
		}

		try {
			fileWorkflow = new File("/3.jbpm");
			if (fileWorkflow.exists()) {
				fileWorkflow.renameTo(new File(tempDir, "3.jbpm"));
			}
		} catch (Exception e) {
		}
	}

	public void testSave() {
		WorkflowPersistenceTemplate workflow1 = dao.findById(1);
		workflow1.setDescription("pippo");
		workflow1.setDeployed(WorkflowPersistenceTemplate.DEPLOYED);
		try {
			dao.save(workflow1, WORKFLOW_STAGE.SAVED);
		} catch (Exception e) {
		}
		assertNotNull(workflow1);

		WorkflowPersistenceTemplate workflow2 = dao.findById(2);
		workflow2.setDescription("paperino");
		workflow2.setStartState("pluto");
		try {
			dao.save(workflow2, WORKFLOW_STAGE.SAVED);
		} catch (Exception e) {
		}
		assertNotNull(workflow2);
	}

	public void testDelete() {
		assertTrue(dao.delete(1));
		WorkflowPersistenceTemplate workflow = dao.findById(1);
		assertNull(workflow);
	}

	public void testLoad() {
		WorkflowPersistenceTemplate workflow = dao.load(1L, WORKFLOW_STAGE.SAVED);
		assertNotNull(workflow);
		assertEquals("workflow1", workflow.getName());
	}

	public void testDeploy() {
		WorkflowPersistenceTemplate workflow = dao.findById(1);
		assertNotNull(workflow);
		try {
			dao.deploy(workflow);
		} catch (Exception e) {
		}
		assertEquals(WorkflowPersistenceTemplate.DEPLOYED, workflow.getDeployed());
	}

	public void testFindAllDeployed() {
		Collection<WorkflowPersistenceTemplate> workflows = dao.findAllDeployed();
		assertNotNull(workflows);
		assertEquals(1, workflows.size());
		assertTrue(workflows.contains(dao.findById(3)));

		WorkflowPersistenceTemplate workflow3 = dao.findById(3);
		workflow3.setDeployed(WorkflowPersistenceTemplate.NOT_DEPLOYED);
		try {
			dao.save(workflow3, WORKFLOW_STAGE.SAVED);
		} catch (Exception e) {
		}

		workflows = dao.findAllDeployed();
		assertNotNull(workflows);
		assertEquals(0, workflows.size());
		assertTrue(!workflows.contains(dao.findById(3)));
	}
}
