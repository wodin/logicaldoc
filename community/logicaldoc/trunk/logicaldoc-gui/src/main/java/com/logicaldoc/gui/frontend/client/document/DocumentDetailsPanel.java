package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
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

/**
 * This panel collects all documents details
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentDetailsPanel extends VLayout {
	private GUIDocument document;

	private Layout propertiesTabPanel;

	private Layout extendedPropertiesTabPanel;

	private Layout versionsTabPanel;

	private Layout historyTabPanel;

	private Layout linksTabPanel;

	private Layout discussionTabPanel;

	private StandardPropertiesPanel propertiesPanel;

	private ExtendedPropertiesPanel extendedPropertiesPanel;

	private VersionsPanel versionsPanel;

	private HistoryPanel historyPanel;

	private LinksPanel linksPanel;

	private Discussion discussionPanel;

	private HLayout savePanel;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private TabSet tabSet = new TabSet();

	private DynamicForm saveForm;

	private DocumentObserver observer;

	public DocumentDetailsPanel(DocumentObserver observer) {
		super();
		this.observer = observer;

		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		saveForm = new DynamicForm();
		Button saveButton = new Button(I18N.message("save"));
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
					DocumentsPanel.get().onSelectedDocument(document.getId(), false);
				else if (getObserver() instanceof HitsListPanel)
					SearchPanel.get().onSelectedHit(document.getId());
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
		extendedPropertiesTabPanel.setWidth100();
		extendedPropertiesTabPanel.setHeight100();
		extendedPropertiesTab.setPane(extendedPropertiesTabPanel);
		tabSet.addTab(extendedPropertiesTab);

		Tab linksTab = new Tab(I18N.message("links"));
		linksTabPanel = new HLayout();
		linksTabPanel.setWidth100();
		linksTabPanel.setHeight100();
		linksTab.setPane(linksTabPanel);
		tabSet.addTab(linksTab);

		Tab discussionTab = new Tab(I18N.message("discussions"));
		discussionTabPanel = new HLayout();
		discussionTabPanel.setWidth100();
		discussionTabPanel.setHeight100();
		discussionTab.setPane(discussionTabPanel);

		if (Feature.visible(Feature.FORUMS))
			tabSet.addTab(discussionTab);

		Tab versionsTab = new Tab(I18N.message("versions"));
		versionsTabPanel = new HLayout();
		versionsTabPanel.setWidth100();
		versionsTabPanel.setHeight100();
		versionsTab.setPane(versionsTabPanel);
		tabSet.addTab(versionsTab);

		Tab historyTab = new Tab(I18N.message("history"));
		historyTabPanel = new HLayout();
		historyTabPanel.setWidth100();
		historyTabPanel.setHeight100();
		historyTab.setPane(historyTabPanel);
		tabSet.addTab(historyTab);

		addMember(tabSet);
	}

	public DocumentObserver getObserver() {
		return observer;
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
		propertiesPanel = new StandardPropertiesPanel(document, changeHandler);
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
		 * Prepare the versions tab
		 */
		if (versionsPanel != null) {
			versionsPanel.destroy();
			if (versionsTabPanel.contains(versionsPanel))
				versionsTabPanel.removeMember(versionsPanel);
		}
		versionsPanel = new VersionsPanel(document);
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
		 * Prepare the discussion tab
		 */
		if (discussionPanel != null) {
			discussionPanel.destroy();
			if (discussionTabPanel.contains(discussionPanel))
				discussionTabPanel.removeMember(discussionPanel);
		}
		discussionPanel = new Discussion(document);
		discussionTabPanel.addMember(discussionPanel);
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
		if (!stdValid)
			tabSet.selectTab(0);
		else if (!extValid)
			tabSet.selectTab(1);
		return stdValid && extValid;
	}

	public void onSave() {
		if (validate()) {
			document.setVersionComment(saveForm.getValueAsString("versionComment"));
			documentService.save(Session.get().getSid(), document, new AsyncCallback<GUIDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					saveForm.setValue("versionComment", "");
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIDocument result) {
					observer.onDocumentSaved(result);
					if (result != null)
						DocumentsPanel.get().onSelectedDocument(result.getId(), false);
					savePanel.setVisible(false);
					saveForm.setValue("versionComment", "");
				}
			});
		}
	}
}