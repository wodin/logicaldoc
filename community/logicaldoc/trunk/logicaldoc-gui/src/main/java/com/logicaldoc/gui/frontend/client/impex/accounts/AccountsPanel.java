package com.logicaldoc.gui.frontend.client.impex.accounts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmailAccount;
import com.logicaldoc.gui.common.client.data.EmailAccountsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.services.EmailAccountService;
import com.logicaldoc.gui.frontend.client.services.EmailAccountServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
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
 * Panel showing the list of import email accounts
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AccountsPanel extends VLayout {
	private EmailAccountServiceAsync service = (EmailAccountServiceAsync) GWT.create(EmailAccountService.class);

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	private ListGrid list;

	private Canvas details = SELECT_ACCOUNT;

	private InfoPanel infoPanel;

	final static Canvas SELECT_ACCOUNT = new HTMLPanel("&nbsp;" + I18N.message("selectaccount"));

	public AccountsPanel() {
		setWidth100();
		infoPanel = new InfoPanel("");
		init();
	}

	public void init() {
		detailsContainer.clear();
		listing.clear();
		if (list != null)
			listing.removeMember(list);
		if (details != null && details instanceof AccountDetailsPanel) {
			detailsContainer.removeMember(details);
			details = SELECT_ACCOUNT;
		}

		// Initialize the listing panel
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("60%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField email = new ListGridField("email", I18N.message("email"), 300);
		email.setCanFilter(true);

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
		list.setShowAllRecords(true);
		list.setAutoFetchData(true);
		list.setWidth100();
		list.setHeight100();
		list.setFields(id, email, enabled);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);
		list.setDataSource(new EmailAccountsDS(false));

		listing.addMember(infoPanel);
		listing.addMember(list);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				init();
			}
		});

		ToolStripButton addAccount = new ToolStripButton();
		addAccount.setTitle(I18N.message("addaccount"));
		toolStrip.addButton(addAccount);
		addAccount.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				GUIEmailAccount account = new GUIEmailAccount();
				showDetails(account);
			}
		});

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
					service.get(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUIEmailAccount>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIEmailAccount account) {
									showDetails(account);
								}
							});
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showaccounts", Integer.toString(list.getTotalRows())));
			}
		});

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(details);

		setMembers(toolStrip, listing, detailsContainer);
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
									showDetails(null);
								}
							});
						}
					}
				});
			}
		});

		MenuItem test = new MenuItem();
		test.setTitle(I18N.message("testconnection"));
		test.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.test(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
						new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result.booleanValue())
									SC.say(I18N.message("connectionestablished"));
								else
									SC.warn(I18N.message("connectionfailed"));
							}
						});

			}
		});

		MenuItem enable = new MenuItem();
		enable.setTitle(I18N.message("enable"));
		enable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.changeStatus(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")), true,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								record.setAttribute("eenabled", "0");
								list.refreshRow(list.getRecordIndex(record));
							}
						});
			}
		});

		MenuItem disable = new MenuItem();
		disable.setTitle(I18N.message("disable"));
		disable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.changeStatus(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")), false,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								record.setAttribute("eenabled", "2");
								list.refreshRow(list.getRecordIndex(record));
							}
						});
			}
		});

		MenuItem resetCache = new MenuItem();
		resetCache.setTitle(I18N.message("resetcache"));
		resetCache.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmresetcache"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.resetCache(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									Log.info(I18N.message("cachedeleted"), null);
								}
							});
						}
					}
				});
			}
		});

		if ("0".equals(record.getAttributeAsString("eenabled")))
			contextMenu.setItems(test, disable, delete, resetCache);
		else
			contextMenu.setItems(test, enable, delete, resetCache);
		contextMenu.showContextMenu();
	}

	public void showDetails(GUIEmailAccount account) {
		if (!(details instanceof AccountDetailsPanel)) {
			detailsContainer.removeMember(details);
			details = new AccountDetailsPanel(this);
			detailsContainer.addMember(details);
		}
		((AccountDetailsPanel) details).setAccount(account);
	}

	public ListGrid getList() {
		return list;
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUIEmailAccount share) {
		ListGridRecord record = list.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("email", share.getMailAddress());
		record.setAttribute("eenabled", share.getEnabled() == 1 ? "0" : "2");

		if (record.getAttributeAsString("id") != null
				&& (share.getId() == Long.parseLong(record.getAttributeAsString("id")))) {
			list.refreshRow(list.getRecordIndex(record));
		} else {
			// Append a new record
			record.setAttribute("id", share.getId());
			list.addData(record);
			list.selectRecord(record);
		}
	}
}