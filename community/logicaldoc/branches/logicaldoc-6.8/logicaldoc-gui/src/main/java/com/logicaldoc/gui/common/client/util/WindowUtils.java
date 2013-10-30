package com.logicaldoc.gui.common.client.util;

import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.i18n.I18N;

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
		$doc.title = title;
		//Change also the main frame window if any
		if ($wnd.parent)
			$wnd.parent.document.title = title;
	}-*/;

	public static void setTitle(GUIInfo info, String prefix) {
		String buf = info.getProductName() + " " + info.getRelease()
				+ (info.getLicensee() != null ? " - " + I18N.message("licensedto") + " " + info.getLicensee() : "");
		if (prefix != null) {
			buf = prefix + " - " + buf;
		}
		WindowUtils.setTitle(buf);
	}

	public static native void openUrl(String url)/*-{
		$wnd.location = url;
	}-*/;
	
	public static native void openUrl(String url, String window)/*-{
	    $wnd.open(url, window); 
	}-*/;
}