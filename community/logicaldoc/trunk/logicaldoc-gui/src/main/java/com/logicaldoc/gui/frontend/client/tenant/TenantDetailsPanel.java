package com.logicaldoc.gui.frontend.client.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.TenantService;
import com.logicaldoc.gui.frontend.client.services.TenantServiceAsync;
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
 * This panel collects all tenant details
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class TenantDetailsPanel extends VLayout {
	private GUITenant tenant;

	private Layout propertiesTabPanel;

	private Layout quotaTabPanel;

	private TenantPropertiesPanel propertiesPanel;

	private TenantQuotaPanel quotaPanel;

	private HLayout savePanel;

	private TenantServiceAsync service = (TenantServiceAsync) GWT.create(TenantService.class);

	private TabSet tabSet = new TabSet();

	private TenantsPanel tenantsPanel;

	private Button saveButton;

	public TenantDetailsPanel(TenantsPanel tenantsPanel) {
		super();
		this.tenantsPanel = tenantsPanel;

		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		savePanel.setHeight(20);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
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
				if (tenant.getId() != 0) {
					service.load(Session.get().getSid(), tenant.getId(), new AsyncCallback<GUITenant>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUITenant tenant) {
							setTenant(tenant);
						}
					});
				} else {
					setTenant(new GUITenant());
				}
				savePanel.setVisible(false);
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

		Tab quotaTab = new Tab(I18N.message("quota"));
		quotaTabPanel = new HLayout();
		quotaTabPanel.setWidth100();
		quotaTabPanel.setHeight100();
		quotaTab.setPane(quotaTabPanel);
		tabSet.addTab(quotaTab);

		addMember(tabSet);
	}

	private void refresh() {
		if (savePanel != null)
			savePanel.setVisible(false);

		/*
		 * Prepare the properties tab
		 */
		if (propertiesPanel != null) {
			propertiesPanel.destroy();
			if (propertiesTabPanel.contains(propertiesPanel))
				propertiesTabPanel.removeMember(propertiesPanel);
		}

		/*
		 * Prepare the quota tab
		 */
		if (quotaPanel != null) {
			quotaPanel.destroy();
			if (quotaTabPanel.contains(quotaPanel))
				quotaTabPanel.removeMember(quotaPanel);
		}

		ChangedHandler changeHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};

		propertiesPanel = new TenantPropertiesPanel(this.tenant, changeHandler);
		propertiesTabPanel.addMember(propertiesPanel);
		quotaPanel = new TenantQuotaPanel(this.tenant, changeHandler);
		quotaTabPanel.addMember(quotaPanel);

		tabSet.selectTab(0);
	}

	public GUITenant getTenant() {
		return tenant;
	}

	public void setTenant(GUITenant tenant) {
		this.tenant = tenant;
		refresh();
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	private boolean validate() {
		if (!propertiesPanel.validate()) {
			tabSet.selectTab(0);
			return false;
		}

		if (!quotaPanel.validate()) {
			tabSet.selectTab(1);
			return false;
		}

		return true;
	}

	public void onSave() {
		if (validate()) {
			saveButton.setDisabled(true);

			final boolean newTenant = TenantDetailsPanel.this.tenant.getId() == 0L;

			service.save(Session.get().getSid(), tenant, new AsyncCallback<GUITenant>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
					saveButton.setDisabled(false);
				}

				@Override
				public void onSuccess(GUITenant tenant) {
					saveButton.setDisabled(false);
					savePanel.setVisible(false);
					if (tenant != null) {
						if (newTenant) {
							SC.say(I18N.message("newtenantresume",
									new String[] { tenant.getName(), tenant.getAdminUsername(), "admin" }));
						}

						TenantDetailsPanel.this.tenant = tenant;
						TenantDetailsPanel.this.tenantsPanel.updateRecord(tenant);
						TenantDetailsPanel.this.tenantsPanel.loadTenant(tenant.getId());
					}
				}
			});
		}
	}
}