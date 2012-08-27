package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.document.DocumentContextMenu;
import com.logicaldoc.gui.frontend.client.document.DocumentObserver;
import com.logicaldoc.gui.frontend.client.document.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
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
public class HitsListPanel extends VLayout implements SearchObserver, DocumentObserver, FolderObserver {

	protected DocumentsGrid grid;

	protected ToolStrip toolStrip;

	private InfoPanel infoPanel;

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	public HitsListPanel() {
		initialize();
		Search.get().addObserver(this);
	}

	protected void initialize() {
		if (grid != null) {
			removeMember(grid);
		}

		if (toolStrip != null) {
			toolStrip.clear();
		}

		GUISearchOptions options = Search.get().getOptions();

		ListGridField id = new ListGridField("id", 60);
		id.setHidden(true);

		grid = new DocumentsGrid(null);

		if (options.getType() == GUISearchOptions.TYPE_FULLTEXT) {
			grid.setCanExpandRecords(true);
			grid.setExpansionMode(ExpansionMode.DETAIL_FIELD);
			grid.setDetailField("summary");
		}
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);

		grid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				onHitSelected();
			}
		});

		grid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				final String type = grid.getSelectedRecord().getAttributeAsString("type");
				long id = Long.parseLong(grid.getSelectedRecord().getAttributeAsString("folderId"));
				if ("folder".equals(type)) {
					id = Long.parseLong(grid.getSelectedRecord().getAttributeAsString("id"));
				}

				folderService.getFolder(Session.get().getSid(), id, false, new AsyncCallback<GUIFolder>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIFolder folder) {
						Menu contextMenu = prepareContextMenu(folder, !"folder".equals(type));
						contextMenu.showContextMenu();
					}
				});
				event.cancel();
			}
		});

		grid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (Search.get().getOptions().getType() != GUISearchOptions.TYPE_FOLDERS) {
					final long id = Long.parseLong(grid.getSelectedRecord().getAttribute("id"));

					folderService.getFolder(Session.get().getSid(),
							Long.parseLong(grid.getSelectedRecord().getAttributeAsString("folderId")), false,
							new AsyncCallback<GUIFolder>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIFolder folder) {
									if (folder.isDownload()
											&& "download".equals(Session.get().getInfo().getConfig("gui.doubleclick")))
										Window.open(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
												+ "&docId=" + id + "&open=true", "_blank", "");
									else {
										String filename = grid.getSelectedRecord().getAttribute("filename");
										String version = grid.getSelectedRecord().getAttribute("version");

										if (filename == null)
											filename = grid.getSelectedRecord().getAttribute("title") + "."
													+ grid.getSelectedRecord().getAttribute("type");
										PreviewPopup iv = new PreviewPopup(id, version, filename,folder != null && folder.isDownload());
										iv.show();
									}
								}
							});
				}
				event.cancel();
			}
		});

		final String previouslySavedState = (String) Offline.get(Constants.COOKIE_HITSLIST);
		if (previouslySavedState != null) {
			grid.addDrawHandler(new DrawHandler() {
				@Override
				public void onDraw(DrawEvent event) {
					// restore any previously saved view state for this grid
					grid.setViewState(previouslySavedState);
				}
			});
		}

		// Prepare the toolbar with some buttons
		setupToolbar(options.getType());

		if (infoPanel == null) {
			infoPanel = new InfoPanel(" ");
			addMember(infoPanel);
		}

		infoPanel.setVisible(true);

		// Prepare a stack for 2 sections the Title with search time and the
		// list of hits
		NumberFormat format = NumberFormat.getFormat("#.###");

		String stats = I18N.message(
				"aboutresults",
				new String[] { "" + Search.get().getEstimatedHits(),
						format.format((double) Search.get().getTime() / (double) 1000) });
		stats += " (<b>" + format.format((double) Search.get().getTime() / (double) 1000) + "</b> "
				+ I18N.message("seconds").toLowerCase() + ")";

		infoPanel.setMessage(stats);

		ListGridRecord[] result = Search.get().getLastResult();
		grid.setRecords(result);

		addMember(grid);
	}

	/**
	 * Prepares the toolbar containing the search report and a set of buttons
	 */
	protected void setupToolbar(int optionsType) {
		if (toolStrip == null)
			toolStrip = new ToolStrip();
		else {
			toolStrip.removeMembers(toolStrip.getMembers());
		}

		toolStrip.setVisible(true);

		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton showSnippets = new ToolStripButton();
		showSnippets.setIcon(ItemFactory.newImgIcon("page_white_text.png").getSrc());
		showSnippets.setTooltip(I18N.message("showsnippets"));
		showSnippets.setDisabled(optionsType != GUISearchOptions.TYPE_FULLTEXT);
		toolStrip.addButton(showSnippets);
		showSnippets.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				expandVisibleRows();
			}
		});

		ToolStripButton save = new ToolStripButton();
		save.setIcon(ItemFactory.newImgIcon("disk.png").getSrc());
		save.setTooltip(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SaveDialog dialog = new SaveDialog();
				dialog.show();
			}
		});

		if (Feature.visible(Feature.SAVED_SEARCHES)) {
			toolStrip.addSeparator();
			toolStrip.addButton(save);
			if (!Feature.enabled(Feature.SAVED_SEARCHES)) {
				save.setDisabled(true);
				save.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Search.get().isHasMore()) {
			toolStrip.addSeparator();
			final IntegerItem max = ItemFactory.newValidateIntegerItem("repeatNumber", "", null, 1, null);
			max.setHint(I18N.message("hits"));
			max.setHintStyle("hint");
			max.setShowTitle(false);
			max.setDefaultValue(40);
			max.setWidth(40);

			max.setValue(Search.get().getOptions().getMaxHits());

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
					opt.setMaxHits((Integer) max.getValue());
					Search.get().search();
				}
			});
		}

		ToolStripButton saveGrid = new ToolStripButton();
		saveGrid.setIcon(ItemFactory.newImgIcon("table_save.png").getSrc());
		saveGrid.setTooltip(I18N.message("savegrid"));
		saveGrid.setAutoFit(true);
		saveGrid.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String viewState = grid.getViewState();
				Offline.put(Constants.COOKIE_HITSLIST, viewState);
				Log.info(I18N.message("settingssaved"), null);
			}
		});
		toolStrip.addSeparator();
		toolStrip.addButton(saveGrid);

		ToolStripButton print = new ToolStripButton();
		print.setIcon(ItemFactory.newImgIcon("printer.png").getSrc());
		print.setTooltip(I18N.message("print"));
		print.setAutoFit(true);
		print.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Canvas.printComponents(new Object[] { grid });
			}
		});
		toolStrip.addSeparator();
		toolStrip.addButton(print);

		if (Feature.visible(Feature.EXPORT_CSV)) {
			toolStrip.addSeparator();
			ToolStripButton export = new ToolStripButton();
			export.setIcon(ItemFactory.newImgIcon("table_row_insert.png").getSrc());
			export.setTooltip(I18N.message("export"));
			export.setAutoFit(true);
			toolStrip.addButton(export);
			export.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Util.exportCSV(grid);
				}
			});
			if (!Feature.enabled(Feature.EXPORT_CSV)) {
				export.setDisabled(true);
				export.setTooltip(I18N.message("featuredisabled"));
			}
		}

		final ToolStripButton toggle = new ToolStripButton();
		if (SearchMenu.get().getWidth() > 0) {
			toggle.setIcon(ItemFactory.newImgIcon("application_side_contract.png").getSrc());
			toggle.setTooltip(I18N.message("closeseleftpanel"));
		} else {
			toggle.setIcon(ItemFactory.newImgIcon("application_side_expand.png").getSrc());
			toggle.setTooltip(I18N.message("openleftpanel"));
		}
		toggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SearchPanel.get().toggleMenu();
				if (SearchPanel.get().isMenuOpened()) {
					toggle.setIcon(ItemFactory.newImgIcon("application_side_contract.png").getSrc());
					toggle.setTooltip(I18N.message("closeseleftpanel"));
				} else {
					toggle.setIcon(ItemFactory.newImgIcon("application_side_expand.png").getSrc());
					toggle.setTooltip(I18N.message("openleftpanel"));
				}
			}
		});
		toolStrip.addSeparator();
		toolStrip.addButton(toggle);

		toolStrip.addFill();

		if (Search.get().getSuggestion() != null) {
			ToolStripButton repeat = new ToolStripButton(I18N.message("searchinstaed") + " <b>"
					+ Search.get().getSuggestion() + "</b>");
			repeat.setAutoFit(true);
			repeat.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Search.get().getOptions().setExpression(Search.get().getSuggestion());
					Search.get().search();
				}
			});
			toolStrip.addButton(repeat);
		}

		addMember(toolStrip);
	}

	protected void expandVisibleRows() {
		Integer[] rows = grid.getVisibleRows();
		if (rows[0] == -1 || rows[1] == -1)
			return;
		for (int i = rows[0]; i < rows[1]; i++) {
			grid.expandRecord(grid.getRecord(i));
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

	@Override
	public void onDocumentSaved(GUIDocument document) {
		SearchPanel.get().onSelectedDocumentHit(document.getId());
		grid.updateSelectedRecord(document);
	}

	@Override
	public void onOptionsChanged(GUISearchOptions newOptions) {
	}

	protected void onHitSelected() {
		// Avoid server load in case of multiple selections
		if (grid.getSelectedRecords() != null && grid.getSelectedRecords().length > 1)
			return;

		if (grid.getSelectedRecord() != null) {
			if ("folder".equals(grid.getSelectedRecord().getAttribute("type")))
				SearchPanel.get().onSelectedFolderHit(Long.parseLong(grid.getSelectedRecord().getAttribute("id")));
			else
				SearchPanel.get().onSelectedDocumentHit(Long.parseLong(grid.getSelectedRecord().getAttribute("id")));
		}
	}

	protected Menu prepareContextMenu(GUIFolder folder, final boolean document) {
		Menu contextMenu = new Menu();
		if (document)
			contextMenu = new DocumentContextMenu(folder, grid);
		if (com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.DOCUMENTS)) {
			MenuItem openInFolder = new MenuItem();
			openInFolder.setTitle(I18N.message("openinfolder"));
			openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
				public void onClick(MenuItemClickEvent event) {
					ListGridRecord record = grid.getSelectedRecord();
					DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
							document ? Long.parseLong(record.getAttributeAsString("id")) : null);
				}
			});
			contextMenu.addItem(openInFolder);
		}
		return contextMenu;
	}

	public ListGrid getList() {
		return grid;
	}

	@Override
	public void onFolderSelected(GUIFolder folder) {
		// Nothing to do
	}

	@Override
	public void onFolderSaved(GUIFolder folder) {
		ListGridRecord selectedRecord = grid.getSelectedRecord();
		if (selectedRecord != null) {
			selectedRecord.setAttribute("title", folder.getName());
			selectedRecord.setAttribute("comment", folder.getDescription());
			grid.refreshRow(grid.getRecordIndex(selectedRecord));
		}
	}
}