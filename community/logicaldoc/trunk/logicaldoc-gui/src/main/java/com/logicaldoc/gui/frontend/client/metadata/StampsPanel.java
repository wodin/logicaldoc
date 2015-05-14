package com.logicaldoc.gui.frontend.client.metadata;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIStamp;
import com.logicaldoc.gui.common.client.data.StampsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.impex.folders.ImportFolderDetailsPanel;
import com.logicaldoc.gui.frontend.client.services.StampService;
import com.logicaldoc.gui.frontend.client.services.StampServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
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
 * Panel showing the list of stamps
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class StampsPanel extends VLayout {
	private StampServiceAsync service = (StampServiceAsync) GWT.create(StampService.class);

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	private ListGrid list;

	private Canvas details = SELECT_POLICY;

	private InfoPanel infoPanel;

	final static Canvas SELECT_POLICY = new HTMLPanel("&nbsp;" + I18N.message("selectstamp"));

	public StampsPanel() {
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
			details = SELECT_POLICY;
		}

		// Initialize the listing panel
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("70%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField name = new ListGridField("name", I18N.message("name"), 150);

		ListGridField description = new ListGridField("description", I18N.message("description"), 200);

		ListGridField text = new ListGridField("text", I18N.message("text"), 200);

		ListGridField image = new ListGridField("image", I18N.message("image"), 300);
		image.setCanFilter(false);
		image.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (value != null && !value.toString().trim().equals(""))
					return "<img height='60px' src='" + Util.contextPath() + "stampimage/" + value + "?sid="
							+ Session.get().getSid() + "&random=" + new Date().getTime() + "'/>";
				else
					return "";
			}
		});

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
		list.setFields(enabled, id, name, description, text, image);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setCanSort(false);
		list.setFilterOnKeypress(true);
		list.setDataSource(new StampsDS());

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

		ToolStripButton addStamp = new ToolStripButton();
		addStamp.setTitle(I18N.message("addstamp"));
		toolStrip.addButton(addStamp);
		addStamp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				GUIStamp stamp = new GUIStamp();
				showStampDetails(stamp);
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
					service.getStamp(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUIStamp>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIStamp stamp) {
									showStampDetails(stamp);
								}
							});
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showstamps", Integer.toString(list.getTotalRows())));
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
									showStampDetails(null);
								}
							});
						}
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

		if ("0".equals(record.getAttributeAsString("eenabled")))
			contextMenu.setItems(disable, delete);
		else
			contextMenu.setItems(enable, delete);

		contextMenu.showContextMenu();
	}

	public void showStampDetails(GUIStamp stamp) {
		if (!(details instanceof StampDetailsPanel)) {
			detailsContainer.removeMember(details);
			details = new StampDetailsPanel(this);
			detailsContainer.addMember(details);
		}
		((StampDetailsPanel) details).setStamp(stamp);
	}

	public ListGrid getList() {
		return list;
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUIStamp stamp) {
		ListGridRecord record = list.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("id", "" + stamp.getId());
		record.setAttribute("name", stamp.getName());
		record.setAttribute("description", "" + stamp.getDescription());
		if (stamp.getType() == GUIStamp.TYPE_TEXT) {
			record.setAttribute("text", "" + stamp.getText());
			record.setAttribute("image", "");
		} else {
			record.setAttribute("image", "" + stamp.getId());
			record.setAttribute("text", "");
		}

		if (record.getAttributeAsString("id") != null
				&& (stamp.getId() == Long.parseLong(record.getAttributeAsString("id")))) {
			list.refreshRow(list.getRecordIndex(record));
		} else {
			// Append a new record
			record.setAttribute("id", stamp.getId());
			list.addData(record);
			list.selectRecord(record);
		}
	}
}