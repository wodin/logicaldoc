package com.logicaldoc.workflow.editor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of the WorkflowPersistenceTemplateDAO.
 * 
 * @author Sebastian Wenzky
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public class HibernateWorkflowPersistenceTemplateDAO extends HibernatePersistentObjectDAO<WorkflowPersistenceTemplate>
		implements WorkflowPersistenceTemplateDAO {

	protected HibernateWorkflowPersistenceTemplateDAO() {
		super(WorkflowPersistenceTemplate.class);
		super.log = LogFactory.getLog(WorkflowPersistenceTemplateDAO.class);
	}

	private File resolveSystemPath(WorkflowPersistenceTemplate template) {
		return resolveSystemPath(template, null);
	}

	private File resolveSystemPath(WorkflowPersistenceTemplate template, String sfx) {
		File file = new File(getTemplatesDirectory(), template.getId() + ((sfx != null) ? "-" + sfx : "") + ".jbpm");
		file.getParentFile().mkdirs();

		log.info("workflowFile=" + file.getPath());
		return file;
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#save(com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate,
	 *      com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE)
	 */
	public void save(WorkflowPersistenceTemplate persistenceTemplate, WORKFLOW_STAGE stage) {
		Serializable xmldata = persistenceTemplate.getXmldata();
		store(persistenceTemplate);
		persistenceTemplate = (WorkflowPersistenceTemplate) getHibernateTemplate().merge(persistenceTemplate);
		persistenceTemplate.setXmldata(xmldata);

		File workflowFile = null;

		if (stage.compareTo(WORKFLOW_STAGE.SAVED) == 0)
			workflowFile = resolveSystemPath(persistenceTemplate);
		else
			workflowFile = resolveSystemPath(persistenceTemplate, "deployed");

		saveWorkflowFile(workflowFile, persistenceTemplate);
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#delete(com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate)
	 */
	public void delete(WorkflowPersistenceTemplate persistenceTemplate) {
		persistenceTemplate.setDeleted(1);
		persistenceTemplate.setName(persistenceTemplate.getName() + "." + persistenceTemplate.getId());
		getHibernateTemplate().saveOrUpdate(persistenceTemplate);

		File workflowFile = resolveSystemPath(persistenceTemplate);

		File deployedWorkflowFile = resolveSystemPath(persistenceTemplate, "deployed");
		try {

			if (workflowFile.exists())
				workflowFile.delete();
			else
				log.warn("WorkflowTemplate " + persistenceTemplate.getName() + " does not physically exist!");

			if (deployedWorkflowFile.exists())
				deployedWorkflowFile.delete();
			else
				log.warn("Deployed WorkflowTemplate " + persistenceTemplate.getName() + " does not physically exist!");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#load(java.lang.Long,
	 *      com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE)
	 */
	public WorkflowPersistenceTemplate load(Long id, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE stage) {
		WorkflowPersistenceTemplate workflowPersistenceTemplate = (WorkflowPersistenceTemplate) getHibernateTemplate()
				.load(WorkflowPersistenceTemplate.class, id);
		try {
			FileInputStream is = null;
			if (stage.compareTo(WORKFLOW_STAGE.SAVED) == 0)
				is = new FileInputStream(resolveSystemPath(workflowPersistenceTemplate));
			else
				is = new FileInputStream(resolveSystemPath(workflowPersistenceTemplate, "deployed"));

			int read = 0;
			byte[] buf = new byte[4192];

			ByteArrayOutputStream bos = new ByteArrayOutputStream(buf.length);

			while ((read = is.read(buf)) != -1)
				bos.write(buf, 0, (int) read);

			is.close();
			bos.close();

			workflowPersistenceTemplate.setXmldata(bos.toString("UTF-8"));

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return workflowPersistenceTemplate;
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#load(com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate,
	 *      com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE)
	 */
	public WorkflowPersistenceTemplate load(WorkflowPersistenceTemplate workflowTemplate, WORKFLOW_STAGE workflow_stage) {
		return load(workflowTemplate.getId(), workflow_stage);
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#deploy(com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate)
	 */
	public void deploy(WorkflowPersistenceTemplate persistenceTemplate) {

		File workflowFile = resolveSystemPath(persistenceTemplate, "deployed");

		persistenceTemplate.setDeployed(WorkflowPersistenceTemplate.DEPLOYED);
		Serializable serializableXmlData = persistenceTemplate.getXmldata();

		persistenceTemplate = (WorkflowPersistenceTemplate) getHibernateTemplate().merge(persistenceTemplate);
		store(persistenceTemplate);

		persistenceTemplate.setXmldata(serializableXmlData);
		saveWorkflowFile(workflowFile, persistenceTemplate);
	}

	private void saveWorkflowFile(File workflowFile, WorkflowPersistenceTemplate persistenceTemplate) {
		try {
			if (workflowFile.exists() == false)
				workflowFile.createNewFile();

			workflowFile.getParentFile().mkdirs();
			DataOutputStream os = new DataOutputStream(new FileOutputStream(workflowFile));
			os.write(((String) persistenceTemplate.getXmldata()).getBytes("UTF-8"));
			os.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#findAllDeployed()
	 */
	public List<WorkflowPersistenceTemplate> findAllDeployed() {
		return findByWhere("_entity.deployed = 1", null);
	}

	/**
	 * @see com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO#load(java.lang.String,
	 *      com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE)
	 */
	public WorkflowPersistenceTemplate load(String name, WORKFLOW_STAGE workflow_stage) {
		WorkflowPersistenceTemplate pt = findByWhere("_entity.name ='" + SqlUtil.doubleQuotes(name) + "'", null).get(0);
		return this.load(pt.getId(), workflow_stage);

	}

	/**
	 * Computes the directory in which all jbpm files must be maintained, that
	 * is the plugin dir
	 */
	private File getTemplatesDirectory() {
		File file = new File("");
		try {
			LogicalDOCPlugin workflowPlugin = (LogicalDOCPlugin) PluginRegistry.getInstance().getManager().getPlugin(
					"logicaldoc-workflow");
			file = workflowPlugin.resolveDataPath("templates");
			if (!file.exists())
				file.mkdir();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
		return file;
	}
}
