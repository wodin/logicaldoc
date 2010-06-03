package com.logicaldoc.web.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.PluginDescriptor;
import org.springframework.util.Log4jConfigurer;

import com.icesoft.faces.component.paneltabset.PanelTab;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;
import com.icesoft.faces.webapp.http.servlet.ServletExternalContext;
import com.logicaldoc.core.SystemProperty;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.dbinit.PluginDbInit;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.DBMSConfigurator;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.ApplicationInitializer;

/**
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class SetupWizard implements TabChangeListener {
	protected static Log log = LogFactory.getLog(SetupWizard.class);

	private String workingDir;

	private String dbms = "intern";

	private String internDBPath;

	private String selectedDBMS;

	private String defaultLanguage;

	/**
	 * Binding used by example to listen
	 */
	private PanelTabSet tabSet;

	private ConnectionData cdata = new ConnectionData();

	private SmtpData smtpData = new SmtpData();

	private boolean setupSuccess;

	public SetupWizard() {
		String workDir = "${user.home}/logicaldoc";

		// Replacing ${var_name} in elements values with system property value
		String tmp = StrSubstitutor.replaceSystemProperties(workDir);
		this.workingDir = FilenameUtils.separatorsToSystem(tmp);
	}

	public String makeWorkingDir() {
		// build phisically the working directory
		// and change settings config
		try {
			String docDir = FilenameUtils.separatorsToSystem(workingDir + "/data/docs/");
			String indexDir = FilenameUtils.separatorsToSystem(workingDir + "/data/index/");
			String userDir = FilenameUtils.separatorsToSystem(workingDir + "/data/users/");
			String pluginDir = FilenameUtils.separatorsToSystem(workingDir + "/data/plugins/");
			String importDir = FilenameUtils.separatorsToSystem(workingDir + "/impex/in/");
			String exportDir = FilenameUtils.separatorsToSystem(workingDir + "/impex/out/");

			PropertiesBean pbean = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
			pbean.setProperty("conf.docdir", docDir);
			pbean.setProperty("conf.indexdir", indexDir);
			pbean.setProperty("conf.userdir", userDir);
			pbean.setProperty("conf.plugindir", pluginDir);
			pbean.setProperty("conf.importdir", importDir);
			pbean.setProperty("conf.exportdir", exportDir);
			pbean.write();

			// Save the LOGICALDOC_REPOSITORY property
			PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
			String logicaldocHome = FilenameUtils.separatorsToSystem(workingDir);
			conf.setProperty(SystemProperty.LOGICALDOC_REPOSITORY, logicaldocHome);
			SystemProperty.setProperty(SystemProperty.LOGICALDOC_REPOSITORY, logicaldocHome);

			ServletContext servletContext = (ServletContext) ((ServletExternalContext) FacesContext
					.getCurrentInstance().getExternalContext()).getContext();
			Properties boot = ApplicationInitializer.loadBootProperties(servletContext);
			boot.setProperty(SystemProperty.LOGICALDOC_REPOSITORY, logicaldocHome);
			ApplicationInitializer.saveBootProperties(boot, servletContext);

			// Refresh the current logging location
			try {
				String rootPath = SystemProperty.getProperty(SystemProperty.LOGICALDOC_APP_ROOTDIR);
				String log4jPath = rootPath + "/WEB-INF/classes/ldoc-log4j.xml";
				System.err.println("log4jPath = " + log4jPath);
				Log4jConfigurer.initLogging(log4jPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			// Reload the application context in order to obtain the new value
			Context.refresh();

			// Reload the bean for security
			conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");

			String path = conf.getProperty("conf.indexdir");
			System.out.println();

			if (!path.endsWith(File.pathSeparator)) {
				path += "/";
			}

			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			indexer.createIndexes();

			// Initialize plugins filesystem
			Collection<PluginDescriptor> descriptors = com.logicaldoc.util.PluginRegistry.getInstance().getPlugins();
			for (PluginDescriptor descriptor : descriptors) {
				try {
					File file = new File(pluginDir, descriptor.getId());
					file.mkdirs();
					file.mkdir();
					file = new File(file, "plugin.properties");
					file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			next();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	public void loadDBPath() {
		if (dbms.equals("intern")) {
			File logicaldocDbPath = new File(workingDir, "db");
			internDBPath = logicaldocDbPath.getPath();
		}
		next();
	}

	public SmtpData getSmtpData() {
		return smtpData;
	}

	public void setSmtpData(SmtpData smtpData) {
		this.smtpData = smtpData;
	}

	private void writeDBConfig(ConnectionData dbdata) throws Exception {
		try {
			PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
			pbean.setProperty("jdbc.driver", dbdata.getClazz());
			pbean.setProperty("jdbc.url", dbdata.getDburl());
			pbean.setProperty("jdbc.username", dbdata.getUser());
			pbean.setProperty("jdbc.password", dbdata.getPswd());
			pbean.setProperty("jdbc.dbms", dbdata.getDbms().toLowerCase());

			if (StringUtils.isNotEmpty(dbdata.getValidationQuery())) {
				pbean.setProperty("jdbc.validationQuery", dbdata.getValidationQuery());
			} else {
				pbean.setProperty("jdbc.validationQuery", "");
			}

			pbean.setProperty("hibernate.dialect", dbdata.getDialect());

			pbean.write();
			log.info("configuration data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing db config on context file: " + e.getMessage(), e);
			throw e;
		}
	}

	public void createDB() {
		try {
			ConnectionData intdata = null;

			if ("intern".equals(dbms)) {
				if (!internDBPath.endsWith("/") && !internDBPath.endsWith("\\")) {
					internDBPath += "/";
				}

				FileUtils.forceMkdir(new File(internDBPath));

				intdata = new ConnectionData();
				intdata.setClazz("org.hsqldb.jdbcDriver");
				intdata.setDburl("jdbc:hsqldb:" + internDBPath);
				intdata.setUser("sa");
				intdata.setPswd("");
				intdata.setDbms("Hsqldb");
				intdata.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			} else {
				intdata = cdata;
			}

			// write the configuration for the db on the general context
			writeDBConfig(intdata);

			// initialize the db
			initDB(intdata);
			setupSuccess = true;
		} catch (Exception e) {
			setupSuccess = false;
		}

		next();
	}

	private void initDB(ConnectionData dbdata) {
		// Reload the application context in order to reconnect DAOs to the
		// database
		Context.refresh();

		PluginDbInit init = new PluginDbInit();
		init.setDbms(dbdata.getDbms());
		init.setDriver(dbdata.getClazz());
		init.setUrl(dbdata.getDburl());
		init.setUsername(dbdata.getUser());
		init.setPassword(dbdata.getPswd());

		if (init.testConnection()) {
			// connection success
			init.init();

			// if a default language was specified, set it for all users
			if (StringUtils.isNotEmpty(defaultLanguage)) {
				init.executeSql("update ld_user set ld_language='" + defaultLanguage + "';");
			}
		} else {
			// connection failure
			log.debug("connection failure");
			throw new RuntimeException("Database Connection failure.");
		}
	}

	public void next() {
		int currentTab = tabSet.getSelectedIndex();
		currentTab++;

		if (currentTab < 6) {
			tabSet.setSelectedIndex(currentTab);

			// Enable the current panel
			List<UIComponent> panels = tabSet.getChildren();

			for (int i = 0; i < panels.size(); i++) {
				PanelTab panel = (PanelTab) panels.get(i);

				if (i == currentTab) {
					panel.setDisabled(false);
				}
			}
		}
	}

	public void writeSmtpSettings() throws Exception {
		try {
			PropertiesBean pbean = new PropertiesBean();
			pbean.setProperty("smtp.host", smtpData.getHost());
			pbean.setProperty("smtp.port", smtpData.getPort().toString());
			pbean.setProperty("smtp.username", smtpData.getUsername());
			pbean.setProperty("smtp.password", smtpData.getPassword());
			pbean.setProperty("smtp.sender", smtpData.getSender());
			pbean.setProperty("smtp.authEncripted", smtpData.isAuthEncripted() ? "true" : "false");
			pbean.setProperty("smtp.connectionSecurity", Integer.toString(smtpData.getConnectionSecurity()));
			pbean.write();

			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			sender.setHost(smtpData.getHost());
			sender.setPort(smtpData.getPort());
			sender.setUsername(smtpData.getUsername());
			sender.setPassword(smtpData.getPassword());
			sender.setSender(smtpData.getSender());
			sender.setAuthEncripted(smtpData.isAuthEncripted() ? true : false);
			sender.setConnectionSecurity(smtpData.getConnectionSecurity());

			log.info("SMTP configuration data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing context file: " + e.getMessage(), e);
			throw e;
		}
		next();
	}

	public void prev() {
		int currentTab = tabSet.getSelectedIndex();
		currentTab--;

		if (currentTab > 0) {
			tabSet.setSelectedIndex(currentTab);
		}
	}

	/**
	 * Called when the table binding's tab focus changes.
	 * 
	 * @param tabChangeEvent used to set the tab focus.
	 * @throws AbortProcessingException An exception that may be thrown by event
	 *         listeners to terminate the processing of the current event.
	 */
	public void processTabChange(TabChangeEvent tabChangeEvent) throws AbortProcessingException {
		log.info("processTabChange: sss = " + tabChangeEvent.getNewTabIndex());
	}

	/**
	 * Gets the tabbed pane object bound to this bean.
	 * 
	 * @return bound tabbed pane.
	 */
	public PanelTabSet getTabSet() {
		return tabSet;
	}

	/**
	 * Set a tabbed pane object which will be bound to this object
	 * 
	 * @param tabSet new PanelTabSet object.
	 */
	public void setTabSet(PanelTabSet tabSet) {
		this.tabSet = tabSet;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public String getInternDBPath() {
		return internDBPath;
	}

	public void setInternDBPath(String internDBPath) {
		this.internDBPath = internDBPath;
	}

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getSelectedDBMS() {
		return selectedDBMS;
	}

	/**
	 * Value change listener for the country change event. Sets up the cities
	 * listbox according to the country.
	 * 
	 * @param event value change event
	 */
	public void dbmsChanged(ValueChangeEvent event) {
		// get new city value and assign it.
		String newDBMS = (String) event.getNewValue();
		this.selectedDBMS = newDBMS;

		try {
			DBMSConfigurator conf = new DBMSConfigurator();
			String clazz = conf.getAttribute(selectedDBMS, "class");
			String sample = conf.getAttribute(selectedDBMS, "sample");
			String validationQuery = conf.getAttribute(selectedDBMS, "validationQuery");

			cdata.setDbms(selectedDBMS);
			cdata.setClazz(clazz);
			cdata.setDburl(sample);
			cdata.setValidationQuery(validationQuery);
			cdata.clear();
		} catch (RuntimeException e) {
			log.error("Unable to load dbms data :" + e.getMessage(), e);
		}
	}

	public void setSelectedDBMS(String selectedDBMS) {
		this.selectedDBMS = selectedDBMS;
	}

	public ConnectionData getCdata() {
		return cdata;
	}

	public boolean isSetupSuccess() {
		return setupSuccess;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

}