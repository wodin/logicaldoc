package com.logicaldoc.gui.frontend.client.webcontent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.document.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
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
 * This popup window is used to create a new web content.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class WebcontentCreate extends Window {
	private SubmitItem create;

	private ValuesManager vm;

	private DocumentsGrid grid;

	protected DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public WebcontentCreate(DocumentsGrid grid) {
		this.grid = grid;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("createwebcontent"));
		setWidth(270);
		setHeight(145);
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
		form.setNumCols(1);

		TextItem title = ItemFactory.newTextItem("title", "title", null);
		title.setRequired(true);
		title.setWidth(200);

		SelectItem template = ItemFactory.newTemplateSelector(false, null);

		create = new SubmitItem();
		create.setTitle(I18N.message("create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCreate();
			}
		});

		form.setItems(title, template, create);

		addItem(form);
	}

	public void onCreate() {
		if (!vm.validate())
			return;
		GUIDocument vo = new GUIDocument();
		vo.setTitle(vm.getValueAsString("title").trim());
		if (vm.getValueAsString("template") == null || "".equals(vm.getValueAsString("template").toString()))
			vo.setTemplateId(null);
		else {
			vo.setTemplateId(Long.parseLong(vm.getValueAsString("template").toString()));
		}
		
		if (vo.getTitle().lastIndexOf('.') != -1) {
			vo.setTitle(vo.getTitle().substring(0, vo.getTitle().lastIndexOf('.')));
		}
		vo.setType("html");
		vo.setFileName(vo.getTitle() + ".html");
		vo.setStatus(1);
		vo.setFolder(Session.get().getCurrentFolder());
		
		service.createEmpty(Session.get().getSid(), vo, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
				destroy();
			}

			@Override
			public void onSuccess(GUIDocument document) {
				DocumentsPanel.get().refresh();
				DocumentsPanel.get().selectDocument(document.getId(), true);

				Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() + 1);
				Log.info(I18N.message("documentcheckedout"), null);

				destroy();

				WebcontentEditor popup = new WebcontentEditor(document, grid);
				popup.show();
			}
		});

		destroy();
	}
}