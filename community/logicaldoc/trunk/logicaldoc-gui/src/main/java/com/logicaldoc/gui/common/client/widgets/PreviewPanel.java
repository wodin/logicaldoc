package com.logicaldoc.gui.common.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.log.Log;
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
	private HTMLFlow preview = null;

	private HTMLFlow media = null;

	private HTMLFlow html = null;

	private long id;

	private String fileName;

	private String fileVersion;

	private boolean printEnabled = false;

	private String language;

	private int zoom = 0;

	public PreviewPanel(GUIDocument document, Integer zoom) {
		this(document.getId(), document.getFileVersion(), document.getFileName(), false, zoom);
	}

	public PreviewPanel(long docId, String fileVersion, String filename, boolean printEnabled, Integer zoom) {
		this.id = docId;
		this.fileName = filename;
		this.fileVersion = fileVersion;
		this.printEnabled = printEnabled;

		retrieveUserInfo();

		if (zoom == null)
			try {
				this.zoom = new Integer(Session.get().getInfo().getConfig("gui.preview.zoom"));
			} catch (Throwable t) {
				this.zoom = 100;
			}
		else
			this.zoom = zoom;

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
		if (preview != null) {
			removeMember(preview);
			reloadPreview(language);
		} else if (html != null) {
			removeMember(html);
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
		media = new HTMLFlow();
		String contents = "";

		try {
			String url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + id;
			if (fileVersion != null)
				url += "&fileVersion=" + fileVersion;

			if (Util.isAudioFile(fileName))
				contents = Util.audioHTML(url);
			else
				contents = Util.videoHTML(url, getWidth() != null ? "" + (getWidth() - 2) : "",
						getHeight() != null ? "" + (getHeight() - 1) : "");
		} catch (Throwable t) {
			Log.info(t.getMessage(), null);
		}

		media.setContents(contents);
		addMember(media);
	}

	/**
	 * Reloads a preview for HTML documents.
	 */
	private void reloadHTML() {
		html = new HTMLPane();
		html.setShowEdges(false);
		html.setContentsURL(Util.downloadURL(id, fileVersion, false));
		html.setContentsType(ContentsType.FRAGMENT);

		setWidth100();
		addMember(html);
	}

	/**
	 * Reloads a preview.
	 */
	private void reloadPreview(String language) {
		preview = new HTMLFlow();
		String contents = "";

		try {
			if (Feature.enabled(Feature.PREVIEW)) {
				contents = "<iframe src='" + Util.contextPath() + "/prev" + (printEnabled ? "" : "_ro")
						+ "/index.jsp?sid=" + Session.get().getSid() + "&docId=" + id
						+ (fileVersion != null ? "&fileVersion=" + fileVersion : "") + "&lang=" + getPreviewLanguage(language)
						+ "&print=" + printEnabled + "&zoom=" + zoom + "&key="
						+ Session.get().getInfo().getConfig("flexpaperviewer.key")
						+ "' style='border:0px solid white; width:" + (getWidth() - 1) + "px; height:"
						+ (getHeight() - 1) + "px; overflow:hidden;'  scrolling='no' seamless='seamless'></iframe>";
			} else {
				String url = Util.fullPreviewUrl(Session.get().getSid(), id, fileVersion);
				contents = Util.flashPreview(getWidth() - 4, getHeight() - 4, zoom, "SwfFile=" + url, printEnabled,
						getPreviewLanguage(language));
			}
		} catch (Throwable t) {
		}

		preview.setContents(contents);
		addMember(preview);
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
