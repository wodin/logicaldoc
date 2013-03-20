package com.logicaldoc.gui.frontend.client.document;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.EventPanel;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This popup window is used to upload a checked-out document to the server.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentCheckin extends Window {
	private SubmitItem sendButton;

	private MultiUploader multiUploader;

	private ValuesManager vm;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private long docId;

	private String fileName;

	private DocumentsGrid documentsGrid;

	public DocumentCheckin(long docId, String filename, DocumentsGrid documentsGrid) {
		this.docId = docId;
		this.fileName = filename;
		this.documentsGrid = documentsGrid;
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("checkin"));
		setWidth(370);
		setHeight(200);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setMembersMargin(3);

		DynamicForm form = new DynamicForm();
		vm = new ValuesManager();
		form.setValuesManager(vm);

		BooleanItem versionItem = new BooleanItem();
		versionItem.setName("majorversion");
		versionItem.setTitle(I18N.message("majorversion"));

		final BooleanItem filenameItem = new BooleanItem();
		filenameItem.setWidth(280);
		filenameItem.setName("checkfilename");
		filenameItem.setTitle(I18N.message("checkfilename"));
		filenameItem.setDefaultValue(true);
		filenameItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (!filenameItem.getValueAsBoolean())
					sendButton.setDisabled(false);
				multiUploader.reset();
			}
		});

		TextItem commentItem = ItemFactory.newTextItem("comment", "comment", null);
		commentItem.setRequired(true);

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

		form.setItems(versionItem, filenameItem, commentItem, sendButton);

		// Create a new uploader panel and attach it to the window
		multiUploader = new MultiUploader();
		multiUploader.setStyleName("upload");
		multiUploader.setHeight("100%");
		multiUploader.setFileInputPrefix("LDOC");
		multiUploader.setMaximumFiles(1);
		multiUploader.reset();

		addItem(form);
		addItem(multiUploader);

		// Add a finish handler which will load the image once the upload
		// finishes
		multiUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
	}

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				sendButton.setDisabled(false);
			}

			// This check is done because IE8 works differently from Firefox
			String uploadedFilename = uploader.getFileName();
			if (uploadedFilename.lastIndexOf('/') != -1)
				uploadedFilename = uploadedFilename.substring(uploadedFilename.lastIndexOf('/') + 1);
			if (uploadedFilename.lastIndexOf('\\') != -1)
				uploadedFilename = uploadedFilename.substring(uploadedFilename.lastIndexOf('\\') + 1);

			if ("true".equals(vm.getValueAsString("checkfilename")) && !fileName.equals(uploadedFilename)) {
				sendButton.setDisabled(true);
				SC.warn(I18N.message("nosamefilename"));
			}
		}
	};

	public void onSend() {
		if (multiUploader.getSuccessUploads() <= 0) {
			SC.warn(I18N.message("filerequired"));
			return;
		}
		if (!vm.validate())
			return;

		documentService.checkin(Session.get().getSid(), docId, vm.getValueAsString("comment"),
				"true".equals(vm.getValueAsString("majorversion")), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(Void result) {
						EventPanel.get().cleanLabel();
						documentService.getById(Session.get().getSid(), docId, new AsyncCallback<GUIDocument>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIDocument document) {
								DocumentsPanel.get().onDocumentSaved(document);
								documentsGrid.markSelectedAsCheckedIn();
								Session.get().getUser()
										.setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() - 1);
								DocumentsPanel.get().onSelectedDocument(document.getId(), false);
								destroy();
							}
						});
					}
				});
	}

	public String getLanguage() {
		return vm.getValueAsString("language");
	}

	public boolean getImportZip() {
		return "true".equals(vm.getValueAsString("zip"));
	}
}