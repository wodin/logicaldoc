package com.logicaldoc.gui.frontend.client.document.grid;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.DocUtil;
import com.logicaldoc.gui.common.client.util.DocumentProtectionManager;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.ShowContextMenuEvent;
import com.smartgwt.client.widgets.events.ShowContextMenuHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.events.DataArrivedEvent;
import com.smartgwt.client.widgets.tile.events.SelectionChangedEvent;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * Grid used to show a documents gallery during navigation or searches.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0
 */
public class DocumentsTileGrid extends TileGrid implements DocumentsGrid {
	private Cursor cursor;

	public DocumentsTileGrid(final DataSource ds, final int totalRecords) {
		setTileWidth(200);
		setTileHeight(250);
		setAutoFetchData(true);
		setSelectionType(SelectionStyle.MULTIPLE);
		setShowAllRecords(false);
		setCanReorderTiles(false);
		setWidth100();

		DetailViewerField thumbnail = new DetailViewerField("thumbnail");
		thumbnail.setDetailFormatter(new DetailFormatter() {

			@Override
			public String format(Object value, Record record, DetailViewerField field) {
				int tileSize = 200;
				if (Session.get().getConfig("gui.tile.size") != null)
					tileSize = Integer.parseInt(Session.get().getConfig("gui.tile.size"));

				try {
					if ("folder".equals(record.getAttribute("type")))
						return Util.imageHTML("folder_tile.png", null, tileSize, null);
					else {
						long docId = Long.parseLong(record.getAttribute("id"));
						if (!record.getAttributeAsBoolean("password") || DocumentProtectionManager.isUnprotected(docId))
							return Util.tileImageHTML(docId, null, null, tileSize);
						else
							return Util.imageHTML("blank.png", null, "width:" + tileSize + "px height:" + tileSize
									+ "px");
					}
				} catch (Throwable e) {
					return "";
				}
			}
		});

		DetailViewerField filename = new DetailViewerField("title");
		filename.setDetailFormatter(new DetailFormatter() {

			@Override
			public String format(Object value, Record record, DetailViewerField field) {
				try {
					String html = "<table style='margin-top:2px' align='center' border='0' cellspacing='0'>";

					// The title row
					html += "<tr><td>" + Util.imageHTML(record.getAttribute("icon") + ".png") + "</td><td>" + value
							+ "</td></tr></table>";
					html += "<table align='center' border='0' cellspacing='0'><tr>";
					// The status row
					html += "<td>" + Util.imageHTML(DocUtil.getIndexedIcon(record.getAttributeAsInt("indexed")))
							+ "</td>";
					html += "<td>" + Util.imageHTML(DocUtil.getLockedIcon(record.getAttributeAsInt("locked")))
							+ "</td>";
					html += "<td>"
							+ Util.imageHTML(DocUtil.getPasswordProtectedIcon(record.getAttributeAsBoolean("password")))
							+ "</td>";
					html += "<td>" + Util.imageHTML(DocUtil.getImmutableIcon(record.getAttributeAsInt("immutable")))
							+ "</td>";
					html += "<td>" + Util.imageHTML(DocUtil.getSignedIcon(record.getAttributeAsInt("signed")))
							+ "</td>";
					html += "<td>" + Util.imageHTML(DocUtil.getStampedIcon(record.getAttributeAsInt("stamped")))
							+ "</td>";
					html += "</tr></table>";

					return html;
				} catch (Throwable e) {
					return "";
				}
			}
		});

		setFields(thumbnail, filename);

		if (ds == null) {
			/*
			 * We are searching
			 */
			setSelectionType(SelectionStyle.SINGLE);
		} else {
			setDataSource(ds);
		}

		addDataArrivedHandler(new com.smartgwt.client.widgets.tile.events.DataArrivedHandler() {

			@Override
			public void onDataArrived(DataArrivedEvent event) {
				if (cursor != null) {
					cursor.setMessage(I18N.message("showndocuments", Integer.toString(getCount())));
					cursor.setTotalRecords(totalRecords);
				}

				sortByProperty("filename", true);
			}
		});
	}

	@Override
	public void updateDocument(GUIDocument document) {
		Record record = null;

		// Find the record the corresponds to the given document
		Record[] records = getData();
		for (Record rec : records)
			if (Long.parseLong(rec.getAttribute("id")) == document.getId())
				record = rec;

		if (record != null) {
			GridUtil.updateRecord(document, record);
			redraw();
		}
	}

