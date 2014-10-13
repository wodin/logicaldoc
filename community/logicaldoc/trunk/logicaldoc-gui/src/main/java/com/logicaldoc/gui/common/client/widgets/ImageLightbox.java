package com.logicaldoc.gui.common.client.widgets;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

public class ImageLightbox extends Window {
	public ImageLightbox(GUIDocument document) {
		int size = Integer.parseInt(Session.get().getConfig("gui.thumbnail.size")) + 10;
		if (size > com.google.gwt.user.client.Window.getClientHeight())
			size = com.google.gwt.user.client.Window.getClientHeight();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(document.getTitle());
		setWidth("50%");
		setHeight(size);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		VLayout layout = new VLayout();
		layout.setMembersMargin(2);
		layout.setMargin(1);

		String url = Util.contextPath() + "/preview?docId=" + document.getId() + "&sid=" + Session.get().getSid();
		Image image = new Image(url);
		layout.addMember(image);

		addItem(layout);
	}
}
