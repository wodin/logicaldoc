package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.data.FoldersDS;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * The panel that shows the folders navigation tree
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FoldersTreePanel extends TreeGrid {
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	private static FoldersTreePanel instance = new FoldersTreePanel();

	private FoldersTreePanel() {
		setWidth100();
		setBorder("0px");
		setBodyStyleName("normal");
		setShowHeader(false);
		setLeaveScrollbarGap(false);
		setManyItemsImage("cubes_all.png");
		setAppImgDir("pieces/16/");
		setCanReorderRecords(true);
		setCanAcceptDroppedRecords(false);
		setCanDragRecordsOut(false);
		setAutoFetchData(true);
		setLoadDataOnDemand(true);
		setDataSource(new FoldersDS());
		setCanSelectAll(false);

		ListGridField name = new ListGridField("name");
		setFields(name);

		addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = setupContextMenu();
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		// Handle the click on a folder to show the contained documents
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {

				service.getFolder(Session.getInstance().getSid(), Long.parseLong(event.getRecord()
						.getAttributeAsString("id")), new AsyncCallback<GUIFolder>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIFolder result) {
						Session.getInstance().setCurrentFolder(result);
					}
				});
			}
		});
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		final long id = Long.parseLong(selectedNode.getAttribute("id"));
		boolean add = selectedNode.getAttributeAsBoolean(Constants.PERMISSION_ADD);
		boolean delete = selectedNode.getAttributeAsBoolean(Constants.PERMISSION_DELETE);

		TreeNode parentNode = getTree().getParent(selectedNode);
		final long parentId;
		boolean parentAdd = false;
		boolean parentDelete = false;
		if (parentNode != null) {
			parentId = Long.parseLong(selectedNode.getAttribute("id"));
			parentAdd = selectedNode.getAttributeAsBoolean(Constants.PERMISSION_ADD);
			parentDelete = selectedNode.getAttributeAsBoolean(Constants.PERMISSION_DELETE);
		}

		Menu contextMenu = new Menu();
		List<MenuItem> items = new ArrayList<MenuItem>();

		MenuItem deleteItem = new MenuItem();
		deleteItem.setTitle(I18N.getMessage("delete"));
		deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.getMessage("question"), I18N.getMessage("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.delete(Session.getInstance().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									removeSelectedData();
								}
							});
						}
					}
				});
			}
		});

		MenuItem addItem = new MenuItem();
		addItem.setTitle(I18N.getMessage("newfolder"));
		addItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onCreate();
			}
		});

		if (add)
			items.add(addItem);
		if (id != 5 && parentDelete)
			items.add(deleteItem);
		contextMenu.setItems(items.toArray(new MenuItem[0]));

		return contextMenu;
	}

	public static FoldersTreePanel getInstance() {
		return instance;
	}

	public void onSavedFolder(GUIFolder folder) {
		TreeNode selectedNode = getTree().find("id", Long.toString(folder.getId()));
		if (selectedNode != null) {
			selectedNode.setTitle(folder.getName());
			selectedNode.setName(folder.getName());
			getTree().reloadChildren(selectedNode);
		}
	}

	private void onCreate() {
		TreeNode selectedNode = (TreeNode) getSelectedRecord();
		GUIFolder data = new GUIFolder();
		data.setName(I18N.getMessage("newfolder"));
		data.setParentId(Long.parseLong(selectedNode.getAttributeAsString("id")));

		service.save(Session.getInstance().getSid(), data, new AsyncCallback<GUIFolder>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIFolder newFolder) {
				TreeNode selectedNode = (TreeNode) getSelectedRecord();
				TreeNode newNode = new TreeNode(newFolder.getName());
				newNode.setAttribute("name", newFolder.getName());
				newNode.setAttribute("id", Long.toString(newFolder.getId()));

				if (!getTree().isOpen(selectedNode)) {
					getTree().openFolder(selectedNode);
				}
				getTree().add(newNode, selectedNode);
				selectRecord(newNode);
				Session.getInstance().setCurrentFolder(newFolder);
			}
		});
	}
}