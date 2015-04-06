package com.logicaldoc.gui.frontend.client.reports;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the reports menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class ReportsMenu extends VLayout {
	public ReportsMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button lastChanges = new Button(I18N.message("lastchanges"));
		lastChanges.setWidth100();
		lastChanges.setHeight(25);
		lastChanges.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new LastChangesPanel());
			}
		});
		if (Menu.enabled(Menu.LAST_CHANGES))
			addMember(lastChanges);

		Button lockedDocs = new Button(I18N.message("lockeddocs"));
		lockedDocs.setWidth100();
		lockedDocs.setHeight(25);
		lockedDocs.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new LockedDocsPanel());
			}
		});
		if (Menu.enabled(Menu.LOCKED_DOCS))
			addMember(lockedDocs);

		Button deletedDocs = new Button(I18N.message("deleteddocs"));
		deletedDocs.setWidth100();
		deletedDocs.setHeight(25);
		deletedDocs.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new DeletedDocsPanel());
			}
		});
		if (Menu.enabled(Menu.DELETED_DOCS))
			addMember(deletedDocs);

		Button archivedDocs = new Button(I18N.message("archiveddocs"));
		archivedDocs.setWidth100();
		archivedDocs.setHeight(25);
		archivedDocs.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new ArchivedDocsPanel());
			}
		});

		if (Feature.visible(Feature.ARCHIVING) && Menu.enabled(Menu.SETTINGS)) {
			addMember(archivedDocs);
			if (!Feature.enabled(Feature.ARCHIVING)) {
				archivedDocs.setDisabled(true);
				archivedDocs.setTooltip(I18N.message("featuredisabled"));
			}
		}

		Button duplicates = new Button(I18N.message("duplicates"));
		duplicates.setWidth100();
		duplicates.setHeight(25);
		duplicates.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new DuplicatesPanel());
			}
		});

		Button calendar = new Button(I18N.message("calendar"));
		calendar.setWidth100();
		calendar.setHeight(25);
		calendar.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new CalendarReport());
			}
		});

		if (Feature.visible(Feature.DUPLICATES_DISCOVERY)) {
			addMember(duplicates);
			if (!Feature.enabled(Feature.DUPLICATES_DISCOVERY)) {
				duplicates.setDisabled(true);
				duplicates.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.CALENDAR)) {
			addMember(calendar);
			if (!Feature.enabled(Feature.CALENDAR) || !Menu.enabled(Menu.CALENDAR_REPORT)) {
				calendar.setDisabled(true);
				calendar.setTooltip(I18N.message("featuredisabled"));
			}
		}
	}
}
