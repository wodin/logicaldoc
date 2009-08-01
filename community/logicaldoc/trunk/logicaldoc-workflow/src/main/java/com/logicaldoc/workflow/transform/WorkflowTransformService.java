package com.logicaldoc.workflow.transform;

import java.io.Serializable;

import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public interface WorkflowTransformService {

	public Object fromObjectToWorkflowDefinition(WorkflowTemplate workflowTemplateModel);
	
	public WorkflowTemplate fromWorkflowDefinitionToObject(
			WorkflowPersistenceTemplate workflowTemplateModel);

	public WorkflowTemplate retrieveWorkflowModels(Serializable binarayContent);
}
