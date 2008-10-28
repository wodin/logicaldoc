package com.logicaldoc.testbench;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Makes a complete test bench. Initially generating a specific number of files
 * inside a root directory. After this, makes the database population searching
 * files and folder in the root directory.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 * 
 */
public class TestBench {

	protected static Log log = LogFactory.getLog(TestBench.class);

	private GenerateFiles genFiles;

	private PopulateDatabase popDatabase;

	public TestBench() throws IOException {
		super();

		Properties conf = new Properties();
		conf.load(this.getClass().getResourceAsStream("/conf.properties"));

		Properties context = new Properties();
		context.load(new FileInputStream(conf.getProperty("testbench.logicaldoc.contextDir")
				+ "/WEB-INF/classes/context.properties"));

		genFiles = new GenerateFiles();
		genFiles.setLogicalDocLayout(true);
		File docsRoot = new File(context.getProperty("conf.docdir") + "/5");
		docsRoot.mkdirs();
		docsRoot.mkdir();
		genFiles.setRootFolder(new File(context.getProperty("conf.docdir") + "/5"));

		popDatabase = new PopulateDatabase();
		popDatabase.setJdbcClass(context.getProperty("jdbc.driver"));
		popDatabase.setJdbcUrl(context.getProperty("jdbc.url"));
		popDatabase.setUsername(context.getProperty("jdbc.username"));
		popDatabase.setPassword(context.getProperty("jdbc.password"));
		popDatabase.setRoot(docsRoot);
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
		popDatabase.generate();
	}

}
