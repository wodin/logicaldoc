package com.logicaldoc.gui.frontend.client.impex.archives;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
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
public class ExportArchivesPanel extends VLayout {

	public ExportArchivesPanel() {
		setWidth100();
		TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		Tab archivesTab = new Tab(I18N.message("exportarchives"));
		archivesTab.setPane(new ExportArchivesList(GUIArchive.TYPE_DEFAULT, false));
		tabSet.addTab(archivesTab);

		Tab incremetalTab = new Tab(I18N.message("incrementalarchives"));
		if (Feature.visible(Feature.INCREMENTAL_ARCHIVES)) {
			tabSet.addTab(incremetalTab);
			if (!Feature.enabled(Feature.INCREMENTAL_ARCHIVES))
				incremetalTab.setPane(new FeatureDisabled());
			else
				incremetalTab.setPane(new IncrementalArchivesList(GUIArchive.TYPE_DEFAULT));
		}

		addMember(tabSet);
	}
}