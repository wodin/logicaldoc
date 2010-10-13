package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.VersionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows a list of versions of an archive in a tabular way.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class VersionsPanel extends VLayout {

	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private VersionsDS dataSource;

	private ListGrid listGrid;

	private int max = 100;

	private ToolStrip toolbar = new ToolStrip();

	public VersionsPanel(final Long archiveId, final boolean readonly) {
		final IntegerItem maxItem = ItemFactory.newValidateIntegerItem("max", "", null, 1, null);
		maxItem.setHint(I18N.message("elements"));
		maxItem.setShowTitle(false);
		maxItem.setDefaultValue(100);
		maxItem.setWidth(40);

		toolbar.setWidth100();
		
		ToolStripButton display = new ToolStripButton();
		display.setTitle(I18N.message("display"));
		toolbar.addButton(display);
		toolbar.addFormItem(maxItem);
		display.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (maxItem.validate() && maxItem.getValue() != null) {
					if (maxItem.getValue() instanceof Integer)
						max = (Integer) maxItem.getValue();
					else
						max = Integer.parseInt(maxItem.getValue().toString());
					initListGrid(archiveId, readonly);
				}
			}
		});
		toolbar.addFill();

		addMember(toolbar);
		initListGrid(archiveId, readonly);
	}

	private void initListGrid(final Long archiveId, final boolean readonly) {
		if (listGrid != null)
			removeMember(listGrid);

		ListGridField id = new ListGridField("id", 80);
		id.setHidden(true);

		ListGridField docid = new ListGridField("docid", I18N.message("id"), 80);

		ListGridField customid = new ListGridField("customid", I18N.message("customid"), 100);
		ListGridField title = new ListGridField("title", I18N.message("title"), 200);

		ListGridField version = new ListGridField("version", I18N.message("version"), 70);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);

		ListGridField size = new ListGridField("size", I18N.message("size"), 70);
		size.setAlign(Alignment.CENTER);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());
		size.setCanFilter(false);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		listGrid = new ListGrid();
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new VersionsDS(null, archiveId, max);
		listGrid.setDataSource(dataSource);
		listGrid.setFields(id, docid, customid, icon, title, version, date, size);
		addMember(listGrid, 1);

		listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				Window.open(
						GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
								+ record.getAttributeAsString("docid") + "&versionId=" + record.getAttribute("id")
								+ "&open=true", "_blank", "");
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = setupContextMenu(archiveId, readonly);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu(final long archiveId, boolean readonly) {
		final ListGridRecord[] selection = listGrid.getSelection();

		Menu contextMenu = new Menu();

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;
				final Long[] ids = new Long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							listGrid.removeSelectedData();
							listGrid.deselectAllRecords();

							service.deleteVersions(Session.get().getSid(), archiveId, ids, new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void ret) {

								}
							});
						}
					}
				});
			}
		});

		if (!readonly)
			delete.setEnabled(false);

		contextMenu.setItems(delete);
		return contextMenu;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}