package com.logicaldoc.gui.frontend.client.system;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration system menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SystemMenu extends VLayout {

	public SystemMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button general = new Button(I18N.getMessage("general"));
		general.setWidth100();
		general.setHeight(25);

		Button lastChanges = new Button(I18N.getMessage("lastchanges"));
		lastChanges.setWidth100();
		lastChanges.setHeight(25);

		Button log = new Button(I18N.getMessage("log"));
		log.setWidth100();
		log.setHeight(25);

		Button tasks = new Button(I18N.getMessage("scheduledtasks"));
		tasks.setWidth100();
		tasks.setHeight(25);

		setMembers(general, lastChanges, log, tasks);

		general.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new GeneralPanel());
			}
		});

		lastChanges.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new LastChangesPanel());
			}
		});

		log.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new LogPanel("DMS"));
			}
		});

		tasks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TasksPanel());
			}
		});
	}
}