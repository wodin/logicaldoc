package com.logicaldoc.gui.frontend.client.workflow;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to upload a new workflow schema to the server.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class WorkflowUploader extends Window {
	private SubmitItem sendButton;

	private MultiUploader uploader;

	private ValuesManager vm;

	private WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DynamicForm form;

	private VLayout layout = new VLayout();

	private WorkflowDesigner designer;

	public WorkflowUploader(WorkflowDesigner designer) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		this.designer = designer;
		setTitle(I18N.message("uploadworkflow"));
		setWidth(380);
		setHeight(120);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		reloadForm();

		// Create a new uploader panel and attach it to the window
		uploader = new MultiUploader();
		uploader.setMaximumFiles(1);
		uploader.setStyleName("upload");
		uploader.setFileInputPrefix("LDOC");
		uploader.setHeight("40px");
		uploader.reset();

		// Add a finish handler which will load the image once the upload
		// finishes
		uploader.addOnFinishUploadHandler(onFinishUploaderHandler);

		layout.addMember(uploader, 1);
		layout.setMembersMargin(10);
		layout.setMargin(20);

		addChild(layout);
	}

	private void reloadForm() {
		if (form != null) {
			layout.removeChild(form);
		}

		form = new DynamicForm();
		vm = new ValuesManager();
		form.setValuesManager(vm);
		form.setHeight(90);

		sendButton = new SubmitItem();
		sendButton.setTitle(I18N.message("send"));
		sendButton.setDisabled(true);
		sendButton.setAlign(Alignment.RIGHT);
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSend();
			}
		});

		form.setItems(sendButton);

		layout.addMember(form, 1);
	}

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				sendButton.setDisabled(false);
			}
		}
	};

	public void onSend() {
		if (uploader.getSuccessUploads() <= 0) {
			SC.warn(I18N.message("filerequired"));
			return;
		}
		if (!vm.validate())
			return;

		workflowService.importSchema(Session.get().getSid(), new AsyncCallback<GUIWorkflow>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.warn(caught.getMessage());
			}

			@Override
			public void onSuccess(GUIWorkflow result) {
				if (result != null) {
					result.setId(designer.getWorkflow().getId());
					result.setName(designer.getWorkflow().getName());
					designer.redraw(result);
					designer.saveModel();
					
					//Cleanup the upload folder
					documentService.cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							destroy();
						}

						@Override
						public void onSuccess(Void result) {
							destroy();
						}
					});
				}
			}

		});
	}
}