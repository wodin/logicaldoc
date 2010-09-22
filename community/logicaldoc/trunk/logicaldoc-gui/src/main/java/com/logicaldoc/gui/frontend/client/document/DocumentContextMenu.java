package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.dashboard.WorkflowDashboard;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.Search;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDetailsDialog;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Dialog;
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

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private SearchServiceAsync searchService = (SearchServiceAsync) GWT.create(SearchService.class);

	private WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	public DocumentContextMenu(final GUIFolder folder, final ListGrid list) {
		final ListGridRecord[] selection = list.getSelection();

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&sid="
						+ Session.get().getSid() + "&docId=" + id, "_self", "");
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
					String id = selection[i].getAttribute("id");
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
					String id = selection[i].getAttribute("id");
					GUIDocument document = new GUIDocument();
					document.setId(Long.parseLong(id));
					document.setTitle(selection[i].getAttribute("title"));
					document.setIcon(selection[i].getAttribute("icon"));
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
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
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
									for (ListGridRecord record : selection) {
										TrashPanel.get().appendRecord(record);
									}
									list.removeSelectedData();
									DocumentsPanel.get().showFolderDetails();
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
				ListGridRecord record = list.getSelectedRecord();
				if (record == null)
					return;
				EmailDialog window = new EmailDialog(Long.parseLong(record.getAttribute("id")), record
						.getAttribute("title"));
				window.show();
			}
		});

		MenuItem similar = new MenuItem();
		similar.setTitle(I18N.message("similardocuments"));
		similar.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				if (record == null)
					return;
				Long id = Long.parseLong(record.getAttribute("id"));
				searchService.getSimilarityOptions(Session.get().getSid(), id, I18N.getLocale(),
						new AsyncCallback<GUISearchOptions>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUISearchOptions options) {
								Search.get().setOptions(options);
								Search.get().search();
							}
						});
			}
		});

		MenuItem links = new MenuItem();
		links.setTitle(I18N.message("connectaslinks"));
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
						// Nothing to do
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

				Dialog dialogProperties = new Dialog();
				SC.askforValue(I18N.message("warning"), I18N.message("immutableadvice"), "", new ValueCallback() {

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
												list.refreshRow(list.getRecordIndex(record));
											}
										}
									});
					}

				}, dialogProperties);
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

				Dialog dialogProperties = new Dialog();
				SC.askforValue(I18N.message("warning"), I18N.message("lockadvice"), "", new ValueCallback() {

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
										record.setAttribute("locked", "document_lock");
										record.setAttribute("lockUserId", Session.get().getUser().getId());
										list.refreshRow(list.getRecordIndex(record));
									}
									Session.get().getUser()
											.setLockedDocs(Session.get().getUser().getLockedDocs() + ids.length);
								}
							});
					}

				}, dialogProperties);
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
							list.refreshRow(list.getRecordIndex(record));
						}
						Session.get().getUser().setLockedDocs(Session.get().getUser().getLockedDocs() - ids.length);
					}
				});
			}
		});

		MenuItem checkout = new MenuItem();
		checkout.setTitle(I18N.message("checkout"));
		checkout.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				if (record == null)
					return;
				final long id = Long.parseLong(record.getAttribute("id"));
				documentService.checkout(Session.get().getSid(), id, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						ListGridRecord record = list.getSelectedRecord();
						record.setAttribute("locked", "page_edit");
						record.setAttribute("lockUserId", Session.get().getUser().getId());
						record.setAttribute("status", Constants.DOC_CHECKED_OUT);
						list.refreshRow(list.getRecordIndex(record));
						Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() + 1);
						Window.open(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
								+ id, "_blank", "");
					}
				});
			}
		});

		MenuItem checkin = new MenuItem();
		checkin.setTitle(I18N.message("checkin"));
		checkin.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = list.getSelectedRecord();
				if (selection == null)
					return;
				long id = Long.parseLong(selection.getAttribute("id"));
				String filename = selection.getAttributeAsString("filename");
				DocumentCheckin checkin = new DocumentCheckin(id, filename, list);
				checkin.show();
			}
		});

		MenuItem bookmark = new MenuItem();
		bookmark.setTitle(I18N.message("bookmark"));
		bookmark.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttributeAsString("id"));
				}
				documentService.addBookmarks(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						// DO NOTHING
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
							list.refreshRow(list.getRecordIndex(record));
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
							list.refreshRow(list.getRecordIndex(record));
						}
					}
				});
			}
		});

		MenuItem sign = new MenuItem();
		sign.setTitle(I18N.message("sign"));
		sign.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				String ids = "";
				String names = "";
				for (ListGridRecord rec : selection) {
					ids += "," + rec.getAttributeAsString("id");
					names += "," + rec.getAttributeAsString("title");
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);
				if (names.startsWith(","))
					names = names.substring(1);

				SignDialog dialog = new SignDialog(ids, names, false);
				dialog.show();
			}
		});

		MenuItem archive = new MenuItem(I18N.message("sendtoarchive"));
		archive.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				ArchiveDialog archiveDialog = new ArchiveDialog(ids, GUIArchive.TYPE_DEFAULT);
				archiveDialog.show();
			}
		});

		MenuItem archiveDematerialization = new MenuItem(I18N.message("sendtostoragearchive"));
		archiveDematerialization.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				ArchiveDialog archiveDialog = new ArchiveDialog(ids, GUIArchive.TYPE_STORAGE);
				archiveDialog.show();

			}
		});

		MenuItem edit = new MenuItem();
		edit.setTitle(I18N.message("editonline"));
		edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = list.getSelectedRecord();
				if (selection == null)
					return;
				long id = Long.parseLong(selection.getAttribute("id"));
				Window.open("ldedit:" + GWT.getHostPageBaseURL() + "download?action=edit&sid=" + Session.get().getSid()
						+ "&docId=" + id, "_self", "");
			}
		});

		MenuItem startWorkflow = new MenuItem(I18N.message("startworkflow"));
		startWorkflow.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;

				String ids = "";
				for (ListGridRecord rec : selection) {
					ids += "," + rec.getAttributeAsString("id");
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);

				WorkflowDialog workflowDialog = new WorkflowDialog(ids);
				workflowDialog.show();
			}
		});

		MenuItem addToWorkflow = new MenuItem(I18N.message("addtoworkflow"));
		addToWorkflow.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;

				String ids = "";
				for (ListGridRecord rec : selection) {
					ids += "," + rec.getAttributeAsString("id");
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);

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

		MenuItem more = new MenuItem(I18N.message("more"));

		boolean enableLock = true;
		boolean enableUnlock = true;
		boolean enableImmutable = false;
		boolean enableDelete = true;
		boolean enableSign = selection != null && selection.length > 0;

		boolean isOfficeFile = false;
		if (selection[0].getAttribute("filename") != null)
			isOfficeFile = Util.isOfficeFile(selection[0].getAttribute("filename"));
		else if (selection[0].getAttribute("type") != null)
			isOfficeFile = Util.isOfficeFileType(selection[0].getAttribute("type"));
		boolean enableEdit = selection != null && selection.length == 1 && isOfficeFile;

		if (selection != null)
			for (ListGridRecord record : selection) {
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
					enableLock = false;
					cut.setEnabled(false);
				}
				if ("blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
					Long lockUser = record.getAttribute("lockUserId") != null ? Long.parseLong(record
							.getAttribute("lockUserId")) : Long.MIN_VALUE;
					if (Session.get().getUser().getId() == lockUser.longValue()
							|| Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
						enableUnlock = false;
				}
			}

		if (folder.hasPermission(Constants.PERMISSION_IMMUTABLE)) {
			enableImmutable = true;
			for (ListGridRecord record : selection) {
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
					enableImmutable = false;
				}
			}
		}

		if (list.getSelection().length != 1) {
			download.setEnabled(false);
			sendMail.setEnabled(false);
			similar.setEnabled(false);
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
		}

		if (!enableUnlock) {
			if (selection[0].getAttribute("status") == null
					|| !(selection.length == 1 && Constants.DOC_CHECKED_OUT == Integer.parseInt(selection[0]
							.getAttribute("status"))))
				checkin.setEnabled(false);
		}

		unlockItem.setEnabled(enableUnlock);
		lock.setEnabled(enableLock);
		checkout.setEnabled(enableLock);
		immutable.setEnabled(enableImmutable);
		delete.setEnabled(enableDelete);

		setItems(download, cut, copy, delete, bookmark, sendMail, links, checkout, checkin, lock, unlockItem, more);

		Menu moreMenu = new Menu();
		moreMenu.setItems(similar, immutable, markIndexable, markUnindexable);

		if (Feature.visible(Feature.OFFICE)) {
			moreMenu.addItem(edit);
			if (!Feature.enabled(Feature.OFFICE))
				edit.setEnabled(false);
			else
				edit.setEnabled(enableEdit);
		}

		if (Feature.visible(Feature.DIGITAL_SIGN)) {
			moreMenu.addItem(sign);
			if (!folder.hasPermission(Constants.PERMISSION_SIGN) || !Feature.enabled(Feature.DIGITAL_SIGN))
				sign.setEnabled(false);
			else
				sign.setEnabled(enableSign);
		}

		if (Feature.visible(Feature.ARCHIVES)) {
			moreMenu.addItem(archive);
			if (!folder.hasPermission(Constants.PERMISSION_ARCHIVE) || !Feature.enabled(Feature.ARCHIVES))
				archive.setEnabled(false);
			else
				archive.setEnabled(enableSign);
		}

		if (Feature.visible(Feature.PAPER_DEMATERIALIZATION)) {
			moreMenu.addItem(archiveDematerialization);
			if (!folder.hasPermission(Constants.PERMISSION_ARCHIVE)
					|| !Feature.enabled(Feature.PAPER_DEMATERIALIZATION))
				archiveDematerialization.setEnabled(false);
			else
				archiveDematerialization.setEnabled(enableSign);
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