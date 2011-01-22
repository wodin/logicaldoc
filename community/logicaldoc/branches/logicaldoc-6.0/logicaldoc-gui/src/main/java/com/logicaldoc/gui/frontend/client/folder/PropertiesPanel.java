package com.logicaldoc.gui.frontend.client.folder;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.DoesntContainValidator;

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

		StaticTextItem idItem = ItemFactory.newStaticTextItem("id", "id", Long.toString(folder.getId()));

		TextItem name = ItemFactory.newTextItem("name", "name", folder.getName());
		DoesntContainValidator validator = new DoesntContainValidator();
		validator.setSubstring("/");
		validator.setErrorMessage(I18N.message("invalidchar"));
		name.setValidators(validator);
		name.addChangedHandler(changedHandler);
		name.setRequired(true);

		TextItem description = ItemFactory.newTextItem("description", "description", folder.getDescription());
		description.addChangedHandler(changedHandler);

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_date"));
		StaticTextItem creation = ItemFactory.newStaticTextItem("creation", "createdon",
				formatter.format(folder.getCreation()));

		StaticTextItem creator = ItemFactory.newStaticTextItem("creator", "creator", folder.getCreator());

		StaticTextItem pathItem = ItemFactory.newStaticTextItem("path", "path", folder.getPathExtended());
		pathItem.setTitle(I18N.message("path"));
		pathItem.setValue(folder.getPathExtended());
		pathItem.addChangedHandler(changedHandler);
		pathItem.setWidth(300);

		StaticTextItem documents = ItemFactory.newStaticTextItem("documents", "documents",
				"" + folder.getDocumentCount());
		StaticTextItem subfolders = ItemFactory
				.newStaticTextItem("folders", "folders", "" + folder.getSubfolderCount());

		if (!folder.hasPermission(Constants.PERMISSION_RENAME)) {
			name.setDisabled(true);
			description.setDisabled(true);
		}

		if (folder.getId() == Constants.DOCUMENTS_FOLDERID)
			form.setItems(idItem, pathItem, creation, creator, documents, subfolders);
		else
			form.setItems(idItem, pathItem, name, description, creation, creator, documents, subfolders);
		addMember(form);
	}

	boolean validate() {
		vm.validate();
		if (folder.getId() != Constants.DOCUMENTS_FOLDERID) {
			folder.setName(vm.getValueAsString("name").replaceAll("/", ""));
			folder.setDescription(vm.getValueAsString("description"));
		}
		return !vm.hasErrors();
	}
}