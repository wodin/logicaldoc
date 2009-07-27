package com.logicaldoc.workflow.editor;

import java.io.File;
import java.util.List;

import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public interface WorkflowTemplateLoader {

	public static enum WORKFLOW_STAGE{DEPLOYED, SAVED};
	
	public List<WorkflowPersistenceTemplate> getAvailableWorkflowTemplates();

	public Long saveWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate, WORKFLOW_STAGE workflow_stage);

	public void deleteWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate);

	public WorkflowPersistenceTemplate loadWorkflowTemplate(Long id, WORKFLOW_STAGE workflow_stage);
	
	public void deployWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate);

	public WorkflowPersistenceTemplate loadWorkflowTemplate(WorkflowPersistenceTemplate workflowTemplate, WORKFLOW_STAGE workflow_stage) ;

	public void setTemplatesDirectory(File templatesDirectory) ;

	public List<WorkflowPersistenceTemplate> loadAllWorkflowDefinitions();
	
	public WorkflowPersistenceTemplate loadWorkflowTemplate(String name, WORKFLOW_STAGE workflow_stage);
}
