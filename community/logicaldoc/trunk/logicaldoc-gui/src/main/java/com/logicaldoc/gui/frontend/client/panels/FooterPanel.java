package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.clipboard.ClipboardObserver;
import com.logicaldoc.gui.frontend.client.clipboard.ClipboardWindow;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * The program footer
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FooterPanel extends HLayout implements ClipboardObserver {
	private Label clipboardSize = new Label("0");

	public FooterPanel() {
		setHeight(20);
		setWidth100();
		setMembersMargin(2);
		setStyleName("footer");

		HLayout slot = new HLayout();
		slot.setMargin(2);
		slot.setMembersMargin(2);
		addMember(slot);

		// Prepare the clipboard image
		Image clipboardImage = new Image(Util.imageUrl("application/paste.gif"));
		clipboardImage.setWidth("16px");
		clipboardImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!Clipboard.getInstance().isEmpty())
					ClipboardWindow.getInstance().show();
			}
		});
		slot.addMember(clipboardImage);

		// And the size label
		slot.addMember(clipboardSize);

		slot = EventPanel.get();
		slot.setWidth100();
		addMember(slot);

		Clipboard.getInstance().addObserver(this);
	}

	@Override
	public void onAdd(GUIDocument entry) {
		clipboardSize.setText(Integer.toString(Clipboard.getInstance().size()));
	}

	@Override
	public void onRemove(GUIDocument entry) {
		clipboardSize.setText(Integer.toString(Clipboard.getInstance().size()));
	}
}