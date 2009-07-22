package com.logicaldoc.workflow.editor;

import java.io.File;
import java.util.List;

import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public interface WorkflowTemplateLoader {

	public List<WorkflowPersistenceTemplate> getAvailableWorkflowTemplates();

	public Long saveWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate);

	public void deleteWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate);

	public WorkflowPersistenceTemplate loadWorkflowTemplate(Long id);

	public WorkflowPersistenceTemplate loaWorkflowTemplate(WorkflowPersistenceTemplate workflowTemplate) ;

	public void setTemplatesDirectory(File templatesDirectory) ;
}
