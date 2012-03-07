package com.logicaldoc.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class Loader {

	private static Log log = LogFactory.getLog(Loader.class);

	protected RandomFile randomFile;

	protected String folderProfile;

	protected long rootFolder = Folder.DEFAULTWORKSPACE;

	protected List<String> paths = new ArrayList<String>();

	protected Long[] folderIds = null;

	protected String url;

	protected String username;

	protected String password;

	protected String sid;

	protected int threads = 2;

	protected long iterations = 0L;

	protected long startTime;

	protected List<LoaderThread> loaders = new ArrayList<LoaderThread>();

	private Random generator = new Random();

	private boolean finished = false;

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "/context.xml" });

		Loader loader = (Loader) context.getBean("Loader");
		loader.execute();
		
		System.exit(0);
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

		for (int i = 0; i < n; i++) {
			String path = parent + "/" + formatter.format(i + 1);
			paths.add(path);
			// System.out.println("Create folder " + path);
			preparePaths(path, subProfile);
		}
	}

	private void execute() {
		// Prepare the paths we will use to populate the database
		loaders.clear();
		paths.clear();
		folderIds = null;
		preparePaths("", folderProfile);
		folderIds = new Long[paths.size()];
		Arrays.fill(folderIds, null);
		log.info("Prepared " + paths.size() + " paths");

		// This threads listen for input
		Thread consoleThread = new Thread() {

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
						for (LoaderThread th : loaders)
							th.interrupt();
						finished = true;
						printReport();
						log.info("Requested stop");
						break;
					} else if (keyPress == 'S' || keyPress == 's') {
						printReport();
						log.info("Requested report print");
					}
				}
			}
		};

		consoleThread.setPriority(Thread.MIN_PRIORITY);
		consoleThread.start();

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

			while (!finished) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
				for (LoaderThread th : loaders) {
					finished = true;
					if (!th.isFinished()) {
						finished = false;
						break;
					}
				}
			}

			consoleThread.interrupt();

			log.info("All threads finished");
			log.info("Prepare the report");

			printReport();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			end();
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
			finished = false;
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
		String path = paths.get(index);
		Long folder = folderIds[index];
		if (folder == null) {
			try {
				folder = createFolder(path);
				folderIds[index] = folder;
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				folder = null;
			}
		}
		return folder;
	}

	protected abstract Long createFolder(String path);

	protected abstract Long createDocument(long folderId, String title, File file);

	protected abstract void end();

	protected class LoaderThread extends Thread {

		private NumberFormat formatter = new DecimalFormat("Loader_00000000000");

		private long testTotal;

		// Total number of successful iterations
		private int statCount = 0;

		// Total number of iterations
		private int testCount = 0;

		// Total execution time
		private long statTotalMs = 0;

		// Total errors
		private int statErrors = 0;

		public LoaderThread(String name, long testTotal) {
			super(name);
			this.testTotal = testTotal < 1 ? Integer.MAX_VALUE : testTotal;
			formatter = new DecimalFormat(name + "_00000000000");
		}

		@Override
		public void run() {
			testCount = 0;
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

						Long docId = Loader.this.createDocument(folder, title, file);
						if (docId != null) {
							statCount++;
						} else {
							throw new Exception("Error creating document " + title);
						}

						// Note: this piece of code is here to allow correctly
						// comparation with the competitors, do not remove
						// Have we done this enough?
						testCount++;
						if (testCount >= testTotal) {
							log.info("end");
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

		public boolean isFinished() {
			return testCount >= testTotal;
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
		// out.print(StringUtils.leftPad("ERRORS", 15));
		out.print(StringUtils.leftPad("TOTAL TIME", 20));
		out.print(StringUtils.leftPad("AVERAGE TIME", 20));
		out.print(StringUtils.leftPad("PER SECOND", 12));

		float statCount = 0;
		int errors = 0;

		for (LoaderThread th : loaders) {
			statCount += th.getStatCount();
			// errors += th.getStatErrors();
		}
		double statTotalSec = statTotalMs / 1000.0;
		double statPerSec = statCount / statTotalSec;
		double statAveSec = statTotalSec / statCount;

		out.print("\n");
		out.print(StringUtils.leftPad("main", 9));
		out.print(StringUtils.leftPad(String.format("%15.0f", (float) statCount), 15));
		// out.print(StringUtils.leftPad(Long.toString(errors), 15));
		out.print(StringUtils.leftPad(String.format("%15.3f", statTotalSec), 20));
		out.print(StringUtils.leftPad(String.format("%15.3f", statAveSec), 20));
		out.print(StringUtils.leftPad(String.format("%15.3f", statPerSec), 12));

		for (LoaderThread th : loaders) {
			out.print("\n");
			out.print(StringUtils.leftPad(th.getName(), 9));
			out.print(StringUtils.leftPad(String.format("%15.0f", (float) th.getStatCount()), 15));
			// out.print(StringUtils.leftPad(Long.toString(th.getErrors()),
			// 15));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatTotalSec()), 20));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatAveSec()), 20));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatPerSec()), 12));
		}
		out.print("\n");
		out.print("\n");
	}
}