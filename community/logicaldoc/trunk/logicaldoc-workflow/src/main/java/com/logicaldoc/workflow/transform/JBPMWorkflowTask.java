package com.logicaldoc.workflow.transform;


import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicaldoc.workflow.editor.model.Assignee;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Transition;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

/**

  <task-node name="review">
     <task name="reviewTask">
        <event type="task-create">
           <script>
              <expression>
                 task_var = process_var;
              </expression>
              <variable name="task_var" access="write"/>
           </script>
        </event>
     </task>
     <transition name="" to="end"/>
  </task-node>

 */
public class JBPMWorkflowTask implements TransformModel {

	@Override
	public Object open(TransformContext ctx){
		
		

		
		Document wr = ((JBPMTransformContext) ctx).getDocumentBuildObject();

		WorkflowTask workflowTask = (WorkflowTask) ctx.getCurrentBaseModel();
		
		Element taskNode = wr.createElement("task-node");
		taskNode.setAttribute("name", workflowTask.getId());
		
		Element task = wr.createElement("task");
		task.setAttribute("name", workflowTask.getName());
		
		//event 
		//still in development...
		/**
		Element eventTaskCreate  = wr.createElement("event");
		eventTaskCreate.setAttribute("type", "task-create");
		Element actionTaskCreate = wr.createElement("action");
		//TODO: Enter classname here
		actionTaskCreate.setAttribute("class", "com.logicaldoc.workflow.flow.action...");
		eventTaskCreate.appendChild(actionTaskCreate);
		taskNode.appendChild(eventTaskCreate);
		**/
		//assignments
		
		List<Assignee> assignees = workflowTask.getAssignees();
		
		Element assignment = wr.createElement("assignment");
		
		/**
		 * Assignment
		 */
		//TODO: We have to check if the actor is a group so we have instead a pooled-task 
		if(assignees.size() == 1)
			assignment.setAttribute("actor-id", assignees.get(0).getValue());
		
		//we got pooled-actors
		else {
			String assigneeStringList = "";
			for(int idx = 0; idx < assignees.size(); idx++){
				assigneeStringList+=assignees.get(idx);
				if(idx+1 != assignees.size())
					assigneeStringList+=",";
			}
			
			assignment.setAttribute("pooled-actors", assigneeStringList);
			
		}
		
		task.appendChild(assignment);
		
		
		/**
		 * Escalationmanagement
		 */
		
		
		if(workflowTask.getDueDateValue() != null && workflowTask.getDueDateValue() > 0){
			Integer dueDateValue = workflowTask.getDueDateValue();
			String dueDateUnit = workflowTask.getDueDateUnit();
			
			//set time unit to plural (e.g. "hour" -> "hours") 
			if(dueDateValue > 1)
				dueDateUnit+="s";
			
			
			Element timer = wr.createElement("timer");

			timer.setAttribute("duedate", dueDateValue + " " + dueDateUnit);
			
			if(workflowTask.getRemindTimeValue() != null && workflowTask.getRemindTimeValue()  > 0){
				Integer reminderValue = workflowTask.getRemindTimeValue();
				String reminderUnit = workflowTask.getRemindTimeUnit();
				
				//set time unit to plural (e.g. "hour" -> "hours") 
				if(reminderValue > 1)
					reminderUnit+="s";
				
				timer.setAttribute("reminder", reminderValue + " " + reminderUnit);
			}
			
			task.appendChild(timer);
			
		}
		
		/**
		 * Transitions
		 */
		
		
		List<Transition> transitions = workflowTask.getTransitions();
		
		
		taskNode.appendChild(task);
		
		
		for (Transition transitionModel : transitions) {
			
			if(transitionModel.getDestination() == null){
				System.out.println("WARN: Destination is null for " + transitionModel.getName() + " in " + workflowTask.getName());
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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean matches(BaseWorkflowModel model) {
		return model instanceof WorkflowTask;
	}
	

}
