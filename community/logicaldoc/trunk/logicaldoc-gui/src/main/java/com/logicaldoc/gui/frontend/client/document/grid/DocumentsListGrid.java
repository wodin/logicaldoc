package com.logicaldoc.gui.frontend.client.document.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.RatingDialog;
import com.logicaldoc.gui.frontend.client.document.SignatureViewer;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SignService;
import com.logicaldoc.gui.frontend.client.services.SignServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.ViewStateChangedEvent;
import com.smartgwt.client.widgets.grid.events.ViewStateChangedHandler;

/**
 * Grid used to show a documents list during navigation or searches.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class DocumentsListGrid extends ListGrid implements DocumentsGrid {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private Cursor cursor = null;

	public DocumentsListGrid(final DataSource ds, final int totalRecords) {
		setEmptyMessage(I18N.message("notitemstoshow"));
		setCanFreezeFields(true);
		setAutoFetchData(true);
		setFilterOnKeypress(true);

		ListGridField id = new ListGridField("id", I18N.message("id"), 50);
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.message("title"), 200);
		title.setCanFilter(true);

		ListGridField size = new ListGridField("size", I18N.message("size"), 70);
		size.setAlign(Alignment.RIGHT);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());
		size.setCanFilter(false);

		ListGridField icon = new ListGridField("icon", " ", 20);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		ListGridField version = new ListGridField("version", I18N.message("version"), 55);
		version.setAlign(Alignment.CENTER);
		version.setHidden(true);
		version.setCanFilter(true);

		ListGridField lastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);
		lastModified.setHidden(true);

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

		ListGridField immutable = new ListGridField("immutable", " ", 20);
		immutable.setType(ListGridFieldType.IMAGE);
		immutable.setCanSort(false);
		immutable.setAlign(Alignment.CENTER);
		immutable.setShowDefaultContextMenu(false);
		immutable.setImageURLPrefix(Util.imagePrefix());
		immutable.setImageURLSuffix(".png");
		immutable.setCanFilter(false);

		ListGridField indexed = new ListGridField("indexed", " ", 20);
		indexed.setType(ListGridFieldType.IMAGE);
		indexed.setCanSort(false);
		indexed.setAlign(Alignment.CENTER);
		indexed.setShowDefaultContextMenu(false);
		indexed.setImageURLPrefix(Util.imagePrefix());
		indexed.setImageURLSuffix(".png");
		indexed.setCanFilter(false);

		ListGridField locked = new ListGridField("locked", " ", 20);
		locked.setType(ListGridFieldType.IMAGE);
		locked.setCanSort(false);
		locked.setAlign(Alignment.CENTER);
		locked.setShowDefaultContextMenu(false);
		locked.setImageURLPrefix(Util.imagePrefix());
		locked.setImageURLSuffix(".png");
		locked.setCanFilter(false);

		ListGridField signed = new ListGridField("signed", " ", 20);
		signed.setType(ListGridFieldType.IMAGE);
		signed.setCanSort(false);
		signed.setAlign(Alignment.CENTER);
		signed.setShowDefaultContextMenu(false);
		signed.setImageURLPrefix(Util.imagePrefix());
		signed.setImageURLSuffix(".png");
		signed.setCanFilter(false);

		ListGridField stamped = new ListGridField("stamped", " ", 20);
		stamped.setType(ListGridFieldType.IMAGE);
		stamped.setCanSort(false);
		stamped.setAlign(Alignment.CENTER);
		stamped.setShowDefaultContextMenu(false);
		stamped.setImageURLPrefix(Util.imagePrefix());
		stamped.setImageURLSuffix(".png");
		stamped.setCanFilter(false);

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

		ListGridField fileVersion = new ListGridField("fileVersion", I18N.message("fileversion"), 70);
		fileVersion.setHidden(false);
		fileVersion.setCanFilter(true);
		fileVersion.setAlign(Alignment.CENTER);

		ListGridField comment = new ListGridField("comment", I18N.message("comment"), 300);
		comment.setHidden(true);
		comment.setCanFilter(true);

		ListGridField source = new ListGridField("source", I18N.message("source"), 100);
		source.setHidden(true);
		source.setCanFilter(true);

		ListGridField sourceId = new ListGridField("sourceId", I18N.message("sourceid"), 100);
		sourceId.setHidden(true);
		sourceId.setCanFilter(true);

		ListGridField recipient = new ListGridField("recipient", I18N.message("recipient"), 100);
		recipient.setHidden(true);
		recipient.setCanFilter(true);

		ListGridField object = new ListGridField("object", I18N.message("object"), 100);
		object.setHidden(true);
		object.setCanFilter(true);

		ListGridField coverage = new ListGridField("coverage", I18N.message("coverage"), 100);
		coverage.setHidden(true);
		coverage.setCanFilter(true);

		ListGridField wfStatus = new ListGridField("workflowStatus", I18N.message("workflowstatus"), 100);
		wfStatus.setHidden(true);
		wfStatus.setCanFilter(true);
		wfStatus.setAlign(Alignment.LEFT);

		ListGridField startPublishing = new ListGridField("startPublishing", I18N.message("startpublishing"), 110);
		startPublishing.setAlign(Alignment.CENTER);
		startPublishing.setType(ListGridFieldType.DATE);
		startPublishing.setCellFormatter(new DateCellFormatter(false));
		startPublishing.setCanFilter(false);
		startPublishing.setHidden(true);

		ListGridField stopPublishing = new ListGridField("stopPublishing", I18N.message("stoppublishing"), 110);
		stopPublishing.setAlign(Alignment.CENTER);
		stopPublishing.setType(ListGridFieldType.DATE);
		stopPublishing.setCellFormatter(new DateCellFormatter(false));
		stopPublishing.setCanFilter(false);
		stopPublishing.setHidden(true);

		ListGridField publishedStatus = new ListGridField("publishedStatus", I18N.message("published"), 50);
		publishedStatus.setHidden(true);
		publishedStatus.setCanFilter(true);

		ListGridField template = new ListGridField("template", I18N.message("template"), 150);
		template.setAlign(Alignment.LEFT);
		template.setHidden(true);
		template.setCanFilter(true);

		ListGridField thumbnail = new ListGridField("thumbnail", I18N.message("thumbnail"), 200);
		thumbnail.setHidden(true);
		thumbnail.setCanFilter(false);
		thumbnail.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				try {
					return Util.thumbnailImgageHTML(Session.get().getSid(), Long.parseLong(record.getAttribute("id")),
							null, 200, null);
				} catch (Throwable e) {
					return "";
				}
			}
		});

		List<ListGridField> fields = new ArrayList<ListGridField>();

		if (ds != null) {
			/*
			 * We are browsing
			 */
			setSelectionType(SelectionStyle.MULTIPLE);

			setDataSource(ds);

			fields.add(id);
			fields.add(indexed);
			fields.add(locked);
			fields.add(immutable);
			fields.add(signed);
			fields.add(stamped);
			fields.add(icon);
			fields.add(filename);
			fields.add(title);
			fields.add(lastModified);
			fields.add(type);
			fields.add(size);
			fields.add(fileVersion);
			fields.add(version);
			fields.add(publisher);
			fields.add(published);
			fields.add(creator);
			fields.add(created);
			fields.add(source);
			fields.add(sourceId);
			fields.add(sourceDate);
			fields.add(sourceAuthor);
			fields.add(customId);
			fields.add(recipient);
			fields.add(object);
			fields.add(coverage);
			fields.add(rating);
			fields.add(comment);
			fields.add(wfStatus);
			fields.add(startPublishing);
			fields.add(stopPublishing);
			fields.add(template);
			fields.add(thumbnail);
		} else {
			/*
			 * We are searching
			 */
			setShowRecordComponents(true);
			setShowRecordComponentsByCell(true);
			setSelectionType(SelectionStyle.SINGLE);
			setShowRowNumbers(true);

			ListGridField folder = new ListGridField("folder", I18N.message("folder"), 200);
			folder.setWidth(200);

			ListGridField score = new ListGridField("score", I18N.message("score"), 120);
			score.setCanFilter(false);
			score.setCellFormatter(new CellFormatter() {
				@Override
				public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
					try {
						int score = record.getAttributeAsInt("score");
						int red = 100 - score > 0 ? 100 - score : 0;
						return "<img src='" + Util.imageUrl("dotblue.gif") + "' style='width: " + score
								+ "px; height: 8px' title='" + score + "%'/>" + "<img src='"
								+ Util.imageUrl("dotgrey.gif") + "' style='width: " + red + "px; height: 8px' title='"
								+ score + "%'/>";
					} catch (Throwable e) {
						return "";
					}
				}
			});

			type.setHidden(true);
			customId.setHidden(true);

			fields.add(id);
			fields.add(indexed);
			fields.add(locked);
			fields.add(immutable);
			fields.add(signed);
			fields.add(stamped);
			fields.add(icon);
			fields.add(filename);
			fields.add(title);
			fields.add(score);
			fields.add(lastModified);
			fields.add(type);
			fields.add(size);
			fields.add(fileVersion);
			fields.add(version);
			fields.add(publisher);
			fields.add(published);
			fields.add(creator);
			fields.add(created);
			fields.add(source);
			fields.add(sourceId);
			fields.add(sourceDate);
			fields.add(sourceAuthor);
			fields.add(customId);
			fields.add(recipient);
			fields.add(object);
			fields.add(coverage);
			fields.add(folder);
			fields.add(rating);
			fields.add(comment);
			fields.add(wfStatus);
			fields.add(startPublishing);
			fields.add(stopPublishing);
			fields.add(template);
			fields.add(thumbnail);
		}

		String[] extNames = Session.get().getInfo().getConfig("search.extattr").split(",");
		for (String name : extNames) {
			if (name != null && !"".equals(name)) {
				ListGridField ext = new ListGridField("ext_" + name, name, 100);
				ext.setHidden(true);
				ext.setCanFilter(true);
				fields.add(ext);
			}
		}

		setFields(fields.toArray(new ListGridField[0]));

		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				ListGridRecord record = event.getRecord();
				if ("indexed".equals(getFieldName(event.getColNum()))) {
					if ("indexed".equals(record.getAttribute("indexed"))) {
						String id = getSelectedRecord().getAttribute("id");
						if (Session.get().getCurrentFolder().isDownload())
							try {
								WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
										+ "&docId=" + id + "&downloadText=true");
							} catch (Throwable t) {

							}
					}
					event.cancel();
				} else if ("signed".equals(getFieldName(event.getColNum()))) {
					if (Feature.enabled(Feature.DIGITAL_SIGN)) {
						if ("rosette".equals(record.getAttribute("signed"))) {
							final String id = getSelectedRecord().getAttribute("id");
							final String fileName = getSelectedRecord().getAttribute("filename") + ".p7m";
							String fileVersion = getSelectedRecord().getAttribute("fileVersion");

							ContactingServer.get().show();
							signService.extractSubjectSignatures(Session.get().getSid(), Long.parseLong(id),
									fileVersion, new AsyncCallback<String[]>() {

										@Override
										public void onFailure(Throwable caught) {
											ContactingServer.get().hide();
											Log.serverError(caught);
											destroy();
										}

										@Override
										public void onSuccess(String[] result) {
											ContactingServer.get().hide();
											SignatureViewer viewer = new SignatureViewer(id, fileName, result);
											viewer.show();
											if (result == null || result.length < 1)
												SC.warn(I18N.message("verificationfailed"));
										}
									});
						}
					}
					event.cancel();
				} else if ("stamped".equals(getFieldName(event.getColNum()))) {
					if (Feature.enabled(Feature.DIGITAL_SIGN)) {
						if ("stamp".equals(record.getAttribute("stamped"))) {
							final String id = getSelectedRecord().getAttribute("id");
							String fileVersion = getSelectedRecord().getAttribute("fileVersion");

							if (Session.get().getCurrentFolder().isDownload())
								WindowUtils.openUrl(GWT.getHostPageBaseURL() + "convertpdf?sid=" + Session.get().getSid()
										+ "&docId=" + id + "&version=" + fileVersion);
						}
					}
					event.cancel();
				} else if ("rating".equals(getFieldName(event.getColNum()))) {
					long id = Long.parseLong(getSelectedRecord().getAttribute("id"));
					String ratingImageName = getSelectedRecord().getAttribute("rating");
					final int docRating = Integer.parseInt(ratingImageName.replace("rating", ""));
					documentService.getRating(Session.get().getSid(), id, new AsyncCallback<GUIRating>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIRating rating) {
							if (rating != null) {
								RatingDialog dialog = new RatingDialog(docRating, rating, null);
								dialog.show();
							}
						}
					});
					event.cancel();
				}
			}
		});

		/*
		 * Save the layout of the grid at every change
		 */
		addViewStateChangedHandler(new ViewStateChangedHandler() {
			@Override
			public void onViewStateChanged(ViewStateChangedEvent event) {
				if (ds != null)
					Offline.put(Constants.COOKIE_DOCSLIST, getViewState());
				else
					Offline.put(Constants.COOKIE_HITSLIST, getViewState());
			}
		});

		/*
		 * Restore any previously saved view state for this grid
		 */
		addDrawHandler(new DrawHandler() {
			@Override
			public void onDraw(DrawEvent event) {
				String previouslySavedState = null;
				if (ds != null)
					previouslySavedState = (String) Offline.get(Constants.COOKIE_DOCSLIST);
				else
					previouslySavedState = (String) Offline.get(Constants.COOKIE_HITSLIST);
				if (previouslySavedState != null)
					setViewState(previouslySavedState);
			}
		});

		addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				if (cursor != null) {
					cursor.setMessage(I18N.message("showndocuments", Integer.toString(getCount())));
					cursor.setTotalRecords(totalRecords);
				}
			}
		});
	}

	@Override
	public void deselectAll() {
		deselectAllRecords();
	}

	@Override
	protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
		if (getFieldName(colNum).equals("title")) {
			if ("stop".equals(record.getAttribute("immutable"))
					|| !"yes".equals(record.getAttribute("publishedStatus"))) {
				return "color: #888888; font-style: italic;";
			} else {
				return super.getCellCSSText(record, rowNum, colNum);
			}
		} else {
			return super.getCellCSSText(record, rowNum, colNum);
		}
	}

	@Override
	public void updateDocument(GUIDocument document) {
		Record record = null;

		// Find the record the corresponds to the given document
		Record[] records = getRecords();
		for (Record rec : records)
			if (Long.parseLong(rec.getAttribute("id")) == document.getId())
				record = rec;

		if (record != null) {
			GridUtil.updateRecord(document, record);
			refreshRow(getRecordIndex(record));
		}
	}

	@Override
	public GUIDocument markSelectedAsCheckedOut() {
		ListGridRecord record = getSelectedRecord();
		if (record == null)
			return null;
		record.setAttribute("locked", "page_edit");
		record.setAttribute("lockUserId", Session.get().getUser().getId());
		record.setAttribute("status", Constants.DOC_CHECKED_OUT);
		refreshRow(getRecordIndex(record));
		return getSelectedDocument();
	}

	@Override
	public GUIDocument markSelectedAsCheckedIn() {
		ListGridRecord record = getSelectedRecord();
		if (record == null)
			return null;
		record.setAttribute("locked", "blank");
		record.setAttribute("status", Constants.DOC_UNLOCKED);
		record.setAttribute("indexed", Constants.INDEX_TO_INDEX);
		record.setAttribute("signed", "blank");
		record.setAttribute("stamped", "blank");
		record.setAttribute("extResId", (String) null);
		refreshRow(getRecordIndex(record));
		return getSelectedDocument();
	}

	@Override
	public GUIDocument getSelectedDocument() {
		return GridUtil.toDocument(getSelectedRecord());
	}

	@Override
	public GUIDocument[] getSelectedDocuments() {
		return GridUtil.toDocuments(getSelectedRecords());
	}

	@Override
	public void setCanDrag(boolean drag) {
		super.setCanDrag(drag);
		setCanDragRecordsOut(drag);
	}

	@Override
	public int getCount() {
		return getTotalRows();
	}

	@Override
	public void showFilters(boolean showFilters) {
		setShowFilterEditor(showFilters);
	}

	@Override
	public void registerDoubleClickHandler(DoubleClickHandler handler) {
		addDoubleClickHandler(handler);
	}

	@Override
	public void registerSelectionChangedHandler(SelectionChangedHandler handler) {
		addSelectionChangedHandler(handler);
	}

	@Override
	public void registerCellContextClickHandler(CellContextClickHandler handler) {
		addCellContextClickHandler(handler);
	}

	@Override
	public void registerDataArrivedHandler(DataArrivedHandler handler) {
		addDataArrivedHandler(handler);
	}

	@Override
	public void selectDocument(long docId) {
		deselectAll();
		RecordList rlist = getDataAsRecordList();
		Record record = rlist.find("id", Long.toString(docId));
		if (record != null)
			selectSingleRecord(record);
	}

	@Override
	public int getSelectedCount() {
		ListGridRecord[] selection = getSelectedRecords();
		if (selection != null && selection.length >= 1)
			return selection.length;
		else
			return 0;
	}

	@Override
	public void updateRating(int rating) {
		ListGridRecord selectedRecord = getSelectedRecord();
		selectedRecord.setAttribute("rating", "rating" + rating);
		refreshRow(getRecordIndex(selectedRecord));
	}

	@Override
	public void updateExtResId(String extResId) {
		getSelectedRecord().setAttribute("extResId", extResId);
	}

	@Override
	public long[] getSelectedIds() {
		return GridUtil.getIds(getSelectedRecords());
	}

	@Override
	public void expandVisibleRows() {
		Integer[] rows = getVisibleRows();
		if (rows[0] == -1 || rows[1] == -1)
			return;
		for (int i = rows[0]; i < rows[1]; i++)
			expandRecord(getRecord(i));
	}

	@Override
	public void setCanExpandRows() {
		setCanExpandRecords(true);
		setExpansionMode(ExpansionMode.DETAIL_FIELD);
		setDetailField("summary");

	}

	@Override
	protected Canvas getExpansionComponent(final ListGridRecord record) {
		return new HTMLFlow("<div class='details'>"
				+ (record.getAttributeAsString("summary") != null ? record.getAttributeAsString("summary") : "")
				+ "</div>");
	}

	@Override
	public void setDocuments(GUIDocument[] documents) {
		ListGridRecord[] records = new ListGridRecord[0];
		if (documents == null || documents.length == 0)
			setRecords(records);

		records = new ListGridRecord[documents.length];
		for (int i = 0; i < documents.length; i++) {
			GUIDocument doc = documents[i];
			ListGridRecord record = GridUtil.fromDocument(doc);
			records[i] = record;
		}

		setRecords(records);
	}

	@Override
	public long[] getIds() {
		ListGridRecord[] records = getRecords();
		if (records == null || records.length == 0)
			return new long[0];

		long[] ids = new long[records.length];
		for (int j = 0; j < ids.length; j++)
			ids[j] = Long.parseLong(records[j].getAttributeAsString("id"));

		return ids;
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
