package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PropertiesPanel extends FolderDetailTab {
	private DynamicForm form = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	public PropertiesPanel(GUIFolder folder, ChangedHandler changedHandler) {
		super(folder, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		refresh();
	}

	private void refresh() {
		vm.clearValues();
		vm.cancel();
		vm.clearErrors(false);

		if (form != null)
			form.destroy();

		removeChild(form);
		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		List<FormItem> items = new ArrayList<FormItem>();

		TextItem idItem = new TextItem();
		idItem.setTitle(I18N.getMessage("id"));
		idItem.setValue(folder.getId());
		idItem.setDisabled(true);

		TextItem nameItem = new TextItem("name");
		nameItem.setTitle(I18N.getMessage("name"));
		nameItem.setValue(folder.getName());
		nameItem.addChangedHandler(changedHandler);
		nameItem.setRequired(true);

		TextItem descriptionItem = new TextItem("description");
		descriptionItem.setTitle(I18N.getMessage("description"));
		descriptionItem.setValue(folder.getDescription());
		descriptionItem.addChangedHandler(changedHandler);

		TextItem pathItem = new TextItem("path");
		pathItem.setTitle(I18N.getMessage("path"));
		pathItem.setValue(folder.getPathExtended());
		pathItem.addChangedHandler(changedHandler);
		pathItem.setWidth(300);
		pathItem.setDisabled(true);

		items.add(idItem);
		items.add(nameItem);
		items.add(descriptionItem);
		items.add(pathItem);

		form.setItems(idItem, nameItem, descriptionItem, pathItem);
		addMember(form);
	}

	boolean validate() {
		vm.validate();
		folder.setName(vm.getValueAsString("name"));
		folder.setDescription(vm.getValueAsString("description"));
		return !vm.hasErrors();
	}
}