package com.logicaldoc.gui.frontend.client.metadata;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
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
	public TagsPanel(GUIParameter[] parameters) {
		setWidth100();
		setHeight100();

		setMembersMargin(10);

		Tab settings = new Tab();
		settings.setTitle(I18N.message("settings"));
		settings.setPane(new TagsSettingsPanel(parameters));

		Tab used = new Tab();
		used.setTitle(I18N.message("usedtags"));
		used.setPane(new TagsForm(true, false));

		Tab preset = new Tab();
		preset.setTitle(I18N.message("tagspreset"));
		for (GUIParameter p : parameters) {
			if ((Session.get().getTenantName() + ".tag.mode").equals(p.getName())) {
				preset.setPane(new TagsPreset(p.getValue()));
				break;
			}
		}

		tabs.setTabs(settings, used, preset);

		setMembers(tabs);
	}
}