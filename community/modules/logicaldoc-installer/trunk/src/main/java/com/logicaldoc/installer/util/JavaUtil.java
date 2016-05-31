package com.logicaldoc.installer.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

public class JavaUtil {
	final static String[] JAVA_VERSIONS = new String[] { "2.0", "1.9", "1.8" };

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
		for (String version : JAVA_VERSIONS) {
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

		Log.info("Unable to find a JDK in the registry, check the fileaystem", null);

		// Perhaps we were not able to access to the registry
		return detectJavaInFilesystem(true, bit64);
	}

	private static String detectJavaInFilesystem(final boolean jdk, File root) {
		if (root == null)
			return null;

		Log.info("Search for java into " + root.getPath(), null);

		File[] dirs = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (!file.isDirectory())
					return false;
				for (String version : JAVA_VERSIONS)
					if (file.getName().startsWith((jdk ? "jdk" : "jre") + version))
						return true;
				return false;
			}
		});

		if (dirs != null && dirs.length > 0) {
			Arrays.sort(dirs, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o2.getName().compareTo(o1.getName());
				}
			});

			return dirs[0].getPath();
		}

		return null;
	}

	private static String detectJavaInFilesystem(boolean jdk, boolean bit64) {
		File root = null;
		if (bit64 && System.getenv("ProgramW6432") != null)
			root = new File(System.getenv("ProgramW6432") + File.separator + "Java");
		else if (System.getenv("ProgramFiles(x86)") != null)
			root = new File(System.getenv("ProgramFiles(x86)") + File.separator + "Java");
		return detectJavaInFilesystem(jdk, root);
	}

	/**
	 * Tries to get the path of JRE 1.8 or greater from the registry
	 */
	public static String getJRE(boolean bit64) {
		for (String version : JAVA_VERSIONS) {
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

		Log.info("Unable to find a JRE in the registry, check the fileaystem", null);

		// Perhaps we were not able to access to the registry
		return detectJavaInFilesystem(false, bit64);
	}

	public static boolean is64Bit(String javaHome) {
		if (javaHome == null || javaHome.isEmpty())
			return false;

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

			Log.info("Java output:\n" + outputText + "\n", null);

			is64bit = outputText.toLowerCase().contains("64-bit");
		} catch (Throwable t) {

		}

		Log.info("Architecture of Java " + javaHome + ": " + (is64bit ? "64bit" : "32bit"), null);

		return is64bit;
	}

	public static boolean is64Bit() {
		return is64Bit(null);
	}
}
