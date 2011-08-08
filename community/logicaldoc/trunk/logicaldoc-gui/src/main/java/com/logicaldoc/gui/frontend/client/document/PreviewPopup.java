package com.logicaldoc.gui.frontend.client.document;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
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

	private boolean printEnabled = false;

	private String language;

	public PreviewPopup(long docId, String version, String filename) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("preview"));
		setWidth(680);
		setHeight(600);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		this.id = docId;
		this.fileName = filename;

		layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(5);

		retrieveUserInfo();

		if (Util.isMediaFile(filename.toLowerCase())) {
			reloadMedia();
		} else if (filename.toLowerCase().endsWith(".dxf")) {
			reloadCAD(printEnabled);
		} else {
			reloadImage(printEnabled, language);
		}

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
			}
		});

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				if (image != null) {
					layout.removeMember(image);
					reloadImage(printEnabled, language);
				} else if (media != null) {
					layout.removeMember(media);
					reloadMedia();
				} else if (cad != null) {
					layout.removeMember(cad);
					reloadCAD(printEnabled);
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
		String tmp = Util.flashPreviewAudioVideo("player.swf", url, mediaProvider, (getWidth() - 26),
				(getHeight() - 40));
		media.setContents(tmp);
		layout.addMember(media);
	}

	/**
	 * Reloads a CAD preview.
	 */
	private void reloadCAD(boolean printEnabled) {
		cad = new HTMLFlow();
		String url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + id;
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
	private void reloadImage(boolean printEnabled, String language) {
		image = new HTMLFlow();
		String url = GWT.getHostPageBaseURL() + "thumbnail?sid=" + Session.get().getSid() + "%26docId=" + id
				+ "%26suffix=preview.swf";
		String tmp = Util.flashPreview("flexpaperviewer.swf", (getWidth() - 26), (getHeight() - 40), "SwfFile=" + url,
				printEnabled, getPreviewLanguage(language));
		image.setContents(tmp);
		layout.addMember(image);
	}

	/**
	 * Retrieve the user information regarding the download permission and the
	 * user language.
	 */
	private void retrieveUserInfo() {
		GUIFolder folder = Session.get().getCurrentFolder();
		printEnabled = folder != null && folder.isDownload();
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

		return languages;
	}
}