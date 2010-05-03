package com.logicaldoc.gui.frontend.client.folder;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
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
		vm = new ValuesManager();

		if (form != null)
			form.destroy();

		if (contains(form))
			removeChild(form);
		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);

		StaticTextItem idItem = new StaticTextItem();
		idItem.setTitle(I18N.getMessage("id"));
		idItem.setValue(folder.getId());

		TextItem nameItem = new TextItem("name");
		nameItem.setTitle(I18N.getMessage("name"));
		nameItem.setValue(folder.getName());
		nameItem.addChangedHandler(changedHandler);
		nameItem.setRequired(true);

		TextItem descriptionItem = new TextItem("description");
		descriptionItem.setTitle(I18N.getMessage("description"));
		descriptionItem.setValue(folder.getDescription());
		descriptionItem.addChangedHandler(changedHandler);

		StaticTextItem pathItem = new StaticTextItem("path");
		pathItem.setTitle(I18N.getMessage("path"));
		pathItem.setValue(folder.getPathExtended());
		pathItem.addChangedHandler(changedHandler);
		pathItem.setWidth(300);

		if (folder.getId() == Constants.DOCUMENTS_FOLDERID)
			form.setItems(idItem, pathItem);
		else
			form.setItems(idItem, pathItem, nameItem, descriptionItem);
		addMember(form);
	}

	boolean validate() {
		vm.validate();
		folder.setName(vm.getValueAsString("name").replaceAll("/", ""));
		folder.setDescription(vm.getValueAsString("description"));
		return !vm.hasErrors();
	}
}