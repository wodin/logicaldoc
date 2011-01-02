package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUISostConfig;
import com.logicaldoc.gui.common.client.data.ArchivesDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.document.SignClosureDialog;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
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
 * Panel showing the list of export archives
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ExportArchivesList extends VLayout {
	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private Layout listing;

	private Layout detailsContainer;

	private ListGrid list;

	private Canvas details = SELECT_ELEMENT;

	private InfoPanel infoPanel;

	final static Canvas SELECT_ELEMENT = new HTMLPanel("&nbsp;" + I18N.message("selectarchive"));

	public ExportArchivesList() {
		setWidth100();
		infoPanel = new InfoPanel("");
		refresh();
	}

	public void refresh() {
		Canvas[] members = getMembers();
		for (Canvas canvas : members) {
			removeMember(canvas);
		}

		listing = new VLayout();
		detailsContainer = new VLayout();
		details = SELECT_ELEMENT;

		// Initialize the listing panel
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("60%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField name = new ListGridField("name", I18N.message("name"), 250);
		name.setCanFilter(true);

		ListGridField type = new ListGridField("type", I18N.message("type"), 130);
		type.setHidden(true);
		ListGridField typeLabel = new ListGridField("typelabel", I18N.message("type"), 130);
		typeLabel.setCanFilter(false);

		ListGridField status = new ListGridField("statusicon", I18N.message("status"), 50);
		status.setType(ListGridFieldType.IMAGE);
		status.setCanSort(false);
		status.setAlign(Alignment.CENTER);
		status.setShowDefaultContextMenu(false);
		status.setImageURLPrefix(Util.imagePrefix());
		status.setImageURLSuffix(".png");
		status.setCanFilter(false);

		ListGridField created = new ListGridField("created", I18N.message("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter(false));
		created.setCanFilter(false);

		ListGridField creator = new ListGridField("creator", I18N.message("creator"), 110);
		creator.setCanFilter(true);
		ListGridField closer = new ListGridField("closer", I18N.message("closedby"), 110);
		closer.setCanFilter(true);

		ListGridField size = new ListGridField("size", I18N.message("size"), 70);
		size.setAlign(Alignment.CENTER);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());
		size.setCanFilter(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setShowAllRecords(true);
		list.setAutoFetchData(true);
		list.setWidth100();
		list.setHeight100();
		list.setFields(id, created, name, typeLabel, size, status, creator, closer);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(true);
		list.setDataSource(new ArchivesDS(GUIArchive.MODE_EXPORT, null, null));

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
				refresh();
			}
		});

		ToolStripButton addArchive = new ToolStripButton();
		addArchive.setTitle(I18N.message("addarchive"));
		addArchive.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ArchiveDialog dialog = new ArchiveDialog(ExportArchivesList.this);
				dialog.show();
				event.cancel();
			}
		});
		toolStrip.addButton(addArchive);

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
				ListGridRecord record = list.getSelectedRecord();
				try {
					showDetails(Long.parseLong(record.getAttribute("id")), Integer.toString(GUIArchive.STATUS_OPENED)
							.equals(record.getAttribute("status")));
				} catch (Throwable t) {
				}
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showarchives", Integer.toString(list.getTotalRows())));
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
									showDetails(null, true);
								}
							});
						}
					}
				});
			}
		});

		MenuItem close = new MenuItem();
		close.setTitle(I18N.message("close"));
		close.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmarchiveclose"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							if (record.getAttributeAsString("type").equals("" + GUIArchive.TYPE_STORAGE)) {
								service.getSostConfigurations(Session.get().getSid(), id,
										new AsyncCallback<GUISostConfig[]>() {
											@Override
											public void onFailure(Throwable caught) {
												Log.serverError(caught);
											}

											@Override
											public void onSuccess(GUISostConfig[] configs) {
												if (configs.length > 0) {
													// Show Archive validation
													// panel
													ArchiveValidation validation = new ArchiveValidation(
															ExportArchivesList.this, configs, id);
													validation.show();
												} else {
													closeArchive(record);
												}
											}
										});
							} else {
								closeArchive(record);
							}
						}
					}
				});
			}
		});

		MenuItem sign = new MenuItem();
		sign.setTitle(I18N.message("sign"));
		sign.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				String id = record.getAttributeAsString("id");
				String name = record.getAttributeAsString("name");

				SignClosureDialog dialog = new SignClosureDialog(ExportArchivesList.this, id, name);
				dialog.show();
			}
		});

		if (GUIArchive.STATUS_OPENED != Integer.parseInt(record.getAttributeAsString("status")))
			close.setEnabled(false);

		if (GUIArchive.STATUS_READYTOSIGN != Integer.parseInt(record.getAttributeAsString("status")))
			sign.setEnabled(false);

		contextMenu.setItems(close, delete, sign);
		contextMenu.showContextMenu();
	}

	public void showDetails(Long archiveId, boolean readonly) {
		if (details != null)
			detailsContainer.removeMember(details);
		if (archiveId != null)
			details = new ArchiveDetailsPanel(this, archiveId, readonly);
		else
			details = SELECT_ELEMENT;
		detailsContainer.addMember(details);
	}

	public ListGrid getList() {
		return list;
	}

	private void closeArchive(final ListGridRecord record) {
		service.setStatus(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
				GUIArchive.STATUS_CLOSED, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						record.setAttribute("status", "1");
						record.setAttribute("statusicon", "lock");
						list.updateData(record);
						showDetails(Long.parseLong(record.getAttributeAsString("id")), false);
					}
				});
	}
}