package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.data.RightsDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.FormItemType;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the links of a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityPanel extends FolderDetailTab {

	private RightsDS dataSource;

	private ListGrid list;

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private VLayout container = new VLayout();

	public SecurityPanel(final GUIFolder folder, ChangedHandler changedHandler) {
		super(folder, changedHandler);

		container.setMembersMargin(3);
		addMember(container);

		ListGridField entityId = new ListGridField("entityId", "entityId", 50);
		entityId.setCanEdit(false);
		entityId.setHidden(true);

		ListGridField entity = new ListGridField("entity", I18N.getMessage("entity"), 200);
		entity.setCanEdit(false);

		ListGridField read = new ListGridField("read", I18N.getMessage("read"), 60);
		read.setType(ListGridFieldType.BOOLEAN);
		read.setCanEdit(false);

		ListGridField write = new ListGridField("write", I18N.getMessage("write"), 60);
		write.setType(ListGridFieldType.BOOLEAN);
		write.setCanEdit(true);

		ListGridField add = new ListGridField("add", I18N.getMessage("addfolder"), 60);
		add.setType(ListGridFieldType.BOOLEAN);
		add.setCanEdit(true);

		ListGridField security = new ListGridField("security", I18N.getMessage("security"), 60);
		security.setType(ListGridFieldType.BOOLEAN);
		security.setCanEdit(true);

		ListGridField immutable = new ListGridField("immutable", I18N.getMessage("immutable"), 60);
		immutable.setType(ListGridFieldType.BOOLEAN);
		immutable.setCanEdit(true);

		ListGridField delete = new ListGridField("delete", I18N.getMessage("delete"), 60);
		delete.setType(ListGridFieldType.BOOLEAN);
		delete.setCanEdit(true);

		ListGridField rename = new ListGridField("rename", I18N.getMessage("rename"), 60);
		rename.setType(ListGridFieldType.BOOLEAN);
		rename.setCanEdit(true);

		ListGridField _import = new ListGridField("import", I18N.getMessage("import"), 60);
		_import.setType(ListGridFieldType.BOOLEAN);
		_import.setCanEdit(true);

		ListGridField export = new ListGridField("export", I18N.getMessage("export"), 60);
		export.setType(ListGridFieldType.BOOLEAN);
		export.setCanEdit(true);

		ListGridField sign = new ListGridField("sign", I18N.getMessage("sign"), 60);
		sign.setType(ListGridFieldType.BOOLEAN);
		sign.setCanEdit(true);

		ListGridField archive = new ListGridField("archive", I18N.getMessage("archive"), 60);
		archive.setType(ListGridFieldType.BOOLEAN);
		archive.setCanEdit(true);

		ListGridField workflow = new ListGridField("workflow", I18N.getMessage("workflow"), 60);
		workflow.setType(ListGridFieldType.BOOLEAN);
		workflow.setCanEdit(true);

		list = new ListGrid();
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		dataSource = new RightsDS(folder.getId());
		list.setDataSource(dataSource);
		list.setFields(entityId, entity, read, write, add, security, immutable, delete, rename, _import, export, sign,
				archive, workflow);
		container.addMember(list);

		if (folder != null && folder.hasPermission(Constants.PERMISSION_SECURITY)) {
			list.setCanEdit(true);
			list.setEditEvent(ListGridEditEvent.CLICK);
			list.setModalEditing(true);
			list.addEditCompleteHandler(new EditCompleteHandler() {
				@Override
				public void onEditComplete(EditCompleteEvent event) {
					SecurityPanel.this.changedHandler.onChanged(null);
				}
			});
			list.addCellContextClickHandler(new CellContextClickHandler() {
				@Override
				public void onCellContextClick(CellContextClickEvent event) {
					Menu contextMenu = setupContextMenu();
					contextMenu.showContextMenu();
					event.cancel();
				}
			});
		}

		HLayout buttons = new HLayout();
		container.addMember(buttons);

		Button applyToSubfolders = new Button(I18N.getMessage("applytosubfolders"));
		buttons.addMember(applyToSubfolders);
		buttons.setMembersMargin(4);
		buttons.setWidth100();
		buttons.setHeight(20);
		applyToSubfolders.setWidth(200);
		applyToSubfolders.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				folderService.applyRightsToTree(Session.get().getSid(), folder.getId(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {

					}
				});
			}
		});

		buttons.addMember(new HTML("<span style='width: 10px' />"));

		// Prepare some items for the combo-boxes
		ListGridField name = new ListGridField("name");
		ListGridField description = new ListGridField("description");
		ListGridField username = new ListGridField("username");
		ListGridField label = new ListGridField("label");

		// Prepare the combo and button for adding a new Group
		final DynamicForm groupForm = new DynamicForm();
		final ComboBoxItem group = new ComboBoxItem("group");
		group.setTitle(I18N.getMessage("group"));
		group.setValueField("id");
		group.setDisplayField("name");
		group.setPickListWidth(300);
		group.setPickListFields(name, description);
		group.setOptionDataSource(GroupsDS.getInstance());
		groupForm.setItems(group);

		buttons.addMember(groupForm);
		Button addGroup = new Button(I18N.getMessage("addgroup"));
		buttons.addMember(addGroup);
		addGroup.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = group.getSelectedRecord();
				if (selectedRecord == null)
					return;

				// Check if the selected user is already present in the rights
				// table
				ListGridRecord[] records = list.getRecords();
				for (ListGridRecord test : records) {
					if (test.getAttribute("entityId").equals(selectedRecord.getAttribute("id"))) {
						return;
					}
				}

				// Update the rights table
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("entityId", selectedRecord.getAttribute("id"));
				record.setAttribute("entity", "Group: " + selectedRecord.getAttribute("name"));
				record.setAttribute("read", true);
				list.addData(record);
				SecurityPanel.this.changedHandler.onChanged(null);
			}
		});

		final DynamicForm userForm = new DynamicForm();
		final ComboBoxItem user = new ComboBoxItem("user");
		user.setTitle(I18N.getMessage("user"));
		user.setType(FormItemType.COMBOBOX_ITEM.getValue());
		user.setValueField("id");
		user.setDisplayField("username");
		user.setPickListWidth(300);
		user.setPickListFields(username, label);
		user.setOptionDataSource(UsersDS.get());
		userForm.setItems(user);

		buttons.addMember(userForm);
		Button addUser = new Button(I18N.getMessage("adduser"));
		buttons.addMember(addUser);
		addUser.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = user.getSelectedRecord();
				if (selectedRecord == null)
					return;

				// Check if the selected user is already present in the rights
				// table
				ListGridRecord[] records = list.getRecords();
				for (ListGridRecord test : records) {
					if (test.getAttribute("entityId").equals(selectedRecord.getAttribute("groupId"))) {
						return;
					}
				}

				// Update the rights table
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("entityId", selectedRecord.getAttribute("groupId"));
				record.setAttribute("entity", "User: " + selectedRecord.getAttribute("label") + " ("
						+ selectedRecord.getAttribute("username") + ")");
				record.setAttribute("read", true);
				list.addData(record);
				SecurityPanel.this.changedHandler.onChanged(null);
			}
		});
	}

	/**
	 * Create an array of all right defined
	 */
	public GUIRight[] getRights() {
		ArrayList<GUIRight> tmp = new ArrayList<GUIRight>();
		ListGridRecord[] records = list.getRecords();

		for (ListGridRecord record : records) {
			GUIRight right = new GUIRight();
			right.setEntityId(Long.parseLong(record.getAttribute("entityId")));
			right.setWrite(record.getAttributeAsBoolean("write"));
			right.setDelete(record.getAttributeAsBoolean("delete"));
			right.setAdd(record.getAttributeAsBoolean("add"));
			right.setWorkflow(record.getAttributeAsBoolean("workflow"));
			right.setSign(record.getAttributeAsBoolean("sign"));
			right.setImport(record.getAttributeAsBoolean("import"));
			right.setExport(record.getAttributeAsBoolean("export"));
			right.setImmutable(record.getAttributeAsBoolean("immutable"));
			right.setRename(record.getAttributeAsBoolean("rename"));
			right.setSecurity(record.getAttributeAsBoolean("security"));
			right.setArchive(record.getAttributeAsBoolean("archive"));
			tmp.add(right);
		}

		return tmp.toArray(new GUIRight[0]);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem deleteItem = new MenuItem();
		deleteItem.setTitle(I18N.getMessage("delete"));
		deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;

				SC.ask(I18N.getMessage("question"), I18N.getMessage("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							list.removeSelectedData();
							SecurityPanel.this.changedHandler.onChanged(null);
						}
					}
				});
			}
		});

		contextMenu.setItems(deleteItem);
		return contextMenu;
	}
}