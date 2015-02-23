package com.logicaldoc.core.stats;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.util.UserUtil;
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

	protected GenericDAO genericDAO;

	protected TenantDAO tenantDAO;

	protected ContextProperties config;

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

		EMailSender sender = new EMailSender(Tenant.DEFAULT_NAME);
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
		int users = userDao.count(null);
		int groups = groupDAO.count();
		log.debug("Collected users data");

		long userdir = 0;
		File userDir = UserUtil.getUsersDir();
		userdir = FileUtils.sizeOfDirectory(userDir);
		saveStatistic("userdir", userdir, Tenant.SYSTEM_ID);

		long indexdir = 0;
		File indexDir = new File(config.getPropertyWithSubstitutions("index.dir"));
		if (indexDir.exists())
			indexdir = FileUtils.sizeOfDirectory(indexDir);
		saveStatistic("indexdir", indexdir, Tenant.SYSTEM_ID);

		long importdir = 0;
		File importDir = new File(config.getPropertyWithSubstitutions("conf.importdir"));
		if (importDir.exists())
			importdir = FileUtils.sizeOfDirectory(importDir);
		saveStatistic("importdir", importdir, Tenant.SYSTEM_ID);

		long exportdir = 0;
		File exportDir = new File(config.getPropertyWithSubstitutions("conf.exportdir"));
		if (exportDir.exists())
			exportdir = FileUtils.sizeOfDirectory(exportDir);
		saveStatistic("exportdir", exportdir, Tenant.SYSTEM_ID);

		long plugindir = 0;
		File pluginsDir = PluginRegistry.getPluginsDir();
		plugindir = FileUtils.sizeOfDirectory(pluginsDir);
		saveStatistic("plugindir", plugindir, Tenant.SYSTEM_ID);

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
		saveStatistic("dbdir", dbdir, Tenant.SYSTEM_ID);

		long logdir = 0;
		File logsDir = new File(config.getPropertyWithSubstitutions("conf.logdir"));
		if (logsDir.exists())
			logdir = FileUtils.sizeOfDirectory(logsDir);
		saveStatistic("logdir", logdir, Tenant.SYSTEM_ID);

		log.debug("Saved repository statistics");

		/*
		 * Collect documents statistics
		 */
		long[] docStats = extractDocStats(Tenant.SYSTEM_ID);
		long totaldocs = docStats[3];
		long archiveddocs = docStats[4];
		long docdir = docStats[5];

		List<Tenant> tenants = tenantDAO.findAll();
		for (Tenant tenant : tenants)
			extractDocStats(tenant.getId());

		log.debug("Saved documents statistics");

		/*
		 * Collect folders statistics
		 */
		long[] fldStats = extractFldStats(Tenant.SYSTEM_ID);
		long withdocs = fldStats[0];
		long empty = fldStats[1];
		long deletedfolders = fldStats[2];

		for (Tenant tenant : tenants)
			extractFldStats(tenant.getId());

		log.debug("Saved folder statistics");

		/*
		 * Collect sizing statistics
		 */
		long tags = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_tag");
		long versions = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_version");
		long histories = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_history");
		long user_histories = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_user_history");

		/*
		 * Collect features statistics
		 */
		long bookmarks = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_bookmark");
		long notes = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_note");
		long links = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_link");
		long aliases = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_document WHERE ld_docref IS NOT NULL");
		long tenantsCount = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_tenant");

		long workflow_histories = -1;
		try {
			try {
				Class.forName("com.logicaldoc.workflow.WorkflowHistory");
				workflow_histories = folderDAO.queryForLong("SELECT COUNT(*) FROM ld_workflowhistory");
			} catch (ClassNotFoundException exception) {

			}
		} catch (Exception e) {
		}

		/*
		 * Save the last update time
		 */
		saveStatistic("lastrun", new Date(), Tenant.SYSTEM_ID);

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
			post.setParameter("archived_docs", Long.toString(archiveddocs));
			post.setParameter("folders", Long.toString(withdocs + empty + deletedfolders));
			post.setParameter("tags", Long.toString(tags));
			post.setParameter("versions", Long.toString(versions));
			post.setParameter("histories", Long.toString(histories));
			post.setParameter("user_histories", Long.toString(user_histories));

			// Features usage
			post.setParameter("bookmarks", Long.toString(bookmarks));
			post.setParameter("notes", Long.toString(notes));
			post.setParameter("links", Long.toString(links));
			post.setParameter("aliases", Long.toString(aliases));
			post.setParameter("workflow_histories", Long.toString(workflow_histories));
			post.setParameter("tenants", Long.toString(tenantsCount));

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
	 * Retrieves the document stats of a specific tenant and saves the results
	 * in the database
	 * 
	 * @param tenantId The tenant Id if the system tenant is used the whole
	 *        stats are computed
	 * @return Ordered list of stats<br/>
	 *         <ol>
	 *         <li>notindexeddocs</li>
	 *         <li>indexeddocs</li>
	 *         <li>deleteddocs</li>
	 *         <li>totaldocs</li>
	 *         <li>archiveddocs</li>
	 *         <li>docdir (total size of the whole repository)</li>
	 *         </ol>
	 */
	private long[] extractDocStats(long tenantId) {
		long[] stats = new long[6];
		stats[0] = documentDAO
				.queryForLong("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_indexed = 0 and A.ld_deleted = 0 "
						+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : "")
						+ " and not A.ld_status=" + AbstractDocument.DOC_ARCHIVED);
		stats[1] = documentDAO
				.queryForLong("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_indexed = 1 and A.ld_deleted = 0 "
						+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : "")
						+ " and not A.ld_status=" + AbstractDocument.DOC_ARCHIVED);
		stats[2] = documentDAO.queryForLong("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_deleted  > 0 "
				+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : ""));
		if (tenantId != Tenant.SYSTEM_ID) {
			stats[3] = documentDAO.count(tenantId, true, true);
		} else {
			stats[3] = documentDAO.count(null, true, true);
		}
		stats[4] = documentDAO.queryForLong("SELECT COUNT(A.ld_id) FROM ld_document A where A.ld_status = "
				+ AbstractDocument.DOC_ARCHIVED
				+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : ""));
		stats[5] = documentDAO
				.queryForLong("SELECT SUM(A.ld_filesize) from ld_version A where A.ld_version = A.ld_fileversion "
						+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : ""));

		saveStatistic("notindexeddocs", stats[0], tenantId);
		saveStatistic("indexeddocs", stats[1], tenantId);
		saveStatistic("deleteddocs", stats[2], tenantId);
		saveStatistic("totaldocs", stats[3], tenantId);
		saveStatistic("archiveddocs", stats[4], tenantId);
		saveStatistic("docdir", stats[5], tenantId);

		return stats;
	}

	/**
	 * Retrieves the folder stats of a specific tenant and saves the results in
	 * the database
	 * 
	 * @param tenantId The tenant Id if the system tenant is used the whole
	 *        stats are computed
	 * @return Ordered list of stats<br/>
	 *         <ol>
	 *         <li>withdocs</li>
	 *         <li>empty</li>
	 *         <li>deletedfolders</li>
	 *         </ol>
	 */
	private long[] extractFldStats(long tenantId) {
		long[] stats = new long[4];
		stats[0] = folderDAO
				.queryForLong("SELECT COUNT(A.ld_id) FROM ld_folder A where A.ld_deleted = 0 and A.ld_id in (select B.ld_folderid FROM ld_document B where B.ld_deleted = 0) "
						+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : ""));

		stats[1] = folderDAO
				.queryForLong("SELECT COUNT(A.ld_id) FROM ld_folder A where A.ld_deleted = 0 and A.ld_id not in (select B.ld_folderid FROM ld_document B where B.ld_deleted = 0) "
						+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : ""));

		stats[2] = folderDAO.queryForLong("SELECT COUNT(A.ld_id) FROM ld_folder A where A.ld_deleted  > 0 "
				+ (tenantId != Tenant.SYSTEM_ID ? " and A.ld_tenantid=" + tenantId : ""));

		saveStatistic("withdocs", stats[0], tenantId);
		saveStatistic("empty", stats[1], tenantId);
		saveStatistic("deletedfolders", stats[2], tenantId);

		return stats;
	}

	/**
	 * Convenience method for saving statistical data in the DB as Generics
	 */
	protected void saveStatistic(String parameter, Object val, long tenantId) {
		Generic gen = genericDAO.findByAlternateKey(STAT, parameter, null, tenantId);
		if (gen == null) {
			gen = new Generic();
			gen.setType(STAT);
			gen.setTenantId(tenantId);
			gen.setSubtype(parameter);
		} else
			genericDAO.initialize(gen);

		if (val instanceof Date)
			gen.setDate1((Date) val);
		else if (val instanceof String)
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

	public void setTenantDAO(TenantDAO tenantDAO) {
		this.tenantDAO = tenantDAO;
	}
}