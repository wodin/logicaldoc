package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
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
	private DocumentsDS dataSource;

	private ListGrid list;

	private InfoPanel infoPanel;

	public DocumentsListPanel(GUIFolder folder, final Long hiliteDoc, Integer maxRows) {
		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.getMessage("title"), 200);

		ListGridField size = new ListGridField("size", I18N.getMessage("size"), 70);
		size.setAlign(Alignment.CENTER);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix() + "/application/");
		icon.setImageURLSuffix(".png");

		ListGridField version = new ListGridField("version", I18N.getMessage("version"), 55);
		version.setAlign(Alignment.CENTER);

		ListGridField lastModified = new ListGridField("lastModified", I18N.getMessage("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter());

		ListGridField publisher = new ListGridField("publisher", I18N.getMessage("publisher"), 90);
		publisher.setAlign(Alignment.CENTER);

		ListGridField published = new ListGridField("published", I18N.getMessage("publishedon"), 110);
		published.setAlign(Alignment.CENTER);
		published.setType(ListGridFieldType.DATE);
		published.setCellFormatter(new DateCellFormatter());

		ListGridField creator = new ListGridField("creator", I18N.getMessage("creator"), 90);
		creator.setAlign(Alignment.CENTER);

		ListGridField created = new ListGridField("created", I18N.getMessage("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter());

		ListGridField customId = new ListGridField("customId", I18N.getMessage("customid"), 110);
		customId.setType(ListGridFieldType.TEXT);

		ListGridField immutable = new ListGridField("immutable", " ", 24);
		immutable.setType(ListGridFieldType.IMAGE);
		immutable.setCanSort(false);
		immutable.setAlign(Alignment.CENTER);
		immutable.setShowDefaultContextMenu(false);
		immutable.setImageURLPrefix(Util.imagePrefix() + "/application/");
		immutable.setImageURLSuffix(".png");

		ListGridField indexed = new ListGridField("indexed", " ", 24);
		indexed.setType(ListGridFieldType.IMAGE);
		indexed.setCanSort(false);
		indexed.setAlign(Alignment.CENTER);
		indexed.setShowDefaultContextMenu(false);
		indexed.setImageURLPrefix(Util.imagePrefix() + "/application/");
		indexed.setImageURLSuffix(".png");

		ListGridField locked = new ListGridField("locked", " ", 24);
		locked.setType(ListGridFieldType.IMAGE);
		locked.setCanSort(false);
		locked.setAlign(Alignment.CENTER);
		locked.setShowDefaultContextMenu(false);
		locked.setImageURLPrefix(Util.imagePrefix() + "/application/");
		locked.setImageURLSuffix(".png");

		ListGridField filename = new ListGridField("filename", I18N.getMessage("filename"), 200);
		filename.setHidden(true);

		ListGridField lockUserId = new ListGridField("lockUserId", " ", 24);
		lockUserId.setHidden(true);

		list = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("title")) {
					if ("stop".equals(record.getAttribute("immutable"))) {
						return "color: #888888; font-style: italic;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		dataSource = new DocumentsDS(folder.getId(), maxRows);
		list.setDataSource(dataSource);
		list.setFields(indexed, locked, immutable, icon, title, size, lastModified, version, publisher, published,
				creator, created, customId, filename);

		// Prepare a panel containing a title and the documents list
		infoPanel = new InfoPanel("");

		addMember(infoPanel);
		addMember(list);

		list.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				if ("indexed".equals(list.getFieldName(event.getColNum()))) {
					ListGridRecord record = event.getRecord();
					if ("indexed".equals(record.getAttribute("indexed"))) {
						String id = list.getSelectedRecord().getAttribute("id");
						Window.open("download?sid=" + Session.get().getSid() + "&docId=" + id + "&downloadText=true",
								"_self", "");
					}
				}
			}
		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				DocumentsPanel.get().onSelectedDocument(Long.parseLong(event.getRecord().getAttribute("id")));
			}
		});

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new DocumentContextMenu(Session.get().getCurrentFolder(), list);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		list.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open("download?sid=" + Session.get().getSid() + "&docId=" + id + "&open=true", "_blank", "");
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.getMessage("showndocuments", Integer.toString(list.getTotalRows())));
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

	/**
	 * Updates the selected record with new data
	 */
	public void updateSelectedRecord(GUIDocument document) {
		ListGridRecord selectedRecord = list.getSelectedRecord();
		if (selectedRecord != null) {
			selectedRecord.setAttribute("title", document.getTitle());
			selectedRecord.setAttribute("customId", document.getCustomId());
			selectedRecord.setAttribute("version", document.getVersion());
			selectedRecord.setAttribute("size", document.getSize());
			selectedRecord.setAttribute("lastModified", document.getLastModified());
			selectedRecord.setAttribute("publisher", document.getPublisher());
			selectedRecord.setAttribute("published", document.getDate());
			selectedRecord.setAttribute("creator", document.getCreator());
			selectedRecord.setAttribute("created", document.getCreation());
			list.updateData(selectedRecord);
		}
	}

	public void hiliteDocument(long docId) {
		list.deselectAllRecords();
		RecordList rlist = list.getDataAsRecordList();
		Record record = rlist.find("id", Long.toString(docId));
		if (record != null) {
			list.selectSingleRecord(record);
		}
	}

	public ListGrid getList() {
		return list;
	}
}