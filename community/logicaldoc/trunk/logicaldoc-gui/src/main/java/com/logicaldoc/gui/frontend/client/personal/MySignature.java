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
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This is the form used to load the user signature.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class MySignature extends Window {
	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private MultiUploader uploader;

	private ValuesManager vm = new ValuesManager();

	private VLayout layout = new VLayout();

	private SelectItem certificates = null;

	private long userId;

	private ButtonItem save = null;

	public MySignature(long id) {
		super();

		this.userId = id;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("mysignature"));
		setWidth(310);
		setHeight(250);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		HLayout signatureLayout = new HLayout(20);

		DynamicForm signatureForm = new DynamicForm();
		signatureForm.setValuesManager(vm);
		certificates = ItemFactory.newSelectItem("certificates", I18N.message("signature"));
		certificates.setWidth(150);
		certificates.setRequired(true);
		signatureForm.setItems(certificates);

		DynamicForm saveForm = new DynamicForm();
		saveForm.setValuesManager(vm);
		save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.setDisabled(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		saveForm.setItems(save);

		signatureLayout.setMembers(signatureForm, saveForm);
		layout.addMember(signatureLayout, 0);

		HTMLFlow hint = new HTMLFlow(I18N.message("mysignaturehint"));
		hint.setWidth(290);
		layout.addMember(hint, 1);

		// Set initial signature value
		String signatureId = Session.get().getUser().getSignatureId();
		String signatureInfo = Session.get().getUser().getSignatureInfo();
		if ((signatureId != null && !signatureId.trim().isEmpty())
				&& (signatureInfo != null && !signatureInfo.trim().isEmpty())) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(signatureInfo, signatureInfo);
			certificates.setValueMap(map);
			certificates.setValue(signatureInfo);
		}

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

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {

				signService.extractSubjectSignatures(Session.get().getSid(), userId, new AsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(String[] result) {
						if (result != null && result.length > 0) {
							save.setDisabled(false);
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

	public void onSave() {
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
							Log.info(I18N.message("signaturesaved"), null);
						} else {
							SC.warn(I18N.message(result));
						}
					}
				});
	}

	private void cancel() {
		save.setDisabled(true);
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
