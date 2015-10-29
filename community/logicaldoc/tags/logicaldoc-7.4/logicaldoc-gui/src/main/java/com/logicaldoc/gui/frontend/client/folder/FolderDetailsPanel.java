package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowTriggersPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
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

	private Layout extendedPropertiesTabPanel;

	private Layout securityTabPanel;

	private Layout historyTabPanel;

	private Layout workflowsTabPanel;

	private Layout subscriptionsTabPanel;

	private PropertiesPanel propertiesPanel;

	private ExtendedPropertiesPanel extendedPropertiesPanel;

	private SecurityPanel securityPanel;

	private HistoryPanel historyPanel;

	private WorkflowTriggersPanel workflowsPanel;

	private FolderSubscriptionsPanel subscriptionsPanel;

	private HLayout savePanel;

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private TabSet tabSet = new TabSet();

	private Tab workflowTab = null;

	private Tab subscriptionsTab = null;

	private FolderObserver listener = null;

	public FolderDetailsPanel(GUIFolder folder, FolderObserver listener) {
		super();
		this.folder = folder;
		this.listener = listener;
		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		savePanel.setHeight(20);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		Button saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
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
								folder.setPathExtended(FolderNavigator.get().getPath(folder.getId()));
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
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);
		tabSet.addTab(propertiesTab);

		Tab extendedPropertiesTab = new Tab(I18N.message("propertiesext"));
		extendedPropertiesTabPanel = new HLayout();
		extendedPropertiesTabPanel.setHeight100();
		extendedPropertiesTab.setPane(extendedPropertiesTabPanel);
		tabSet.addTab(extendedPropertiesTab);

		Tab securityTab = new Tab(I18N.message("security"));
		securityTabPanel = new HLayout();
		securityTabPanel.setWidth100();
		securityTabPanel.setHeight100();
		securityTab.setPane(securityTabPanel);
		if (folder.hasPermission(Constants.PERMISSION_SECURITY))
			tabSet.addTab(securityTab);

		Tab historyTab = new Tab(I18N.message("history"));
		historyTabPanel = new HLayout();
		historyTabPanel.setWidth100();
		historyTabPanel.setHeight100();
		historyTab.setPane(historyTabPanel);
		if (Menu.enabled(Menu.HISTORY))
			tabSet.addTab(historyTab);

		workflowTab = new Tab(I18N.message("workflow"));
		if (folder.hasPermission(Constants.PERMISSION_WORKFLOW))
			if (Feature.visible(Feature.WORKFLOW)) {
				if (Feature.enabled(Feature.WORKFLOW)) {
					workflowsTabPanel = new HLayout();
					workflowsTabPanel.setWidth100();
					workflowsTabPanel.setHeight100();
				} else {
					workflowsTabPanel = new FeatureDisabled();
				}
				workflowTab.setPane(workflowsTabPanel);
				tabSet.addTab(workflowTab);
			}

		subscriptionsTab = new Tab(I18N.message("subscriptions"));
		if (folder.hasPermission(Constants.PERMISSION_SUBSCRIPTION))
			if (Feature.visible(Feature.AUDIT)) {
				if (Feature.enabled(Feature.AUDIT)) {
					subscriptionsTabPanel = new HLayout();
					subscriptionsTabPanel.setWidth100();
					subscriptionsTabPanel.setHeight100();
				} else {
					subscriptionsTabPanel = new FeatureDisabled();
				}
				subscriptionsTab.setPane(subscriptionsTabPanel);
				tabSet.addTab(subscriptionsTab);
			}

		addMember(tabSet);
		refresh();
	}

	private void refresh() {
		try {
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
			 * Prepare the extended properties tab
			 */
			if (extendedPropertiesPanel != null) {
				extendedPropertiesPanel.destroy();
				extendedPropertiesTabPanel.removeMember(extendedPropertiesPanel);
			}
			extendedPropertiesPanel = new ExtendedPropertiesPanel(folder, changeHandler);
			if (Feature.enabled(Feature.TEMPLATE)) {
				extendedPropertiesTabPanel.addMember(extendedPropertiesPanel);
			}

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

			if (Feature.enabled(Feature.WORKFLOW) && folder.hasPermission(Constants.PERMISSION_WORKFLOW)) {
				/*
				 * Prepare the workflow tab
				 */
				if (workflowsPanel != null) {
					workflowsPanel.destroy();
					workflowsTabPanel.removeMember(workflowsPanel);
				}

				workflowsPanel = new WorkflowTriggersPanel(folder);
				workflowsTabPanel.addMember(workflowsPanel);
			}

			if (Feature.enabled(Feature.AUDIT) && folder.hasPermission(Constants.PERMISSION_SUBSCRIPTION)) {
				/*
				 * Prepare the subscriptions tab
				 */
				if (subscriptionsPanel != null) {
					subscriptionsPanel.destroy();
					subscriptionsTabPanel.removeMember(subscriptionsPanel);
				}

				subscriptionsPanel = new FolderSubscriptionsPanel(folder);
				subscriptionsTabPanel.addMember(subscriptionsPanel);
			}
		} catch (Throwable r) {
			SC.warn(r.getMessage());
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
		if (propValid && Feature.enabled(Feature.TEMPLATE)) {
			propValid = extendedPropertiesPanel.validate();
			if (!propValid)
				tabSet.selectTab(1);
		}

		return propValid;
	}

	public void onSave() {
		final int oldPosition = folder.getPosition();
		if (validate()) {
			folder.setName(folder.getName().trim());
			folderService.save(Session.get().getSid(), folder, new AsyncCallback<GUIFolder>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIFolder folder) {
					savePanel.setVisible(false);

					if (listener != null)
						listener.onFolderSaved(folder, oldPosition != folder.getPosition());

					// Adjust the path
					String p = folder.getPathExtended();
					p = p.substring(0, p.lastIndexOf('/'));
					p += "/" + folder.getName().trim();
					folder.setPathExtended(p);
					setFolder(folder);

					GUIFolder current = Session.get().getCurrentFolder();
					current.setTemplate(folder.getTemplate());
					current.setTemplateId(folder.getTemplateId());
					current.setAttributes(folder.getAttributes());
				}
			});
		}
	}
}