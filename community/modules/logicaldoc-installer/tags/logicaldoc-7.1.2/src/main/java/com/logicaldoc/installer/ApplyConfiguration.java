package com.logicaldoc.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;
import com.logicaldoc.installer.util.FileUtil;
import com.logicaldoc.installer.util.Log;
import com.logicaldoc.installer.util.Replace;
import com.logicaldoc.util.config.ContextProperties;

public class ApplyConfiguration {
	public void run(AbstractUIProcessHandler handler, String[] args) {
		try {
			String rootDir = args[0].trim();
			String name = args[2].trim();
			String organization = args[3].trim();
			String email = args[4].trim();
			String website = args[5].trim();

			String dbms = "embedded".equals(args[6].trim()) ? "hsqldb" : args[6].trim();
			String dbdriver = args[7].trim();
			String dburl = args[8].trim();
			String dbuser = args[9].trim();
			String dbpassword = args[10].trim();
			String dbquery = args[11].trim();
			String dbdialect = args[12].trim();
			String dbport = args[14].trim();
			String dbdatabase = args[15].trim();

			if ("hsqldb".equals(dbms)) {
				dbdriver = "org.hsqldb.jdbc.JDBCDriver";
				dburl = ("jdbc:hsqldb:/" + rootDir.replaceAll("\\\\", "/") + "/repository/" + "/db/db").replaceAll(
						"//", "/");
				dbuser = "sa";
				dbpassword = "";
				dbquery = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
				dbdialect = "org.hibernate.dialect.HSQLDialect";
			}

			String httpPort = args[16].trim();
			String httpsPort = args[17].trim();
			String shutdownPort = args[18].trim();
			String architecture = args[19].trim();
			int memory = Integer.parseInt(args[20].trim());
			if (memory > 8000)
				memory = 8000;
			if ("32bit".equals(architecture) && memory > 1200)
				memory = 1200;

			String servicename = args[21].trim();
			String openoffice = args[22].trim();
			String convert = args[23].trim();
			String ghostscript = args[24].trim();
			String swftools = args[25].trim();
			String tesseract = args[26].trim();
			String clamscan = args[27].trim();
			String openssl = args[28].trim();

			String lang = "en";
			if (args[29] != null && !args[29].contains("$"))
				lang = args[29].trim();

			String setuppassword = "admin";
			if (args[30] != null && !args[30].contains("$"))
				setuppassword = args[30].trim();

			/*
			 * Save the configuration in the context.properties
			 */
			File contextFile = new File(rootDir + "/conf/context.properties");
			Log.info("Save configurations into " + contextFile.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + contextFile.getAbsolutePath(), false);
			ContextProperties config = new ContextProperties(contextFile);

			config.setProperty("LDOCHOME", rootDir);
			config.setProperty("id", UUID.randomUUID().toString());

			config.setProperty("reg.name", name);
			config.setProperty("reg.organization", organization);
			config.setProperty("reg.website", website);
			config.setProperty("reg.email", email);

			config.setProperty("jdbc.dbms", dbms);
			config.setProperty("jdbc.driver", dbdriver);
			config.setProperty("jdbc.url", dburl);
			config.setProperty("jdbc.username", dbuser);
			config.setProperty("jdbc.password", dbpassword);
			config.setProperty("jdbc.validationQuery", dbquery);
			config.setProperty("hibernate.dialect", dbdialect);

			config.setProperty("cluster.node.port", httpPort);
			config.setProperty("server.url", "http://my_server:" + httpPort);

			String repoDir = rootDir + "/repository/";
			config.setProperty("index.dir", repoDir + "index/");
			config.setProperty("conf.dbdir", repoDir + "db/");
			config.setProperty("conf.exportdir", repoDir + "impex/out/");
			config.setProperty("conf.importdir", repoDir + "impex/in/");
			config.setProperty("conf.logdir", repoDir + "logs/");
			config.setProperty("conf.plugindir", repoDir + "plugins/");
			config.setProperty("conf.userdir", repoDir + "users/");
			config.setProperty("store.1.dir", repoDir + "docs/");

			config.setProperty("swftools.path", swftools);
			config.setProperty("command.convert", convert);
			config.setProperty("command.gs", ghostscript);
			config.setProperty("command.tesseract", tesseract);
			config.setProperty("command.openssl", openssl);
			config.setProperty("openoffice.path", openoffice);
			config.setProperty("antivirus.command", clamscan);

			/*
			 * Setup some AcmeCAD settings
			 */
			config.setProperty("acmecad.command", new File(rootDir + "/acmecad/AcmeCADConverter.exe").getAbsolutePath());
			config.setProperty("acmecad.resource", new File(rootDir + "/acmecad/logicaldoc.ini").getAbsolutePath());
			config.write();

			/*
			 * Update the loader/build.xml used by external procedures
			 */
			File file = new File(rootDir + "/loader/build.xml");
			Log.info("Save configurations into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);

			try {
				Replace.replace(file.getAbsolutePath(), "database logicaldoc;", "database " + dbdatabase + ";");
			} catch (Throwable e) {
				Log.error("Exception while editing build properties", e);
			}

			/*
			 * Save the configuration in the server.xml
			 */
			file = new File(rootDir + "/tomcat/conf/server.xml");
			Log.info("Save configurations into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);

			String serverContent = readFileAsString(file);
			serverContent = serverContent.replaceFirst("port=\"9005\"", "port=\"" + shutdownPort + "\"");
			serverContent = serverContent.replaceFirst("port=\"8080\"", "port=\"" + httpPort + "\"");
			serverContent = serverContent.replaceAll("port=\"8443\"", "port=\"" + httpsPort + "\"");
			serverContent = serverContent.replaceAll("redirectPort=\"8443\"", "redirectPort=\"" + httpsPort + "\"");
			FileWriter out = new FileWriter(file);
			out.write(serverContent);
			out.close();

			/*
			 * Save the configuration in loader/build.properties
			 */
			file = new File(rootDir + "/loader/build.properties");
			Log.info("Save configurations into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);
			config = new ContextProperties(file);
			config.setProperty("lang.default", lang);
			config.write();

			// Save also the setting in open.bat
			file = new File(rootDir + "/bin/open.bat");
			String openBat = readFileAsString(file);
			openBat = openBat.replaceFirst("8080", httpPort);
			out = new FileWriter(file);
			out.write(openBat);
			out.close();

			/*
			 * Save the configuration in the service.bat
			 */
			file = new File(rootDir + "/tomcat/bin/service.bat");
			Log.info("Save configuration into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);
			Replace.replace(file.getAbsolutePath(), "SSERVICE", servicename);
			Replace.replace(file.getAbsolutePath(), "SSERVNAME", servicename);
			String serviceBat = readFileAsString(file);
			serviceBat = serviceBat.replaceAll("--JvmMx 900", "--JvmMx " + memory);
			out = new FileWriter(file);
			out.write(serviceBat);
			out.close();

			/*
			 * Save the configuration in the logicaldoc.bat and logicaldoc.sh
			 */
			file = new File(rootDir + "/bin/logicaldoc.bat");
			Log.info("Save configuration into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);
			Replace.replace(file.getAbsolutePath(), "-Xmx900m", "-Xmx" + memory + "m");
			file = new File(rootDir + "/bin/logicaldoc.sh");
			Log.info("Save configuration into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);
			Replace.replace(file.getAbsolutePath(), "-Xmx900m", "-Xmx" + memory + "m");

			Log.info("Using architecture: " + architecture, null);

			/*
			 * Save the configuration in the tomcat-users.xml
			 */
			file = new File(rootDir + "/tomcat/conf/tomcat-users.xml");
			Log.info("Save configurations into " + file.getAbsolutePath(), null);
			handler.logOutput("Save configurations into " + file.getAbsolutePath(), false);
			Replace.replace(file.getAbsolutePath(), "adminpwd", setuppassword);

			/*
			 * Replace the right Tomcat binaries
			 */
			if ("32bit".equals(architecture)) {
				File src = new File(args[0] + "/tomcat/bin/32/tcnative-1.dll");
				File dst = new File(args[0] + "/tomcat/bin/tcnative-1.dll");
				FileUtil.copyFile(src, dst);
				src = new File(args[0] + "/tomcat/bin/32/tomcat7.exe");
				dst = new File(args[0] + "/tomcat/bin/tomcat7.exe");
				FileUtil.copyFile(src, dst);
				if (memory > 1300)
					memory = 1300;
			}

			/*
			 * To be sure, make executable the bin scripts
			 */
			if (!FileUtil.isWindows()) {
				File[] files = new File(rootDir + "/bin").listFiles();
				for (File f : files)
					if (f.getName().contains("logicaldoc") || f.getName().endsWith(".sh")) {
						handler.logOutput("Make executable " + f.getName(), false);
						Log.info("Make executable " + f.getName(), null);
						f.setExecutable(true);
						Runtime.getRuntime().exec("chmod 777 " + f.getAbsolutePath());
					}
			}
		} catch (Throwable e) {
			Log.error(e.getMessage(), e);
		}
	}

	/**
	 * Reads a file into one string
	 * 
	 * @param file The file to be read
	 * @return The string containing the file content
	 * @throws java.io.IOException
	 */
	private String readFileAsString(File file) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}