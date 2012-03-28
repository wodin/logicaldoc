package com.logicaldoc.benchmark;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
					// Long folder = getRandomFolder();
					// if (folder == null) {
					// throw new Exception("Error getting folder");
					// }
					//
					// File file = randomFile.getFile();
					// String title = formatter.format(testCount);
					//
					// Long docId = Loader.this.createDocument(folder, title,
					// file);
					// if (docId != null) {
					// statCount++;
					// } else {
					// throw new Exception("Error creating document " + title);
					// }

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
					if (testCount > testTotal) {
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

}
