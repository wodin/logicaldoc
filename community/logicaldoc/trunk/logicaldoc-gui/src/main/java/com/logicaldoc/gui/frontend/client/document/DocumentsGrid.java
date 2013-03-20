package com.logicaldoc.gui.frontend.client.document;

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
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SignService;
import com.logicaldoc.gui.frontend.client.services.SignServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;

/**
 * Grid used to show a documents list during navigation or searches.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class DocumentsGrid extends ListGrid {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	public DocumentsGrid(DataSource ds) {
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
		version.setHidden(true);
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

		ListGridField fileVersion = new ListGridField("fileVersion", I18N.message("fileversion"), 70);
		fileVersion.setHidden(false);
		fileVersion.setCanFilter(true);
		fileVersion.setAlign(Alignment.CENTER);

		ListGridField comment = new ListGridField("comment", I18N.message("comment"), 300);
		comment.setHidden(true);
		comment.setCanFilter(true);

		ListGridField wfStatus = new ListGridField("workflowStatus", I18N.message("workflowstatus"), 100);
		wfStatus.setHidden(true);
		wfStatus.setCanFilter(true);

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

		setEmptyMessage(I18N.message("notitemstoshow"));
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setCanFreezeFields(true);
		setAutoFetchData(true);
		setFilterOnKeypress(true);
		setWrapCells(false);

		List<ListGridField> fields = new ArrayList<ListGridField>();

		if (ds != null) {
			/*
			 * We are browsing
			 */
			setSelectionType(SelectionStyle.MULTIPLE);

			setDataSource(ds);

			fields.add(indexed);
			fields.add(locked);
			fields.add(immutable);
			fields.add(signed);
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
			fields.add(sourceDate);
			fields.add(sourceAuthor);
			fields.add(customId);
			fields.add(rating);
			fields.add(comment);
			fields.add(wfStatus);
			fields.add(startPublishing);
			fields.add(stopPublishing);
		} else {
			/*
			 * We are searching
			 */
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

			fields.add(indexed);
			fields.add(locked);
			fields.add(immutable);
			fields.add(signed);
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
			fields.add(sourceDate);
			fields.add(sourceAuthor);
			fields.add(customId);
			fields.add(folder);
			fields.add(rating);
			fields.add(comment);
			fields.add(wfStatus);
			fields.add(startPublishing);
			fields.add(stopPublishing);
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

							signService.extractSubjectSignatures(Session.get().getSid(), Session.get().getUser()
									.getId(), Long.parseLong(id), fileVersion, new AsyncCallback<String[]>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
									destroy();
								}

								@Override
								public void onSuccess(String[] result) {
									SignVerifyDialog verify = new SignVerifyDialog(id, fileName, result);
									verify.show();
									if (result == null || result.length < 1)
										SC.warn(I18N.message("verificationfailed"));
								}
							});
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

	/**
	 * Updates the selected record with new data
	 */
	public void updateSelectedRecord(GUIDocument document) {
		ListGridRecord selectedRecord = getSelectedRecord();
		if (selectedRecord != null) {
			selectedRecord.setAttribute("indexed", "blank");
			selectedRecord.setAttribute("title", document.getTitle());
			selectedRecord.setAttribute("customId", document.getCustomId());
			selectedRecord.setAttribute("version", document.getVersion());
			selectedRecord.setAttribute("fileVersion", document.getFileVersion());
			selectedRecord.setAttribute("size", document.getFileSize());
			selectedRecord.setAttribute("lastModified", document.getLastModified());
			selectedRecord.setAttribute("publisher", document.getPublisher());
			selectedRecord.setAttribute("published", document.getDate());
			selectedRecord.setAttribute("creator", document.getCreator());
			selectedRecord.setAttribute("created", document.getCreation());
			selectedRecord.setAttribute("rating", "rating" + document.getRating());
			selectedRecord.setAttribute("extResId", document.getExtResId());
			refreshRow(getRecordIndex(selectedRecord));
		}
	}

	/**
	 * Renders the selected row as checked-out
	 */
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

	public GUIDocument markSelectedAsCheckedIn() {
		ListGridRecord record = getSelectedRecord();
		if (record == null)
			return null;
		record.setAttribute("locked", "blank");
		record.setAttribute("status", Constants.DOC_UNLOCKED);
		record.setAttribute("indexed", Constants.INDEX_TO_INDEX);
		record.setAttribute("signed", "blank");
		record.setAttribute("extResId", (String) null);
		refreshRow(getRecordIndex(record));
		return getSelectedDocument();
	}
	
	
	/**
	 * Gets a bean representation of the currently selected item (not all
	 * properties are populated).
	 */
	public GUIDocument getSelectedDocument() {
		ListGridRecord record = getSelectedRecord();
		if (record == null)
			return null;

		GUIDocument document = null;
		if (record != null) {
			document = new GUIDocument();
			document.setId(Long.parseLong(record.getAttribute("id")));
			document.setExtResId(record.getAttributeAsString("extResId"));
			document.setTitle(record.getAttribute("title"));
			document.setFileName(record.getAttribute("filename"));
			document.setImmutable("blank".equals(record.getAttributeAsString("immutable")) ? 0 : 1);
			document.setStatus(Integer.parseInt(record.getAttributeAsString("status")));
		}
		return document;
	}
}
