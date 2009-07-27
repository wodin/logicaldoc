package com.logicaldoc.workflow.action;

import java.util.LinkedList;
import java.util.List;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowUtil;
import com.logicaldoc.workflow.editor.model.Assignee;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.thoughtworks.xstream.XStream;

public abstract class AbstractAssignmentHandler implements AssignmentHandler {

	private String taskId;
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6145523287668659543L;

	@Override
	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		
		XStream xStream = new XStream();
		
		WorkflowTemplate workflowTemplate = (WorkflowTemplate)xStream.fromXML((String)executionContext.getVariable(WorkflowConstants.VAR_TEMPLATE));
		
		WorkflowTask workflowTask = WorkflowUtil.getWorkflowTaskById(taskId, workflowTemplate.getWorkflowComponents());
		
		List<String> assignees = new LinkedList<String>();
		
		if(workflowTask.getAssignees().size() > 1){
			List<String> pooledActors = new LinkedList<String>();
			
			for(Assignee assignee : workflowTask.getAssignees()){
				pooledActors.add(assignee.getValue());
				assignees.add(assignee.getValue());
			}
			
			assignable.setPooledActors(pooledActors.toArray(new String[]{}));
			
			System.out.println("users " + pooledActors + " has been assigned to task: " + taskId);
			
		}
		else {
			assignable.setActorId(workflowTask.getAssignees().get(0).getValue());
			assignees.add(workflowTask.getAssignees().get(0).getValue());
			System.out.println("user " + workflowTask.getAssignees().get(0).getValue() + " has been assigned to task: " + taskId);
			
		}
		
		executeImpl(assignees, executionContext);
	}
	
	public abstract void executeImpl(List<String> assignees, ExecutionContext executionContext);

}
