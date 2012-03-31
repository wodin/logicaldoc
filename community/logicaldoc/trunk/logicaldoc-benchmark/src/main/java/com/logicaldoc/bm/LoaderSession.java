package com.logicaldoc.bm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.benchmark.RandomFile;
import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.system.SystemClient;

public class LoaderSession {

	private static Log log = LogFactory.getLog(LoaderSession.class);
	
	public static final ThreadGroup THREAD_GROUP = new ThreadGroup("FileFolderRemoteLoader");

	private String username;
	private String password;
	private String name;
	private String wsUrl;		
	private File sourceDir;
	private int[] folderProfiles;
	private String language;		
	
	private LoaderServerProxy remoteServer;
    //private OutputStream outVerbose;
    private OutputStream outSummary;
    //private OutputStream outError;
	private long rootFolder;	
	private long startTime;

	protected RandomFile randomFile;
	
	public int[] getFolderProfiles() {
		return folderProfiles;
	}

	public String getName() {
		return name;
	}

	public LoaderServerProxy getRemoteServer() {
		return remoteServer;
	}

	public LoaderSession(String username, String password, String name, String wsUrl, long rootFolder, File sourceDir, int[] folderProfiles, String language) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.wsUrl = wsUrl;
		this.rootFolder = rootFolder;
		this.sourceDir = sourceDir;
		this.folderProfiles = folderProfiles;
		this.language = language;
	}

	/**
	 * Initialize the object before first use.
	 */
	public synchronized void initialize() throws Exception {

        if (remoteServer != null)
        {
            throw new RuntimeException("The client has already been initialized");
        }		
        remoteServer = LoaderSession.connect(wsUrl, username, password); 
		
	    // Get the source files
        this.randomFile = new RandomFile();
        randomFile.setSourceDir(sourceDir.getPath());
        
        // Construct output and error files
        long time = System.currentTimeMillis();
        //File fileVerbose = new File("./LoaderSession-" + name + "-"+ time + "-verbose.tsv");
        File fileSummary = new File("./LoaderSession-" + name + "-"+ time + "-summary.tsv");
        //File fileError = new File("./LoaderSession-" + name + "-"+ time + "-error.tsv");
        //outVerbose = new BufferedOutputStream(new FileOutputStream(fileVerbose));
        outSummary = new BufferedOutputStream(new FileOutputStream(fileSummary));
        //outError = new BufferedOutputStream(new FileOutputStream(fileError));		
		
		// Record the start time
		startTime = System.currentTimeMillis();
	}
	
    public synchronized void close()
    {
        //try { outVerbose.close(); } catch (Throwable e) {}
        //try { outSummary.close(); } catch (Throwable e) {}
        //try { outError.close(); } catch (Throwable e) {}
        
        //outVerbose = null;
        //outSummary = null;
        //outError = null;
        
        this.remoteServer.authClient.logout(remoteServer.ticket);
    }	

	private static LoaderServerProxy connect(String wsUrl, String username, String password) throws Exception {
		log.info("Connect to the server");
		
		LoaderServerProxy remoteServer = null;
		
		try {
			AuthClient auth = new AuthClient(wsUrl + "/services/Auth");
			DocumentClient documentClient = new DocumentClient(wsUrl + "/services/Document");
			FolderClient folderClient = new FolderClient(wsUrl + "/services/Folder");
			SystemClient systemClient = new SystemClient(wsUrl + "/services/System");
			SearchClient searchClient = new SearchClient(wsUrl + "/services/Search");
			log.info("Connection established");
			
			// Authenticate
            String ticket = auth.login(username, password);
			log.info("Created SID: " + ticket);            
			
	        // Store the service references
	        LoaderServerProxy lsp = new LoaderServerProxy(
	        		wsUrl,
	                ticket,	
	                auth,
	                folderClient,
	                documentClient,
	                systemClient,
	                searchClient);	
	        remoteServer = lsp;
		} catch (Throwable e) {
			log.error("Unable to initialize WebServices connection", e);
		}
		
        // Check that there is at least one server
        if (remoteServer == null)
        {
            throw new Exception("No remote servers are available");
        }		
		return remoteServer;
	}

	public long getRootFolder() {
		return rootFolder;
	}
	
    public static String getLineEnding()
    {
        return System.getProperty("line.separator", "\n");
    }
    
    public synchronized void logSummary(String msg)
    {
        if (outSummary == null)
        {
            return;
        }
        try
        {
            byte[] bytes = msg.getBytes("UTF-8");
            outSummary.write(bytes);
            outSummary.write(getLineEnding().getBytes("UTF-8"));
            outSummary.flush();
        }
        catch (Throwable e)
        {
            System.err.println("Failed to write message to summary file: " + e.getMessage());
        }
    }
    
    public String getSummary()
    {
        List<Integer> folderProfilesAsList = new ArrayList<Integer>(10);
        for (int folderProfile : folderProfiles)
        {
            folderProfilesAsList.add(Integer.valueOf(folderProfile));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Session name:     ").append(name).append(getLineEnding())
          .append("WS URL:         ").append(wsUrl).append(getLineEnding())
          .append("Root Folder: ").append(rootFolder).append(getLineEnding())
          .append("Folder Profiles:  ").append(folderProfilesAsList).append(getLineEnding())
          .append("Start Time:       ").append(new Date(startTime));
        return sb.toString();
    }

	public String getLanguage() {
		return language;
	}	
	
}
