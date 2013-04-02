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
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.core.security.SystemQuota;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Collects statistical informations to the stats site
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StatsCollector extends Task {
	public static final String STAT = "stat";

	public final static String NAME = "StatsCollector";

	private DocumentDAO documentDAO;

	private FolderDAO folderDAO;

	private GroupDAO groupDAO;

	private GenericDAO genericDAO;

	private ContextProperties config;

	private Storer storer;

	private static String userno = "community";

	private static String product = "LogicalDOC";

	private static String productName = "LogicalDOC Community";

	public StatsCollector() {
		super(NAME);
		log = LoggerFactory.getLogger(StatsCollector.class);
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start statistics collection");

		/*
		 * Collect identification data
		 */
		String id = config.getProperty("id");
		String release = config.getProperty("product.release");
		log.debug("Collected identification data");

		EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
		String email = sender.getSender();
		log.debug("Collected contact data");

		/*
		 * Collect environment data
		 */
		String osname = System.getProperty("os.name");
		String osversion = System.getProperty("os.version");
		String javaversion = System.getProperty("java.version");
		String javavendor = System.getProperty("java.vendor");
		String fileencoding = System.getProperty("file.encoding");
		String userlanguage = System.getProperty("user.language");
		String usercountry = System.getProperty("user.country");
		String javaarch = System.getProperty("sun.arch.data.model");
		String dbms = config.getProperty("jdbc.dbms");

		log.debug("Collected environment data");

		/*
		 * Collect registration data
		 */
		String regName = config.getProperty("reg.name");
		String regEmail = config.getProperty("reg.email");
		String regOrganization = config.getProperty("reg.organization");
		String regWebsite = config.getProperty("reg.website");

		/*
		 * Collect users data
		 */
		int users = userDao.count();
		int groups = groupDAO.count();
		log.debug("Collected users data");

		/*
		 * Compute repository statistics. The docs total size is computed on DB
		 * so it is just an estimation of the effective size.
		 */
		long docdir = documentDAO
				.queryForLong("select sum(ld_filesize) from ld_version where ld_version = ld_fileversion");
		SystemQuota.setTotalSize(docdir);
		saveStatistic("docdir", Long.toString(docdir));

		long userdir = 0;
		File userDir = UserUtil.getUsersDir();
		userdir = FileUtils.sizeOfDirectory(userDir);
		saveStatistic("userdir", Long.toString(userdir));

		long indexdir = 0;
		File indexDir = new File(config.getPropertyWithSubstitutions("index.dir"));
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
		File pluginsDir = PluginRegistry.getPluginsDir();
		plugindir = FileUtils.sizeOfDirectory(pluginsDir);
		saveStatistic("plugindir", Long.toString(plugindir));

		long dbdir = 0;

		/*
		 * Try to determine the database size by using proprietary queries
		 */
		try {
			if ("mysql".equals(documentDAO.getDbms()))
				dbdir = documentDAO
						.queryForLong("select sum(data_length+index_length) from information_schema.tables where table_schema=database();");
			else if ("oracle".equals(documentDAO.getDbms()))
				dbdir = documentDAO.queryForLong("SELECT sum(bytes) FROM user_segments");
			else if ("postgresql".equals(documentDAO.getDbms()))
				dbdir = documentDAO.queryForLong("select pg_database_size(current_database())");
		} catch (Throwable t) {

		}

		/*
		 * Fall back to the database dir
		 */
		if (dbdir == 0) {
			File dbDir = new File(config.getPropertyWithSubstitutions("conf.dbdir"));
			if (dbDir.exists())
				dbdir = FileUtils.sizeOfDirectory(dbDir);
		}

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
		int totaldocs = (int) documentDAO.count(false) + deleteddocs;
		saveStatistic("totaldocs", documentDAO.count(true));

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
		 * Collect sizing statistics
		 */
		int tags = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_tag");
		int versions = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_version");
		int histories = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_history");
		int user_histories = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_user_history");

		/*
		 * Collect features statistics
		 */
		int bookmarks = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_bookmark");
		int notes = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_note");
		int links = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_link");
		int aliases = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_document WHERE ld_docref IS NOT NULL");

		int workflow_histories = -1;
		try {
			try {
				Class.forName("com.logicaldoc.workflow.WorkflowHistory");
				workflow_histories = folderDAO.queryForInt("SELECT COUNT(*) FROM ld_workflowhistory");
			} catch (ClassNotFoundException exception) {

			}
		} catch (Exception e) {
		}

		/*
		 * Save the last update time
		 */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastrun = df.format(new Date());
		saveStatistic("lastrun", lastrun);

		log.info("Statistics collected");

		try {
			log.debug("Package collected statistics");
			// Prepare HTTP post
			PostMethod post = new PostMethod("http://stat.logicaldoc.com/stats/collect");

			// Add all statistics as parameters
			post.setParameter("id", id != null ? id : "");
			post.setParameter("userno", userno != null ? userno : "");
			post.setParameter("product_release", release != null ? release : "");
			post.setParameter("email", email != null ? email : "");
			post.setParameter("product", StatsCollector.product != null ? StatsCollector.product : "");
			post.setParameter("product_name", StatsCollector.productName != null ? StatsCollector.productName : "");

			post.setParameter("java_version", javaversion != null ? javaversion : "");
			post.setParameter("java_vendor", javavendor != null ? javavendor : "");
			post.setParameter("java_arch", javaarch != null ? javaarch : "");
			post.setParameter("dbms", dbms != null ? dbms : "");

			post.setParameter("os_name", osname != null ? osname : "");
			post.setParameter("os_version", osversion != null ? osversion : "");
			post.setParameter("file_encoding", fileencoding != null ? fileencoding : "");

			post.setParameter("user_language", userlanguage != null ? userlanguage : "");
			post.setParameter("user_country", usercountry != null ? usercountry : "");

			// Sizing
			post.setParameter("users", Integer.toString(users));
			post.setParameter("groups", Integer.toString(groups));
			post.setParameter("docs", Long.toString(totaldocs));
			post.setParameter("folders", Integer.toString(withdocs + empty + deletedfolders));
			post.setParameter("tags", Integer.toString(tags));
			post.setParameter("versions", Integer.toString(versions));
			post.setParameter("histories", Integer.toString(histories));
			post.setParameter("user_histories", Integer.toString(user_histories));

			// Features usage
			post.setParameter("bookmarks", Integer.toString(bookmarks));
			post.setParameter("notes", Integer.toString(notes));
			post.setParameter("links", Integer.toString(links));
			post.setParameter("aliases", Integer.toString(aliases));
			post.setParameter("workflow_histories", Integer.toString(workflow_histories));

			// Quotas
			post.setParameter("docdir", Long.toString(docdir));
			post.setParameter("indexdir", Long.toString(indexdir));
			post.setParameter("quota",
					Long.toString(docdir + indexdir + userdir + importdir + exportdir + plugindir + dbdir + logdir));

			// Registration
			post.setParameter("reg_name", regName != null ? regName : "");
			post.setParameter("reg_email", regEmail != null ? regEmail : "");
			post.setParameter("reg_organization", regOrganization != null ? regOrganization : "");
			post.setParameter("reg_website", regWebsite != null ? regWebsite : "");

			// Get HTTP client
			HttpClient httpclient = new HttpClient();

			// Setup Proxy configuration
			String proxyHost = config.getProperty("proxy.host");
			String proxyPort = config.getProperty("proxy.port");
			String proxyUsername = config.getProperty("proxy.username");
			String proxyPassword = config.getProperty("proxy.password");

			if (proxyHost != null && !"".equals(proxyHost.trim())) {
				log.debug("Use proxy " + proxyHost);
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
				// have done
				post.releaseConnection();
			}

			log.info("Statistics packaged");
		} catch (Throwable t) {
			log.warn("Troubles packaging the statistics");
			log.debug("Unable to send statistics", t);
		}
	}

	/**
	 * Convenience method for saving statistical data in the DB as Generics
	 */
	private void saveStatistic(String parameter, Object val) {
		Generic gen = genericDAO.findByAlternateKey(STAT, parameter, null);
		if (gen == null) {
			gen = new Generic();
			gen.setType(STAT);
			gen.setSubtype(parameter);
		} else
			genericDAO.initialize(gen);

		if (val instanceof String)
			gen.setString1((String) val);
		else if (val instanceof Long)
			gen.setInteger1((Long) val);
		else
			gen.setInteger1(((Integer) val).longValue());
		genericDAO.store(gen);
	}

	@Override
	public boolean isIndeterminate() {
		return true;
	}

	@Override
	public boolean isConcurrent() {
		return true;
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

	public static void setUserno(String userno) {
		StatsCollector.userno = userno;
	}

	public static void setProduct(String product) {
		StatsCollector.product = product;
	}

	public static void setProductName(String productName) {
		StatsCollector.productName = productName;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}
}