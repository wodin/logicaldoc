package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowsFolderPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
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
 * This panel collects all folder details
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FolderDetailsPanel extends VLayout {
	private GUIFolder folder;

	private Layout propertiesTabPanel;

	private Layout securityTabPanel;

	private Layout historyTabPanel;

	private Layout workflowsTabPanel;

	private PropertiesPanel propertiesPanel;

	private SecurityPanel securityPanel;

	private HistoryPanel historyPanel;

	private WorkflowsFolderPanel workflowsPanel;

	private HLayout savePanel;

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private TabSet tabSet = new TabSet();

	private Tab workflowTab = null;

	private String pathExtended = "";

	public FolderDetailsPanel(GUIFolder folder) {
		super();
		this.folder = folder;
		this.pathExtended = folder.getPathExtended();
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
		saveButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		saveButton.setLayoutAlign(VerticalAlignment.CENTER);

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("70%");
		spacer.setOverflow(Overflow.HIDDEN);

		Img closeImage = ItemFactory.newImgIcon("delete.png");
		closeImage.setHeight("16px");
		closeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				folderService.getFolder(Session.get().getSid(), getFolder().getId(), false,
						new AsyncCallback<GUIFolder>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIFolder folder) {
								folder.setPathExtended(pathExtended);
								setFolder(folder);
								savePanel.setVisible(false);
							}
						});
			}
		});
		closeImage.setCursor(Cursor.HAND);
		closeImage.setTooltip(I18N.message("close"));
		closeImage.setLayoutAlign(Alignment.RIGHT);
		closeImage.setLayoutAlign(VerticalAlignment.CENTER);

		savePanel.addMember(saveButton);
		savePanel.addMember(spacer);
		savePanel.addMember(closeImage);
		addMember(savePanel);

		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		Tab propertiesTab = new Tab(I18N.message("properties"));
		propertiesTab.setID("folderproperties");
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);
		tabSet.addTab(propertiesTab);

		Tab securityTab = new Tab(I18N.message("security"));
		securityTabPanel = new HLayout();
		securityTabPanel.setWidth100();
		securityTabPanel.setHeight100();
		securityTab.setPane(securityTabPanel);
		tabSet.addTab(securityTab);

		Tab historyTab = new Tab(I18N.message("history"));
		historyTabPanel = new HLayout();
		historyTabPanel.setWidth100();
		historyTabPanel.setHeight100();
		historyTab.setPane(historyTabPanel);
		tabSet.addTab(historyTab);

		workflowTab = new Tab(I18N.message("workflow"));
		if (Feature.visible(Feature.WORKFLOW)) {
			if (Feature.enabled(Feature.WORKFLOW)) {
				workflowsTabPanel = new HLayout();
				workflowsTabPanel.setWidth100();
				workflowsTabPanel.setHeight100();
				workflowTab.setPane(workflowsTabPanel);
			} else {
				workflowTab.setPane(new FeatureDisabled());
			}
			tabSet.addTab(workflowTab);
		}

		addMember(tabSet);
		refresh();
	}

	private void refresh() {
		if (savePanel != null)
			savePanel.setVisible(false);

		/*
		 * Prepare the standard properties tab
		 */
		if (propertiesPanel != null) {
			propertiesPanel.destroy();
			propertiesTabPanel.removeMember(propertiesPanel);
		}

		ChangedHandler changeHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};
		propertiesPanel = new PropertiesPanel(folder, changeHandler);
		propertiesTabPanel.addMember(propertiesPanel);

		/*
		 * Prepare the security properties tab
		 */
		if (securityPanel != null) {
			securityPanel.destroy();
			securityTabPanel.removeMember(securityPanel);
		}
		securityPanel = new SecurityPanel(folder);
		securityTabPanel.addMember(securityPanel);

		/*
		 * Prepare the history tab
		 */
		if (historyPanel != null) {
			historyPanel.destroy();
			historyTabPanel.removeMember(historyPanel);
		}
		historyPanel = new HistoryPanel(folder);
		historyTabPanel.addMember(historyPanel);

		if (Feature.visible(Feature.WORKFLOW) && folder.hasPermission(Constants.PERMISSION_WORKFLOW)) {
			/*
			 * Prepare the workflow tab
			 */
			if (workflowsPanel != null) {
				workflowsPanel.destroy();
				workflowsTabPanel.removeMember(workflowsPanel);
			}
			workflowsPanel = new WorkflowsFolderPanel(folder);
			workflowsTabPanel.addMember(workflowsPanel);
		}
	}

	public GUIFolder getFolder() {
		return folder;
	}

	public void setFolder(GUIFolder folder) {
		this.folder = folder;
		refresh();
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	private boolean validate() {
		boolean propValid = propertiesPanel.validate();
		if (!propValid)
			tabSet.selectTab(0);
		return propValid;
	}

	public void onSave() {
		if (validate()) {
			folderService.save(Session.get().getSid(), folder, new AsyncCallback<GUIFolder>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIFolder result) {
					FoldersNavigator.get().onSavedFolder(result);
					savePanel.setVisible(false);
				}
			});
		}
	}
}