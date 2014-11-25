package com.logicaldoc.installer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility class used to execute system commands
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Exec {

	/**
	 * Execute the command by using the process builder
	 */
	public static int exec(List<String> command) throws IOException {
		return exec(command, null, null, -1);
	}

	/**
	 * Execute the command by using the process builder
	 */
	public static int exec(final List<String> command, Map<String, String> env, File dir, int timeout)
			throws IOException {
		int exit = 0;
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);

		if (env != null) {
			for (String name : env.keySet()) {
				builder.environment().put(name, env.get(name).toString());
			}
		}
		if (dir != null)
			builder.directory(dir);

		final Process process = builder.start();

		Timer t = null;
		if (timeout > 0) {
			t = new Timer(true);
			t.schedule(new TimerTask() {

				@Override
				public void run() {
					Log.info("Timeout command " + command.get(0), null);
					process.destroy();
				}
			}, timeout * 1000); // it will kill the process after timeout
								// seconds
								// (if it's
								// not finished yet).
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
		if (t != null)
			t.cancel();

		process.destroy();
		return exit;
	}

	/**
	 * Execute the command by using the Runtime.exec
	 */
	public static int exec(final String commandLine, String[] env, File dir, int timeout) throws IOException {
		int exit = 0;
		Runtime runtime = Runtime.getRuntime();
		final Process process = runtime.exec(commandLine, env, dir);

		Timer t = null;
		if (timeout > 0) {
			t = new Timer(true);
			t.schedule(new TimerTask() {

				@Override
				public void run() {
					Log.info("Timeout command " + commandLine, null);
					process.destroy();
				}
			}, timeout * 1000); // it will kill the process after timeout
								// seconds
								// (if it's
								// not finished yet).
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
					Log.info(prefix + ":" + line, null);
					line = br.readLine();
				}

				br.close();

			} catch (IOException e) {
				Log.error(e.getMessage(), e);
			}
		}
	}
}