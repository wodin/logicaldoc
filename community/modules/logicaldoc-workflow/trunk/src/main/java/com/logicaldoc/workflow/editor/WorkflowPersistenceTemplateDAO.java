package com.logicaldoc.workflow.editor;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;

/**
 * DAO for Persistent WorkFlow Templates handling.
 * 
 * @author Sebastian Wenzky
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public interface WorkflowPersistenceTemplateDAO extends PersistentObjectDAO<WorkflowPersistenceTemplate> {

	public static enum WORKFLOW_STAGE {
		DEPLOYED, SAVED
	};

	/**
	 * This method saves the persistence workflow template with the given
	 * workflow stage.
	 * 
	 * @param persistenceTemplate The persistence workflow template to be saved
	 * @param workflow_stage The workflow stage
	 */
	public void save(WorkflowPersistenceTemplate persistenceTemplate, WORKFLOW_STAGE workflow_stage);

	/**
	 * This method deletes the given persistence workflow template.
	 * 
	 * @param persistenceTemplate The persistence workflow template to be
	 *        deleted
	 */
	public void delete(WorkflowPersistenceTemplate persistenceTemplate);

	/**
	 * This method loads the persistence workflow template with the given id and
	 * the given workflow stage.
	 * 
	 * @param id The persistence workflow template id
	 * @param workflow_stage The workflow stage
	 * @return The persistence workflow template
	 */
	public WorkflowPersistenceTemplate load(Long id, WORKFLOW_STAGE workflow_stage);

	/**
	 * This method loads the given persistence workflow template with the given
	 * workflow stage.
	 * 
	 * @param id The persistence workflow template
	 * @param workflow_stage The workflow stage
	 * @return The persistence workflow template
	 */
	public WorkflowPersistenceTemplate load(WorkflowPersistenceTemplate workflowTemplate, WORKFLOW_STAGE workflow_stage);

	/**
	 * This method loads the persistence workflow template with the given name
	 * and with the given workflow stage.
	 * 
	 * @param name The persistence workflow template name
	 * @param workflow_stage The workflow stage
	 * @return The persistence workflow template
	 */
	public WorkflowPersistenceTemplate load(String name, WORKFLOW_STAGE workflow_stage);

	/**
	 * This method stores the given persistence workflow template.
	 * 
	 * @param persistenceTemplate The persistence workflow template
	 */
	public void deploy(WorkflowPersistenceTemplate persistenceTemplate);

	/**
	 * This method loads all the workflow already deployed..
	 * 
	 * @return A list of persistence workflow template
	 */
	public List<WorkflowPersistenceTemplate> findAllDeployed();
}
