package com.logicaldoc.gui.common.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class Util {
	public static String[] OFFICE_EXTS = new String[] { ".doc", ".xls", ".ppt", ".docx", ".xlsx", ".pptx" };

	public static String[] IMAGE_EXTS = new String[] { ".gif", ".jpg", ".jpeg", ".bmp", ".tif", ".tiff", ".png" };

	public static String[] MEDIA_EXTS = new String[] { ".mp3", ".mp4", ".wav", ".avi", ".mpg", ".wmv", ".wma", ".asf",
			".mov", ".rm", ".flv", ".aac", ".vlc", ".ogg", ".webm", ".swf", ".mpeg", ".swf" };

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

	/**
	 * Generates Flash code
	 */
	public static String flashHTML(String flashName, int width, int height, String flashvars) {
		String tmp = "<div align=\"center\"><object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0\" width=\""
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
	 * Generates Flash code for Image Preview
	 */
	public static String flashPreview(String flashName, int width, int height, int zoom, String flashvars,
			boolean printEnabled, String language) {

		String key = Session.get().getInfo().getConfig("flexpaperviewer.key");

		Float fzoom = new Float(1.0F);
		try {
			fzoom = new Float(((float) zoom) / 100);
		} catch (Throwable t) {

		}

		String vars = flashvars + "&Scale=" + (zoom > 0 ? fzoom.toString() : "1.0") + "&FitPageOnLoad=false&FitWidthOnLoad="
				+ (zoom <= 0 ? "true" : "false") + "&PrintEnabled=" + printEnabled + "&ProgressiveLoading=true"
				+ "&ViewModeToolsVisible=true" + "&ZoomToolsVisible=true" + "&NavToolsVisible=true"
				+ "&CursorToolsVisible=true" + "&SearchToolsVisible=true";

		if (key != null) {
			vars += "&key=" + key;
		}

		String tmp = "<div align=\"center\">";
		tmp += "<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" id=\"FlexPaperViewer\" width=\"" + width
				+ "\" height=\"" + height
				+ "\" codebase=\"http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab\">\n";
		tmp += " <param name=\"allowScriptAccess\" value=\"always\" />\n";
		tmp += " <param name=\"allowFullScreen\" value=\"true\" />\n";
		tmp += " <param name=\"movie\" value=\"" + Util.flashUrl(flashName) + "\" />\n";
		tmp += " <param name=\"quality\" value=\"high\" />\n";
		tmp += " <param name=\"bgcolor\" value=\"#ffffff\" />\n";
		tmp += " <param name=\"flashvars\" value=\"" + vars + "\" />\n";
		tmp += " 	<embed type=\"application/x-shockwave-flash\" src=\"" + Util.flashUrl(flashName) + "\" height=\""
				+ height + "\" width=\"" + width + "\" name=\"FlexPaperViewer\""
				+ " bgcolor=\"#ffffff\" quality=\"high\" allowFullScreen=\"true\" flashvars=\"" + vars + "\">\n";
		tmp += "</embed>\n";
		tmp += "</object></div>\n";

		return tmp;
	}

	/**
	 * Generates Flash code for Audio and Video Preview
	 */
	public static String flashPreviewAudioVideo(String flashName, String mediaUrl, String mediaProvider, int width,
			int height) {
		String vars = "file=" + mediaUrl + "&provider=" + mediaProvider + "&autostart=true";

		String tmp = "<div align=\"center\"><object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0\" width=\""
				+ width + "\" height=\"" + height + "\" align=\"middle\">\n";
		tmp += " <param name=\"allowScriptAccess\" value=\"always\" />\n";
		tmp += " <param name=\"allowFullScreen\" value=\"true\" />\n";
		tmp += " <param name=\"movie\" value=\"" + Util.flashUrl(flashName) + "\" />\n";
		tmp += " <param name=\"quality\" value=\"high\" />\n";
		tmp += " <param name=\"bgcolor\" value=\"#ffffff\" />\n";
		tmp += " <param name=\"flashvars\" value=\"" + vars + "\" />";
		tmp += "	<embed type=\"application/x-shockwave-flash\" src=\"" + Util.flashUrl(flashName) + "\" height=\""
				+ height + "\" width=\"" + width + "\""
				+ " bgcolor=\"#ffffff\" quality=\"high\" allowFullScreen=\"true\" flashvars=\"" + vars + "\">\n";
		tmp += "</object></div>\n";
		return tmp;
	}
	
	public static String imageUrl(String imageName) {
		return imagePrefix() + imageName;
	}

	public static String flashUrl(String flashName) {
		return flashPrefix() + flashName;
	}

	public static String brandUrl(String imageName) {
		return brandPrefix() + imageName;
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

	public static String brandPrefix() {
		return contextPath() + "skin/brand/";
	}

	public static String flashPrefix() {
		return contextPath() + "flash/";
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

	public static boolean isMediaFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : MEDIA_EXTS) {
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

	public static void showWaitCursor() {
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
	}

	public static void showDefaultCursor() {
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
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
		String str;
		if (size < 1) {
			str = "0 bytes";
		} else if (size < 1024) {
			str = size + " bytes";
		} else if (size < 1048576) {
			double tmp = size / 1024;
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
		} else {
			double tmp = size / 1048576;
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
		tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath() + "servlet.gupld?new_session=true&sid="
				+ Session.get().getSid() + "\" />";
		tmp += "</applet>";
		dropSpotApplet.getElement().setInnerHTML(tmp);
	}

	/**
	 * Exports into the CSV format the content of a ListGrid.
	 * 
	 * @param listGrid Grid containing the data
	 * @return The CSV document as tring
	 */
	public static void exportCSV(ListGrid listGrid) {
		StringBuilder stringBuilder = new StringBuilder(); // csv data in here

		// column names
		ListGridField[] fields = listGrid.getFields();
		for (int i = 0; i < fields.length; i++) {
			ListGridField listGridField = fields[i];
			if (listGridField.getType().equals(ListGridFieldType.ICON)
					|| listGridField.getType().equals(ListGridFieldType.IMAGE)
					|| listGridField.getType().equals(ListGridFieldType.IMAGEFILE)
					|| listGridField.getType().equals(ListGridFieldType.BINARY))
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

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_dateshort"));
		for (int i = 0; i < records.length; i++) {
			Record record = records[i];
			ListGridField[] listGridFields = listGrid.getFields();

			for (int j = 0; j < listGridFields.length; j++) {
				try {
					ListGridField listGridField = listGridFields[j];
					if (listGridField.getType().equals(ListGridFieldType.ICON)
							|| listGridField.getType().equals(ListGridFieldType.IMAGE)
							|| listGridField.getType().equals(ListGridFieldType.IMAGEFILE)
							|| listGridField.getType().equals(ListGridFieldType.BINARY))
						continue;

					stringBuilder.append("\"");
					if (listGridField.getType().equals(ListGridFieldType.DATE)) {
						stringBuilder.append(formatter.format(record.getAttributeAsDate(listGridField.getName())));
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
}
