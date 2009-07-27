package com.logicaldoc.workflow.model;

import java.util.LinkedList;
import java.util.List;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public class WorkflowTemplate {
	
	private Long id; 
	
	private String name;
	
	private String description;
	
	private String startState;
	
	private EMailMessage assignmentMailMessage;
	
	private EMailMessage reminderMailMessage;
	
	private List<BaseWorkflowModel> workflowComponents = new LinkedList<BaseWorkflowModel>();
	
	public WorkflowTemplate(){
		this.assignmentMailMessage = new EMailMessage("", "");
		this.reminderMailMessage = new EMailMessage("", "");
	}
	
	public void setWorkflowComponents(List<BaseWorkflowModel> workflowComponents) {
		this.workflowComponents = workflowComponents;
	}
	
	public List<BaseWorkflowModel> getWorkflowComponents() {
		return workflowComponents;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStartState() {
		return startState;
	}
	
	public void setStartState(String startState) {
		this.startState = startState;
	}
	
	public void setAssignmentMailMessage(EMailMessage assignmentMailMessage) {
		this.assignmentMailMessage = assignmentMailMessage;
	}
	
	public EMailMessage getAssignmentMailMessage() {
		return assignmentMailMessage;
	}
	
	public void setReminderMailMessage(EMailMessage reminderMailMessage) {
		this.reminderMailMessage = reminderMailMessage;
	}
	
	public EMailMessage getReminderMailMessage() {
		return reminderMailMessage;
	}
}
