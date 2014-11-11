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
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This is the form used to load the user's private key.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.2
 */
public class MyPrivateKey extends Window {
	private SignServiceAsync signService = (SignServiceAsync) GWT.create(SignService.class);

	private MultiUploader uploader;

	private ValuesManager vm = new ValuesManager();

	private SelectItem keys = null;

	private FormItem password = null;

	private GUIUser currentUser;

	private ButtonItem save = null;

	private HTMLFlow hint = null;

	public MyPrivateKey(GUIUser user) {
		super();

		this.currentUser = user;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("myprivatekey"));
		setWidth(450);
		setHeight(230);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		keys = ItemFactory.newSelectItem("keys", I18N.message("key"));
		keys.setWidth(180);
		keys.setRequired(true);
		password = ItemFactory.newPasswordItem("keyPassword", I18N.message("password"), null);
		password.setHint(I18N.message("keypassowrdhint"));

		save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.setDisabled(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSave();
			}
		});

		form.setItems(keys, password, save);

		HLayout spacer = new HLayout();
		spacer.setHeight(15);

		hint = new HTMLFlow(I18N.message("myprivatekeyhint"));
		hint.setWidth100();

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
		String key = user.getKeyDigest();
		if ((key != null && !key.trim().isEmpty()) && (key != null && !key.trim().isEmpty())) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(key, key);
			keys.setValueMap(map);
			keys.setValue(key);
			hint.setVisible(false);
			uploader.setVisible(false);
			password.setVisible(false);
			save.setVisible(false);
		} else {
			save.setVisible(true);
			password.setVisible(true);
			uploader.setVisible(true);
		}

		VLayout layout = new VLayout();
		layout.setMargin(2);
		layout.addMember(form);
		layout.addMember(spacer);
		layout.addMember(hint);
		layout.addMember(uploader);

		addItem(layout);
	}

	// Load the image in the document and in the case of success attach it to
	// the viewer
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {

				signService.extractKeyDigest(Session.get().getSid(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(String result) {
						if (result != null) {
							save.setDisabled(false);
							save.setVisible(true);
							password.setVisible(true);
							keys.clearValue();
							if (result.length() > 20)
								result = result.substring(0, 20);
							LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
							map.put(result, result);
							keys.setValueMap(map);
						} else {
							SC.warn(I18N.message("myprivatekeyerrorcertificates"));
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
		signService.storePrivateKey(Session.get().getSid(), vm.getValueAsString("keyPassword"),
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						save.setDisabled(false);
						ContactingServer.get().hide();
						Log.serverError(caught);
						cancelUpload();
						destroy();
					}

					@Override
					public void onSuccess(String result) {
						save.setDisabled(false);
						ContactingServer.get().hide();
						if (result == "ok") {
							Session.get().getUser().setKeyDigest(vm.getValueAsString("keys"));
							destroy();
							Log.info(I18N.message("privatekeysaved"), null);
						} else {
							SC.warn(I18N.message(result));
						}
					}
				});
	}

	public void onReset() {
		signService.resetPrivateKey(Session.get().getSid(), currentUser.getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				Log.info(I18N.message("privatekeyreset"), null);
				keys.clearValue();
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put("", "");
				keys.setValueMap(map);
				save.setDisabled(true);
				save.setVisible(true);
				password.setVisible(true);
				password.setValue((String) null);
				hint.setVisible(true);
				uploader.setVisible(true);
				destroy();
			}
		});
	}

	private void cancelUpload() {
		save.setDisabled(true);
		password.setVisible(false);
		password.setValue((String) null);
		keys.clearValue();
		String key = currentUser.getKeyDigest();
		uploader.reset();
		if (!key.trim().isEmpty() && !key.trim().isEmpty()) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(key, key);
			keys.setValueMap(map);
			keys.setValue(key);
		} else {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("", "");
			keys.setValueMap(map);
			keys.clearValue();
		}

	}
}