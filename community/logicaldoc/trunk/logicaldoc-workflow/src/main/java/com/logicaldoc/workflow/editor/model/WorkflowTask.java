package com.logicaldoc.workflow.editor.model;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.editor.controll.EditController;


public class WorkflowTask extends BaseWorkflowModel {
	
	private String description;
	
	private Integer dueDateValue;
	
	private String dueDateUnit;
	
	private Integer remindTimeValue;
	
	private String remindTimeUnit;
	
	private List<Assignee> assignees;
	
	private boolean parallelProcessingSupported;
	
	public WorkflowTask(){
		this.assignees = new ArrayList<Assignee>();
	}
	
	public WorkflowTask(WorkflowTask workflowTask){
		this.copy(workflowTask);
		
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDueDateUnit() {
		return dueDateUnit;
	}
	
	public void setRemindTimeUnit(String remindTimeUnit) {
		this.remindTimeUnit = remindTimeUnit;
	}
	
	public void setDueDateUnit(String dueDateUnit) {
		this.dueDateUnit = dueDateUnit;
	}
	
	public String getRemindTimeUnit() {
		return remindTimeUnit;
	}
	
	public void addAssignee(){
		this.assignees.add(new Assignee());
	}
	
	public void setAssignees(List<Assignee> assignes) {
		this.assignees = assignes;
	}

	public List<Assignee> getAssignees() {
		return assignees;
	}
	
	public void setRemindTimeValue(Integer remindTimeValue) {
		this.remindTimeValue = remindTimeValue;
	}
	
	public Integer getRemindTimeValue() {
		return remindTimeValue;
	}
	
	public void setDueDateValue(Integer dueDateValue) {
		this.dueDateValue = dueDateValue;
	}
	
	public Integer getDueDateValue() {
		return dueDateValue;
	}
	
	public void setParallelProcessingSupported(
			boolean parallelProcessingSupported) {
		this.parallelProcessingSupported = parallelProcessingSupported;
	}
	
	public boolean getParallelProcessingSupported() {
		return parallelProcessingSupported;
	}
	
	public boolean isParallelProcessingSupported() {
		return parallelProcessingSupported;
	}
	
	@Override
	public String getImage() {
		return "task.png";
	}
	
	@Override
	public String getTemplate() {
		return "task";
	}
	
	@Override
	public EditController getController() {
		return (EditController)Context.getInstance().getBean("workflowTaskController");
	}
	
	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		super.copy(baseWorkflowModel);
		
		WorkflowTask workflowTask = (WorkflowTask) baseWorkflowModel;
		this.dueDateValue = workflowTask.dueDateValue;
		this.dueDateUnit = workflowTask.dueDateUnit;
		this.remindTimeUnit = workflowTask.remindTimeUnit;
		this.remindTimeValue = workflowTask.remindTimeValue;
		this.assignees = workflowTask.assignees;
		this.parallelProcessingSupported = workflowTask.parallelProcessingSupported;
		
		return this;
	}
	
	@Override
	public boolean isPossibleStartState() {
		return true;
	}
	
	@Override
	public String getType() {
		return "task";
	}
	
}
