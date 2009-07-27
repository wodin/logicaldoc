package com.logicaldoc.workflow;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.Constants;
import com.logicaldoc.workflow.model.Transition;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;

public class WorkflowManager {

	private WorkflowService workflowService;

	public WorkflowManager() {
		this.workflowService = (WorkflowService) Context.getInstance().getBean(
				"workflowService");
	}

	public List<WorkflowDefinition> getWorkflowDefinitions() {
		return this.workflowService.getAllDefinitions();
	}

	public void rowSelectionListener(RowSelectorEvent event) {
		WorkflowDefinition workflowDefinition = this.workflowService
				.getAllDefinitions().get(event.getRow());

		WorkflowInstance instance = this.workflowService
				.startWorkflow(workflowDefinition, null);
		this.workflowService.signal(instance.id);
	}

	public void endTask(ActionEvent actionEvent){
		UIComponent component = (UIComponent)actionEvent.getSource();
		WorkflowTaskInstance taskInstance = (WorkflowTaskInstance)((UIParameter)component.getChildren().get(0)).getValue();
		Transition transition = (Transition)((UIParameter)component.getChildren().get(1)).getValue();
		
		this.workflowService.endTask(taskInstance.id, transition.name);
		
	}
	
	public List<WorkflowTaskInstance> getTaskInstances(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		
		return this.workflowService.getTaskInstancesForUser(username);
		
	}
	
	public void assign(ActionEvent actionEvent){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		
		UIComponent component = (UIComponent)actionEvent.getSource();
		WorkflowTaskInstance instance = (WorkflowTaskInstance)((UIParameter)component.getChildren().get(0)).getValue();
		this.workflowService.assign(instance.id, username);
	}
	
	public List<WorkflowTaskInstance> getPooledTaskInstances(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		return this.workflowService.getPooledTaskInstancesForUser(username);
	}
}
