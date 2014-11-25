package com.logicaldoc.installer.util;

import java.io.PrintWriter;
import java.util.Date;

public class Log {
	private static PrintWriter fw = null;

	static {
		try {
			fw = new PrintWriter("install.log");
			info("Setup BEGINS", null);
		} catch (Throwable e) {
			e.printStackTrace();
			fw = null;
		}
	}

	public static void info(String message, Throwable t) {
		if (fw == null)
			return;
		try {
			fw.write(String.format("%tc - INFO - %s", new Date(), message != null ? message : ""));
			fw.write("\n");
			if (t != null) {
				if (t.getMessage() != null) {
					fw.write(t.getMessage());
					fw.write("\n");
				}
				fw.write("\n");
			}
		} catch (Throwable e) {
			error(e.getMessage(), null);
		} finally {
			try {
				fw.flush();
			} catch (Throwable e) {
			}
		}
	}

	public static void error(String message, Throwable t) {
		if (fw == null)
			return;
		try {
			fw.write(String.format("%tc - ERROR - %s", new Date(), message != null ? message : ""));
			fw.write("\n");
			if (t != null) {
				if (t.getMessage() != null) {
					fw.write(t.getMessage());
					fw.write("\n");
				}
				fw.write("\n");
			}
		} catch (Throwable e) {
			fw.write(String.format("%tc - ERROR - %s", new Date(), e.getMessage()));
		} finally {
			try {
				fw.flush();
			} catch (Throwable e) {
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		fw.close();
	}
}
