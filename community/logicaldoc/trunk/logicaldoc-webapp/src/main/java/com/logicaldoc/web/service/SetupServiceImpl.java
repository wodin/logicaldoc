package com.logicaldoc.web.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.PluginDescriptor;
import org.springframework.util.Log4jConfigurer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.SystemProperty;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.dbinit.PluginDbInit;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.gui.setup.client.SetupInfo;
import com.logicaldoc.gui.setup.client.services.SetupService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.smartgwt.client.util.SC;

/**
 * Implements the
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SetupServiceImpl extends RemoteServiceServlet implements SetupService {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(SetupServiceImpl.class);

	@Override
	public void setup(SetupInfo data) {
		File repoFolder = new File(data.getRepositoryFolder());
		log.warn("Initialize system using repository " + repoFolder);
		try {
			makeWorkingDir(repoFolder);
			createDB(data);
			writeSmtpConfig(data);

			// Reload the application context in order to reconnect DAOs to the
			// database
			Context.refresh();
		} catch (Throwable caught) {
			SC.warn("Server error setup: " + caught.getMessage());
			log.error(caught.getMessage(), caught);
			throw new RuntimeException(caught.getMessage(), caught);
		}
	}

	public void writeSmtpConfig(SetupInfo data) throws Exception {
		try {
			PropertiesBean pbean = new PropertiesBean();
			pbean.setProperty("smtp.host", data.getSmtpHost() != null ? data.getSmtpHost() : "");
			pbean.getProperty("smtp.port", data.getSmtpPort() != null ? Integer.toString(data.getSmtpPort()) : "");
			pbean.setProperty("smtp.username", data.getSmtpUsername() != null ? data.getSmtpUsername() : "");
			pbean.setProperty("smtp.password", data.getSmtpPassword() != null ? data.getSmtpPassword() : "");
			pbean.setProperty("smtp.sender", data.getSmtpSender() != null ? data.getSmtpSender() : "");
			pbean.setProperty("smtp.authEncripted", Boolean.toString(data.isSmtpSecureAuth()));
			pbean.setProperty("smtp.connectionSecurity", data.getSmtpConnectionSecurity() != null ? data
					.getSmtpConnectionSecurity() : "0");
			pbean.write();

			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			if (data.getSmtpHost() != null)
				sender.setHost(data.getSmtpHost());
			if (data.getSmtpPort() != null)
				sender.setPort(data.getSmtpPort());
			if (data.getSmtpUsername() != null)
				sender.setUsername(data.getSmtpUsername());
			if (data.getSmtpPassword() != null)
				sender.setPassword(data.getSmtpPassword());
			if (data.getSmtpSender() != null)
				sender.setSender(data.getSmtpSender());
			sender.setAuthEncripted(data.isSmtpSecureAuth());
			if (data.getSmtpConnectionSecurity() != null)
				sender.setConnectionSecurity(Integer.parseInt(data.getSmtpConnectionSecurity()));

			log.info("SMTP configuration data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing context file: " + e.getMessage(), e);
			throw e;
		}
	}

	private void writeDBConfig(SetupInfo data) throws Exception {
		try {
			PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
			pbean.setProperty("jdbc.driver", data.getDbDriver() != null ? data.getDbDriver() : "");
			pbean.setProperty("jdbc.url", data.getDbUrl() != null ? data.getDbUrl() : "");
			pbean.setProperty("jdbc.username", data.getDbUsername() != null ? data.getDbUsername() : "");
			pbean.setProperty("jdbc.password", data.getDbPassword() != null ? data.getDbPassword() : "");
			pbean.setProperty("jdbc.dbms", data.getDbEngine().toLowerCase());

			if (StringUtils.isNotEmpty(data.getDbValidationQuery())) {
				pbean.setProperty("jdbc.validationQuery", data.getDbValidationQuery());
			} else {
				pbean.setProperty("jdbc.validationQuery", "");
			}

			pbean.setProperty("hibernate.dialect", data.getDbDialect());

			pbean.write();
			log.info("configuration data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing db config on context file: " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Reloads the application context
	 */
	private void reloadContext() {
		// Reload the application context in order to obtain the new value
		Context.refresh();

		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		String path = conf.getPropertyWithSubstitutions("conf.indexdir");

		if (!path.endsWith(File.pathSeparator)) {
			path += "/";
		}

		Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
		indexer.createIndexes();

		// Initialize plugins filesystem
		Collection<PluginDescriptor> descriptors = com.logicaldoc.util.PluginRegistry.getInstance().getPlugins();
		for (PluginDescriptor descriptor : descriptors) {
			try {
				File file = new File(conf.getPropertyWithSubstitutions("conf.plugindir"), descriptor.getId());
				file.mkdirs();
				file.mkdir();
				file = new File(file, "plugin.properties");
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void makeWorkingDir(File repoFolder) throws IOException {
		repoFolder.mkdirs();
		repoFolder.mkdir();

		File dbDir = new File(repoFolder, "db");
		FileUtils.forceMkdir(dbDir);

		// build phisically the working directory
		// and change settings config
		String docDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/data/docs/");
		FileUtils.forceMkdir(new File(docDir));
		String indexDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/data/index/");
		FileUtils.forceMkdir(new File(indexDir));
		String userDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/data/users/");
		FileUtils.forceMkdir(new File(userDir));
		String pluginDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/data/plugins/");
		FileUtils.forceMkdir(new File(pluginDir));
		String importDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/impex/in/");
		FileUtils.forceMkdir(new File(importDir));
		String exportDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/impex/out/");
		FileUtils.forceMkdir(new File(exportDir));

		PropertiesBean pbean = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		pbean.setProperty("conf.docdir", docDir);
		pbean.setProperty("conf.indexdir", indexDir);
		pbean.setProperty("conf.userdir", userDir);
		pbean.setProperty("conf.plugindir", pluginDir);
		pbean.setProperty("conf.importdir", importDir);
		pbean.setProperty("conf.exportdir", exportDir);
		pbean.write();

		// Save the LOGICALDOC_REPOSITORY property
		String logicaldocHome = FilenameUtils.separatorsToSystem(repoFolder.getPath());
		SystemProperty.setProperty(SystemProperty.LOGICALDOC_REPOSITORY, logicaldocHome);

		// Refresh the current logging location
		try {
			String rootPath = SystemProperty.getProperty(SystemProperty.LOGICALDOC_APP_ROOTDIR);
			String log4jPath = rootPath + "/WEB-INF/classes/ldoc-log4j.xml";
			System.err.println("log4jPath = " + log4jPath);
			Log4jConfigurer.initLogging(log4jPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		reloadContext();
	}

	public void createDB(SetupInfo info) throws Exception {
		// write the configuration for the db on the general context
		writeDBConfig(info);

		PluginDbInit init = new PluginDbInit();
		init.setDbms(info.getDbEngine());
		init.setDriver(info.getDbDriver());
		init.setUrl(info.getDbUrl());
		init.setUsername(info.getDbUsername());
		init.setPassword(info.getDbPassword());

		if (init.testConnection()) {
			// connection success
			init.init();

			// if a default language was specified, set it for all users
			if (StringUtils.isNotEmpty(info.getLanguage())) {
				init.executeSql("update ld_user set ld_language='" + info.getLanguage() + "';");
			}
		} else {
			// connection failure
			log.debug("connection failure");
			throw new RuntimeException("Database Connection failure.");
		}
	}
}