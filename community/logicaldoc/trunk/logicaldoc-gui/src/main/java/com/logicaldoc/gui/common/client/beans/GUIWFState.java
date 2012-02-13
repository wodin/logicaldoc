package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

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

	private String[] participants;

	private GUITransition[] transitions;

	private String owner = "";

	private String pooledActors = "";

	private Date startDate;

	private Date endDate;

	private String dueDate;

	private String taskState;

	private String comment;

	private int top = 0;

	private int left = 0;

	private boolean initial = false;

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

	public String[] getParticipants() {
		return participants;
	}

	public void setParticipants(String[] participants) {
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPooledActors() {
		return pooledActors;
	}

	public void setPooledActors(String pooledActors) {
		this.pooledActors = pooledActors;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}
}
