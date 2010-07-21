package com.logicaldoc.workflow;

import org.java.plugin.PluginManager;
import org.jbpm.JbpmConfiguration;
import org.jbpm.job.executor.JobExecutor;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.event.SystemEvent;
import com.logicaldoc.util.event.SystemEventStatus;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Entry-point for the Workflow plug-in
 * 
 * @author Sebastian Wenzky
 * @since 5.0
 */
@SuppressWarnings("unused")
public class WorkflowPlugin extends LogicalDOCPlugin {

	private class VariableEvent extends SystemEvent {

		private PluginManager manager;

		public VariableEvent(PluginManager manager) {
			this();
			this.manager = manager;
		}

		public VariableEvent() {
			super(SystemEventStatus.BEANS_AVAILABLE);
		}

		@Override
		public void processEvent() {
			Context ctx = Context.getInstance();
			JbpmConfiguration jbpmConfiguration = (JbpmConfiguration) ctx.getBean("jbpmConfiguration");

			JobExecutor jobExecutor = jbpmConfiguration.getJobExecutor();

			if (jobExecutor.isStarted() == false) {
				log.info("Starting jBPM Timer...");
				jobExecutor.setJbpmConfiguration(jbpmConfiguration);
				jobExecutor.start();
			}
		}
	}

	protected void install() throws Exception {
		super.install();

		// Add some workflow CE defaults
		PropertiesBean pbean = new PropertiesBean();
		pbean.setProperty("workflow.assignment.handler", "com.logicaldoc.workflow.action.DefaultAssignmentHandler");
		pbean.setProperty("workflow.remind.handler", "com.logicaldoc.workflow.action.DefaultRemindHandler");
		pbean.setProperty("history.workflow.ttl", "90");
		pbean.write();
	}

	@Override
	protected void start() throws Exception {
		VariableEvent variableEvent = new VariableEvent();
		Context.addListener(variableEvent);
	}
}