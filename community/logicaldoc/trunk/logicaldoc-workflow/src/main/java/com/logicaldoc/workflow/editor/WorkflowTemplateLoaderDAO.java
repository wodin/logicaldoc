package com.logicaldoc.workflow.editor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.workflow.editor.WorkflowTemplateLoader.WORKFLOW_STAGE;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public class WorkflowTemplateLoaderDAO extends HibernateDaoSupport implements
		WorkflowTemplateLoader {

	protected static Log log = LogFactory.getLog(WorkflowTemplateLoader.class);

	private File templatesDirectory;

	private File resolveSystemPath(WorkflowPersistenceTemplate template) {
		return resolveSystemPath(template, null);
	}

	private File resolveSystemPath(WorkflowPersistenceTemplate template,
			String sfx) {
		File file = new File(templatesDirectory, template.getId()
				+ ((sfx != null) ? "-" + sfx : "") + ".jbpm");

		log.info("workflowFile=" + file.getPath());

		return file;
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowPersistenceTemplate> getAvailableWorkflowTemplates() {

		List<WorkflowPersistenceTemplate> workflowTemplateList = getHibernateTemplate()
				.find(" from WorkflowPersistenceTemplate");

		return workflowTemplateList;
	}

	public Long saveWorkflowTemplate(
			WorkflowPersistenceTemplate persistenceTemplate,
			WORKFLOW_STAGE stage) {

		Serializable xmldata = persistenceTemplate.getXmldata();
		persistenceTemplate = (WorkflowPersistenceTemplate) getHibernateTemplate()
				.merge(persistenceTemplate);
		persistenceTemplate.setXmldata(xmldata);

		Long id = null;
		Object ret = getHibernateTemplate().get(
				WorkflowPersistenceTemplate.class, persistenceTemplate.getId());
		if (ret == null)
			id = (Long) getHibernateTemplate().save(persistenceTemplate);
		else {
			id = persistenceTemplate.getId();
			getHibernateTemplate().update(persistenceTemplate);
		}

		File workflowFile = null;

		if (stage.compareTo(WORKFLOW_STAGE.SAVED) == 0)
			workflowFile = resolveSystemPath(persistenceTemplate);
		else
			workflowFile = resolveSystemPath(persistenceTemplate, "deployed");

		saveWorkflowFile(workflowFile, persistenceTemplate);

		return (Long) id;
	}

	public void deleteWorkflowTemplate(
			WorkflowPersistenceTemplate persistenceTemplate) {

		getHibernateTemplate().delete(persistenceTemplate);

		File workflowFile = resolveSystemPath(persistenceTemplate);

		File deployedWorkflowFile = resolveSystemPath(persistenceTemplate,
				"deployed");
		try {

			if (workflowFile.exists())
				workflowFile.delete();
			else
				log.warn("WorkflowTemplate " + persistenceTemplate.getName()
						+ " does not physically exist!");

			if (deployedWorkflowFile.exists())
				deployedWorkflowFile.delete();
			else
				log.warn("Deployed WorkflowTemplate "
						+ persistenceTemplate.getName()
						+ " does not physically exist!");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public WorkflowPersistenceTemplate loadWorkflowTemplate(Long id,
			WorkflowTemplateLoader.WORKFLOW_STAGE stage) {
		WorkflowPersistenceTemplate workflowPersistenceTemplate = (WorkflowPersistenceTemplate) getHibernateTemplate()
				.load(WorkflowPersistenceTemplate.class, id);

		try {

			FileInputStream is = null;
			if (stage.compareTo(WORKFLOW_STAGE.SAVED) == 0)
				is = new FileInputStream(
						resolveSystemPath(workflowPersistenceTemplate));
			else
				is = new FileInputStream(resolveSystemPath(
						workflowPersistenceTemplate, "deployed"));

			int read = 0;
			byte[] buf = new byte[4192];

			ByteArrayOutputStream bos = new ByteArrayOutputStream(buf.length);

			while ((read = is.read(buf)) != -1)
				bos.write(buf, 0, (int) read);

			is.close();
			bos.close();

			workflowPersistenceTemplate.setXmldata(bos.toString("UTF-8"));

			return workflowPersistenceTemplate;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public WorkflowPersistenceTemplate loadWorkflowTemplate(
			WorkflowPersistenceTemplate workflowTemplate,
			WORKFLOW_STAGE workflow_stage) {
		return loadWorkflowTemplate(workflowTemplate.getId(), workflow_stage);
	}

	public void setTemplatesDirectory(File templatesDirectory) {
		this.templatesDirectory = templatesDirectory;
	}

	@Override
	public void deployWorkflowTemplate(
			WorkflowPersistenceTemplate persistenceTemplate) {

		File workflowFile = resolveSystemPath(persistenceTemplate, "deployed");

		persistenceTemplate.setDeployed(true);
		Serializable serializableXmlData = persistenceTemplate.getXmldata();

		persistenceTemplate = (WorkflowPersistenceTemplate) getHibernateTemplate()
				.merge(persistenceTemplate);

		getHibernateTemplate().update(persistenceTemplate);

		persistenceTemplate.setXmldata(serializableXmlData);
		saveWorkflowFile(workflowFile, persistenceTemplate);

	}

	private void saveWorkflowFile(File workflowFile,
			WorkflowPersistenceTemplate persistenceTemplate) {

		try {
			if (workflowFile.exists() == false)
				workflowFile.createNewFile();

			DataOutputStream os = new DataOutputStream(new FileOutputStream(
					workflowFile));
			os.write(((String) persistenceTemplate.getXmldata())
					.getBytes("UTF-8"));
			os.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkflowPersistenceTemplate> loadAllWorkflowDefinitions() {

		return getHibernateTemplate().find(
				"from WorkflowPersistenceTemplate Where deployed == 1");

	}

	public WorkflowPersistenceTemplate loadWorkflowTemplate(String name,
			WORKFLOW_STAGE workflow_stage) {
		WorkflowPersistenceTemplate pt = (WorkflowPersistenceTemplate) getHibernateTemplate()
				.find("from WorkflowPersistenceTemplate where name = ?", name)
				.get(0);
		return this.loadWorkflowTemplate(pt.getId(), workflow_stage);

	}
}
