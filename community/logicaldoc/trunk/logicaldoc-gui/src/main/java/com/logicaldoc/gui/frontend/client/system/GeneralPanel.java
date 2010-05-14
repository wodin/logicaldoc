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

		SystemStatistics top = new SystemStatistics();
		top.setHeight("60%");
		top.setShowResizeBar(true);

		GeneralBottom bottom = new GeneralBottom();
		bottom.setHeight("40%");

		setMembers(top, bottom);
	}
}
