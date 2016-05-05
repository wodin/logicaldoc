package com.logicaldoc.gui.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
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
 * @author Marco Meschieri - LogicalDOC
 * @since 7.1.1
 */
public class PreviewPanel extends VLayout {
	private HTMLFlow preview = null;

	private HTMLFlow media = null;

	private HTMLFlow html = null;

	private long id;

	private String fileName;

	private String fileVersion;

	private String language;

	public PreviewPanel(GUIDocument document) {
		this(document.getDocRef() != null ? document.getDocRef() : document.getId(), document.getFileVersion(),
				document.getFileName());
	}

	public PreviewPanel(long docId, String fileVersion, String filename) {
		this.id = docId;
		this.fileName = filename;
		this.fileVersion = fileVersion;

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
			String url = Util.downloadURL(id, fileVersion, false);

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
			String url = Util.contextPath() + "/prev/index.jsp?docId=" + id
					+ (fileVersion != null ? "&fileVersion=" + fileVersion : "") + "&locale=" + I18N.getLocale();

			contents = "<iframe src='" + url + "' style='border:0px solid white; width:" + (getWidth() - 1)
					+ "px; height:" + (getHeight() - 1)
					+ "px; overflow:hidden;'  scrolling='no' seamless='seamless'></iframe>";
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
}
