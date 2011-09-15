package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.Util;

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
		int size = Integer.parseInt(Session.get().getInfo().getConfig("gui.thumbnail.size"));
		Image image = new Image(url);
		image.setHeight(size + "px");

		addMember(image);
	}
}