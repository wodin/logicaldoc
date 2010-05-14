package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.SessionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * The bottom side of the general panel
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GeneralBottom extends HLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ListGrid sessionsList;

	private Layout sessionsInfos = new VLayout();

	private TabSet tabs = new TabSet();

	public GeneralBottom() {
		setWidth100();
		setMembersMargin(10);

		Tab system = new Tab();
		system.setTitle(I18N.getMessage("system"));

		VLayout form = new VLayout();
		form.setWidth(200);
		form.setHeight(300);

		DynamicForm systemForm = new DynamicForm();
		systemForm.setColWidths(1, "*");

		StaticTextItem productName = new StaticTextItem();
		productName.setName("");
		productName.setTitle("");
		productName.setValue("<b>" + Util.getContext().get("product_name") + "</b>");

		StaticTextItem version = new StaticTextItem();
		version.setName("");
		version.setTitle("");
		version.setValue(I18N.getMessage("version") + " " + Util.getContext().get("product_release"));

		StaticTextItem vendor = new StaticTextItem();
		vendor.setName("");
		vendor.setTitle("");
		vendor.setValue("&copy; " + Util.getContext().get("product_vendor"));

		StaticTextItem address = new StaticTextItem();
		address.setName("");
		address.setTitle("");
		address.setValue(Util.getContext().get("product_vendor_address"));

		StaticTextItem capAndCity = new StaticTextItem();
		capAndCity.setName("");
		capAndCity.setTitle("");
		capAndCity.setValue(Util.getContext().get("product_vendor_cap") + "  "
				+ Util.getContext().get("product_vendor_city"));

		StaticTextItem country = new StaticTextItem();
		country.setName("");
		country.setTitle("");
		country.setValue(Util.getContext().get("product_vendor_country"));

		DynamicForm supportForm = new DynamicForm();
		supportForm.setAlign(Alignment.LEFT);
		supportForm.setTitleOrientation(TitleOrientation.TOP);
		supportForm.setColWidths(1);
		supportForm.setWrapItemTitles(false);
		supportForm.setMargin(8);
		supportForm.setNumCols(1);

		LinkItem support = new LinkItem();
		support.setName(I18N.getMessage("support"));
		support.setLinkTitle(Util.getContext().get("product_support"));
		support.setValue("mailto:" + Util.getContext().get("product_support") + "?subject=LogicalDOC Support - UUID("
				+ Util.getContext().get("id") + ")");
		support.setRequired(true);

		StaticTextItem installationID = new StaticTextItem();
		installationID.setName("");
		installationID.setTitle(I18N.getMessage("installid"));
		installationID.setValue(Util.getContext().get("id"));
		installationID.setRequired(true);

		systemForm.setItems(productName, version, vendor, address, capAndCity, country);

		supportForm.setItems(support, installationID);

		form.addMember(systemForm);
		form.addMember(supportForm);

		system.setPane(form);

		Tab sessions = new Tab();
		sessions.setTitle(I18N.getMessage("sessions"));

		// Initialize the listing panel as placeholder
		sessionsInfos.setAlign(Alignment.CENTER);

		ListGridField sid = new ListGridField("sid", I18N.getMessage("sid"), 250);

		ListGridField username = new ListGridField("username", I18N.getMessage("username"), 80);
		username.setCanFilter(true);

		ListGridField created = new ListGridField("created", I18N.getMessage("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter());
		created.setCanFilter(false);

		ListGridField renew = new ListGridField("renew", I18N.getMessage("lastrenew"), 110);
		renew.setAlign(Alignment.CENTER);
		renew.setType(ListGridFieldType.DATE);
		renew.setCellFormatter(new DateCellFormatter());
		renew.setCanFilter(false);

		ListGridField statusLabel = new ListGridField("statusLabel", I18N.getMessage("status"), 80);
		statusLabel.setCanFilter(false);

		sessionsList = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("sid")) {
					if (Session.get().getSid().equals(record.getAttribute("sid"))) {
						return "font-style: bold;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else if (getFieldName(colNum).equals("statusLabel")) {
					if (!"0".equals(record.getAttribute("status"))) {
						return "color: red;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};

		sessionsList.setShowRecordComponents(true);
		sessionsList.setShowRecordComponentsByCell(true);
		sessionsList.setCanFreezeFields(true);
		sessionsList.setAutoFetchData(true);
		sessionsList.setSelectionType(SelectionStyle.SINGLE);
		sessionsList.setDataSource(SessionsDS.get());
		sessionsList.setFields(sid, statusLabel, username, created, renew);

		sessionsList.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		sessionsInfos.addMember(sessionsList);

		sessions.setPane(sessionsInfos);

		tabs.setTabs(system, sessions);

		setMembers(tabs);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem killSession = new MenuItem();
		killSession.setTitle(I18N.getMessage("kill"));
		killSession.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = sessionsList.getSelectedRecord();
				service.kill(record.getAttributeAsString("sid"), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						sessionsList.removeSelectedData();
					}
				});
			}
		});

		if (!"0".equals(sessionsList.getSelectedRecord().getAttributeAsString("status"))
				|| Session.get().getSid().equals(sessionsList.getSelectedRecord().getAttributeAsString("sid")))
			killSession.setEnabled(false);

		contextMenu.setItems(killSession);
		contextMenu.showContextMenu();
	}
}
