package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;

/**
 * This panel shows the preview of a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PreviewPanel extends DocumentDetailTab {

	public PreviewPanel(final GUIDocument document) {
		super(document, null);

		if (Feature.enabled(Feature.PREVIEW)) {
			if (Util.isImageFile(document.getFileName())) {
				Image preview = new Image("thumbnail?docId=" + document.getId() + "&fileVersion="
						+ document.getFileVersion());
				addMember(preview);
			} else {
				HTML applet = new HTML();
				String tmp = "<applet name=\"PreviewApplet\" archive=\""
						+ Util.contextPath()
						+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.preview.PreviewApplet\" width=\"600\" height=\"400\">";
				tmp += "<param name=\"url\" value=\"" + GWT.getHostPageBaseURL() + "convertpdf?sid="
						+ Session.get().getSid() + "&docId=" + document.getId() + "&version=" + document.getVersion()
						+ "\" />";
				tmp += "</applet>";
				applet.setHTML(tmp);
				applet.setWidth("620px");
				applet.setHeight("400px");
				addMember(applet);
			}
		} else
			addMember(new FeatureDisabled());
	}
}