package com.logicaldoc.gui.frontend.client.system;

import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows the charts pies
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PieCharts extends HLayout {

	public PieCharts() {
		super();
		setMembersMargin(30);
		setWidth100();
		addMember(new StatisticWidget("Documents"));
		addMember(new StatisticWidget("Folders"));
		addMember(new StatisticWidget("Disk Usage"));
	}

}
