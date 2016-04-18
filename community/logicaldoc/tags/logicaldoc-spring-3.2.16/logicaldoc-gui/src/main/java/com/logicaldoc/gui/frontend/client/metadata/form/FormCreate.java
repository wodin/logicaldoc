package com.logicaldoc.gui.frontend.client.metadata.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.FormService;
import com.logicaldoc.gui.frontend.client.services.FormServiceAsync;
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
 * This popup window is used to create a new form.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.3
 */
public class FormCreate extends Window {
	private SubmitItem create;

	private ValuesManager vm;

	private FormsPanel grid;

	protected FormServiceAsync service = (FormServiceAsync) GWT.create(FormService.class);

	public FormCreate(FormsPanel grid) {
		this.grid = grid;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("createform"));
		setWidth(270);
		setHeight(155);
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

		TextItem name = ItemFactory.newSimpleTextItem("name", "name", null);
		name.setRequired(true);
		name.setWidth(200);

		SelectItem template = ItemFactory.newTemplateSelector(true, null);

		create = new SubmitItem();
		create.setTitle(I18N.message("create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCreate();
			}
		});

		form.setItems(name, template, create);

		addItem(form);
	}

	public void onCreate() {
		if (!vm.validate())
			return;

		GUIDocument vo = new GUIDocument();
		vo.setTitle(vm.getValueAsString("name").trim());
		if (vm.getValueAsString("template") == null || "".equals(vm.getValueAsString("template").toString()))
			vo.setTemplateId(null);
		else
			vo.setTemplateId(Long.parseLong(vm.getValueAsString("template").toString()));

		if (vo.getTitle().lastIndexOf('.') != -1)
			vo.setTitle(vo.getTitle().substring(0, vo.getTitle().lastIndexOf('.')));
		vo.setType("html");
		vo.setFileName(vo.getTitle() + ".html");
		vo.setNature(Constants.NATURE_FORM);

		service.create(Session.get().getSid(), vo, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
				destroy();
			}

			@Override
			public void onSuccess(GUIDocument form) {
				grid.init();
				destroy();
			}
		});

		destroy();
	}
}