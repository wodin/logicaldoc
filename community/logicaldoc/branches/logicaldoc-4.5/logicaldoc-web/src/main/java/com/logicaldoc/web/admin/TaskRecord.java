package com.logicaldoc.web.admin;

import java.io.IOException;
import java.text.ParseException;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.task.Task;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * A wrapper around the Task for listing and also for editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5.0
 */
public class TaskRecord {
	private Task task;

	public TaskRecord() {
	}

	public TaskRecord(Task task) {
		this.task = task;
	}

	public String getName() {
		return task.getName();
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public String getDisplayName() {
		return Messages.getMessage("task.name." + task.getName());
	}

	public String enable() {
		task.getScheduling().setEnabled(true);
		try {
			task.getScheduling().save();
		} catch (IOException e) {
			Messages.addError("task.error.save");
		} catch (ParseException e) {
			Messages.addError("task.error.syntax");
		}
		return "";
	}

	public String disable() {
		task.getScheduling().setEnabled(false);
		try {
			task.getScheduling().save();
		} catch (IOException e) {
			Messages.addError("task.error.save");
		} catch (ParseException e) {
			Messages.addError("task.error.syntax");
		}
		return "";
	}

	public String save() {
		try {
			if(!task.getScheduling().getDayOfMonth().trim().equals("?") && !task.getScheduling().getDayOfWeek().trim().equals("?"))
				throw new Exception("Unsupported combination");
			task.getScheduling().save();
			TasksRecordsManager manager = ((TasksRecordsManager) FacesUtil.accessBeanFromFacesContext(
					"tasksRecordsManager", FacesContext.getCurrentInstance()));
			manager.setSelectedPanel("list");
		} catch (IOException e) {
			Messages.addError("task.error.save");
		} catch (ParseException e) {
			Messages.addError("task.error.syntax");
		}catch (Exception e) {
			Messages.addError("task.error.syntax.unsupported");
		}
		return null;
	}
}