package com.logicaldoc.workflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.PooledActor;

import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;

public class WorkflowFactory {

	@SuppressWarnings("unchecked")
	public static WorkflowTaskInstance createTaskInstance(
			org.jbpm.taskmgmt.exe.TaskInstance ti) {

		WorkflowTaskInstance taskInstance = new WorkflowTaskInstance();
		
		taskInstance.id = createTaskId(ti.getProcessInstance().getId(), ti
				.getId());
		ti.getVariables();
		taskInstance.name = ti.getName();
		
		Map<String, Object> variables = ti.getVariablesLocally();

		if(ti.isCancelled())
			taskInstance.state = WorkflowTaskInstance.STATE.CANCELED;
		else if(ti.getEnd() != null)
			taskInstance.state = WorkflowTaskInstance.STATE.DONE;
		else if(ti.isOpen())
			taskInstance.state = WorkflowTaskInstance.STATE.ACTIVE;
		
		// mapping
		HashMap<String, Object> mapped_properties = new HashMap<String, Object>();
		for (String key : variables.keySet())
			mapped_properties.put(key.toString(), variables.get(key));

		if (ti.getActorId() != null)
			taskInstance.properties.put(WorkflowConstants.VAR_OWNER, ti
					.getActorId());
		
		if(ti.getPooledActors() != null) {
			Set<PooledActor> pooledActors = ti.getPooledActors();
			List<String> actors = new LinkedList<String>();
			
			for(PooledActor actor : pooledActors)
				actors.add(actor.getActorId());
			
			taskInstance.properties.put(WorkflowConstants.VAR_POOLEDACTORS, actors);
			
		}
			

		if (ti.getStart() != null)
			taskInstance.properties.put(WorkflowConstants.VAR_STARTDATE, ti
					.getStart());

		if (ti.getEnd() != null)
			taskInstance.properties.put(WorkflowConstants.VAR_ENDDATE, ti
					.getEnd());

		if (ti.getDueDate() != null)
			taskInstance.properties.put(WorkflowConstants.VAR_DUEDATE, ti
					.getDueDate());

		if (ti.getDescription() != null)
			taskInstance.properties.put(WorkflowConstants.VAR_DUEDATE, ti
					.getDescription());

		taskInstance.properties.putAll(mapped_properties);

		List<Transition> transitions = ti.getAvailableTransitions();
		
		//you dont have any more transition if you got finished a task for a while ago
		if(transitions != null){
			for(Transition tr : transitions){
				
				//TODO: should not occure
				if(tr.getTo() == null)
					continue;
				
				com.logicaldoc.workflow.model.Transition trans = new com.logicaldoc.workflow.model.Transition();
				trans.name = tr.getName();
				
				trans.to = tr.getTo().getName();
				
				taskInstance.getTransitions().add(trans);
			}
		}
		
		return taskInstance;

	}

	public static WorkflowInstance createWorkflowInstance(
			ProcessInstance processInstance) {

		WorkflowInstance wfi = new WorkflowInstance();
		wfi.processDefinitionId = processInstance.getProcessDefinition()
				.getId();
		wfi.id = Long.toString(processInstance.getId());
		wfi.startDate = processInstance.getStart();
		wfi.endDate = processInstance.getEnd();

		return wfi;
	}
	
	@SuppressWarnings("unchecked")
	public static WorkflowDefinition createWorkflowDefinition(ProcessDefinition definition){
		WorkflowDefinition workflowDefinition = new WorkflowDefinition();
		workflowDefinition.setName(definition.getName());
		workflowDefinition.setDescription(definition
				.getDescription());
		workflowDefinition.setDefinitionId(WorkflowFactory
				.createProcessDefintionId(definition
						.getId()));
		
		workflowDefinition.setProperties(definition.getDefinitions());
		
		return workflowDefinition;
	}

	public static long getJbpmProcessInstanceId(String processId) {

		long jbpmProcessId = -1;

		if (processId.contains("@")) {
			jbpmProcessId = Long.parseLong(processId.substring(0, processId
					.indexOf('@')));
		} else {
			if (processId.matches("[0-9]*")) {
				jbpmProcessId = Long.parseLong(processId);
			} else {
				throw new WorkflowException(
						"The WorkflowId does not provide a valid id");
			}
		}

		return jbpmProcessId;
	}

	public static long getJbpmTaskId(String taskId) {
		int idxOfAt = taskId.indexOf("@");
		return Long.parseLong(taskId.substring(idxOfAt + 1, taskId.length()));
	}

	public static String createTaskId(long processId, long taskid) {
		return new String(processId + "@" + taskid);
	}

	public static String createProcessDefintionId(Long defId){
		return "jbpm-" + defId + "pid";
	}
	
	public static Long getJbpmProcessDefinitionId(String id){
		
		if(id.startsWith("jbpm-") == false)
			throw new IllegalStateException("id does not start with jbpm");
		
		if(id.endsWith("pid") == false)
			throw new IllegalStateException("id does not end with pid");
		
		id = id.replace("jbpm-", "").replace("pid", "");
		
		try {
			return Long.parseLong(id);
		}
		catch(NumberFormatException e){
			throw new IllegalStateException("id does not match to a long value");
		}
	}
	
	public static Long getJbpmProcessDefinitionIdFromTaskInstance(String id){
		return Long.parseLong(id.substring(0, id.indexOf('@')));
	}
	
	
}
