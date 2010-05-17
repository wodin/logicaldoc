package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.UserObserver;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.clipboard.ClipboardObserver;
import com.logicaldoc.gui.frontend.client.clipboard.ClipboardWindow;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Container for a set of clickable icons representing the program state.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FooterIcons extends HLayout implements ClipboardObserver, UserObserver {
	private static FooterIcons instance;

	private HTML clipboardSize = new HTML("0");

	private HTML lockedCount = new HTML("0");

	private HTML checkoutCount = new HTML("0");

	private FooterIcons() {
		Img clipboardImage = new Img(Util.imageUrl("application/paste.gif"));
		clipboardImage.setWidth("16px");
		clipboardImage.setHeight("16px");
		clipboardImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!Clipboard.getInstance().isEmpty())
					ClipboardWindow.getInstance().show();
			}
		});
		clipboardImage.setCursor(Cursor.HAND);

		Img lockedImage = new Img(Util.imageUrl("application/document_lock.png"));
		lockedImage.setWidth("16px");
		lockedImage.setHeight("16px");
		lockedImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPanel.get().selectDashboardTab();
			}
		});
		lockedImage.setCursor(Cursor.HAND);

		Img checkoutImage = new Img(Util.imageUrl("application/page_edit.png"));
		checkoutImage.setWidth("16px");
		checkoutImage.setHeight("16px");
		checkoutImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPanel.get().selectDashboardTab();
			}
		});
		checkoutImage.setCursor(Cursor.HAND);

		clipboardSize.setWidth("20px");
		lockedCount.setWidth("20px");
		checkoutCount.setWidth("20px");

		addMember(clipboardImage);
		addMember(clipboardSize);
		addMember(lockedImage);
		addMember(lockedCount);
		addMember(checkoutImage);
		addMember(checkoutCount);

		Clipboard.getInstance().addObserver(this);
		Session.get().getUser().addObserver(this);
		onUserChanged(Session.get().getUser());
	}

	public static FooterIcons get() {
		if (instance == null)
			instance = new FooterIcons();
		return instance;
	}

	@Override
	public void onAdd(GUIDocument entry) {
		clipboardSize.setText(Integer.toString(Clipboard.getInstance().size()));
	}

	@Override
	public void onRemove(GUIDocument entry) {
		clipboardSize.setText(Integer.toString(Clipboard.getInstance().size()));
	}

	@Override
	public void onUserChanged(GUIUser user) {
		checkoutCount.setText(Integer.toString(user.getCheckedOutDocs()));
		lockedCount.setText(Integer.toString(user.getLockedDocs()));
	}
}