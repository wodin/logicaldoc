package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.formatters.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.document.DocumentContextMenu;
import com.logicaldoc.gui.frontend.client.document.DocumentObserver;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
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

	private InfoPanel infoPanel;

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

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

		ListGridField id = new ListGridField("id", 60);
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.message("title"), 300);

		ListGridField size = new ListGridField("size", I18N.message("size"), 90);
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

		ListGridField version = new ListGridField("version", I18N.message("version"), 55);
		version.setAlign(Alignment.CENTER);

		ListGridField lastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter());
		lastModified.setCanFilter(false);

		ListGridField publisher = new ListGridField("publisher", I18N.message("publisher"), 90);
		publisher.setAlign(Alignment.CENTER);

		ListGridField published = new ListGridField("published", I18N.message("publishedon"), 110);
		published.setAlign(Alignment.CENTER);
		published.setType(ListGridFieldType.DATE);
		published.setCellFormatter(new DateCellFormatter());
		published.setCanFilter(false);

		ListGridField creator = new ListGridField("creator", I18N.message("creator"), 90);
		creator.setAlign(Alignment.CENTER);
		creator.setCanFilter(false);

		ListGridField created = new ListGridField("created", I18N.message("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter());
		created.setCanFilter(false);

		ListGridField customId = new ListGridField("customId", I18N.message("customid"), 110);

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

		ListGridField filename = new ListGridField("filename", I18N.message("filename"), 200);
		filename.setHidden(true);

		ListGridField folderId = new ListGridField("folderId", I18N.message("folder"), 200);
		folderId.setHidden(true);
		folderId.setCanFilter(false);

		ListGridField lockUserId = new ListGridField("lockUserId", " ", 24);
		lockUserId.setHidden(true);
		lockUserId.setCanFilter(false);

		ListGridField score = new ListGridField("score", I18N.message("score"), 120);
		score.setCanFilter(false);
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

		ListGridField summary = new ListGridField("summary", I18N.message("summary"));
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
			list.setFields(id, folderId, icon, title, customId, size, score);

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
				folderService.getFolder(Session.get().getSid(),
						Long.parseLong(event.getRecord().getAttributeAsString("folderId")), false,
						new AsyncCallback<GUIFolder>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIFolder folder) {
								Menu contextMenu = new DocumentContextMenu(folder, list);
								MenuItem openInFolder = new MenuItem();
								openInFolder.setTitle(I18N.message("openinfolder"));
								openInFolder
										.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
											public void onClick(MenuItemClickEvent event) {
												ListGridRecord record = list.getSelectedRecord();
												DocumentsPanel.get().openInFolder(
														Long.parseLong(record.getAttributeAsString("folderId")),
														Long.parseLong(record.getAttributeAsString("id")));
											}
										});
								contextMenu.addItem(openInFolder);
								contextMenu.showContextMenu();
							}

						});
				event.cancel();
			}
		});

		list.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + id
						+ "&open=true", "_blank", "");
			}
		});

		// Prepare the toolbar with some buttons
		setupToolbar();

		if (infoPanel == null) {
			infoPanel = new InfoPanel(" ");
			addMember(infoPanel);
		}

		if (Search.get().isEmpty())
			infoPanel.setVisible(false);
		else
			infoPanel.setVisible(true);

		// Prepare a stack for 2 sections the Title with search time and the
		// list of hits
		GUISearchOptions options = Search.get().getOptions();
		NumberFormat format = NumberFormat.getFormat("#.###");
		String stats = I18N
				.message(
						"resultstat",
						new String[] { options.getExpression(),
								format.format((double) Search.get().getTime() / (double) 1000) });
		infoPanel.setMessage(stats);

		ListGridRecord[] result = Search.get().getLastResult();
		list.setRecords(result);

		if (!contains(list))
			addMember(list);
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
		showSnippets.setTitle(I18N.message("showsnippets"));
		toolStrip.addButton(showSnippets);
		showSnippets.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				expandVisibleRows();
			}
		});

		toolStrip.addSeparator();
		ToolStripButton save = new ToolStripButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SaveDialog dialog = new SaveDialog();
				dialog.show();
			}
		});

		if (Feature.enabled(Feature.SAVED_SEARCHES)) {
			toolStrip.addButton(save);
		} else if (Feature.showDisabled()) {
			save.setDisabled(true);
			save.setTooltip(I18N.message("featuredisabled"));
			toolStrip.addButton(save);
		}

		if (Search.get().isHasMore()) {
			toolStrip.addSeparator();
			final IntegerItem max = ItemFactory.newValidateIntegerItem("repeatNumber", "", null, 1, null);
			max.setHint(I18N.message("hits"));
			max.setShowTitle(false);
			max.setDefaultValue(40);
			max.setWidth(40);

			ToolStripButton repeat = new ToolStripButton();
			repeat.setTitle(I18N.message("display"));
			toolStrip.addButton(repeat);
			toolStrip.addFormItem(max);
			repeat.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!max.validate())
						return;
					GUISearchOptions opt = Search.get().getOptions();
					opt.setMaxHits(opt.getMaxHits() + (Integer) max.getValue());
					Search.get().search();
				}
			});
		}

		toolStrip.addFill();
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
		MainPanel.get().selectSearchTab();
		if (Search.get().isHasMore()) {
			Log.warn(I18N.message("possiblemorehits"), I18N.message("possiblemorehitsdetail"));
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
			selectedRecord.setAttribute("size", document.getFileSize());
			list.refreshRow(list.getRecordIndex(selectedRecord));
		}
	}

	@Override
	public void onDocumentSaved(GUIDocument document) {
		updateSelectedRecord(document);
	}

	@Override
	public void onOptionsChanged(GUISearchOptions newOptions) {
	}
}