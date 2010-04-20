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
import com.logicaldoc.gui.common.client.util.DateCellFormatter;
import com.logicaldoc.gui.common.client.util.FileSizeCellFormatter;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.data.DocumentsDS;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows a list of documents in a tabular way.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsListPanel extends HLayout {
	private DocumentsDS dataSource;

	private ListGrid list;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public DocumentsListPanel(GUIFolder folder) {
		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.getMessage("title"), 200);

		ListGridField size = new ListGridField("size", I18N.getMessage("size"), 70);
		size.setAlign(Alignment.CENTER);
		size.setType(ListGridFieldType.FLOAT);
		size.setCellFormatter(new FileSizeCellFormatter());

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix() + "/application/");
		icon.setImageURLSuffix(".png");

		ListGridField version = new ListGridField("version", I18N.getMessage("version"), 55);
		version.setAlign(Alignment.CENTER);

		ListGridField lastModified = new ListGridField("lastModified", I18N.getMessage("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter());

		ListGridField publisher = new ListGridField("publisher", I18N.getMessage("publisher"), 90);
		publisher.setAlign(Alignment.CENTER);

		ListGridField published = new ListGridField("published", I18N.getMessage("publishedon"), 110);
		published.setAlign(Alignment.CENTER);
		published.setType(ListGridFieldType.DATE);
		published.setCellFormatter(new DateCellFormatter());

		ListGridField creator = new ListGridField("creator", I18N.getMessage("creator"), 90);
		creator.setAlign(Alignment.CENTER);

		ListGridField created = new ListGridField("created", I18N.getMessage("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter());

		ListGridField customId = new ListGridField("customId", I18N.getMessage("customid"), 110);

		ListGridField immutable = new ListGridField("immutable", " ", 24);
		immutable.setType(ListGridFieldType.IMAGE);
		immutable.setCanSort(false);
		immutable.setAlign(Alignment.CENTER);
		immutable.setShowDefaultContextMenu(false);
		immutable.setImageURLPrefix(Util.imagePrefix() + "/application/");
		immutable.setImageURLSuffix(".png");

		ListGridField indexed = new ListGridField("indexed", " ", 24);
		indexed.setType(ListGridFieldType.IMAGE);
		indexed.setCanSort(false);
		indexed.setAlign(Alignment.CENTER);
		indexed.setShowDefaultContextMenu(false);
		indexed.setImageURLPrefix(Util.imagePrefix() + "/application/");
		indexed.setImageURLSuffix(".png");

		ListGridField locked = new ListGridField("locked", " ", 24);
		locked.setType(ListGridFieldType.IMAGE);
		locked.setCanSort(false);
		locked.setAlign(Alignment.CENTER);
		locked.setShowDefaultContextMenu(false);
		locked.setImageURLPrefix(Util.imagePrefix() + "/application/");
		locked.setImageURLSuffix(".png");

		ListGridField filename = new ListGridField("filename", I18N.getMessage("filename"), 200);
		filename.setHidden(true);

		ListGridField lockUserId = new ListGridField("lockUserId", " ", 24);
		lockUserId.setHidden(true);

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
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		dataSource = new DocumentsDS(folder.getId());
		list.setDataSource(dataSource);
		list.setFields(indexed, locked, immutable, icon, title, size, lastModified, version, publisher, published,
				creator, created, customId, filename);
		addMember(list);

		list.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				if ("indexed".equals(list.getFieldName(event.getColNum()))) {
					ListGridRecord record = event.getRecord();
					if ("indexed".equals(record.getAttribute("indexed"))) {
						String id = list.getSelectedRecord().getAttribute("id");
						Window.open("download?sid=" + Session.get().getSid() + "&docId=" + id
								+ "&downloadText=true", "_self", "");
					}
				}
			}

		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				DocumentsPanel.get().onSelectedDocument(Long.parseLong(event.getRecord().getAttribute("id")));
			}
		});

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = setupContextMenu(Session.get().getCurrentFolder());
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		list.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open("download?sid=" + Session.get().getSid() + "&docId=" + id + "&open=true", "_blank",
						"");
			}
		});
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu(GUIFolder folder) {
		Menu contextMenu = new Menu();
		List<MenuItem> items = new ArrayList<MenuItem>();
		MenuItem downloadItem = new MenuItem();
		downloadItem.setTitle(I18N.getMessage("download"));
		downloadItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				String id = list.getSelectedRecord().getAttribute("id");
				Window.open("download?sid=" + Session.get().getSid() + "&sid=" + Session.get().getSid()
						+ "&docId=" + id, "_self", "");
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
				EmailWindow window = new EmailWindow(Long.parseLong(selection.getAttribute("id")), selection
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
			for (ListGridRecord record : selection) {
				if (!record.getAttribute("locked").equals("blank") || !record.getAttribute("immutable").equals("blank")) {
					enableLock = false;
				}
				if (record.getAttribute("locked").equals("blank") || !record.getAttribute("immutable").equals("blank")) {
					Long lockUser = record.getAttribute("lockUserId") != null ? Long.parseLong(record
							.getAttribute("lockUserId")) : Long.MIN_VALUE;
					if (Session.get().getUser().getId() == lockUser.longValue()
							|| Session.get().getUser().getUserName().equals("admin"))
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
							Window.open("../download?sid=" + Session.get().getSid() + "&docId=" + id, "_blank",
									"");
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
					DocumentCheckin checkin = new DocumentCheckin(id, filename, DocumentsListPanel.this);
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
				if (!record.getAttribute("locked").equals("blank") || !record.getAttribute("immutable").equals("blank"))
					enableImmutable = false;
			}

			if (enableImmutable)
				items.add(immutableItem);
		}

		contextMenu.setItems(items.toArray(new MenuItem[0]));

		return contextMenu;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateSelectedRecord(GUIDocument document) {
		ListGridRecord selectedRecord = list.getSelectedRecord();
		if (selectedRecord != null) {
			selectedRecord.setAttribute("title", document.getTitle());
			selectedRecord.setAttribute("customId", document.getCustomId());
			selectedRecord.setAttribute("version", document.getVersion());
			selectedRecord.setAttribute("size", document.getSize());
			selectedRecord.setAttribute("lastModified", document.getLastModified());
			selectedRecord.setAttribute("publisher", document.getPublisher());
			selectedRecord.setAttribute("published", document.getDate());
			selectedRecord.setAttribute("creator", document.getCreator());
			selectedRecord.setAttribute("created", document.getCreation());
			list.updateData(selectedRecord);
		}
	}

	/**
	 * Marks the currently selected record as checked-in
	 */
	void markAsCheckedIn() {
		ListGridRecord selection = list.getSelectedRecord();
		if (selection == null)
			return;
		selection.setAttribute("locked", "blank");
		selection.setAttribute("status", Constants.DOC_UNLOCKED);
		list.refreshRow(list.getRecordIndex(selection));
	}
}