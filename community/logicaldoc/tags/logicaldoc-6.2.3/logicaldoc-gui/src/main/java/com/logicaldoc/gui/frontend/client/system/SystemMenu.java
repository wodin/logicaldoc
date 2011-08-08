package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
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

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	public SystemMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button general = new Button(I18N.message("general"));
		general.setWidth100();
		general.setHeight(25);
		addMember(general);

		Button plugins = new Button(I18N.message("plugins"));
		plugins.setWidth100();
		plugins.setHeight(25);
		addMember(plugins);

		Button tasks = new Button(I18N.message("scheduledtasks"));
		tasks.setWidth100();
		tasks.setHeight(25);
		addMember(tasks);

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

		addInformations();

		general.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new GeneralPanel());
			}
		});

		plugins.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.getPlugins(Session.get().getSid(), new AsyncCallback<GUIValuePair[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIValuePair[] plugins) {
						AdminPanel.get().setContent(new PluginsPanel(plugins));
					}

				});
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
		support.setValue("mailto:" + Session.get().getInfo().getSupport() + "?subject="
				+ Session.get().getInfo().getProductName() + " Support - ID("
				+ Session.get().getInfo().getInstallationId() + ")");
		support.setRequired(true);
		support.setShouldSaveValue(false);

		StaticTextItem installationID = ItemFactory.newStaticTextItem("", "installid", Session.get().getInfo()
				.getInstallationId());
		installationID.setRequired(true);
		installationID.setShouldSaveValue(false);
		installationID.setWrap(false);
		installationID.setWrapTitle(false);

		systemForm.setItems(productName, version, vendor);

		supportForm.setItems(support, installationID);

		addMember(systemForm);
		addMember(supportForm);
	}
}