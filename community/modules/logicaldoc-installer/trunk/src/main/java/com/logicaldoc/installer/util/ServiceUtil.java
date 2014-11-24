package com.logicaldoc.installer.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility methods related to Windows services
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ServiceUtil {

	public static boolean exists(String service) {
		try {
			Process p = Runtime.getRuntime().exec("sc query " + service);
			InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

			StringBuffer buffer = new StringBuffer();
			for (;;) {
				int c = stdoutStream.read();
				if (c == -1)
					break;
				buffer.append((char) c);
			}
			String outputText = buffer.toString();
			stdoutStream.close();

			return outputText.contains(service);
		} catch (Throwable t) {
			return false;
		}
	}

	public static String findSuitableName(String baseName) {
		String service = baseName;
		int i = 1;
		while (i < 100 && exists(service)) {
			service = baseName + (i++);
		}
		return service;
	}

	public static boolean isRunning(String service) throws IOException {
		Process p = Runtime.getRuntime().exec("sc query " + service);
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();

		return outputText.toLowerCase().contains("running");
	}
}
