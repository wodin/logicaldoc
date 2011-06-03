package com.logicaldoc.gui.frontend.client.metadata;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel collects the administration panels regarding the tags.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.2
 */
public class TagsPanel extends HLayout {

	private TabSet tabs = new TabSet();

	/**
	 * 
	 * @param tagMode The current input mode: 'free' or 'preset'
	 */
	public TagsPanel(String tagMode) {
		setWidth100();
		setHeight100();

		setMembersMargin(10);

		Tab used = new Tab();
		used.setTitle(I18N.message("usedtags"));
		used.setPane(new TagsForm(true, false));

		Tab preset = new Tab();
		preset.setTitle(I18N.message("tagspreset"));
		preset.setPane(new TagsPreset(tagMode));

		tabs.setTabs(used, preset);

		setMembers(tabs);
	}
}