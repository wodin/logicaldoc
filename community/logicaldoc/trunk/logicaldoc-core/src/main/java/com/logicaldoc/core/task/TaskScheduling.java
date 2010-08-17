package com.logicaldoc.core.task;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.system.CpuInfo;

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

	private String mode = TaskTrigger.MODE_SIMPLE;

	private long delay = 1000;

	private long interval = 60000;

	private int minCpuIdle = 0;

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
		Trigger trigger = (Trigger) Context.getInstance().getBean(taskName + "Trigger");
		if (!(trigger instanceof TaskTrigger))
			return null;

		// The following loop 'while' is needed to update the next execution
		// time of the task into the Task list page
		if (TaskTrigger.MODE_CRON.equals(getMode())) {
			((TaskTrigger) trigger).setNextFireTime(trigger.getFireTimeAfter(previousFireTime));
			return trigger.getFireTimeAfter(previousFireTime);
		} else {
			if (previousFireTime != null) {
				long next = previousFireTime.getTime() + this.getInterval();
				return new Date(next);
			} else
				return null;
		}
	}

	public String getCronExpression() {
		return seconds.trim() + " " + minutes.trim() + " " + hours.trim() + " " + dayOfMonth.trim() + " "
				+ month.trim().trim() + " " + dayOfWeek.trim();
	}

	public void setCronExpression(String cronExpression) throws ParseException {
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
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String enbl = config.getProperty("schedule.enabled." + taskName);
		this.enabled = "true".equals(enbl);
		setCronExpression(config.getProperty("schedule.cron." + taskName));
		setMode(config.getProperty("schedule.mode." + taskName));
		try {
			maxLength = Long.parseLong(config.getProperty("schedule.length." + taskName));
			interval = Long.parseLong(config.getProperty("schedule.interval." + taskName));
			delay = Long.parseLong(config.getProperty("schedule.delay." + taskName));
			minCpuIdle = Integer.parseInt(config.getProperty("schedule.cpuidle." + taskName));
		} catch (Exception e) {
		}
	}

	/**
	 * Saves scheduling configurations in the persistent storage
	 */
	public void save() throws IOException, ParseException {
		Scheduler scheduler = (Scheduler) Context.getInstance().getBean("Scheduler");
		TaskTrigger trigger = (TaskTrigger) Context.getInstance().getBean(taskName + "Trigger");
		String expression = getCronExpression();

		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		config.setProperty("schedule.cron." + taskName, expression);
		config.setProperty("schedule.enabled." + taskName, enabled ? "true" : "false");
		config.setProperty("schedule.length." + taskName, Long.toString(maxLength));
		config.setProperty("schedule.mode." + taskName, getMode());
		config.setProperty("schedule.delay." + taskName, Long.toString(delay));
		config.setProperty("schedule.interval." + taskName, Long.toString(interval));
		config.setProperty("schedule.cpuidle." + taskName, Integer.toString(minCpuIdle));
		config.write();

		trigger.reload();

		if ("simple".equals(getMode()))
			trigger.setStartTime(new Date(new Date().getTime() + trigger.getRepeatInterval()));

		try {
			// Reschedule the job
			scheduler.deleteJob(taskName + "Job", "DEFAULT");
			scheduler.scheduleJob(trigger.getJobDetail(), trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}

		load();
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

	/**
	 * Checks if the CPU idle percentage is greater than the minimum required
	 */
	boolean isCpuIdle() {
		if (minCpuIdle <= 0)
			return true;
		else
			return (CpuInfo.getCpuIdle() * 100) >= minCpuIdle;
	}

	@Override
	public String toString() {
		return getCronExpression();
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public long getDelay() {
		return delay;
	}

	public long getDelaySeconds() {
		return delay / 1000;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setDelaySeconds(long delay) {
		this.delay = delay * 1000;
	}

	public long getInterval() {
		return interval;
	}

	public long getIntervalSeconds() {
		return interval / 1000;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setIntervalSeconds(long interval) {
		this.interval = interval * 1000;
	}

	public int getMinCpuIdle() {
		return minCpuIdle;
	}

	public void setMinCpuIdle(int minCpuIdle) {
		this.minCpuIdle = minCpuIdle;
	}
}