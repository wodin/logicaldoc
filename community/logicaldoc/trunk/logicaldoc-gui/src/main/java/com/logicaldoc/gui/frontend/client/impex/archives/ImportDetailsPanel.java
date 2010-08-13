package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel collects all documents details
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImportDetailsPanel extends VLayout {
	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private Layout settingsTabPanel;

	private ImportSettingsPanel settingsPanel;

	private HLayout savePanel;

	private TabSet tabSet = new TabSet();

	private GUIArchive archive;

	private ImportArchivesList listPanel;

	public ImportDetailsPanel(GUIArchive archive, ImportArchivesList listPanel) {
		super();
		this.listPanel = listPanel;
		this.archive = archive;
		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		savePanel.setHeight(20);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		Button saveButton = new Button(I18N.message("save"));
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		savePanel.addMember(saveButton);
		addMember(savePanel);

		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		Tab versionsTab = new Tab(I18N.message("settings"));
		settingsTabPanel = new HLayout();
		settingsTabPanel.setWidth100();
		settingsTabPanel.setHeight100();
		versionsTab.setPane(settingsTabPanel);
		tabSet.addTab(versionsTab);

		addMember(tabSet);

		refresh();
	}

	private void refresh() {
		ChangedHandler changeHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};

		/*
		 * Prepare the versions tab
		 */
		if (settingsPanel != null) {
			settingsPanel.destroy();
			if (settingsTabPanel.contains(settingsPanel))
				settingsTabPanel.removeMember(settingsPanel);
		}
		settingsPanel = new ImportSettingsPanel(archive, changeHandler);
		settingsTabPanel.addMember(settingsPanel);
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	public void onSave() {
		if (settingsPanel.validate()) {
			service.save(Session.get().getSid(), archive, new AsyncCallback<GUIArchive>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIArchive result) {
					savePanel.setVisible(false);
					listPanel.updateRecord(result);
				}
			});
		}
	}
}