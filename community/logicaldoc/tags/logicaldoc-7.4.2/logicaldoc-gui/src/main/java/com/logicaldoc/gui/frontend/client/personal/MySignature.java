package com.logicaldoc.gui.frontend.client.personal;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
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
 * This is the form used to load the user's certificate.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class MySignature extends Window {
	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private MultiUploader uploader;

	private ValuesManager vm = new ValuesManager();

	private SelectItem certificates = null;

	private GUIUser currentUser;

	private ButtonItem save = null;

	private ButtonItem reset = null;

	private HTMLFlow hint = null;

	public MySignature(GUIUser user, boolean administration) {
		super();

		this.currentUser = user;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("mysignature"));
		setWidth(450);
		setHeight(administration ? 100 : 240);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		certificates = ItemFactory.newSelectItem("certificates", I18N.message("signature"));
		certificates.setWidth(180);
		certificates.setRequired(true);
		form.setItems(certificates);

		save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.setDisabled(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSave();
			}
		});

		reset = new ButtonItem();
		reset.setTitle(I18N.message("reset"));
		reset.setAutoFit(true);
		reset.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onReset();
			}
		});
		reset.setVisible(administration);
		reset.setDisabled(!administration);

		if (administration)
			form.setItems(certificates, reset);
		else
			form.setItems(certificates, save, reset);

		hint = new HTMLFlow(I18N.message("mysignaturehint"));
		hint.setWidth100();

		HLayout spacer = new HLayout();
		spacer.setHeight(15);

		// Create a new uploader panel and attach it to the window
		uploader = new MultiUploader();
		uploader.setMaximumFiles(1);
		uploader.setStyleName("upload");
		uploader.setFileInputPrefix("LDOC");
		uploader.setWidth("350px");
		uploader.setHeight("40px");
		uploader.reset();

		// Add a finish handler which will load the image once the upload
		// finishes
		uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		uploader.addOnCancelUploadHandler(onCancelUploaderHandler);

		// Set initial signature value
		String cert = user.getCertSubject();
		if ((cert != null && !cert.trim().isEmpty()) && (cert != null && !cert.trim().isEmpty())) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(cert, cert);
			certificates.setValueMap(map);
			certificates.setValue(cert);
			hint.setVisible(false);
			uploader.setVisible(false);
			reset.setVisible(false);
			save.setVisible(false);
			if (!administration) {
				setHeight(130);
			}
		} else {
			save.setVisible(true);
		}

		VLayout layout = new VLayout();
		layout.setMargin(2);
		layout.addMember(form);

		if (!administration) {
			layout.addMember(spacer);
			layout.addMember(hint);
			layout.addMember(uploader);
		} else {
			reset.setVisible(true);
		}

		addItem(layout);
	}

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {

				signService.extractSubjectSignatures(Session.get().getSid(), null, null, new AsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(String[] result) {
						if (result != null && result.length > 0) {
							save.setDisabled(false);
							reset.setVisible(false);
							certificates.clearValue();
							LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
							for (String cert : result) {
								map.put(cert, cert);
							}
							certificates.setValueMap(map);
						} else {
							SC.warn(I18N.message("mysignatureerrorcertificates"));
							cancelUpload();
						}
					}
				});
			}
		}
	};

	private IUploader.OnCancelUploaderHandler onCancelUploaderHandler = new IUploader.OnCancelUploaderHandler() {
		@Override
		public void onCancel(IUploader uploader) {
			cancelUpload();
		}
	};

	public void onSave() {
		if (uploader.getSuccessUploads() <= 0) {
			SC.warn(I18N.message("filerequired"));
			return;
		}
		if (!vm.validate())
			return;

		save.setDisabled(true);
		ContactingServer.get().show();
		signService.storeSignature(Session.get().getSid(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				save.setDisabled(false);
				ContactingServer.get().hide();
				Log.serverError(caught);
				destroy();
			}

			@Override
			public void onSuccess(String result) {
				save.setDisabled(false);
				ContactingServer.get().hide();
				if (result == "ok") {
					Session.get().getUser().setCertSubject(vm.getValueAsString("certificates"));
					destroy();
					Log.info(I18N.message("signaturesaved"), null);
				} else {
					SC.warn(I18N.message(result));
				}
			}
		});
	}

	public void onReset() {
		signService.resetSignature(Session.get().getSid(), currentUser.getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				Log.info(I18N.message("signaturereset"), null);
				certificates.clearValue();
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put("", "");
				certificates.setValueMap(map);
				Session.get().getUser().setCertSubject(null);
				reset.setDisabled(true);
				reset.setVisible(false);
				save.setDisabled(true);
				hint.setVisible(true);
				uploader.setVisible(true);
				destroy();
			}
		});
	}

	private void cancelUpload() {
		save.setDisabled(true);
		certificates.clearValue();
		String cert = currentUser.getCertSubject();
		if (!cert.trim().isEmpty() && !cert.trim().isEmpty()) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(cert, cert);
			certificates.setValueMap(map);
			certificates.setValue(cert);
		} else {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("", "");
			certificates.setValueMap(map);
			certificates.clearValue();
		}

	}
}
