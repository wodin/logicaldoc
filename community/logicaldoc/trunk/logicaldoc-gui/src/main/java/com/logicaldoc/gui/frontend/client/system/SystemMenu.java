package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;
import com.logicaldoc.gui.frontend.client.services.SearchEngineServiceAsync;
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
	private SearchEngineServiceAsync seService = (SearchEngineServiceAsync) GWT.create(SearchEngineService.class);

	public SystemMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button general = new Button(I18N.message("general"));
		general.setWidth100();
		general.setHeight(25);
		addMember(general);

		Button lastChanges = new Button(I18N.message("lastchanges"));
		lastChanges.setWidth100();
		lastChanges.setHeight(25);
		if (Menu.enabled(Menu.LAST_CHANGES))
			addMember(lastChanges);

		Button tasks = new Button(I18N.message("scheduledtasks"));
		tasks.setWidth100();
		tasks.setHeight(25);
		addMember(tasks);

		Button searchAndIndexing = new Button(I18N.message("searchandindexing"));
		searchAndIndexing.setWidth100();
		searchAndIndexing.setHeight(25);
		addMember(searchAndIndexing);

		addInformations();

		general.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new GeneralPanel());
			}
		});

		lastChanges.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new LastChangesPanel());
			}
		});

		tasks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TasksPanel());
			}
		});

		searchAndIndexing.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				seService.getInfo(Session.get().getSid(), new AsyncCallback<GUISearchEngine>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUISearchEngine searchEngine) {
						AdminPanel.get().setContent(new SearchIndexingPanel(searchEngine));
					}

				});
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

		StaticTextItem address = ItemFactory.newStaticTextItem("address", "", Session.get().getInfo()
				.getVendorAddress());
		address.setShouldSaveValue(false);

		StaticTextItem capAndCity = ItemFactory.newStaticTextItem("capAndCity", "", Session.get().getInfo()
				.getVendorCap()
				+ "  " + Session.get().getInfo().getVendorCity());
		capAndCity.setShouldSaveValue(false);

		StaticTextItem country = ItemFactory.newStaticTextItem("country", "", Session.get().getInfo()
				.getVendorCountry());
		country.setShouldSaveValue(false);

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

		systemForm.setItems(productName, version, vendor, address, capAndCity, country);

		supportForm.setItems(support, installationID);

		addMember(systemForm);
		addMember(supportForm);
	}
}