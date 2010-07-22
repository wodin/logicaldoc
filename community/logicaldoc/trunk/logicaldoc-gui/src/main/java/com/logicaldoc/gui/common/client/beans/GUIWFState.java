package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Workflow State bean as used in the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUIWFState implements Serializable {

	private static final long serialVersionUID = 1L;

	private int type;

	private String name;

	private String description;

	private Date duedate;

	private Date remindTime;

	private GUIUser[] participants;

	private Map<String, GUIWFState> transitions;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public Date getDuedate() {
		return duedate;
	}

	public void setDuedate(Date duedate) {
		this.duedate = duedate;
	}

	public Date getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(Date remindTime) {
		this.remindTime = remindTime;
	}

	public GUIUser[] getParticipants() {
		return participants;
	}

	public void setParticipants(GUIUser[] participants) {
		this.participants = participants;
	}

	public Map<String, GUIWFState> getTransitions() {
		return transitions;
	}

	public void setTransitions(Map<String, GUIWFState> transitions) {
		this.transitions = transitions;
	}
}
