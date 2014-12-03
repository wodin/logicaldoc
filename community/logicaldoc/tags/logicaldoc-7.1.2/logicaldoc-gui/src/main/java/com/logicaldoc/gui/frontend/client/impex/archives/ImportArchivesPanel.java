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

	private Tab importTab = null;

	private Tab bundlesTab = null;

	private TabSet tabSet = new TabSet();

	public ImportArchivesPanel() {
		setWidth100();

		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		importTab = new Tab(I18N.message("importarchives"));
		importTab.setPane(new ImportArchivesList());
		tabSet.addTab(importTab, 0);

		bundlesTab = new Tab(I18N.message("incomingbundles"));
		bundlesTab.setPane(new ImportFoldersList(this));
		tabSet.addTab(bundlesTab, 1);

		addMember(tabSet);
	}

	public TabSet getTabSet() {
		return tabSet;
	}
}