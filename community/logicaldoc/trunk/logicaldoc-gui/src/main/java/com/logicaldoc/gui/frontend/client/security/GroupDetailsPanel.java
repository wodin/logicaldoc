package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
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
 * This panel collects all groups details
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 * 
 */
public class GroupDetailsPanel extends VLayout {
	private GUIGroup group;

	private Layout propertiesTabPanel;

	private Layout historyTabPanel;

	private GroupPropertiesPanel propertiesPanel;

	private HLayout savePanel;

	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private TabSet tabSet = new TabSet();

	private GroupsPanel groupsPanel;

	private UserHistoryPanel historyPanel;

	public GroupDetailsPanel(GroupsPanel groupsPanel) {
		super();
		this.groupsPanel = groupsPanel;

		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		savePanel.setHeight(20);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		Button saveButton = new Button(I18N.getMessage("save"));
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

		Tab propertiesTab = new Tab(I18N.getMessage("properties"));
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);
		tabSet.addTab(propertiesTab);

		// Tab historyTab = new Tab(I18N.getMessage("history"));
		// historyTabPanel = new HLayout();
		// historyTabPanel.setWidth100();
		// historyTabPanel.setHeight100();
		// historyTab.setPane(historyTabPanel);
		// tabSet.addTab(historyTab);
		
		addMember(tabSet);
	}

	private void refresh() {
		if (savePanel != null)
			savePanel.setVisible(false);

		/*
		 * Prepare the standard properties tab
		 */
		if (propertiesPanel != null) {
			propertiesPanel.destroy();
			if (propertiesTabPanel.contains(propertiesPanel))
				propertiesTabPanel.removeMember(propertiesPanel);
		}

		ChangedHandler changeHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};

		propertiesPanel = new GroupPropertiesPanel(group, changeHandler);
		propertiesTabPanel.addMember(propertiesPanel);

		// /*
		// * Prepare the history tab
		// */
		// if (historyPanel != null) {
		// historyPanel.destroy();
		// if (historyTabPanel.contains(historyPanel))
		// historyTabPanel.removeMember(historyPanel);
		// }
		// historyPanel = new UserHistoryPanel(group.getId());
		// historyTabPanel.addMember(historyPanel);
	}

	public GUIGroup getGroup() {
		return group;
	}

	public void setGroup(GUIGroup group) {
		this.group = group;
		refresh();
	}

	public void onModified() {
		Log.debug("***");
		savePanel.setVisible(true);
	}

	private boolean validate() {
		boolean stdValid = propertiesPanel.validate();
		if (!stdValid)
			tabSet.selectTab(0);
		return stdValid;
	}

	public void onSave() {
		if (validate()) {
			service.saveGroup(Session.get().getSid(), group, new AsyncCallback<GUIGroup>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIGroup result) {
					savePanel.setVisible(false);
					groupsPanel.updateRecord(result);
				}
			});
		}
	}
}