package com.logicaldoc.gui.frontend.client.system;

import com.smartgwt.client.widgets.layout.VLayout;

/**
 * General adnministration panel with statistics
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GeneralPanel extends VLayout {

	public GeneralPanel() {
		setWidth100();
		setHeight100();
		setMembersMargin(10);

		PieStats charts = new PieStats();

		GeneralBottom bottom = new GeneralBottom();
		bottom.setHeight(200);

		setMembers(charts, bottom);
	}
}
