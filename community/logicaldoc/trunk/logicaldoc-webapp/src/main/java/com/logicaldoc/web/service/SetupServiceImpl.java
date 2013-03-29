package com.logicaldoc.web.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.java.plugin.registry.PluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Log4jConfigurer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.dbinit.PluginDbInit;
import com.logicaldoc.core.searchengine.SearchEngine;
import com.logicaldoc.gui.setup.client.SetupInfo;
import com.logicaldoc.gui.setup.client.services.SetupService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Implements the
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SetupServiceImpl extends RemoteServiceServlet implements SetupService {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(SetupServiceImpl.class);

	@Override
	public void setup(SetupInfo data) {
		File repoFolder = new File(data.getRepositoryFolder());
		log.warn("Initialize system using repository " + repoFolder);
		try {
			makeWorkingDir(repoFolder);
			createDB(data);
			writeSmtpConfig(data);
			writeRegConfig(data);

			// Setup the correct logs folder
			try {
				ContextProperties pbean = new ContextProperties();
				LoggingConfigurator lconf = new LoggingConfigurator();
				lconf.setLogsRoot(pbean.getProperty("conf.logdir"));
				lconf.write();
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
				throw new RuntimeException(t.getMessage(), t);
			}

			// Reload the application context in order to reconnect DAOs to the
			// database
			Context.refresh();

		} catch (Throwable caught) {
			caught.printStackTrace();
			log.error(caught.getMessage(), caught);
			throw new RuntimeException(caught.getMessage(), caught);
		}
	}

	public void writeSmtpConfig(SetupInfo data) throws Exception {
		try {
			ContextProperties pbean = new ContextProperties();
			pbean.setProperty("smtp.host", data.getSmtpHost() != null ? data.getSmtpHost() : "");
			pbean.getProperty("smtp.port", data.getSmtpPort() != null ? Integer.toString(data.getSmtpPort()) : "");
			pbean.setProperty("smtp.username", data.getSmtpUsername() != null ? data.getSmtpUsername() : "");
			pbean.setProperty("smtp.password", data.getSmtpPassword() != null ? data.getSmtpPassword() : "");
			pbean.setProperty("smtp.sender", data.getSmtpSender() != null ? data.getSmtpSender() : "");
			pbean.setProperty("smtp.authEncripted", Boolean.toString(data.isSmtpSecureAuth()));
			pbean.setProperty("smtp.connectionSecurity",
					data.getSmtpConnectionSecurity() != null ? data.getSmtpConnectionSecurity() : "0");
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
			e.printStackTrace();
			log.error("Exception writing context file: " + e.getMessage(), e);
			throw e;
		}
	}

	private void writeDBConfig(SetupInfo data) throws Exception {
		try {
			ContextProperties pbean = new ContextProperties();
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
			e.printStackTrace();
			log.error("Exception writing db config on context file: " + e.getMessage(), e);
			throw e;
		}
	}

	private void writeRegConfig(SetupInfo data) throws Exception {
		try {
			ContextProperties pbean = new ContextProperties();
			pbean.setProperty("reg.name", data.getRegName() != null ? data.getRegName() : "");
			pbean.setProperty("reg.website", data.getRegWebsite() != null ? data.getRegWebsite() : "");
			pbean.setProperty("reg.organization", data.getRegOrganization() != null ? data.getRegOrganization() : "");
			pbean.setProperty("reg.email", data.getRegEmail() != null ? data.getRegEmail() : "");
			pbean.write();
			log.info("configuration data written successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception writing registration config on context file: " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Reloads the application context
	 */
	private void reloadContext() {
		// Reload the application context in order to obtain the new value
		Context.refresh();

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String path = conf.getPropertyWithSubstitutions("index.dir");

		if (!path.endsWith(File.pathSeparator)) {
			path += "/";
		}

		SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
		indexer.close();
		indexer.init();

		// Initialize plugins filesystem
		Collection<PluginDescriptor> descriptors = PluginRegistry.getInstance().getPlugins();
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
		String docDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/docs/");
		FileUtils.forceMkdir(new File(docDir));
		String indexDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/index/");
		FileUtils.forceMkdir(new File(indexDir));
		String userDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/users/");
		FileUtils.forceMkdir(new File(userDir));
		String pluginDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/plugins/");
		FileUtils.forceMkdir(new File(pluginDir));
		String importDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/impex/in/");
		FileUtils.forceMkdir(new File(importDir));
		String exportDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/impex/out/");
		FileUtils.forceMkdir(new File(exportDir));
		String logDir = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/logs/");
		FileUtils.forceMkdir(new File(logDir));
		String dbDirectory = FilenameUtils.separatorsToSystem(repoFolder.getPath() + "/db/");

		ContextProperties pbean = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		pbean.setProperty("store.1.dir", docDir);
		pbean.setProperty("store.write", "1");
		pbean.setProperty("index.dir", indexDir);
		pbean.setProperty("conf.userdir", userDir);
		pbean.setProperty("conf.plugindir", pluginDir);
		pbean.setProperty("conf.importdir", importDir);
		pbean.setProperty("conf.exportdir", exportDir);
		pbean.setProperty("conf.logdir", logDir);
		pbean.setProperty("conf.dbdir", dbDirectory);
		pbean.write();

		// Refresh the current logging location
		try {
			String log4jPath = URLDecoder.decode(this.getClass().getResource("/log.xml").getPath(), "UTF-8");
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

		// Try to create the database schema (in Oracle is not possible and in
		// HSQLDB is not necessary)
		if (!info.getDbEngine().toLowerCase().contains("oracle")
				&& !info.getDbEngine().toLowerCase().contains("hsqldb")) {
			String dbName = info.getDbUrl().substring(info.getDbUrl().lastIndexOf('/') + 1);
			String adminjdbcUrl = info.getDbUrl().substring(0, info.getDbUrl().lastIndexOf('/'));
			if (StringUtils.isNotEmpty(dbName))
				try {
					PluginDbInit init = new PluginDbInit();
					init.setDbms(info.getDbEngine());
					init.setDriver(info.getDbDriver());
					init.setUrl(adminjdbcUrl);
					init.setUsername(info.getDbUsername());
					init.setPassword(info.getDbPassword());

					if (init.testConnection())
						init.executeSql("create database " + dbName);
				} catch (Throwable t) {
					log.warn("Unable to create the database schema, perhaps it already exists");
				}
		}

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