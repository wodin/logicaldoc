package com.logicaldoc.gui.frontend.client.personal;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SignService;
import com.logicaldoc.gui.frontend.client.services.SignServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This is the form used to load the user signature.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class MySignature extends Window {
	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private SubmitItem sendButton;

	private MultiUploader uploader;

	private ValuesManager vm;

	private DynamicForm form;

	private VLayout layout = new VLayout();

	private SelectItem certificates = null;

	private long userId;

	public MySignature(long id) {
		super();

		this.userId = id;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("mysignature"));
		setWidth(430);
		setHeight(250);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		HTMLFlow hint = new HTMLFlow(I18N.message("mysignaturehint"));
		hint.setWidth(400);
		layout.addMember(hint, 0);

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

		layout.addMember(uploader, 1);
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

		certificates = ItemFactory.newSelectItem("certificates", I18N.message("certificatesfound"));
		certificates.setWidth(150);
		certificates.setRequired(true);

		String signatureId = Session.get().getUser().getSignatureId();
		String signatureInfo = Session.get().getUser().getSignatureInfo();
		if (!signatureId.trim().isEmpty() && !signatureInfo.trim().isEmpty()) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(signatureInfo, signatureInfo);
			certificates.setValueMap(map);
			certificates.setValue(signatureInfo);
		}

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

		form.setItems(certificates, sendButton);

		layout.addMember(form, 2);
	}

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				sendButton.setDisabled(false);

				signService.extractSubjectSignatures(Session.get().getSid(), userId, new AsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(String[] result) {
						if (result != null && result.length > 0) {
							certificates.clearValue();
							LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
							for (String cert : result) {
								map.put(cert, cert);
							}
							certificates.setValueMap(map);
						} else {
							SC.warn(I18N.message("mysignatureerrorcertificates"));
							cancel();
						}
					}
				});
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

		signService.storeSignature(Session.get().getSid(), userId, vm.getValueAsString("certificates"),
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(String result) {
						if (result == "ok") {
							destroy();
						} else {
							SC.warn(I18N.message(result));
						}
					}
				});
	}

	private void cancel() {
		sendButton.setDisabled(true);
		certificates.clearValue();
		String signatureId = Session.get().getUser().getSignatureId();
		String signatureInfo = Session.get().getUser().getSignatureInfo();
		if (!signatureId.trim().isEmpty() && !signatureInfo.trim().isEmpty()) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(signatureInfo, signatureInfo);
			certificates.setValueMap(map);
			certificates.setValue(signatureInfo);
		} else {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("", "");
			certificates.setValueMap(map);
			certificates.clearValue();
		}

	}
}
