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

	private long id = 0;

	private String name = "";

	private String description = "";

	private String taskAssignmentSubject = "";

	private String taskAssignmentBody = "";

	private String reminderSubject = "";

	private String reminderBody = "";

	private String startStateId = "0";

	private String supervisor = "";

	private GUIWFState[] states;

	public GUIWFState getStateById(String id) {
		if (states != null && states.length > 0) {
			for (GUIWFState state : states) {
				if (state.getId().equals(id)) {
					return state;
				}
			}
		}
		return null;
	}

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

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getStartStateId() {
		return startStateId;
	}

	public void setStartStateId(String startStateId) {
		this.startStateId = startStateId;
	}

	public GUIWFState[] getStates() {
		return states;
	}

	public void setStates(GUIWFState[] states) {
		this.states = states;
	}
}
