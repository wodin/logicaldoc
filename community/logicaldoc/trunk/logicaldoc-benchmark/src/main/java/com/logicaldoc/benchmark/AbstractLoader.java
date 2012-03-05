package com.logicaldoc.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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

	protected long iteration = 0;

	protected long iterations;

	protected long startTime;

	protected CyclicBarrier barrier;

	protected List<Loader> loaders = new ArrayList<Loader>();

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

	private synchronized long next() {
		iteration++;
		return iteration;
	}

	private void execute() {
		// Prepare the paths we will use to populate the database
		iteration = 0;
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
				Loader th = new Loader("Loader-" + (i + 1));
				loaders.add(th);
			}

			/*
			 * Launch all the threads
			 */
			for (Loader loadThread : loaders) {
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
				for (Loader th : loaders) {
					if (!th.isAlive())
						alive = false;
				}

				try {
					int keyPress = System.in.read();

					if (keyPress == 'Q' || keyPress == 'q') {
						for (Loader th : loaders)
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
			iteration = 0;
			sid = null;
			barrier = new CyclicBarrier(threads);
		} catch (Throwable e) {
			log.error("Unable to initialize", e);
		}
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	/**
	 * Gets a random path and retrieves the respective folder ID. If the folder
	 * doesn't exists it is cre
	 * ated and the ID cached.
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

	protected class Loader extends Thread {

		private NumberFormat formatter = new DecimalFormat("Document-00000000000");

		// Statistics
		private int statCount = 0;
		private long statTotalMs = 0;
		private int statErrors = 0;

		//private long average = 0;
		//private long sum = 0;

		public Loader(String name) {
			super(name);
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();

			try {
				while (sid != null && AbstractLoader.this.iteration < AbstractLoader.this.iterations) {
					//long t = System.currentTimeMillis();

					try {
						Long folder = getRandomFolder();

						if (folder == null) {
							throw new Exception("Error getting folder");
						}

						try {
							barrier.await();
						} catch (InterruptedException e) {
						} catch (BrokenBarrierException e) {
						}

						File file = randomFile.getFile();
						long c = next();
						String title = formatter.format(c);

						Long docId = AbstractLoader.this.createDocument(folder, title, file);

						if (docId != null) {
							statCount++;
						} else {
							throw new Exception("Error creating document " + title);
						}

						try {
							barrier.await();
						} catch (InterruptedException e) {
						} catch (BrokenBarrierException e) {
						}
					} catch (Throwable ex) {
						log.error(ex.getMessage(), ex);
						statErrors++;
						continue;
					} finally {
//						long now = System.currentTimeMillis();
//						statTotalMs = now - startTime;
//						long time = now - t;
//						sum += time;
//						average = Math.round((double) sum / statCount);
						statTotalMs = System.currentTimeMillis() - startTime;
					}
				}
			} finally {
//				statTotalMs = System.currentTimeMillis() - startTime;
//				average = Math.round((double) sum / statCount);
				statTotalMs = System.currentTimeMillis() - startTime;
				log.info(getName() + " finished");
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

		for (Loader th : loaders) {
			statCount += th.getStatCount();
			errors += th.getStatErrors();
		}
        double statTotalSec = statTotalMs / 1000.0;
        double statPerSec = statCount / statTotalSec;
        double statAveSec = statTotalSec / statCount;

		//DecimalFormat nb = new DecimalFormat("#####.###");

		out.print("\n");
		out.print(StringUtils.leftPad("main", 9));
		out.print(StringUtils.leftPad(String.format("%15.0f", (float)statCount), 15));
		//out.print(StringUtils.leftPad(Long.toString(errors), 15));
		out.print(StringUtils.leftPad(String.format("%15.3f", statTotalSec), 20));
		out.print(StringUtils.leftPad(String.format("%15.3f", statAveSec), 20));
		out.print(StringUtils.leftPad(String.format("%15.3f", statPerSec), 12));
		

		for (Loader th : loaders) {
			out.print("\n");
			out.print(StringUtils.leftPad(th.getName(), 9));
			out.print(StringUtils.leftPad(String.format("%15.0f", (float)th.getStatCount()), 15));
			//out.print(StringUtils.leftPad(Long.toString(th.getErrors()), 15));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatTotalSec()), 20));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatAveSec()), 20));
			//out.print(StringUtils.leftPad(nb.format((double) 1 / (double) th.getStatPerSec() * 1000), 12));
			out.print(StringUtils.leftPad(String.format("%15.3f", th.getStatPerSec()), 12));
		}
		out.print("\n");
		out.print("\n");
	}
}