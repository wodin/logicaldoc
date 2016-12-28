package com.logicaldoc.installer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WinKill {

	private static final String TASKLIST = "tasklist";

	private static final String KILL = "taskkill /F /T /IM ";

	private static final String TOMCAT_IMAGE_NAME = "tomcat";

	public static void main(String[] args) {
		if (args == null || args.length == 0)
			killTomcatProcess();
		else
			killProcess(args[0]);
	}

	public static boolean isProcessRunning(String imageName) {
		try {
			Process p = Runtime.getRuntime().exec(TASKLIST);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(imageName)) {
					return true;
				}
			}
			return false;
		} catch (Throwable t) {
			return false;
		}

	}

	public static void killTomcatProcess() {
		killProcess(TOMCAT_IMAGE_NAME);
	}

	public static void killProcess(String serviceName) {
		try {
			Runtime.getRuntime().exec(KILL + serviceName);
		} catch (Throwable t) {

		}
	}
}