package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This popup window is used to start a workflow on the selected documents.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDialog extends Window {

	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private TabSet tabs = new TabSet();

	public WorkflowDialog(final long[] ids) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("startworkflow"));
		setWidth(600);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(400);
		tabs.setHeight(200);

		Tab webService = new Tab();
		webService.setTitle(I18N.message("webservice"));

		DynamicForm webServiceForm = new DynamicForm();
		webServiceForm.setTitleOrientation(TitleOrientation.TOP);
		webServiceForm.setNumCols(1);
		//
		// // Enabled
		// RadioGroupItem enabled = ItemFactory.newBooleanSelector("wsEnabled",
		// "enabled");
		// enabled.setName("wsEnabled");
		// enabled.setValue(this.wsSettings.isEnabled() ? "yes" : "no");
		//
		// // Url
		// LinkItem url = new LinkItem();
		// url.setName(I18N.message("url"));
		// url.setLinkTitle(this.wsSettings.getUrl());
		// url.setValue(this.wsSettings.getUrl());
		//
		// // Descriptor
		// LinkItem descriptor = new LinkItem();
		// descriptor.setName(I18N.message("descriptor"));
		// descriptor.setLinkTitle(this.wsSettings.getDescriptor());
		// descriptor.setValue(this.wsSettings.getDescriptor());
		//
		// webServiceForm.setItems(url, descriptor, enabled);
		webService.setPane(webServiceForm);

		Tab webDav = new Tab();
		webDav.setTitle(I18N.message("webdav"));

		DynamicForm webDavForm = new DynamicForm();
		webDavForm.setTitleOrientation(TitleOrientation.TOP);
		webDavForm.setNumCols(1);

		// // Status
		// RadioGroupItem wdEnabled =
		// ItemFactory.newBooleanSelector("wdEnabled", "enabled");
		// wdEnabled.setName("wdEnabled");
		// wdEnabled.setValue(this.webDavSettings.isEnabled() ? "yes" : "no");
		//
		// // Url
		// LinkItem wdUrl = new LinkItem();
		// wdUrl.setName(I18N.message("url"));
		// wdUrl.setLinkTitle(this.webDavSettings.getUrl());
		// wdUrl.setValue(this.webDavSettings.getUrl());
		//
		// webDavForm.setItems(wdUrl, wdEnabled);
		webDav.setPane(webDavForm);

		Tab xxx = new Tab();
		xxx.setTitle(I18N.message("webdav"));

		DynamicForm xxxForm = new DynamicForm();
		xxxForm.setTitleOrientation(TitleOrientation.TOP);
		xxxForm.setNumCols(1);

		// // Status
		// RadioGroupItem wdEnabled =
		// ItemFactory.newBooleanSelector("wdEnabled", "enabled");
		// wdEnabled.setName("wdEnabled");
		// wdEnabled.setValue(this.webDavSettings.isEnabled() ? "yes" : "no");
		//
		// // Url
		// LinkItem wdUrl = new LinkItem();
		// wdUrl.setName(I18N.message("url"));
		// wdUrl.setLinkTitle(this.webDavSettings.getUrl());
		// wdUrl.setValue(this.webDavSettings.getUrl());
		//
		// webDavForm.setItems(wdUrl, wdEnabled);
		webDav.setPane(xxxForm);

		tabs.setTabs(webService, webDav, xxx);

		addMember(tabs);
	}

	public void onSend(long[] ids) {
		service.addDocuments(Session.get().getSid(), 1, ids,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						Log.info(I18N.message("documentsaddedtoarchive"), null);
						destroy();
					}
				});
	}
}