	@Override
	public void setDocuments(GUIDocument[] documents) {
		Record[] records = new Record[0];
		if (documents == null || documents.length == 0)
			setData(records);

		records = new Record[documents.length];
		for (int i = 0; i < documents.length; i++) {
			GUIDocument doc = documents[i];
			Record record = GridUtil.fromDocument(doc);
			records[i] = record;
		}

		setData(records);
	}

	@Override
	public GUIDocument markSelectedAsCheckedOut() {
		Record record = getSelectedRecord();
		if (record == null)
			return null;
		record.setAttribute("lockUserId", Session.get().getUser().getId());
		record.setAttribute("lockUser", Session.get().getUser().getFullName());
		record.setAttribute("status", Constants.DOC_CHECKED_OUT);
		redraw();
		return getSelectedDocument();
	}

	@Override
	public GUIDocument markSelectedAsCheckedIn() {
		Record record = getSelectedRecord();
		if (record == null)
			return null;
		record.setAttribute("status", Constants.DOC_UNLOCKED);
		record.setAttribute("indexed", Constants.INDEX_TO_INDEX);
		record.setAttribute("signed", 0);
		record.setAttribute("extResId", (String) null);
		record.setAttribute("lockUserId", (String) null);
		record.setAttribute("lockUser", (String) null);
		redraw();

		return getSelectedDocument();
	}

	@Override
	public GUIDocument getSelectedDocument() {
		return GridUtil.toDocument(getSelectedRecord());
	}

	@Override
	public GUIDocument[] getSelectedDocuments() {
		return GridUtil.toDocuments(getSelection());
	}

	@Override
	public GUIDocument[] getDocuments() {
		return GridUtil.toDocuments(getRecordList().toArray());
	}

	@Override
	public int getSelectedIndex() {
		return super.getRecordIndex(getSelectedRecord());
	}

	@Override
	public long[] getSelectedIds() {
		return GridUtil.getIds(getSelection());
	}

	@Override
	public long[] getIds() {
		return GridUtil.getIds(getRecordList().toArray());
	}

	@Override
	public void deselectAll() {
		deselectAllRecords();
	}

	@Override
	public void setCanExpandRows() {
		// Nothing to do
	}

	@Override
	public int getCount() {
		RecordList rl = getRecordList();
		if (rl != null)
			return getRecordList().getLength();
		else
			return 0;
	}

	@Override
	public int getSelectedCount() {
		Record[] selection = getSelection();
		if (selection != null)
			return selection.length;
		else
			return 0;
	}

	@Override
	public void updateRating(int rating) {
		// Nothing to do
	}

	@Override
	public void updateExtResId(String extResId) {
		// Nothing to do
	}

	@Override
	public void showFilters(boolean showFilters) {
		// Nothing to do
	}

	@Override
	public void selectDocument(long docId) {
		deselectAll();
		RecordList rlist = getDataAsRecordList();
		Record record = rlist.find("id", Long.toString(docId));
		if (record != null)
			selectRecord(record);
	}

	@Override
	public void expandVisibleRows() {
		// Nothing to do
	}

	@Override
	public void setCanDrag(boolean drag) {
		super.setCanDrag(drag);
		setCanDragTilesOut(drag);
	}

	@Override
	public void registerDoubleClickHandler(final DoubleClickHandler handler) {
		addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				handler.onDoubleClick(null);
			}
		});
	}

	@Override
	public void registerSelectionChangedHandler(final SelectionChangedHandler handler) {
		addSelectionChangedHandler(new com.smartgwt.client.widgets.tile.events.SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionChangedEvent event) {
				handler.onSelectionChanged(null);
			}
		});
	}

	@Override
	public void registerCellContextClickHandler(final CellContextClickHandler handler) {
		addShowContextMenuHandler(new ShowContextMenuHandler() {

			@Override
			public void onShowContextMenu(ShowContextMenuEvent event) {
				handler.onCellContextClick(null);
				if (event != null)
					event.cancel();
			}
		});
	}

	@Override
	public void registerDataArrivedHandler(final DataArrivedHandler handler) {
		addDataArrivedHandler(new com.smartgwt.client.widgets.tile.events.DataArrivedHandler() {

			@Override
			public void onDataArrived(DataArrivedEvent event) {
				handler.onDataArrived(null);
			}
		});
	}

	@Override
	public void removeSelectedDocuments() {
		removeSelectedData();
	}

	@Override
	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}
}