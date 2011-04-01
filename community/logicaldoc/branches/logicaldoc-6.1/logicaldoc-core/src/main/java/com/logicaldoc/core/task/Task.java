package com.logicaldoc.core.task;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

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

	public final static int STATUS_STOPPING = 2;

	private int status = STATUS_IDLE;

	protected long size = 0;

	private long progress = 0;

	private TaskScheduling scheduling;

	private String name;

	// When becomes true, the processing must be terminated asap but gracefully
	// and leaving the system in a consistent state
	protected boolean interruptRequested = false;

	private List<TaskListener> taskListeners = Collections.synchronizedList(new ArrayList<TaskListener>());

	protected Throwable lastRunError = null;

	protected ContextProperties config;

	protected EMailSender sender = null;

	protected UserDAO userDao = null;

	protected boolean sendActivityReport = false;

	private String reportRecipients = null;

	public Task(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private void setStatus(int status) {
		if (status != STATUS_IDLE && status != STATUS_RUNNING && status != STATUS_STOPPING)
			throw new InvalidParameterException("Invalid status  value");
		boolean needNotification = this.status != status;
		this.status = status;
		if (needNotification)
			for (TaskListener listener : taskListeners)
				listener.statusChanged(status);

	}

	/**
	 * Increments the progress by one and performs a GC
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
		System.gc();
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
		lastRunError = null;
		getScheduling().setPreviousFireTime(new Date());

		try {
			runTask();
		} catch (Throwable t) {
			log.error("Error caught " + t.getMessage(), t);
			log.error("The task is stopped");
			lastRunError = t;
		} finally {
			setStatus(STATUS_IDLE);
			interruptRequested = false;
			saveWork();
			log.info("Task " + getName() + " finished");
			if (isSendActivityReport() && StringUtils.isNotEmpty(getReportRecipients()))
				notifyReport();
		}
	}

	public void interrupt() {
		interruptRequested = true;
		setStatus(STATUS_STOPPING);
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

	public void notifyReport() {
		StringTokenizer st = new StringTokenizer(getReportRecipients(), ", ;", false);

		// Iterate over tokens loading the user to be notified
		while (st.hasMoreTokens()) {
			String userId = st.nextToken();
			User recipient = userDao.findById(Long.parseLong(userId));
			if (recipient == null || StringUtils.isEmpty(recipient.getEmail()))
				continue;

			EMail email = new EMail();
			String taskname = I18N.message("task.name." + name, recipient.getLocale());
			email.setSubject(taskname);

			// Prepare the mail recipient
			Set<Recipient> rec = new HashSet<Recipient>();
			Recipient r = new Recipient();
			r.setAddress(recipient.getEmail());
			r.setType(Recipient.TYPE_EMAIL);
			r.setMode(Recipient.MODE_EMAIL_TO);
			rec.add(r);
			email.setRecipients(rec);

			// Prepare the mail body
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StringBuffer body = new StringBuffer();
			body.append(taskname);
			body.append("\n");
			body.append(I18N.message("startedon", recipient.getLocale()) + ": ");
			body.append(df.format(scheduling.getPreviousFireTime()));
			body.append("\n");
			body.append(I18N.message("finishedon", recipient.getLocale()) + ": ");
			body.append(df.format(new Date()));
			body.append("\n-----------------------------------\n");
			if (lastRunError != null) {
				body.append(I18N.message("error", recipient.getLocale()) + ": ");
				body.append(lastRunError.getMessage());
				body.append("\n-----------------------------------\n");
			}

			String report = prepareReport(recipient.getLocale());
			if (StringUtils.isNotEmpty(report))
				body.append(prepareReport(recipient.getLocale()));
			email.setMessageText(body.toString());

			// Send the email
			try {
				sender.send(email);
				log.info("Report sent to: " + recipient.getEmail());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Implementations may compose a locale specific report.
	 */
	protected String prepareReport(Locale locale) {
		return null;
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

	public String getReportRecipients() {
		return reportRecipients;
	}

	public ContextProperties getConfig() {
		return config;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
		sendActivityReport = "true".equals(config.getProperty("task.sendreport." + name));
		reportRecipients = config.getProperty("task.recipients." + name);
	}

	public void setSender(EMailSender sender) {
		this.sender = sender;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	/**
	 * Saves the task configuration
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	public void save() throws IOException, ParseException {
		getScheduling().save();
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		config.setProperty("task.recipients." + name, getReportRecipients());
		config.setProperty("task.sendreport." + name, isSendActivityReport() ? "true" : "false");
		config.write();
	}

	public boolean isSendActivityReport() {
		return sendActivityReport;
	}

	public void setSendActivityReport(boolean sendActivityReport) {
		this.sendActivityReport = sendActivityReport;
	}

	public void setReportRecipients(String reportRecipients) {
		this.reportRecipients = reportRecipients;
	}
}