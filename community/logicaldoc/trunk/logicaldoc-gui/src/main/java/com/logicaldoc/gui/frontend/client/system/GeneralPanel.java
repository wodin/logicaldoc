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

		// PieLegends legends = new PieLegends();
		// legends.setShowResizeBar(true);

		GeneralBottom bottom = new GeneralBottom();
		bottom.setHeight(210);

		setMembers(charts, bottom);

		// setMembers(charts, legends, bottom);
	}
}
