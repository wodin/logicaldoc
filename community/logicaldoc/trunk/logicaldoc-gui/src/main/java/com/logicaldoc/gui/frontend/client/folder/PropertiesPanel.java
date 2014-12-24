package com.logicaldoc.gui.frontend.client.folder;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
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

	private static final int DEFAULT_ITEM_WIDTH = 250;

	private DynamicForm form = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private boolean update = false;

	public PropertiesPanel(GUIFolder folder, ChangedHandler changedHandler) {
		super(folder, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		update = folder.hasPermission(Constants.PERMISSION_RENAME);
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
		name.setWidth(200);
		DoesntContainValidator validator = new DoesntContainValidator();
		validator.setSubstring("/");
		validator.setErrorMessage(I18N.message("invalidchar"));
		name.setValidators(validator);
		if (folder.hasPermission(Constants.PERMISSION_RENAME))
			name.addChangedHandler(changedHandler);
		name.setRequired(true);

		IntegerItem position = ItemFactory.newIntegerItem("position", "position", folder.getPosition());
		if (folder.hasPermission(Constants.PERMISSION_RENAME))
			position.addChangedHandler(changedHandler);
		position.setRequired(true);

		TextItem description = ItemFactory.newTextItem("description", "description", folder.getDescription());
		description.setWidth(250);
		if (folder.hasPermission(Constants.PERMISSION_RENAME))
			description.addChangedHandler(changedHandler);

		StaticTextItem creation = ItemFactory.newStaticTextItem(
				"creation",
				"createdon",
				Util.padLeft(
						I18N.formatDate((Date) folder.getCreation()) + " " + I18N.message("by") + " "
								+ folder.getCreator(), 40));
		creation.setTooltip(I18N.formatDate((Date) folder.getCreation()) + " " + I18N.message("by") + " "
				+ folder.getCreator());
		creation.setWidth(DEFAULT_ITEM_WIDTH);

		LinkItem pathItem = ItemFactory.newLinkItem("path", folder.getPathExtended());
		pathItem.setTitle(I18N.message("path"));
		pathItem.setValue(Util.contextPath() + "?folderId=" + folder.getId());
		pathItem.addChangedHandler(changedHandler);
		pathItem.setWidth(400);

		LinkItem barcode = ItemFactory.newLinkItem("barcode", I18N.message("generatebarcode"));
		barcode.setTarget("_blank");
		barcode.setTitle(I18N.message("barcode"));
		barcode.setValue(GWT.getHostPageBaseURL() + "barcode?sid=" + Session.get().getSid() + "&code=" + folder.getId()
				+ "&width=400&height=150");

		StaticTextItem documents = ItemFactory.newStaticTextItem("documents", "documents",
				"" + folder.getDocumentCount());
		StaticTextItem subfolders = ItemFactory
				.newStaticTextItem("folders", "folders", "" + folder.getSubfolderCount());

		name.setDisabled(!update);
		description.setDisabled(!update);
		position.setDisabled(!update);

		if (folder.isDefaultWorkspace()) {
			if (Feature.enabled(Feature.BARCODES))
				form.setItems(idItem, pathItem, position, creation, documents, subfolders, barcode);
			else
				form.setItems(idItem, pathItem, position, creation, documents, subfolders);
		} else if (Feature.enabled(Feature.BARCODES))
			form.setItems(idItem, pathItem, name, position, description, creation, documents, subfolders, barcode);
		else
			form.setItems(idItem, pathItem, name, position, description, creation, documents, subfolders);
		addMember(form);
	}

	boolean validate() {
		vm.validate();
		folder.setPosition(Integer.parseInt(vm.getValueAsString("position")));
		
		if (!folder.isDefaultWorkspace()) {
			folder.setName(vm.getValueAsString("name").replaceAll("/", ""));
			folder.setDescription(vm.getValueAsString("description"));
		}	
		return !vm.hasErrors();
	}
}