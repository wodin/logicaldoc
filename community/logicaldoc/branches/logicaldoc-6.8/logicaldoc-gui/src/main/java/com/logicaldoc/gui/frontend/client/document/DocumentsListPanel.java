package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;

/**
 * This panel shows a list of documents in a tabular way.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsListPanel extends VLayout {
	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DocumentsDS dataSource;

	private DocumentsGrid grid;

	private InfoPanel infoPanel;

	private boolean filters;

	public DocumentsListPanel(GUIFolder folder, final Long hiliteDoc, Integer max) {
		dataSource = new DocumentsDS(folder.getId(), null, max, null, null);
		grid = new DocumentsGrid(dataSource);

		if (folder.isDownload()) {
			grid.setCanDrag(true);
			grid.setCanDragRecordsOut(true);
		}

		// Prepare a panel containing a title and the documents list
		infoPanel = new InfoPanel("");

		addMember(infoPanel);
		addMember(grid);

		grid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				long id = Long.parseLong(grid.getSelectedRecord().getAttribute("id"));
				String filename = grid.getSelectedRecord().getAttribute("filename");
				String version = grid.getSelectedRecord().getAttribute("version");

				if (filename == null)
					filename = grid.getSelectedRecord().getAttribute("title") + "."
							+ grid.getSelectedRecord().getAttribute("type");

				if (Session.get().getCurrentFolder().isDownload()
						&& "download".equals(Session.get().getInfo().getConfig("gui.doubleclick")))
					try {
						WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
								+ "&docId=" + id + "&open=true");
					} catch (Throwable t) {

					}
				else {
					GUIFolder folder = Session.get().getCurrentFolder();
					PreviewPopup iv = new PreviewPopup(id, version, filename, folder != null && folder.isDownload());
					iv.show();
				}

				event.cancel();
			}
		});

		grid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				onRecordSelected();
			}
		});

		grid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new DocumentContextMenu(Session.get().getCurrentFolder(), grid);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		grid.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showndocuments", Integer.toString(grid.getTotalRows())));
				if (hiliteDoc != null)
					DocumentsListPanel.this.hiliteDocument(hiliteDoc);
			}
		});
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}

	public void hiliteDocument(long docId) {
		grid.deselectAllRecords();
		RecordList rlist = grid.getDataAsRecordList();
		Record record = rlist.find("id", Long.toString(docId));
		if (record != null) {
			grid.selectSingleRecord(record);
		}
	}

	public DocumentsGrid getGrid() {
		return grid;
	}

	public void toggleFilters() {
		grid.setShowFilterEditor(!filters);
		filters = !filters;
	}

	protected void onRecordSelected() {
		// Avoid server load in case of multiple selections
		if (grid.getSelectedRecords() != null && grid.getSelectedRecords().length > 1)
			return;

		documentService.getById(Session.get().getSid(), grid.getSelectedDocument().getId(),
				new AsyncCallback<GUIDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						/*
						 * Sometimes we can have spurious errors using Firefox.
						 */
						if (Session.get().isDevel())
							Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIDocument result) {
						Session.get().setCurrentDocument(result);
					}
				});
	}
}