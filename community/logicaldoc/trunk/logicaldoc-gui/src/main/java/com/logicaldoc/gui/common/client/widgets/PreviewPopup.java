package com.logicaldoc.gui.common.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to show the document preview.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PreviewPopup extends Window {

	private HTMLFlow image = null;

	private HTMLFlow media = null;

	private HTMLFlow cad = null;

	private VLayout layout = null;

	private long id;

	private String fileName;

	private String fileVersion;

	private boolean printEnabled = false;

	private String language;

	public PreviewPopup(long docId, String fileVersion, String filename, boolean printEnabled) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("preview"));

		int size = 100;
		try {
			size = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.size"));
			if (size <= 0 || size > 100)
				size = 100;
		} catch (Throwable t) {

		}

		setWidth(Math.round((float) com.google.gwt.user.client.Window.getClientWidth() * (float) size / 100F));
		setHeight(Math.round((float) com.google.gwt.user.client.Window.getClientHeight() * (float) size / 100F));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		this.id = docId;
		this.fileName = filename;
		this.fileVersion = fileVersion;
		this.printEnabled = printEnabled;

		layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(5);

		retrieveUserInfo();

		if (Util.isMediaFile(filename.toLowerCase())) {
			reloadMedia();
		} else if (filename.toLowerCase().endsWith(".dxf")) {
			reloadCAD();
		} else {
			reloadImage(language);
		}

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				if (image != null) {
					layout.removeMember(image);
					reloadImage(language);
				} else if (media != null) {
					layout.removeMember(media);
					reloadMedia();
				} else if (cad != null) {
					layout.removeMember(cad);
					reloadCAD();
				}
			}
		});

		addChild(layout);
	}

	/**
	 * Reloads a media preview.
	 */
	private void reloadMedia() {
		String mediaProvider = "";
		String f = fileName.toLowerCase();

		if (f.endsWith(".mp3") || f.endsWith(".wav") || f.endsWith(".wma")) {
			mediaProvider = "sound";
		} else {
			mediaProvider = "video";
		}

		media = new HTMLFlow();
		String url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "%26docId=" + id
				+ "%26filename=" + fileName;
		if (fileVersion != null)
			url += "%26fileVersion=" + fileVersion;
		String tmp = Util.flashPreviewAudioVideo("player.swf", url, mediaProvider, (getWidth() - 26),
				(getHeight() - 40));
		media.setContents(tmp);
		layout.addMember(media);
	}

	/**
	 * Reloads a CAD preview.
	 */
	private void reloadCAD() {
		cad = new HTMLFlow();
		String url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + id;
		if (fileVersion != null)
			url += "%26fileVersion=" + fileVersion;

		String tmp = "<applet name=\"CAD Applet\" archive=\"" + Util.contextPath()
				+ "applet/dxf-applet.jar\"  code=\"de.caff.dxf.applet.DxfApplet\" width=\"" + (getWidth() - 26)
				+ "\" height=\"" + (getHeight() - 40) + "\">";
		tmp += "<param name=\"dxf.file\" value=\"" + url + "\" />";
		tmp += "<param name=\"dxf.applet.gui.descr\" value=\"" + Util.contextPath() + "applet/"
				+ (printEnabled ? "Print.xml" : "Default.xml") + "\" />";
		tmp += "<param name=\"locale\" value=\"" + I18N.getLocale() + "\" /";
		tmp += "</applet>";

		cad.setContents(tmp);
		layout.addMember(cad);
	}

	/**
	 * Reloads an image preview.
	 */
	private void reloadImage(String language) {
		image = new HTMLFlow();
		String url = GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "%26docId=" + id
				+ "%26suffix=preview.swf";
		if (fileVersion != null)
			url += "%26fileVersion=" + fileVersion;

		String flash = "flexpaperviewer.swf";
		if (!printEnabled)
			flash = "flexpaperviewer_ro.swf";
		String tmp = Util.flashPreview(flash, (getWidth() - 26), (getHeight() - 40), getZoom(), "SwfFile=" + url,
				printEnabled, getPreviewLanguage(language));
		image.setContents(tmp);
		layout.addMember(image);
	}

	/**
	 * Retrieve the user information regarding the download permission and the
	 * user language.
	 */
	private void retrieveUserInfo() {
		GUIUser user = Session.get().getUser();
		language = user.getLanguage();
	}

	/**
	 * Retrieves the correct language code for the preview viewer for the given
	 * language.
	 */
	public static String getPreviewLanguage(String language) {
		return getPreviewLanguageMap().get(language) != null ? getPreviewLanguageMap().get(language) : "en_US";
	}

	public static int getZoom() {
		try {
			return Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.zoom"));
		} catch (Throwable t) {
			return 100;
		}
	}

	/**
	 * Retrieves the correct language code for the preview viewer.
	 */
	private static Map<String, String> getPreviewLanguageMap() {
		Map<String, String> languages = new HashMap<String, String>();
		languages.put("en", "en_US");
		languages.put("it", "it_IT");
		languages.put("de", "de_DE");
		languages.put("fr", "fr_FR");
		languages.put("es", "es_ES");
		languages.put("zh", "zh_CN");
		languages.put("pt", "pt_BR");
		languages.put("ru", "ru_RU");
		languages.put("fi", "fi_FN");
		languages.put("nl", "nl_NL");
		languages.put("tr", "tr_TR");
		languages.put("se", "se_SE");
		languages.put("pt", "pt_PT");
		languages.put("el", "el_EL");
		languages.put("da", "da_DN");
		languages.put("cz", "cz_CS");
		languages.put("pl", "pl_PL");
		languages.put("pv", "pv_FN");
		languages.put("hu", "hu_HU");
		languages.put("ja", "ja_JA");

		return languages;
	}
}