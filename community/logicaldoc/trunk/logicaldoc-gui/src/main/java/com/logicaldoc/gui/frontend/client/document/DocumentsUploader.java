package com.logicaldoc.gui.frontend.client.document;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to upload documents to the server.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentsUploader extends Window {

	private SubmitItem sendButton;

	private MultiUploader multiUploader;

	private ValuesManager vm;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private boolean zipImport = true;

	private DynamicForm form;

	private VLayout layout = new VLayout();

	public DocumentsUploader() {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("adddocuments"));
		setWidth(430);
		setHeight(250);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		reloadForm();

		// Create a new uploader panel and attach it to the window
		multiUploader = new MultiUploader();

		multiUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		multiUploader.setStyleName("upload");
		multiUploader.setHeight("100%");
		multiUploader.setWidth("100%");
		multiUploader.setFileInputPrefix("LDOC");
		multiUploader.reset();

		layout.addMember(multiUploader, 1);
		layout.setMembersMargin(10);
		layout.setMargin(25);
		layout.setHeight(250);
		layout.setWidth100();

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

		CheckboxItem zipItem = new CheckboxItem();
		zipItem.setName("zip");
		zipItem.setTitle(I18N.message("importfromzip"));
		zipItem.setValue(!zipImport);

		SelectItem encodingItem = ItemFactory.newEncodingSelector("encoding");
		encodingItem.setDisabled(zipImport);

		SelectItem template = ItemFactory.newTemplateSelector(false, null);
		template.setMultiple(false);

		zipItem.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				zipImport = !zipImport;
				reloadForm();
			}
		});

		sendButton = new SubmitItem();
		sendButton.setTitle(I18N.message("send"));
		sendButton.setEndRow(true);
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSend();
			}
		});

		form.setItems(zipItem, encodingItem, sendButton);

		layout.addMember(form, 0);
	}

	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				sendButton.setDisabled(false);
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

		GUIDocument metadata = new GUIDocument();
		metadata.setFolder(Session.get().getCurrentFolder());
		metadata.setLanguage(I18N.getDefaultLocaleForDoc());

		BulkUpdateDialog bulk = new BulkUpdateDialog(null, metadata);
		bulk.setZip(getImportZip());
		bulk.setEncoding(getEncoding());
		bulk.show();
		destroy();
	}

	public String getEncoding() {
		return vm.getValueAsString("encoding");
	}

	public boolean getImportZip() {
		return "true".equals(vm.getValueAsString("zip"));
	}
}