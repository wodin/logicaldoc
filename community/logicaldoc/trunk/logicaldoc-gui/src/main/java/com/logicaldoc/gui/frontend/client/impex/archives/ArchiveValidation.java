package com.logicaldoc.gui.frontend.client.impex.archives;

import com.logicaldoc.gui.common.client.beans.GUISostConfig;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This popup window is used to start an archive validation.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ArchiveValidation extends Window {

	// private ArchiveServiceAsync service = (ArchiveServiceAsync)
	// GWT.create(ArchiveService.class);

	private VLayout layout = null;

	private TabSet tabs = new TabSet();

	// private ListGrid docsList;

	// private GUISostConfig[] sostConfigurations = null;
	//
	// private ValuesManager vm = new ValuesManager();

	public ArchiveValidation(GUISostConfig[] configs) {
		// this.sostConfigurations = configs;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		// setTitle(I18N.message("startworkflow"));
		setWidth(600);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		layout = new VLayout(20);
		layout.setMargin(25);

		tabs = new TabSet();
		tabs.setWidth(550);
		tabs.setHeight(300);

		for (GUISostConfig guiSostConfig : configs) {
			Tab tab = new ValidationTab(guiSostConfig);
			tabs.addTab(tab);
		}

		layout.addMember(tabs);
		layout.redraw();
	}
}
