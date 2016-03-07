package com.logicaldoc.installer.util;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class JavaUtil {
	public static void main(String[] args) {
		System.out.println("JRE arch: " + (is64Bit() ? "64bit" : "32bit"));
	}

	public static boolean is64Bit() {
		boolean is64bit = (System.getProperty("os.arch").indexOf("64") != -1);

		if (!is64bit) {
			// Give another trial analyzing the output of java -version
			try {
				Process p = Runtime.getRuntime().exec("java -version");
				InputStream stdoutStream = new BufferedInputStream(p.getErrorStream());

				StringBuffer buffer = new StringBuffer();
				for (;;) {
					int c = stdoutStream.read();
					if (c == -1)
						break;
					buffer.append((char) c);
				}
				String outputText = buffer.toString().toLowerCase();
				
				stdoutStream.close();

				is64bit = outputText.contains("64-bit");
			} catch (Throwable t) {
			}
		}

		Log.info("Detected Java architecture: " + (is64bit ? "64bit" : "32bit"), null);

		return is64bit;
	}
}
