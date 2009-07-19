package com.logicaldoc.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;

import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;

public class WorkflowFactory {

	@SuppressWarnings("unchecked")
	public static WorkflowTaskInstance createTaskInstance(
			org.jbpm.taskmgmt.exe.TaskInstance ti) {

		WorkflowTaskInstance taskInstance = new WorkflowTaskInstance();

		taskInstance.id = createTaskId(ti.getContextInstance().getId(), ti
				.getId());

		taskInstance.name = ti.getName();
		
		Map<String, Object> variables = ti.getVariablesLocally();

		// mapping
		HashMap<String, Object> mapped_properties = new HashMap<String, Object>();
		for (String key : variables.keySet())
			mapped_properties.put(key.toString(), variables.get(key));

		if (ti.getActorId() != null)
			taskInstance.properties.put(WorkflowConstants.VAR_OWNER, ti
					.getActorId());

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

		taskInstance.properties = mapped_properties;

		List<Transition> transitions = ti.getAvailableTransitions();
		
		
		for(Transition tr : transitions){
			com.logicaldoc.workflow.model.Transition trans = new com.logicaldoc.workflow.model.Transition();
			trans.name = tr.getName();
			trans.to = tr.getTo().getName();
			
			taskInstance.getTransitions().add(trans);
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
	
}
