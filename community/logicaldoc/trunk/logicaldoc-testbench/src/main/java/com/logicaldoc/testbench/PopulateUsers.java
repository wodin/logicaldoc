package com.logicaldoc.testbench;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generates users and groups records
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class PopulateUsers {

	protected static Log log = LogFactory.getLog(PopulateUsers.class);

	private File rootFolder;

	private String jdbcClass;

	private String jdbcUrl;

	private String username;

	private String password;

	private PreparedStatement insertUser;

	private PreparedStatement insertGroup;

	private PreparedStatement insertUserGroup;

	private int batchSize = 50;

	private Connection con = null;

	private int batchCount = 0;

	private String userPrefix = "user_";

	private String groupPrefix = "group_";

	private int usersTotal = 1000;

	private int groupsTotal = 100;

	public PopulateUsers() {
		try {
			Properties conf = new Properties();
			conf.load(this.getClass().getResourceAsStream("/conf.properties"));
			this.batchSize = Integer.parseInt(conf.getProperty("database.batchSize"));
			this.rootFolder = new File(conf.getProperty("files.rootFolder"));
			this.jdbcClass = conf.getProperty("database.jdbcClass");
			this.jdbcUrl = conf.getProperty("database.jdbcUrl");
			this.username = conf.getProperty("database.username");
			this.password = conf.getProperty("database.password");
			this.userPrefix = conf.getProperty("user.userPrefix");
			this.groupPrefix = conf.getProperty("user.groupPrefix");
			this.usersTotal = Integer.parseInt(conf.getProperty("user.usersTotal"));
			this.groupsTotal = Integer.parseInt(conf.getProperty("user.groupsTotal"));
		} catch (IOException e) {
		}
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
	 * Generate the users and groups population
	 */
	public void populate() {
		log.fatal("Start of users and groups population");
		batchCount = 0;
		try {
			Class.forName(jdbcClass);
			con = DriverManager.getConnection(jdbcUrl, username, password);
			con.setAutoCommit(false);
			insertUser = con
					.prepareStatement("INSERT INTO LD_USER (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_USERNAME,LD_PASSWORD,LD_NAME,LD_FIRSTNAME,LD_STREET,LD_POSTALCODE,LD_CITY,LD_COUNTRY,LD_LANGUAGE,LD_EMAIL,LD_TELEPHONE,LD_TYPE,LD_ENABLED,LD_STATE,LD_TELEPHONE2) VALUES (?,?,0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'');");
			insertGroup = con
					.prepareStatement("INSERT INTO LD_GROUP (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION,LD_TYPE) VALUES (?,?,0,?,?,?);");
			insertUserGroup = con.prepareStatement("INSERT INTO LD_USERGROUP (LD_GROUPID,LD_USERID) VALUES (?,?);");

			long nextUserId = getMaxUserId() + 1;
			long nextGroupId = getMaxGroupId() + 1;

			for (int i = 0; i < usersTotal; i++) {
				try {
					long userId = insertUser(nextUserId);
					if (userId > 0) {
						commit();
						nextUserId++;
						log.info("Created user " + userId);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return;
					// log.error(e, e);
					// log.info("Error on user " + nextUserId);
				}
			}

			for (int j = 0; j < groupsTotal; j++) {
				try {
					long groupId = insertGroup(nextGroupId, null, new Integer(0));
					if (groupId > 0) {
						commit();
						nextGroupId++;
						log.info("Created group " + groupId);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					log.error(e, e);
					log.info("Error on group " + nextGroupId);
				}
			}

			con.commit();
			con.createStatement().execute("SHUTDOWN COMPACT");
			con.commit();
		} catch (Throwable e) {
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
		log.fatal("End of users and groups population");
	}

	private void commit() throws SQLException {
		if (batchCount % batchSize == 0) {
			insertUser.executeBatch();
			insertGroup.executeBatch();
			insertUserGroup.executeBatch();
			con.commit();
			gc();
		}
	}

	private void gc() {
		System.gc();
		System.gc();
		System.gc();
		log.debug("Memory usage: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024));
	}

	/**
	 * Retrieves the max user ID
	 * 
	 * @return user ID
	 * @throws SQLException
	 */
	private long getMaxUserId() throws SQLException {
		log.info("Finding max user id");

		ResultSet rs = con.createStatement().executeQuery("select max(abs(ld_id)) from ld_user;");
		rs.next();
		long userCount = rs.getLong(1);

		log.info("The max user Id was " + userCount);

		return userCount;
	}

	/**
	 * Retrieves the max group ID
	 * 
	 * @return group ID
	 * @throws SQLException
	 */
	private long getMaxGroupId() throws SQLException {
		log.info("Finding max group id");

		ResultSet rs = con.createStatement().executeQuery("select max(abs(ld_id)) from ld_group;");
		rs.next();
		long groupCount = rs.getLong(1);

		log.info("The max group Id was " + groupCount);

		return groupCount;
	}

	/**
	 * Inserts a single user
	 * 
	 * @return the user ID (LD_ID)
	 * @throws SQLException
	 */
	private long insertUser(long id) throws SQLException {
		// LD_ID
		insertUser.setLong(1, id);
		// LD_LASTMODIFIED
		insertUser.setDate(2, new Date(new java.util.Date().getTime()));
		// LD_USERNAME
		insertUser.setString(3, userPrefix + id);
		// LD_PASSWORD
		insertUser.setString(4, cryptString(userPrefix + id));
		// LD_NAME
		insertUser.setString(5, userPrefix + id);
		// LD_FIRSTNAME
		insertUser.setString(6, userPrefix + id);
		// LD_STREET
		insertUser.setString(7, "street");
		// LD_POSTALCODE
		insertUser.setString(8, "41100");
		// LD_CITY
		insertUser.setString(9, "Modena");
		// LD_COUNTRY
		insertUser.setString(10, "ITA");
		// LD_LANGUAGE
		insertUser.setString(11, "it");
		// LD_EMAIL
		insertUser.setString(12, id + "@acme.com");
		// LD_TELEPHONE
		insertUser.setString(13, "333333");
		// LD_TYPE
		insertUser.setInt(14, 0);
		// LD_ENABLED
		insertUser.setInt(15, 1);
		// LD_STATE
		insertUser.setString(16, "Italy");

		insertUser.addBatch();
		batchCount++;

		insertGroup(-id, "_user_" + id, new Integer(1));
		insertUserGroup(-id, id);

		return id;
	}

	private static String cryptString(String original) {
		String copy = "";

		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] digest = md.digest(original.getBytes());

			for (int i = 0; i < digest.length; i++) {
				copy += Integer.toHexString(digest[i] & 0xFF);
			}
		} catch (NoSuchAlgorithmException nsae) {
			log.error(nsae.getMessage());
		}

		return copy;
	}

	/**
	 * Inserts a single group
	 * 
	 * @return the group ID (LD_ID)
	 * @throws SQLException
	 */
	private long insertGroup(long id, String name, Integer type) throws SQLException {
		// LD_ID
		insertGroup.setLong(1, id);
		// LD_LASTMODIFIED
		insertGroup.setDate(2, new Date(new java.util.Date().getTime()));
		// LD_NAME
		insertGroup.setString(3, name != null ? name : (groupPrefix + id));
		// LD_DESCRIPTION
		insertGroup.setString(4, name != null ? name : (groupPrefix + id));
		// LD_TYPE
		insertGroup.setInt(5, type.intValue());

		insertGroup.addBatch();
		batchCount++;

		return id;
	}

	/**
	 * Inserts a user group record mapping
	 * 
	 * @return the group ID (LD_ID)
	 * @throws SQLException
	 */
	private void insertUserGroup(long groupId, long userId) throws SQLException {
		// LD_GROUPID
		insertUserGroup.setLong(1, groupId);
		// LD_USERID
		insertUserGroup.setLong(2, userId);

		insertUserGroup.addBatch();
		batchCount++;
	}
}