package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.frontend.client.Log;
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
		List<MenuItem> items = new ArrayList<MenuItem>();
		MenuItem downloadItem = new MenuItem();
		downloadItem.setTitle(I18N.getMessage("download"));
		downloadItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open("download?sid=" + Session.get().getSid() + "&sid=" + Session.get().getSid() + "&docId="
						+ id, "_self", "");
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

		MenuItem deleteItem = new MenuItem();
		deleteItem.setTitle(I18N.getMessage("delete"));
		deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelection();
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
									ListGridRecord[] records = list.getSelection();
									for (ListGridRecord record : records) {
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

		MenuItem sendMailItem = new MenuItem();
		sendMailItem.setTitle(I18N.getMessage("sendmail"));
		sendMailItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = list.getSelectedRecord();
				if (selection == null)
					return;
				EmailDialog window = new EmailDialog(Long.parseLong(selection.getAttribute("id")), selection
						.getAttribute("title"));
				window.show();
			}
		});

		MenuItem similarItem = new MenuItem();
		similarItem.setTitle(I18N.getMessage("similardocuments"));
		similarItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord selection = list.getSelectedRecord();
				if (selection == null)
					return;
				// TODO implement
				SC.warn("To be Implemented");
			}
		});

		MenuItem linksItem = new MenuItem();
		linksItem.setTitle(I18N.getMessage("connectaslinks"));
		linksItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelection();
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

		MenuItem immutableItem = new MenuItem();
		immutableItem.setTitle(I18N.getMessage("makeimmutable"));
		immutableItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord[] selection = list.getSelection();
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

		MenuItem lockItem = new MenuItem();
		lockItem.setTitle(I18N.getMessage("lock"));
		lockItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord[] selection = list.getSelection();
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
				final ListGridRecord[] selection = list.getSelection();
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
					}
				});
			}
		});

		if (list.getSelection().length == 1)
			items.add(downloadItem);

		items.add(copyItem);
		if (folder.hasPermission(Constants.PERMISSION_DELETE))
			items.add(deleteItem);

		if (list.getSelection().length == 1) {
			items.add(sendMailItem);
			items.add(similarItem);
		}

		if (folder.hasPermission(Constants.PERMISSION_WRITE)) {
			items.add(linksItem);

			boolean enableLock = true;
			boolean enableUnlock = true;

			ListGridRecord[] selection = list.getSelection();
			if (selection != null)
				for (ListGridRecord record : selection) {
					if (!"blank".equals(record.getAttribute("locked"))
							|| !"blank".equals(record.getAttribute("immutable"))) {
						enableLock = false;
					}
					if ("blank".equals(record.getAttribute("locked"))
							|| !"blank".equals(record.getAttribute("immutable"))) {
						Long lockUser = record.getAttribute("lockUserId") != null ? Long.parseLong(record
								.getAttribute("lockUserId")) : Long.MIN_VALUE;
						if (Session.get().getUser().getId() == lockUser.longValue()
								|| Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
							enableUnlock = false;
					}
				}

			MenuItem checkoutItem = new MenuItem();
			checkoutItem.setTitle(I18N.getMessage("checkout"));
			checkoutItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
				public void onClick(MenuItemClickEvent event) {
					final ListGridRecord selection = list.getSelectedRecord();
					if (selection == null)
						return;
					final long id = Long.parseLong(selection.getAttribute("id"));
					documentService.checkout(Session.get().getSid(), id, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							selection.setAttribute("locked", "document_lock");
							selection.setAttribute("lockUserId", Session.get().getUser().getId());
							selection.setAttribute("status", Constants.DOC_CHECKED_OUT);
							list.refreshRow(list.getRecordIndex(selection));
							Window.open("../download?sid=" + Session.get().getSid() + "&docId=" + id, "_blank", "");
						}
					});
				}
			});

			MenuItem checkinItem = new MenuItem();
			checkinItem.setTitle(I18N.getMessage("checkin"));
			checkinItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
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

			if (enableLock) {
				items.add(lockItem);
				items.add(checkoutItem);
			}

			if (enableUnlock) {
				if (selection.length == 1
						&& Constants.DOC_CHECKED_OUT == Integer.parseInt(selection[0].getAttribute("status")))
					items.add(checkinItem);
				items.add(unlockItem);
			}
		}

		if (folder.hasPermission(Constants.PERMISSION_IMMUTABLE)) {
			boolean enableImmutable = true;
			ListGridRecord[] selection = list.getSelection();
			for (ListGridRecord record : selection) {
				if (!"blank".equals(record.getAttribute("locked")) || !"blank".equals(record.getAttribute("immutable")))
					enableImmutable = false;
			}

			if (enableImmutable)
				items.add(immutableItem);
		}

		setItems(items.toArray(new MenuItem[0]));
	}
}
