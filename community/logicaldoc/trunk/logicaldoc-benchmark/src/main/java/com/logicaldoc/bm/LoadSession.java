package com.logicaldoc.bm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.system.SystemClient;

/**
 * Models a load session during which the LogicalDOC server will be heavy
 * loaded.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 6.5
 */
public class LoadSession {

	private static Log log = LogFactory.getLog(LoadSession.class);

	public static final ThreadGroup THREAD_GROUP = new ThreadGroup("FileFolderRemoteLoader");

	private String username;

	private String password;

	private String url;

	private String language = "en";

	private LoadServerProxy remoteServer;

	private OutputStream outSummary;

	private long startTime;

	public LoadSession() {

	}

	public LoadServerProxy getRemoteServer() {
		return remoteServer;
	}

	/**
	 * Connects to the server before first use.
	 */
	public synchronized void connect() throws Exception {

		if (remoteServer != null) {
			throw new RuntimeException("The client has already been initialized");
		}
		remoteServer = LoadSession.connect(url, username, password);

		// Construct output and error files
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		long time = System.currentTimeMillis();

		File fileSummary = new File("./LoadSession-" + time + ".tsv");

		outSummary = new BufferedOutputStream(new FileOutputStream(fileSummary));

		// Record the start time
		startTime = System.currentTimeMillis();
	}

	public synchronized void close() {
		this.remoteServer.authClient.logout(remoteServer.ticket);
	}

	private static LoadServerProxy connect(String url, String username, String password) throws Exception {
		log.info("Connect to the server");

		LoadServerProxy remoteServer = null;

		try {
			AuthClient auth = new AuthClient(url + "/services/Auth");
			DocumentClient documentClient = new DocumentClient(url + "/services/Document");
			FolderClient folderClient = new FolderClient(url + "/services/Folder");
			SystemClient systemClient = new SystemClient(url + "/services/System");
			SearchClient searchClient = new SearchClient(url + "/services/Search");
			log.info("Connection established");

			// Authenticate
			String ticket = auth.login(username, password);
			log.info("Created SID: " + ticket);

			// Store the service references
			LoadServerProxy lsp = new LoadServerProxy(url, ticket, auth, folderClient, documentClient, systemClient,
					searchClient);
			remoteServer = lsp;
		} catch (Throwable e) {
			log.error("Unable to initialize WebServices connection", e);
		}

		// Check that there is at least one server
		if (remoteServer == null) {
			throw new Exception("No remote servers are available");
		}
		return remoteServer;
	}

	public static String getLineEnding() {
		return System.getProperty("line.separator", "\n");
	}

	public synchronized void logSummary(String msg) {
		if (outSummary == null) {
			return;
		}
		try {
			byte[] bytes = msg.getBytes("UTF-8");
			outSummary.write(bytes);
			outSummary.write(getLineEnding().getBytes("UTF-8"));
			outSummary.flush();
		} catch (Throwable e) {
			System.err.println("Failed to write message to summary file: " + e.getMessage());
		}
	}

	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("Server URL:         ").append(url).append(getLineEnding()).append(getLineEnding())
				.append("Start Time:       ").append(new Date(startTime));
		return sb.toString();
	}

	public String getLanguage() {
		return language;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}