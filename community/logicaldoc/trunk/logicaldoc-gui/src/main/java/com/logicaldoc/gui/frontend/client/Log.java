package com.logicaldoc.gui.frontend.client;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.panels.EventPanel;

/**
 * Represents a client work session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Log {

	private Log() {
	}

	public static void serverError(Throwable caught) {
		serverError(null, caught);
	}

	/**
	 * Logs a server error and shows a warning to the user
	 * 
	 * @param message The message to be shown (optional)
	 * @param caught The caught exception (if any)
	 */
	public static void serverError(String message, Throwable caught) {
		String m = message;
		if (message == null || "".equals(message))
			m = caught.getMessage() != null ? caught.getMessage() : "";
		EventPanel.get().error(I18N.getMessage("servererror"), m);
		GWT.log("Server error: " + m, caught);
	}

	public static void warn(String message, String detail) {
		EventPanel.get().warn(message, detail);
		GWT.log("warn: " + message, null);
	}

	public static void error(String message, String detail, Throwable caught) {
		EventPanel.get().error(message, detail);
		GWT.log("info: " + message, caught);
	}

	public static void info(String message, String detail) {
		EventPanel.get().info(message, detail);
		GWT.log("info: " + message, null);
	}

	public static void debug(String message) {
		GWT.log("debug: " + message, null);
	}
}