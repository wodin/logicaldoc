package com.logicaldoc.gui.frontend.client.workflow;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.folder.FolderSelector;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used for the workflow transition settings
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class TransitionDialog extends Window {
	protected FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private ValuesManager vm = new ValuesManager();

	private DynamicForm form;

	private StateWidget widget;

	public TransitionDialog(StateWidget widget) {
		this.widget = widget;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("editworkflowstate", I18N.message("transition")));
		setWidth(340);
		setHeight(200);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setMargin(3);

		form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);
		form.setValuesManager(vm);

		TextItem name = ItemFactory.newTextItem("name", "name", widget.getTransition().getText());
		name.setRequired(true);

		SelectItem effect = ItemFactory.newEffectSelector("effect", "effect");
		effect.setValue(widget.getTransition().getEffect());

		final FolderSelector target = new FolderSelector("target", false);
		target.setRequired(false);
		target.setWidth(200);
		target.setTitle(I18N.message("target"));
		if (widget.getTransition().getTargetFolder() != null)
			target.setFolder(widget.getTransition().getTargetFolder(), "");

		ButtonItem save = new ButtonItem("save", I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					TransitionDialog.this.widget.getTransition().setText((String) values.get("name"));
					TransitionDialog.this.widget.getTransition().setEffect((String) values.get("effect"));
					if (target.getFolder() != null)
						TransitionDialog.this.widget.getTransition().setTargetFolder(target.getFolderId());
					else
						TransitionDialog.this.widget.getTransition().setTargetFolder(null);

					TransitionDialog.this.widget.setContents((String) values.get("name"));

					destroy();
				}
			}
		});

		form.setItems(name, effect, target, save);

		if (widget.getTransition().getTargetFolder() != null) {
			folderService.getFolder(Session.get().getSid(), widget.getTransition().getTargetFolder(), false,
					new AsyncCallback<GUIFolder>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIFolder folder) {
							addItem(form);
							target.setFolder(folder);
						}
					});

		} else
			addItem(form);
	}
}