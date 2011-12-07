package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.ImageViewer;

/**
 * This panel shows the notes on a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class ThumbnailPanel extends DocumentDetailTab {

	public ThumbnailPanel(final GUIDocument document) {
		super(document, null);
		setMembersMargin(1);

		String url = Util.contextPath() + "/thumbnail?docId=" + document.getId() + "&sid=" + Session.get().getSid();

		ImageViewer viewer = new ImageViewer(url);
		setMembers(viewer);
	}
}