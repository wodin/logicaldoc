package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;

/**
 * This popup window is used to show the document preview.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PreviewPopup extends Window {

	private PreviewPanel previewPanel = null;

	public PreviewPopup(long docId, String fileVersion, String filename, boolean printEnabled) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("preview"));

		int size = 100;
		try {
			size = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.size"));
			if (size <= 0 || size > 100)
				size = 100;
		} catch (Throwable t) {

		}

		setWidth(Math.round((float) com.google.gwt.user.client.Window.getClientWidth() * (float) size / 100F));
		setHeight(Math.round((float) com.google.gwt.user.client.Window.getClientHeight() * (float) size / 100F));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		previewPanel = new PreviewPanel(docId, fileVersion, filename, printEnabled, null);
		previewPanel.setWidth100();
		previewPanel.setHeight100();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				previewPanel.destroy();
				destroy();
			}
		});

		addItem(previewPanel);
	}
}