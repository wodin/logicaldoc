package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.search.HitsListPanel;
import com.logicaldoc.gui.frontend.client.search.SearchPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * This panel collects all documents details
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentDetailsPanel extends VLayout {
	private static final String TAB_SUBSCRIPTIONS = "TabSubscriptions";

	protected GUIDocument document;

	protected Layout propertiesTabPanel;

	protected Layout extendedPropertiesTabPanel;

	protected Layout versionsTabPanel;

	protected Layout historyTabPanel;

	protected Layout linksTabPanel;

	protected Layout notesTabPanel;

	protected Layout previewTabPanel;

	protected Layout retentionPoliciesTabPanel;

	protected Layout calendarTabPanel;

	protected Layout subscriptionsTabPanel;

	protected StandardPropertiesPanel propertiesPanel;

	protected ExtendedPropertiesPanel extendedPropertiesPanel;

	protected VersionsPanel versionsPanel;

	protected HistoryPanel historyPanel;

	protected LinksPanel linksPanel;

	protected NotesPanel notesPanel;

	protected PreviewPanel previewPanel;

	protected DocumentCalendarPanel calendarPanel;

	protected DocumentSubscriptionsPanel subscriptionsPanel;

	protected PublishingPanel retentionPoliciesPanel;

	protected HLayout savePanel;

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected TabSet tabSet = new TabSet();

	protected DynamicForm saveForm;

	protected DocumentObserver observer;

	protected Tab propertiesTab;

	protected Tab extendedPropertiesTab;

	protected Tab linksTab;

	protected Tab notesTab;

	protected Tab versionsTab;

	protected Tab historyTab;

	protected Tab previewTab;

	protected Tab retentionPoliciesTab;

	protected Tab calendarTab;

	protected Tab subscriptionsTab;

	public DocumentDetailsPanel(DocumentObserver observer) {
		super();
		this.observer = observer;

		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		saveForm = new DynamicForm();
		Button saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		saveButton.setLayoutAlign(VerticalAlignment.CENTER);

		Img closeImage = ItemFactory.newImgIcon("delete.png");
		closeImage.setHeight("16px");
		closeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// We have to reload the document because the tags may be
				// reverted to the original tags list.
				// This 'if condition' is necessary to know if the close image
				// has been selected into the Documents list panel or into the
				// Search list panel.
				if (getObserver() instanceof DocumentsPanel)
					DocumentsPanel.get().selectDocument(document.getId(), false);
				else if (getObserver() instanceof HitsListPanel)
					SearchPanel.get().onSelectedDocumentHit(document.getId());
				savePanel.setVisible(false);
			}
		});
		closeImage.setCursor(Cursor.HAND);
		closeImage.setTooltip(I18N.message("close"));
		closeImage.setLayoutAlign(Alignment.RIGHT);
		closeImage.setLayoutAlign(VerticalAlignment.CENTER);

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		TextItem versionComment = ItemFactory.newTextItem("versionComment", "versioncomment", null);
		versionComment.setWidth(300);
		saveForm.setItems(versionComment);
		savePanel.addMember(saveButton);
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.addMember(closeImage);
		savePanel.setHeight(20);
		savePanel.setMembersMargin(10);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		addMember(savePanel);

		prepareTabs();
		prepareTabset();

		if ("preview".equals(Session.get().getInfo().getConfig("gui.document.tab")))
			tabSet.selectTab(previewTab);
	}

	protected void prepareTabs() {
		propertiesTab = new Tab(I18N.message("properties"));
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);

		extendedPropertiesTab = new Tab(I18N.message("propertiesext"));
		extendedPropertiesTabPanel = new HLayout();
		extendedPropertiesTabPanel.setWidth100();
		extendedPropertiesTabPanel.setHeight100();
		extendedPropertiesTab.setPane(extendedPropertiesTabPanel);

		linksTab = new Tab(I18N.message("links"));
		linksTabPanel = new HLayout();
		linksTabPanel.setWidth100();
		linksTabPanel.setHeight100();
		linksTab.setPane(linksTabPanel);

		notesTab = new Tab(I18N.message("notes"));
		notesTabPanel = new HLayout();
		notesTabPanel.setWidth100();
		notesTabPanel.setHeight100();
		notesTab.setPane(notesTabPanel);

		versionsTab = new Tab(I18N.message("versions"));
		versionsTabPanel = new HLayout();
		versionsTabPanel.setWidth100();
		versionsTabPanel.setHeight100();
		versionsTab.setPane(versionsTabPanel);

		historyTab = new Tab(I18N.message("history"));
		historyTabPanel = new HLayout();
		historyTabPanel.setWidth100();
		historyTabPanel.setHeight100();
		historyTab.setPane(historyTabPanel);

		previewTab = new Tab(I18N.message("preview"));
		previewTabPanel = new HLayout();
		previewTabPanel.setWidth100();
		previewTabPanel.setHeight100();
		previewTab.setPane(previewTabPanel);

		retentionPoliciesTab = new Tab(I18N.message("publishing"));
		retentionPoliciesTabPanel = new HLayout();
		retentionPoliciesTabPanel.setWidth100();
		retentionPoliciesTabPanel.setHeight100();
		retentionPoliciesTab.setPane(retentionPoliciesTabPanel);

		calendarTab = new Tab(I18N.message("calendar"));
		calendarTabPanel = new HLayout();
		calendarTabPanel.setWidth100();
		calendarTabPanel.setHeight100();
		calendarTab.setPane(calendarTabPanel);

		subscriptionsTab = new Tab(I18N.message("subscriptions"));
		subscriptionsTab.setID(TAB_SUBSCRIPTIONS);
		subscriptionsTabPanel = new HLayout();
		subscriptionsTabPanel.setWidth100();
		subscriptionsTabPanel.setHeight100();
		subscriptionsTab.setPane(subscriptionsTabPanel);
	}

	protected void prepareTabset() {
		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		tabSet.addTab(propertiesTab);
		propertiesTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				propertiesPanel.onTabSelected();
			}
		});

		tabSet.addTab(extendedPropertiesTab);
		propertiesTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				extendedPropertiesPanel.onTabSelected();
			}
		});

		tabSet.addTab(versionsTab);
		versionsTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				versionsPanel.onTabSelected();
			}
		});

		tabSet.addTab(previewTab);
		previewTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				previewPanel.onTabSelected();
			}
		});

		tabSet.addTab(linksTab);
		linksTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				linksPanel.onTabSelected();
			}
		});

		if (Menu.enabled(Menu.HISTORY)) {
			tabSet.addTab(historyTab);
			historyTab.addTabSelectedHandler(new TabSelectedHandler() {
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					historyPanel.onTabSelected();
				}
			});
		}

		if (Feature.visible(Feature.NOTES)) {
			tabSet.addTab(notesTab);
			notesTab.addTabSelectedHandler(new TabSelectedHandler() {
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					notesPanel.onTabSelected();
				}
			});
		}

		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)
				|| Session.get().getUser().isMemberOf(Constants.GROUP_PUBLISHER))
			if (Feature.visible(Feature.RETENTION_POLICIES))
				tabSet.addTab(retentionPoliciesTab);
		retentionPoliciesTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				retentionPoliciesPanel.onTabSelected();
			}
		});

		if (Feature.visible(Feature.CALENDAR)) {
			tabSet.addTab(calendarTab);
			calendarTab.addTabSelectedHandler(new TabSelectedHandler() {
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					calendarPanel.onTabSelected();
				}
			});
		}

		if (Feature.visible(Feature.AUDIT)) {
			subscriptionsTab.addTabSelectedHandler(new TabSelectedHandler() {
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					subscriptionsPanel.onTabSelected();
				}
			});
		}

		addMember(tabSet);
	}

	public DocumentObserver getObserver() {
		return observer;
	}

	protected void refresh() {
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
		propertiesPanel = new StandardPropertiesPanel(document, changeHandler, getObserver());
		propertiesTabPanel.addMember(propertiesPanel);

		/*
		 * Prepare the extended properties tab
		 */
		if (extendedPropertiesPanel != null) {
			extendedPropertiesPanel.destroy();
			if (extendedPropertiesTabPanel.contains(extendedPropertiesPanel))
				extendedPropertiesTabPanel.removeMember(extendedPropertiesPanel);
		}
		extendedPropertiesPanel = new ExtendedPropertiesPanel(document, changeHandler);
		extendedPropertiesTabPanel.addMember(extendedPropertiesPanel);

		/*
		 * Prepare the retention policies tab
		 */
		if (retentionPoliciesPanel != null) {
			retentionPoliciesPanel.destroy();
			if (retentionPoliciesTabPanel.contains(retentionPoliciesPanel))
				retentionPoliciesTabPanel.removeMember(retentionPoliciesPanel);
		}
		retentionPoliciesPanel = new PublishingPanel(document, changeHandler);
		retentionPoliciesTabPanel.addMember(retentionPoliciesPanel);

		/*
		 * Prepare the versions tab
		 */
		if (versionsPanel != null) {
			versionsPanel.destroy();
			if (versionsTabPanel.contains(versionsPanel))
				versionsTabPanel.removeMember(versionsPanel);
		}
		versionsPanel = new VersionsPanel(document, observer);
		versionsTabPanel.addMember(versionsPanel);

		/*
		 * Prepare the history tab
		 */
		if (historyPanel != null) {
			historyPanel.destroy();
			if (historyTabPanel.contains(historyPanel))
				historyTabPanel.removeMember(historyPanel);
		}
		historyPanel = new HistoryPanel(document);
		historyTabPanel.addMember(historyPanel);

		/*
		 * Prepare the links tab
		 */
		if (linksPanel != null) {
			linksPanel.destroy();
			if (linksTabPanel.contains(linksPanel))
				linksTabPanel.removeMember(linksPanel);
		}
		linksPanel = new LinksPanel(document);
		linksTabPanel.addMember(linksPanel);

		/*
		 * Prepare the notes tab
		 */
		if (notesPanel != null) {
			notesPanel.destroy();
			if (notesTabPanel.contains(notesPanel))
				notesTabPanel.removeMember(notesPanel);
		}
		notesPanel = new NotesPanel(document);
		notesTabPanel.addMember(notesPanel);

		/*
		 * Prepare the preview tab
		 */
		if (previewPanel != null) {
			previewPanel.destroy();
			if (previewTabPanel.contains(previewPanel))
				previewTabPanel.removeMember(previewPanel);
		}
		previewPanel = new PreviewPanel(document);
		previewTabPanel.addMember(previewPanel);

		/*
		 * Prepare the calendar tab
		 */
		if (calendarPanel != null) {
			calendarPanel.destroy();
			if (calendarTabPanel.contains(calendarPanel))
				calendarTabPanel.removeMember(calendarPanel);
		}
		calendarPanel = new DocumentCalendarPanel(document);
		calendarTabPanel.addMember(calendarPanel);

		/*
		 * Prepare the subscriptions tab
		 */
		if (Feature.visible(Feature.AUDIT)) {
			if (subscriptionsPanel != null) {
				subscriptionsPanel.destroy();
				if (subscriptionsTabPanel.contains(subscriptionsPanel))
					subscriptionsTabPanel.removeMember(subscriptionsPanel);
			}

			if (document.getFolder().hasPermission(Constants.PERMISSION_SUBSCRIPTION)) {
				subscriptionsPanel = new DocumentSubscriptionsPanel(document);
				subscriptionsTabPanel.addMember(subscriptionsPanel);
				if (tabSet.getTab(TAB_SUBSCRIPTIONS) == null)
					tabSet.addTab(subscriptionsTab);
			} else
				tabSet.removeTab(subscriptionsTab);
		}

		if (tabSet != null && tabSet.getSelectedTab() != null) {
			Tab selectedTab = tabSet.getSelectedTab();
			Canvas pane = selectedTab.getPane();
			((DocumentDetailTab) pane.getChildren()[0]).onTabSelected();
		}
	}

	public GUIDocument getDocument() {
		return document;
	}

	public void setDocument(GUIDocument document) {
		this.document = document;
		refresh();
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	private boolean validate() {
		boolean stdValid = propertiesPanel.validate();
		boolean extValid = extendedPropertiesPanel.validate();
		boolean publishingValid = retentionPoliciesPanel.validate();
		if (!stdValid)
			tabSet.selectTab(0);
		else if (!extValid)
			tabSet.selectTab(1);
		else if (!publishingValid)
			tabSet.selectTab(2);
		return stdValid && extValid && publishingValid;
	}

	public void onSave() {
		if (validate()) {
			document.setComment(saveForm.getValueAsString("versionComment"));
			String ext = null;
			if (document.getFileName().lastIndexOf('.') != -1) {
				ext = document.getFileName().substring(document.getFileName().lastIndexOf('.'));
				document.setFileName(document.getTitle() + ext);
			}

			documentService.save(Session.get().getSid(), document, new AsyncCallback<GUIDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					saveForm.setValue("versionComment", "");
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIDocument result) {
					if (observer != null) {
						observer.onDocumentSaved(result);
						if (result != null)
							DocumentsPanel.get().selectDocument(result.getId(), false);
						savePanel.setVisible(false);
						saveForm.setValue("versionComment", "");
					}
				}
			});
		}
	}
}