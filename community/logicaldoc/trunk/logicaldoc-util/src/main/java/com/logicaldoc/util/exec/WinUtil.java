package com.logicaldoc.util.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WinUtil {
	private static final String TASKLIST = "tasklist";

	private static final String KILL = "taskkill /F /T /IM ";

	public static int main(String[] args) {
		if ("kill".equals(args[0])) {
			if (isProcessRunning(args[1])) {
				return killProcess(args[1]);
			}
		}

		return 0;
	}

	public static boolean isProcessRunning(String serviceName) {
		try {
			Process p = Runtime.getRuntime().exec(TASKLIST);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().contains(serviceName.toLowerCase())) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public static int killProcess(String imageName) {
		try {
			Process p = Runtime.getRuntime().exec(KILL + imageName);
			return p.exitValue();
		} catch (IOException e) {
			return 1;
		}
	}
}
