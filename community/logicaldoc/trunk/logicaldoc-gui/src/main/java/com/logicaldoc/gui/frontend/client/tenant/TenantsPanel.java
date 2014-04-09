package com.logicaldoc.gui.frontend.client.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.data.TenantsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.services.TenantService;
import com.logicaldoc.gui.frontend.client.services.TenantServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows the list of tenants and a details area.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class TenantsPanel extends VLayout {
	private TenantServiceAsync service = (TenantServiceAsync) GWT.create(TenantService.class);

	private ListGrid list;

	private InfoPanel infoPanel;

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	final static Canvas SELECT_TENANT = new HTMLPanel("&nbsp;" + I18N.message("selecttenant"));

	private Canvas details = SELECT_TENANT;

	public TenantsPanel() {
		setWidth100();

		infoPanel = new InfoPanel("");

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("55%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField name = new ListGridField("name", I18N.message("name"), 100);
		name.setCanFilter(true);

		ListGridField displayName = new ListGridField("displayName", I18N.message("displayname"), 150);
		displayName.setCanFilter(true);

		ListGridField telephone = new ListGridField("telephone", I18N.message("phone"), 90);
		telephone.setCanFilter(true);

		ListGridField country = new ListGridField("country", I18N.message("country"), 90);
		country.setCanFilter(true);

		ListGridField city = new ListGridField("city", I18N.message("city"), 90);
		city.setCanFilter(true);

		ListGridField email = new ListGridField("email", I18N.message("email"), 200);
		email.setCanFilter(true);

		ListGridField address = new ListGridField("address", I18N.message("address"), 150);
		address.setCanFilter(true);

		ListGridField enabled = new ListGridField("eenabled", " ", 24);
		enabled.setType(ListGridFieldType.IMAGE);
		enabled.setCanSort(false);
		enabled.setAlign(Alignment.CENTER);
		enabled.setShowDefaultContextMenu(false);
		enabled.setImageURLPrefix(Util.imagePrefix());
		enabled.setImageURLSuffix(".gif");
		enabled.setCanFilter(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(true);
		list.setDataSource(new TenantsDS());
		list.setFields(id, name, displayName, email, telephone, country, city, address);

		listing.addMember(infoPanel);
		listing.addMember(list);

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(details);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("addtenant"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				showTenantDetails(new GUITenant());
			}
		});
		toolStrip.addFill();

		setMembers(toolStrip, listing, detailsContainer);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = list.getSelectedRecord();
				if (record != null)
					service.load(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUITenant>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUITenant tenant) {
									showTenantDetails(tenant);
								}
							});
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showtenants", Integer.toString(list.getTotalRows())));
			}
		});
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUITenant tenant) {
		Log.info("Update record " + tenant.getName(), null);

		ListGridRecord record = list.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("name", tenant.getName());
		record.setAttribute("displayName", tenant.getDisplayName());
		record.setAttribute("email", tenant.getEmail());
		record.setAttribute("telephone", tenant.getTelephone());
		record.setAttribute("address", tenant.getStreet());
		record.setAttribute("country", tenant.getCountry());
		record.setAttribute("city", tenant.getCity());
		record.setAttribute("postalCode", tenant.getPostalCode());
		record.setAttribute("state", tenant.getState());

		if (record.getAttributeAsString("id") != null) {
			list.refreshRow(list.getRecordIndex(record));
		} else {
			// Append a new record
			record.setAttribute("id", tenant.getId());
			list.addData(record);
			list.selectRecord(record);
		}
	}

	public void showTenantDetails(GUITenant tenant) {
		detailsContainer.removeMember(details);
		details = new TenantDetailsPanel(this);
		detailsContainer.addMember(details);
		((TenantDetailsPanel) details).setTenant(tenant);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord record = list.getSelectedRecord();
		final long id = Long.parseLong(record.getAttributeAsString("id"));

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.delete(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.removeSelectedData();
									list.deselectAllRecords();
									details = SELECT_TENANT;
									detailsContainer.setMembers(details);
								}
							});
						}
					}
				});
			}
		});

		MenuItem password = new MenuItem();
		password.setTitle(I18N.message("changepassword"));
		password.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SetAdminPassword dialog = new SetAdminPassword(record.getAttributeAsString("name"));
				dialog.show();
			}
		});
		password.setEnabled(!Session.get().isDemo());

		if (id == Constants.TENANT_DEFAULTID) {
			delete.setEnabled(false);
			password.setEnabled(false);
		}

		contextMenu.setItems(password, delete);
		contextMenu.showContextMenu();
	}
}