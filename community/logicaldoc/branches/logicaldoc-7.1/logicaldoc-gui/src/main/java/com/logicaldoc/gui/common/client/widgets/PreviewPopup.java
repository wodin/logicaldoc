package com.logicaldoc.gui.common.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HTMLPane;
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

	private HTMLFlow html = null;

	private HTMLFlow media = null;

	private HTMLFlow cad = null;

	private HTMLFlow htmlPanel = null;

	private VLayout layout = null;

	private long id;

	private String fileName;

	private String version;

	private boolean printEnabled = false;

	private String language;

	public PreviewPopup(long docId, String version, String filename, boolean printEnabled) {
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
		this.version = version;
		this.printEnabled = printEnabled;

		layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(5);

		retrieveUserInfo();

		if (Util.isMediaFile(filename.toLowerCase())) {
			reloadMedia();
		} else if (filename.toLowerCase().endsWith(".html") || filename.toLowerCase().endsWith(".htm")
				|| filename.toLowerCase().endsWith(".xhtml")) {
			reloadHTML();
		} else if (filename.toLowerCase().endsWith(".dxf")) {
			reloadCAD();
		} else {
			reloadPreview(language);
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
				if (html != null) {
					layout.removeMember(html);
					reloadPreview(language);
				} else if (htmlPanel != null) {
					layout.removeMember(htmlPanel);
					reloadHTML();
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
		if (version != null)
			url += "%26version=" + version;
		String tmp = Util.flashPreviewAudioVideo("player.swf", url, mediaProvider, (getWidth() - 26),
				(getHeight() - 40));
		media.setContents(tmp);
		layout.addMember(media);
	}

	/**
	 * Reloads a CAD preview.
	 */
	private void reloadCAD() {
		String url = Util.downloadURL(id, version, true);
		String tmp = "<applet name=\"CAD Applet\" archive=\"" + Util.contextPath()
				+ "applet/dxf-applet.jar\"  code=\"de.caff.dxf.applet.DxfApplet\" width=\"" + (getWidth() - 26)
				+ "\" height=\"" + (getHeight() - 40) + "\">";
		tmp += "<param name=\"dxf.file\" value=\"" + url + "\" />";
		tmp += "<param name=\"dxf.applet.gui.descr\" value=\"" + Util.contextPath() + "applet/"
				+ (printEnabled ? "Print.xml" : "Default.xml") + "\" />";
		tmp += "<param name=\"locale\" value=\"" + I18N.getLocale() + "\" /";
		tmp += "</applet>";

		HTMLFlow cad = new HTMLFlow();
		cad.setContents(tmp);
		layout.addMember(cad);
	}

	/**
	 * Reloads a preview for HTML documents.
	 */
	private void reloadHTML() {
		htmlPanel = new HTMLPane();
		htmlPanel.setShowEdges(false);
		htmlPanel.setContentsURL(Util.downloadURL(id, version, false));
		htmlPanel.setContentsType(ContentsType.FRAGMENT);

		layout.setWidth100();
		layout.setHeight(getHeight() - 30);
		layout.addMember(htmlPanel);
	}

	/**
	 * Reloads a preview.
	 */
	private void reloadPreview(String language) {
		html = new HTMLFlow();
		String contents = "";

		if (Feature.enabled(Feature.PREVIEW)) {
			contents = "<iframe src='" + Util.contextPath() + "/prev" + (printEnabled ? "" : "_ro") + "/index.jsp?sid="
					+ Session.get().getSid() + "&docId=" + id + (version != null ? "&version" + version : "")
					+ "&lang=" + getPreviewLanguage(language) + "&print=" + printEnabled + "&zoom=" + getZoom()
					+ "&key=" + Session.get().getInfo().getConfig("flexpaperviewer.key")
					+ "' style='border:0px solid white; width:" + (getWidth() - 15) + "px; height:"
					+ (getHeight() - 45) + "px; overflow:hidden;'  scrolling='no' seamless='seamless'></iframe>";
		} else {
			String url = Util.fullPreviewUrl(Session.get().getSid(), id, version);
			contents = Util.flashPreview((getWidth() - 15), (getHeight() - 40), getZoom(), "SwfFile=" + url,
					printEnabled, getPreviewLanguage(language));
		}

		html.setContents(contents);
		layout.addMember(html);
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