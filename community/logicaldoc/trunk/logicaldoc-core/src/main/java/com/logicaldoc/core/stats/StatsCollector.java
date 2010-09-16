package com.logicaldoc.core.stats;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Collects statistical informations to the stats site
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StatsCollector extends Task {
	public static final String STAT = "stat";

	public final static String NAME = "StatsCollector";

	private UserDAO userDAO;

	private DocumentDAO documentDAO;

	private FolderDAO folderDAO;

	private GroupDAO groupDAO;

	private GenericDAO genericDAO;

	private ContextProperties config;

	public StatsCollector() {
		super(NAME);
		log = LogFactory.getLog(StatsCollector.class);
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start statistics collection");

		/*
		 * Collect identification data
		 */
		String id = config.getProperty("id");
		String userno = config.getProperty("userno");
		String release = config.getProperty("product.release");
		log.debug("Collected identification data");

		/*
		 * Collect environment data
		 */
		String osname = System.getProperty("os.name");
		String osversion = System.getProperty("os.version");
		String osarch = System.getProperty("os.arch");
		String javaversion = System.getProperty("java.version");
		String javavendor = System.getProperty("java.vendor");
		String fileencoding = System.getProperty("file.encoding");
		String userlanguage = System.getProperty("user.language");
		String userregion = System.getProperty("user.region");
		String usertimezone = System.getProperty("user.timezone");
		String javaarch = System.getProperty("sun.arch.data.model");

		log.debug("Collected environment data");

		/*
		 * Collect users data
		 */
		int users = userDAO.count();
		int groups = groupDAO.count();
		log.debug("Collected users data");

		/*
		 * Compute repository statistics
		 */
		long docdir = 0;
		File docDir = new File(config.getPropertyWithSubstitutions("conf.docdir"));
		if (docDir.exists())
			docdir = FileUtils.sizeOfDirectory(docDir);
		saveStatistic("docdir", Long.toString(docdir));

		long userdir = 0;
		File userDir = new File(config.getPropertyWithSubstitutions("conf.userdir"));
		if (userDir.exists())
			userdir = FileUtils.sizeOfDirectory(userDir);
		saveStatistic("userdir", Long.toString(userdir));

		long indexdir = 0;
		File indexDir = new File(config.getPropertyWithSubstitutions("conf.indexdir"));
		if (indexDir.exists())
			indexdir = FileUtils.sizeOfDirectory(indexDir);
		saveStatistic("indexdir", Long.toString(indexdir));

		long importdir = 0;
		File importDir = new File(config.getPropertyWithSubstitutions("conf.importdir"));
		if (importDir.exists())
			importdir = FileUtils.sizeOfDirectory(importDir);
		saveStatistic("importdir", Long.toString(importdir));

		long exportdir = 0;
		File exportDir = new File(config.getPropertyWithSubstitutions("conf.exportdir"));
		if (exportDir.exists())
			exportdir = FileUtils.sizeOfDirectory(exportDir);
		saveStatistic("exportdir", Long.toString(exportdir));

		long plugindir = 0;
		File pluginsDir = new File(config.getPropertyWithSubstitutions("conf.plugindir"));
		if (pluginsDir.exists())
			plugindir = FileUtils.sizeOfDirectory(pluginsDir);
		saveStatistic("plugindir", Long.toString(plugindir));

		long dbdir = 0;
		File dbDir = new File(config.getPropertyWithSubstitutions("conf.dbdir"));
		if (dbDir.exists())
			dbdir = FileUtils.sizeOfDirectory(dbDir);
		saveStatistic("dbdir", Long.toString(dbdir));

		long logdir = 0;
		File logsDir = new File(config.getPropertyWithSubstitutions("conf.logdir"));
		if (logsDir.exists())
			logdir = FileUtils.sizeOfDirectory(logsDir);
		saveStatistic("logdir", Long.toString(logdir));

		log.debug("Saved repository statistics");

		/*
		 * Collect documents statistics
		 */
		int notindexeddocs = documentDAO
				.queryForInt("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_indexed = 0 and A.ld_deleted = 0 ");
		saveStatistic("notindexeddocs", notindexeddocs);
		int indexeddocs = documentDAO
				.queryForInt("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_indexed = 1 and A.ld_deleted = 0 ");
		saveStatistic("indexeddocs", indexeddocs);
		int deleteddocs = documentDAO.queryForInt("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_deleted = 1 ");
		saveStatistic("deleteddocs", deleteddocs);

		log.debug("Saved documents statistics");

		/*
		 * Collect folders statistics
		 */
		int withdocs = folderDAO
				.queryForInt("SELECT COUNT(A.ld_id) FROM ld_folder A where A.ld_deleted = 0 and A.ld_id in (select B.ld_folderid FROM ld_document B where B.ld_deleted = 0)");
		saveStatistic("withdocs", withdocs);
		int empty = folderDAO
				.queryForInt("SELECT COUNT(A.ld_id) FROM ld_folder A where A.ld_deleted = 0 and A.ld_id not in (select B.ld_folderid FROM ld_document B where B.ld_deleted = 0)");
		saveStatistic("empty", empty);
		int deletedfolders = folderDAO.queryForInt("SELECT COUNT(A.ld_id) FROM ld_folder A where A.ld_deleted = 1 ");
		saveStatistic("deletedfolders", deletedfolders);

		log.debug("Saved folder statistics");

		/*
		 * Save the last update time
		 */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastrun = df.format(new Date());
		saveStatistic("lastrun", lastrun);

		log.info("Statistics collected");

		try {
			log.info("Send collected statistics");
			// Prepare HTTP post
			PostMethod post = new PostMethod("http://stat.logicaldoc.com");
			post.setRequestHeader("Content-Type", "application/txt");
			post.setRequestHeader("Content-Transfer-Encoding", "utf8");

			// Add all statistics as parameters
			post.setParameter("id", id != null ? id : "");
			post.setParameter("userno", userno != null ? userno : "");
			post.setParameter("release", release != null ? release : "");

			post.setParameter("osname", osname != null ? osname : "");
			post.setParameter("osversion", osversion != null ? osversion : "");
			post.setParameter("osarch", osarch != null ? osarch : "");
			post.setParameter("javaversion", javaversion != null ? javaversion : "");
			post.setParameter("javavendor", javavendor != null ? javavendor : "");
			post.setParameter("fileencoding", fileencoding != null ? fileencoding : "");
			post.setParameter("userlanguage", userlanguage != null ? userlanguage : "");
			post.setParameter("userregion", userregion != null ? userregion : "");
			post.setParameter("usertimezone", usertimezone != null ? usertimezone : "");
			post.setParameter("javaarch", javaarch != null ? javaarch : "");

			post.setParameter("users", Integer.toString(users));
			post.setParameter("groups", Integer.toString(groups));
			
			post.setParameter("docdir", Long.toString(docdir));
			post.setParameter("userdir", Long.toString(userdir));
			post.setParameter("indexdir", Long.toString(indexdir));
			post.setParameter("importdir", Long.toString(importdir));
			post.setParameter("exportdir", Long.toString(exportdir));
			post.setParameter("plugindir", Long.toString(plugindir));
			post.setParameter("dbdir", Long.toString(dbdir));
			post.setParameter("logdir", Long.toString(logdir));

			post.setParameter("notindexeddocs", Integer.toString(notindexeddocs));
			post.setParameter("indexeddocs", Integer.toString(indexeddocs));
			post.setParameter("deleteddocs", Integer.toString(deleteddocs));
			post.setParameter("withdocs", Integer.toString(withdocs));
			post.setParameter("empty", Integer.toString(empty));
			post.setParameter("deletedfolders", Integer.toString(deletedfolders));

			// Get HTTP client
			HttpClient httpclient = new HttpClient();

			// Setup Proxy configuration
			String proxyHost = config.getProperty("proxy.host");
			String proxyPort = config.getProperty("proxy.port");
			String proxyUsername = config.getProperty("proxy.username");
			String proxyPassword = config.getProperty("proxy.password");

			if (proxyHost != null && !"".equals(proxyHost.trim())) {
				log.info("Use proxy " + proxyHost);
				httpclient.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
				if (proxyUsername != null && !"".equals(proxyUsername)) {
					httpclient.getState().setCredentials(AuthScope.ANY,
							new UsernamePasswordCredentials(proxyUsername, proxyPassword));
				}
			}

			// Execute request
			try {
				int responseStatusCode = httpclient.executeMethod(post);
				// log status code
				log.debug("Response status code: " + responseStatusCode);
				if (responseStatusCode != 200) {
					throw new IOException(post.getResponseBodyAsString());
				}
			} finally {
				// Release current connection to the connection pool once you
				// have
				// done
				post.releaseConnection();
			}

			log.info("Statistics sent");
		} catch (Throwable t) {
			t.printStackTrace();
			log.error("Unable to send statistics", t);
		}
	}

	/**
	 * Convenience method for saving statistical data in the DB as Generics
	 */
	private void saveStatistic(String parameter, Object val) {
		Generic gen = genericDAO.findByAlternateKey(STAT, parameter);
		if (gen == null) {
			gen = new Generic();
			gen.setType(STAT);
			gen.setSubtype(parameter);
		} else
			genericDAO.initialize(gen);

		if (val instanceof String)
			gen.setString1((String) val);
		else
			gen.setInteger1((Integer) val);
		genericDAO.store(gen);
	}

	@Override
	public boolean isIndeterminate() {
		return true;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public GenericDAO getGenericDAO() {
		return genericDAO;
	}

	public void setGenericDAO(GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	public ContextProperties getConfig() {
		return config;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}

	public DocumentDAO getDocumentDAO() {
		return documentDAO;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public FolderDAO getFolderDAO() {
		return folderDAO;
	}

	public void setFolderDAO(FolderDAO folderDAO) {
		this.folderDAO = folderDAO;
	}

	public GroupDAO getGroupDAO() {
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}
}