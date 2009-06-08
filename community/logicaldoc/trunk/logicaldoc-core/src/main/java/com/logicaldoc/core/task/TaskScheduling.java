package com.logicaldoc.core.task;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;

/**
 * Scheduling configuration for a Task
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5.0
 */
public class TaskScheduling {
	private String taskName;

	private String seconds = "0";

	private String minutes = "10";

	private String hours = "*";

	private String dayOfMonth = "*";

	private String month = "*";

	private String dayOfWeek = "?";

	private Date previousFireTime;

	// Maximum execution length expressed in seconds
	private long maxLength = -1;

	private boolean enabled = true;

	public TaskScheduling(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getMinutes() {
		return minutes;
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * The maximum duration expressed in seconds
	 */
	public long getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(long maxLength) {
		this.maxLength = maxLength;
	}

	public Date getNextFireTime() {
		CronTrigger cronTrigger = (CronTrigger) Context.getInstance().getBean(taskName + "Trigger");
		// The following loop 'while' is needed to update the next execution time
		// of the task into the Task list page
		while (cronTrigger.getNextFireTime().getTime() < System.currentTimeMillis()) {
			cronTrigger.setNextFireTime(cronTrigger.getFireTimeAfter(previousFireTime));
		}
		return cronTrigger.getNextFireTime();
	}

	public String getCronExpression() {
		return seconds.trim() + " " + minutes.trim() + " " + hours.trim() + " " + dayOfMonth.trim() + " "
				+ month.trim().trim() + " " + dayOfWeek.trim();
	}

	public void setCronExpression(String cronExpression) throws ParseException {
		CronTrigger cronTrigger = (CronTrigger) Context.getInstance().getBean(taskName + "Trigger");
		cronTrigger.setCronExpression(cronExpression);
		StringTokenizer st = new StringTokenizer(cronExpression, " ", false);
		seconds = st.nextToken();
		minutes = st.nextToken();
		hours = st.nextToken();
		dayOfMonth = st.nextToken();
		month = st.nextToken();
		dayOfWeek = st.nextToken();

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Loads scheduling configurations from persistent storage
	 */
	public void load() throws IOException, ParseException {
		PropertiesBean config = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		String enbl = config.getProperty("schedule.enabled." + taskName);
		this.enabled = "true".equals(enbl);
		setCronExpression(config.getProperty("schedule.cron." + taskName));
		try {
			maxLength = Long.parseLong(config.getProperty("schedule.length." + taskName));
		} catch (Exception e) {

		}
	}

	/**
	 * Saves scheduling configurations in the persistent storage
	 */
	public void save() throws IOException, ParseException {
		Scheduler scheduler = (Scheduler) Context.getInstance().getBean("Scheduler");
		CronTrigger cronTrigger = (CronTrigger) Context.getInstance().getBean(taskName + "Trigger");
		String expression = getCronExpression();
		cronTrigger.setCronExpression(expression);

		try {
			// Reschedule the job
			scheduler.rescheduleJob(taskName + "Trigger", "DEFAULT", cronTrigger);
		} catch (Exception e) {

		}

		PropertiesBean config = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		config.setProperty("schedule.cron." + taskName, expression);
		config.setProperty("schedule.enabled." + taskName, enabled ? "true" : "false");
		config.setProperty("schedule.length." + taskName, Long.toString(maxLength));
		config.write();
	}

	/**
	 * Checks if the time was expired(a maxLength must be defined)
	 */
	boolean isExpired() {
		if (previousFireTime == null || getMaxLength() <= 0)
			return false;
		else {
			Date now = new Date();
			long elapsedTime = now.getTime() - previousFireTime.getTime();
			elapsedTime = (long) elapsedTime / 1000;
			return elapsedTime > getMaxLength();
		}
	}

	@Override
	public String toString() {
		return getCronExpression();
	}
}