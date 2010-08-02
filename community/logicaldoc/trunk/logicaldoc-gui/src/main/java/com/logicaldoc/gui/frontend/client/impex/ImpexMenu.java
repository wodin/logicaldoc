package com.logicaldoc.gui.frontend.client.impex;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.impex.accounts.AccountsPanel;
import com.logicaldoc.gui.frontend.client.impex.folders.ImportFoldersPanel;
import com.logicaldoc.gui.frontend.client.template.TemplatesPanel;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration of import/export features.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ImpexMenu extends VLayout {

	public ImpexMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button importFolders = new Button(I18N.message("importfolders"));
		importFolders.setWidth100();
		importFolders.setHeight(25);
		if (Feature.visible(Feature.IMPORT_REMOTE_FOLDERS) || Feature.visible(Feature.IMPORT_LOCAL_FOLDERS)) {
			addMember(importFolders);
			if (!Feature.enabled(Feature.IMPORT_REMOTE_FOLDERS) && !Feature.enabled(Feature.IMPORT_LOCAL_FOLDERS)) {
				importFolders.setDisabled(true);
				importFolders.setTooltip(I18N.message("featuredisabled"));
			}
		}
		importFolders.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new ImportFoldersPanel());
			}
		});

		Button emails = new Button(I18N.message("emailaccounts"));
		emails.setWidth100();
		emails.setHeight(25);
		if (Feature.visible(Feature.EMAIL_IMPORT)) {
			addMember(emails);
			if (!Feature.enabled(Feature.EMAIL_IMPORT)) {
				emails.setDisabled(true);
				emails.setTooltip(I18N.message("featuredisabled"));
			}
		}
		emails.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new AccountsPanel());
			}
		});

		Button importArchives = new Button(I18N.message("importarchives"));
		importArchives.setWidth100();
		importArchives.setHeight(25);
		if (Feature.visible(Feature.ARCHIVES)) {
			addMember(importArchives);
			if (!Feature.enabled(Feature.ARCHIVES)) {
				importArchives.setDisabled(true);
				importArchives.setTooltip(I18N.message("featuredisabled"));
			}
		}
		importArchives.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TemplatesPanel());
			}
		});

		Button exportArchives = new Button(I18N.message("exportarchives"));
		exportArchives.setWidth100();
		exportArchives.setHeight(25);

		if (Feature.visible(Feature.ARCHIVES)) {
			addMember(exportArchives);
			if (!Feature.enabled(Feature.ARCHIVES)) {
				exportArchives.setDisabled(true);
				exportArchives.setTooltip(I18N.message("featuredisabled"));
			}
		}
		exportArchives.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TemplatesPanel());
			}
		});
	}
}
