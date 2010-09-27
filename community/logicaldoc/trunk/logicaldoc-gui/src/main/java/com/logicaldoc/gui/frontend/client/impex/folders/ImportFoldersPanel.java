package com.logicaldoc.gui.frontend.client.impex.folders;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIShare;
import com.logicaldoc.gui.common.client.data.ImportFoldersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.services.ImportFoldersService;
import com.logicaldoc.gui.frontend.client.services.ImportFoldersServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
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
 * Panel showing the list of import folders
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ImportFoldersPanel extends VLayout {
	private ImportFoldersServiceAsync service = (ImportFoldersServiceAsync) GWT.create(ImportFoldersService.class);

	private SettingServiceAsync settingService = (SettingServiceAsync) GWT.create(SettingService.class);

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	private ListGrid list;

	private Canvas details = SELECT_FOLDER;

	private InfoPanel infoPanel;

	final static Canvas SELECT_FOLDER = new HTMLPanel("&nbsp;" + I18N.message("selectimportfolder"));

	public ImportFoldersPanel() {
		setWidth100();
		infoPanel = new InfoPanel("");
		init();
	}

	public void init() {
		detailsContainer.clear();
		listing.clear();
		if (list != null)
			listing.removeMember(list);
		if (details != null && details instanceof ImportFolderDetailsPanel) {
			detailsContainer.removeMember(details);
			details = SELECT_FOLDER;
		}

		// Initialize the listing panel
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("70%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField src = new ListGridField("src", I18N.message("source"), 300);
		src.setCanFilter(true);

		ListGridField type = new ListGridField("type", I18N.message("type"), 50);
		type.setCanFilter(false);

		ListGridField enabled = new ListGridField("eenabled", " ", 24);
		enabled.setType(ListGridFieldType.IMAGE);
		enabled.setCanSort(false);
		enabled.setAlign(Alignment.CENTER);
		enabled.setShowDefaultContextMenu(false);
		enabled.setImageURLPrefix(Util.imagePrefix());
		enabled.setImageURLSuffix(".gif");
		enabled.setCanFilter(false);

		list = new ListGrid();
		list.setShowAllRecords(true);
		list.setAutoFetchData(true);
		list.setWidth100();
		list.setHeight100();
		list.setFields(id, src, type, enabled);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);
		list.setDataSource(ImportFoldersDS.get());

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

		ToolStripButton addLocal = new ToolStripButton();
		addLocal.setTitle(I18N.message("addlocalfolder"));
		addLocal.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				GUIShare share = new GUIShare();
				share.setProvider("file");
				showShareDetails(share);
			}
		});
		if (Feature.visible(Feature.IMPORT_LOCAL_FOLDERS)) {
			toolStrip.addButton(addLocal);
			if (!Feature.enabled(Feature.IMPORT_LOCAL_FOLDERS)) {
				addLocal.setDisabled(true);
				addLocal.setTooltip(I18N.message("featuredisabled"));
			}
		}

		ToolStripButton addRemote = new ToolStripButton();
		addRemote.setTitle(I18N.message("addremotefolder"));
		addRemote.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				GUIShare share = new GUIShare();
				share.setProvider("smb");
				showShareDetails(share);
			}
		});
		if (Feature.visible(Feature.IMPORT_REMOTE_FOLDERS)) {
			toolStrip.addButton(addRemote);
			if (!Feature.enabled(Feature.IMPORT_REMOTE_FOLDERS)) {
				addRemote.setDisabled(true);
				addRemote.setTooltip(I18N.message("featuredisabled"));
			}
		}

		ToolStripButton discovery = new ToolStripButton();
		discovery.setTitle(I18N.message("automaticdiscovery"));
		discovery.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadValues(Session.get().getSid(), new String[] { "smb.username", "smb.password",
						"smb.lang" }, new AsyncCallback<String[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(String[] values) {
						DiscoveryDialog discovery = new DiscoveryDialog(values[0], values[1], values[2]);
						discovery.show();
					}
				});
			}
		});
		if (Feature.visible(Feature.SHARE_DISCOVERY)) {
			toolStrip.addButton(discovery);
			if (!Feature.enabled(Feature.SHARE_DISCOVERY)) {
				discovery.setDisabled(true);
				discovery.setTooltip(I18N.message("featuredisabled"));
			}
		}

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
					service.getShare(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUIShare>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIShare share) {
									showShareDetails(share);
								}
							});
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showimportfolders", Integer.toString(list.getTotalRows())));
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
				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
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
									showShareDetails(null);
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
								list.updateData(record);
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
								list.updateData(record);
							}
						});
			}
		});

		MenuItem resetCache = new MenuItem();
		resetCache.setTitle(I18N.message("resetcache"));
		resetCache.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmresetcache"), new BooleanCallback() {
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

	private void showShareDetails(GUIShare share) {
		if (!(details instanceof ImportFolderDetailsPanel)) {
			detailsContainer.removeMember(details);
			details = new ImportFolderDetailsPanel(this);
			detailsContainer.addMember(details);
		}
		((ImportFolderDetailsPanel) details).setShare(share);
	}

	public ListGrid getList() {
		return list;
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUIShare share) {
		ListGridRecord record = list.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("src", share.getPath());
		record.setAttribute("eenabled", share.getEnabled() == 1 ? "0" : "2");
		record.setAttribute("type", "smb".equals(share.getProvider()) ? I18N.message("remote") : I18N.message("local"));

		if (record.getAttributeAsString("id") != null
				&& (share.getId() == Long.parseLong(record.getAttributeAsString("id")))) {
			list.updateData(record);
		} else {
			// Append a new record
			record.setAttribute("id", share.getId());
			list.addData(record);
			list.selectRecord(record);
		}
	}
}