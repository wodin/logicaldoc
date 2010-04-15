package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
				Menu contextMenu = setupContextMenu(Session.getInstance().getCurrentFolder());
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
	private Menu setupContextMenu(final GUIFolder folder) {
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
							service.delete(Session.getInstance().getSid(), folder.getId(), new AsyncCallback<Void>() {
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
		if (folder.getId() != 5)
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
}