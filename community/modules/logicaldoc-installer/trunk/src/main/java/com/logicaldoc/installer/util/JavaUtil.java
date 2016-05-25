package com.logicaldoc.installer.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class JavaUtil {
	public static void main(String[] args) {
		String jdkpath = getJDK(true);
		System.out.println(jdkpath + " " + is64Bit(jdkpath));
		jdkpath = getJDK(false);
		System.out.println(jdkpath + " " + is64Bit(jdkpath));

		jdkpath = getJRE(true);
		System.out.println(jdkpath + " " + is64Bit(jdkpath));
		jdkpath = getJRE(false);
		System.out.println(jdkpath + " " + is64Bit(jdkpath));
	}

	/**
	 * Tries to get the path of JDK 1.8 or greater from the registry
	 */
	public static String getJDK(boolean bit64) {
		String[] javaVersion = new String[] { "2.0", "1.9", "1.8" };

		for (String version : javaVersion) {
			String jdkPath = WindowsReqistry.readRegistry(
					"HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\" + version, "JavaHome");
			if (jdkPath != null && bit64 == is64Bit(jdkPath))
				return jdkPath;

			jdkPath = WindowsReqistry
					.readRegistry("HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\JavaSoft\\Java Development Kit\\"
							+ version, "JavaHome");
			if (jdkPath != null && bit64 == is64Bit(jdkPath))
				return jdkPath;
		}

		return null;
	}

	/**
	 * Tries to get the path of JRE 1.8 or greater from the registry
	 */
	public static String getJRE(boolean bit64) {
		String[] javaVersion = new String[] { "2.0", "1.9", "1.8" };

		for (String version : javaVersion) {
			String jdkPath = WindowsReqistry.readRegistry(
					"HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Runtime Environment\\" + version, "JavaHome");
			if (jdkPath != null && bit64 == is64Bit(jdkPath))
				return jdkPath;

			jdkPath = WindowsReqistry.readRegistry(
					"HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\JavaSoft\\Java Runtime Environment\\" + version,
					"JavaHome");
			if (jdkPath != null && bit64 == is64Bit(jdkPath))
				return jdkPath;
		}

		return null;
	}

	public static boolean is64Bit(String javaHome) {
		boolean is64bit = (System.getProperty("os.arch").indexOf("64") != -1);

		// Give another trial analyzing the output of java -version
		try {
			String command = (javaHome == null || "".equals(javaHome)) ? ""
					: (javaHome + File.separator + "bin" + File.separator);
			command += "java -version";

			Process p = Runtime.getRuntime().exec(command);
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

		Log.info("Architecture of Java " + javaHome + ": " + (is64bit ? "64bit" : "32bit"), null);

		return is64bit;
	}

	public static boolean is64Bit() {
		return is64Bit(null);
	}
}
