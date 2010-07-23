package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Workflow bean as used in the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUIWorkflow implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private String name;

	private String description;

	private String taskAssignmentSubject;

	private String taskAssignmentBody;

	private String reminderSubject;

	private String reminderBody;

	private String startState;

	private GUIUser supervisor;

	private GUIWFState[] states;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTaskAssignmentSubject() {
		return taskAssignmentSubject;
	}

	public void setTaskAssignmentSubject(String taskAssignmentSubject) {
		this.taskAssignmentSubject = taskAssignmentSubject;
	}

	public String getTaskAssignmentBody() {
		return taskAssignmentBody;
	}

	public void setTaskAssignmentBody(String taskAssignmentBody) {
		this.taskAssignmentBody = taskAssignmentBody;
	}

	public String getReminderSubject() {
		return reminderSubject;
	}

	public void setReminderSubject(String reminderSubject) {
		this.reminderSubject = reminderSubject;
	}

	public String getReminderBody() {
		return reminderBody;
	}

	public void setReminderBody(String reminderBody) {
		this.reminderBody = reminderBody;
	}

	public GUIUser getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(GUIUser supervisor) {
		this.supervisor = supervisor;
	}

	public String getStartState() {
		return startState;
	}

	public void setStartState(String startState) {
		this.startState = startState;
	}

	public GUIWFState[] getStates() {
		return states;
	}

	public void setStates(GUIWFState[] states) {
		this.states = states;
	}
}
