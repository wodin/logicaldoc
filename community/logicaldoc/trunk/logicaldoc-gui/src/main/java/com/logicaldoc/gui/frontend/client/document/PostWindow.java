package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to send a new post
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PostWindow extends Window {
	private long discussionId = -1;

	private int replyTo;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DiscussionsPanel discussionPanel;

	private ButtonItem sendItem;

	private ValuesManager vm = new ValuesManager();

	public PostWindow(long discussionId, int replyTo, String postTitle, final PostsPanel postsPanel) {
		super();
		this.discussionId = discussionId;
		this.replyTo = replyTo;

		sendItem = new ButtonItem();
		sendItem.setTitle(I18N.message("send"));
		sendItem.setAutoFit(true);
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					documentService.replyPost(Session.get().getSid(), PostWindow.this.discussionId,
							PostWindow.this.replyTo, vm.getValueAsString("title"), vm.getValueAsString("message"),
							new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
									destroy();
								}

								@Override
								public void onSuccess(Integer result) {
									postsPanel.onReplySent(result.intValue());
									destroy();
								}
							});
				}
			}
		});
		initGUI(postTitle);
	}

	public PostWindow(final long docId, String postTitle, DiscussionsPanel discussionPanel) {
		super();
		this.discussionPanel = discussionPanel;

		sendItem = new ButtonItem();
		sendItem.setTitle(I18N.message("send"));
		sendItem.setAutoFit(true);
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					documentService.startDiscussion(Session.get().getSid(), docId, vm.getValueAsString("title"), vm
							.getValueAsString("message"), new AsyncCallback<Long>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
							destroy();
						}

						@Override
						public void onSuccess(Long result) {
							PostWindow.this.discussionPanel.onDiscussionAdded(result.longValue(), vm
									.getValueAsString("title"));
							destroy();
						}
					});
				}
			}
		});

		initGUI(postTitle);
	}

	private void initGUI(String postTitle) {
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sendpost"));
		setWidth(400);
		setHeight(200);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		DynamicForm postForm = new DynamicForm();
		postForm.setID("postform");
		postForm.setValuesManager(vm);
		postForm.setWidth(350);
		postForm.setMargin(5);

		TextItem title = ItemFactory.newTextItem("title", "title", postTitle);
		title.setRequired(true);
		title.setWidth(300);

		TextAreaItem message = new TextAreaItem();
		message.setName("message");
		message.setRequired(true);
		message.setTitle(I18N.message("message"));
		message.setValue("");
		message.setWidth(300);

		postForm.setFields(title, message, sendItem);
		addItem(postForm);
	}
}