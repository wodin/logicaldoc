package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel collects all documents details needed by a bulk update.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class BulkUpdatePanel extends VLayout {
	protected GUIDocument document = new GUIDocument();

	protected Layout propertiesTabPanel;

	protected Layout extendedPropertiesTabPanel;

	protected Layout retentionPoliciesTabPanel;

	protected BulkStandardPropertiesPanel propertiesPanel;

	protected ExtendedPropertiesPanel extendedPropertiesPanel;

	protected RetentionPoliciesPanel retentionPoliciesPanel;

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected TabSet tabSet = new TabSet();

	protected Tab propertiesTab;

	protected Tab extendedPropertiesTab;

	protected Tab retentionPoliciesTab;

	public BulkUpdatePanel(GUIDocument metadata) {
		super();

		if (metadata != null)
			document = metadata;
		else {
			document.setStartPublishing(null);
			document.setPublished(-1);

			GUIFolder currentFolder = Session.get().getCurrentFolder();
			document.setFolder(currentFolder);

			if (currentFolder.getTemplateLocked()==1){
				document.setTemplateId(currentFolder.getTemplateId());
				document.setTemplate(currentFolder.getTemplate());
				document.setAttributes(currentFolder.getAttributes());
			}
		}

		setHeight100();
		setWidth100();
		setMembersMargin(10);

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		prepareTabs();
		prepareTabset();

		refresh();
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

		retentionPoliciesTab = new Tab(I18N.message("publishing"));
		retentionPoliciesTabPanel = new HLayout();
		retentionPoliciesTabPanel.setWidth100();
		retentionPoliciesTabPanel.setHeight100();
		retentionPoliciesTab.setPane(retentionPoliciesTabPanel);
	}

	protected void prepareTabset() {
		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		tabSet.addTab(propertiesTab);
		tabSet.addTab(extendedPropertiesTab);
		if (Feature.enabled(Feature.RETENTION_POLICIES)
				&& (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN) || Session.get().getUser()
						.isMemberOf(Constants.GROUP_PUBLISHER)))
			tabSet.addTab(retentionPoliciesTab);

		addMember(tabSet);
	}

	protected void refresh() {
		/*
		 * Prepare the standard properties tab
		 */
		if (propertiesPanel != null) {
			propertiesPanel.destroy();
			if (propertiesTabPanel.contains(propertiesPanel))
				propertiesTabPanel.removeMember(propertiesPanel);
		}

		propertiesPanel = new BulkStandardPropertiesPanel(document);
		propertiesTabPanel.addMember(propertiesPanel);

		/*
		 * Prepare the extended properties tab
		 */
		if (extendedPropertiesPanel != null) {
			extendedPropertiesPanel.destroy();
			if (extendedPropertiesTabPanel.contains(extendedPropertiesPanel))
				extendedPropertiesTabPanel.removeMember(extendedPropertiesPanel);
		}

		ChangedHandler nothingToDo = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {

			}
		};
		extendedPropertiesPanel = new ExtendedPropertiesPanel(document, nothingToDo);
		extendedPropertiesTabPanel.addMember(extendedPropertiesPanel);

		/*
		 * Prepare the retention policies tab
		 */
		if (retentionPoliciesPanel != null) {
			retentionPoliciesPanel.destroy();
			if (retentionPoliciesTabPanel.contains(retentionPoliciesPanel))
				retentionPoliciesTabPanel.removeMember(retentionPoliciesPanel);
		}

		retentionPoliciesPanel = new RetentionPoliciesPanel(document, nothingToDo);
		retentionPoliciesTabPanel.addMember(retentionPoliciesPanel);
	}

	public GUIDocument getDocument() {
		return document;
	}

	public boolean validate() {
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
}