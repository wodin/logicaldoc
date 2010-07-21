package com.logicaldoc.workflow.editor;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.logicaldoc.workflow.AbstractWorkflowTCase;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE;

public class HibernateWorkflowPersistenceTemplateDAOTest extends AbstractWorkflowTCase {

	// Instance under test
	private WorkflowPersistenceTemplateDAO dao;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateWorkflowPersistenceTemplateDAO
		dao = (WorkflowPersistenceTemplateDAO) context.getBean("WorkflowPersistenceTemplateDAO");

	}

	@Override
	public void tearDown() throws Exception {
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

	@Test
	public void testSave() {
		WorkflowPersistenceTemplate workflow1 = dao.findById(1);
		workflow1.setDescription("pippo");
		workflow1.setDeployed(WorkflowPersistenceTemplate.DEPLOYED);
		try {
			dao.save(workflow1, WORKFLOW_STAGE.SAVED);
		} catch (Exception e) {
		}
		Assert.assertNotNull(workflow1);

		WorkflowPersistenceTemplate workflow2 = dao.findById(2);
		workflow2.setDescription("paperino");
		workflow2.setStartState("pluto");
		try {
			dao.save(workflow2, WORKFLOW_STAGE.SAVED);
		} catch (Exception e) {
		}
		Assert.assertNotNull(workflow2);
	}

	@Test
	public void testDelete() {
		Assert.assertTrue(dao.delete(1));
		WorkflowPersistenceTemplate workflow = dao.findById(1);
		Assert.assertNull(workflow);
	}

	@Test
	public void testLoad() {
		WorkflowPersistenceTemplate workflow = dao.load(1L, WORKFLOW_STAGE.SAVED);
		Assert.assertNotNull(workflow);
		Assert.assertEquals("workflow1", workflow.getName());
	}

	@Test
	public void testDeploy() {
		WorkflowPersistenceTemplate workflow = dao.findById(1);
		Assert.assertNotNull(workflow);
		try {
			dao.deploy(workflow);
		} catch (Exception e) {
		}
		Assert.assertEquals(WorkflowPersistenceTemplate.DEPLOYED, workflow.getDeployed());
	}

	@Test
	public void testFindAllDeployed() {
		Collection<WorkflowPersistenceTemplate> workflows = dao.findAllDeployed();
		Assert.assertNotNull(workflows);
		Assert.assertEquals(1, workflows.size());
		Assert.assertTrue(workflows.contains(dao.findById(3)));

		WorkflowPersistenceTemplate workflow3 = dao.findById(3);
		workflow3.setDeployed(WorkflowPersistenceTemplate.NOT_DEPLOYED);
		try {
			dao.save(workflow3, WORKFLOW_STAGE.SAVED);
		} catch (Exception e) {
		}

		workflows = dao.findAllDeployed();
		Assert.assertNotNull(workflows);
		Assert.assertEquals(0, workflows.size());
		Assert.assertTrue(!workflows.contains(dao.findById(3)));
	}

	@Test
	public void testFindByName() {
		WorkflowPersistenceTemplate template = dao.findByName("workflow1");
		Assert.assertNotNull(template);
		Assert.assertEquals(1, template.getId());
		Assert.assertEquals("workflow1", template.getName());
		Assert.assertEquals("pippo", template.getStartState());

		template = dao.findByName("xxx");
		Assert.assertNull(template);
	}
}