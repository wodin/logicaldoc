package com.logicaldoc.gui.frontend.client.security;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the login informations and the logout button
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LoginInfo extends VLayout {
	public LoginInfo(String stylePrefix) {
		setHeight(30);
		setAlign(Alignment.RIGHT);

		Label user = new Label(Session.get().getUser().getFullName());
		user.setHeight(10);
		user.setAlign(Alignment.RIGHT);
		user.setMargin(2);
		user.setStyleName(stylePrefix + "loginInfo");

		addMember(user);

	}
}