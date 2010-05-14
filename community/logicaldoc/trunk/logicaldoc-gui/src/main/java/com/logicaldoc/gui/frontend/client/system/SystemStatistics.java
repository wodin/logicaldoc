package com.logicaldoc.gui.frontend.client.system;

import com.smartgwt.client.widgets.layout.HLayout;

/**
 * This panel shows some system statistics
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SystemStatistics extends HLayout {
	public SystemStatistics() {
		setMembersMargin(30);
		addMember(new StatisticsPie("Documents"));
		addMember(new StatisticsPie("Folders"));
		addMember(new StatisticsPie("Disk Usage"));
	}
}
