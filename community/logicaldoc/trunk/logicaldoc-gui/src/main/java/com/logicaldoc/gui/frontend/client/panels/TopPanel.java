package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.security.LoginInfo;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * The Login entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TopPanel extends HLayout {
	public TopPanel() {
		setStyleName("topPanel");
		setWidth100();
		setHeight(45);

		// Prepare the logo image to be shown inside the banner
		Image logoImage = new Image(Util.imageUrl("brand/logo_head.png"));
		logoImage.setStyleName("topLogo");
		addMember(logoImage);

		// Place the search box
		QuickSearch quickSearch = new QuickSearch();
		quickSearch.setWidth("*");
		quickSearch.setAlign(Alignment.CENTER);
		addMember(quickSearch);

		// Anchor console = new Anchor();
		// console.setText("console");
		// console.addClickHandler(new
		// com.google.gwt.event.dom.client.ClickHandler() {
		// @Override
		// public void onClick(com.google.gwt.event.dom.client.ClickEvent event)
		// {
		// SC.showConsole();
		// }
		// });
		// addMember(console);

		// Place login informations
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setWidth("200px");
		loginInfo.setAlign(Alignment.RIGHT);
		addMember(loginInfo);
	}
}