package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.UserObserver;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
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
public class StatusBarIcons extends HLayout implements ClipboardObserver, UserObserver {
	private static StatusBarIcons instance;

	private HTML clipboardSize = new HTML("0");

	private HTML lockedCount = new HTML("0");

	private HTML checkoutCount = new HTML("0");

	private HTML messagesCount = new HTML("0");

	private HTML workflowsCount = new HTML("0");

	private StatusBarIcons() {
		Img clipboardImage = ItemFactory.newImgIcon("page_white_paste.png");
		clipboardImage.setHeight("16px");
		clipboardImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!Clipboard.getInstance().isEmpty())
					ClipboardWindow.getInstance().show();
			}
		});
		clipboardImage.setCursor(Cursor.HAND);
		clipboardImage.setTooltip(I18N.message("clipboard"));

		Img lockedImage = ItemFactory.newImgIcon("page_white_lock.png");
		lockedImage.setHeight("16px");
		lockedImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPanel.get().selectUserTab();
			}
		});
		lockedImage.setCursor(Cursor.HAND);
		lockedImage.setTooltip(I18N.message("event.lockeddocs"));

		Img checkoutImage = ItemFactory.newImgIcon("page_edit.png");
		checkoutImage.setHeight("16px");
		checkoutImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPanel.get().selectUserTab();
			}
		});
		checkoutImage.setCursor(Cursor.HAND);
		checkoutImage.setTooltip(I18N.message("event.checkedoutdocs"));

		Img messageImage = ItemFactory.newImgIcon("mail.png");
		messageImage.setHeight("16px");
		messageImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPanel.get().selectMessagesTab();
			}
		});
		messageImage.setCursor(Cursor.HAND);
		messageImage.setTooltip(I18N.message("messages"));

		Img workflowImage = ItemFactory.newImgIcon("cog.png");
		workflowImage.setHeight("16px");
		workflowImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPanel.get().selectWorkflowTab();
			}
		});
		workflowImage.setCursor(Cursor.HAND);
		workflowImage.setTooltip(I18N.message("workflowtasksassigned"));

		clipboardSize.setWidth("20px");
		lockedCount.setWidth("20px");
		checkoutCount.setWidth("20px");
		messagesCount.setWidth("20px");
		workflowsCount.setWidth("20px");

		addMember(clipboardImage);
		addMember(clipboardSize);
		addMember(lockedImage);
		addMember(lockedCount);
		addMember(checkoutImage);
		addMember(checkoutCount);

		if (Feature.enabled(Feature.MESSAGES)) {
			addMember(messageImage);
			addMember(messagesCount);
		}

		if (Feature.enabled(Feature.WORKFLOW)) {
			addMember(workflowImage);
			addMember(workflowsCount);
		}

		Clipboard.getInstance().addObserver(this);
		Session.get().getUser().addObserver(this);
		onUserChanged(Session.get().getUser(), null);
	}

	public static StatusBarIcons get() {
		if (instance == null)
			instance = new StatusBarIcons();
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
	public void onUserChanged(GUIUser user, String attribute) {
		checkoutCount.setText(Integer.toString(user.getCheckedOutDocs()));
		lockedCount.setText(Integer.toString(user.getLockedDocs()));
		messagesCount.setText(Integer.toString(user.getUnreadMessages()));
		workflowsCount.setText(Integer.toString(user.getActiveTasks()));
	}
}