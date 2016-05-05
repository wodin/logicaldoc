package com.logicaldoc.gui.frontend.client.subscription;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.SubscriptionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.AuditService;
import com.logicaldoc.gui.frontend.client.services.AuditServiceAsync;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows the list of the user's subscriptions.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PersonalSubscriptions extends com.smartgwt.client.widgets.Window {
	private AuditServiceAsync service = (AuditServiceAsync) GWT.create(AuditService.class);

	private DocumentServiceAsync docService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private ListGrid list;

	private Layout listing = new VLayout();

	public PersonalSubscriptions() {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("subscriptions"));
		setWidth(400);
		setHeight(350);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
		listing.setHeight100();
		listing.setWidth100();
		initListGrid();

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
				initListGrid();
			}
		});
		toolStrip.addFill();

		VLayout layout = new VLayout();
		layout.setMembersMargin(5);
		layout.setMargin(2);
		layout.addMember(toolStrip);
		layout.addMember(listing);

		addItem(layout);
	}

	private void initListGrid() {
		if (list != null) {
			listing.removeMember(list);
			list.destroy();
		}

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField icon = new ListGridField("icon", " ", 25);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		ListGridField name = new ListGridField("name", I18N.message("name"));
		name.setWidth("*");
		name.setCanFilter(true);

		ListGridField created = new ListGridField("created", I18N.message("date"), 120);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter(false));
		created.setCanFilter(false);
		created.setHidden(true);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(false);
		list.setDataSource(new SubscriptionsDS(null, null));
		list.setFields(id, icon, name, created);

		list.sort(1, SortDirection.DESCENDING);
		listing.addMember(list);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String type = list.getSelectedRecord().getAttribute("type");
				Long id = list.getSelectedRecord().getAttributeAsLong("objectid");
				if ("document".equals(type)) {
					Window.open(Util.downloadURL(id, null, true), "_blank", "");
				} else
					DocumentsPanel.get().openInFolder(id, null);
			}
		});

		// Count the total of events and the total of unchecked events
		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent e) {
				int total = list.getTotalRows();
				Session.get().getUser().setSubscriptions(total);
			}
		});
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord[] selection = list.getSelectedRecords();
		if (selection == null || selection.length == 0)
			return;
		final long[] ids = new long[selection.length];
		for (int i = 0; i < selection.length; i++) {
			ids[i] = Long.parseLong(selection[i].getAttribute("id"));
		}

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.deleteSubscriptions(ids, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.removeSelectedData();
									list.deselectAllRecords();
								}
							});
						}
					}
				});
			}
		});

		MenuItem edit = new MenuItem();
		edit.setTitle(I18N.message("edit"));
		edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SubscriptionDialog dialog = new SubscriptionDialog(list);
				dialog.show();
			}
		});

		MenuItem openInFolder = new MenuItem();
		openInFolder.setTitle(I18N.message("openinfolder"));
		openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				String type = record.getAttribute("type");
				String id = record.getAttribute("objectid");
				if ("folder".equals(type))
					DocumentsPanel.get().openInFolder(Long.parseLong(id), null);
				else {
					docService.getById(Long.parseLong(id), new AsyncCallback<GUIDocument>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIDocument result) {
							DocumentsPanel.get().openInFolder(result.getFolder().getId(), result.getId());
						}
					});
				}
			}
		});

		contextMenu.setItems(openInFolder, edit, delete);
		contextMenu.showContextMenu();
	}
}