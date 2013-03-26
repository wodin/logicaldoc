package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExternalCall;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDashboard;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDetailsDialog;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This context menu is used for grids containing document records.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentContextMenu extends Menu {

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected SearchServiceAsync searchService = (SearchServiceAsync) GWT.create(SearchService.class);

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	public DocumentContextMenu(final GUIFolder folder, final DocumentsGrid grid) {
		final ListGridRecord[] selection = grid.getSelectedRecords();

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = grid.getSelectedRecords();
				if (selection.length == 1) {
					String id = grid.getSelectedRecord().getAttribute("id");
					WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
							+ id);
				} else {
					String url = GWT.getHostPageBaseURL() + "zip-export?sid=" + Session.get().getSid() + "&folderId="
							+ folder.getId();
					for (ListGridRecord record : selection) {
						url += "&docId=" + record.getAttributeAsString("id");
					}
					WindowUtils.openUrl(url);
				}
			}
		});

		MenuItem cut = new MenuItem();
		cut.setTitle(I18N.message("cut"));
		cut.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				Clipboard.getInstance().clear();
				for (int i = 0; i < selection.length; i++) {
					String id = "";
					if (selection[i].getAttribute("aliasId") != null)
						id = selection[i].getAttribute("aliasId");
					else
						id = selection[i].getAttribute("id");
					GUIDocument document = new GUIDocument();
					document.setId(Long.parseLong(id));
					document.setTitle(selection[i].getAttribute("title"));
					document.setIcon(selection[i].getAttribute("icon"));
					Clipboard.getInstance().add(document);
					Clipboard.getInstance().setLastAction(Clipboard.CUT);
				}
			}
		});

		MenuItem copy = new MenuItem();
		copy.setTitle(I18N.message("copy"));
		copy.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				Clipboard.getInstance().clear();
				for (int i = 0; i < selection.length; i++) {
					String id = "";
					if (selection[i].getAttribute("aliasId") != null)
						id = selection[i].getAttribute("aliasId");
					else
						id = selection[i].getAttribute("id");
					GUIDocument document = new GUIDocument();
					document.setId(Long.parseLong(id));
					document.setTitle(selection[i].getAttribute("title"));
					document.setIcon(selection[i].getAttribute("icon"));
					Clipboard.getInstance().add(document);
					Clipboard.getInstance().setLastAction(Clipboard.COPY);
				}
			}
		});

		MenuItem rename = new MenuItem();
		rename.setTitle(I18N.message("renamefile"));
		rename.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord selection = grid.getSelectedRecord();

				LD.askforValue(I18N.message("rename"), I18N.message("filename"), selection.getAttribute("filename"),
						"250", new ValueCallback() {
							@Override
							public void execute(final String value) {
								if (value == null || value.isEmpty())
									return;

								documentService.rename(Session.get().getSid(),
										Long.parseLong(selection.getAttribute("id")), value, new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {
												Log.serverError(caught);
											}

											@Override
											public void onSuccess(Void result) {
												selection.setAttribute("filename", value);
												selection.setAttribute("indexed", "blank");
												grid.refreshRow(grid.getRecordIndex(selection));
												DocumentsPanel.get().showFolderDetails();
												DocumentsPanel.get().onSelectedDocument(
														Long.parseLong(selection.getAttribute("id")), false);
											}
										});
							}
						});
			}
		});

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					if (selection[i].getAttribute("aliasId") != null)
						ids[i] = Long.parseLong(selection[i].getAttribute("aliasId"));
					else
						ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							documentService.delete(Session.get().getSid(), ids, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									grid.removeSelectedData();

									DocumentsPanel.get().showFolderDetails();
									DocumentsPanel.get().getDocumentsMenu().refresh("trash");
								}
							});
						}
					}
				});
			}
		});

		MenuItem sendMail = new MenuItem();
		sendMail.setTitle(I18N.message("sendmail"));
		sendMail.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = grid.getSelectedRecords();
				if (selection == null || selection.length < 1)
					return;

				long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}
				EmailDialog window = new EmailDialog(ids, selection[0].getAttribute("title"));
				window.show();
			}
		});

		MenuItem links = new MenuItem();
		links.setTitle(I18N.message("pasteaslinks"));
		links.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0 || Clipboard.getInstance().isEmpty())
					return;

				final long[] outIds = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					outIds[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				final long[] inIds = new long[Clipboard.getInstance().size()];
				int i = 0;
				for (GUIDocument doc : Clipboard.getInstance()) {
					inIds[i++] = doc.getId();
				}

				documentService.linkDocuments(Session.get().getSid(), inIds, outIds, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						Clipboard.getInstance().clear();
					}

				});
			}
		});

		MenuItem immutable = new MenuItem();
		immutable.setTitle(I18N.message("makeimmutable"));
		immutable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				LD.askforValue(I18N.message("warning"), I18N.message("immutableadvice"), "", "50%",
						new ValueCallback() {

							@Override
							public void execute(String value) {
								if (value == null)
									return;

								if (value.isEmpty())
									SC.warn(I18N.message("commentrequired"));
								else
									documentService.makeImmutable(Session.get().getSid(), ids, value,
											new AsyncCallback<Void>() {
												@Override
												public void onFailure(Throwable caught) {
													Log.serverError(caught);
												}

												@Override
												public void onSuccess(Void result) {
													for (ListGridRecord record : selection) {
														record.setAttribute("immutable", "stop");
														grid.refreshRow(grid.getRecordIndex(record));
													}

													GUIDocument doc = grid.getSelectedDocument();
													Session.get().setCurrentDocument(doc);
												}
											});
							}

						});
			}
		});

		MenuItem lock = new MenuItem();
		lock.setTitle(I18N.message("lock"));
		lock.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				LD.askforValue(I18N.message("info"), I18N.message("lockadvice"), "", "50%", new ValueCallback() {

					@Override
					public void execute(String value) {
						if (value != null)
							documentService.lock(Session.get().getSid(), ids, value, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									for (ListGridRecord record : selection) {
										record.setAttribute("locked", "lock");
										record.setAttribute("lockUserId", Session.get().getUser().getId());
										grid.refreshRow(grid.getRecordIndex(record));
									}
									Session.get().getUser()
											.setLockedDocs(Session.get().getUser().getLockedDocs() + ids.length);
									GUIDocument doc = grid.getSelectedDocument();
									Session.get().setCurrentDocument(doc);
								}
							});
					}

				});
			}
		});

		MenuItem unlockItem = new MenuItem();
		unlockItem.setTitle(I18N.message("unlock"));
		unlockItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				documentService.unlock(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						for (ListGridRecord record : selection) {
							record.setAttribute("locked", "blank");
							record.setAttribute("status", Constants.DOC_UNLOCKED);
							if (Session.get().getUser().isMemberOf("admin")) {
								record.setAttribute("immutable", "blank");
							}
							grid.refreshRow(grid.getRecordIndex(record));
						}
						Session.get().getUser().setLockedDocs(Session.get().getUser().getLockedDocs() - ids.length);
						GUIDocument doc = grid.getSelectedDocument();
						if (doc != null)
							Session.get().setCurrentDocument(doc);
					}
				});
			}
		});

		MenuItem checkout = new MenuItem();
		checkout.setTitle(I18N.message("checkout"));
		checkout.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				GUIDocument record = grid.getSelectedDocument();
				documentService.checkout(Session.get().getSid(), record.getId(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						GUIDocument doc = grid.markSelectedAsCheckedOut();
						Session.get().setCurrentDocument(doc);
						Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() + 1);
						Log.info(I18N.message("documentcheckedout"), null);

						WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
								+ "&docId=" + id);
					}
				});
			}
		});

		MenuItem checkin = new MenuItem();
		checkin.setTitle(I18N.message("checkin"));
		checkin.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = grid.getSelectedRecord();
				if (selection == null)
					return;
				long id = Long.parseLong(selection.getAttribute("id"));
				String filename = selection.getAttributeAsString("filename");
				DocumentCheckin checkin = new DocumentCheckin(id, filename, grid);
				checkin.show();
			}
		});

		MenuItem bookmark = new MenuItem();
		bookmark.setTitle(I18N.message("addbookmark"));
		bookmark.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = grid.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttributeAsString("id"));
				}
				documentService.addBookmarks(Session.get().getSid(), ids, 0, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						DocumentsPanel.get().getDocumentsMenu().refresh("bookmarks");
					}
				});
			}
		});

		MenuItem markUnindexable = new MenuItem();
		markUnindexable.setTitle(I18N.message("markunindexable"));
		markUnindexable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				documentService.markUnindexable(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						for (ListGridRecord record : selection) {
							record.setAttribute("indexed", "unindexable");
							grid.refreshRow(grid.getRecordIndex(record));
						}
					}
				});
			}
		});

		MenuItem markIndexable = new MenuItem();
		markIndexable.setTitle(I18N.message("markindexable"));
		markIndexable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				documentService.markIndexable(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						for (ListGridRecord record : selection) {
							if ("indexed".equals(record.getAttribute("indexed")))
								continue;
							record.setAttribute("indexed", "blank");
							grid.refreshRow(grid.getRecordIndex(record));
						}
					}
				});
			}
		});

		MenuItem sign = new MenuItem();
		sign.setTitle(I18N.message("sign"));
		sign.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = grid.getSelectedRecord();
				if (selection == null)
					return;

				String id = selection.getAttribute("id");
				String filename = selection.getAttribute("filename");

				SignDialog dialog = new SignDialog(id, filename, null);
				dialog.show();
			}
		});

		MenuItem archive = new MenuItem(I18N.message("sendtoarchive"));
		archive.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getDocumentsGrid();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				SendToArchiveDialog archiveDialog = new SendToArchiveDialog(ids, true);
				archiveDialog.show();
			}
		});

		MenuItem edit = new MenuItem();
		edit.setTitle(I18N.message("editwithoffice"));
		edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = grid.getSelectedRecord();
				if (selection == null)
					return;
				long id = Long.parseLong(selection.getAttribute("id"));
				WindowUtils.openUrl("ldedit:" + GWT.getHostPageBaseURL() + "ldedit?action=edit&sid="
						+ Session.get().getSid() + "&docId=" + id);
			}
		});

		MenuItem startWorkflow = new MenuItem(I18N.message("startworkflow"));
		startWorkflow.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getDocumentsGrid();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;

				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				WorkflowDialog workflowDialog = new WorkflowDialog(ids);
				workflowDialog.show();
			}
		});

		MenuItem addToWorkflow = new MenuItem(I18N.message("addtoworkflow"));
		addToWorkflow.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getDocumentsGrid();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;

				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				workflowService.appendDocuments(Session.get().getSid(), Session.get().getCurrentWorkflow()
						.getSelectedTask().getId(), ids, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
						MainPanel.get().selectWorkflowTab();
						workflowService.getWorkflowDetailsByTask(Session.get().getSid(), Session.get()
								.getCurrentWorkflow().getSelectedTask().getId(), new AsyncCallback<GUIWorkflow>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								if (result != null) {
									WorkflowDetailsDialog workflowDetailsDialog = new WorkflowDetailsDialog(
											WorkflowDashboard.get(), result);
									workflowDetailsDialog.getTabs().setSelectedTab(1);
									workflowDetailsDialog.show();
									Session.get().setCurrentWorkflow(null);
								}
							}
						});
					}
				});
			}
		});

		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				long id = Long.parseLong(grid.getSelectedRecord().getAttribute("id"));
				String filename = grid.getSelectedRecord().getAttribute("filename");
				String fileVersion = grid.getSelectedRecord().getAttribute("fileVersion");

				// In the search hitlist we don't have the filename
				if (filename == null)
					filename = grid.getSelectedRecord().getAttribute("title") + "."
							+ grid.getSelectedRecord().getAttribute("type");

				GUIFolder folder = Session.get().getCurrentFolder();
				PreviewPopup iv = new PreviewPopup(id, fileVersion, filename, folder != null && folder.isDownload());
				iv.show();
			}
		});
		preview.setEnabled(selection.length == 1);

		MenuItem more = new MenuItem(I18N.message("more"));

		MenuItem externalCall = new MenuItem();
		externalCall.setEnabled(selection.length > 0);

		boolean enableLock = false;
		boolean enableUnlock = false;
		boolean enableImmutable = false;
		boolean enableDelete = true;
		boolean enableSign = selection != null && selection.length > 0;
		boolean enableRename = selection != null && selection.length == 1 && folder.isRename();

		boolean isOfficeFile = false;
		if (selection[0].getAttribute("filename") != null)
			isOfficeFile = Util.isOfficeFile(selection[0].getAttribute("filename"));
		else if (selection[0].getAttribute("type") != null)
			isOfficeFile = Util.isOfficeFileType(selection[0].getAttribute("type"));

		if (selection != null && selection.length == 1) {
			ListGridRecord record = selection[0];
			if ("blank".equals(record.getAttribute("locked")) && "blank".equals(record.getAttribute("immutable"))) {
				enableLock = true;
			}
			if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
				Long lockUser = record.getAttribute("lockUserId") != null ? Long.parseLong(record
						.getAttribute("lockUserId")) : Long.MIN_VALUE;
				if (Session.get().getUser().getId() == lockUser.longValue()
						|| Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
					enableUnlock = true;
			}
		}

		boolean enableOffice = (enableUnlock || enableLock)
				&& (selection != null && selection.length == 1 && isOfficeFile);

		for (ListGridRecord record : selection)
			if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
				cut.setEnabled(false);
				enableRename = false;
				break;
			}

		if (folder.hasPermission(Constants.PERMISSION_IMMUTABLE)) {
			enableImmutable = true;
			for (ListGridRecord record : selection) {
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
					enableImmutable = false;
				}
			}
		}

		if (grid.getSelectedRecords().length != 1) {
			rename.setEnabled(false);
		}

		if (Clipboard.getInstance().isEmpty()) {
			links.setEnabled(false);
		}

		if (!folder.hasPermission(Constants.PERMISSION_DELETE)) {
			enableDelete = false;
			cut.setEnabled(false);
		} else {
			enableDelete = true;
			for (ListGridRecord record : selection) {
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
					enableDelete = false;
				}
			}
		}

		if (!folder.hasPermission(Constants.PERMISSION_WRITE)) {
			links.setEnabled(false);
			markIndexable.setEnabled(false);
			markUnindexable.setEnabled(false);
			checkout.setEnabled(false);
		}

		if (selection.length != 1
				|| (selection[0].getAttribute("status") != null && Constants.DOC_CHECKED_OUT != Integer
						.parseInt(selection[0].getAttribute("status"))))
			checkin.setEnabled(false);

		if (selection != null && selection.length == 1) {
			Long lockUser = selection[0].getAttribute("lockUserId") != null ? Long.parseLong(selection[0]
					.getAttribute("lockUserId")) : Long.MIN_VALUE;
			if (Session.get().getUser().getId() != lockUser.longValue()
					&& !Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
				checkin.setEnabled(false);
		}

		unlockItem.setEnabled(enableUnlock);
		lock.setEnabled(enableLock);
		if (checkout.getEnabled())
			checkout.setEnabled(enableLock);
		immutable.setEnabled(enableImmutable);
		delete.setEnabled(enableDelete);

		for (ListGridRecord record : selection)
			if ("indexed".equals(record.getAttribute("indexed")) && !"blank".equals(record.getAttribute("immutable"))) {
				markIndexable.setEnabled(false);
				break;
			}

		if ((selection.length == 1 && selection[0].getAttribute("indexed").equals("blank"))) {
			markIndexable.setEnabled(false);
		}

		if ((selection.length == 1 && selection[0].getAttribute("status") == null)) {
			checkin.setEnabled(false);
			checkout.setEnabled(false);
			lock.setEnabled(false);
			unlockItem.setEnabled(false);
		}

		if (!folder.isDownload()) {
			download.setEnabled(false);
			sendMail.setEnabled(false);
			checkout.setEnabled(false);
			copy.setEnabled(false);
			cut.setEnabled(false);
			enableSign = false;
		}

		rename.setEnabled(enableRename);

		final GUIExternalCall extCall = Session.get().getSession().getExternalCall();
		if (Feature.enabled(Feature.EXTERNAL_CALL) && extCall != null) {
			externalCall.setTitle(extCall.getName());
			externalCall.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
				public void onClick(MenuItemClickEvent event) {
					List<Long> ids = new ArrayList<Long>();
					List<String> titles = new ArrayList<String>();
					for (ListGridRecord record : selection) {
						ids.add(Long.parseLong(record.getAttributeAsString("id")));
						titles.add(record.getAttributeAsString("title"));
					}

					WindowUtils.openUrl(extCall.getUrl(true, ids.toArray(new Long[0]), titles.toArray(new String[0])),
							extCall.getTargetWindow() != null ? extCall.getTargetWindow() : "_blank");
				}
			});
			setItems(download, preview, cut, copy, rename, delete, bookmark, sendMail, links, checkout, checkin, lock,
					unlockItem, more, externalCall);
		} else
			setItems(download, preview, cut, copy, rename, delete, bookmark, sendMail, links, checkout, checkin, lock,
					unlockItem, more);

		Menu moreMenu = new Menu();
		moreMenu.setItems(immutable, markIndexable, markUnindexable);

		if (Feature.visible(Feature.OFFICE)) {
			moreMenu.addItem(edit);
			if (!Feature.enabled(Feature.OFFICE))
				edit.setEnabled(false);
			else
				edit.setEnabled(enableOffice);
		}

		if (enableSign && Feature.visible(Feature.DIGITAL_SIGN)) {
			moreMenu.addItem(sign);
			if (!folder.hasPermission(Constants.PERMISSION_SIGN) || !Feature.enabled(Feature.DIGITAL_SIGN))
				sign.setEnabled(false);
			else
				sign.setEnabled(enableSign && selection.length == 1);
		}

		if (Feature.visible(Feature.ARCHIVES)) {
			moreMenu.addItem(archive);
			if (!folder.hasPermission(Constants.PERMISSION_ARCHIVE) || !Feature.enabled(Feature.ARCHIVES))
				archive.setEnabled(false);
			else
				archive.setEnabled(enableSign);
		}

		if (Feature.visible(Feature.WORKFLOW)) {
			moreMenu.addItem(startWorkflow);
			moreMenu.addItem(addToWorkflow);
			if (!folder.hasPermission(Constants.PERMISSION_WORKFLOW) || !Feature.enabled(Feature.WORKFLOW)) {
				startWorkflow.setEnabled(false);
				addToWorkflow.setEnabled(false);
			} else {
				startWorkflow.setEnabled(enableSign);
				addToWorkflow.setEnabled(enableSign && Session.get().getCurrentWorkflow() != null);
			}
		}

		more.setSubmenu(moreMenu);
	}
}