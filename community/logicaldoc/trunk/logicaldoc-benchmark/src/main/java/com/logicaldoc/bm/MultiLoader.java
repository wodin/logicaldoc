package com.logicaldoc.bm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.logicaldoc.core.security.Folder;

/**
 * Loads the LogicalDOC instance with a population of documents
 * 
 * @author Marco Meschieri - Logical Objects
 * @author Alessandro Gasparini - Logical Objects
 * @since 6.5
 */
public class MultiLoader {

	private static Log log = LogFactory.getLog(MultiLoader.class);

	protected String folderProfile;

	protected long rootFolder = Folder.DEFAULTWORKSPACE;

	protected String url;

	protected String username;

	protected String password;
	
	protected String language = "en";

	public void setLanguage(String language) {
		this.language = language;
	}	

	private static final String COLUMNS_VERBOSE = String.format("%40s\t%15s\t%15s\t%15s\t%15s\t%15s", "NAME", "COUNT",
			"TIME", "AVERAGE TIME", "PER SECOND", "DESCRIPTION");
	private static final String COLUMNS_SUMMARY = String.format("%40s\t%15s\t%15s\t%15s\t%15s\t%15s", "NAME", "COUNT",
			"TOTAL TIME", "AVERAGE TIME", "PER SECOND", "DESCRIPTION");

	protected long startTime;

    private LoaderSession session;
    private AbstractLoaderThread[] loaderThreads;

	private String sourceDir;

	private List<TestDefinition> testList;

	private Thread consoleThread;
	
	private boolean finished = false;

	public void setTestList(List<TestDefinition> testList) {
		this.testList = testList;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	
	
	public static void main(String[] args) {

		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "/context.xml" });

			MultiLoader app = (MultiLoader) context.getBean("MultiLoader");

			// Initialize
			app.initialize();

			// Run
			app.start();
			
