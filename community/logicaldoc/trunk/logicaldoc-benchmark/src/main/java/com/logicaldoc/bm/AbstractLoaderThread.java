package com.logicaldoc.bm;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.cache.EhCacheAdapter;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * A description of what the remote loader should do.
 */
public abstract class AbstractLoaderThread extends Thread {
	
	private static Log log = LogFactory.getLog(AbstractLoaderThread.class);

	protected NumberFormat formatter = new DecimalFormat("Loader_00000000000");

	protected final LoaderSession session;
	protected final String loaderName;
	protected final long testTotal;

	private AtomicBoolean mustStop;
	protected Random random;

	// Statistics
	private int statCount = 0; // Total number of successful iterations
	private long statTotalMs = 0; // Total execution time
	private int statErrors = 0; // Total errors
	protected int testCount = 0;
    
    private static EhCacheAdapter<String, Long> pathCache;

    static
    {
        System.setProperty(CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "TRUE");
        URL url = LoaderUploadThread.class.getResource("/com/logicaldoc/bm/loader-ehcache.xml");
        CacheManager cacheManager = CacheManager.create(url);
        Cache cache = cacheManager.getCache("com.logicaldoc.bm.LoaderUploadThread.PathCache");

        pathCache = new EhCacheAdapter<String, Long>();
        pathCache.setCache(cache);
    }

	public AbstractLoaderThread(LoaderSession session, String loaderName, long testTotal) {

		super(LoaderSession.THREAD_GROUP, "LoaderThread-" + loaderName);

		this.session = session;
		this.loaderName = loaderName;
		this.testTotal = testTotal < 1 ? Integer.MAX_VALUE : testTotal;

		this.mustStop = new AtomicBoolean(false);
		this.random = new Random();

 		this.testCount = 0;
		this.statCount = 0;
		this.statTotalMs = 0;	
	}

	/**
	 * Notify the running thread to exit at the first available opportunity.
	 */
	public void setStop() {
		mustStop.set(true);
	}

	@Override
	public void run() {
		testCount = 0;
		long startTime = System.currentTimeMillis();

		try {
			while (!mustStop.get()) {
				try {
					// Choose a server
					// int serverCount = session.getRemoteServers().size();
					// int serverIndex = random.nextInt(serverCount);
					// LoaderServerProxy serverProxy =
					// session.getRemoteServers().get(serverIndex);
					LoaderServerProxy serverProxy = session.getRemoteServer();

					// Choose a working root node
					long rootNode = session.getRootFolder();

					doLoading(serverProxy, rootNode);
					statCount++;

					// Note: this piece of code is here to allow correctly
					// comparation with the competitors, do not remove
					// Have we done this enough?
					testCount++;
					if (testCount >= testTotal) {
						break;
					}
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					statErrors++;
				} finally {
					statTotalMs = System.currentTimeMillis() - startTime;
					// testCount++;
				}
			}
		} finally {
			statTotalMs = System.currentTimeMillis() - startTime;
			log.info(getName() + " finished in ms: " + statTotalMs);
		}
	}

    /**
     * <pre>
     * NAME+36\tCOUNT          \tTOTAL TIME     \tAVERAGE TIME   \tPER SECOND     \tDESCRIPTION     
     * </pre>
     * 
     * @return          Returns the summary of the results
     */
    public String getSummary()
    {
        double statTotalSec = statTotalMs / 1000.0;
        double statPerSec = statCount / statTotalSec;
        double statAveSec = statTotalSec / statCount;
        // Summarize the results
        StringBuilder sb = new StringBuilder();
        sb
          .append(String.format("%40s", loaderName)).append("\t")
          .append(String.format("%15.0f", (float)statCount)).append("\t")
          .append(String.format("%15.3f", statTotalSec)).append("\t")
          .append(String.format("%15.3f", statAveSec)).append("\t")
          .append(String.format("%15.3f", statPerSec)).append("\t")
          .append("");
        return sb.toString();
    }

	/**
	 * @return a brief description of the loading
	 * @throws Exception
	 *             any exception will be handled
	 */
	protected abstract String doLoading(LoaderServerProxy serverProxy, long rootFolder) throws Exception;

	protected File getFile() throws Exception {
		return session.randomFile.getFile();
	}	

	public boolean isFinished() {
		return testCount >= testTotal;
	}	
	
    protected List<String> chooseFolderPath(long testLoadDepth)
    {
        int[] folderProfiles = session.getFolderProfiles();
        // We work through these until we get the required depth.
        // The root node is ignored as it acts as the search root
        List<String> path = new ArrayList<String>((int)testLoadDepth);
        for (int i = 1; i < testLoadDepth; i++)
        {
            int folderProfile = folderProfiles[i];
            int randomFolderId = random.nextInt(folderProfile);
            String name = String.format("folder-%05d", randomFolderId);
            path.add(name);
        }
        return path;
    }	
    
    /**
     * Creates or find the folders based on caching.
     */
    protected Long makeFoldersExactly(String ticket,
            LoaderServerProxy serverProxy, Long rootFolder, List<String> folderPath) throws Exception
    {
        // Iterate down the path, checking the cache and populating it as necessary
        Long currentParentFolderID = rootFolder;
        String currentKey = "";

        for (String aFolderPath : folderPath)
        {
            currentKey += ("/" + aFolderPath);
            // Is this there?
            Long folderID = pathCache.get(currentKey);
            if (folderID != null)
            {
                // Found it
                currentParentFolderID = folderID;
                // Step into the next level
                continue;
            }

            // It is not there, so create it
            try
            {
            	WSFolder newFolder = new WSFolder();
            	newFolder.setName(aFolderPath);
            	newFolder.setParentId(currentParentFolderID);
            	WSFolder folder = serverProxy.folderClient.create(ticket, newFolder);

            	currentParentFolderID = folder.getId();
            } catch (Exception e)
            {
                currentParentFolderID = pathCache.get(currentKey);
            }

            // Cache the new node
            pathCache.put(currentKey, currentParentFolderID);
        }
        // Done
        return currentParentFolderID;
    }	   
    
    
    
    /**
     * Creates or find the folders based on caching.
     */
    protected Long makeFoldersFromPath(String ticket,
            LoaderServerProxy serverProxy, Long rootFolder, List<String> folderPath) throws Exception
    {
        // Iterate down the path, checking the cache and populating it as necessary
        String currentKey = "";
        for (String aFolderPath : folderPath)
        {
        	currentKey += ("/" + aFolderPath);
        }
        
        Long nodeRef = pathCache.get(currentKey);
        
        // It is not there, so create it
        if (nodeRef == null) {
        	WSFolder folder = serverProxy.folderClient.createPath(ticket, rootFolder, currentKey);
        	
        	nodeRef = folder.getId();
           // Cache the new node
           pathCache.put(currentKey, nodeRef);
        }
        
        return nodeRef;
    }	       

}
