package com.logicaldoc.webservice;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.hsqldb.util.SqlFile;
import org.hsqldb.util.SqlTool.SqlToolException;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.logicaldoc.util.io.FileUtil;

/**
 * Abstract test case for the Web Service module. This class initialises a test
 * database and prepares the spring test context.
 * <p>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class AbstractWebServiceTestCase extends TestCase {

	protected ApplicationContext context;

	protected DataSource ds;

	protected File tempDir = new File("target/tmp");

	protected File coreSchemaFile;

	protected File webServiceSchemaFile;

	protected File dataFile;

	private String userHome;

	static {
		System.setProperty("LOGICALDOC_REPOSITORY", "target");
	}

	@Before
	public void setUp() throws Exception {
		tempDir = new File("target/tmp");

		userHome = System.getProperty("user.home");
		System.setProperty("user.home", tempDir.getPath());
		context = new ClassPathXmlApplicationContext(new String[] { "/context.xml" });
		createTestDirs();
		createTestDatabase();
	}

	protected void createTestDirs() throws IOException {
		// Create test dirs
		try {
			if (tempDir.exists() && tempDir.isDirectory())
				FileUtils.deleteDirectory(tempDir);
		} catch (Exception e) {
		}
		tempDir.mkdirs();

		coreSchemaFile = new File(tempDir, "logicaldoc-core.sql");
		webServiceSchemaFile = new File(tempDir, "logicaldoc-webservice.sql");
		dataFile = new File(tempDir, "data.sql");

		// Copy sql files
		copyResource("/sql/logicaldoc-core.sql", coreSchemaFile.getCanonicalPath());
		copyResource("/sql/logicaldoc-webservice.sql", webServiceSchemaFile.getCanonicalPath());
		copyResource("/data.sql", dataFile.getCanonicalPath());
	}

	protected void copyResource(String classpath, String destinationPath) throws IOException {
		FileUtil.copyResource(classpath, new File(destinationPath));
	}

	@After
	public void tearDown() throws Exception {
		destroyDatabase();
		((AbstractApplicationContext) context).close();

		// Restore user home system property
		System.setProperty("user.home", userHome);
	}

	/**
	 * Destroys the in-memory database
	 */
	private void destroyDatabase() {
		Connection con = null;
		try {
			con = ds.getConnection();
			con.createStatement().execute("SHUTDOWN IMMEDIATELY");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Creates an in-memory test database
	 * 
	 * @throws SqlToolException
	 * 
	 */
	private void createTestDatabase() throws Exception {
		ds = (DataSource) context.getBean("DataSource");

		Connection con = null;
		try {
			con = ds.getConnection();

			// Load schema
			SqlFile sqlFile = new SqlFile(coreSchemaFile, false, null);
			sqlFile.execute(con, false);
			sqlFile = new SqlFile(webServiceSchemaFile, false, null);
			sqlFile.execute(con, false);

			// Load data
			sqlFile = new SqlFile(dataFile, false, null);
			sqlFile.execute(con, false);

			// Test the connection
			ResultSet rs = con.createStatement().executeQuery("select * from ld_menu where ld_id=1");
			rs.next();

			Assert.assertEquals(1, rs.getInt(1));
		} finally {
			if (con != null)
				con.close();
		}
	}

}