			// Now lower this threads priority
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			
			while (!app.finished) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
				if (app.allThreadsFinished()) {
					System.out.println("The test is complete.");
					app.finished = true;
					break;
				}
			}			

			// Finish off
 			app.stopAll();
 			app.dumpThreadSummaries();
 			
		} catch (LoaderClientException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (Throwable e) {
			System.err.println("A failure prevented proper execution.");
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}	
	
	private boolean allThreadsFinished() {
		for (AbstractLoaderThread alt : loaderThreads) {
			if (!alt.isFinished()) 
				return false;
		}
		return true;
	}

	public synchronized void start() {
		
        if (session == null || loaderThreads == null)
        {
            throw new RuntimeException("Application not initialized");
        }     
        
        consoleThread.start();
        
        // Fire up the threads
        for (Thread thread : loaderThreads)
        {
            thread.start();
        }		
	}

	private void initConsole() {
		
		consoleThread = new Thread() {
			
			@Override
			public void run() {
				/*
				 * Waiting the end of the job
				 */
				System.out.println("   Enter 'q' to quit.");
				System.out.println("   Enter 's' to dump a thread summary.");

				while (!finished) {
					int keyPress = 0;
					try {
						keyPress = System.in.read();
					} catch (IOException e) {
						break;
					}

					if (keyPress == 'Q' || keyPress == 'q') {
						log.info("Requested stop");
						stopThreads();
						finished = true;						
						break;
					} else if (keyPress == 'S' || keyPress == 's') {
						dumpThreadSummaries();
					}
				}
			}
		};

		consoleThread.setPriority(Thread.MIN_PRIORITY);       
	}

	protected synchronized void stopThreads() {
		// Stop the threads
		for (AbstractLoaderThread thread : loaderThreads) {
			thread.setStop();
		}
		// Now join each thread to make sure they all finish their current operation
		for (AbstractLoaderThread thread : loaderThreads) {
			// Notify any waits
			synchronized (thread) {
				thread.notifyAll();
			}
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}				
	}

	public void dumpThreadSummaries() {
		System.out.println("");
		System.out.println(COLUMNS_SUMMARY);
		// Dump each thread's summary
		for (AbstractLoaderThread thread : loaderThreads) {
			String summary = thread.getSummary();
			System.out.println(summary);
		}
	}
	
	public synchronized void stopAll() {
		
		consoleThread.interrupt();
				
		// Print and Log each thread's summary
		for (AbstractLoaderThread thread : loaderThreads) {
			String summary = thread.getSummary();
			session.logSummary(summary);
		}
		session.close();
	}	

	public void setFolderProfile(String folderProfile) {
		this.folderProfile = folderProfile;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Initializes resources and connects to the WebService
	 * @throws Exception 
	 */
	public synchronized void initialize() throws Exception {
		System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");

        if (session != null || loaderThreads != null)
        {
            throw new RuntimeException("Application already initialized");
        }
        
        session = MultiLoader.makeSession(username, password, url, rootFolder, sourceDir, folderProfile, language);
        loaderThreads = MultiLoader.makeThreads(session, testList);
        
        // Log the initial summaries
        String summary = session.getSummary();
        session.logSummary(summary);
        
        // Header the outputs
        session.logSummary(LoaderSession.getLineEnding());
        session.logSummary(COLUMNS_SUMMARY);
        
        // Initialize control console
        initConsole();           
	}
	
	
	private static AbstractLoaderThread[] makeThreads(LoaderSession session, List<TestDefinition> testList) throws Exception {
		
		ArrayList<AbstractLoaderThread> alThreads = new ArrayList<AbstractLoaderThread>(3);
		
		// Iterate through the list
		for (TestDefinition test : testList) {
			
			int threadCount = test.testCount;
			String type = test.type;
			String name = test.name;
			long iterations = test.iterations;
			long testDepth = test.depth;
			
			// Construct
	        for (int i = 0; i < threadCount; i++)
	        {        	
	            AbstractLoaderThread thread = null;
	            if (type.equals("upload"))
	            {
	                thread = new LoaderUploadThread(session, name, iterations, testDepth);
	            }
	            else if (type.equals("totals"))
	            {
	                thread = new LoaderTotalsThread(session, name, iterations, testDepth);
	            } else if (type.equals("listFolders"))
	            {
	                thread = new LoaderListFoldersThread(session, name, iterations, testDepth);
	            } else if (type.equals("searchFullText"))
	            {
	            	List<String> queries = loadFullTextQueries();
	                thread = new LoaderSearchFullText(session, name, iterations, testDepth, queries);
	            }
	            else
	            {
	                throw new RuntimeException("Unknown test type: " + name);
	            }                    
	            alThreads.add(thread);
	        }
			
		}

		// Done
        AbstractLoaderThread[] ret = new AbstractLoaderThread[alThreads.size()];
        return alThreads.toArray(ret);        
	}

	private static List<String> loadFullTextQueries() {

		String config = "conf/query.properties";
		List<String> aaaa = new ArrayList<String>(3);

		try {
			File propertiesFile = new File(config);
			if (!propertiesFile.exists()) {
				System.err.println("Unable to find config file: " + config);
			}
			Properties properties = new Properties();
			properties.load(new FileInputStream(propertiesFile));
			
			Iterator<Object> it = properties.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String val = properties.getProperty(key);
				aaaa.add((String) val);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return aaaa;
	}

	/**
     * Factory method to construct a session using the given properties.
	 * @param randomFile 
	 * @throws Exception 
     */
	private static LoaderSession makeSession(String username, String password, String wsUrl, long rootFolder, String sourceDirStr, String folderProfilesStr, String language) throws Exception {
		
		File sourceDir = new File(sourceDirStr);
		
		log.error("folderProfilesStr: " +folderProfilesStr);
		StringTokenizer tokenizer = new StringTokenizer(folderProfilesStr, ",");
        ArrayList<Integer> folderProfilesList = new ArrayList<Integer>(5);
        while (tokenizer.hasMoreTokens())
        {
            String folderProfileStr = tokenizer.nextToken().trim();
            Integer folderProfile = Integer.valueOf(folderProfileStr);
            folderProfilesList.add(folderProfile);
        }
        int[] folderProfiles = new int[folderProfilesList.size()];
        for (int i = 0; i < folderProfiles.length; i++)
        {
            folderProfiles[i] = folderProfilesList.get(i);
        }
        if (folderProfiles.length == 0 || folderProfiles[0] != 1)
        {
            throw new LoaderClientException(
                    "'load.folderprofile' must always start with '1', " +
                    "which represents the root of the hierarchy, and have at least one other value.  " +
                    "E.g. '1, 3'");
        }	
        
        log.error("folderProfilesStr.length(): " +folderProfilesStr.length());
			
        // Construct
        LoaderSession session = new LoaderSession(        		
                username,
                password,
                "session.name",
                wsUrl,
                rootFolder,
                sourceDir,
                folderProfiles, 
                language);
        
        // Initialize the session
        session.initialize();
        
        // Done
        return session;
	}

}