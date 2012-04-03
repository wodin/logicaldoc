package com.logicaldoc.gui.frontend.client.system;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * The bottom side of the general panel
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GeneralPanel extends HLayout {

	private TabSet tabs = new TabSet();

	public GeneralPanel() {
		setWidth100();
		setHeight100();

		setMembersMargin(10);

		Tab stats = new Tab();
		stats.setTitle(I18N.message("statistics"));
		stats.setPane(new StatsPanel());

		Tab sessions = new Tab();
		sessions.setTitle(I18N.message("sessions"));
		sessions.setPane(new SessionsPanel(true));

		Tab plugins = new Tab();
		plugins.setTitle(I18N.message("plugins"));
		plugins.setPane(new PluginsPanel());

		Tab log = new Tab();
		log.setTitle(I18N.message("log"));
		log.setPane(new LogPanel("DMS_WEB"));

		tabs.setTabs(stats, sessions, plugins, log);
		setMembers(tabs);
	}
}