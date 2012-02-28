package com.logicaldoc.benchmark;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Loads the LogicalDOC instance with a population of documents
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class BMLoader {

	private static Log log = LogFactory.getLog(BMLoader.class);

	private RandomFile randomFile;

	private String folderProfile;

	private long rootFolder = Folder.DEFAULTWORKSPACE;

	private List<String> paths = new ArrayList<String>();

	private String url;

	private String username;

	private String password;

	private AuthClient auth;

	private DocumentClient documentClient;

	private FolderClient folderClient;

	private String sid;

	private int threads = 2;

	private long iteration = 0;

	private long iterations;

	private long totalTime;

	private CyclicBarrier barrier;

	private List<Loader> loaders = new ArrayList<Loader>();

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "/context.xml" });

		// RandomFile rf = (RandomFile) context.getBean("RandomFile");
		BMLoader loader = (BMLoader) context.getBean("BMLoader");
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

		long startTime = System.currentTimeMillis();

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

			/*
			 * Waiting the end of the job
			 */
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
			}

			// for (Loader loadThread : loaders) {
			// loadThread.interrupt();
			// }

			log.info("All threads finished");

			log.info("Prepare the report");

			totalTime = System.currentTimeMillis() - startTime;

			printReport();
		} finally {
			auth.logout(sid);
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
	private void init() {
		log.info("Connect to the server");
		try {
			iteration = 0;
			sid = null;
			barrier = new CyclicBarrier(threads);
			auth = new AuthClient(url + "/services/Auth");
			documentClient = new DocumentClient(url + "/services/Document");
			folderClient = new FolderClient(url + "/services/Folder");
			log.info("Connection established");

			sid = auth.login(username, password);
			log.info("Created SID: " + sid);
		} catch (Throwable e) {
			log.error("Unable to initialize WebServices connection", e);
		}
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	private class Loader extends Thread {

		private NumberFormat formatter = new DecimalFormat("Document-00000000000");

		private Random generator = new Random();

		private long count;

		private long errors;

		private long totalTime = 0;

		private long average = 0;

		private long sum = 0;

		public Loader(String name) {
			super(name);
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();

			try {
				while (sid != null && BMLoader.this.iteration < BMLoader.this.iterations) {
					long t = System.currentTimeMillis();

					try {
						String path = BMLoader.this.paths.get(generator.nextInt(paths.size()));
						WSFolder folder = null;
						synchronized (BMLoader.this.paths) {
							try {
								if (sid == null)
									continue;
								folder = folderClient.createPath(BMLoader.this.sid, BMLoader.this.rootFolder, path);
							} catch (Throwable ex) {
								log.error(ex.getMessage(), ex);
								folder = null;
								errors++;
							}
						}

						if (folder == null)
							continue;

						try {
							barrier.await();
						} catch (InterruptedException e) {
						} catch (BrokenBarrierException e) {
						}

						File file = randomFile.getFile();
						long c = next();
						String title = formatter.format(c);
						String fileName = title + "." + FilenameUtils.getExtension(file.getName());

						WSDocument doc = new WSDocument();
						doc.setFolderId(folder.getId());
						doc.setTitle(title);
						doc.setFileName(fileName);
						doc.setLanguage("en");

						documentClient.create(sid, doc, file);
						log.debug("Created " + fileName);

						count++;
						try {
							barrier.await();
						} catch (InterruptedException e) {
						} catch (BrokenBarrierException e) {
						}
					} catch (Throwable ex) {
						log.error(ex.getMessage(), ex);
						errors++;
						continue;
					} finally {
						long now = System.currentTimeMillis();
						totalTime = now - startTime;
						long time = now - t;
						sum += time;
						average = Math.round((double) sum / count);
					}
				}
			} finally {
				totalTime = System.currentTimeMillis() - startTime;
				average = Math.round((double) sum / count);
				log.info(getName() + " finished");
			}
		}

		public long getCount() {
			return count;
		}

		public long getErrors() {
			return errors;
		}

		public long getTotalTime() {
			return totalTime;
		}

		public long getAverage() {
			return average;
		}
	}

	public void setIterations(long iterations) {
		this.iterations = iterations;
	}

	private void printReport() {
		PrintStream out = System.out;

		out.print("\n");
		out.print(StringUtils.leftPad("NAME", 9));
		out.print(StringUtils.leftPad("COUNT", 15));
		out.print(StringUtils.leftPad("ERRORS", 15));
		out.print(StringUtils.leftPad("TOTAL TIME", 20));
		out.print(StringUtils.leftPad("AVERAGE TIME", 20));
		out.print(StringUtils.leftPad("PER SECOND", 12));

		long count = 0;
		long errors = 0;
		long average = 0;

		for (Loader th : loaders) {
			count += th.getCount();
			errors += th.getErrors();
			average += th.getAverage();
		}
		average = Math.round((double) average / loaders.size());

		DecimalFormat nb = new DecimalFormat("#####.###");

		out.print("\n");
		out.print(StringUtils.leftPad("main", 9));
		out.print(StringUtils.leftPad(Long.toString(count), 15));
		out.print(StringUtils.leftPad(Long.toString(errors), 15));
		out.print(StringUtils.leftPad(Util.formatFriendlyTimeSpan(totalTime), 20));
		out.print(StringUtils.leftPad(Util.formatFriendlyTimeSpan(average), 20));
		out.print(StringUtils.leftPad(nb.format((double) 1 / (double) average * 1000), 12));

		for (Loader th : loaders) {
			out.print("\n");
			out.print(StringUtils.leftPad(th.getName(), 9));
			out.print(StringUtils.leftPad(Long.toString(th.getCount()), 15));
			out.print(StringUtils.leftPad(Long.toString(th.getErrors()), 15));
			out.print(StringUtils.leftPad(Util.formatFriendlyTimeSpan(th.getTotalTime()), 20));
			out.print(StringUtils.leftPad(Util.formatFriendlyTimeSpan(th.getAverage()), 20));
			out.print(StringUtils.leftPad(nb.format((double) 1 / (double) th.getAverage() * 1000), 12));
		}

	}
}