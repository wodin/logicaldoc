package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.util.DateCellFormatter;
import com.logicaldoc.gui.common.client.util.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.Main;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.document.DocumentObserver;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.EmailWindow;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows a list of search results in a tabular way.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HitsListPanel extends VLayout implements SearchObserver, DocumentObserver {

	private ListGrid list;

	private ToolStrip toolStrip;

	public HitsListPanel() {
		initialize();
		Search.get().addObserver(this);
	}

	private void initialize() {
		if (list != null) {
			list.clear();
		}

		if (toolStrip != null) {
			toolStrip.clear();
		}

		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.getMessage("title"), 300);

		ListGridField size = new ListGridField("size", I18N.getMessage("size"), 90);
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

		ListGridField folderId = new ListGridField("folderId", I18N.getMessage("folder"), 200);
		folderId.setHidden(true);

		ListGridField lockUserId = new ListGridField("lockUserId", " ", 24);
		lockUserId.setHidden(true);

		ListGridField score = new ListGridField("score", I18N.getMessage("score"), 120);
		score.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				int score = record.getAttributeAsInt("score");
				int red = 100 - score > 0 ? 100 - score : 0;
				return "<img src='" + Util.imageUrl("application/dotblue.gif") + "' style='width: " + score
						+ "px; height: 8px' title='" + score + "%'/>" + "<img src='"
						+ Util.imageUrl("application/dotgrey.gif") + "' style='width: " + red
						+ "px; height: 8px' title='" + score + "%'/>";
			}
		});

		ListGridField summary = new ListGridField("summary", I18N.getMessage("summary"));
		summary.setWidth(300);

		if (list == null) {
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

			list.setCanExpandRecords(true);
			list.setExpansionMode(ExpansionMode.DETAIL_FIELD);
			list.setDetailField("summary");
			list.setShowRecordComponents(true);
			list.setShowRecordComponentsByCell(true);
			list.setCanFreezeFields(true);
			list.setSelectionType(SelectionStyle.SINGLE);
			list.setShowRowNumbers(true);
			list.setWrapCells(true);
			list.setFields(id, folderId, score, icon, title, customId, size);

			list.addSelectionChangedHandler(new SelectionChangedHandler() {
				@Override
				public void onSelectionChanged(SelectionEvent event) {
					SearchPanel.get().onSelectedHit(Long.parseLong(event.getRecord().getAttribute("id")));
				}
			});
		}

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = setupContextMenu();
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

		/*
		 * Prepare the toolbar displaying search statistics
		 */
		setupToolbar();
		addMember(list);

		ListGridRecord[] result = Search.get().getLastResult();
		list.setRecords(result);
	}

	/**
	 * Prepares the toolbar containing the search report and a set of buttons
	 */
	private void setupToolbar() {
		if (toolStrip == null)
			toolStrip = new ToolStrip();
		else {
			toolStrip.removeMembers(toolStrip.getMembers());
		}
		if (Search.get().isEmpty())
			toolStrip.setVisible(false);
		else
			toolStrip.setVisible(true);

		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton showSnippets = new ToolStripButton();
		showSnippets.setTitle(I18N.getMessage("showsnippets"));
		toolStrip.addButton(showSnippets);
		showSnippets.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				expandVisibleRows();
			}
		});

		if (Search.get().isHasMore()) {
			toolStrip.addSeparator();
			final IntegerItem repeatNumber = new IntegerItem();
			repeatNumber.setName("repeatNumber");
			repeatNumber.setHint("hits");
			repeatNumber.setShowTitle(false);
			repeatNumber.setDefaultValue(40);
			repeatNumber.setWidth(40);

			ToolStripButton repeat = new ToolStripButton();
			repeat.setTitle(I18N.getMessage("repeatsearchinlcuding"));
			toolStrip.addButton(repeat);
			toolStrip.addFormItem(repeatNumber);
			repeat.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GUISearchOptions opt = Search.get().getOptions();
					opt.setMaxHits(opt.getMaxHits() + (Integer) repeatNumber.getValue());
					Search.get().search();
				}
			});
		}
		toolStrip.addFill();
		GUISearchOptions options = Search.get().getOptions();
		NumberFormat format = NumberFormat.getFormat("#.###");
		Label resultLabel = new Label(I18N.getMessage("resultstat", new String[] { options.getExpression(),
				format.format((double) Search.get().getTime() / (double) 1000) }));
		resultLabel.setWrap(false);
		resultLabel.setAlign(Alignment.RIGHT);
		resultLabel.setMargin(5);
		toolStrip.addMember(resultLabel);
		addMember(toolStrip);
	}

	protected void expandVisibleRows() {
		Integer[] rows = list.getVisibleRows();
		if (rows[0] == -1 || rows[1] == -1)
			return;
		for (int i = rows[0]; i < rows[1]; i++) {
			list.expandRecord(list.getRecord(i));
		}
	}

	@Override
	public void onSearchArrived() {
		initialize();
		Main.get().getMainPanel().selectSearchTab();
		if (Search.get().isHasMore()) {
			Log.info(I18N.getMessage("possiblemorehits"), I18N.getMessage("possiblemorehitsdetail"));
		}
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateSelectedRecord(GUIDocument document) {
		ListGridRecord selectedRecord = list.getSelectedRecord();
		if (selectedRecord != null) {
			selectedRecord.setAttribute("title", document.getTitle());
			selectedRecord.setAttribute("customId", document.getCustomId());
			selectedRecord.setAttribute("size", document.getSize());
			list.refreshRow(list.getRecordIndex(selectedRecord));
		}
	}

	@Override
	public void onDocumentSaved(GUIDocument document) {
		updateSelectedRecord(document);
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu() {
		Menu contextMenu = new Menu();
		MenuItem openInFolder = new MenuItem();
		openInFolder.setTitle(I18N.getMessage("openinfolder"));
		openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")));
			}
		});

		MenuItem copyItem = new MenuItem();
		copyItem.setTitle(I18N.getMessage("copy"));
		copyItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelection();
				if (selection == null)
					return;
				for (int i = 0; i < selection.length; i++) {
					String id = selection[i].getAttribute("id");
					GUIDocument document = new GUIDocument();
					document.setId(Long.parseLong(id));
					document.setTitle(selection[i].getAttribute("title"));
					document.setIcon(selection[i].getAttribute("icon"));
					Clipboard.getInstance().add(document);
				}
			}
		});

		MenuItem sendMailItem = new MenuItem();
		sendMailItem.setTitle(I18N.getMessage("sendmail"));
		sendMailItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = list.getSelectedRecord();
				if (selection == null)
					return;
				EmailWindow window = new EmailWindow(Long.parseLong(selection.getAttribute("id")), selection
						.getAttribute("title"));
				window.show();
			}
		});

		contextMenu.setItems(copyItem, sendMailItem, openInFolder);

		return contextMenu;
	}

	//
	// MenuItem similarItem = new MenuItem();
	// similarItem.setTitle(I18N.getMessage("similardocuments"));
	// similarItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// ListGridRecord selection = list.getSelectedRecord();
	// if (selection == null)
	// return;
	// TODO implement
	// SC.warn("To be Implemented");
	// }
	// });
	//
	// MenuItem linksItem = new MenuItem();
	// linksItem.setTitle(I18N.getMessage("connectaslinks"));
	// linksItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// ListGridRecord[] selection = list.getSelection();
	// if (selection == null || selection.length == 0 ||
	// Clipboard.getInstance().isEmpty())
	// return;
	//
	// final long[] outIds = new long[selection.length];
	// for (int j = 0; j < selection.length; j++) {
	// outIds[j] = Long.parseLong(selection[j].getAttribute("id"));
	// }
	//
	// final long[] inIds = new long[Clipboard.getInstance().size()];
	// int i = 0;
	// for (GUIDocument doc : Clipboard.getInstance()) {
	// inIds[i++] = doc.getId();
	// }
	//
	// documentService.linkDocuments(Session.getInstance().getSid(), inIds,
	// outIds, new AsyncCallback<Void>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// Log.serverError(caught);
	// }
	//
	// @Override
	// public void onSuccess(Void result) {
	// // Nothing to do
	// }
	//
	// });
	// }
	// });
	//
	// MenuItem immutableItem = new MenuItem();
	// immutableItem.setTitle(I18N.getMessage("makeimmutable"));
	// immutableItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// final ListGridRecord[] selection = list.getSelection();
	// if (selection == null)
	// return;
	// final long[] ids = new long[selection.length];
	// for (int j = 0; j < selection.length; j++) {
	// ids[j] = Long.parseLong(selection[j].getAttribute("id"));
	// }
	//
	// Dialog dialogProperties = new Dialog();
	// SC.askforValue(I18N.getMessage("warning"),
	// I18N.getMessage("immutableadvice"), "", new ValueCallback() {
	//
	// @Override
	// public void execute(String value) {
	// if (value == null)
	// return;
	//
	// if (value.isEmpty())
	// SC.warn(I18N.getMessage("commentrequired"));
	// else
	// documentService.makeImmutable(Session.getInstance().getSid(), ids, value,
	// new AsyncCallback<Void>() {
	// @Override
	// public void onFailure(Throwable caught) {
	// Log.serverError(caught);
	// }
	//
	// @Override
	// public void onSuccess(Void result) {
	// for (ListGridRecord record : selection) {
	// record.setAttribute("immutable", "stop");
	// list.refreshRow(list.getRecordIndex(record));
	// }
	// }
	// });
	// }
	//
	// }, dialogProperties);
	// }
	// });
	//
	// MenuItem lockItem = new MenuItem();
	// lockItem.setTitle(I18N.getMessage("lock"));
	// lockItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// final ListGridRecord[] selection = list.getSelection();
	// if (selection == null)
	// return;
	// final long[] ids = new long[selection.length];
	// for (int j = 0; j < selection.length; j++) {
	// ids[j] = Long.parseLong(selection[j].getAttribute("id"));
	// }
	//
	// Dialog dialogProperties = new Dialog();
	// SC.askforValue(I18N.getMessage("warning"), I18N.getMessage("lockadvice"),
	// "", new ValueCallback() {
	//
	// @Override
	// public void execute(String value) {
	// if (value != null)
	// documentService.lock(Session.getInstance().getSid(), ids, value, new
	// AsyncCallback<Void>() {
	// @Override
	// public void onFailure(Throwable caught) {
	// Log.serverError(caught);
	// }
	//
	// @Override
	// public void onSuccess(Void result) {
	// for (ListGridRecord record : selection) {
	// record.setAttribute("locked", "document_lock");
	// record.setAttribute("lockUserId",
	// Session.getInstance().getUser().getId());
	// list.refreshRow(list.getRecordIndex(record));
	// }
	// }
	// });
	// }
	//
	// }, dialogProperties);
	// }
	// });
	//
	// MenuItem unlockItem = new MenuItem();
	// unlockItem.setTitle(I18N.getMessage("unlock"));
	// unlockItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// final ListGridRecord[] selection = list.getSelection();
	// if (selection == null)
	// return;
	// final long[] ids = new long[selection.length];
	// for (int j = 0; j < selection.length; j++) {
	// ids[j] = Long.parseLong(selection[j].getAttribute("id"));
	// }
	//
	// documentService.unlock(Session.getInstance().getSid(), ids, new
	// AsyncCallback<Void>() {
	// @Override
	// public void onFailure(Throwable caught) {
	// Log.serverError(caught);
	// }
	//
	// @Override
	// public void onSuccess(Void result) {
	// for (ListGridRecord record : selection) {
	// record.setAttribute("locked", "blank");
	// record.setAttribute("status", Constants.DOC_UNLOCKED);
	// list.refreshRow(list.getRecordIndex(record));
	// }
	// }
	// });
	// }
	// });
	//
	// if (list.getSelection().length == 1)
	// items.add(downloadItem);
	//
	// items.add(copyItem);
	// if (folder.hasPermission(Constants.PERMISSION_DELETE))
	// items.add(deleteItem);
	//
	// if (list.getSelection().length == 1) {
	// items.add(sendMailItem);
	// items.add(similarItem);
	// }
	//
	// if (folder.hasPermission(Constants.PERMISSION_WRITE)) {
	// items.add(linksItem);
	//
	// boolean enableLock = true;
	// boolean enableUnlock = true;
	//
	// ListGridRecord[] selection = list.getSelection();
	// for (ListGridRecord record : selection) {
	// if (!record.getAttribute("locked").equals("blank") ||
	// !record.getAttribute("immutable").equals("blank")) {
	// enableLock = false;
	// }
	// if (record.getAttribute("locked").equals("blank") ||
	// !record.getAttribute("immutable").equals("blank")) {
	// Long lockUser = record.getAttribute("lockUserId") != null ?
	// Long.parseLong(record
	// .getAttribute("lockUserId")) : Long.MIN_VALUE;
	// if (Session.getInstance().getUser().getId() == lockUser.longValue()
	// || Session.getInstance().getUser().getUserName().equals("admin"))
	// enableUnlock = false;
	// }
	// }
	//
	// MenuItem checkoutItem = new MenuItem();
	// checkoutItem.setTitle(I18N.getMessage("checkout"));
	// checkoutItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// final ListGridRecord selection = list.getSelectedRecord();
	// if (selection == null)
	// return;
	// final long id = Long.parseLong(selection.getAttribute("id"));
	// documentService.checkout(Session.getInstance().getSid(), id, new
	// AsyncCallback<Void>() {
	// @Override
	// public void onFailure(Throwable caught) {
	// Log.serverError(caught);
	// }
	//
	// @Override
	// public void onSuccess(Void result) {
	// selection.setAttribute("locked", "document_lock");
	// selection.setAttribute("lockUserId",
	// Session.getInstance().getUser().getId());
	// selection.setAttribute("status", Constants.DOC_CHECKED_OUT);
	// list.refreshRow(list.getRecordIndex(selection));
	// Window.open("../download?sid=" + Session.getInstance().getSid() +
	// "&docId=" + id, "_blank",
	// "");
	// }
	// });
	// }
	// });
	//
	// MenuItem checkinItem = new MenuItem();
	// checkinItem.setTitle(I18N.getMessage("checkin"));
	// checkinItem.addClickHandler(new
	// com.smartgwt.client.widgets.menu.events.ClickHandler() {
	// public void onClick(MenuItemClickEvent event) {
	// ListGridRecord selection = list.getSelectedRecord();
	// if (selection == null)
	// return;
	// long id = Long.parseLong(selection.getAttribute("id"));
	// String filename = selection.getAttributeAsString("filename");
	// DocumentCheckin checkin = new DocumentCheckin(id, filename,
	// ResultListPanel.this);
	// checkin.show();
	// }
	// });
	//
	// if (enableLock) {
	// items.add(lockItem);
	// items.add(checkoutItem);
	// }
	//
	// if (enableUnlock) {
	// if (selection.length == 1
	// && Constants.DOC_CHECKED_OUT ==
	// Integer.parseInt(selection[0].getAttribute("status")))
	// items.add(checkinItem);
	// items.add(unlockItem);
	// }
	// }
	//
	// if (folder.hasPermission(Constants.PERMISSION_IMMUTABLE)) {
	// boolean enableImmutable = true;
	// ListGridRecord[] selection = list.getSelection();
	// for (ListGridRecord record : selection) {
	// if (!record.getAttribute("locked").equals("blank") ||
	// !record.getAttribute("immutable").equals("blank"))
	// enableImmutable = false;
	// }
	//
	// if (enableImmutable)
	// items.add(immutableItem);
	// }
	//
	// contextMenu.setItems(items.toArray(new MenuItem[0]));
	//
	// return contextMenu;
	// }
}