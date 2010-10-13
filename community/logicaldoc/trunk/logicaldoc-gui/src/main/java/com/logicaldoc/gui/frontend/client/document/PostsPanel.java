package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.PostsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
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
import com.smartgwt.client.widgets.grid.CellFormatter;
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
 * This panel shows the posts of a discussion
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PostsPanel extends DocumentDetailTab {

	private DataSource dataSource;

	private ListGrid listGrid;

	private VLayout container = new VLayout();

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private long discussionId;

	private Discussion discussion;

	public PostsPanel(GUIDocument document, final long discussionId, final Discussion discussion) {
		super(document, null);
		this.discussion = discussion;
		this.discussionId = discussionId;
		addMember(container);
		container.setMembersMargin(2);

		ListGridField id = new ListGridField("id", I18N.message("id"), 200);
		id.setHidden(true);
		ListGridField title = new ListGridField("title", I18N.message("title"), 150);
		title.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				String val = (String) value;
				Integer indent = record.getAttributeAsInt("indent");
				for (int i = 0; i < indent; i++)
					val = "&nbsp;&nbsp;" + val;
				return val;
			}
		});
		ListGridField user = new ListGridField("user", I18N.message("by"), 100);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);
		ListGridField message = new ListGridField("message", I18N.message("message"), 400);
		ListGridField indent = new ListGridField("indent");
		indent.setHidden(true);

		listGrid = new ListGrid();
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		listGrid.setWrapCells(true);
		listGrid.setCellHeight(56);
		listGrid.setCellPadding(2);
		dataSource = new PostsDS(discussionId);
		listGrid.setDataSource(dataSource);
		listGrid.setFields(id, title, user, date, message, indent);
		container.setHeight100();
		container.addMember(listGrid);

		Button backToDiscussions = new Button("<< " + I18N.message("back"));
		container.addMember(backToDiscussions);
		backToDiscussions.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				discussion.showDiscussions();
			}
		});

		listGrid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				replyToPost();
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new Menu();
				MenuItem deleteItem = new MenuItem();
				deleteItem.setTitle(I18N.message("ddelete"));
				deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						deleteSelection();
					}
				});

				MenuItem replyItem = new MenuItem();
				replyItem.setTitle(I18N.message("reply"));
				replyItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						replyToPost();
					}
				});

				// Administrators only can delete a post
				if (Session.get().getUser().isMemberOf("admin")) {
					contextMenu.setItems(replyItem, deleteItem);
				} else {
					contextMenu.setItems(replyItem);
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
		final int[] ids = new int[selection.length];
		for (int i = 0; i < selection.length; i++) {
			ids[i] = Integer.parseInt(selection[i].getAttribute("id"));
		}

		SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					documentService.deletePosts(Session.get().getSid(), discussionId, ids,
							new AsyncCallback<Void>() {
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

	private void replyToPost() {
		ListGridRecord selection = listGrid.getSelectedRecord();
		if (selection != null) {
			int id = Integer.parseInt(selection.getAttribute("id"));
			PostWindow post = new PostWindow(discussionId, id, "RE: " + selection.getAttribute("title").trim(),
					PostsPanel.this);
			post.show();
		}
	}

	public void onReplySent(int id) {
		discussion.showPosts(discussionId);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}