package com.logicaldoc.gui.frontend.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.DocumentObserver;
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
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.grid.ContextMenu;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsListGrid;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsTileGrid;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
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

	private int mode = DocumentsGrid.MODE_LIST;;

	public HitsListPanel() {
		if (Offline.get(Constants.COOKIE_HITSLIST_MODE) != null)
			mode = Integer.parseInt(Offline.get(Constants.COOKIE_HITSLIST_MODE).toString());
		initialize(mode);
		Search.get().addObserver(this);
	}

	protected void initialize(int mode) {
		this.mode = mode;

		if (grid != null)
			removeMember((Canvas) grid);

		if (toolStrip != null)
			toolStrip.clear();

		GUISearchOptions options = Search.get().getOptions();

		ListGridField id = new ListGridField("id", 60);
		id.setHidden(true);

		if (mode == DocumentsGrid.MODE_LIST)
			grid = new DocumentsListGrid(null);
		else if (mode == DocumentsGrid.MODE_GALLERY)
			grid = new DocumentsTileGrid(null);

		if (options.getType() == GUISearchOptions.TYPE_FULLTEXT)
			grid.setCanExpandRows();

		grid.registerSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				onHitSelected();
			}
		});

		grid.registerCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				GUIDocument doc = grid.getSelectedDocument();
				final String type = doc.getType();
				long id = doc.getFolder().getId();

				if (!"folder".equals(type) && Session.get().getCurrentDocument() != null
						&& Session.get().getCurrentDocument().getId() == id) {
					Menu contextMenu = prepareContextMenu(Session.get().getCurrentDocument().getFolder(), true);
					contextMenu.showContextMenu();
				} else {
					/*
					 * We need to retrieve the folder from the server
					 */
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
				}
				event.cancel();
			}
		});

		grid.registerDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (Search.get().getOptions().getType() != GUISearchOptions.TYPE_FOLDERS) {
					final GUIDocument doc = grid.getSelectedDocument();

					folderService.getFolder(Session.get().getSid(), doc.getFolder().getId(), false,
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
												+ "&docId=" + doc.getId() + "&open=true", "_blank", "");
									else {
										String filename = doc.getFileName();
										String version = doc.getVersion();

										if (filename == null)
											filename = doc.getTitle() + "." + doc.getType();
										PreviewPopup iv = new PreviewPopup(doc.getId(), version, filename,
												folder != null && folder.isDownload());
										iv.show();
									}
								}
							});
				}
				event.cancel();
			}
		});

		// Prepare the toolbar with some buttons
		prepareToolbar(options.getType());

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

		GUIDocument[] result = Search.get().getLastResult();
		grid.setDocuments(result);

		addMember((Canvas) grid);
	}

	/**
	 * Prepares the toolbar containing the search report and a set of buttons
	 */
	protected void prepareToolbar(int optionsType) {
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
					Util.exportCSV((ListGrid) grid, false);
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

		final ToolStripButton list = new ToolStripButton();
		list.setTooltip(I18N.message("list"));
		list.setIcon(ItemFactory.newImgIcon("application_view_list.png").getSrc());
		list.setActionType(SelectionType.RADIO);
		list.setRadioGroup("mode");
		list.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Offline.put(Constants.COOKIE_HITSLIST_MODE, DocumentsGrid.MODE_LIST);
				initialize(DocumentsGrid.MODE_LIST);
			}
		});

		final ToolStripButton gallery = new ToolStripButton();
		gallery.setTooltip(I18N.message("gallery"));
		gallery.setIcon(ItemFactory.newImgIcon("application_view_tile.png").getSrc());
		gallery.setActionType(SelectionType.RADIO);
		gallery.setRadioGroup("mode");
		gallery.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Session.get().getCurrentFolder() != null)
					Offline.put(Constants.COOKIE_HITSLIST_MODE, DocumentsGrid.MODE_GALLERY);
				initialize(DocumentsGrid.MODE_GALLERY);
			}
		});

		if (mode == DocumentsGrid.MODE_LIST)
			list.setSelected(true);
		else
			gallery.setSelected(true);

		toolStrip.addSeparator();
		toolStrip.addButton(list);
		toolStrip.addButton(gallery);

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
		grid.expandVisibleRows();
	}

	@Override
	public void onSearchArrived() {
		initialize(mode);
		MainPanel.get().selectSearchTab();
		if (Search.get().isHasMore()) {
			Log.warn(I18N.message("possiblemorehits"), I18N.message("possiblemorehitsdetail"));
		}
	}

	@Override
	public void onDocumentSaved(GUIDocument document) {
		SearchPanel.get().onSelectedDocumentHit(document.getId());
		grid.updateDocument(document);
	}

	@Override
	public void onOptionsChanged(GUISearchOptions newOptions) {
	}

	protected void onHitSelected() {
		if (grid.getSelectedCount() != 1)
			return;

		GUIDocument doc = grid.getSelectedDocument();

		if ("folder".equals(doc.getType()))
			SearchPanel.get().onSelectedFolderHit(doc.getId());
		else
			SearchPanel.get().onSelectedDocumentHit(doc.getId());
	}

	protected Menu prepareContextMenu(GUIFolder folder, final boolean document) {
		Menu contextMenu = new Menu();
		if (document)
			contextMenu = new ContextMenu(folder, grid);
		if (com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.DOCUMENTS)) {
			MenuItem openInFolder = new MenuItem();
			openInFolder.setTitle(I18N.message("openinfolder"));
			openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
				public void onClick(MenuItemClickEvent event) {
					GUIDocument doc = grid.getSelectedDocument();
					DocumentsPanel.get().openInFolder(doc.getFolder().getId(), document ? doc.getId() : null);
				}
			});
			contextMenu.addItem(openInFolder);
		}
		return contextMenu;
	}

	public DocumentsGrid getList() {
		return grid;
	}

	@Override
	public void onFolderSelected(GUIFolder folder) {
		// Nothing to do
	}

	@Override
	public void onFolderSaved(GUIFolder folder) {
		GUIDocument doc = grid.getSelectedDocument();
		if (doc != null) {
			doc.setTitle(folder.getName());
			doc.getFolder().setName(folder.getName());
			doc.getFolder().setDescription(folder.getDescription());
			grid.updateDocument(doc);
		}
	}

	@Override
	public void onDocumentSelected(GUIDocument document) {
		// Nothing to do
	}
}