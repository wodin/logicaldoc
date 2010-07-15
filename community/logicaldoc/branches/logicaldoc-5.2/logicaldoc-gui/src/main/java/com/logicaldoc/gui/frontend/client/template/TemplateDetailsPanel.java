package com.logicaldoc.gui.frontend.client.template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.gui.frontend.client.services.TemplateServiceAsync;
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
 * This panel collects all template details
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TemplateDetailsPanel extends VLayout {
	private GUITemplate template;

	private Layout propertiesTabPanel;

	private TemplatePropertiesPanel propertiesPanel;

	private HLayout savePanel;

	private TemplateServiceAsync service = (TemplateServiceAsync) GWT.create(TemplateService.class);

	private TabSet tabSet = new TabSet();

	private TemplatesPanel templatesPanel;

	public TemplateDetailsPanel(TemplatesPanel usersPanel) {
		super();
		this.templatesPanel = usersPanel;

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

		Tab propertiesTab = new Tab(I18N.message("properties"));
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);
		tabSet.addTab(propertiesTab);

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

		propertiesPanel = new TemplatePropertiesPanel(template, changeHandler, this);
		propertiesTabPanel.addMember(propertiesPanel);
	}

	public GUITemplate getTemplate() {
		return template;
	}

	public void setTemplate(GUITemplate template) {
		this.template = template;
		refresh();
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	public HLayout getSavePanel() {
		return savePanel;
	}

	private boolean validate() {
		boolean stdValid = propertiesPanel.validate();
		if (!stdValid)
			tabSet.selectTab(0);
		return stdValid;
	}

	public void onSave() {
		if (validate()) {
			service.save(Session.get().getSid(), template, new AsyncCallback<GUITemplate>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUITemplate result) {
					savePanel.setVisible(false);
					templatesPanel.updateRecord(result);
				}
			});
		}
	}
}
