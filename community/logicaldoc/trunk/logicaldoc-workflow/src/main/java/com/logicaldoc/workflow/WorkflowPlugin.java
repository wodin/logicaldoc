package com.logicaldoc.workflow;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.java.plugin.PluginManager;
import org.jbpm.JbpmConfiguration;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.event.SystemEvent;
import com.logicaldoc.util.event.SystemEventStatus;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;
import com.logicaldoc.workflow.editor.WorkflowTemplateLoader;

@SuppressWarnings("unused")
public class WorkflowPlugin extends LogicalDOCPlugin {

	private class InstallationEvent extends SystemEvent {

		private File templatesDirectory;

		private PluginManager manager;

		public InstallationEvent(PluginManager manager) {
			this();
			this.manager = manager;
		}

		public InstallationEvent() {
			super(SystemEventStatus.BEANS_AVAILABLE);
		}

		@Override
		public void processEvent() {
			templatesDirectory.mkdirs();
			templatesDirectory.mkdir();

			Context ctx = Context.getInstance();
			WorkflowTemplateLoader workflowTemplateLoader = (WorkflowTemplateLoader) ctx
					.getBean(WorkflowTemplateLoader.class);
			workflowTemplateLoader.setTemplatesDirectory(templatesDirectory);

			// Create JBPM database schema
			JbpmConfiguration jbpmInstallConfig = (JbpmConfiguration) ctx.getBean("jbpmConfiguration");
			jbpmInstallConfig.dropSchema();
			jbpmInstallConfig.createSchema();
		}

		public File getTemplatesDirectory() {
			return templatesDirectory;
		}

		public void setTemplatesDirectory(File templatesDirectory) {
			this.templatesDirectory = templatesDirectory;
		}

	}

	private class VariableEvent extends SystemEvent {

		private File templatesDirectory;

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
			WorkflowTemplateLoader workflowTemplateLoader = (WorkflowTemplateLoader) ctx
					.getBean(WorkflowTemplateLoader.class);
			workflowTemplateLoader.setTemplatesDirectory(templatesDirectory);
		}

		public void setTemplatesDirectory(File templatesDirectory) {
			this.templatesDirectory = templatesDirectory;
		}
	}

	protected void install() throws Exception {
		super.install();

		InstallationEvent installationEvent = new InstallationEvent();
		installationEvent.setTemplatesDirectory(resolveDataPath("templates"));
		Context.addListener(installationEvent);

		String webappDir = resolvePath("webapp");
		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));
		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
		FileUtils.copyDirectory(src, dest);
	}

	@Override
	protected void start() throws Exception {
		VariableEvent variableEvent = new VariableEvent();
		variableEvent.setTemplatesDirectory(resolveDataPath("templates"));
		Context.addListener(variableEvent);
	}
}