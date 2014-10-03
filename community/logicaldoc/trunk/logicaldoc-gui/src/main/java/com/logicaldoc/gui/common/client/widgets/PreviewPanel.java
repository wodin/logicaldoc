package com.logicaldoc.gui.common.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel is used to show the document preview.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.1
 */
public class PreviewPanel extends VLayout {
	private HTMLFlow html = null;

	private HTMLFlow media = null;

	private HTMLFlow htmlPanel = null;

	private long id;

	private String fileName;

	private String version;

	private boolean printEnabled = false;

	private String language;

	public PreviewPanel(GUIDocument document) {
		this(document.getId(), document.getVersion(), document.getFileName(), false);
	}

	public PreviewPanel(long docId, String version, String filename, boolean printEnabled) {
		this.id = docId;
		this.fileName = filename;
		this.version = version;
		this.printEnabled = printEnabled;

		retrieveUserInfo();

		if (Util.isMediaFile(filename.toLowerCase())) {
			reloadMedia();
		} else if (filename.toLowerCase().endsWith(".html") || filename.toLowerCase().endsWith(".htm")
				|| filename.toLowerCase().endsWith(".xhtml")) {
			reloadHTML();
		} else {
			reloadPreview(language);
		}

		redraw();

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				redraw();
			}
		});
	}

	public void redraw() {
		if (html != null) {
			removeMember(html);
			reloadPreview(language);
		} else if (htmlPanel != null) {
			removeMember(htmlPanel);
			reloadHTML();
		} else if (media != null) {
			removeMember(media);
			reloadMedia();
		}
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
		addMember(media);
	}

	/**
	 * Reloads a preview for HTML documents.
	 */
	private void reloadHTML() {
		htmlPanel = new HTMLPane();
		htmlPanel.setShowEdges(false);
		htmlPanel.setContentsURL(Util.downloadURL(id, version, false));
		htmlPanel.setContentsType(ContentsType.FRAGMENT);

		setWidth100();
		setHeight(getHeight() - 30);
		addMember(htmlPanel);
	}

	/**
	 * Reloads a preview.
	 */
	private void reloadPreview(String language) {
		html = new HTMLFlow();
		String contents = "";

		try {
			if (Feature.enabled(Feature.PREVIEW)) {
				// contents = "<iframe src='" + Util.contextPath() + "/prev" +
				// (printEnabled ? "" : "_ro")
				// + "/index.jsp?sid=" + Session.get().getSid() + "&docId=" + id
				// + (version != null ? "&version" + version : "") + "&lang=" +
				// getPreviewLanguage(language)
				// + "&print=" + printEnabled + "&zoom=" + getZoom() + "&key="
				// + Session.get().getInfo().getConfig("flexpaperviewer.key")
				// + "' style='border:0px solid white; width:" + (getWidth() -
				// 15) + "px; height:"
				// + (getHeight() - 45) +
				// "px; overflow:hidden;'  scrolling='no' seamless='seamless'></iframe>";
				contents = "<iframe src='" + Util.contextPath() + "/prev" + (printEnabled ? "" : "_ro")
						+ "/index.jsp?sid=" + Session.get().getSid() + "&docId=" + id
						+ (version != null ? "&version" + version : "") + "&lang=" + getPreviewLanguage(language)
						+ "&print=" + printEnabled + "&zoom=" + getZoom() + "&key="
						+ Session.get().getInfo().getConfig("flexpaperviewer.key")
						+ "' style='border:0px solid white; width:" + (getWidth() - 1) + "px; height:"
						+ (getHeight() - 1) + "px; overflow:hidden;'  scrolling='no' seamless='seamless'></iframe>";
			} else {
				String url = Util.fullPreviewUrl(Session.get().getSid(), id, version);
				contents = Util.flashPreview((getWidth() - 15), (getHeight() - 40), getZoom(), "SwfFile=" + url,
						printEnabled, getPreviewLanguage(language));
			}
		} catch (Throwable t) {
		}

		html.setContents(contents);
		addMember(html);
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
