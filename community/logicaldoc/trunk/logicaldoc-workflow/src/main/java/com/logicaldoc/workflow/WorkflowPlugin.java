package com.logicaldoc.workflow;

import org.hibernate.cfg.Configuration;
import org.java.plugin.PluginManager;
import org.jbpm.JbpmConfiguration;
import org.springmodules.workflow.jbpm31.LocalJbpmConfigurationFactoryBean;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.event.SystemEvent;
import com.logicaldoc.util.event.SystemEventStatus;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

@SuppressWarnings("unused")
public class WorkflowPlugin extends LogicalDOCPlugin {

	private class InstallationEvent extends SystemEvent {

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

			Context ctx = Context.getInstance();
			
			JbpmConfiguration jbpmInstallConfig = (JbpmConfiguration) ctx
					.getBean("jbpmConfiguration");
			
			jbpmInstallConfig.dropSchema();

			jbpmInstallConfig.createSchema();
			

		}

	}

	protected void install() throws Exception {

		Configuration installConfiguration = new Configuration();
		Context.addListener(new InstallationEvent());
		
		WebConfigurator config = new WebConfigurator();
		config.addServlet("Workflow Servlet",
				"com.logicaldoc.workflow.TestServlet", 4);
		config.writeXMLDoc();
	
		config.addServletMapping("Workflow Servlet", "/workflow/*");
		config.writeXMLDoc();
	}
}
