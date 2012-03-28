package com.logicaldoc.benchmark;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
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
	private List<String> paths = new ArrayList<String>();
	private Long[] folderIds;
	
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
        
        this.randomFile = new RandomFile();
        randomFile.setSourceDir(sourceDir.getPath());

        ArrayIterator ai = new ArrayIterator(folderProfiles);
        String folderProfileStr = StringUtils.join(ai, ", ");
        
    	// Prepare the paths we will use to populate the database
		paths.clear();
		folderIds = null;
		preparePaths("", folderProfileStr);
		folderIds = new Long[paths.size()];
		Arrays.fill(folderIds, null);
		log.info("Prepared " + paths.size() + " paths");        

		
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

	private static LoaderServerProxy connect(String wsUrl, String username, String password) throws Exception {
		log.info("Connect to the server");
		
		LoaderServerProxy remoteServer = null;
		
		try {
			AuthClient auth = new AuthClient(wsUrl + "/services/Auth");
			DocumentClient documentClient = new DocumentClient(wsUrl + "/services/Document");
			FolderClient folderClient = new FolderClient(wsUrl + "/services/Folder");
			SystemClient systemClient = new SystemClient(wsUrl + "/services/System");
			log.info("Connection established");
			
			// Authenticate
            String ticket = auth.login(username, password);
			log.info("Created SID: " + ticket);            
			
	        // Store the service references
	        LoaderServerProxy lsp = new LoaderServerProxy(
	        		wsUrl,
	                ticket,
	                folderClient,
	                documentClient,
	                systemClient);	
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
	
	
	/**
	 * Prepares the paths that will be used during the population. This method
	 * is recursive, invoke it carefully.
	 * 
	 * @param parent The parent path
	 * @param profile The folder profile to be applied to children
	 */
	private void preparePaths(String parent, String profile) {
		
		if (StringUtils.isEmpty(profile))
			return;

		NumberFormat formatter = new DecimalFormat("Folder-000000");

		int index = profile.indexOf(',');
		int n = 0;
		if (index < 0)
			n = Integer.parseInt(profile.trim());
		else
			n = Integer.parseInt(profile.substring(0, index).trim());

		String subProfile = null;
		if (index > 0)
			subProfile = profile.substring(index + 1);

		for (int i = 0; i < n; i++) {
			String path = parent + "/" + formatter.format(i + 1);
			paths.add(path);
			// System.out.println("Create folder " + path);
			preparePaths(path, subProfile);
		}
	}

	public List<String> getPaths() {
		return paths;
	}
	
	public Long getFolderIds(int index) {
		return folderIds[index];
	}

	public void setFolderIds(int index, Long folder) {
		folderIds[index] = folder;
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
