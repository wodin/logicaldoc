package com.logicaldoc.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.logicaldoc.core.security.Folder;

/**
 * Loads the LogicalDOC instance with a population of documents
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public abstract class AbstractLoader {

	private static Log log = LogFactory.getLog(AbstractLoader.class);

	protected RandomFile randomFile;

	protected String folderProfile;

	protected long rootFolder = Folder.DEFAULTWORKSPACE;

	protected List<String> paths = new ArrayList<String>();

	protected List<Long> folderIds = new ArrayList<Long>();

	protected String url;

	protected String username;

	protected String password;

	protected String sid;

	protected int threads = 2;

	protected long iterations = 0L;

	protected long startTime;

	protected List<LoaderThread> loaders = new ArrayList<LoaderThread>();

	private Random generator = new Random();

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "/context.xml" });

		AbstractLoader loader = (AbstractLoader) context.getBean("Loader");
		loader.execute();
	}

	public void setRandomFile(RandomFile randomFile) {
		this.randomFile = randomFile;
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

		for (int i = 1; i <= n; i++) {
			String path = parent + "/" + formatter.format(paths.size() + 1);
			paths.add(path);
			folderIds.add(null);
			// System.out.println("Create folder " + path);
			preparePaths(path, subProfile);
		}
	}
	

	private void execute() {
		// Prepare the paths we will use to populate the database
		loaders.clear();
		paths.clear();
		preparePaths("", folderProfile);

		startTime = System.currentTimeMillis();

		try {
			// Initialize the system and connect to the server
			init();

			/*
			 * Prepare the threads
			 */
			for (int i = 0; i < threads; i++) {
				LoaderThread th = new LoaderThread("Loader-" + (i + 1), this.iterations);
				loaders.add(th);
			}

			/*
			 * Launch all the threads
			 */
			for (LoaderThread loadThread : loaders) {
				loadThread.start();
			}

			// Now lower this threads priority
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			/*
			 * Waiting the end of the job
			 */
			System.out.println("   Enter 'q' to quit.");
			System.out.println("   Enter 's' to dump a thread summary.");
			boolean alive = true;
			while (alive) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
				for (LoaderThread th : loaders) {
					if (!th.isAlive())
						alive = false;
				}

				try {
					int keyPress = System.in.read();

					if (keyPress == 'Q' || keyPress == 'q') {
						for (LoaderThread th : loaders)
							th.interrupt();
						alive = false;
						log.info("Requested stop");
					} else if (keyPress == 'S' || keyPress == 's') {
						printReport();
						log.info("Requested report print");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			log.info("All threads finished");
			log.info("Prepare the report");

			printReport();
		} finally {
			sid = null;
		}

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
	 */
	protected void init() {
		System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");
		log.info("Connect to the server");
		try {
			//iteration = 0;
			sid = null;
		} catch (Throwable e) {
			log.error("Unable to initialize", e);
		}
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	/**
	 * Gets a random path and retrieves the respective folder ID. If the folder
	 * doesn't exists it is created and the ID cached.
	 */
	protected Long getRandomFolder() {
		int index = generator.nextInt(paths.size());
		String path = AbstractLoader.this.paths.get(index);
		Long folder = folderIds.get(index);
		if (folder == null) {
			synchronized (AbstractLoader.this.paths) {
				try {
					folder = AbstractLoader.this.createFolder(path);
					folderIds.set(index, folder);
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					folder = null;
				}
			}
		}
		return folder;
	}

	protected abstract Long createFolder(String path);

	protected abstract Long createDocument(long folderId, String title, File file);

	protected class LoaderThread extends Thread {

		private NumberFormat formatter = new DecimalFormat("Loader_00000000000");

		private long testTotal;
		
		// Total number of successfull iterations
		private int statCount = 0;
		
		// Total execution time
		private long statTotalMs = 0;
		
		// Total errors
		private int statErrors = 0;

		public LoaderThread(String name, long testTotal) {
			super(name);
			this.testTotal = testTotal < 1 ? Integer.MAX_VALUE : testTotal;
			formatter = new DecimalFormat(name +"_00000000000");
		}

		@Override
		public void run() {
			int testCount = 0;
			long startTime = System.currentTimeMillis();

			try {
				while (sid != null) {
					try {												
						Long folder = getRandomFolder();
						if (folder == null) {
							throw new Exception("Error getting folder");
						}

						File file = randomFile.getFile();
						String title = formatter.format(testCount);

						Long docId = AbstractLoader.this.createDocument(folder, title, file);
						if (docId != null) {
							statCount++;
						} else {
							throw new Exception("Error creating document " + title);
						}	
						
						// Have we done this enough?
						testCount++;
						if (testCount > testTotal) {
							break;
						}
						
					} catch (Throwable ex) {
						log.error(ex.getMessage(), ex);
						statErrors++;
					} finally {
						statTotalMs = System.currentTimeMillis() - startTime;	
					}	
				}
			} finally {
				statTotalMs = System.currentTimeMillis() - startTime;
				log.info(getName() + " finished in ms: " +statTotalMs);
			}
		}

		public int getStatErrors() {
			return statErrors;
		}

		public double getStatTotalSec() {
			return statTotalMs / 1000.0;
		}

		public int getStatCount() {
			return statCount;
		}

		public double getStatAveSec() {
	        return getStatTotalSec() / statCount;
		}

		public double getStatPerSec() {
			return statCount / getStatTotalSec();
		}
	}

	public void setIterations(long iterations) {
		this.iterations = iterations;
	}

	private void printReport() {
		
		double statTotalMs = System.currentTimeMillis() - startTime;

		PrintStream out = System.out;

		out.print("\n");
		out.print("\n");
		out.print(StringUtils.leftPad("NAME", 9));
		out.print(StringUtils.leftPad("COUNT", 15));
		//out.print(StringUtils.leftPad("ERRORS", 15));
		out.print(StringUtils.leftPad("TOTAL TIME", 20));
		out.print(StringUtils.leftPad("AVERAGE TIME", 20));
		out.print(StringUtils.leftPad("PER SECOND", 12));

		float statCount = 0;
		int errors = 0;

		for (LoaderThread th : loaders) {
			statCount += th.getStatCount();
			//errors += th.getStatErrors();
		}
        double statTotalSec = statTotalMs / 1000.0;
        double statPerSec = statCount / statTotalSec;
        double statAveSec = statTotalSec / statCount;

		out.print("\n");
		out.print(StringUtils.leftPad("main", 9));
		out.print(StringUtils.leftPad(String.format("%15.0f", (float)statCount), 15));
		//out.print(StringUtils.leftPad(Long.toString(errors), 15));
		out.print(StringUtils.leftPad(String.format("%15.3f", statTotalSec), 20));
		out.print(StringUtils.leftPad(String.format("%15.3f", statAveSec), 20));
		out.print(StringUtils.leftPad(String.format("%15.3f", statPerSec), 12));
		

		for (LoaderThread th : loaders) {
			out.print("\n");
			out.print(StringUtils.leftPad(th.getName(), 9));
			out.print(StringUtils.leftPad(String.format("%15.0f", (float)th.getStatCount()), 15));
			//out.print(StringUtils.leftPad(Long.toString(th.getErrors()), 15));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatTotalSec()), 20));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatAveSec()), 20));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatPerSec()), 12));
		}
		out.print("\n");
		out.print("\n");
	}
}