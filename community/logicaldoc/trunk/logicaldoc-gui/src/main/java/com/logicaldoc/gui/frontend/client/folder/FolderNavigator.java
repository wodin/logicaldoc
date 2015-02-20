package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExternalCall;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.FoldersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.SendToArchiveDialog;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.Search;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.logicaldoc.gui.frontend.client.subscription.SubscriptionDialog;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
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
 * The panel that shows the workspaces/folders navigation tree
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FolderNavigator extends TreeGrid implements FolderObserver {
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private static FolderNavigator instance = new FolderNavigator();

	private boolean firstTime = true;

	private FolderNavigator() {
		setWidth100();
		setBorder("0px");
		setBodyStyleName("normal");
		setShowHeader(false);
		setLeaveScrollbarGap(false);
		setCanReorderRecords(false);
		setCanDragRecordsOut(false);
		setAutoFetchData(true);
		setLoadDataOnDemand(true);
		setDataSource(FoldersDS.get());
		setCanSelectAll(false);
		setShowConnectors(true);
		setShowRoot(false);
		setCanAcceptDrop(true);
		setCanAcceptDroppedRecords(true);

		addDragStartHandler(new DragStartHandler() {

			@Override
			public void onDragStart(DragStartEvent ev) {
				if (EventHandler.getDragTarget() instanceof FolderNavigator) {
					// Workspaces cannot be moved
					if ("1".equals(getDragData()[0].getAttributeAsString("type"))) {
						ev.cancel();
						return;
					}
				}
			}

		});

		addDropHandler(new DropHandler() {
			public void onDrop(final DropEvent event) {
				try {
					if (EventHandler.getDragTarget() instanceof FolderNavigator) {
						// Workspaces cannot be moved
						if ("1".equals(getDragData()[0].getAttributeAsString("type"))) {
							event.cancel();
							return;
						}

						final long source = Long.parseLong(getDragData()[0].getAttributeAsString("folderId"));
						final long target = Long.parseLong(getDropFolder().getAttributeAsString("folderId"));

						final String sourceName = getDragData()[0].getAttributeAsString("name");
						final String targetName = getDropFolder().getAttributeAsString("name");

						LD.ask(I18N.message("move"), I18N.message("moveask", new String[] { sourceName, targetName }),
								new BooleanCallback() {

									@Override
									public void execute(Boolean value) {
										if (value) {
											service.move(Session.get().getSid(), source, target,
													new AsyncCallback<Void>() {
														@Override
														public void onFailure(Throwable caught) {
															Log.serverError(caught);
															Log.warn(I18N.message("operationnotallowed"), null);
														}

														@Override
														public void onSuccess(Void ret) {

														}
													});
										}

										TreeNode node = getTree().find("folderId", (Object) new Long(source));
										if (node != null) {
											node = getTree().getParent(node);
											if (node != null)
												getTree().reloadChildren(node);
										}
										node = getTree().find("folderId", (Object) new Long(target));
										if (node != null) {
											getTree().reloadChildren(node);
										}
									}
								});
					} else if (EventHandler.getDragTarget() instanceof DocumentsGrid) {
						/*
						 * In this case we are moving a document
						 */
						DocumentsGrid grid = (DocumentsGrid) EventHandler.getDragTarget();
						final GUIDocument[] selection = grid.getSelectedDocuments();
						if (selection == null || selection.length == 0)
							return;
						final long[] ids = new long[selection.length];
						for (int i = 0; i < selection.length; i++)
							if (selection[i].getDocRef() != null)
								ids[i] = selection[i].getDocRef();
							else
								ids[i] = selection[i].getId();

						final TreeNode selectedNode = getDropFolder();
						final long folderId = Long.parseLong(selectedNode.getAttribute("folderId"));

						if (Session.get().getCurrentFolder().getId() == folderId)
							return;

						final String sourceName = selection.length == 1 ? selection[0].getTitle() : (selection.length
								+ " " + I18N.message("documents").toLowerCase());
						final String targetName = selectedNode.getAttributeAsString("name");

						LD.ask(I18N.message("move"), I18N.message("moveask", new String[] { sourceName, targetName }),
								new BooleanCallback() {

									@Override
									public void execute(Boolean value) {
										if (value) {
											service.paste(Session.get().getSid(), ids, folderId, "cut",
													new AsyncCallback<Void>() {
														@Override
														public void onFailure(Throwable caught) {
															Log.serverError(caught);
															Log.warn(I18N.message("operationnotallowed"), null);
														}

														@Override
														public void onSuccess(Void result) {
															DocumentsPanel.get().onFolderSelected(
																	Session.get().getCurrentFolder());
															Log.debug("Drag&Drop operation completed.");
														}
													});
										}

										TreeNode node = getTree().find("folderId", (Object) new Long(folderId));
										if (node != null) {
											getTree().reloadChildren(node);
										}
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
								Menu contextMenu = prepateContextMenu(folder);
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
						TreeNode rootNode = getTree().getRoot();
						TreeNode[] children = getTree().getChildren(rootNode);
						if (children != null && children.length > 0) {
							getTree().openFolder(children[0]);
						}

						service.getFolder(Session.get().getSid(), Long.parseLong(children[0].getAttribute("folderId")),
								true, new AsyncCallback<GUIFolder>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIFolder folder) {
										Session.get().setCurrentFolder(folder);
									}
								});
					}

					FolderNavigator.this.firstTime = false;
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
	private Menu prepateContextMenu(final GUIFolder folder) {
		final TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long id = Long.parseLong(selectedNode.getAttribute("folderId"));
		final String name = selectedNode.getAttribute("name");

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
				onDelete(id);
			}
		});

		MenuItem create = new MenuItem();
		create.setTitle(I18N.message("newfolder"));
		create.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onCreate(folder.getId());
			}
		});

		MenuItem rename = new MenuItem();
		rename.setTitle(I18N.message("rename"));
		rename.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onRename();
			}
		});

		MenuItem createWorkspace = new MenuItem();
		createWorkspace.setTitle(I18N.message("newworkspace"));
		createWorkspace.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onCreateWorkspace();
			}
		});

		MenuItem applyTemplate = new MenuItem();
		applyTemplate.setTitle(I18N.message("applytemplate"));
		applyTemplate.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onApplyTemplate();
			}
		});

		MenuItem archive = new MenuItem();
		archive.setTitle(I18N.message("archive"));
		archive.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onArchive(folder.getId());
			}
		});

		MenuItem reload = new MenuItem();
		reload.setTitle(I18N.message("reload"));
		reload.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				reload();
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

		MenuItem copy = new MenuItem();
		copy.setTitle(I18N.message("copy"));
		copy.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				CopyDialog dialog = new CopyDialog();
				dialog.show();
			}
		});

		MenuItem paste = new MenuItem();
		paste.setTitle(I18N.message("paste"));
		paste.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onPaste();
			}
		});

		MenuItem pasteAsAlias = new MenuItem();
		pasteAsAlias.setTitle(I18N.message("pasteasalias"));
		pasteAsAlias.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
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
				SubscriptionDialog dialog = new SubscriptionDialog(id, null);
				dialog.show();
			}
		});

		MenuItem addBookmark = new MenuItem();
		addBookmark.setTitle(I18N.message("addbookmark"));
		addBookmark.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onAddBookmark();
			}
		});

		MenuItem sendToExpArchive = new MenuItem();
		sendToExpArchive.setTitle(I18N.message("sendtoexparchive"));
		sendToExpArchive.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmarchive"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							SendToArchiveDialog archiveDialog = new SendToArchiveDialog(new long[] { id }, false);
							archiveDialog.show();
						}
					}
				});
			}
		});

		MenuItem externalCall = null;
		final GUIExternalCall extCall = Session.get().getSession().getExternalCall();
		if (Feature.enabled(Feature.EXTERNAL_CALL) && extCall != null) {
			externalCall = new MenuItem();
			externalCall.setTitle(extCall.getName());
			externalCall.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
				public void onClick(MenuItemClickEvent event) {
					WindowUtils.openUrl(extCall.getUrl(false, new Long[] { id }, new String[] { name }),
							extCall.getTargetWindow() != null ? extCall.getTargetWindow() : "_blank", null);
				}
			});
		}

		if (!folder.hasPermission(Constants.PERMISSION_ADD)) {
			create.setEnabled(false);
		}

		if (!folder.hasPermission(Constants.PERMISSION_RENAME)) {
			rename.setEnabled(false);
		}

		if (!folder.hasPermission(Constants.PERMISSION_EXPORT) || !folder.hasPermission(Constants.PERMISSION_DOWNLOAD)) {
			exportZip.setEnabled(false);
		}

		// Avoid alterations of the Default workspace
		if (folder.isDefaultWorkspace() || !folder.hasPermission(Constants.PERMISSION_DELETE)) {
			delete.setEnabled(false);
			move.setEnabled(false);
		}

		if (folder.isDefaultWorkspace()) {
			rename.setEnabled(false);
		}

		if (!folder.hasPermission(Constants.PERMISSION_WRITE) || Clipboard.getInstance().isEmpty()) {
			paste.setEnabled(false);
			pasteAsAlias.setEnabled(false);
		}

		if (Clipboard.getInstance().getLastAction().equals(Clipboard.CUT))
			pasteAsAlias.setEnabled(false);

		if (Session.get().getUser().isMemberOf("admin") && folder.getType() == 1
				&& Feature.visible(Feature.MULTI_WORKSPACE)) {
			delete.setEnabled(!folder.isDefaultWorkspace());
			move.setEnabled(false);
			rename.setEnabled(!folder.isDefaultWorkspace());
			createWorkspace.setEnabled(Feature.enabled(Feature.MULTI_WORKSPACE));
			contextMenu.setItems(reload, search, create, rename, createWorkspace, delete, addBookmark, paste,
					pasteAsAlias, move, copy, exportZip);
		} else {
			contextMenu.setItems(reload, search, create, rename, delete, addBookmark, paste, pasteAsAlias, move, copy,
					exportZip);
		}

		if (Feature.visible(Feature.AUDIT)) {
			contextMenu.addItem(audit);
			audit.setEnabled(Feature.enabled(Feature.AUDIT));
		}

		if (Feature.visible(Feature.RSS)) {
			contextMenu.addItem(rss);
			rss.setEnabled(Feature.enabled(Feature.RSS));
		}

		if (Feature.visible(Feature.FOLDER_TEMPLATE)) {
			contextMenu.addItem(applyTemplate);
			if (!Feature.enabled(Feature.FOLDER_TEMPLATE) || !folder.hasPermission(Constants.PERMISSION_ADD))
				applyTemplate.setEnabled(false);
		}

		if (Feature.visible(Feature.ARCHIVING)) {
			contextMenu.addItem(archive);
			if (!Feature.enabled(Feature.ARCHIVING) || !folder.hasPermission(Constants.PERMISSION_ARCHIVE))
				archive.setEnabled(false);
		}

		if (Feature.visible(Feature.IMPEX)) {
			contextMenu.addItem(sendToExpArchive);
			if (!Feature.enabled(Feature.IMPEX) || !folder.hasPermission(Constants.PERMISSION_EXPORT))
				sendToExpArchive.setEnabled(false);
		}

		if (externalCall != null)
			contextMenu.addItem(externalCall);

		return contextMenu;
	}

	private void onDelete(final long folderId) {
		documentService.countDocuments(Session.get().getSid(), folderId, Constants.DOC_ARCHIVED,
				new AsyncCallback<Long>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Long count) {
						LD.ask(I18N.message("question"),
								count == 0 ? I18N.message("confirmdeletefolder") : I18N
										.message("confirmdeletefolderarchdocs"), new BooleanCallback() {
									@Override
									public void execute(Boolean value) {
										if (value) {
											service.delete(Session.get().getSid(), folderId, new AsyncCallback<Void>() {
												@Override
												public void onFailure(Throwable caught) {
													Log.serverError(caught);
												}

												@Override
												public void onSuccess(Void result) {
													/*
													 * This is needed because of
													 * strange behaviours if we
													 * directly delete the
													 * selected node.
													 */
													TreeNode node = getTree().find("folderId", Long.toString(folderId));
													TreeNode parent = getTree().find("folderId",
															node.getAttribute("parent"));
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
	}

	/**
	 * Allows the selection of a folders template to apply to the current node
	 */
	private void onApplyTemplate() {
		ApplyTemplateDialog dialog = new ApplyTemplateDialog();
		dialog.show();
	}

	/**
	 * Adds a bookmark to the currently selected folder.
	 */
	private void onAddBookmark() {
		final TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long folderId = Long.parseLong(selectedNode.getAttributeAsString("folderId"));

		documentService.addBookmarks(Session.get().getSid(), new long[] { folderId }, 1, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void v) {
			}
		});
	}

	public static FolderNavigator get() {
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
					if (fld.getId() == Constants.DOCUMENTS_FOLDERID)
						continue;
					TreeNode node = new TreeNode(fld.getName());
					node.setAttribute("folderId", Long.toString(fld.getId()));
					node.setAttribute("type", Integer.toString(fld.getType()));
					node.setAttribute(Constants.PERMISSION_ADD, fld.hasPermission(Constants.PERMISSION_ADD));
					node.setAttribute(Constants.PERMISSION_DELETE, fld.hasPermission(Constants.PERMISSION_DELETE));
					node.setAttribute(Constants.PERMISSION_RENAME, fld.hasPermission(Constants.PERMISSION_RENAME));

					getTree().add(node, parent);
					parent = node;
				}
				TreeNode node = new TreeNode(folder.getName());
				node.setAttribute("folderId", Long.toString(folder.getId()));
				node.setAttribute("type", Integer.toString(folder.getType()));
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

	@Override
	public void onFolderSaved(GUIFolder folder, boolean positionChanged) {
		TreeNode selectedNode = getTree().find("folderId", Long.toString(folder.getId()));
		folder.setPathExtended(getPath(folder.getId()));
		if (selectedNode != null) {
			selectedNode.setTitle(folder.getName());
			selectedNode.setName(folder.getName());
			getTree().reloadChildren(selectedNode);

			if (positionChanged) {
				TreeNode parentNode = getTree().find("folderId", Long.toString(folder.getParentId()));
				if (parentNode != null)
					getTree().reloadChildren(parentNode);
				else
					getTree().reloadChildren(getTree().getRoot());
			}
		}
	}

	public void reload() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		getTree().reloadChildren(selectedNode);
		selectFolder(Long.parseLong(selectedNode.getAttributeAsString("folderId")));
	}

	private void onCreate(long parentId) {
		GUIFolder folder = new GUIFolder();
		folder.setParentId(parentId);
		CreateDialog dialog = new CreateDialog(folder);
		dialog.show();
	}

	private void onRename() {
		final TreeNode selectedNode = (TreeNode) getSelectedRecord();
		LD.askforValue(I18N.message("rename"), I18N.message("title"), selectedNode.getAttributeAsString("name"), "200",
				new ValueCallback() {
					@Override
					public void execute(final String value) {
						if (value == null || "".equals(value.trim()))
							return;
						final String val = value.trim().replaceAll("/", "").replaceAll("\\\\", "");
						final long folderId = Long.parseLong(selectedNode.getAttributeAsString("folderId"));
						service.rename(Session.get().getSid(), folderId, val, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void v) {
								selectedNode.setAttribute("name", val);
								refreshRow(getRecordIndex(selectedNode));
								selectFolder(folderId);
							}
						});
					}
				});
	}

	private void onCreateWorkspace() {
		GUIFolder folder = new GUIFolder();
		folder.setType(1);
		CreateDialog dialog = new CreateDialog(folder);
		dialog.show();
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
						DocumentsPanel.get().onFolderSelected(Session.get().getCurrentFolder());
						Clipboard.getInstance().clear();
					}
				});
	}

	private void onPasteAsAlias() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long folderId = Long.parseLong(selectedNode.getAttribute("folderId"));
		final long[] docIds = new long[Clipboard.getInstance().size()];
		int i = 0;
		for (GUIDocument doc : Clipboard.getInstance())
			docIds[i++] = doc.getId();

		if (Feature.enabled(Feature.PDF))
			LD.askforValue(I18N.message("pasteasalias"), "type", "", "250", ItemFactory.newAliasTypeSelector(),
					new ValueCallback() {

						@Override
						public void execute(String type) {
							pasteAsAlias(folderId, docIds, type);
						}
					});
		else
			pasteAsAlias(folderId, docIds, null);
	}

	private void pasteAsAlias(final long folderId, final long[] docIds, String type) {
		service.pasteAsAlias(Session.get().getSid(), docIds, folderId, type, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				DocumentsPanel.get().onFolderSelected(Session.get().getCurrentFolder());
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

	/**
	 * Copies the currently selected folder to the new parent folder
	 * 
	 * @param targetFolderId The parent folder
	 */
	public void copyTo(long targetFolderId, boolean foldersOnly, boolean inheritPermissions) {
		final TreeNode selected = (TreeNode) getSelectedRecord();
		final TreeNode target = getTree().findById(Long.toString(targetFolderId));

		Log.debug("try to copy folder " + selected.getAttribute("folderId"));

		ContactingServer.get().show();
		service.copyFolder(Session.get().getSid(), Long.parseLong(selected.getAttribute("folderId")), targetFolderId,
				foldersOnly, inheritPermissions, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
						ContactingServer.get().hide();
						if (target != null)
							getTree().reloadChildren(target);
					}
				});
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	@Override
	protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
		if ("1".equals(record.getAttribute("type"))) {
			return "font-weight:bold;";
		} else
			return super.getCellCSSText(record, rowNum, colNum);
	}

	@Override
	protected String getIcon(Record record, boolean defaultState) {
		if ("1".equals(record.getAttribute("type"))) {
			setCustomNodeIcon(record, Util.imageUrl("cube_blue16.png"));
		}
		return super.getIcon(record, defaultState);
	}

	@Override
	public void onFolderSelected(GUIFolder folder) {
		// Nothing to do
	}

	public void onArchive(final long folderId) {
		LD.askforValue(I18N.message("warning"), I18N.message("archiveadvice"), "", "50%", new ValueCallback() {

			@Override
			public void execute(String value) {
				if (value == null)
					return;

				if (value.isEmpty())
					SC.warn(I18N.message("commentrequired"));
				else
					documentService.archiveFolder(Session.get().getSid(), folderId, value, new AsyncCallback<Long>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Long result) {
							Log.info(I18N.message("documentswerearchived", "" + result), null);
							reload();
						}
					});
			}
		});
	}
}