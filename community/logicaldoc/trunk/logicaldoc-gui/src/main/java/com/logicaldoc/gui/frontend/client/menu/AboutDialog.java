package com.logicaldoc.gui.frontend.client.menu;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.MessageLabel;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * This is the about dialog.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AboutDialog extends Window {
	public AboutDialog() {
		super();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.CLOSE_BUTTON);
		setTitle(" ");
		setWidth(290);
		setHeight(180);
		centerInPage();
		setPadding(5);

		Img logoImage = ItemFactory.newBrandImg("logo.png");
		logoImage.setHeight("40px");
		logoImage.setWidth("205px");

		Label version = new Label(I18N.message("version") + " " + Session.get().getInfo().getRelease());
		version.setShowEdges(false);
		version.setHeight(20);
		version.setWidth100();
		version.setWrap(false);

		GUIMessage message = new GUIMessage(Session.get().getInfo().getUrl(), 0);
		message.setUrl(Session.get().getInfo().getUrl());
		MessageLabel url = new MessageLabel(message);

		Label copyryght = new Label("&copy; " + Session.get().getInfo().getYear() + " "
				+ Session.get().getInfo().getVendor());
		copyryght.setShowEdges(false);
		copyryght.setWrap(false);
		copyryght.setHeight(20);
		
		VStack content = new VStack();
		content.setMembersMargin(5);
		content.setTop(20);
		content.setMargin(4);
		content.setBackgroundColor("#eeeeee");
		content.setWidth100();
		content.setMembers(logoImage, version, url, copyryght);

		setBackgroundColor("#eeeeee");
		addChild(content);
	}
}