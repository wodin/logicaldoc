package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
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
	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DocumentsDS dataSource;

	private ListGrid list;

	private InfoPanel infoPanel;

	private boolean filters;

	public DocumentsListPanel(GUIFolder folder, final Long hiliteDoc, Integer max) {
		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.message("title"), 200);
		title.setCanFilter(true);

		ListGridField size = new ListGridField("size", I18N.message("size"), 70);
		size.setAlign(Alignment.RIGHT);
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

		ListGridField version = new ListGridField("version", I18N.message("version"), 55);
		version.setAlign(Alignment.CENTER);
		version.setCanFilter(true);

		ListGridField lastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);

		ListGridField publisher = new ListGridField("publisher", I18N.message("publisher"), 90);
		publisher.setAlign(Alignment.CENTER);
		publisher.setCanFilter(true);

		ListGridField published = new ListGridField("published", I18N.message("publishedon"), 110);
		published.setAlign(Alignment.CENTER);
		published.setType(ListGridFieldType.DATE);
		published.setCellFormatter(new DateCellFormatter(false));
		published.setCanFilter(false);

		ListGridField creator = new ListGridField("creator", I18N.message("creator"), 90);
		creator.setAlign(Alignment.CENTER);
		creator.setCanFilter(true);
		creator.setHidden(true);

		ListGridField created = new ListGridField("created", I18N.message("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter(false));
		created.setCanFilter(false);
		created.setHidden(true);

		ListGridField sourceDate = new ListGridField("sourceDate", I18N.message("date"), 110);
		sourceDate.setAlign(Alignment.CENTER);
		sourceDate.setType(ListGridFieldType.DATE);
		sourceDate.setCellFormatter(new DateCellFormatter(true));
		sourceDate.setCanFilter(false);
		sourceDate.setHidden(true);

		ListGridField sourceAuthor = new ListGridField("sourceAuthor", I18N.message("author"), 90);
		sourceAuthor.setAlign(Alignment.CENTER);
		sourceAuthor.setCanFilter(true);
		sourceAuthor.setHidden(true);

		ListGridField customId = new ListGridField("customId", I18N.message("customid"), 110);
		customId.setType(ListGridFieldType.TEXT);

		ListGridField type = new ListGridField("type", I18N.message("type"), 55);
		type.setType(ListGridFieldType.TEXT);
		type.setAlign(Alignment.CENTER);

		ListGridField immutable = new ListGridField("immutable", " ", 24);
		immutable.setType(ListGridFieldType.IMAGE);
		immutable.setCanSort(false);
		immutable.setAlign(Alignment.CENTER);
		immutable.setShowDefaultContextMenu(false);
		immutable.setImageURLPrefix(Util.imagePrefix());
		immutable.setImageURLSuffix(".png");
		immutable.setCanFilter(false);

		ListGridField indexed = new ListGridField("indexed", " ", 24);
		indexed.setType(ListGridFieldType.IMAGE);
		indexed.setCanSort(false);
		indexed.setAlign(Alignment.CENTER);
		indexed.setShowDefaultContextMenu(false);
		indexed.setImageURLPrefix(Util.imagePrefix());
		indexed.setImageURLSuffix(".png");
		indexed.setCanFilter(false);

		ListGridField locked = new ListGridField("locked", " ", 24);
		locked.setType(ListGridFieldType.IMAGE);
		locked.setCanSort(false);
		locked.setAlign(Alignment.CENTER);
		locked.setShowDefaultContextMenu(false);
		locked.setImageURLPrefix(Util.imagePrefix());
		locked.setImageURLSuffix(".png");
		locked.setCanFilter(false);

		ListGridField signed = new ListGridField("signed", " ", 24);
		signed.setType(ListGridFieldType.IMAGE);
		signed.setCanSort(false);
		signed.setAlign(Alignment.CENTER);
		signed.setShowDefaultContextMenu(false);
		signed.setImageURLPrefix(Util.imagePrefix());
		signed.setImageURLSuffix(".png");
		signed.setCanFilter(false);

		ListGridField filename = new ListGridField("filename", I18N.message("filename"), 200);
		filename.setHidden(true);
		filename.setCanFilter(true);

		ListGridField lockUserId = new ListGridField("lockUserId", " ", 24);
		lockUserId.setHidden(true);
		lockUserId.setCanFilter(false);

		ListGridField rating = new ListGridField("rating", I18N.message("rating"), 95);
		rating.setType(ListGridFieldType.IMAGE);
		rating.setCanSort(false);
		rating.setAlign(Alignment.CENTER);
		rating.setImageURLPrefix(Util.imagePrefix());
		rating.setImageURLSuffix(".png");
		rating.setImageWidth(88);
		rating.setCanFilter(false);
		rating.setHidden(true);

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
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		list.setFilterOnKeypress(true);
		dataSource = new DocumentsDS(folder.getId(), null, max, null, null);
		list.setDataSource(dataSource);

		list.setCanDrag(true);
		list.setCanDragRecordsOut(true);

		list.setFields(indexed, locked, immutable, signed, icon, filename, title, lastModified, type, size, version,
				publisher, published, creator, created, sourceDate, sourceAuthor, customId, rating);

		// Prepare a panel containing a title and the documents list
		infoPanel = new InfoPanel("");

		addMember(infoPanel);
		addMember(list);

		list.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				ListGridRecord record = event.getRecord();
				if ("indexed".equals(list.getFieldName(event.getColNum()))) {
					if ("indexed".equals(record.getAttribute("indexed"))) {
						String id = list.getSelectedRecord().getAttribute("id");
						if (Session.get().getCurrentFolder().isDownload())
							WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
									+ "&docId=" + id + "&downloadText=true");
					}
				} else if ("signed".equals(list.getFieldName(event.getColNum()))) {
					if (Feature.enabled(Feature.DIGITAL_SIGN)) {
						if ("rosette".equals(record.getAttribute("signed"))) {
							String id = list.getSelectedRecord().getAttribute("id");
							String fileName = list.getSelectedRecord().getAttribute("filename") + ".p7m";
							SignVerifyDialog verify = new SignVerifyDialog(id, fileName);
							verify.show();
							event.cancel();
						}
					}
				} else if ("rating".equals(list.getFieldName(event.getColNum()))) {
					long id = Long.parseLong(list.getSelectedRecord().getAttribute("id"));
					String ratingImageName = list.getSelectedRecord().getAttribute("rating");
					final int docRating = Integer.parseInt(ratingImageName.replace("rating", ""));
					documentService.getRating(Session.get().getSid(), id, new AsyncCallback<GUIRating>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIRating rating) {
							if (rating != null) {
								RatingDialog dialog = new RatingDialog(docRating, rating);
								dialog.show();
							}
						}
					});
				}
			}
		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				onRecordSelected();
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
				if (Session.get().getCurrentFolder().isDownload())
					WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
							+ id + "&open=true");
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showndocuments", Integer.toString(list.getTotalRows())));
				if (hiliteDoc != null)
					DocumentsListPanel.this.hiliteDocument(hiliteDoc);
			}
		});

		final String previouslySavedState = (String) Offline.get("doclist");
		if (previouslySavedState != null) {
			list.addDrawHandler(new DrawHandler() {
				@Override
				public void onDraw(DrawEvent event) {
					// restore any previously saved view state for this grid
					list.setViewState(previouslySavedState);
				}
			});
		}
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
			selectedRecord.setAttribute("indexed", "blank");
			selectedRecord.setAttribute("title", document.getTitle());
			selectedRecord.setAttribute("customId", document.getCustomId());
			selectedRecord.setAttribute("version", document.getVersion());
			selectedRecord.setAttribute("size", document.getFileSize());
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

	public void toggleFilters() {
		list.setShowFilterEditor(!filters);
		filters = !filters;
	}

	protected void onRecordSelected() {
		ListGridRecord record = list.getSelectedRecord();
		if (record != null)
			DocumentsPanel.get().onSelectedDocument(Long.parseLong(record.getAttribute("id")), false);
	}
}