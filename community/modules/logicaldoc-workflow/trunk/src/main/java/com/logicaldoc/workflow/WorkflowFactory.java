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
	public static WorkflowTaskInstance createTaskInstance(org.jbpm.taskmgmt.exe.TaskInstance ti) {

		WorkflowTaskInstance taskInstance = new WorkflowTaskInstance();

		taskInstance.setId(createTaskId(ti.getProcessInstance().getId(), ti.getId()));
		ti.getVariables();
		taskInstance.setName(ti.getName());

		taskInstance.getProperties().put(WorkflowConstants.VAR_TASKID,
				ti.getVariableLocally(WorkflowConstants.VAR_TASKID));
		Map<String, Object> variables = ti.getVariablesLocally();

		// if the task is ended
		if (ti.getEnd() != null)
			taskInstance.setState(WorkflowTaskInstance.STATE.DONE);
		else if (ti.getStart() == null)
			taskInstance.setState(WorkflowTaskInstance.STATE.NOT_YET_STARTED);
		else if (ti.getStart() != null && ti.isSuspended() != true)
			taskInstance.setState(WorkflowTaskInstance.STATE.STARTED);
		else if (ti.getStart() != null && ti.isSuspended())
			taskInstance.setState(WorkflowTaskInstance.STATE.SUSPENDED);

		taskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE, taskInstance.getState().getVal());

		// mapping
		HashMap<String, Object> mapped_properties = new HashMap<String, Object>();
		for (String key : variables.keySet())
			mapped_properties.put(key.toString(), variables.get(key));

		if (ti.getActorId() != null)
			taskInstance.getProperties().put(WorkflowConstants.VAR_OWNER, ti.getActorId());

		if (ti.getPooledActors() != null) {
			Set<PooledActor> pooledActors = ti.getPooledActors();
			List<String> actors = new LinkedList<String>();

			for (PooledActor actor : pooledActors)
				actors.add(actor.getActorId());

			taskInstance.getProperties().put(WorkflowConstants.VAR_POOLEDACTORS, actors);

		}
		
		if (ti.getStart() != null)
			taskInstance.getProperties().put(WorkflowConstants.VAR_STARTDATE, ti.getStart());

		if (ti.getEnd() != null)
			taskInstance.getProperties().put(WorkflowConstants.VAR_ENDDATE, ti.getEnd());

		if (ti.getDueDate() != null)
			taskInstance.getProperties().put(WorkflowConstants.VAR_DUEDATE, ti.getDueDate());

		if (ti.getDescription() != null)
			taskInstance.getProperties().put(WorkflowConstants.VAR_DESCRIPTION, ti.getDescription());

		taskInstance.getProperties().putAll(mapped_properties);

		List<Transition> transitions = ti.getAvailableTransitions();

		// you don't have any more transition if you got finished a task for a
		// while ago
		if (transitions != null) {
			for (Transition tr : transitions) {

				// TODO: should not occure
				if (tr.getTo() == null)
					continue;

				com.logicaldoc.workflow.model.Transition trans = new com.logicaldoc.workflow.model.Transition();
				trans.name = tr.getName();

				trans.to = tr.getTo().getName();

				taskInstance.getTransitions().add(trans);
			}

			for (int i = 0; i < taskInstance.getTransitions().size() - 1; i++) {
				for (int j = 0; j < taskInstance.getTransitions().size() - 1; j++) {
					com.logicaldoc.workflow.model.Transition current = taskInstance.getTransitions().get(j);
					com.logicaldoc.workflow.model.Transition next = taskInstance.getTransitions().get(j + 1);

					if (current.name.compareTo(next.name) > 0) {
						taskInstance.getTransitions().set(j, next);
						taskInstance.getTransitions().set(j + 1, current);
					}

				}
			}
		}

		return taskInstance;

	}

	public static WorkflowInstance createWorkflowInstance(ProcessInstance processInstance) {

		WorkflowInstance wfi = new WorkflowInstance();
		wfi.setProcessDefinitionId(processInstance.getProcessDefinition().getId());
		wfi.setId(Long.toString(processInstance.getId()));
		wfi.setStartDate(processInstance.getStart());
		wfi.setEndDate(processInstance.getEnd());
		wfi.setName(processInstance.getProcessDefinition().getName());

		wfi.getProperties().put(WorkflowConstants.VAR_DOCUMENTS,
				processInstance.getContextInstance().getVariable(WorkflowConstants.VAR_DOCUMENTS));

		wfi.getProperties().put(WorkflowConstants.VAR_TEMPLATE,
				processInstance.getContextInstance().getVariable(WorkflowConstants.VAR_TEMPLATE));

		return wfi;
	}

	@SuppressWarnings("unchecked")
	public static WorkflowDefinition createWorkflowDefinition(ProcessDefinition definition) {
		WorkflowDefinition workflowDefinition = new WorkflowDefinition();
		workflowDefinition.setName(definition.getName());
		workflowDefinition.setDescription(definition.getDescription());
		workflowDefinition.setDefinitionId(WorkflowFactory.createProcessDefintionId(definition.getId()));

		workflowDefinition.setProperties(definition.getDefinitions());

		return workflowDefinition;
	}

	public static long getJbpmProcessInstanceId(String processId) {

		long jbpmProcessId = -1;

		if (processId.contains("@")) {
			jbpmProcessId = Long.parseLong(processId.substring(0, processId.indexOf('@')));
		} else {
			if (processId.matches("[0-9]*")) {
				jbpmProcessId = Long.parseLong(processId);
			} else {
				throw new WorkflowException("The WorkflowId does not provide a valid id");
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

	public static String createProcessDefintionId(Long defId) {
		return "jbpm-" + defId + "pid";
	}

	public static Long getJbpmProcessDefinitionId(String id) {

		if (id.startsWith("jbpm-") == false)
			throw new IllegalStateException("id does not start with jbpm");

		if (id.endsWith("pid") == false)
			throw new IllegalStateException("id does not end with pid");

		id = id.replace("jbpm-", "").replace("pid", "");

		try {
			return Long.parseLong(id);
		} catch (NumberFormatException e) {
			throw new IllegalStateException("id does not match to a long value");
		}
	}

	public static Long getJbpmProcessDefinitionIdFromTaskInstance(String id) {
		return Long.parseLong(id.substring(0, id.indexOf('@')));
	}

}
