package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
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
			Image preview = new Image("thumbnail?docId=" + document.getId() + "&fileVersion="
					+ document.getFileVersion());
			addMember(preview);
		} else
			addMember(new FeatureDisabled());
	}
}