package com.logicaldoc.bm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all loaders.
 */
public abstract class AbstractLoader extends Thread {

	private static Log log = LogFactory.getLog(AbstractLoader.class);

	protected NumberFormat formatter = new DecimalFormat("Loader_00000000000");

	protected LoadSession session;

	protected String loaderName;

	protected long iterations;

	private AtomicBoolean mustStop;

	protected Random random;

	// Statistics
	protected int statCount = 0; // Total number of successful iterations

	private long statTotalMs = 0; // Total execution time

	private int statErrors = 0; // Total errors

	protected int loaderCount = 0;

	public AbstractLoader(String name) {
		super(LoadSession.THREAD_GROUP, "Loader-" + name);
		this.loaderName = name;
	}

	public void init(LoadSession session) {
		this.session = session;

		this.mustStop = new AtomicBoolean(false);
		this.random = new Random();

		this.loaderCount = 0;
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
		loaderCount = 0;
		long startTime = System.currentTimeMillis();

		try {
			while (!mustStop.get()) {
				try {
					// Choose a server
					// int serverCount = session.getRemoteServers().size();
					// int serverIndex = random.nextInt(serverCount);
					// LoaderServerProxy serverProxy =
					// session.getRemoteServers().get(serverIndex);
					ServerProxy serverProxy = session.getRemoteServer();

					doLoading(serverProxy);
					statCount++;

					loaderCount++;
					if (loaderCount >= iterations) {
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
			log.info(getName() + " finished in ms: " + statTotalMs);
		}
	}

	/**
	 * <pre>
	 * NAME+36\tCOUNT          \tTOTAL TIME     \tAVERAGE TIME   \tPER SECOND     \tDESCRIPTION
	 * </pre>
	 * 
	 * @return Returns the summary of the results
	 */
	public String getSummary() {
		double statTotalSec = statTotalMs / 1000.0;
		double statPerSec = statCount / statTotalSec;
		double statAveSec = statTotalSec / statCount;
		// Summarize the results
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%40s", loaderName)).append("\t").append(String.format("%15.0f", (float) statCount))
				.append("\t").append(String.format("%15.3f", statTotalSec)).append("\t")
				.append(String.format("%15.3f", statAveSec)).append("\t").append(String.format("%15.3f", statPerSec))
				.append("\t").append("");
		return sb.toString();
	}

	/**
	 * @return a brief description of the loading
	 * @throws Exception any exception will be handled
	 */
	protected abstract String doLoading(ServerProxy serverProxy) throws Exception;

	public boolean isFinished() {
//		if (mustStop.get())
//			return true;
		return loaderCount >= iterations;
	}

	public long getIterations() {
		return iterations;
	}

	public void setIterations(long iterations) {
		this.iterations = iterations;
	}
}