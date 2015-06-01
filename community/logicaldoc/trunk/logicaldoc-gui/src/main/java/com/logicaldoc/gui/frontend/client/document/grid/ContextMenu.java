package com.logicaldoc.gui.frontend.client.document.grid;

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
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.document.DocumentCheckin;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.EmailDialog;
import com.logicaldoc.gui.frontend.client.document.SendToArchiveDialog;
import com.logicaldoc.gui.frontend.client.document.StampDialog;
import com.logicaldoc.gui.frontend.client.document.UploadSignedDocument;
import com.logicaldoc.gui.frontend.client.document.WorkflowDialog;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SignService;
import com.logicaldoc.gui.frontend.client.services.SignServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
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
public class ContextMenu extends Menu {

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected SearchServiceAsync searchService = (SearchServiceAsync) GWT.create(SearchService.class);

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	protected SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	public ContextMenu(final GUIFolder folder, final DocumentsGrid grid) {
		final GUIDocument[] selection = grid.getSelectedDocuments();
		final long[] selectionIds = grid.getSelectedIds();

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection.length == 1) {
					long id = selection[0].getId();
					WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
							+ id);
				} else {
					String url = GWT.getHostPageBaseURL() + "zip-export?sid=" + Session.get().getSid() + "&folderId="
							+ folder.getId();
					for (GUIDocument record : selection) {
						url += "&docId=" + record.getId();
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
					long id = selection[i].getId();
					if (selection[i].getDocRef() != null)
						id = selection[i].getDocRef();

					GUIDocument document = new GUIDocument();
					document.setId(id);
					document.setTitle(selection[i].getTitle());
					document.setIcon(selection[i].getIcon());
					document.setLastModified(selection[i].getLastModified());
					document.setVersion(selection[i].getVersion());
					document.setFileVersion(selection[i].getFileVersion());
					document.setFileName(selection[i].getFileName());

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
					long id = selection[i].getId();
					if (selection[i].getDocRef() != null)
						id = selection[i].getDocRef();

					GUIDocument document = new GUIDocument();
					document.setId(id);
					document.setTitle(selection[i].getTitle());
					document.setIcon(selection[i].getIcon());
					document.setLastModified(selection[i].getLastModified());
					document.setVersion(selection[i].getVersion());
					document.setFileVersion(selection[i].getFileVersion());
					document.setFileName(selection[i].getFileName());

					Clipboard.getInstance().add(document);
					Clipboard.getInstance().setLastAction(Clipboard.COPY);
				}
			}
		});

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++)
					ids[i] = selection[i].getId();

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
									grid.removeSelectedDocuments();

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
				GUIDocument[] selection = grid.getSelectedDocuments();
				if (selection == null || selection.length < 1)
					return;
				EmailDialog window = new EmailDialog(grid.getSelectedIds(), selection[0].getTitle());
				window.show();
			}
		});

		MenuItem links = new MenuItem();
		links.setTitle(I18N.message("pasteaslinks"));
		links.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0 || Clipboard.getInstance().isEmpty())
					return;

				final long[] inIds = new long[Clipboard.getInstance().size()];
				int i = 0;
				for (GUIDocument doc : Clipboard.getInstance())
					inIds[i++] = doc.getId();

				documentService.linkDocuments(Session.get().getSid(), inIds, selectionIds, new AsyncCallback<Void>() {

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
				if (selection == null || selection.length == 0)
					return;

				LD.askforValue(I18N.message("warning"), I18N.message("immutableadvice"), "", "50%",
						new ValueCallback() {

							@Override
							public void execute(String value) {
								if (value == null)
									return;

								if (value.isEmpty())
									SC.warn(I18N.message("commentrequired"));
								else
									documentService.makeImmutable(Session.get().getSid(), selectionIds, value,
											new AsyncCallback<Void>() {
												@Override
												public void onFailure(Throwable caught) {
													Log.serverError(caught);
												}

												@Override
												public void onSuccess(Void result) {
													for (GUIDocument record : selection) {
														record.setImmutable(1);
														grid.updateDocument(record);
													}

													grid.selectDocument(selection[0].getId());
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
				if (selection == null || selection.length == 0)
					return;

				LD.askforValue(I18N.message("info"), I18N.message("lockadvice"), "", "50%", new ValueCallback() {

					@Override
					public void execute(String value) {
						if (value != null)
							documentService.lock(Session.get().getSid(), selectionIds, value,
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											for (GUIDocument record : selection) {
												record.setLockUserId(Session.get().getUser().getId());
												record.setStatus(Constants.DOC_LOCKED);
												grid.updateDocument(record);
											}

											Session.get()
													.getUser()
													.setLockedDocs(
															Session.get().getUser().getLockedDocs() + selection.length);
											grid.selectDocument(selectionIds[0]);
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

				documentService.unlock(Session.get().getSid(), selectionIds, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						for (GUIDocument record : selection) {
							record.setStatus(Constants.DOC_UNLOCKED);
							if (Session.get().getUser().isMemberOf("admin")) {
								record.setImmutable(0);
							}
							grid.updateDocument(record);
						}
						Session.get().getUser()
								.setLockedDocs(Session.get().getUser().getLockedDocs() - selectionIds.length);
						grid.selectDocument(selectionIds[0]);
					}
				});
			}
		});

		MenuItem checkout = new MenuItem();
		checkout.setTitle(I18N.message("checkout"));
		checkout.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final GUIDocument document = grid.getSelectedDocument();
				documentService.checkout(Session.get().getSid(), document.getId(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						grid.markSelectedAsCheckedOut();
						grid.selectDocument(document.getId());
						Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() + 1);
						Log.info(I18N.message("documentcheckedout"), null);

						WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
								+ "&docId=" + document.getId());
					}
				});
			}
		});

		MenuItem checkin = new MenuItem();
		checkin.setTitle(I18N.message("checkin"));
		checkin.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				GUIDocument selection = grid.getSelectedDocument();
				if (selection == null)
					return;
				long id = selection.getId();
				final String filename = selection.getFileName();
				documentService.getById(Session.get().getSid(), id, new AsyncCallback<GUIDocument>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIDocument document) {
						DocumentCheckin checkin = new DocumentCheckin(document, filename, grid);
						checkin.show();
					}
				});
			}
		});

		MenuItem archive = new MenuItem();
		archive.setTitle(I18N.message("archive"));
		archive.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;

				LD.askforValue(I18N.message("warning"), I18N.message("archiveadvice"), "", "50%", new ValueCallback() {

					@Override
					public void execute(String value) {
						if (value == null)
							return;

						if (value.isEmpty())
							SC.warn(I18N.message("commentrequired"));
						else
							documentService.archiveDocuments(Session.get().getSid(), selectionIds, value,
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											grid.removeSelectedDocuments();
											Log.info(I18N.message("documentswerearchived", "" + selectionIds.length),
													null);
										}
									});
					}

				});
			}
		});

		MenuItem bookmark = new MenuItem();
		bookmark.setTitle(I18N.message("addbookmark"));
		bookmark.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;
				documentService.addBookmarks(Session.get().getSid(), selectionIds, 0, new AsyncCallback<Void>() {
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
				if (selection == null || selection.length == 0)
					return;

				documentService.markUnindexable(Session.get().getSid(), selectionIds, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						for (GUIDocument record : selection) {
							record.setIndexed(Constants.INDEX_SKIP);
							grid.updateDocument(record);
						}
					}
				});
			}
		});

		MenuItem markIndexable = new MenuItem();
		markIndexable.setTitle(I18N.message("markindexable"));
		markIndexable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;

				documentService.markIndexable(Session.get().getSid(), selectionIds, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						for (GUIDocument record : selection) {
							record.setIndexed(Constants.INDEX_TO_INDEX);
							grid.updateDocument(record);
						}
					}
				});
			}
		});

		MenuItem indexSelection = new MenuItem();
		indexSelection.setTitle(I18N.message("index"));
		indexSelection.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;

				Long[] ids = new Long[selectionIds.length];
				for (int i = 0; i < selectionIds.length; i++)
					ids[i] = selectionIds[i];

				ContactingServer.get().show();
				documentService.indexDocuments(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						ContactingServer.get().hide();
						for (GUIDocument record : selection) {
							record.setIndexed(Constants.INDEX_INDEXED);
							grid.updateDocument(record);
						}
					}
				});
			}
		});

		MenuItem sign = new MenuItem();
		sign.setTitle(I18N.message("sign"));
		sign.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (Session.get().getUser().getCertSubject() == null) {
					SC.warn(I18N.message("loadsignaturefirst"));
					return;
				}

				if (Session.get().getUser().getKeyDigest() == null) {
					// The user must upload the signed .p7m version of the
					// original file
					GUIDocument selection = grid.getSelectedDocument();
					if (selection == null)
						return;

					long id = selection.getId();
					String filename = selection.getFileName();

					UploadSignedDocument dialog = new UploadSignedDocument(id, filename);
					dialog.show();
				} else {
					ContactingServer.get().show();
					signService.signDocuments(Session.get().getSid(), selectionIds, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							ContactingServer.get().hide();
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(String result) {
							ContactingServer.get().hide();
							DocumentsPanel.get().refresh();

							if (!"ok".equals(result))
								SC.warn(I18N.message(result));
						}
					});
				}
			}
		});

		MenuItem stamp = new MenuItem();
		stamp.setTitle(I18N.message("stamp"));
		stamp.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				GUIDocument selection = grid.getSelectedDocument();
				if (selection == null)
					return;
				long docId = selection.getId();
				
				StampDialog dialog = new StampDialog(new long[]{docId});
				dialog.show();
			}
		});
		
		MenuItem sendToExpArchive = new MenuItem(I18N.message("sendtoexparchive"));
		sendToExpArchive.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;

				SendToArchiveDialog archiveDialog = new SendToArchiveDialog(selectionIds, true);
				archiveDialog.show();
			}
		});

		MenuItem startWorkflow = new MenuItem(I18N.message("startworkflow"));
		startWorkflow.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = (ListGrid) DocumentsPanel.get().getDocumentsGrid();
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

		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				GUIDocument selection = grid.getSelectedDocument();
				long id = selection.getId();
				String filename = selection.getFileName();
				String fileVersion = selection.getFileVersion();

				// In the search hitlist we don't have the filename
				if (filename == null)
					filename = selection.getTitle() + "." + selection.getType();

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
		boolean enableStamp = selection != null && selection.length > 0;

		if (selection != null && selection.length == 1) {
			GUIDocument record = selection[0];
			if (record.getStatus() == Constants.DOC_UNLOCKED && record.getImmutable() == 0 && folder.isWrite()) {
				enableLock = true;
			}
			if (record.getStatus() != Constants.DOC_UNLOCKED || record.getImmutable() != 0) {
				Long lockUser = record.getLockUserId();
				if ((lockUser != null && Session.get().getUser().getId() == lockUser.longValue())
						|| Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
					enableUnlock = true;
			}

			enableSign = enableSign && record.getSigned() == 0;
		}

		for (GUIDocument record : selection)
			if (record.getStatus() != Constants.DOC_UNLOCKED || record.getImmutable() != 0) {
				cut.setEnabled(false);
				enableSign=false;
				enableStamp=false;
				break;
			}

		if (folder.hasPermission(Constants.PERMISSION_IMMUTABLE)) {
			enableImmutable = true;
			for (GUIDocument record : selection) {
				if (record.getStatus() != Constants.DOC_UNLOCKED || record.getImmutable() != 0) {
					enableImmutable = false;
					break;
				}
			}
		}

		if (Clipboard.getInstance().isEmpty()) {
			links.setEnabled(false);
		}

		if (!folder.hasPermission(Constants.PERMISSION_DELETE)) {
			enableDelete = false;
			cut.setEnabled(false);
		} else {
			enableDelete = true;
			for (GUIDocument record : selection) {
				if (record.getStatus() != Constants.DOC_UNLOCKED || record.getImmutable() != 0) {
					enableDelete = false;
					break;
				}
			}
		}

		if (!folder.hasPermission(Constants.PERMISSION_WRITE)) {
			links.setEnabled(false);
			markIndexable.setEnabled(false);
			markUnindexable.setEnabled(false);
			indexSelection.setEnabled(false);
		}

		for (GUIDocument record : selection) {
			if (record.getIndexed() == Constants.INDEX_SKIP) {
				indexSelection.setEnabled(false);
				break;
			}
		}

		if (selection.length != 1 || Constants.DOC_CHECKED_OUT != selection[0].getStatus())
			checkin.setEnabled(false);

		if (selection != null && selection.length == 1) {
			Long lockUser = selection[0].getLockUserId();
			if (lockUser != null && Session.get().getUser().getId() != lockUser.longValue()
					&& !Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
				checkin.setEnabled(false);
		}

		unlockItem.setEnabled(enableUnlock);
		lock.setEnabled(enableLock);
		checkout.setEnabled(enableLock);
		immutable.setEnabled(enableImmutable);
		delete.setEnabled(enableDelete);

		if (selection.length == 1 && selection[0].getStatus() < 0) {
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
		}

		if (!folder.isWrite()) {
			checkin.setEnabled(false);
			checkout.setEnabled(false);
		}

		final GUIExternalCall extCall = Session.get().getSession().getExternalCall();
		if (extCall != null) {
			externalCall.setTitle(extCall.getName());
			externalCall.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
				public void onClick(MenuItemClickEvent event) {
					List<Long> ids = new ArrayList<Long>();
					List<String> titles = new ArrayList<String>();
					for (GUIDocument record : selection) {
						ids.add(record.getId());
						titles.add(record.getTitle());
					}

					WindowUtils.openUrl(extCall.getUrl(true, ids.toArray(new Long[0]), titles.toArray(new String[0])),
							extCall.getTargetWindow() != null ? extCall.getTargetWindow() : "_blank", null);
				}
			});
		}

		setItems(download, preview, cut, copy, delete, bookmark, sendMail, links, checkout, checkin, lock, unlockItem);
		
		if (Feature.visible(Feature.ARCHIVING)) {
			addItem(archive);
			if (!folder.hasPermission(Constants.PERMISSION_ARCHIVE) || !Feature.enabled(Feature.ARCHIVING))
				archive.setEnabled(false);
			else
				archive.setEnabled(true);
		}

		if (Feature.enabled(Feature.EXTERNAL_CALL) && extCall != null)
			addItem(externalCall);

		addItem(more);

		Menu moreMenu = new Menu();
		moreMenu.setItems(indexSelection, markIndexable, markUnindexable, immutable);

		if (enableSign && Feature.visible(Feature.DIGITAL_SIGN)) {
			moreMenu.addItem(sign);
			if (!folder.hasPermission(Constants.PERMISSION_SIGN) || !Feature.enabled(Feature.DIGITAL_SIGN))
				sign.setEnabled(false);
			else
				sign.setEnabled(enableSign && selection.length > 0);
		}
		
		if (enableStamp && Feature.visible(Feature.STAMP)) {
			moreMenu.addItem(stamp);
			if (!folder.hasPermission(Constants.PERMISSION_WRITE) || !Feature.enabled(Feature.STAMP))
				stamp.setEnabled(false);
			else
				stamp.setEnabled(enableStamp && selection.length > 0);
		}

		if (Feature.visible(Feature.IMPEX)) {
			moreMenu.addItem(sendToExpArchive);
			if (!folder.hasPermission(Constants.PERMISSION_EXPORT) || !Feature.enabled(Feature.IMPEX))
				sendToExpArchive.setEnabled(false);
			else
				sendToExpArchive.setEnabled(true);
		}

		if (Feature.visible(Feature.WORKFLOW)) {
			moreMenu.addItem(startWorkflow);
			if (!folder.hasPermission(Constants.PERMISSION_WORKFLOW) || !Feature.enabled(Feature.WORKFLOW)) {
				startWorkflow.setEnabled(false);
			} else {
				startWorkflow.setEnabled(true);
			}
		}

		more.setSubmenu(moreMenu);
	}
}