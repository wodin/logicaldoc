package com.logicaldoc.workflow;

import java.io.File;

import org.java.plugin.PluginManager;
import org.jbpm.JbpmConfiguration;
import org.jbpm.job.executor.JobExecutor;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.WebConfigurator;
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

	private static final String SERVICE_NAME = "WorkflowService";

	private static final String SERVLET_DATA = "WorkflowsData";

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

		File dest = new File(getPluginPath());
		dest = dest.getParentFile().getParentFile();

		WebConfigurator config = new WebConfigurator(dest.getPath() + "/web.xml");
		config.addServlet(SERVICE_NAME, "com.logicaldoc.workflow.service.WorkflowServiceImpl", 4);
		config.writeXMLDoc();
		config.addServletMapping(SERVICE_NAME, "/frontend/workflow");
		config.writeXMLDoc();

		config.addServlet(SERVLET_DATA, "com.logicaldoc.workflow.data.WorkflowsDataServlet", 4);
		config.writeXMLDoc();
		config.addServletMapping(SERVLET_DATA, "/data/workflows.xml");
		config.writeXMLDoc();

		// Add some workflow CE defaults
		PropertiesBean pbean = new PropertiesBean();
		pbean.setProperty("workflow.assignment.handler", "com.logicaldoc.workflow.action.DefaultAssignmentHandler");
		pbean.setProperty("workflow.remind.handler", "com.logicaldoc.workflow.action.DefaultRemindHandler");
		pbean.setProperty("history.workflow.ttl", "90");
		pbean.write();

		setRestartRequired();
	}

	@Override
	protected void start() throws Exception {
		VariableEvent variableEvent = new VariableEvent();
		Context.addListener(variableEvent);
	}
}