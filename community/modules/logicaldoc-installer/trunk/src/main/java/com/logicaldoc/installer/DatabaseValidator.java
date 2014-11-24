package com.logicaldoc.installer;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.installer.util.Log;

/**
 * Validates the connection parameters and composes the database connection URL.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8.1
 */
public class DatabaseValidator implements DataValidator {

	protected static Map<String, String[]> dbDefaults = new HashMap<String, String[]>();

	static {
		dbDefaults.put("embedded", new String[] { "hsqldb", "org.hsqldb.jdbc.JDBCDriver",
				"org.hibernate.dialect.HSQLDialect", "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS", "", "" });
		dbDefaults.put("mysql", new String[] { "mysql", "com.mysql.jdbc.Driver", "org.hibernate.dialect.MySQLDialect",
				"SELECT 1", "3306", "jdbc:mysql://<server>[,<failoverhost>][<:3306>]/<database>" });
		// settings.put(DATABASES[2], new String[] { "postgresql",
		// "org.postgresql.Driver",
		// "org.hibernate.dialect.PostgreSQLDialect", "SELECT 1",
		// "5432","jdbc:postgresql:[<//server>[<:5432>/]]<database>" });
		dbDefaults.put("oracle", new String[] { "oracle", "oracle.jdbc.driver.OracleDriver",
				"org.hibernate.dialect.Oracle10gDialect", "SELECT 1 FROM DUAL", "1521",
				"jdbc:oracle:thin:@<server>[<:1521>]:<sid>", "org.hibernate.dialect.Oracle10gDialect" });
		dbDefaults.put("mssql", new String[] { "mssql", "net.sourceforge.jtds.jdbc.Driver",
				"org.hibernate.dialect.SQLServerDialect", "SELECT 1", "1433",
				"jdbc:jtds:sqlserver://<server>[:<1433>]/<database>;instance=<instance>" });
	}

	@Override
	public boolean getDefaultAnswer() {
		return false;
	}

	@Override
	public String getErrorMessageId() {
		return I18N.message("databasenotconnected");
		// An IzPack bug doesn't allow the resolution of the id in the langpack
		// file
		// return "databasenotconnected";
	}

	@Override
	public String getWarningMessageId() {
		return I18N.message("databasenotconnected");
		// An IzPack bug doesn't allow the resolution of the id in the langpack
		// file
		// return "databasenotconnected";
	}

	@Override
	public Status validateData(InstallData data) {
		String engine = data.getVariable(Constants.DBENGINE).trim();
		String driver = ((String[]) dbDefaults.get(engine))[1];
		String dialect = ((String[]) dbDefaults.get(engine))[2];
		String validationQuery = ((String[]) dbDefaults.get(engine))[3];
		boolean manualUrl = "true".equals(data.getVariable(Constants.DBMANUALURL).trim());
		String url = data.getVariable(Constants.DBURL);

		data.setVariable(Constants.DBDRIVER, driver);
		data.setVariable(Constants.DBDIALECT, dialect);
		data.setVariable(Constants.DBQUERY, validationQuery);

		boolean connected = true;

		if ("embedded".equals(engine)) {
			url = "embedded";
			connected = true;
		} else {
			connected = checkDbConnection(data);
			if (!manualUrl)
				url = composeUrl(true, data);
		}

		data.setVariable(Constants.DBURL, url);

		if (connected)
			return Status.OK;
		else
			return Status.ERROR;
	}

	/**
	 * Use the given parameters to compose the final JDBC URL
	 * 
	 * @param withDb is the database has to be put in the URL or not
	 */
	private String composeUrl(boolean withDb, InstallData data) {
		String engine = data.getVariable(Constants.DBENGINE);
		String host = data.getVariable(Constants.DBHOST);
		String port = data.getVariable(Constants.DBPORT);
		String database = data.getVariable(Constants.DBDATABASE);
		String instance = data.getVariable(Constants.DBINSTANCE);

		String url = "jdbc:";

		url = "jdbc:";
		if (engine.equals("mysql"))
			url += "mysql://" + host.trim() + ":" + port.trim() + (withDb ? "/" + database.trim() : "");
		// else if
		// (engine.getSelectedItem().toString().contains(DATABASES[2]))
		// url += "postgresql://" + host.getText().trim() + ":" +
		// port.getText().trim() + "/"
		// + database.getText().trim();
		else if (engine.equals("oracle"))
			url += "oracle:thin:@" + host.trim() + ":" + port.trim() + (withDb ? ":" + database.trim() : "");
		else if (engine.equals("mssql"))
			url += "jtds:sqlserver://" + host.trim() + ":" + port.trim() + (withDb ? "/" + database.trim() : "")
					+ (withDb ? (";instance=" + instance.trim()) : "");
		return url;
	}

	private boolean checkDbConnection(InstallData data) {
		boolean manualUrl = "true".equals(data.getVariable(Constants.DBMANUALURL).trim());

		String username = data.getVariable(Constants.DBUSERNAME).trim();
		String password = data.getVariable(Constants.DBPASSWORD).trim();
		String driver = data.getVariable(Constants.DBDRIVER);
		String validationQuery = data.getVariable(Constants.DBQUERY);
		String database = data.getVariable(Constants.DBDATABASE).trim();
		String url = data.getVariable(Constants.DBURL);

		if (manualUrl) {
			boolean connected = checkDbConnection(url, username, password, driver, validationQuery);
			if (!connected)
				url = url.replace(database, "");
		} else {
			url = composeUrl(true, data);
			boolean connected = checkDbConnection(url, username, password, driver, validationQuery);
			if (!connected)
				url = composeUrl(false, data);
		}
		// Try the URL without the database specification
		return checkDbConnection(url, username, password, driver, validationQuery);
	}

	public static boolean checkDbConnection(String url, String username, String password, String driver,
			String validationQuery) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		// Try the exact URL with the database specification
		try {
			Log.info("Validating connection to " + url, null);

			@SuppressWarnings("unused")
			Driver d = (Driver) Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, username, password);

			stmt = conn.createStatement();
			stmt.execute(validationQuery);
			rs = stmt.getResultSet();
			if (!rs.next())
				throw new Exception("Unable to execute the validation query " + validationQuery);

			return true;
		} catch (Throwable e) {
			Log.info("Database not connected perhaps the schema doesnt exist yet", null);
			return false;
		} finally {
			try {
				rs.close();
			} catch (Throwable rse) {
			}
			try {
				stmt.close();
			} catch (Throwable sse) {
			}
			try {
				conn.close();
			} catch (Throwable cse) {
			}
		}
	}

	public static Map<String, String[]> getDbDefaults() {
		return dbDefaults;
	}
}