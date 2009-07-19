package com.logicaldoc.workflow.transform;

import java.util.List;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public interface WorkflowTransformService {

	public Object fromObjectToWorkflowDefinition(WorkflowPersistenceTemplate workflowTemplateModel);
	
	public List<BaseWorkflowModel> fromWorkflowDefinitionToObject(WorkflowPersistenceTemplate workflowTemplateModel);

}
