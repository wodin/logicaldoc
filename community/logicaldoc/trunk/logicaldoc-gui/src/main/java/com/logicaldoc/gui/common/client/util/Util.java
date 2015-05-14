package com.logicaldoc.gui.common.client.util;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class Util {
	public static String[] OFFICE_EXTS = new String[] { ".doc", ".xls", ".ppt", ".docx", ".xlsx", ".pptx", ".rtf",
			".odt", ".ods", ".odp" };

	public static String[] IMAGE_EXTS = new String[] { ".gif", ".jpg", ".jpeg", ".bmp", ".tif", ".tiff", ".png" };

	public static String[] VIDEO_EXTS = new String[] { ".mp4", ".avi", ".mpg", ".wmv", ".wma", ".asf", ".mov", ".rm",
			".flv", ".aac", ".vlc", ".ogg", ".webm", ".swf", ".mpeg", ".swf" };

	public static String[] AUDIO_EXTS = new String[] { ".mp3", ".m4p", ".m4a", ".wav" };

	public static String[] WEBCONTENT_EXTS = new String[] { ".html", ".htm", ".xhtml" };

	/**
	 * Generates HTML image code with style.
	 * 
	 * @param imageName the name of the icon image
	 * @param alt the image alt
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageName, String alt, String style) {
		return "<img border=\"0\" align=\"absmidle\" alt=\"" + alt + "\" title=\"" + alt + "\""
				+ (style != null ? "style='" + style + "'" : "") + " src='" + Util.imageUrl(imageName) + "' />";
	}

	public static String imageHTML(String imageName, Integer width, Integer height, String style) {
		String html = "<img border='0' alt='' title='' src='" + Util.imageUrl(imageName) + "' ";
		if (width != null)
			html += " width='" + width + "px' ";
		if (height != null)
			html += " height='" + height + "px' ";
		if (style != null)
			html += " style='" + style + "' ";
		html += " />";
		return html;
	}

	public static String downloadURL(long docId, String fileVersion, String suffix, boolean open) {
		String url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId;
		if (fileVersion != null)
			url += "&fileVersion=" + fileVersion;
		if (suffix != null)
			url += "&suffix=" + suffix;
		if (open)
			url += "&open=true";
		return url;
	}

	public static String downloadURL(long docId, String fileVersion, boolean open) {
		String url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId;
		if (fileVersion != null)
			url += "&fileVersion=" + fileVersion;
		if (open)
			url += "&open=true";
		return url;
	}

	public static String webEditorUrl(long docId, String fileName, int height) {
		String url = contextPath() + "ckeditor/index.jsp?sid=" + Session.get().getSid() + "&docId=" + docId + "&lang="
				+ I18N.getLocale() + "&fileName=" + fileName + "&height=" + height;
		return url;
	}

	/**
	 * Generates Flash code
	 */
	public static String flashHTML(String flashName, int width, int height, String flashvars) {
		String tmp = "<div align=\"center\"><object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\"  width=\""
				+ width + "\" height=\"" + height + "\" align=\"middle\">\n";
		tmp += " <param name=\"allowScriptAccess\" value=\"always\" />\n";
		tmp += " <param name=\"allowFullScreen\" value=\"false\" />\n";
		tmp += " <param name=\"movie\" value=\"" + Util.flashUrl(flashName) + "\" />\n";
		tmp += " <param name=\"quality\" value=\"high\" />\n";
		tmp += " <param name=\"bgcolor\" value=\"#ffffff\" />\n";
		tmp += " <param name=\"wmode\" value=\"transparent\" />\n";
		tmp += " <param name=\"flashvars\" value=\"" + flashvars + "\" />";
		tmp += "	<embed type=\"application/x-shockwave-flash\" src=\"" + Util.flashUrl(flashName) + "\" height=\""
				+ height + "\" width=\"" + width
				+ "\" id=\"tagcloud\" name=\"tagcloud\" bgcolor=\"#ffffff\" quality=\"high\" flashvars=\"" + flashvars
				+ "\" />";
		tmp += "</object></div>\n";
		return tmp;
	}

	/**
	 * Generates Flash code for Preview
	 */
	public static String flashPreview(int width, int height, int zoom, String flashvars, boolean printEnabled,
			String language) {
		String key = Session.get().getInfo().getConfig("flexpaperviewer.key");
		Float fzoom = new Float(1.0F);
		try {
			fzoom = new Float(((float) zoom) / 100);
		} catch (Throwable t) {

		}

		String vars = flashvars + "&Scale=" + (zoom > 0 ? fzoom.toString() : "1.0") + "&FitPageOnLoad="
				+ (zoom <= 0 ? "true" : "false") + "&FitWidthOnLoad=false&PrintEnabled=" + printEnabled
				+ "&ProgressiveLoading=true" + "&ViewModeToolsVisible=true" + "&ZoomToolsVisible=true"
				+ "&NavToolsVisible=true&CursorToolsVisible=true&SearchToolsVisible=true&MaxZoomSize=20";

		if (key != null) {
			vars += "&key=" + key;
		}
		String tmp = "<div align='center' style='overflow:hidden;' >";
		tmp += "<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" id=\"FlexPaperViewer\" width=\"" + width
				+ "\" height=\"" + height
				+ "\" codebase=\"http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab\">\n";
		tmp += " <param name=\"allowScriptAccess\" value=\"always\" />\n";
		tmp += " <param name=\"allowFullScreen\" value=\"true\" />\n";
		tmp += " <param name=\"movie\" value=\"" + Util.flashUrl("flexpaperviewer.swf") + "\" />\n";
		tmp += " <param name=\"quality\" value=\"high\" />\n";
		tmp += " <param name=\"bgcolor\" value=\"#ffffff\" />\n";
		tmp += " <param name=\"flashvars\" value=\"" + vars + "\" />\n";
		tmp += " 	<embed type=\"application/x-shockwave-flash\" src=\"" + Util.flashUrl("flexpaperviewer.swf")
				+ "\" height=\"" + height + "\" width=\"" + width + "\" name=\"FlexPaperViewer\""
				+ " bgcolor=\"#ffffff\" quality=\"high\" allowFullScreen=\"true\" flashvars=\"" + vars + "\">\n";
		tmp += "</embed>\n";
		tmp += "</object></div>\n";

		return tmp;
	}

	/**
	 * Generates HTML code for reproducing video files
	 */
	public static String videoHTML(String mediaUrl, String width, String height) {
		String tmp = "<video controls ";
		if (width != null)
			tmp += "width='" + width + "' ";
		if (height != null)
			tmp += "height='" + height + "' ";
		tmp += ">";
		tmp += "<source src='" + mediaUrl + "' />";
		tmp += "</video>";
		return tmp;
	}

	/**
	 * Generates HTML code for reproducing audio files
	 */
	public static String audioHTML(String mediaUrl) {
		String tmp = "<audio style='margin-top: 20px; vertical-align: middle; text-align: center' controls >";
		tmp += "<source src='" + mediaUrl + "' />";
		tmp += "</audio>";
		return tmp;
	}

	public static String fullPreviewUrl(String sid, long docId, String fileVersion) {
		String url = GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "%26docId=" + docId
				+ "%26suffix=preview.swf";
		if (fileVersion != null)
			url += "%26fileVersion=" + fileVersion;
		return url;
	}

	public static String thumbnailUrl(String sid, long docId, String fileVersion) {
		String url = GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "&docId=" + docId
				+ "&random=" + new Date().getTime();
		if (fileVersion != null)
			url += "&fileVersion=" + fileVersion;
		return url;
	}

	public static String thumbnailImgageHTML(String sid, long docId, String fileVersion, Integer width, Integer height) {
		String style = "";
		if (width != null)
			style += "width:" + width + "px; ";
		if (height != null)
			style += "height:" + height + "px; ";

		String img = "<img src='" + thumbnailUrl(sid, docId, fileVersion) + "' style='" + style + "' />";
		return img;
	}

	public static String tileUrl(String sid, long docId, String fileVersion) {
		return thumbnailUrl(sid, docId, fileVersion) + "&suffix=tile.jpg";
	}

	public static String tileImgageHTML(String sid, long docId, String fileVersion, Integer width, Integer height) {
		String style = "";
		if (width != null)
			style += "width:" + width + "px; ";
		if (height != null)
			style += "height:" + height + "px; ";

		String img = "<img src='" + tileUrl(sid, docId, fileVersion) + "' style='" + style + "' />";
		return img;
	}

	public static String imageUrl(String imageName) {
		return imagePrefix() + imageName;
	}

	public static String flashUrl(String flashName) {
		return flashPrefix() + flashName;
	}

	public static String strip(String src) {
		if (src == null)
			return null;
		else
			return src.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	public static String contextPath() {
		return GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "");
	}

	public static void changeLocale(String locale) {
		redirect(contextPath() + GWT.getModuleName() + ".jsp?locale=" + locale);
	}

	public static String imagePrefix() {
		return contextPath() + "skin/images/";
	}

	public static String flashPrefix() {
		return contextPath() + "flash/";
	}

	/**
	 * Generates HTML image code.
	 * 
	 * @param imageName the image name
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageName) {
		return imageHTML(imageName, "", null);
	}

	public static boolean isPreviewable(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : OFFICE_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isOfficeFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : OFFICE_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isImageFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : IMAGE_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isWebContentFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : WEBCONTENT_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isMediaFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : VIDEO_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		for (String ext : AUDIO_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isAudioFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : AUDIO_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isOfficeFileType(String type) {
		for (String ext : OFFICE_EXTS) {
			if (type.equalsIgnoreCase(ext))
				return true;
		}
		return false;
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
		} else if (size / 1073741824 < 1) {
			str = (size / 1048576).toFixed(1) + " MBytes";
		} else {
			str = (size / 1073741824).toFixed(1) + " GBytes";
		}
		return str;
	}-*/;

	public static String formatLong(long number) {
		String str;
		NumberFormat fmt = NumberFormat.getFormat("#,###");
		str = fmt.format(number);
		str = str.replace(',', I18N.groupingSepator());
		return str;
	}

	/**
	 * Format file size in KB.
	 * 
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static String formatSizeKB(double size) {
		String str;
		if (size < 1) {
			str = "0 KB";
		} else if (size < 1024) {
			str = "1 KB";
		} else {
			NumberFormat fmt = NumberFormat.getFormat("#,###");
			str = fmt.format(Math.ceil(size / 1024)) + " KB";
			str = str.replace(',', I18N.groupingSepator());
		}
		return str;
	}

	/**
	 * Format file size in Windows 7 Style.
	 * 
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static String formatSizeW7(double size) {
		if (size < 0)
			return "";

		double KB = 1024;
		double MB = 1024 * KB;
		double GB = 1024 * MB;
		double TB = 1024 * GB;

		String str;
		if (size < 1) {
			str = "0 bytes";
		} else if (size < KB) {
			str = size + " bytes";
		} else if (size < MB) {
			double tmp = size / KB;
			if (tmp < 10) {
				NumberFormat fmt = NumberFormat.getFormat("###.##");
				str = fmt.format(tmp) + " KB";
			} else if (tmp < 100) {
				NumberFormat fmt = NumberFormat.getFormat("###.#");
				str = fmt.format(tmp) + " KB";
			} else {
				NumberFormat fmt = NumberFormat.getFormat("###");
				str = fmt.format(tmp) + " KB";
			}
			str = str.replace('.', I18N.decimalSepator());
		} else if (size < GB) {
			double tmp = size / MB;
			if (tmp < 10) {
				NumberFormat fmt = NumberFormat.getFormat("###.##");
				str = fmt.format(tmp) + " MB";
			} else if (tmp < 100) {
				NumberFormat fmt = NumberFormat.getFormat("###.#");
				str = fmt.format(tmp) + " MB";
			} else {
				NumberFormat fmt = NumberFormat.getFormat("###");
				str = fmt.format(tmp) + " MB";
			}
			str = str.replace('.', I18N.decimalSepator());
		} else if (size < TB) {
			double tmp = size / GB;
			if (tmp < 10) {
				NumberFormat fmt = NumberFormat.getFormat("###.##");
				str = fmt.format(tmp) + " GB";
			} else if (tmp < 100) {
				NumberFormat fmt = NumberFormat.getFormat("###.#");
				str = fmt.format(tmp) + " GB";
			} else {
				NumberFormat fmt = NumberFormat.getFormat("###");
				str = fmt.format(tmp) + " GB";
			}
			str = str.replace('.', I18N.decimalSepator());
		} else {
			double tmp = size / TB;
			if (tmp < 10) {
				NumberFormat fmt = NumberFormat.getFormat("###.##");
				str = fmt.format(tmp) + " TB";
			} else if (tmp < 100) {
				NumberFormat fmt = NumberFormat.getFormat("###.#");
				str = fmt.format(tmp) + " TB";
			} else {
				NumberFormat fmt = NumberFormat.getFormat("###");
				str = fmt.format(tmp) + " TB";
			}
			str = str.replace('.', I18N.decimalSepator());
		}
		return str;
	}

	/**
	 * Format file size in bytes
	 * 
	 * @param size The file size in bytes.
	 */
	public static String formatSizeBytes(double size) {
		String str;
		NumberFormat fmt = NumberFormat.getFormat("#,###");
		str = fmt.format(size) + " bytes";
		str = str.replace(',', I18N.groupingSepator());
		return str;
	}

	/**
	 * Format number percentage.
	 * 
	 * @param value The value to be formatted.
	 * @param fixed The number of decimal places.
	 * @return The formated value.
	 */
	public static native String formatPercentage(double value, int fixed) /*-{
		str = value.toFixed(fixed);

		return str + "%";
	}-*/;

	/**
	 * Get browser language
	 * 
	 * @return The language in ISO 639 format.
	 */
	public static native String getBrowserLanguage() /*-{
		var lang = navigator.language ? navigator.language
				: navigator.userLanguage;

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
			if (window.opera)
				return 'opera';
			var ua = navigator.userAgent.toLowerCase();
			if (ua.indexOf('webkit') != -1)
				return 'safari';
			if (ua.indexOf('msie 6.0') != -1)
				return 'ie6';
			if (ua.indexOf('msie 7.0') != -1)
				return 'ie7';
			if (ua.indexOf('gecko') != -1)
				return 'gecko';
			return 'unknown';
		} catch (e) {
			return 'unknown'
		}
	}-*/;

	public static native void copyToClipboard(String text) /*-{
		new $wnd.copyToClipboard(text);
	}-*/;

	public static native boolean isValidEmail(String email) /*-{
		var reg1 = /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)/; // not valid
		var reg2 = /^.+\@(\[?)[a-zA-Z0-9\-\.]+\.([a-zA-Z]{2,3}|[0-9]{1,3})(\]?)$/; // valid
		return !reg1.test(email) && reg2.test(email);
	}-*/;

	public static native void redirect(String url)
	/*-{
		$wnd.location.replace(url);
	}-*/;

	public static String padLeft(String s, int n) {
		if (s.length() > n) {
			return s.substring(0, n - 3) + "...";
		} else
			return s;
	}

	public static void openDropSpot() {
		Widget dropSpotApplet = RootPanel.get("DropSpot");
		dropSpotApplet.setSize("1", "1");
		String tmp = "<applet name=\"DropApplet\" archive=\""
				+ Util.contextPath()
				+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.upload.DropApplet\" width=\"1\" height=\"1\" mayscript>";
		tmp += "<param name=\"baseUrl\" value=\"" + Util.contextPath() + "\" />";
		tmp += "<param name=\"sid\" value=\"" + Session.get().getSid() + "\" />";
		tmp += "<param name=\"language\" value=\"" + I18N.getDefaultLocaleForDoc() + "\" />";
		tmp += "<param name=\"sizeMax\" value=\"" + Long.parseLong(Session.get().getInfo().getConfig("upload.maxsize"))
				* 1024 * 1024 + "\" />";
		tmp += "<param name=\"disallow\" value=\"" + Session.get().getInfo().getConfig("upload.disallow") + "\" />";
		tmp += "</applet>";
		dropSpotApplet.getElement().setInnerHTML(tmp);
	}

	/**
	 * Exports into the CSV format the content of a ListGrid.
	 * 
	 * @param listGrid Grid containing the data
	 * @param allFields True if all the fields(even if hidden) have to be
	 *        extracted
	 * 
	 * @return The CSV document as trying
	 */
	public static void exportCSV(ListGrid listGrid, boolean allFields) {
		StringBuilder stringBuilder = new StringBuilder(); // csv data in here

		// column names
		ListGridField[] fields = listGrid.getFields();
		if (allFields)
			fields = listGrid.getAllFields();
		for (int i = 0; i < fields.length; i++) {
			ListGridField listGridField = fields[i];
			if (listGridField.getType().equals(ListGridFieldType.ICON)
					|| listGridField.getType().equals(ListGridFieldType.IMAGE)
					|| listGridField.getType().equals(ListGridFieldType.IMAGEFILE)
					|| listGridField.getType().equals(ListGridFieldType.BINARY) || "".equals(listGridField.getTitle())
					|| "&nbsp;".equals(listGridField.getTitle()))
				continue;

			stringBuilder.append("\"");
			stringBuilder.append(listGridField.getTitle());
			stringBuilder.append("\";");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove last
																// ";"
		stringBuilder.append("\n");

		// column data
		Record[] records = new Record[0];
		try {
			records = listGrid.getRecords();
		} catch (Throwable t) {
		}

		if (records == null || records.length < 1) {
			/*
			 * In case of data bound grid, we need to call the original records
			 * list
			 */
			RecordList buf = listGrid.getOriginalRecordList();
			if (buf != null) {
				records = new Record[buf.getLength()];
				for (int i = 0; i < records.length; i++)
					records[i] = buf.get(i);
			}
		}

		for (int i = 0; i < records.length; i++) {
			Record record = records[i];

			for (int j = 0; j < fields.length; j++) {
				try {
					ListGridField listGridField = fields[j];
					if (listGridField.getType().equals(ListGridFieldType.ICON)
							|| listGridField.getType().equals(ListGridFieldType.IMAGE)
							|| listGridField.getType().equals(ListGridFieldType.IMAGEFILE)
							|| listGridField.getType().equals(ListGridFieldType.BINARY)
							|| "".equals(listGridField.getTitle()) || "&nbsp;".equals(listGridField.getTitle()))
						continue;

					stringBuilder.append("\"");
					if (listGridField.getType().equals(ListGridFieldType.DATE)) {
						stringBuilder.append(I18N.formatDateShort(record.getAttributeAsDate(listGridField.getName())));
					} else {
						stringBuilder.append(record.getAttribute(listGridField.getName()));
					}
					stringBuilder.append("\";");
				} catch (Throwable t) {
					/*
					 * May be that not all the rows are available, since we can
					 * count just on the rows that were rendered.
					 */
				}
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove
																	// last ";"
			stringBuilder.append("\n");
		}
		String content = stringBuilder.toString();

		/*
		 * Now post the CSV content to the server
		 */
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Util.contextPath() + "/csv?sid="
				+ Session.get().getSid());
		builder.setHeader("Content-type", "application/csv");

		try {
			builder.sendRequest(content, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Log.error(exception.getMessage(), null, exception);
				}

				public void onResponseReceived(Request request, Response response) {
					/*
					 * Now we can download the complete file
					 */
					WindowUtils.openUrl(GWT.getHostPageBaseURL() + "/csv?sid=" + Session.get().getSid());
				}
			});
		} catch (RequestException e) {
			GWT.log("error", e);
		}
	}

	/**
	 * Checks if the passed filename can be uploaded or not on the basis of what
	 * configured in 'upload.disallow'.
	 */
	public static boolean isAllowedForUpload(String filename) {
		Session session = Session.get();
		if (session == null)
			return true;
		String disallow = session.getConfig("upload.disallow");
		if (disallow == null || disallow.trim().isEmpty())
			return true;

		// Extract and normalize the extensions
		String[] disallowedExtensions = disallow.split(",");
		for (int i = 0; i < disallowedExtensions.length; i++) {
			disallowedExtensions[i] = disallowedExtensions[i].toLowerCase().trim();
			if (!disallowedExtensions[i].startsWith("."))
				disallowedExtensions[i] = "." + disallowedExtensions[i];
		}

		for (int i = 0; i < disallowedExtensions.length; i++)
			if (filename.toLowerCase().endsWith(disallowedExtensions[i]))
				return false;

		return true;
	}

	/**
	 * Detect tenant specification from the request
	 */
	public static String detectTenant() {
		RequestInfo request = WindowUtils.getRequestInfo();
		// Tries to capture tenant parameter
		String tenant = Constants.TENANT_DEFAULTNAME;
		if (request.getParameter(Constants.TENANT) != null && !request.getParameter(Constants.TENANT).equals("")) {
			tenant = request.getParameter(Constants.TENANT);
		}
		return tenant;
	}

	/**
	 * Detect SID specification from the request and then from the cookie
	 */
	public static String detectSid() {
		String sid = null;

		try {
			RequestInfo request = WindowUtils.getRequestInfo();
			sid = request.getParameter(Constants.SID);
		} catch (Throwable t) {
		}

		try {
			if (sid == null)
				sid = Offline.get(Constants.COOKIE_SID).toString();
		} catch (Throwable t) {
		}

		try {

			if (sid == null)
				sid = Cookies.getCookie(Constants.COOKIE_SID);
		} catch (Throwable t) {
		}

		return sid;
	}

	public static void redirectToRoot() {
		String base = GWT.getHostPageBaseURL();
		String url = base + (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/" + GWT.getModuleName() + ".jsp");
		url += "?locale=" + I18N.getLocale() + "&tenant=" + Session.get().getTenantName();
		Util.redirect(url);
	}
}
