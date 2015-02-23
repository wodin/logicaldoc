package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.logicaldoc.gui.frontend.client.tenant.TenantsPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration system menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SystemMenu extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	public SystemMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button general = new Button(I18N.message("general"));
		general.setWidth100();
		general.setHeight(25);
		addMember(general);

		Button tasks = new Button(I18N.message("scheduledtasks"));
		tasks.setWidth100();
		tasks.setHeight(25);
		addMember(tasks);

		Button clustering = new Button(I18N.message("clustering"));
		clustering.setWidth100();
		clustering.setHeight(25);

		if (Feature.visible(Feature.CLUSTERING) && Menu.enabled(Menu.CLUSTERING) && Session.get().isDefaultTenant()) {
			addMember(clustering);
			if (!Feature.enabled(Feature.CLUSTERING)) {
				clustering.setDisabled(true);
				clustering.setTooltip(I18N.message("featuredisabled"));
			}
		}

		Button tenants = new Button(I18N.message("tenants"));
		tenants.setWidth100();
		tenants.setHeight(25);

		if (Feature.visible(Feature.MULTI_TENANT) && "admin".equals(Session.get().getUser().getUserName())) {
			addMember(tenants);
			if (!Feature.enabled(Feature.MULTI_TENANT)) {
				tenants.setDisabled(true);
				tenants.setTooltip(I18N.message("featuredisabled"));
			}
		}

		Button productNews = new Button(I18N.message("task.name.ProductNews"));
		productNews.setWidth100();
		productNews.setHeight(25);

		if (Feature.visible(Feature.PRODUCT_NEWS)) {
			addMember(productNews);
			if (!Feature.enabled(Feature.PRODUCT_NEWS)) {
				productNews.setDisabled(true);
				productNews.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Session.get().isDemo()) {
			clustering.setDisabled(true);
			tenants.setDisabled(true);
		}

		if (!Session.get().isDefaultTenant()) {
			clustering.setVisible(false);
			tenants.setVisible(false);
		}

		addInformations();

		general.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new GeneralPanel());
			}
		});

		tasks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TasksPanel());
			}
		});

		productNews.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new ProductNewsPanel());
			}
		});

		clustering.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadSettingsByNames(Session.get().getSid(), new String[] { "cluster.enabled", "cluster.name",
						"cluster.node.host", "cluster.node.port", "cluster.node.context", "cluster.port",
						"cluster.multicastip", "cluster.nodeId" }, new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new ClusteringPanel(settings));
					}
				});
			}
		});

		tenants.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TenantsPanel());
			}
		});
	}

	private void addInformations() {
		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth(300);
		systemForm.setColWidths(1, "*");

		StaticTextItem productName = ItemFactory.newStaticTextItem("productName", "", "<b>"
				+ Session.get().getInfo().getProductName() + "</b>");
		productName.setShouldSaveValue(false);
		productName.setWrapTitle(false);

		StaticTextItem version = ItemFactory.newStaticTextItem("version", "", I18N.message("version") + " "
				+ Session.get().getInfo().getRelease());
		version.setShouldSaveValue(false);

		StaticTextItem vendor = ItemFactory.newStaticTextItem("vendor", "", "&copy; "
				+ Session.get().getInfo().getVendor());
		vendor.setShouldSaveValue(false);

		String userno = Session.get().getInfo().getConfig("userno");

		DynamicForm supportForm = new DynamicForm();
		supportForm.setAlign(Alignment.LEFT);
		supportForm.setTitleOrientation(TitleOrientation.TOP);
		supportForm.setColWidths(1);
		supportForm.setWrapItemTitles(false);
		supportForm.setMargin(8);
		supportForm.setNumCols(1);

		LinkItem support = new LinkItem();
		support.setName(I18N.message("support"));
		support.setLinkTitle(Session.get().getInfo().getSupport());

		String mailTo = "mailto:" + Session.get().getInfo().getSupport() + "?subject="
				+ Session.get().getInfo().getProductName() + " Support - ";
		if (userno != null)
			mailTo += "UserNo(" + userno + ")";
		else
			mailTo += "ID(" + Session.get().getInfo().getInstallationId() + ")";
		support.setValue(mailTo);
		support.setRequired(true);
		support.setShouldSaveValue(false);

		StaticTextItem installationID = ItemFactory.newStaticTextItem("installid", "installid", Session.get().getInfo()
				.getInstallationId());
		installationID.setWidth(250);
		installationID.setRequired(true);
		installationID.setShouldSaveValue(false);
		installationID.setWrap(true);
		installationID.setWrapTitle(false);

		StaticTextItem usernoItem = ItemFactory.newStaticTextItem("userno", "userno", userno);
		usernoItem.setWidth(250);
		usernoItem.setRequired(true);
		usernoItem.setShouldSaveValue(false);
		usernoItem.setWrap(true);
		usernoItem.setWrapTitle(false);

		systemForm.setItems(productName, version, vendor);

		if (userno != null)
			supportForm.setItems(support, usernoItem);
		else
			supportForm.setItems(support, installationID);

		if (!Session.get().isDemo()) {
			addMember(systemForm);
			addMember(supportForm);
		}
	}
}