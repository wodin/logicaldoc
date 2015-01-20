package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;

public class ImageLightbox extends Window {
	public ImageLightbox(GUIDocument document) {
		int size = 800;
		if (Session.get().getConfig("gui.thumbnail.size") != null)
			size = Integer.parseInt(Session.get().getConfig("gui.thumbnail.size"));
		int windowHeight = size + 10;
		if (windowHeight > com.google.gwt.user.client.Window.getClientHeight())
			windowHeight = com.google.gwt.user.client.Window.getClientHeight();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(document.getTitle());
		setWidth("50%");
		setHeight(windowHeight);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		HTMLPanel html = new HTMLPanel(Util.thumbnailImgageHTML(Session.get().getSid(), document.getId(), null, null, size));
		addItem(html);
	}
}
