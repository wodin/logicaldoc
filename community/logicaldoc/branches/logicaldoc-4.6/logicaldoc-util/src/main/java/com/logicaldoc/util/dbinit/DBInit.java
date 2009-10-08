package com.logicaldoc.util.dbinit;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.util.SqlFile;
import org.hsqldb.util.SqlToolError;

/**
 * Database initialisation utility
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class DBInit {

	protected static Log log = LogFactory.getLog(DBInit.class);

	private String dbms = "";

	private String driver = "";

	private String url = "";

	private String username = "";

	private String password = "";

	private Connection con;

	// List of sql files to be executed
	private List<String> sqlList = new ArrayList<String>();
	
	/**
	 * A list of sql files to execute
	 * 
	 * @param sqlList The list of sql files
	 */
	public DBInit(List<String> sqlList) {
		this.sqlList = sqlList;
	}

	public DBInit() {
	}

	/**
	 * Executes all the sql files defined in the constructor
	 */
	public void execute() {
		try {
			doConnection();
			for (String sql : sqlList) {
				try {
					execute(sql);
				} catch (Exception e) {
					log.error("Failed to execute " + sql, e);
				}
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			try {
				if (con != null)
					con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Execute a SQL statements in the passed string
	 * 
	 * @param sql The SQL to execute
	 */
    public void executeSql(String sql){
    	try {
			doConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.execute();
			st.close();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			log.error("Failed to execute " + sql, e);
			try {
				if (con != null)
					con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
    }

	/**
	 * Executes a single sql file
	 * 
	 * @param sqlFile Path of the file to execute(it can be a classpath
	 *        resource)
	 * @throws IOException
	 * @throws SqlToolError
	 * @throws SQLException
	 */
	private void execute(String sqlFile) throws IOException, SQLException {
		log.debug("Execute " + sqlFile);
		System.out.println("Execute " + sqlFile);
		File file = new File(sqlFile);
		if (!file.exists() || !file.canRead()) {
			// Try to interpret the path as a classpath path
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try {
				file = new File(URLDecoder.decode(loader.getResource(sqlFile).getPath(), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		SqlFile sFile = new SqlFile(file, false, null);
		try {
			sFile.execute(con, new Boolean(true));
		} catch (SqlToolError e) {
			throw new SQLException(e.getMessage());
		}
	}

	protected void doConnection() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			con.setAutoCommit(true);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	protected void rollback() {
		try {
			con.rollback();
		} catch (Exception ex) {
			log.error("can't rollback", ex);
		}
	}

	/**
	 * This method returns the state of the connection.
	 */
	public boolean isConnected() {
		try {
			return !con.isClosed();
		} catch (Exception ex) {
			log.debug("db-connection is open:" + ex.getMessage(), ex);
			return false;
		}
	}

	/**
	 * This method tests a connection.
	 */
	public boolean testConnection() {
		boolean result = false;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			result = true;
			con.close();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

		return result;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getSqlList() {
		return sqlList;
	}

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}
}