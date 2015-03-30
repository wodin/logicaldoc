package com.logicaldoc.util.exec;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WinUtil {
	private static final String TASKLIST = "tasklist";

	private static final String KILL = "taskkill /IM ";

	public static boolean isProcessRunning(String serviceName) throws Exception {
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.toLowerCase().contains(serviceName.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	public static void killProcess(String serviceName) throws Exception {
		Runtime.getRuntime().exec(KILL + serviceName + " /F");
	}
}
