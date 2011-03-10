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
import com.logicaldoc.gui.frontend.client.impex.archives.ExportArchivesList;
import com.logicaldoc.gui.frontend.client.services.SignService;
import com.logicaldoc.gui.frontend.client.services.SignServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to sign documents or view signatures.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SignClosureDialog extends Window {
	private ExportArchivesList archivesList = null;

	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private SubmitItem sendButton;

	private MultiUploader uploader;

	private ValuesManager vm;

	private DynamicForm form;

	private VLayout layout = new VLayout();

	private long docId;

	public SignClosureDialog(ExportArchivesList list, String id, String name) {
		this.archivesList = list;
		final String archiveId = id;

		VLayout layout = new VLayout();
		layout.setMargin(25);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
				archivesList.refresh(archivesList.getArchivesType(), false);
				archivesList.showDetails(Long.parseLong(archiveId), false);
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("signdocument"));
		setWidth(550);
		setHeight(230);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		DynamicForm urlForm = new DynamicForm();
		urlForm.setMargin(3);
		LinkItem downloadUrl = ItemFactory.newLinkItem("", "<b>(1)</b> " + I18N.message("downloadfiletosign"));
		downloadUrl.setTitleOrientation(TitleOrientation.LEFT);
		downloadUrl.setWrapTitle(false);
		downloadUrl.setValue(GWT.getHostPageBaseURL() + "downloadarchive?archiveId=" + id + "&sid="
				+ Session.get().getSid());
		downloadUrl.setLinkTitle(name + ".zip");

		urlForm.setItems(downloadUrl);
		layout.addMember(urlForm, 0);

		DynamicForm messageForm = new DynamicForm();
		messageForm.setMargin(3);
		StaticTextItem uploadMessage = ItemFactory.newStaticTextItem(I18N.message(""),
				I18N.message("signmarkandupload", name), null);
		uploadMessage.setTitle("<b>(2)</b> " + uploadMessage.getTitle());
		uploadMessage.setWidth(500);
		uploadMessage.setWrapTitle(false);
		messageForm.setItems(uploadMessage);
		layout.addMember(messageForm, 1);

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
		uploader.addOnCancelUploadHandler(onCancelUploaderHandler);

		layout.addMember(uploader, 2);
		layout.setMembersMargin(10);
		layout.setTop(25);
		layout.setMargin(5);

		addChild(layout);
	}

	private void reloadForm() {
		if (form != null) {
			layout.removeChild(form);
		}

		form = new DynamicForm();
		vm = new ValuesManager();
		form.setValuesManager(vm);

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

		layout.addMember(form, 3);
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
		if (!vm.validate())
			return;

		// TODO See the method 'signDocument' of the SignService. Here we have
		// to upload the .m7m file uploaded by the user, but only after the
		// original file digest verification with the file contained
		// into the uploaded file (inside the .p7m file contained into the .m7m
		// file).
		// See the 'CloseArchive servlet' for the archive closure.

		signService.signDocument(Session.get().getSid(), Session.get().getUser().getId(), docId,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(String result) {
						if (result == "ok") {
							DocumentsPanel.get().refresh();
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
}