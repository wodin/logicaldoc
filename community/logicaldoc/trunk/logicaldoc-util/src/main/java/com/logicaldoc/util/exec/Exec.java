package com.logicaldoc.util.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class used to execute system commands
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class Exec {
	protected static Logger log = LoggerFactory.getLogger(Exec.class);

	/**
	 * Execute the command by using the process builder
	 */
	public static int exec(List<String> command) throws IOException {
		return exec(command, null, null, -1);
	}

	/**
	 * Execute the command by using the process builder
	 */
	public static int exec(final List<String> commandLine, String[] env, File dir, int timeout) throws IOException {
		int exit = 0;

		final Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]), env, dir);

		if (timeout > 0) {
			ExecutorService service = Executors.newSingleThreadExecutor();
			try {
				Callable<Integer> call = new CallableProcess(process);
				Future<Integer> future = service.submit(call);
				exit = future.get(timeout, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				process.destroy();
				String message = "Timeout command " + commandLine;
				log.warn(message);
			} catch (Exception e) {
				log.warn("Command failed to execute - " + commandLine);
				exit = 1;
			} finally {
				service.shutdown();
			}
		}

		StreamEater errEater = new StreamEater("err", process.getErrorStream());

		StreamEater outEater = new StreamEater("out", process.getInputStream());

		Thread a = new Thread(errEater);
		a.start();

		Thread b = new Thread(outEater);
		b.start();

		try {
			exit = process.waitFor();
		} catch (InterruptedException e) {

		}

		return exit;
	}

	/**
	 * Execute the command by using the Runtime.exec
	 */
	public static int exec(final String commandLine, String[] env, File dir, int timeout) throws IOException {
		int exit = 0;

		final Process process = Runtime.getRuntime().exec(commandLine, env, dir);

		if (timeout > 0) {
			ExecutorService service = Executors.newSingleThreadExecutor();
			try {
				Callable<Integer> call = new CallableProcess(process);
				Future<Integer> future = service.submit(call);
				exit = future.get(timeout, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				process.destroy();
				String message = "Timeout command " + commandLine;
				log.warn(message);
			} catch (Exception e) {
				log.warn("Command failed to execute - " + commandLine);
				exit = 1;
			} finally {
				service.shutdown();
			}
		}

		StreamEater errEater = new StreamEater("err", process.getErrorStream());

		StreamEater outEater = new StreamEater("out", process.getInputStream());

		Thread a = new Thread(errEater);
		a.start();

		Thread b = new Thread(outEater);
		b.start();

		try {
			exit = process.waitFor();
		} catch (InterruptedException e) {

		}

		return exit;
	}

	static class StreamEater implements Runnable {

		private InputStream stream;

		private String prefix;

		public StreamEater(String prefix, InputStream stream) {
			super();
			this.prefix = prefix;
			this.stream = stream;
		}

		public void run() {

			try {

				InputStreamReader isr = new InputStreamReader(stream);

				BufferedReader br = new BufferedReader(isr);

				String line = br.readLine();

				while (line != null) {
					System.out.println(prefix + ":" + line);
					line = br.readLine();
				}

				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class CallableProcess implements Callable<Integer> {
		private Process p;

		public CallableProcess(Process process) {
			p = process;
		}

		public Integer call() throws Exception {
			return p.waitFor();
		}
	}
}