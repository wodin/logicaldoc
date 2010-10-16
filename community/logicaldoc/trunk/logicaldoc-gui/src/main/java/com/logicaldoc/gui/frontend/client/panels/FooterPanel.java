package com.logicaldoc.gui.frontend.client.panels;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.EventPanel;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * The program footer
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FooterPanel extends HLayout {

	public FooterPanel() {
		setHeight(20);
		setWidth100();
		setMembersMargin(2);
		setStyleName("footer");

		HLayout events = EventPanel.get();
		events.setWidth100();

		addMember(events);
		addMember(FooterIcons.get());
	}
}