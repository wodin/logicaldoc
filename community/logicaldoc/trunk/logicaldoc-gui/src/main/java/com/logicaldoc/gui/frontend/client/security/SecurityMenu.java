package com.logicaldoc.gui.frontend.client.security;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the saved searches of the user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityMenu extends VLayout {

	public SecurityMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button users = new Button(I18N.getMessage("users"));
		users.setWidth100();
		users.setHeight(25);

		Button groups = new Button(I18N.getMessage("groups"));
		groups.setWidth100();
		groups.setHeight(25);

		setMembers(users, groups);

		users.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new UsersPanel());
			}
		});

		groups.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new GroupsPanel());
			}
		});
	}
}