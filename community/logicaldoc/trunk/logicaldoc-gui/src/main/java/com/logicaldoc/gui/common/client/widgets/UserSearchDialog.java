package com.logicaldoc.gui.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This is a form used for quick user selection
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.6
 */
public class UserSearchDialog extends Window {
	private ValuesManager vm = new ValuesManager();

	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ListGrid grid = new ListGrid();

	private UserSelector selector;

	private ListGridRecord[] lastResult = new ListGridRecord[0];

	public UserSearchDialog(UserSelector selector) {
		this.selector = selector;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("users"));
		setWidth(450);
		setHeight(300);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);
		setMembersMargin(3);

		VLayout formPanel = new VLayout();
		formPanel.setWidth100();
		formPanel.setHeight(100);

		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(4);
		form.setWidth100();

		TextItem username = ItemFactory.newTextItem("username", "username", null);
		SelectItem group = ItemFactory.newGroupSelector("group", "group");

		form.setItems(username, group);

		IButton search = new IButton(I18N.message("search"));
		search.setAutoFit(true);
		search.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				search(vm.getValueAsString("username"), vm.getValueAsString("group"));
			}
		});

		formPanel.setMembers(form, search);

		grid.setWidth100();
		grid.setHeight100();
		grid.setMinHeight(250);
		ListGridField usernameField = new ListGridField("username", I18N.message("username"));
		ListGridField nameField = new ListGridField("firstname", I18N.message("firstname"));
		ListGridField lastnameField = new ListGridField("lastname", I18N.message("lastname"));
		grid.setFields(usernameField, nameField, lastnameField);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		grid.setAutoFetchData(true);
		grid.setWrapCells(false);
		grid.setData(lastResult);

		grid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				ListGridRecord selection = grid.getSelectedRecord();
				onSelect(selection.getAttributeAsLong("id"));
			}
		});

		addItem(formPanel);
		addItem(grid);
	}

	protected void search(String username, String groupId) {
		service.searchUsers(Session.get().getSid(), username, groupId, new AsyncCallback<GUIUser[]>() {
			@Override
			public void onFailure(Throwable caught) {
				ContactingServer.get().hide();
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIUser[] result) {
				lastResult = new ListGridRecord[result.length];
				for (int i = 0; i < result.length; i++) {
					GUIUser hit = result[i];
					ListGridRecord record = new ListGridRecord();
					lastResult[i] = record;
					record.setAttribute("id", hit.getId());
					record.setAttribute("username", hit.getUserName());
					record.setAttribute("firstname", hit.getFirstName());
					record.setAttribute("lastname", hit.getName());
				}

				if (lastResult.length == 1) {
					onSelect(lastResult[0].getAttributeAsLong("id"));
				} else
					grid.setData(lastResult);
			}
		});
	}

	public ListGridRecord[] getLastResult() {
		return lastResult;
	}

	public void onSelect(long id) {
		selector.setValue(Long.toString(id));
		destroy();
	}
}