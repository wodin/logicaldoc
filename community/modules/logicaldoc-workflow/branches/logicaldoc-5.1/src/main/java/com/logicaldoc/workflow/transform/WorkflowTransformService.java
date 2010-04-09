package com.logicaldoc.workflow.transform;

import java.io.Serializable;

import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.model.WorkflowTemplate;

public interface WorkflowTransformService {

	public Object fromObjectToWorkflowDefinition(WorkflowTemplate workflowTemplateModel);

	public WorkflowTemplate fromWorkflowDefinitionToObject(WorkflowPersistenceTemplate workflowTemplateModel);

	public WorkflowTemplate retrieveWorkflowModels(Serializable binarayContent);
}
