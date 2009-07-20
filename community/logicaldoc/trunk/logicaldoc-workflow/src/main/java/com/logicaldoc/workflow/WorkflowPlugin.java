package com.logicaldoc.workflow;

import java.io.File;

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
			
			MenuDAO menuDAO = (MenuDAO)ctx.getBean("MenuDAO");
			Menu menu =  new Menu();
			menu.setDeleted(0);
			
			MenuGroup mg = new MenuGroup();
			mg.setGroupId(1);
			
			menu.setText("Workflow");
			menu.setSize(0);
			menu.setParentId(2);
			menu.setSort(4);
			menu.setIcon("tags.png");
			menu.setType(1);
			menu.setRef("workflow/manage-workflowtemplates");
			
			long groups[] = {1};
			
			menu.setMenuGroup(groups);
			menuDAO.store(menu);
			
		}

	}

	protected void install() throws Exception {

		Configuration installConfiguration = new Configuration();
		Context.addListener(new InstallationEvent());
			
		WebConfigurator config = new WebConfigurator();

		//TODO: Its the "best" way to adding a faces-file?
		config.addContextParam("javax.faces.CONFIG_FILES", "/WEB-INF/faces-config-workflow.xml", "", WebConfigurator.INIT_PARAM.PARAM_APPEND);
		config.writeXMLDoc();
		
		super.install();

		String webappDir = resolvePath("webapp");
		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));
		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
		FileUtils.copyDirectory(src, dest);
	}
	
	@Override
	protected void start() throws Exception {
		//Debug issues
		Log4jConfigurer.initLogging("classpath:log4j.xml");		
	}
}