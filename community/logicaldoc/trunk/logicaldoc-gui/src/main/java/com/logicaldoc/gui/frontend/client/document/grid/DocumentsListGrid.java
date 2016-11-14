package com.logicaldoc.gui.frontend.client.document.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.smartgwt.client.types.DateDisplayFormat;
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

	private Map<String, ListGridField> prepareFields() {
		Map<String, ListGridField> map = new HashMap<String, ListGridField>();

		ListGridField id = new ListGridField("id", I18N.getAttributeLabel("id"), 50);
		id.setHidden(true);
		map.put(id.getName(), id);

		ListGridField title = new ListGridField("title", I18N.getAttributeLabel("title"), 200);
		title.setCanFilter(true);
		map.put(title.getName(), title);

		ListGridField size = new ListGridField("size", I18N.getAttributeLabel("size"), 70);
		size.setAlign(Alignment.RIGHT);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());
		size.setCanFilter(false);
		map.put(size.getName(), size);

		ListGridField icon = new ListGridField("icon", " ", 20);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);
		map.put(icon.getName(), icon);

		ListGridField version = new ListGridField("version", I18N.getAttributeLabel("version"), 55);
		version.setAlign(Alignment.CENTER);
		version.setHidden(true);
		version.setCanFilter(true);
		map.put(version.getName(), version);

		ListGridField lastModified = new ListGridField("lastModified", I18N.getAttributeLabel("lastModified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);
		lastModified.setHidden(true);
		map.put(lastModified.getName(), lastModified);

		ListGridField publisher = new ListGridField("publisher", I18N.message("publisher"), 90);
		publisher.setAlign(Alignment.CENTER);
		publisher.setCanFilter(true);
		map.put(publisher.getName(), publisher);

		ListGridField published = new ListGridField("published", I18N.message("published"), 110);
		published.setAlign(Alignment.CENTER);
		published.setType(ListGridFieldType.DATE);
		published.setCellFormatter(new DateCellFormatter(false));
		published.setCanFilter(false);
		map.put(published.getName(), published);

		ListGridField creator = new ListGridField("creator", I18N.message("creator"), 90);
		creator.setAlign(Alignment.CENTER);
		creator.setCanFilter(true);
		creator.setHidden(true);
		map.put(creator.getName(), creator);

		ListGridField created = new ListGridField("created", I18N.message("created"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter(false));
		created.setCanFilter(false);
		created.setHidden(true);
		map.put(created.getName(), created);

		ListGridField customId = new ListGridField("customId", I18N.message("customid"), 110);
		customId.setType(ListGridFieldType.TEXT);
		map.put(customId.getName(), customId);

		ListGridField type = new ListGridField("type", I18N.message("type"), 55);
		type.setType(ListGridFieldType.TEXT);
		type.setAlign(Alignment.CENTER);
		map.put(type.getName(), type);

		ListGridField immutable = new ListGridField("immutable", " ", 20);
		immutable.setType(ListGridFieldType.IMAGE);
		immutable.setCanSort(false);
		immutable.setAlign(Alignment.CENTER);
		immutable.setShowDefaultContextMenu(false);
		immutable.setImageURLPrefix(Util.imagePrefix());
		immutable.setImageURLSuffix(".png");
		immutable.setCanFilter(false);
		map.put(immutable.getName(), immutable);

		ListGridField indexed = new ListGridField("indexed", " ", 20);
		indexed.setType(ListGridFieldType.IMAGE);
		indexed.setCanSort(false);
		indexed.setAlign(Alignment.CENTER);
		indexed.setShowDefaultContextMenu(false);
		indexed.setImageURLPrefix(Util.imagePrefix());
		indexed.setImageURLSuffix(".png");
		indexed.setCanFilter(false);
		map.put(indexed.getName(), indexed);

		ListGridField locked = new ListGridField("locked", " ", 20);
		locked.setType(ListGridFieldType.IMAGE);
		locked.setCanSort(false);
		locked.setAlign(Alignment.CENTER);
		locked.setShowDefaultContextMenu(false);
		locked.setImageURLPrefix(Util.imagePrefix());
		locked.setImageURLSuffix(".png");
		locked.setCanFilter(false);
		locked.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (record.getAttributeAsString("lockUser") == null)
					return Util.imageHTML(record.getAttributeAsString("locked") + ".png");
				else
					return Util.imageHTML(record.getAttributeAsString("locked") + ".png", I18N.message("lockedby")
							+ " " + record.getAttributeAsString("lockUser"), null);
			}
		});
		map.put(locked.getName(), locked);

		ListGridField signed = new ListGridField("signed", " ", 20);
		signed.setType(ListGridFieldType.IMAGE);
		signed.setCanSort(false);
		signed.setAlign(Alignment.CENTER);
		signed.setShowDefaultContextMenu(false);
		signed.setImageURLPrefix(Util.imagePrefix());
		signed.setImageURLSuffix(".png");
		signed.setCanFilter(false);
		map.put(signed.getName(), signed);

		ListGridField stamped = new ListGridField("stamped", " ", 20);
		stamped.setType(ListGridFieldType.IMAGE);
		stamped.setCanSort(false);
		stamped.setAlign(Alignment.CENTER);
		stamped.setShowDefaultContextMenu(false);
		stamped.setImageURLPrefix(Util.imagePrefix());
		stamped.setImageURLSuffix(".png");
		stamped.setCanFilter(false);
		map.put(stamped.getName(), stamped);

		ListGridField filename = new ListGridField("filename", I18N.message("filename"), 200);
		filename.setHidden(true);
		filename.setCanFilter(true);
		map.put(filename.getName(), filename);

		ListGridField lockUserId = new ListGridField("lockUserId", " ", 24);
		lockUserId.setHidden(true);
		lockUserId.setCanFilter(false);
		map.put(lockUserId.getName(), lockUserId);

		ListGridField rating = new ListGridField("rating", I18N.message("rating"), 95);
		rating.setType(ListGridFieldType.IMAGE);
		rating.setCanSort(false);
		rating.setAlign(Alignment.CENTER);
		rating.setImageURLPrefix(Util.imagePrefix());
		rating.setImageURLSuffix(".png");
		rating.setImageWidth(88);
		rating.setCanFilter(false);
		rating.setHidden(true);
		map.put(rating.getName(), rating);

		ListGridField fileVersion = new ListGridField("fileVersion", I18N.message("fileversion"), 70);
		fileVersion.setHidden(false);
		fileVersion.setCanFilter(true);
		fileVersion.setAlign(Alignment.CENTER);
		map.put(fileVersion.getName(), fileVersion);

		ListGridField comment = new ListGridField("comment", I18N.message("comment"), 300);
		comment.setHidden(true);
		comment.setCanFilter(true);
		map.put(comment.getName(), comment);

		ListGridField wfStatus = new ListGridField("workflowStatus", I18N.message("workflowstatus"), 100);
		wfStatus.setHidden(true);
		wfStatus.setCanFilter(true);
		wfStatus.setAlign(Alignment.LEFT);
		map.put(wfStatus.getName(), wfStatus);

		ListGridField startPublishing = new ListGridField("startPublishing", I18N.message("startpublishing"), 110);
		startPublishing.setAlign(Alignment.CENTER);
		startPublishing.setType(ListGridFieldType.DATE);
		startPublishing.setCellFormatter(new DateCellFormatter(false));
		startPublishing.setCanFilter(false);
		startPublishing.setHidden(true);
		map.put(startPublishing.getName(), startPublishing);

		ListGridField stopPublishing = new ListGridField("stopPublishing", I18N.message("stoppublishing"), 110);
		stopPublishing.setAlign(Alignment.CENTER);
		stopPublishing.setType(ListGridFieldType.DATE);
		stopPublishing.setCellFormatter(new DateCellFormatter(false));
		stopPublishing.setCanFilter(false);
		stopPublishing.setHidden(true);
		map.put(stopPublishing.getName(), stopPublishing);

		ListGridField publishedStatus = new ListGridField("publishedStatus", I18N.message("published"), 50);
		publishedStatus.setHidden(true);
		publishedStatus.setCanFilter(true);
		map.put(publishedStatus.getName(), publishedStatus);

		ListGridField template = new ListGridField("template", I18N.message("template"), 150);
		template.setAlign(Alignment.LEFT);
		template.setHidden(true);
		template.setCanFilter(true);
		map.put(template.getName(), template);

		ListGridField thumbnail = new ListGridField("thumbnail", I18N.message("thumbnail"), 200);
		thumbnail.setHidden(true);
		thumbnail.setCanFilter(false);
		thumbnail.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				try {
					return Util.thumbnailImgageHTML(Long.parseLong(record.getAttribute("id")), null, 200, null);
				} catch (Throwable e) {
					return "";
				}
			}
		});
		map.put(thumbnail.getName(), thumbnail);

		// For search only
		ListGridField folder = new ListGridField("folder", I18N.message("folder"), 200);
		folder.setWidth(200);
		folder.setHidden(true);
		map.put(folder.getName(), folder);

		// For search only
		ListGridField score = new ListGridField("score", I18N.message("score"), 120);
		score.setCanFilter(false);
		score.setHidden(true);
		score.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				try {
					int score = record.getAttributeAsInt("score");
					int red = 100 - score > 0 ? 100 - score : 0;
					return "<img src='" + Util.imageUrl("dotblue.gif") + "' style='width: " + score
							+ "px; height: 8px' title='" + score + "%'/>" + "<img src='" + Util.imageUrl("dotgrey.gif")
							+ "' style='width: " + red + "px; height: 8px' title='" + score + "%'/>";
				} catch (Throwable e) {
					return "";
				}
			}
		});
		map.put(score.getName(), score);

		String[] extNames = Session.get().getInfo().getConfig("search.extattr").split(",");
		for (String name : extNames) {
			if (name != null && !"".equals(name)) {
				ListGridField ext = new ListGridField("ext_" + name, name, 100);
				ext.setHidden(true);
				ext.setCanFilter(true);
				map.put(ext.getName(), ext);
			}
		}

		return map;
	}

	public DocumentsListGrid(final DataSource ds, final int totalRecords) {
		setEmptyMessage(I18N.message("notitemstoshow"));
		setCanFreezeFields(true);
		setAutoFetchData(true);
		setFilterOnKeypress(true);

		Map<String, ListGridField> map = prepareFields();

		List<ListGridField> fields = new ArrayList<ListGridField>();

		if (ds != null) {
			/*
			 * We are browsing
			 */
			setSelectionType(SelectionStyle.MULTIPLE);

			setDataSource(ds);

			fields.add(map.get("id"));
			fields.add(map.get("thumbnail"));
			fields.add(map.get("indexed"));
			fields.add(map.get("locked"));
			fields.add(map.get("immutable"));
			fields.add(map.get("signed"));
			fields.add(map.get("stamped"));
			fields.add(map.get("icon"));

			String[] cols = Session.get().getInfo().getConfig("gui.document.columns").split(",");
			for (String col : cols) {
				ListGridField field = map.get(col);
				if (field != null) {
					field.setHidden(false);
					fields.add(field);
				}
			}

			if (!fields.contains(map.get("filename"))) {
				map.get("filename").setHidden(true);
				fields.add(map.get("filename"));
			}
			if (!fields.contains(map.get("title"))) {
				map.get("title").setHidden(true);
				fields.add(map.get("title"));
			}
			if (!fields.contains(map.get("lastModified"))) {
				map.get("lastModified").setHidden(true);
				fields.add(map.get("lastModified"));
			}
			if (!fields.contains(map.get("type"))) {
				map.get("type").setHidden(true);
				fields.add(map.get("type"));
			}
			if (!fields.contains(map.get("size"))) {
				map.get("size").setHidden(true);
				fields.add(map.get("size"));
			}
			if (!fields.contains(map.get("fileVersion"))) {
				map.get("fileVersion").setHidden(true);
				fields.add(map.get("fileVersion"));
			}
			if (!fields.contains(map.get("version"))) {
				map.get("version").setHidden(true);
				fields.add(map.get("version"));
			}
			if (!fields.contains(map.get("publisher"))) {
				map.get("publisher").setHidden(true);
				fields.add(map.get("publisher"));
			}
			if (!fields.contains(map.get("published"))) {
				map.get("published").setHidden(true);
				fields.add(map.get("published"));
			}
			if (!fields.contains(map.get("creator"))) {
				map.get("creator").setHidden(true);
				fields.add(map.get("creator"));
			}
			if (!fields.contains(map.get("created"))) {
				map.get("created").setHidden(true);
				fields.add(map.get("created"));
			}
			if (!fields.contains(map.get("customId"))) {
				map.get("customId").setHidden(true);
				fields.add(map.get("customId"));
			}
			if (!fields.contains(map.get("rating"))) {
				map.get("rating").setHidden(true);
				fields.add(map.get("rating"));
			}
			if (!fields.contains(map.get("comment"))) {
				map.get("comment").setHidden(true);
				fields.add(map.get("comment"));
			}
			if (!fields.contains(map.get("workflowStatus"))) {
				map.get("workflowStatus").setHidden(true);
				fields.add(map.get("workflowStatus"));
			}
			if (!fields.contains(map.get("template"))) {
				map.get("template").setHidden(true);
				fields.add(map.get("template"));
			}
			if (!fields.contains(map.get("startPublishing"))) {
				map.get("startPublishing").setHidden(true);
				fields.add(map.get("startPublishing"));
			}
			if (!fields.contains(map.get("stopPublishing"))) {
				map.get("stopPublishing").setHidden(true);
				fields.add(map.get("stopPublishing"));
			}
		} else {
			/*
			 * We are searching
			 */
			setShowRecordComponents(true);
			setShowRecordComponentsByCell(true);
			setSelectionType(SelectionStyle.SINGLE);
			setShowRowNumbers(true);

			map.get("type").setHidden(true);
			map.get("customId").setHidden(true);

			fields.add(map.get("id"));
			fields.add(map.get("thumbnail"));
			fields.add(map.get("indexed"));
			fields.add(map.get("locked"));
			fields.add(map.get("immutable"));
			fields.add(map.get("signed"));
			fields.add(map.get("stamped"));
			fields.add(map.get("icon"));

			String[] cols = Session.get().getInfo().getConfig("gui.search.columns").split(",");
			for (String col : cols) {
				ListGridField field = map.get(col);
				if (field != null) {
					field.setHidden(false);
					fields.add(field);
				}
			}

			if (!fields.contains(map.get("filename")))
				fields.add(map.get("filename"));
			if (!fields.contains(map.get("title")))
				fields.add(map.get("title"));
			if (!fields.contains(map.get("score")))
				fields.add(map.get("score"));
			if (!fields.contains(map.get("lastModified")))
				fields.add(map.get("lastModified"));
			if (!fields.contains(map.get("type")))
				fields.add(map.get("type"));
			if (!fields.contains(map.get("size")))
				fields.add(map.get("size"));
			if (!fields.contains(map.get("fileVersion")))
				fields.add(map.get("fileVersion"));
			if (!fields.contains(map.get("version")))
				fields.add(map.get("version"));
			if (!fields.contains(map.get("publisher")))
				fields.add(map.get("publisher"));
			if (!fields.contains(map.get("published")))
				fields.add(map.get("published"));
			if (!fields.contains(map.get("creator")))
				fields.add(map.get("creator"));
			if (!fields.contains(map.get("created")))
				fields.add(map.get("created"));
			if (!fields.contains(map.get("customId")))
				fields.add(map.get("customId"));
			if (!fields.contains(map.get("folder")))
				fields.add(map.get("folder"));
			if (!fields.contains(map.get("rating")))
				fields.add(map.get("rating"));
			if (!fields.contains(map.get("rating")))
				fields.add(map.get("rating"));
			if (!fields.contains(map.get("comment")))
				fields.add(map.get("comment"));
			if (!fields.contains(map.get("workflowStatus")))
				fields.add(map.get("workflowStatus"));
			if (!fields.contains(map.get("startPublishing")))
				fields.add(map.get("startPublishing"));
			if (!fields.contains(map.get("stopPublishing")))
				fields.add(map.get("stopPublishing"));
			if (!fields.contains(map.get("template")))
				fields.add(map.get("template"));
		}

		setFields(fields.toArray(new ListGridField[0]));

		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				ListGridRecord record = event.getRecord();
				if ("indexed".equals(getFieldName(event.getColNum()))) {
					if ("indexed".equals(record.getAttribute("indexed"))) {
						Long id = getSelectedRecord().getAttributeAsLong("id");
						if (Session.get().getCurrentFolder().isDownload())
							try {
								WindowUtils.openUrl(Util.downloadURL(id) + "&downloadText=true");
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
							signService.extractSubjectSignatures(Long.parseLong(id), fileVersion,
									new AsyncCallback<String[]>() {

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
								WindowUtils.openUrl(Util.contextPath() + "convertpdf?docId=" + id + "&version="
										+ fileVersion, "_blank");
						}
					}
					event.cancel();
				} else if ("rating".equals(getFieldName(event.getColNum()))) {
					long id = Long.parseLong(getSelectedRecord().getAttribute("id"));
					String ratingImageName = getSelectedRecord().getAttribute("rating");
					final int docRating = Integer.parseInt(ratingImageName.replace("rating", ""));
					documentService.getRating(id, new AsyncCallback<GUIRating>() {
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
		if (getFieldName(colNum).equals("title") || getFieldName(colNum).equals("filename")) {
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
		record.setAttribute("lockUser", Session.get().getUser().getFullName());
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
		record.setAttribute("lockUserId", (String) null);
		record.setAttribute("lockUser", (String) null);
		refreshRow(getRecordIndex(record));
		return getSelectedDocument();
	}

	@Override
	public GUIDocument getSelectedDocument() {
		return GridUtil.toDocument(getSelectedRecord());
	}

	@Override
	public int getSelectedIndex() {
		return super.getRecordIndex(getSelectedRecord());
	}

	@Override
	public GUIDocument[] getSelectedDocuments() {
		return GridUtil.toDocuments(getSelectedRecords());
	}

	@Override
	public GUIDocument[] getDocuments() {
		return GridUtil.toDocuments(getRecords());
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

	@Override
	public DateDisplayFormat getDateFormatter() {
		return I18N.getDateDisplayFormat(false);
	}

	@Override
	public DateDisplayFormat getDatetimeFormatter() {
		return I18N.getDateDisplayFormat(true);
	}
	
	
}