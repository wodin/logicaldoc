package com.logicaldoc.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.logicaldoc.workflow.WorkflowConstants;

public class TaskSetupHandler implements ActionHandler{

	String taskId;
	
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		executionContext.getTaskInstance().setVariableLocally(WorkflowConstants.VAR_TASKID, taskId);
	}

}
