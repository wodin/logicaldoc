package com.logicaldoc.testbench;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generates database records browsing an existing filesystem in LogicalDOC's
 * format.
 * <p>
 * <b>NOTE:</b> The file system must be compliant with the one used by
 * LogicalDOC to store document archive files, so folders must be named with
 * internal menu id.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class PopulateDatabase {
	protected static Log log = LogFactory.getLog(PopulateDatabase.class);

	private File rootFolder;

	private String jdbcClass;

	private String jdbcUrl;

	private String username;

	private String password;

	private PreparedStatement insertMenu;

	private PreparedStatement insertMenuGroup;

	private PreparedStatement insertDoc;

	private PreparedStatement insertTags;

	private PreparedStatement insertVersion;

	private long[] groupIds = new long[] { 1 };

	private int batchSize = 50;

	private Connection con = null;

	private int batchCount = 0;

	private String language = "en";

	private Set<Long> existingDocIds = new HashSet<Long>();

	private Set<Long> existingMenuIds = new HashSet<Long>();

	public PopulateDatabase() {
		try {
			Properties conf = new Properties();
			conf.load(this.getClass().getResourceAsStream("/conf.properties"));
			this.batchSize = Integer.parseInt(conf.getProperty("database.batchSize"));
			this.rootFolder = new File(conf.getProperty("files.rootFolder"));
			this.jdbcClass = conf.getProperty("database.jdbcClass");
			this.jdbcUrl = conf.getProperty("database.jdbcUrl");
			this.username = conf.getProperty("database.username");
			this.password = conf.getProperty("database.password");
			this.language = conf.getProperty("database.language");
		} catch (IOException e) {
		}
	}

	private void initExistingIds() throws SQLException {
		log.info("Collecting existing ids");
		existingDocIds.clear();
		existingMenuIds.clear();

		ResultSet rs = con.createStatement().executeQuery("select ld_id from ld_document;");
		while (rs.next()) {
			existingDocIds.add(rs.getLong(1));
		}

		rs = con.createStatement().executeQuery("select ld_id from ld_menu;");
		while (rs.next()) {
			existingMenuIds.add(rs.getLong(1));
		}

		log.info("Found " + existingDocIds.size() + " existing documents");
		log.info("Found " + existingMenuIds.size() + " existing menus");
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Retrieves the rootFolder directory
	 */
	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * Retrieves the JDBC Driver
	 */
	public String getJdbcClass() {
		return jdbcClass;
	}

	public void setJdbcClass(String jdbcClass) {
		this.jdbcClass = jdbcClass;
	}

	/**
	 * Retrieves the JDBC Url for database connection
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	/**
	 * Retrieves the user name
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Retrieves the password
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retrieves the group IDs
	 * 
	 * @return The array with the group IDs
	 */
	public long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(long[] groupIds) {
		this.groupIds = groupIds;
	}

	/**
	 * Retrieves the inserts number of folder and documents after which make a
	 * commit
	 */
	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * Generate the database population looking for folders and files in the
	 * rootFolder directory
	 */
	public void populate() {
		log.fatal("Start of database population");
		batchCount = 0;
		try {
			Class.forName(jdbcClass);
			con = DriverManager.getConnection(jdbcUrl, username, password);
			con.setAutoCommit(false);
			insertMenu = con
					.prepareStatement("INSERT INTO LD_MENU (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TEXT,LD_PARENTID,LD_SORT,LD_ICON,LD_PATH,LD_PATHEXTENDED,LD_TYPE,LD_REF,LD_SIZE) VALUES (?,?,0,?,?,?,?,?,?,?,?,?);");
			insertMenuGroup = con
					.prepareStatement("INSERT INTO LD_MENUGROUP (LD_MENUID,LD_GROUPID,LD_WRITE,ld_add,LD_SECURITY,LD_IMMUTABLE,LD_DELETE,LD_RENAME, ld_import, ld_export, LD_SIGN, LD_ARCHIVE) VALUES (?,?,?,1,1,1,1,1,1,1,1,1);");
			insertDoc = con
					.prepareStatement("INSERT INTO LD_DOCUMENT (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TITLE,LD_VERSION,LD_DATE,LD_PUBLISHER,LD_PUBLISHERID,LD_STATUS,LD_TYPE,LD_LOCKUSERID,LD_SOURCE,LD_SOURCEAUTHOR,LD_SOURCEDATE,LD_SOURCETYPE,LD_COVERAGE,LD_LANGUAGE,LD_FILENAME,LD_FILESIZE,LD_INDEXED,LD_FOLDERID,LD_CREATION,LD_IMMUTABLE,LD_DIGEST,LD_SIGNED,LD_FILEVERSION,LD_CUSTOMID,LD_CREATOR,LD_CREATORID,LD_SOURCEID,LD_OBJECT,LD_RECIPIENT,LD_TEMPLATEID,LD_EXPORTSTATUS) VALUES (?,?,0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,?,?,0,?,0,'1.0',?,?,?,?,?,?,null,null);");
			insertTags = con.prepareStatement("INSERT INTO LD_TAG (LD_DOCID,LD_TAG) VALUES (?,?);");
			insertVersion = con
					.prepareStatement("INSERT INTO LD_VERSION  (LD_ID, LD_VERSION, LD_USERID, LD_USERNAME, LD_DATE, LD_COMMENT, LD_LASTMODIFIED,LD_DELETED,LD_IMMUTABLE,LD_CUSTOMID,LD_TITLE,LD_FILEVERSION,LD_CREATION,LD_PUBLISHER,LD_PUBLISHERID,LD_CREATOR,LD_CREATORID,LD_STATUS,LD_TYPE,LD_LOCKUSERID,LD_SOURCE,LD_SOURCEAUTHOR,LD_SOURCEDATE,LD_SOURCEID,LD_SOURCETYPE,LD_OBJECT,LD_COVERAGE,LD_LANGUAGE,LD_FILENAME,LD_FILESIZE,LD_INDEXED,LD_SIGNED,LD_DIGEST,LD_RECIPIENT,LD_FOLDERID,LD_FOLDERNAME,LD_TEMPLATEID,LD_TEMPLATENAME,LD_TGS,LD_VERSIONDATE,LD_EVENT,LD_DOCUMENTID)"
							+ "VALUES (?,?,?,?,?,?,?,0,1,?,?,'1.0',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,0,?,?,?,?,null,null,?,?,?,?);");
			initExistingIds();
			addDocuments(rootFolder, "/");
			forceCommit();

			con.commit();
			con.createStatement().execute("SHUTDOWN COMPACT");
			con.commit();
		} catch (Throwable e) {
			e.printStackTrace();
			log.error(e);
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
		log.fatal("End of database population");
	}

	/**
	 * Adds all documents inside the specified folder
	 * 
	 * @param folder The folder to browse
	 * @param path Path for LD_PATH and LS_PATHEXT fields
	 * @throws SQLException
	 */
	private void addDocuments(File folder, String path) {
		long parentFolderId = Long.parseLong(folder.getName());
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && !files[i].getName().startsWith("doc_")) {
				try {
					long folderId = insertFolder(files[i], (path + "/" + parentFolderId).replaceAll("//", "/"));
					if (folderId > 0) {
						commit();
						log.info("Created folder " + folderId);
					}
				} catch (SQLException e) {
					log.error(e, e);
				}
				// Recursive invocation
				addDocuments(files[i], path + "/" + parentFolderId);
			} else if (files[i].isDirectory() && files[i].getName().startsWith("doc_")) {
				try {
					long docId = insertDocument(files[i]);
					if (docId > 0) {
						commit();
						log.info("Created document " + docId);
					}
				} catch (SQLException e) {
					log.error(e, e);
				}
			}
		}
	}

	private void forceCommit() throws SQLException {
		insertDoc.executeBatch();
		insertVersion.executeBatch();
		insertTags.executeBatch();
		insertDoc.clearBatch();
		insertVersion.clearBatch();
		insertTags.clearBatch();
		con.commit();
		gc();
	}

	private void commit() throws SQLException {
		if (batchCount % batchSize == 0) {
			forceCommit();
		}
	}

	private void gc() {
		System.gc();
		System.gc();
		System.gc();
		log.debug("Memory usage: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024));
	}

	/**
	 * Inserts a single document
	 * 
	 * @param dir The document dir in the file system
	 * @return the document ID (LD_ID)
	 * @throws SQLException
	 */
	private long insertDocument(File dir) throws SQLException {
		File docFile = dir.listFiles()[0];
		String filename = docFile.getName();
		long filesize = docFile.length();
		long id = Long.parseLong(dir.getName().substring(dir.getName().lastIndexOf("_") + 1));
		String content = Util.parse(docFile);
		String digest = Util.computeDigest(docFile);

		// Skip condition
		if (existingDocIds.contains(id)) {
			log.debug("Skipping file " + dir.getName());
			return -1;
		}

		long folderId = Long.parseLong(dir.getParentFile().getName());
		String extension = docFile.getName().substring(docFile.getName().lastIndexOf(".") + 1);

		// LD_ID
		insertDoc.setLong(1, id);
		// LD_LASTMODIFIED
		insertDoc.setDate(2, new Date(docFile.lastModified()));
		// LD_TITLE
		insertDoc.setString(3, docFile.getName().substring(0, docFile.getName().lastIndexOf(".")));
		// LD_VERSION
		insertDoc.setString(4, "1.0");
		// LD_DATE
		insertDoc.setDate(5, new Date(docFile.lastModified()));
		// LD_PUBLISHER
		insertDoc.setString(6, "admin");
		// LD_PUBLISHERID
		insertDoc.setLong(7, 1);
		// LD_STATUS
		insertDoc.setInt(8, 0);
		// LD_TYPE
		insertDoc.setString(9, extension);
		// LD_LOCKUSERID
		insertDoc.setLong(10, -1);
		// LD_SOURCE
		insertDoc.setString(11, "LogicalDOC");
		// LD_SOURCEAUTHOR
		insertDoc.setString(12, "");
		// LD_SOURCEDATE
		insertDoc.setDate(13, new Date(new java.util.Date().getTime()));
		// LD_SOURCETYPE
		insertDoc.setString(14, "");
		// LD_COVERAGE
		insertDoc.setString(15, "test");
		// LD_LANGUAGE
		insertDoc.setString(16, language);
		// LD_FILENAME
		insertDoc.setString(17, filename);
		// LD_FILESIZE
		insertDoc.setLong(18, filesize);
		// LD_FOLDERID
		insertDoc.setLong(19, folderId);
		// LD_CREATION
		insertDoc.setDate(20, new Date(new java.util.Date().getTime()));
		// LD_DIGEST
		insertDoc.setString(21, digest);
		// LD_CUSTOMID
		insertDoc.setString(22, "" + id);
		// LD_CREATOR
		insertDoc.setString(23, "admin");
		// LD_CREATORID
		insertDoc.setLong(24, 1);
		// LD_SOURCEID
		insertDoc.setString(25, "" + id);
		// LD_OBJECT
		insertDoc.setString(26, "testbench");
		// LD_RECIPIENT
		insertDoc.setString(27, "testUser");

		insertDoc.addBatch();
		batchCount++;

		// Insert 3 document's tags
		Set<String> tgs = Util.extractWords(3, content);
		int i = 0;
		for (String tag : tgs) {
			// LD_DOCID
			insertTags.setLong(1, id);
			if (tag.length() > 255)
				tag = tag.substring(0, 254);
			// LD_TAG
			insertTags.setString(2, tag);
			insertTags.addBatch();
			i++;
		}

		// Insert a version

		// LD_ID
		insertVersion.setLong(1, id);
		// LD_VERSION
		insertVersion.setString(2, "1.0");
		// LD_USERID
		insertVersion.setLong(3, 1);
		// LD_USERNAME
		insertVersion.setString(4, "admin");
		// LD_DATE
		insertVersion.setDate(5, new Date(docFile.lastModified()));
		// LD_COMMENT
		insertVersion.setString(6, "initial version");
		// LD_LASTMODIFIED
		insertVersion.setDate(7, new Date(docFile.lastModified()));
		// LD_CUSTOMID
		insertVersion.setString(8, "" + id);
		// LD_TITLE
		insertVersion.setString(9, docFile.getName().substring(0, docFile.getName().lastIndexOf(".")));
		// LD_CREATION
		insertVersion.setDate(10, new Date(new java.util.Date().getTime()));
		// LD_PUBLISHER
		insertVersion.setString(11, "admin");
		// LD_PUBLISHERID
		insertVersion.setLong(12, 1);
		// LD_CREATOR
		insertVersion.setString(13, "admin");
		// LD_CREATORID
		insertVersion.setLong(14, 1);
		// LD_STATUS
		insertVersion.setInt(15, 0);
		// LD_TYPE
		insertVersion.setString(16, extension);
		// LD_LOCKUSERID
		insertVersion.setLong(17, -1);
		// LD_SOURCE
		insertVersion.setString(18, "LogicalDOC");
		// LD_SOURCEAUTHOR
		insertVersion.setString(19, "");
		// LD_SOURCEDATE
		insertVersion.setDate(20, new Date(new java.util.Date().getTime()));
		// LD_SOURCEID
		insertVersion.setString(21, "" + id);
		// LD_SOURCETYPE
		insertVersion.setString(22, "");
		// LD_OBJECT
		insertVersion.setString(23, "testbench");
		// LD_COVERAGE
		insertVersion.setString(24, "test");
		// LD_LANGUAGE
		insertVersion.setString(25, language);
		// LD_FILENAME
		insertVersion.setString(26, filename);
		// LD_FILESIZE
		insertVersion.setLong(27, filesize);
		// LD_DIGEST
		insertVersion.setString(28, digest);
		// LD_RECIPIENT
		insertVersion.setString(29, "testUser");
		// LD_FOLDERID
		insertVersion.setLong(30, folderId);
		// LD_FOLDERNAME
		insertVersion.setString(31, Long.toString(folderId));
		// LD_KWDS
		insertVersion.setString(32, "pippo");
		// LD_VERSIONDATE
		insertVersion.setDate(33, new Date(new java.util.Date().getTime()));
		// LD_EVENT
		insertVersion.setString(34, "testEvent");
		// LD_DOCUMENTID
		insertVersion.setLong(35, id);

		insertVersion.addBatch();

		return id;
	}

	/**
	 * Inserts a folder into the database.
	 * 
	 * @param dir The file system counterpart of the folder (it's name is used
	 *        as LD_ID)
	 * @param path Path to be used as LD_PATH and LD_PATHEXT
	 * @return the folder ID (LD_ID)
	 * @throws SQLException
	 */
	private long insertFolder(File dir, String path) throws SQLException {
		long id = Long.parseLong(dir.getName());
		long parentId = Long.parseLong(dir.getParentFile().getName());

		// Skip condition
		if (existingMenuIds.contains(id)) {
			log.debug("Skipping folder " + dir.getName());
			return -1;
		}

		// LD_ID
		insertMenu.setLong(1, id);
		// LD_LASTMODIFIED
		insertMenu.setDate(2, new Date(new java.util.Date().getTime()));
		// LD_TEXT
		insertMenu.setString(3, dir.getName());
		// LD_PARENTID
		insertMenu.setLong(4, parentId);
		// LD_SORT
		insertMenu.setInt(5, 0);
		// LD_ICON
		insertMenu.setString(6, "administration.png");
		// LD_PATH
		insertMenu.setString(7, path);

		String pathExt = path;
		if (pathExt.startsWith("/5") && pathExt.length() > 2) {
			pathExt = "/menu.documents" + pathExt.substring(2);
		} else if ("/5".equals(pathExt)) {
			pathExt = "/menu.documents";
		}
		if (pathExt.endsWith("/"))
			pathExt += "/";
		// LD_PATHEXTENDED
		insertMenu.setString(8, pathExt);
		// LD_TYPE
		insertMenu.setInt(9, 3);
		// LD_REF
		insertMenu.setString(10, "");
		// LD_SIZE
		insertMenu.setLong(11, 0);
		insertMenu.execute();
		batchCount++;

		for (int j = 0; j < groupIds.length; j++) {
			// LD_MENUID
			insertMenuGroup.setLong(1, id);
			// LD_GROUPID
			insertMenuGroup.setLong(2, groupIds[j]);
			// LD_WRITEENABLE
			insertMenuGroup.setInt(3, 1);
			insertMenuGroup.addBatch();
		}
		insertMenuGroup.executeBatch();
		insertMenuGroup.clearBatch();

		return id;
	}
}