package com.logicaldoc.gui.frontend.client.metadata.template;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Panel showing the panels for handling templates and attribute sets
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class TemplatesAndAttributesPanel extends VLayout {
	public TemplatesAndAttributesPanel() {
		super();

		setHeight100();
		setWidth100();
		setMembersMargin(10);

		Tab templatesTab = new Tab(I18N.message("templates"));
		templatesTab.setPane(new TemplatesPanel());

		Tab attributesTab = new Tab(I18N.message("attributesets"));
		attributesTab.setPane(new AttributeSetsPanel());
		
		TabSet tabSet = new TabSet();
		tabSet.setTabs(templatesTab, attributesTab);

		setMembers(tabSet);
	}
}