package com.logicaldoc.workflow.transform;

import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public interface WorkflowTransformService {

	public Object fromObjectToWorkflowDefinition(WorkflowTemplate workflowTemplateModel);
	
	public WorkflowTemplate fromWorkflowDefinitionToObject(
			WorkflowPersistenceTemplate workflowTemplateModel);

}
