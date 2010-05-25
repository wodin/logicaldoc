package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
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

	public DocumentContextMenu(final GUIFolder folder, final ListGrid list) {
		final ListGridRecord[] selection = list.getSelection();

		MenuItem download = new MenuItem();
		download.setTitle(I18N.getMessage("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open("download?sid=" + Session.get().getSid() + "&sid=" + Session.get().getSid() + "&docId="
						+ id, "_self", "");
			}
		});

		MenuItem copy = new MenuItem();
		copy.setTitle(I18N.getMessage("copy"));
		copy.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
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

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.getMessage("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				SC.ask(I18N.getMessage("question"), I18N.getMessage("confirmdelete"), new BooleanCallback() {
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
								}
							});
						}
					}
				});
			}
		});

		MenuItem sendMail = new MenuItem();
		sendMail.setTitle(I18N.getMessage("sendmail"));
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
		similar.setTitle(I18N.getMessage("similardocuments"));
		similar.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				if (record == null)
					return;
				// TODO implement
				SC.warn("To be Implemented");
			}
		});

		MenuItem links = new MenuItem();
		links.setTitle(I18N.getMessage("connectaslinks"));
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
		immutable.setTitle(I18N.getMessage("makeimmutable"));
		immutable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				Dialog dialogProperties = new Dialog();
				SC.askforValue(I18N.getMessage("warning"), I18N.getMessage("immutableadvice"), "", new ValueCallback() {

					@Override
					public void execute(String value) {
						if (value == null)
							return;

						if (value.isEmpty())
							SC.warn(I18N.getMessage("commentrequired"));
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
		lock.setTitle(I18N.getMessage("lock"));
		lock.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				if (selection == null)
					return;
				final long[] ids = new long[selection.length];
				for (int j = 0; j < selection.length; j++) {
					ids[j] = Long.parseLong(selection[j].getAttribute("id"));
				}

				Dialog dialogProperties = new Dialog();
				SC.askforValue(I18N.getMessage("warning"), I18N.getMessage("lockadvice"), "", new ValueCallback() {

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
									Session.get().getUser().setLockedDocs(
											Session.get().getUser().getLockedDocs() + ids.length);
								}
							});
					}

				}, dialogProperties);
			}
		});

		MenuItem unlockItem = new MenuItem();
		unlockItem.setTitle(I18N.getMessage("unlock"));
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
		checkout.setTitle(I18N.getMessage("checkout"));
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
						Window.open("../download?sid=" + Session.get().getSid() + "&docId=" + id, "_blank", "");
					}
				});
			}
		});

		MenuItem checkin = new MenuItem();
		checkin.setTitle(I18N.getMessage("checkin"));
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
		bookmark.setTitle(I18N.getMessage("bookmark"));
		bookmark.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttributeAsString("id"));
				}
			}
		});

		MenuItem markUnindexable = new MenuItem();
		markUnindexable.setTitle(I18N.getMessage("markunindexable"));
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
		markIndexable.setTitle(I18N.getMessage("markindexable"));
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

		boolean enableLock = true;
		boolean enableUnlock = true;
		boolean enableImmutable = false;

		if (selection != null)
			for (ListGridRecord record : selection) {
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable"))) {
					enableLock = false;
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
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable")))
					enableImmutable = false;
			}
		}

		if (list.getSelection().length != 1) {
			download.setEnabled(false);
			sendMail.setEnabled(false);
			similar.setEnabled(false);
		}

		if (!folder.hasPermission(Constants.PERMISSION_DELETE))
			delete.setEnabled(false);

		if (!folder.hasPermission(Constants.PERMISSION_WRITE)) {
			links.setEnabled(false);
			markIndexable.setEnabled(false);
			markUnindexable.setEnabled(false);
		}

		if (!enableLock) {
			lock.setEnabled(false);
			checkout.setEnabled(false);
		}

		if (!enableUnlock) {
			if (selection[0].getAttribute("status") == null
					|| !(selection.length == 1 && Constants.DOC_CHECKED_OUT == Integer.parseInt(selection[0]
							.getAttribute("status"))))
				checkin.setEnabled(false);
			unlockItem.setEnabled(false);
		}

		if (!enableImmutable)
			immutable.setEnabled(false);

		setItems(download, copy, delete, bookmark, sendMail, similar, links, checkout, checkin, lock, unlockItem,
				immutable, markIndexable, markUnindexable);
	}
}