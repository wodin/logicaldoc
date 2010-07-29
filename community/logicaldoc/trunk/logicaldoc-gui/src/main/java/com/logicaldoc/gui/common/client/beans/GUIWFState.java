package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

import com.logicaldoc.gui.common.client.util.Util;

/**
 * Workflow State bean as used in the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUIWFState implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static int TYPE_UNDEFINED = -1;

	public final static int TYPE_TASK = 0;

	public final static int TYPE_END = 1;

	public final static int TYPE_JOIN = 2;

	public final static int TYPE_FORK = 3;

	public final static String TIME_MINUTE = "minute";

	public final static String TIME_HOUR = "hour";

	public final static String TIME_BUSINESS_HOUR = "businesshour";

	public final static String TIME_DAY = "day";

	public final static String TIME_BUSINESS_DAY = "businessday";

	public final static String TIME_WEEK = "week";

	public final static String TIME_BUSINESS_WEEK = "businessweek";

	private int type = TYPE_TASK;

	private String id;

	private String name;

	private String description;

	private int dueDateNumber = 0;

	private String dueDateUnit = TIME_MINUTE;

	private int reminderNumber = 0;

	private String reminderUnit = TIME_MINUTE;

	private GUIUser[] participants;

	private GUITransition[] transitions;

	public GUIWFState() {
	}

	public GUIWFState(String id, String name, int type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

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

	public GUIUser[] getParticipants() {
		return participants;
	}

	public void setParticipants(GUIUser[] participants) {
		this.participants = participants;
	}

	public GUITransition[] getTransitions() {
		return transitions;
	}

	public void setTransitions(GUITransition[] transitions) {
		this.transitions = transitions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		if (type == TYPE_TASK) {
			return Util.imageUrl("task.png");
		} else if (type == TYPE_JOIN) {
			return Util.imageUrl("join.png");
		} else if (type == TYPE_FORK) {
			return Util.imageUrl("fork.png");
		} else if (type == TYPE_END) {
			return Util.imageUrl("endState.png");
		} else {
			return "";
		}
	}

	public int getDueDateNumber() {
		return dueDateNumber;
	}

	public void setDueDateNumber(int dueDateNumber) {
		this.dueDateNumber = dueDateNumber;
	}

	public String getDueDateUnit() {
		return dueDateUnit;
	}

	public void setDueDateUnit(String dueDateUnit) {
		this.dueDateUnit = dueDateUnit;
	}

	public int getReminderNumber() {
		return reminderNumber;
	}

	public void setReminderNumber(int reminderNumber) {
		this.reminderNumber = reminderNumber;
	}

	public String getReminderUnit() {
		return reminderUnit;
	}

	public void setReminderUnit(String reminderUnit) {
		this.reminderUnit = reminderUnit;
	}
}
