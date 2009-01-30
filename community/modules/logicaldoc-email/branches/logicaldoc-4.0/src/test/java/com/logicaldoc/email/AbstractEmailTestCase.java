package com.logicaldoc.email;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.hsqldb.util.SqlFile;
import org.hsqldb.util.SqlTool.SqlToolException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Abstract test case for the Core module. This class initialises a test
 * database and prepares the spring test context.
 * <p>
 * All LogicalDOC's tests must extend this test case in order to find a ready and
 * accessible database.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public abstract class AbstractEmailTestCase extends TestCase {

	protected ApplicationContext context;

	protected DataSource ds;

	protected File tempDir = new File("target/tmp");

	protected File dbSchemaFile;

	protected File emailSchemaFile;
	
	protected File dataFile;

	private String userHome;

	static {
		System.setProperty("LOGICALDOC_REPOSITORY", "target");
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

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
		assertTrue(tempDir.exists() && tempDir.isDirectory());

		dbSchemaFile = new File(tempDir, "logicaldoc-core.sql");
		dataFile = new File(tempDir, "data.sql");
		emailSchemaFile = new File(tempDir, "logicaldoc-email.sql");

		// Copy sql files
		copyResource("/sql/logicaldoc-core.sql", dbSchemaFile.getCanonicalPath());
		copyResource("/sql/logicaldoc-email.sql", emailSchemaFile.getCanonicalPath());
		copyResource("/data.sql", dataFile.getCanonicalPath());
	}

	protected void copyResource(String classpath, String destinationPath) throws IOException {
		copyResource(classpath, new File(destinationPath));
	}

	/**
	 * Copy a resource from the classpath into a file
	 * 
	 * @param classpath The classpath specification
	 * @param out The target file
	 * @throws IOException
	 */
	protected void copyResource(String classpath, File out) throws IOException {
		InputStream is = new BufferedInputStream(this.getClass().getResource(classpath).openStream());
		OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
		try {
			for (;;) {
				int b = is.read();
				if (b == -1)
					break;
				os.write(b);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

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
	 * Constructor for AbstractCoreTest.
	 * 
	 * @param name
	 */
	public AbstractEmailTestCase(String name) {
		super(name);
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
			SqlFile sqlFile = new SqlFile(dbSchemaFile, false, null);
			sqlFile.execute(con, false);
			
			// Load email schema
			sqlFile = new SqlFile(emailSchemaFile, false, null);
			sqlFile.execute(con, false);

			// Load data
			sqlFile = new SqlFile(dataFile, false, null);
			sqlFile.execute(con, false);

			// Test the connection
			ResultSet rs = con.createStatement().executeQuery("select * from ld_menu where ld_id=1");
			rs.next();

			assertEquals(1, rs.getInt(1));
		} finally {
			if (con != null)
				con.close();
		}
	}
}