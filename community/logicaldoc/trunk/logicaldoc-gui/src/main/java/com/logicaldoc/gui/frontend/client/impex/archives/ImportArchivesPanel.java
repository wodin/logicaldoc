package com.logicaldoc.gui.frontend.client.impex.archives;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Panel showing export archives control panel
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImportArchivesPanel extends VLayout {

	public ImportArchivesPanel() {
		setWidth100();
		TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		Tab importTab = new Tab(I18N.message("importarchives"));
		importTab.setPane(new ImportArchivesList());
		tabSet.addTab(importTab);

		Tab bundlesTab = new Tab(I18N.message("incomingbundles"));
		bundlesTab.setPane(new ImportFoldersList());
		tabSet.addTab(bundlesTab);

		addMember(tabSet);
	}
}