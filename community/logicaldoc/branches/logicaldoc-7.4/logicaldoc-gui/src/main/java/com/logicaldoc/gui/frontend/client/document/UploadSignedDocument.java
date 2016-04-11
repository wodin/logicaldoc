package com.logicaldoc.gui.frontend.client.document;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SignService;
import com.logicaldoc.gui.frontend.client.services.SignServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to sign documents or view signatures.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UploadSignedDocument extends Window {
	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private IButton sendButton;

	private MultiUploader uploader;

	private VLayout layout = new VLayout();

	private long docId;

	public UploadSignedDocument(long id, String filename) {
		docId = id;

		layout.setMargin(15);
		layout.setMembersMargin(2);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				onClose();
				destroy();
			}
		});

		setTitle(I18N.message("signdocument"));
		setWidth(550);
		setHeight(180);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		DynamicForm urlForm = new DynamicForm();
		urlForm.setMargin(3);
		LinkItem downloadUrl = ItemFactory.newLinkItem("", "<b>(1)</b> " + I18N.message("downloadfiletosign"));
		downloadUrl.setTitleOrientation(TitleOrientation.LEFT);
		downloadUrl.setWrapTitle(false);
		downloadUrl.setValue(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + id);
		downloadUrl.setLinkTitle(filename);

		urlForm.setItems(downloadUrl);
		layout.addMember(urlForm, 0);

		DynamicForm messageForm = new DynamicForm();
		messageForm.setMargin(3);
		StaticTextItem uploadMessage = ItemFactory.newStaticTextItem(I18N.message(""),
				I18N.message("signandupload", filename), null);
		uploadMessage.setTitle("<b>(2)</b> " + uploadMessage.getTitle());
		uploadMessage.setWrapTitle(false);
		messageForm.setItems(uploadMessage);

		// Create a new uploader panel and attach it to the window
		uploader = new MultiUploader();
		uploader.setMaximumFiles(1);
		uploader.setStyleName("upload");
		uploader.setFileInputPrefix("LDOC");
		uploader.setWidth("380px");
		uploader.setHeight("40px");
		uploader.reset();

		// Add a finish handler which will load the image once the upload
		// finishes
		uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		uploader.addOnCancelUploadHandler(onCancelUploaderHandler);

		sendButton = new IButton();
		sendButton.setTitle(I18N.message("send"));
		sendButton.setDisabled(true);
		sendButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				onSend();
			}
		});

		layout.addMember(messageForm);
		layout.addMember(uploader);
		layout.addMember(sendButton);
		layout.setMargin(2);

		addItem(layout);
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

	private IUploader.OnCancelUploaderHandler onCancelUploaderHandler = new IUploader.OnCancelUploaderHandler() {
		@Override
		public void onCancel(IUploader uploader) {
			cancel();
		}
	};

	public void onSend() {
		if (uploader.getSuccessUploads() <= 0) {
			SC.warn(I18N.message("filerequired"));
			return;
		}

		sendButton.setDisabled(true);
		ContactingServer.get().show();
		signService.storeSignedDocument(Session.get().getSid(), docId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				ContactingServer.get().hide();
				sendButton.setDisabled(false);
				Log.serverError(caught);
				destroy();
			}

			@Override
			public void onSuccess(String result) {
				ContactingServer.get().hide();
				sendButton.setDisabled(false);
				if ("ok".equals(result)) {
					onClose();
					destroy();
				} else {
					SC.warn(I18N.message(result));
				}
			}
		});
	}

	private void cancel() {
		sendButton.setDisabled(true);
	}

	protected void onClose() {
		DocumentsPanel.get().refresh();
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				documentService.cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						destroy();
					}
				});
			}
		});
	}
}