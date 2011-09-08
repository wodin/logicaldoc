package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.FoldersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.Search;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * The panel that shows the folders navigation tree
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FoldersNavigator extends TreeGrid {
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	private static FoldersNavigator instance = new FoldersNavigator();

	private boolean firstTime = true;

	private FoldersNavigator() {
		setWidth100();
		setBorder("0px");
		setBodyStyleName("normal");
		setShowHeader(false);
		setLeaveScrollbarGap(false);
		setManyItemsImage("cubes_all.png");
		setAppImgDir("pieces/16/");
		setCanReorderRecords(false);
		setCanDragRecordsOut(false);
		setAutoFetchData(true);
		setLoadDataOnDemand(true);
		setDataSource(FoldersDS.get());
		setCanSelectAll(false);
		setShowConnectors(true);

		setCanAcceptDrop(true);
		setCanAcceptDroppedRecords(true);

		addDropHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				try {
					ListGrid list = null;
					if (EventHandler.getDragTarget() instanceof FoldersNavigator) {
						final long source = Long.parseLong(getDragData()[0].getAttributeAsString("folderId"));
						final long target = Long.parseLong(getDropFolder().getAttributeAsString("folderId"));

						service.move(Session.get().getSid(), source, target, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
								Log.warn(I18N.message("operationnotallowed"), null);
							}

							@Override
							public void onSuccess(Void ret) {

							}
						});

					} else if (EventHandler.getDragTarget() instanceof ListGrid) {
						list = (ListGrid) EventHandler.getDragTarget();

						final ListGridRecord[] selection = list.getSelection();
						if (selection == null || selection.length == 0)
							return;
						final long[] ids = new long[selection.length];
						for (int i = 0; i < selection.length; i++) {
							if (selection[i].getAttribute("aliasId") != null)
								ids[i] = Long.parseLong(selection[i].getAttribute("aliasId"));
							else
								ids[i] = Long.parseLong(selection[i].getAttribute("id"));
						}

						final TreeNode selectedNode = getDropFolder();
						final long folderId = Long.parseLong(selectedNode.getAttribute("folderId"));

						if (Session.get().getCurrentFolder().getId() == folderId)
							return;

						service.paste(Session.get().getSid(), ids, folderId, "cut", new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								DocumentsPanel.get().onFolderSelect(Session.get().getCurrentFolder());
								Log.debug("Drag&Drop operation completed.");
							}
						});

					}
				} catch (Throwable e) {
				}
			}
		});

		ListGridField name = new ListGridField("name");
		setFields(name);

		// Handles the right click on folder name to create the context menu
		addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				service.getFolder(Session.get().getSid(),
						Long.parseLong(event.getRecord().getAttributeAsString("folderId")), true,
						new AsyncCallback<GUIFolder>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIFolder folder) {
								Menu contextMenu = setupContextMenu(folder);
								contextMenu.showContextMenu();
							}
						});
				if (event != null)
					event.cancel();
			}
		});

		// Handles the click on a folder to show the contained documents
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				selectFolder(Long.parseLong(event.getRecord().getAttributeAsString("folderId")));

				// Expand the selected node if it is not already expanded
				TreeNode selectedNode = (TreeNode) getSelectedRecord();
				getTree().openFolder(selectedNode);
			}
		});

		// Used to expand root folder after login
		addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				if (isFirstTime()) {
					/*
					 * Redirect the user to the correct folder and/or document
					 */
					RequestInfo loc = WindowUtils.getRequestInfo();
					if (loc.getParameter("folderId") != null) {
						DocumentsPanel.get().openInFolder(Long.parseLong(loc.getParameter("folderId")), null);
					} else if (loc.getParameter("docId") != null) {
						DocumentsPanel.get().openInFolder(Long.parseLong(loc.getParameter("docId")));
					} else {
						getTree().openFolder(getTree().findById("" + Constants.DOCUMENTS_FOLDERID));
					}

					FoldersNavigator.this.firstTime = false;
				}
			}
		});
	}

	/**
	 * Select the specified folder.
	 * 
	 * @param folderId The folder's identifier
	 */
	public void selectFolder(long folderId) {
		service.getFolder(Session.get().getSid(), folderId, false, new AsyncCallback<GUIFolder>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.toString());
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIFolder result) {
				if (result != null) {
					if (result.getId() == Constants.DOCUMENTS_FOLDERID)
						result.setPathExtended("/");
					else
						result.setPathExtended(getPath(result.getId()));
					Session.get().setCurrentFolder(result);
				}
			}
		});
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu(GUIFolder folder) {
		final TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long id = Long.parseLong(selectedNode.getAttribute("folderId"));
		final String name = selectedNode.getAttribute("name");

		GUIFolder parent = folder.getParent();

		Menu contextMenu = new Menu();

		MenuItem search = new MenuItem();
		search.setTitle(I18N.message("search"));
		search.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				Search.get().getOptions().setFolder(id);
				Search.get().getOptions().setFolderName(name);
				Search.get().getOptions().setSearchInSubPath(false);
				Search.get().setOptions(Search.get().getOptions());
				MainPanel.get().selectSearchTab();
			}
		});

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.delete(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									/*
									 * This is needed because of strange
									 * behaviours if we directly delete the
									 * selected node.
									 */
									TreeNode node = getTree().find("folderId", Long.toString(id));
									TreeNode parent = getTree().find("folderId", node.getAttribute("parent"));
									getTree().closeFolder(parent);
									getTree().remove(node);
									getTree().openFolder(parent);

									selectFolder(Long.parseLong(node.getAttributeAsString("parent")));
								}
							});
						}
					}
				});
			}
		});

		MenuItem createItem = new MenuItem();
		createItem.setTitle(I18N.message("newfolder"));
		createItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onCreate();
			}
		});

		MenuItem applyItem = new MenuItem();
		applyItem.setTitle(I18N.message("applytemplate"));
		applyItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onApplyTemplate();
			}
		});

		MenuItem reload = new MenuItem();
		reload.setTitle(I18N.message("reload"));
		reload.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onReload();
			}
		});

		MenuItem move = new MenuItem();
		move.setTitle(I18N.message("move"));
		move.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				MoveDialog dialog = new MoveDialog();
				dialog.show();
			}
		});

		MenuItem pasteItem = new MenuItem();
		pasteItem.setTitle(I18N.message("paste"));
		pasteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onPaste();
			}
		});

		MenuItem pasteAsAliasItem = new MenuItem();
		pasteAsAliasItem.setTitle(I18N.message("pasteasalias"));
		pasteAsAliasItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onPasteAsAlias();
			}
		});

		MenuItem rss = new MenuItem();
		rss.setTitle(I18N.message("rssfeed"));
		if (!Feature.enabled(9)) {
			rss.setEnabled(false);
		}
		rss.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				Window.open(GWT.getHostPageBaseURL() + "folder_rss?sid=" + Session.get().getSid() + "&folderId=" + id
						+ "&locale=" + I18N.getLocale(), "_blank", "");
			}
		});

		MenuItem exportZip = new MenuItem();
		exportZip.setTitle(I18N.message("exportzip"));
		exportZip.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				Window.open(GWT.getHostPageBaseURL() + "zip-export?sid=" + Session.get().getSid() + "&folderId=" + id,
						"_blank", "");
			}
		});

		MenuItem audit = new MenuItem();
		audit.setTitle(I18N.message("subscribe"));
		audit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SubscriptionDialog dialog = new SubscriptionDialog(id);
				dialog.show();
			}
		});

		if (!folder.hasPermission(Constants.PERMISSION_ADD)) {
			createItem.setEnabled(false);
		}

		if (id == Constants.DOCUMENTS_FOLDERID || !parent.hasPermission(Constants.PERMISSION_DELETE)) {
			delete.setEnabled(false);
			move.setEnabled(false);
		}
		if (id == Constants.DOCUMENTS_FOLDERID)
			exportZip.setEnabled(false);

		if (!folder.hasPermission(Constants.PERMISSION_WRITE) || Clipboard.getInstance().isEmpty()) {
			pasteItem.setEnabled(false);
			pasteAsAliasItem.setEnabled(false);
		}

		if (Clipboard.getInstance().getLastAction().equals(Clipboard.CUT))
			pasteAsAliasItem.setEnabled(false);

		contextMenu.setItems(reload, search, createItem, delete, pasteItem, pasteAsAliasItem, move, exportZip);

		if (Feature.visible(Feature.AUDIT)) {
			contextMenu.addItem(audit);
			audit.setEnabled(Feature.enabled(Feature.AUDIT));
		}

		if (Feature.visible(Feature.RSS)) {
			contextMenu.addItem(rss);
			rss.setEnabled(Feature.enabled(Feature.RSS));
		}

		if (Feature.visible(Feature.FOLDER_TEMPLATE)) {
			contextMenu.addItem(applyItem);
			applyItem.setEnabled(Feature.enabled(Feature.FOLDER_TEMPLATE));
		}

		return contextMenu;
	}

	/**
	 * Allows the selection of a folders template to appy to the current node
	 */
	private void onApplyTemplate() {
		LD.askforValue(I18N.message("applytemplate"), I18N.message("ttemplate"), null, "200px",
				ItemFactory.newFolderTemplateSelector(), new ValueCallback() {
					@Override
					public void execute(String value) {
						if (value == null)
							return;

						final TreeNode selectedNode = (TreeNode) getSelectedRecord();
						final long folderId = Long.parseLong(selectedNode.getAttributeAsString("folderId"));
						long templateId = Long.parseLong(value);

						service.applyTemplate(Session.get().getSid(), folderId, templateId, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void arg0) {
								FoldersNavigator.this.getTree().reloadChildren(selectedNode);
								Log.info(I18N.message("templateapplied"), null);
							}
						});
					}
				});
	}

	public static FoldersNavigator get() {
		return instance;
	}

	/**
	 * Opens the tree to show the specified folder.
	 */
	public void openFolder(final long folderId) {
		getTree().closeAll();

		service.getFolder(Session.get().getSid(), folderId, true, new AsyncCallback<GUIFolder>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIFolder folder) {
				TreeNode parent = getTree().getRoot();
				for (GUIFolder fld : folder.getPath()) {
					TreeNode node = new TreeNode(fld.getName());
					node.setAttribute("folderId", Long.toString(fld.getId()));
					node.setAttribute(Constants.PERMISSION_ADD, fld.hasPermission(Constants.PERMISSION_ADD));
					node.setAttribute(Constants.PERMISSION_DELETE, fld.hasPermission(Constants.PERMISSION_DELETE));
					node.setAttribute(Constants.PERMISSION_RENAME, fld.hasPermission(Constants.PERMISSION_RENAME));

					getTree().add(node, parent);
					parent = node;
				}
				TreeNode node = new TreeNode(folder.getName());
				node.setAttribute("folderId", Long.toString(folder.getId()));
				node.setAttribute(Constants.PERMISSION_ADD,
						Boolean.toString(folder.hasPermission(Constants.PERMISSION_ADD)));
				node.setAttribute(Constants.PERMISSION_DELETE,
						Boolean.toString(folder.hasPermission(Constants.PERMISSION_DELETE)));
				node.setAttribute(Constants.PERMISSION_RENAME,
						Boolean.toString(folder.hasPermission(Constants.PERMISSION_RENAME)));
				getTree().add(node, parent);
				parent = node;

				getTree().openFolders(getTree().getParents(parent));
				getTree().openFolder(parent);
				folder.setPathExtended(getPath(folderId));
				Session.get().setCurrentFolder(folder);
			}
		});
	}

	public String getPath(long folderId) {
		TreeNode selectedNode = getTree().find("folderId", Long.toString(folderId));
		String path = "";
		TreeNode[] parents = getTree().getParents(selectedNode);
		for (int i = parents.length - 1; i >= 0; i--) {
			if (parents[i].getName() != null && !"/".equals(parents[i].getName()))
				path += "/" + parents[i].getName();
		}
		path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());
		return path;
	}

	public void onSavedFolder(GUIFolder folder) {
		TreeNode selectedNode = getTree().find("folderId", Long.toString(folder.getId()));
		if (selectedNode != null) {
			selectedNode.setTitle(folder.getName());
			selectedNode.setName(folder.getName());
			getTree().reloadChildren(selectedNode);
		}
	}

	private void onReload() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		getTree().reloadChildren(selectedNode);
	}

	private void onCreate() {
		LD.askforValue(I18N.message("newfolder"), I18N.message("newfoldername"), I18N.message("newfolder"), "200px",
				new ValueCallback() {
					@Override
					public void execute(String value) {
						if (value == null || "".equals(value.trim()))
							return;

						TreeNode selectedNode = (TreeNode) getSelectedRecord();
						final GUIFolder data = new GUIFolder();
						data.setName(value);
						data.setParentId(Long.parseLong(selectedNode.getAttributeAsString("folderId")));
						data.setDescription("");

						service.save(Session.get().getSid(), data, new AsyncCallback<GUIFolder>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIFolder newFolder) {
								TreeNode selectedNode = (TreeNode) getSelectedRecord();
								TreeNode newNode = new TreeNode(newFolder.getName());
								newNode.setAttribute("name", newFolder.getName());
								newNode.setAttribute("folderId", Long.toString(newFolder.getId()));

								if (!getTree().isOpen(selectedNode)) {
									getTree().openFolder(selectedNode);
								}
								getTree().add(newNode, selectedNode);
							}
						});
					}
				});
	}

	private void onPaste() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long folderId = Long.parseLong(selectedNode.getAttribute("folderId"));
		final long[] docIds = new long[Clipboard.getInstance().size()];
		int i = 0;
		for (GUIDocument doc : Clipboard.getInstance()) {
			docIds[i++] = doc.getId();
		}

		service.paste(Session.get().getSid(), docIds, folderId, Clipboard.getInstance().getLastAction(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						DocumentsPanel.get().onFolderSelect(Session.get().getCurrentFolder());

						Clipboard.getInstance().clear();
						Log.debug("Paste operation completed.");
					}
				});
	}

	private void onPasteAsAlias() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long folderId = Long.parseLong(selectedNode.getAttribute("folderId"));
		final long[] docIds = new long[Clipboard.getInstance().size()];
		int i = 0;
		for (GUIDocument doc : Clipboard.getInstance()) {
			docIds[i++] = doc.getId();
		}

		service.pasteAsAlias(Session.get().getSid(), docIds, folderId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				DocumentsPanel.get().onFolderSelect(Session.get().getCurrentFolder());
				Clipboard.getInstance().clear();
				Log.debug("Paste as Alias operation completed.");
			}
		});
	}

	@Override
	public void enable() {
		super.enable();
		getTree().setReportCollisions(false);
	}

	/**
	 * Moves the currently selected folder to the new parent folder
	 * 
	 * @param targetFolderId The parent folder
	 */
	public void moveTo(long targetFolderId) {
		final TreeNode selected = (TreeNode) getSelectedRecord();
		final TreeNode target = getTree().findById(Long.toString(targetFolderId));

		Log.debug("try to move folder " + selected.getAttribute("folderId"));
		service.move(Session.get().getSid(), Long.parseLong(selected.getAttribute("folderId")), targetFolderId,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						Log.warn(I18N.message("operationnotallowed"), null);
					}

					@Override
					public void onSuccess(Void ret) {
						getTree().remove(selected);
						if (target != null) {
							getTree().add(selected, target);
						}
					}
				});
	}

	public boolean isFirstTime() {
		return firstTime;
	}
}