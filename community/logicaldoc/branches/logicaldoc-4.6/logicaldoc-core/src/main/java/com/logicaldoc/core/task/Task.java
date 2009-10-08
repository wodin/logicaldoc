package com.logicaldoc.core.task;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A task is a long running sequence of operations
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5.0
 */
public abstract class Task implements Runnable {
	protected Log log = LogFactory.getLog(Task.class);

	public final static int STATUS_IDLE = 0;

	public final static int STATUS_RUNNING = 1;

	private int status = STATUS_IDLE;

	protected long size = 0;

	private long progress = 0;

	private TaskScheduling scheduling;

	private String name;

	// When becomes true, the processing must be terminated asap but gracefully
	// and leaving the system in a consistent state
	protected boolean interruptRequested = false;

	private List<TaskListener> taskListeners = Collections.synchronizedList(new ArrayList<TaskListener>());

	public Task(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private void setStatus(int status) {
		if (status != STATUS_IDLE && status != STATUS_RUNNING)
			throw new InvalidParameterException("Invalid status  value");
		boolean needNotification = this.status != status;
		this.status = status;
		if (needNotification)
			for (TaskListener listener : taskListeners)
				listener.statusChanged(status);

	}

	/**
	 * Increments the progress by one
	 */
	protected void next() {
		setProgress(progress + 1);
	}

	protected void setProgress(long progress) {
		try {
			if (progress > size || progress < 0)
				return;

			boolean needNotification = this.progress != progress;
			this.progress = progress;
			if (needNotification)
				for (TaskListener listener : taskListeners)
					listener.progressChanged(progress);
		} catch (Throwable t) {
			// Nothing to do
		} finally {
			// Check it time was expired, and request interruption if the case
			if (getScheduling().isExpired())
				interrupt();
		}
	}

	@Override
	public void run() {
		if (!getScheduling().isEnabled()) {
			log.debug("Task " + getName() + " is disabled");
			return;
		}

		if (!getScheduling().isCpuIdle()) {
			log.debug("CPU too busy");
			return;
		}

		if (getStatus() != STATUS_IDLE) {
			log.debug("Task " + getName() + " is already running");
			return;
		}

		log.info("Task " + getName() + " started");
		interruptRequested = false;
		setStatus(STATUS_RUNNING);
		setProgress(0);
		getScheduling().setPreviousFireTime(new Date());

		try {
			runTask();
		} catch (Throwable t) {
			log.error("Error caught " + t.getMessage(), t);
			log.error("The task is stopped");
		} finally {
			setStatus(STATUS_IDLE);
			interruptRequested = false;
			saveWork();
			log.info("Task " + getName() + " finished");
		}
	}

	public void interrupt() {
		interruptRequested = true;
	}

	public boolean isInterrupted() {
		return getStatus() == STATUS_IDLE;
	}

	/**
	 * The the total size of the processing(number of units of work)
	 */
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * The task status(one of STATUS_IDLE or STATUS_RUNNING)
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * The current processing step
	 */
	public long getProgress() {
		return progress;
	}

	/**
	 * The percentage of completion(1-100)
	 */
	public int getCompletionPercentage() {
		if (isIndeterminate()) {
			if (getStatus() == STATUS_IDLE)
				return 0;
			else
				return 1;
		} else {
			if (size == 0)
				return 0;
			return (int) (Math.round(((double) progress / (double) size) * 100));
		}
	}

	/**
	 * Check if the task is currently running
	 */
	public boolean isRunning() {
		return status == STATUS_RUNNING;
	}

	/**
	 * Concrete implementations must insert here the code needed to save the
	 * elaboration state in a persistent storage
	 */
	public void saveWork() {
		// By default do nothing
	}

	/**
	 * Scheduling policies
	 */
	public TaskScheduling getScheduling() {
		if (scheduling == null) {
			scheduling = new TaskScheduling(getName());
			try {
				scheduling.load();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return scheduling;
	}

	public synchronized void addTaskListener(TaskListener listener) {
		if (!taskListeners.contains(listener))
			taskListeners.add(listener);
	}

	public synchronized void removeTaskListener(TaskListener listener) {
		if (taskListeners.contains(listener))
			taskListeners.remove(listener);
	}

	/**
	 * Concrete implementations must override this method implementing their own
	 * processing logic.
	 * 
	 * @throws Exception If something goes wrong this exception is raised
	 */
	abstract protected void runTask() throws Exception;

	/**
	 * Concrete implementations must override this method declaring if the task
	 * is indeterminate. An indeterminate task is not able to compute it's time
	 * length
	 */
	abstract public boolean isIndeterminate();
}