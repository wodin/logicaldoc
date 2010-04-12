package com.logicaldoc.workflow.transform;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Transition;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

/**
 * 
 * <task-node name="review"> <task name="reviewTask"> <event type="task-create">
 * <script> <expression> task_var = process_var; </expression> <variable
 * name="task_var" access="write"/> </script> </event> </task> <transition
 * name="" to="end"/> </task-node>
 * 
 */
public class JBPMWorkflowTask implements TransformModel {

	protected static Log log = LogFactory.getLog(JBPMWorkflowTask.class);

	@Override
	public Object open(TransformContext ctx) {

		Document wr = ((JBPMTransformContext) ctx).getDocumentBuildObject();

		WorkflowTask workflowTask = (WorkflowTask) ctx.getCurrentBaseModel();

		Element taskNode = wr.createElement("task-node");
		taskNode.setAttribute("name", workflowTask.getId());

		Element task = wr.createElement("task");
		task.setAttribute("name", workflowTask.getName());

		// event
		// still in development...

		Element assignmentNode = wr.createElement("assignment");
		try {
			PropertiesBean pbean = new PropertiesBean();
			assignmentNode.setAttribute("class", pbean.getProperty("workflow.assignment.handler"));
		} catch (IOException e) {
			assignmentNode.setAttribute("class", "com.logicaldoc.workflow.action.DefaultAssignmentHandler");
		}

		Element taskId = wr.createElement("taskId");
		taskId.setTextContent(workflowTask.getId());
		assignmentNode.appendChild(taskId);
		task.appendChild(assignmentNode);

		Element initialEvent = wr.createElement("event");
		initialEvent.setAttribute("type", "task-create");
		Element initialActionEvent = wr.createElement("action");
		initialActionEvent.setAttribute("class", "com.logicaldoc.workflow.action.TaskSetupHandler");
		initialEvent.appendChild(initialActionEvent);

		taskId = wr.createElement("taskId");
		taskId.setTextContent(workflowTask.getId());
		initialActionEvent.appendChild(taskId);
		task.appendChild(initialEvent);
		/**
		 * Assignment
		 */
		/*
		 * //TODO: We have to check if the actor is a group so we have instead a
		 * pooled-task if(assignees.size() == 1)
		 * assignment.setAttribute("actor-id", assignees.get(0).getValue());
		 * 
		 * //we got pooled-actors else { String assigneeStringList = ""; for(int
		 * idx = 0; idx < assignees.size(); idx++){
		 * assigneeStringList+=assignees.get(idx).getValue(); if(idx+1 !=
		 * assignees.size()) assigneeStringList+=","; }
		 * 
		 * assignment.setAttribute("pooled-actors", assigneeStringList); }
		 * 
		 * task.appendChild(assignment);
		 */

		/**
		 * Escalation management
		 */
		if (workflowTask.getDueDateValue() != null && workflowTask.getDueDateValue() > 0) {
			Integer dueDateValue = workflowTask.getDueDateValue();
			String dueDateUnit = workflowTask.getDueDateUnit();

			// set time unit to plural (e.g. "hour" -> "hours")
			if (dueDateValue > 1)
				dueDateUnit += "s";

			Element timer = wr.createElement("timer");
			timer.setAttribute("name", UUID.randomUUID().toString());
			timer.setAttribute("duedate", dueDateValue + " " + dueDateUnit.toLowerCase());

			if (workflowTask.getRemindTimeValue() != null && workflowTask.getRemindTimeValue() > 0) {
				Integer reminderValue = workflowTask.getRemindTimeValue();
				String reminderUnit = workflowTask.getRemindTimeUnit();

				// set time unit to plural (e.g. "hour" -> "hours")
				if (reminderValue > 1)
					reminderUnit += "s";

				timer.setAttribute("repeat", reminderValue + " " + reminderUnit.toLowerCase());
			}

			Element actionTimer = wr.createElement("action");

			try {
				PropertiesBean pbean = new PropertiesBean();
				actionTimer.setAttribute("class", pbean.getProperty("workflow.remind.handler"));
			} catch (IOException e) {
				actionTimer.setAttribute("class", "com.logicaldoc.workflow.action.DefaultRemindHandler");
			}
			timer.appendChild(actionTimer);

			task.appendChild(timer);
		}

		/**
		 * Transitions
		 */

		List<Transition> transitions = workflowTask.getTransitions();

		taskNode.appendChild(task);

		for (Transition transitionModel : transitions) {

			if (transitionModel.getDestination() == null) {
				log.warn("Destination is null for " + transitionModel.getName() + " in " + workflowTask.getName());
				continue;
			}

			Element transition = wr.createElement("transition");
			transition.setAttribute("name", transitionModel.getName());
			transition.setAttribute("to", transitionModel.getDestination().getId());

			taskNode.appendChild(transition);
		}

		return taskNode;
	}

	@Override
	public Object end(TransformContext ctx) {
		return null;
	}

	@Override
	public boolean matches(BaseWorkflowModel model) {
		return model instanceof WorkflowTask;
	}

}
