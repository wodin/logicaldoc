package com.logicaldoc.gui.common.client.util;

import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.Session;

/**
 * Utilities for accessing the javascript Window object
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WindowUtils {

	public static RequestInfo getRequestInfo() {
		RequestInfo result = new RequestInfo();
		result.setHash(getHash());
		result.setHost(getHost());
		result.setHostName(getHostName());
		result.setHref(getHref());
		result.setPath(getPath());
		result.setPort(getPort());
		result.setProtocol(getProtocol());
		result.setQueryString(getQueryString());
		return result;
	}

	public static native String getAppName() /*-{
		return $wnd.navigator.appName;
	}-*/;

	private static native String getQueryString() /*-{
		return $wnd.location.search;
	}-*/;

	private static native String getProtocol() /*-{
		return $wnd.location.protocol;
	}-*/;

	private static native String getPort() /*-{
		return $wnd.location.port;
	}-*/;

	private static native String getPath() /*-{
		return $wnd.location.pathname;
	}-*/;

	private static native String getHref() /*-{
		return $wnd.location.href;
	}-*/;

	private static native String getHostName() /*-{
		return $wnd.location.hostname;
	}-*/;

	private static native String getHost() /*-{
		return $wnd.location.host;
	}-*/;

	private static native String getHash() /*-{
		return $wnd.location.hash;
	}-*/;

	public static native void setTitle(String title)/*-{
		$doc.title=title;
	}-*/;

	public static void openUrl(String url) {
		Session.get().setConfirmExit(false);
		Window.open(url, "_self", "");
		if (!Session.get().isConfirmExit() && !WindowUtils.getAppName().toLowerCase().contains("explorer"))
			Session.get().setConfirmExit(true);
	}

	public static native void triggerUrl(String url) /*-{
		var popup=$wnd.open(url,"tmp","width=1, height=1");
		popup.close();
	}-*/;
}