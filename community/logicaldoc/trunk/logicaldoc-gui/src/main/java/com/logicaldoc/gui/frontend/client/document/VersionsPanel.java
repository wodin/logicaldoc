package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.common.client.data.VersionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows a list of versions in a tabular way.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class VersionsPanel extends DocumentDetailTab {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private VersionsDS dataSource;

	private ListGrid listGrid;

	private Menu contextMenu;

	public VersionsPanel(final GUIDocument document) {
		super(document, null);
		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField user = new ListGridField("user", I18N.getMessage("user"), 100);
		ListGridField event = new ListGridField("event", I18N.getMessage("event"), 200);
		ListGridField version = new ListGridField("version", I18N.getMessage("version"), 70);
		ListGridField fileVersion = new ListGridField("fileVersion", I18N.getMessage("fileversion"), 70);
		ListGridField date = new ListGridField("date", I18N.getMessage("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter());
		ListGridField comment = new ListGridField("comment", I18N.getMessage("comment"));

		listGrid = new ListGrid();
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new VersionsDS(document.getId());
		listGrid.setDataSource(dataSource);
		listGrid.setFields(user, event, version, fileVersion, date, comment);
		addMember(listGrid);

		setupContextMenu();

		listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				Window.open("download?sid=" + Session.get().getSid() + "&docId=" + document.getId()
						+ "&versionId=" + record.getAttribute("version") + "&open=true", "_blank", "");
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				contextMenu.showContextMenu();
				event.cancel();
			}
		});
	}

	/**
	 * Prepares the context menu.
	 */
	private void setupContextMenu() {
		contextMenu = new Menu();
		List<MenuItem> items = new ArrayList<MenuItem>();
		MenuItem downloadItem = new MenuItem();
		downloadItem.setTitle(I18N.getMessage("compare"));
		downloadItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				// Detect the two selected records
				ListGridRecord[] selection = listGrid.getSelection();
				if (selection == null || selection.length != 2){
					SC.warn(I18N.getMessage("select2versions"));
				    return;
				}
				documentService.getVersionsById(Session.get().getSid(), Long.parseLong(selection[0]
						.getAttribute("id")), Long.parseLong(selection[1].getAttribute("id")),
						new AsyncCallback<GUIVersion[]>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIVersion[] result) {
								VersionsDiff diffWinfow = new VersionsDiff(result[0], result[1]);
								diffWinfow.show();
							}
						});
			}
		});

		items.add(downloadItem);
		contextMenu.setItems(items.toArray(new MenuItem[0]));
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}