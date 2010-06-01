package com.logicaldoc.gui.common.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

public class Util {

	private static Dictionary context;

	/**
	 * Generates HTML image code with style.
	 * 
	 * @param imageName the name of the icon image
	 * @param alt the image alt
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageName, String alt) {
		return "<img border=\"0\" align=\"absmidle\" alt=\"" + alt + "\" title=\"" + alt + "\" src='"
				+ Util.imageUrl(imageName) + "' />";
	}

	public static String imageUrl(String imageName) {
		return contextPath()+imagePrefix() + imageName;
	}

	public static String contextPath(){
		return GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", ""); 
	}
	
	public static Dictionary getContext() {
		if (context == null)
			context = Dictionary.getDictionary("context");
		return context;
	}

	public static String imagePrefix() {
		return "skin/images/";
	}

	/**
	 * Generates HTML image code with style.
	 * 
	 * @param imageName the image name
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageName) {
		return imageHTML(imageName, "");
	}

	/**
	 * Format file size in Bytes, KBytes or MBytes.
	 * 
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static native String formatSize(double size) /*-{
		if (size / 1024 < 1) {
			str = size + " Bytes";
		} else if (size / 1048576 < 1) {
			str = (size / 1024).toFixed(1) + " KBytes";
		} else {
			str = (size / 1048576).toFixed(1) + " MBytes";
		} 

		return str;
	}-*/;

	/**
	 * Format number percentage.
	 * 
	 * @param value The value to be formatted.
	 * @param fixed The number of decimal places.
	 * @return The formated value.
	 */
	public static native String formatPercentage(double value, int fixed) /*-{
		str = value.toFixed(fixed);

		return str+"%";
	}-*/;

	/**
	 * Get browser language
	 * 
	 * @return The language in ISO 639 format.
	 */
	public static native String getBrowserLanguage() /*-{
		var lang = navigator.language? navigator.language : navigator.userLanguage;

		if (lang) {
			return lang;
		} else {
		  	return "en";
		}
	}-*/;

	/**
	 * returns 'opera', 'safari', 'ie6', 'ie7', 'gecko', or 'unknown'.
	 */
	public static native String getUserAgent() /*-{
		try {
		    if ( window.opera ) return 'opera';
		    var ua = navigator.userAgent.toLowerCase();
		    if ( ua.indexOf('webkit' ) != -1 ) return 'safari';
		    if ( ua.indexOf('msie 6.0') != -1 ) return 'ie6';
		    if ( ua.indexOf('msie 7.0') != -1 ) return 'ie7';
		    if ( ua.indexOf('gecko') != -1 ) return 'gecko';
		    return 'unknown';
		} catch ( e ) { return 'unknown' }
	}-*/;

	public static native void copyToClipboard(String text) /*-{
		new $wnd.copyToClipboard(text);
	}-*/;

	public native boolean isValidEmail(String email) /*-{
		var reg1 = /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)/; // not valid
		var reg2 = /^.+\@(\[?)[a-zA-Z0-9\-\.]+\.([a-zA-Z]{2,3}|[0-9]{1,3})(\]?)$/; // valid
		return !reg1.test(email) && reg2.test(email);
	}-*/;
}
