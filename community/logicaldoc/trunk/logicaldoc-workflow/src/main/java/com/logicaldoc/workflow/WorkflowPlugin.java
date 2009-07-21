package com.logicaldoc.workflow;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hibernate.cfg.Configuration;
import org.java.plugin.PluginManager;
import org.jbpm.JbpmConfiguration;
import org.springframework.util.Log4jConfigurer;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.FacesConfigurator;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.event.SystemEvent;
import com.logicaldoc.util.event.SystemEventStatus;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;
import com.logicaldoc.workflow.editor.WorkflowTemplateLoader;

@SuppressWarnings("unused")
public class WorkflowPlugin extends LogicalDOCPlugin {

	private class InstallationEvent extends SystemEvent {
		
		private String pluginDirectory;
		
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
			
			//Create a folder for the templates
			File f = new File(this.pluginDirectory + "/templates/");
			
			if(f.exists() == false){
				
				try {
					f.createNewFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
			}
			
			Context ctx = Context.getInstance();
			JbpmConfiguration jbpmInstallConfig = (JbpmConfiguration) ctx
					.getBean("jbpmConfiguration");			
			jbpmInstallConfig.dropSchema();
			jbpmInstallConfig.createSchema();
		}
		
		public void setPluginDirectory(String pluginDirectory) {
			this.pluginDirectory = pluginDirectory;
		}
	}
	
	private class VariableEvent extends SystemEvent {

		private String pluginDirectory;
		
		private PluginManager manager;

		public VariableEvent(PluginManager manager) {
			this();
			this.manager = manager;
		}

		public VariableEvent() {
			super(SystemEventStatus.BEANS_AVAILABLE);
		}
		
		public void setPluginDirectory(String pluginDirectory) {
			this.pluginDirectory = pluginDirectory;
		}

		@Override
		public void processEvent() {
			Context ctx = Context.getInstance();
			WorkflowTemplateLoader workflowTemplateLoader = (WorkflowTemplateLoader) ctx
					.getBean("WorkflowTemplateLoader");			
		
			workflowTemplateLoader.setPluginDirectory(this.pluginDirectory);
		}
	}

	protected void install() throws Exception {
		
		
		InstallationEvent installationEvent = new InstallationEvent();
		installationEvent.setPluginDirectory(getDataDirectory().getAbsolutePath());
		Context.addListener(installationEvent);
		
		super.install();

		String webappDir = resolvePath("webapp");
		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));
		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
		FileUtils.copyDirectory(src, dest);
	}
	
	@Override
	protected void start() throws Exception {
		
		VariableEvent variableEvent = new VariableEvent();
		variableEvent.setPluginDirectory(getDataDirectory().getAbsolutePath() + "/templates/");
		
		Context.addListener(variableEvent);
	}
}