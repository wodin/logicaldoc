package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.RichTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to edit a note in a workflow
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.6
 */
public class WorkflowNoteEditor extends Window {

	private WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private WorkflowDetailsDialog parentDialog;

	private ButtonItem save;

	private RichTextItem message;

	private DynamicForm noteForm = new DynamicForm();

	public WorkflowNoteEditor(WorkflowDetailsDialog parentDialog) {
		super();
		this.parentDialog = parentDialog;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("note"));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setAutoSize(true);

		message = new RichTextItem("message");
		message.setTitle(I18N.message("message"));
		message.setShowTitle(false);
		message.setRequired(true);
		message.setWidth(680);
		message.setHeight(230);

		save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSave();
			}
		});

		noteForm.setItems(message, save);
		addItem(noteForm);
	}

	private void onSave() {
		if (!noteForm.validate())
			return;
		service.addNote(parentDialog.getWorkflow().getSelectedTask().getId(), message.getValue().toString(),
				new AsyncCallback<Long>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Long noteId) {
						parentDialog.refreshAndSelectNotesTab();
						destroy();
					}
				});
	}
}