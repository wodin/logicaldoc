package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to start a workflow on the selected documents.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDialog extends Window {

	private WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private DynamicForm form = new DynamicForm();

	private SelectItem workflow;

	public WorkflowDialog(final long[] ids) {
		VLayout layout = new VLayout();
		layout.setMargin(5);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("startworkflow"));
		setWidth(420);
		setHeight(115);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		layout.addMember(form);

		workflow = ItemFactory.newWorkflowSelector();
		workflow.setTitle(I18N.message("chooseworkflow"));
		workflow.setWrapTitle(false);
		workflow.setRequired(true);

		ButtonItem start = new ButtonItem();
		start.setTitle(I18N.message("startworkflow"));
		start.setAutoFit(true);
		start.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onStart(ids);
			}
		});

		form.setTitleOrientation(TitleOrientation.TOP);
		form.setFields(workflow, start);
		addItem(layout);
	}

	public void onStart(long[] ids) {
		if (!form.validate())
			return;

		ListGridRecord selection = workflow.getSelectedRecord();
		service.startWorkflow(Session.get().getSid(), selection.getAttributeAsString("name"),
				selection.getAttributeAsString("description"), ids, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						Log.info(I18N.message("event.workflow.start"), null);
						destroy();
					}
				});
	}
}