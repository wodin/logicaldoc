package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.task.TaskListener;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Bean used for Tasks listing and management
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5.0
 */
public class TasksRecordsManager implements Renderable, DisposableBean, TaskListener {
	protected static Log log = LogFactory.getLog(TasksRecordsManager.class);

	private String selectedPanel = "list";

	// render manager for the application, uses session id for on demand
	// render group.
	private RenderManager renderManager;

	private PersistentFacesState persistentFacesState;

	private String sessionId;

	private String appender;

	public TasksRecordsManager() {
		persistentFacesState = PersistentFacesState.getInstance();
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String selectedPanel) {
		this.selectedPanel = selectedPanel;
	}

	/**
	 * Gets the collection of tasks registered in the system
	 */
	public Collection<TaskRecord> getTasks() {
		TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
		Collection<Task> tasks = manager.getTasks();
		Collection<TaskRecord> records = new ArrayList<TaskRecord>();
		for (Task task : tasks) {
			records.add(new TaskRecord(task));
			task.addTaskListener(this);
		}
		return records;
	}

	/**
	 * Sets the application render manager reference and creates a new on demand
	 * render for this session id.
	 * 
	 * @param renderManager RenderManager reference for this application.
	 *        Usually called via the faces-config.xml using chaining.
	 */
	public void setRenderManager(RenderManager renderManager) {
		this.renderManager = renderManager;

		// Casting to HttpSession ruins it for portlets, in this case we only
		// need a unique reference so we use the object hash
		sessionId = FacesContext.getCurrentInstance().getExternalContext().getSession(false).toString();
		renderManager.getOnDemandRenderer(sessionId).add(this);

		// new Thread() {
		// @Override
		// public void run() {
		// while (true) {
		// try {
		// sleep(1000);
		// } catch (InterruptedException e) {
		// }
		// getRenderManager().getOnDemandRenderer(sessionId).requestRender();
		// System.out.println("**** refresh");
		// }
		// }
		// }.start();
	}

	@Override
	public PersistentFacesState getState() {
		return persistentFacesState;
	}

	public String getAppender() {
		return appender;
	}

	@Override
	public void renderingException(RenderingException renderingException) {
		if (log.isTraceEnabled() && renderingException instanceof TransientRenderingException) {
			log.trace("TaskRecord Transient Rendering exception:", renderingException);
		} else if (renderingException instanceof FatalRenderingException) {
			if (log.isTraceEnabled()) {
				log.trace("TaskRecord Fatal rendering exception: ", renderingException);
			}
			renderManager.getOnDemandRenderer(sessionId).remove(this);
			renderManager.getOnDemandRenderer(sessionId).dispose();
		}
	}

	/**
	 * Dispose callback called due to a view closing or session
	 * invalidation/timeout
	 */
	public void dispose() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("TaskRecord dispose OnDemandRenderer for session: " + sessionId);
		}
		if (renderManager != null) {
			renderManager.getOnDemandRenderer(sessionId).remove(this);
			renderManager.getOnDemandRenderer(sessionId).dispose();
			TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
			Collection<Task> tasks = manager.getTasks();
			for (Task task : tasks) {
				task.removeTaskListener(this);
			}
		}
	}

	/**
	 * Launches the task's execution in another thread
	 */
	public String start() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		final TaskRecord task = (TaskRecord) map.get("task");
		Thread thread = new Thread(task.getTask());
		thread.start();
		return "";
	}

	/**
	 * Stops the task's execution
	 */
	public String stop() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		TaskRecord task = (TaskRecord) map.get("task");
		task.getTask().interrupt();
		return "";
	}

	public String editScheduling() {
		selectedPanel = "scheduling";
		// The form is implemented through a TaskRecord
		TaskRecord form = ((TaskRecord) FacesUtil.accessBeanFromFacesContext("taskForm", FacesContext
				.getCurrentInstance()));
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		TaskRecord task = (TaskRecord) map.get("task");
		form.setTask(task.getTask());
		return "";
	}

	public String list() {
		selectedPanel = "list";
		return "";
	}

	public String log() {
		selectedPanel = "log";
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		if (map.containsKey("task")) {
			TaskRecord task = (TaskRecord) map.get("task");
			appender = task.getName() + "_WEB";
		}
		return "";
	}

	@Override
	public void progressChanged(long progress) {
		synchronized (this) {
			renderManager.getOnDemandRenderer(sessionId).requestRender();
		}
	}

	@Override
	public void statusChanged(int status) {
		synchronized (this) {
			renderManager.getOnDemandRenderer(sessionId).requestRender();
		}
	}

	public RenderManager getRenderManager() {
		return renderManager;
	}
}