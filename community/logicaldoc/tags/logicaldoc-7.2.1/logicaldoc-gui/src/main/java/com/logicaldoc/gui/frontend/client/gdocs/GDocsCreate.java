package com.logicaldoc.gui.frontend.client.gdocs;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.GDocsService;
import com.logicaldoc.gui.frontend.client.services.GDocsServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This popup window is used to create a new document in Google Docs.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class GDocsCreate extends Window {
	private SubmitItem create;

	private ValuesManager vm;

	protected GDocsServiceAsync gdocsService = (GDocsServiceAsync) GWT.create(GDocsService.class);

	public GDocsCreate() {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("createdoc"));
		setWidth(300);
		setHeight(120);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setMembersMargin(3);

		DynamicForm form = new DynamicForm();
		vm = new ValuesManager();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);

		TextItem title = ItemFactory.newTextItem("title", "title", null);
		title.setRequired(true);
		title.setWidth(200);

		SelectItem type = ItemFactory.newSelectItem("type", I18N.message("type"));
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("doc", "doc");
		map.put("docx", "docx");
		map.put("odt", "odt");
		map.put("txt", "txt");
		map.put("xls", "xls");
		map.put("xlsx", "xlsx");
		map.put("ods", "ods");
		map.put("ppt", "ppt");
		map.put("pptx", "pptx");
		map.put("odp", "odp");
		type.setValueMap(map);
		type.setValue("doc");
		type.setWidth(50);
		type.setEndRow(true);
		type.setRequired(true);

		create = new SubmitItem();
		create.setTitle(I18N.message("create"));
		create.setAlign(Alignment.RIGHT);
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCreate();
			}
		});

		form.setItems(title, type, create);

		addItem(form);
	}

	public void onCreate() {
		if (!vm.validate())
			return;
		hide();
		ContactingServer.get().show();
		gdocsService.create(Session.get().getSid(), vm.getValueAsString("title"), vm.getValueAsString("type"),
				new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
						destroy();
					}

					@Override
					public void onSuccess(String resId) {
						ContactingServer.get().hide();
						GUIDocument document = new GUIDocument();
						document.setTitle(vm.getValueAsString("title"));
						document.setFileName(vm.getValueAsString("title") + "." + vm.getValueAsString("type"));
						document.setType(vm.getValueAsString("type"));
						document.setExtResId(resId);
						GDocsEditor editor = new GDocsEditor(document, DocumentsPanel.get().getDocumentsGrid());
						editor.show();
						destroy();
					}
				});
	}
}