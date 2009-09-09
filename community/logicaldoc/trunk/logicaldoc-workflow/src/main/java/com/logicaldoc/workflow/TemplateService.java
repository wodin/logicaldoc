package com.logicaldoc.workflow;

import java.util.Map;

import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;

public interface TemplateService {
	
	public String transformToString(String text,
			Map<String, Object> modelProperties);
	
	public String transformWorkflowTask(WorkflowTask workflowTask, WorkflowInstance workflowInstance, WorkflowTaskInstance workflowTaskInstance, String msg);
	
	public String transformWorkflowTask(WorkflowTask workflowTask, WorkflowTaskInstance workflowTaskInstance);
	
	public String transformWorkflowInstance(WorkflowInstance workflowInstance, WorkflowTemplate workflowDefinition, String msg);
}
