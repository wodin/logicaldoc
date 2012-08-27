package com.logicaldoc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class used to execute system commands
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class Exec {

	public static int exec(String commandLine, String[] env, File dir, int timeout) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commandLine, env, dir);

		Worker worker = new Worker(process);
		worker.start();
		try {
			worker.join(timeout * 1000);

			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}

			if (worker.exit != null)
				return worker.exit;
			else
				return 0;
		} catch (InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			return 0;
		} finally {
			process.destroy();
		}

	}

	static class Worker extends Thread {

		private final Process process;

		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}
}