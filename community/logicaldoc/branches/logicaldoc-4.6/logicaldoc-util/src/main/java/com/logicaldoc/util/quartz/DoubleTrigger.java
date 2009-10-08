package com.logicaldoc.util.quartz;

import java.util.Date;

import org.quartz.Calendar;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.utils.Key;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * This trigger wraps both a SimpleTrigger and a
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class DoubleTrigger extends SimpleTriggerBean {
	private static final long serialVersionUID = 1L;

	private SimpleTriggerBean simpleTrigger;

	private CronTriggerBean cronTrigger;

	public static String MODE_CRON = "cron";

	public static String MODE_SIMPLE = "simple";

	private String mode = MODE_SIMPLE;

	private DoubleTrigger() {
		super();
	}

	public void addTriggerListener(String name) {
		getWrappedTrigger().addTriggerListener(name);
	}

	public void clearAllTriggerListeners() {
		getWrappedTrigger().clearAllTriggerListeners();
	}

	public Object clone() {
		return getWrappedTrigger().clone();
	}

	public int compareTo(Object obj) {
		return getWrappedTrigger().compareTo(obj);
	}

	public Date computeFirstFireTime(Calendar arg0) {
		return getWrappedTrigger().computeFirstFireTime(arg0);
	}

	public boolean equals(Object obj) {
		return getWrappedTrigger().equals(obj);
	}

	public int executionComplete(JobExecutionContext arg0, JobExecutionException arg1) {
		return getWrappedTrigger().executionComplete(arg0, arg1);
	}

	public String getCalendarName() {
		return getWrappedTrigger().getCalendarName();
	}

	public String getDescription() {
		return getWrappedTrigger().getDescription();
	}

	public Date getEndTime() {
		return getWrappedTrigger().getEndTime();
	}

	public Date getFinalFireTime() {
		return getWrappedTrigger().getFinalFireTime();
	}

	public String getFireInstanceId() {
		return getWrappedTrigger().getFireInstanceId();
	}

	public Date getFireTimeAfter(Date arg0) {
		return getWrappedTrigger().getFireTimeAfter(arg0);
	}

	public String getFullJobName() {
		return getWrappedTrigger().getFullJobName();
	}

	public String getFullName() {
		return getWrappedTrigger().getFullName();
	}

	public String getGroup() {
		return getWrappedTrigger().getGroup();
	}

	public JobDataMap getJobDataMap() {
		return getWrappedTrigger().getJobDataMap();
	}

	public String getJobGroup() {
		return getWrappedTrigger().getJobGroup();
	}

	public String getJobName() {
		return getWrappedTrigger().getJobName();
	}

	public Key getKey() {
		return getWrappedTrigger().getKey();
	}

	public int getMisfireInstruction() {
		return getWrappedTrigger().getMisfireInstruction();
	}

	public String getName() {
		return getWrappedTrigger().getName();
	}

	public Date getNextFireTime() {
		return getWrappedTrigger().getNextFireTime();
	}

	public Date getPreviousFireTime() {
		return getWrappedTrigger().getPreviousFireTime();
	}

	public int getPriority() {
		return getWrappedTrigger().getPriority();
	}

	public Date getStartTime() {
		return getWrappedTrigger().getStartTime();
	}

	public String[] getTriggerListenerNames() {
		return getWrappedTrigger().getTriggerListenerNames();
	}

	public int hashCode() {
		return getWrappedTrigger().hashCode();
	}

	public boolean isVolatile() {
		return getWrappedTrigger().isVolatile();
	}

	public boolean mayFireAgain() {
		return getWrappedTrigger().mayFireAgain();
	}

	public boolean removeTriggerListener(String name) {
		return getWrappedTrigger().removeTriggerListener(name);
	}

	public void setCalendarName(String calendarName) {
		getWrappedTrigger().setCalendarName(calendarName);
	}

	public void setDescription(String description) {
		getWrappedTrigger().setDescription(description);
	}

	public void setEndTime(Date arg0) {
		getWrappedTrigger().setEndTime(arg0);
	}

	public void setFireInstanceId(String id) {
		getWrappedTrigger().setFireInstanceId(id);
	}

	public void setGroup(String group) {
		getWrappedTrigger().setGroup(group);
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		getWrappedTrigger().setJobDataMap(jobDataMap);
	}

	public void setJobGroup(String jobGroup) {
		getWrappedTrigger().setJobGroup(jobGroup);
	}

	public void setJobName(String jobName) {
		getWrappedTrigger().setJobName(jobName);
	}

	public void setMisfireInstruction(int misfireInstruction) {
		getWrappedTrigger().setMisfireInstruction(misfireInstruction);
	}

	public void setName(String name) {
		getWrappedTrigger().setName(name);
	}

	public void setPriority(int priority) {
		getWrappedTrigger().setPriority(priority);
	}

	public void setStartTime(Date arg0) {
		getWrappedTrigger().setStartTime(arg0);
	}

	public void setVolatility(boolean volatility) {
		getWrappedTrigger().setVolatility(volatility);
	}

	public String toString() {
		return getWrappedTrigger().toString();
	}

	public void triggered(Calendar arg0) {
		getWrappedTrigger().triggered(arg0);
	}

	public void updateAfterMisfire(Calendar arg0) {
		getWrappedTrigger().updateAfterMisfire(arg0);
	}

	public void updateWithNewCalendar(Calendar arg0, long arg1) {
		getWrappedTrigger().updateWithNewCalendar(arg0, arg1);
	}

	public void validate() throws SchedulerException {
		getWrappedTrigger().validate();
	}

	protected boolean validateMisfireInstruction(int arg0) {
		return validateMisfireInstruction(arg0);
	}

	public Trigger getWrappedTrigger() {
		if ("simple".equals(mode)) {
			return simpleTrigger;
		} else {
			return cronTrigger;
		}
	}

	public void setSimpleTrigger(SimpleTriggerBean simpleTrigger) {
		this.simpleTrigger = simpleTrigger;
	}

	public void setCronTrigger(CronTriggerBean cronTrigger) {
		this.cronTrigger = cronTrigger;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public CronTriggerBean getCronTrigger() {
		return cronTrigger;
	}

	public SimpleTriggerBean getSimpleTrigger() {
		return simpleTrigger;
	}
}
