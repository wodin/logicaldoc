package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.widgets.ImageViewer;

/**
 * This panel shows the notes on a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class ThumbnailPanel extends DocumentDetailTab {

	ImageViewer viewer = null;

	public ThumbnailPanel(final GUIDocument document) {
		super(document, null);
		setMembersMargin(1);
	}

	@Override
	protected void onTabSelected() {
		if (viewer == null) {
			ImageViewer viewer = new ImageViewer(document);
			setMembers(viewer);
		}
	}
}