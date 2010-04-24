package com.logicaldoc.gui.frontend.client;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.panels.FooterStatus;

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
		FooterStatus.getInstance().error(I18N.getMessage("servererror"), m);
		GWT.log("Server error: " + m, caught);
	}

	public static void info(String message, String detail) {
		if (message == null || "".equals(message))
			FooterStatus.getInstance().info(message, detail);
		GWT.log("info message: " + message, null);
	}
}