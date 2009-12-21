package com.logicaldoc.workflow;

import java.util.List;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

public class WorkflowUtil {

	public static WorkflowTask getWorkflowTaskById(String taskId, List<BaseWorkflowModel> list){
		
		for(BaseWorkflowModel model : list){
			
			if((model instanceof WorkflowTask) == false)
				continue;
			
			if(model.getId().equals(taskId))
				return (WorkflowTask)model;
		}
		
		return null;
	}
	
}
