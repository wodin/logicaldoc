package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.data.RightsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
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
 * This panel shows the security policies.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityPanel extends FolderDetailTab {

	private RightsDS dataSource;

	private ListGrid list;

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private VLayout container = new VLayout();

	public SecurityPanel(final GUIFolder folder) {
		super(folder, null);

		container.setMembersMargin(3);
		addMember(container);

		ListGridField entityId = new ListGridField("entityId", "entityId", 50);
		entityId.setCanEdit(false);
		entityId.setHidden(true);

		ListGridField entity = new ListGridField("entity", I18N.message("entity"), 200);
		entity.setCanEdit(false);

		ListGridField read = new ListGridField("read", I18N.message("read"), 60);
		read.setType(ListGridFieldType.BOOLEAN);
		read.setCanEdit(false);

		ListGridField download = new ListGridField("download", I18N.message("download"), 60);
		download.setType(ListGridFieldType.BOOLEAN);
		download.setCanEdit(true);

		ListGridField write = new ListGridField("write", I18N.message("write"), 60);
		write.setType(ListGridFieldType.BOOLEAN);
		write.setCanEdit(true);

		ListGridField add = new ListGridField("add", I18N.message("addfolder"), 60);
		add.setType(ListGridFieldType.BOOLEAN);
		add.setCanEdit(true);

		ListGridField security = new ListGridField("security", I18N.message("security"), 60);
		security.setType(ListGridFieldType.BOOLEAN);
		security.setCanEdit(true);

		ListGridField immutable = new ListGridField("immutable", I18N.message("immutable"), 60);
		immutable.setType(ListGridFieldType.BOOLEAN);
		immutable.setCanEdit(true);

		ListGridField delete = new ListGridField("delete", I18N.message("ddelete"), 60);
		delete.setType(ListGridFieldType.BOOLEAN);
		delete.setCanEdit(true);

		ListGridField rename = new ListGridField("rename", I18N.message("rename"), 60);
		rename.setType(ListGridFieldType.BOOLEAN);
		rename.setCanEdit(true);

		ListGridField _import = new ListGridField("import", I18N.message("iimport"), 60);
		_import.setType(ListGridFieldType.BOOLEAN);
		_import.setCanEdit(true);

		ListGridField export = new ListGridField("export", I18N.message("eexport"), 60);
		export.setType(ListGridFieldType.BOOLEAN);
		export.setCanEdit(true);

		ListGridField sign = new ListGridField("sign", I18N.message("sign"), 60);
		sign.setType(ListGridFieldType.BOOLEAN);
		sign.setCanEdit(true);

		ListGridField archive = new ListGridField("archive", I18N.message("archive"), 60);
		archive.setType(ListGridFieldType.BOOLEAN);
		archive.setCanEdit(true);

		ListGridField workflow = new ListGridField("workflow", I18N.message("workflow"), 60);
		workflow.setType(ListGridFieldType.BOOLEAN);
		workflow.setCanEdit(true);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		list.setAutoFetchData(true);
		dataSource = new RightsDS(folder.getId(), true);
		list.setDataSource(dataSource);

		List<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(entityId);
		fields.add(entity);
		fields.add(read);
		fields.add(download);
		fields.add(write);
		fields.add(add);
		fields.add(security);
		fields.add(immutable);
		fields.add(delete);
		fields.add(rename);
		fields.add(_import);
		fields.add(export);
		if (Feature.enabled(Feature.DIGITAL_SIGN))
			fields.add(sign);
		if (Feature.enabled(Feature.ARCHIVES))
			fields.add(archive);
		if (Feature.enabled(Feature.WORKFLOW))
			fields.add(workflow);

		list.setFields(fields.toArray(new ListGridField[0]));

		container.addMember(list);

		if (folder != null && folder.hasPermission(Constants.PERMISSION_SECURITY)) {
			list.setCanEdit(true);
			list.setEditEvent(ListGridEditEvent.CLICK);
			list.setModalEditing(true);
			list.addCellContextClickHandler(new CellContextClickHandler() {
				@Override
				public void onCellContextClick(CellContextClickEvent event) {
					if (event.getColNum() == 0) {
						Menu contextMenu = setupContextMenu();
						contextMenu.showContextMenu();
					}
					event.cancel();
				}
			});
		}

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(4);
		buttons.setWidth100();
		buttons.setHeight(20);
		container.addMember(buttons);

		Button applyRights = new Button(I18N.message("applyrights"));
		applyRights.setAutoFit(true);
		buttons.addMember(applyRights);

		Button applyRightsSubfolders = new Button(I18N.message("applytosubfolders"));
		applyRightsSubfolders.setAutoFit(true);
		buttons.addMember(applyRightsSubfolders);

		applyRights.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave(false);
			}
		});

		applyRightsSubfolders.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave(true);
			}
		});

		// Prepare the combo and button for adding a new Group
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

				// Check if the selected user is already present in the rights
				// table
				ListGridRecord[] records = list.getRecords();
				for (ListGridRecord test : records) {
					if (test.getAttribute("entityId").equals(selectedRecord.getAttribute("id"))) {
						group.clearValue();
						return;
					}
				}

				// Update the rights table
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("entityId", selectedRecord.getAttribute("id"));
				record.setAttribute("entity", I18N.message("group") + ": " + selectedRecord.getAttribute("name"));
				record.setAttribute("read", true);
				list.addData(record);
				group.clearValue();
			}
		});

		final DynamicForm userForm = new DynamicForm();
		final SelectItem user = ItemFactory.newUserSelector("user", "adduser", null);
		userForm.setItems(user);

		user.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord selectedRecord = user.getSelectedRecord();
				if (selectedRecord == null)
					return;

				// Check if the selected user is already present in the rights
				// table
				ListGridRecord[] records = list.getRecords();
				for (ListGridRecord test : records) {
					if (test.getAttribute("entityId").equals(selectedRecord.getAttribute("usergroup"))) {
						user.clearValue();
						return;
					}
				}

				// Update the rights table
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("entityId", selectedRecord.getAttribute("usergroup"));
				record.setAttribute("entity", I18N.message("user") + ": " + selectedRecord.getAttribute("label") + " ("
						+ selectedRecord.getAttribute("username") + ")");
				record.setAttribute("read", true);
				list.addData(record);
				user.clearValue();
			}
		});

		buttons.addMember(userForm);
	}

	/**
	 * Create an array of all right defined
	 */
	public GUIRight[] getRights() {
		ListGridRecord[] records = list.getRecords();
		GUIRight[] tmp = new GUIRight[records.length];

		int i = 0;
		for (ListGridRecord record : records) {
			GUIRight right = new GUIRight();

			right.setName(record.getAttributeAsString("entity"));
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
			right.setDownload(record.getAttributeAsBoolean("download"));

			tmp[i] = right;
			i++;
		}

		return tmp;
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
		deleteItem.setTitle(I18N.message("ddelete"));
		deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;

				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							list.removeSelectedData();
						}
					}
				});
			}
		});

		contextMenu.setItems(deleteItem);
		return contextMenu;
	}

	public void onSave(boolean recursive) {
		// Apply all rights
		folder.setRights(this.getRights());
		final boolean onSubFolders = recursive;

		folderService.applyRights(Session.get().getSid(), folder, recursive, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				if (!onSubFolders)
					Log.info(I18N.message("appliedrights"), null);
				else
					Log.info(I18N.message("appliedrightsonsubfolders"), null);
			}
		});

	}
}