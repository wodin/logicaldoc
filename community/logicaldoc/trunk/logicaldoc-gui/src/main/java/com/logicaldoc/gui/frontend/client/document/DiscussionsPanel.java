package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.DiscussionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the opened forums on a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DiscussionsPanel extends DocumentDetailTab {

	private DataSource dataSource;

	// Table of all discussions
	private ListGrid listGrid;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private VLayout container = new VLayout();

	public DiscussionsPanel(final GUIDocument document, final Discussion discussion) {
		super(document, null);
		addMember(container);
		container.setMembersMargin(2);

		ListGridField id = new ListGridField("id", I18N.getMessage("id"), 200);
		id.setHidden(true);
		ListGridField title = new ListGridField("title", I18N.getMessage("title"), 200);
		ListGridField user = new ListGridField("user", I18N.getMessage("startedby"), 200);
		ListGridField posts = new ListGridField("posts", I18N.getMessage("posts"), 50);
		posts.setType(ListGridFieldType.INTEGER);
		posts.setAlign(Alignment.CENTER);
		ListGridField visits = new ListGridField("visits", I18N.getMessage("visits"), 50);
		visits.setType(ListGridFieldType.INTEGER);
		visits.setAlign(Alignment.CENTER);

		ListGridField lastPost = new ListGridField("lastPost", I18N.getMessage("lastpost"), 110);
		lastPost.setAlign(Alignment.CENTER);
		lastPost.setType(ListGridFieldType.DATE);
		lastPost.setCellFormatter(new DateCellFormatter());

		listGrid = new ListGrid();
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new DiscussionsDS(document.getId());
		listGrid.setDataSource(dataSource);
		listGrid.setFields(id, title, user, posts, visits, lastPost);
		container.setHeight100();
		container.addMember(listGrid);

		Button startDiscussion = new Button(I18N.getMessage("startdiscussion"));
		container.addMember(startDiscussion);
		startDiscussion.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PostWindow post = new PostWindow(document.getId(), "", DiscussionsPanel.this);
				post.show();
			}
		});

		listGrid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				ListGridRecord selection = listGrid.getSelectedRecord();
				if (selection != null)
					discussion.showPosts(Long.parseLong(selection.getAttribute("id")));
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new Menu();
				MenuItem deleteItem = new MenuItem();
				deleteItem.setTitle(I18N.getMessage("delete"));
				deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						deleteSelection();
					}
				});

				MenuItem showPostsItem = new MenuItem();
				showPostsItem.setTitle(I18N.getMessage("showposts"));
				showPostsItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						ListGridRecord selection = listGrid.getSelectedRecord();
						if (selection != null)
							discussion.showPosts(Long.parseLong(selection.getAttribute("id")));
					}
				});

				// Administrators only can delete a post
				if (Session.get().getUser().isMemberOf("admin")) {
					contextMenu.setItems(showPostsItem, deleteItem);
				} else {
					contextMenu.setItems(showPostsItem);
				}

				contextMenu.showContextMenu();
				event.cancel();
			}
		});
	}

	private void deleteSelection() {
		ListGridRecord[] selection = listGrid.getSelection();
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
					documentService.deleteDiscussions(Session.get().getSid(), ids, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							listGrid.removeSelectedData();
						}
					});
				}
			}
		});
	}

	public void onDiscussionAdded(long id, String title) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("id", Long.toString(id));
		record.setAttribute("title", title);
		record.setAttribute("user", Session.get().getUser().getFullName());
		listGrid.addData(record);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}