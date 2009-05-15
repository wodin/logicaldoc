package com.logicaldoc.testbench;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a complete test environment. Initially generating a specific number
 * of files inside a root directory. After this, makes the database population
 * searching files and folder in the root directory.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class TestBench {

	protected static Log log = LogFactory.getLog(TestBench.class);

	private GenerateFiles genFiles;

	private PopulateDatabase popDatabase;

	private PopulateUsers popUsers;

	private PopulateIndex popIndex;

	private PrepareFileSystem prepareFilesystem;

	public TestBench() throws IOException {
		super();

		Properties conf = new Properties();
		conf.load(this.getClass().getResourceAsStream("/conf.properties"));

		Properties context = new Properties();
		context.load(new FileInputStream(conf.getProperty("logicaldoc.contextDir")
				+ "/WEB-INF/classes/context.properties"));

		genFiles = new GenerateFiles();
		genFiles.setLogicalDocLayout(true);
		File docsRoot = new File(context.getProperty("conf.docdir") + "/tmp/5");
		docsRoot.mkdirs();
		docsRoot.mkdir();
		genFiles.setRootFolder(new File(context.getProperty("conf.docdir") + "/tmp/5"));

		popDatabase = new PopulateDatabase();
		popDatabase.setJdbcClass(context.getProperty("jdbc.driver"));
		popDatabase.setJdbcUrl(context.getProperty("jdbc.url"));
		popDatabase.setUsername(context.getProperty("jdbc.username"));
		popDatabase.setPassword(context.getProperty("jdbc.password"));
		popDatabase.setRootFolder(docsRoot);

		popUsers = new PopulateUsers();
		popUsers.setJdbcClass(context.getProperty("jdbc.driver"));
		popUsers.setJdbcUrl(context.getProperty("jdbc.url"));
		popUsers.setUsername(context.getProperty("jdbc.username"));
		popUsers.setPassword(context.getProperty("jdbc.password"));

		popIndex = new PopulateIndex();
		File indexFolder = new File(context.getProperty("conf.indexdir") + "/" + popDatabase.getLanguage());
		popIndex.setIndexFolder(indexFolder);
		popIndex.setTempFolder(genFiles.getRootFolder());
		popIndex.setRootFolder(new File(context.getProperty("conf.docdir")));

		prepareFilesystem = new PrepareFileSystem();
		prepareFilesystem.setRootFolder(popIndex.getRootFolder());
		prepareFilesystem.setTempFolder(popIndex.getTempFolder());
		
	}

	public static void main(String[] args) throws IOException {
		TestBench testBench = new TestBench();

		String phase = "all";
		if (args.length > 0)
			phase = args[0];

		if (phase.equals("all") || phase.equals("files"))
			testBench.generateFiles();
		if (phase.equals("all") || phase.equals("database"))
			testBench.populateDatabase();
		if (phase.equals("all") || phase.equals("users"))
			testBench.populateUsers();
		if (phase.equals("all") || phase.equals("index"))
			testBench.populateIndex();
		if (phase.equals("all") || phase.equals("filesystem"))
			testBench.prepareFilesystem();

	}

	/**
	 * Launches the files generation
	 */
	private void generateFiles() throws IOException {
		genFiles.generate();
	}

	/**
	 * Launches the database population
	 */
	private void populateDatabase() {
		popDatabase.populate();
	}

	/**
	 * Launches the users population
	 */
	private void populateUsers() {
		popUsers.populate();
	}

	/**
	 * Launches the index population
	 */
	private void populateIndex() {
		popIndex.populate();
	}

	/**
	 * Launches the file system prepare
	 */
	private void prepareFilesystem() {
		prepareFilesystem.prepare();
	}
}