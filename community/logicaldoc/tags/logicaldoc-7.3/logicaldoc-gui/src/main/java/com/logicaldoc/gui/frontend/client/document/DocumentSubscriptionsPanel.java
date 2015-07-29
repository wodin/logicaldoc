package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.SubscriptionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.AuditService;
import com.logicaldoc.gui.frontend.client.services.AuditServiceAsync;
import com.logicaldoc.gui.frontend.client.subscription.SubscriptionDialog;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the subscriptions on a document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.3
 */
public class DocumentSubscriptionsPanel extends DocumentDetailTab {

	private ListGrid list;

	private AuditServiceAsync service = (AuditServiceAsync) GWT.create(AuditService.class);

	private VLayout container = new VLayout();

	public DocumentSubscriptionsPanel(final GUIDocument document) {
		super(document, null, null);
		addMember(container);
		refresh();
	}

	private void refreshList() {
		if (list != null)
			container.removeMember(list);

		ListGridField userId = new ListGridField("userId", "userId", 50);
		userId.setCanEdit(false);
		userId.setHidden(true);

		ListGridField userName = new ListGridField("userName", I18N.message("user"), 200);
		userName.setCanEdit(false);

		ListGridField created = new ListGridField("created", I18N.message("subscription"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter(false));
		created.setCanFilter(false);

		ListGridField events = new ListGridField("events", I18N.message("notifyon"));
		events.setWidth("*");
		events.setCanEdit(false);
		events.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				try {
					if (value != null && !value.toString().isEmpty()) {
						// Translate the set of events
						String[] key = null;

						if (!value.toString().contains(","))
							key = new String[] { value.toString().trim() };
						else
							key = value.toString().split(",");
						List<String> labels = new ArrayList<String>();
						for (String string : key) {
							labels.add(I18N.message(string + ".short"));
						}
						String str = labels.toString().substring(1);
						return str.substring(0, str.length() - 1);
					} else
						return "";
				} catch (Throwable e) {
					return "";
				}
			}
		});

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		list.setAutoFetchData(true);
		list.setDataSource(new SubscriptionsDS(null, document.getId()));
		list.setFields(userId, userName, created, events);
		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		container.addMember(list, 0);
	}

	void refresh() {
		container.removeMembers(container.getMembers());

		refreshList();

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(4);
		buttons.setWidth100();
		buttons.setHeight(20);
		container.addMember(buttons);

		// Prepare the combo for adding a new Group
		final DynamicForm groupForm = new DynamicForm();
		final SelectItem group = ItemFactory.newGroupSelector("group", "addgroup");
		groupForm.setItems(group);
		buttons.addMember(groupForm);

		group.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord selectedRecord = group.getSelectedRecord();
				if (selectedRecord == null)
					return;
				long groupId = Long.parseLong(selectedRecord.getAttributeAsString("id"));
				service.subscribeDocuments(Session.get().getSid(), new long[] { document.getId() },
						Constants.AUDIT_DEFAULT_EVENTS, null, groupId, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void arg0) {
								refreshList();
							}
						});
			}
		});

		final DynamicForm userForm = new DynamicForm();
		final SelectItem user = ItemFactory.newUserSelector("user", "adduser", null, true);
		userForm.setItems(user);

		user.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord selectedRecord = user.getSelectedRecord();
				if (selectedRecord == null)
					return;
				long userId = Long.parseLong(selectedRecord.getAttributeAsString("id"));
				service.subscribeDocuments(Session.get().getSid(), new long[] { document.getId() },
						Constants.AUDIT_DEFAULT_EVENTS, userId, null, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void arg0) {
								refreshList();
							}
						});
			}
		});

		buttons.addMember(userForm);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord[] selection = list.getSelectedRecords();
		if (selection == null || selection.length == 0)
			return;
		final long[] ids = new long[selection.length];
		for (int i = 0; i < selection.length; i++) {
			ids[i] = Long.parseLong(selection[i].getAttribute("id"));
		}

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.deleteSubscriptions(Session.get().getSid(), ids, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.removeSelectedData();
									list.deselectAllRecords();
								}
							});
						}
					}
				});
			}
		});

		MenuItem edit = new MenuItem();
		edit.setTitle(I18N.message("edit"));
		edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SubscriptionDialog dialog = new SubscriptionDialog(list);
				dialog.show();
			}
		});

		contextMenu.setItems(edit, delete);
		contextMenu.showContextMenu();
	}
}