package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to send a new note
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class AddNoteWindow extends Window {
	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private NotesPanel notesPanel;

	private ButtonItem sendItem;

	public AddNoteWindow(final long docId, final NotesPanel notesPanel) {
		super();
		this.notesPanel = notesPanel;

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sendpost"));
		setWidth(600);
		setHeight(200);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final RichTextEditor message = new RichTextEditor();
		message.setWidth(595);
		message.setHeight(155);
		message.setMargin(5);
		message.setOverflow(Overflow.HIDDEN);
		message.setCanDragResize(true);
		message.setShowEdges(true);
		message.setValue("");
		addItem(message);

		DynamicForm noteForm = new DynamicForm();
		noteForm.setID("noteform");
		noteForm.setWidth(350);
		noteForm.setMargin(5);

		sendItem = new ButtonItem();
		sendItem.setTitle(I18N.message("send"));
		sendItem.setAutoFit(true);
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!message.getValue().trim().isEmpty()) {
					documentService.addNote(Session.get().getSid(), docId, message.getValue(),
							new AsyncCallback<Long>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
									destroy();
								}

								@Override
								public void onSuccess(Long result) {
									AddNoteWindow.this.notesPanel.onNoteAdded(result.longValue(), message.getValue());
									destroy();
								}
							});
				}
			}
		});
		noteForm.setFields(sendItem);
		addItem(noteForm);
	}
}
