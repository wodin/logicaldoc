package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ColorItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.DoesntContainValidator;

/**
 * Shows folder's standard properties and read-only data
 * 
 * @author Marco Meschieri - LogicalDOC
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
		if (folder.getFoldRef() != null)
			idItem.setTooltip(I18N.message("thisisalias") + ": " + folder.getFoldRef());

		TextItem name = ItemFactory.newTextItem("name", "name", folder.getName());
		name.setWidth(200);
		DoesntContainValidator validator = new DoesntContainValidator();
		validator.setSubstring("/");
		validator.setErrorMessage(I18N.message("invalidchar"));
		name.setValidators(validator);
		if (folder.hasPermission(Constants.PERMISSION_RENAME))
			name.addChangedHandler(changedHandler);
		name.setRequired(true);

		SpinnerItem position = ItemFactory.newSpinnerItem("position", "position", folder.getPosition());
		if (folder.hasPermission(Constants.PERMISSION_RENAME))
			position.addChangedHandler(changedHandler);
		position.setRequired(true);

		SelectItem storage = ItemFactory.newStorageSelector("storage", folder.getStorage());
		storage.setDisabled(!folder.isWrite());
		boolean storageVisible = folder.isWorkspace() && folder.getFoldRef() == null;
		storage.setVisible(storageVisible);
		if (folder.isWrite() && storageVisible)
			storage.addChangedHandler(changedHandler);

		SpinnerItem maxVersions = ItemFactory.newSpinnerItem("maxVersions", I18N.message("maxversions"),
				folder.getMaxVersions());
		maxVersions.setDisabled(!folder.isWrite());
		boolean maxVersionsVisible = folder.isWorkspace() && folder.getFoldRef() == null;
		maxVersions.setVisible(storageVisible);
		if (folder.isWrite() && maxVersionsVisible)
			maxVersions.addChangedHandler(changedHandler);

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

		ColorItem color = ItemFactory.newColorItemPicker("color", "color", folder.getColor());
		color.addChangedHandler(changedHandler);

		LinkItem pathItem = ItemFactory.newLinkItem("path", folder.getPathExtended());
		pathItem.setTitle(I18N.message("path"));
		pathItem.setValue(Util.displaydURL(null, folder.getId()));
		pathItem.addChangedHandler(changedHandler);
		pathItem.setWidth(400);

		LinkItem barcode = ItemFactory.newLinkItem("barcode", I18N.message("generatebarcode"));
		barcode.setTarget("_blank");
		barcode.setTitle(I18N.message("barcode"));
		barcode.setValue(GWT.getHostPageBaseURL() + "barcode?code=" + folder.getId() + "&width=400&height=150");

		StaticTextItem documents = ItemFactory.newStaticTextItem("documents", "documents",
				"" + folder.getDocumentCount());
		StaticTextItem subfolders = ItemFactory
				.newStaticTextItem("folders", "folders", "" + folder.getSubfolderCount());

		name.setDisabled(!update);
		description.setDisabled(!update);
		position.setDisabled(!update);
		color.setDisabled(!update);

		List<FormItem> items = new ArrayList<FormItem>();
		items.addAll(Arrays.asList(new FormItem[] { idItem, pathItem, name, description, color, position, storage,
				maxVersions, creation, documents, subfolders, barcode }));
		if (!Feature.enabled(Feature.BARCODES))
			items.remove(barcode);
		if (!Feature.enabled(Feature.MULTI_STORAGE))
			items.remove(storage);
		if (folder.isDefaultWorkspace())
			items.remove(name);

		form.setItems(items.toArray(new FormItem[0]));
		addMember(form);
	}

	boolean validate() {
		vm.validate();
		folder.setPosition(Integer.parseInt(vm.getValueAsString("position")));
		folder.setColor(vm.getValueAsString("color"));

		if (!folder.isDefaultWorkspace()) {
			folder.setName(vm.getValueAsString("name").replaceAll("/", ""));
			folder.setDescription(vm.getValueAsString("description"));
		}

		if (folder.isWorkspace()) {
			try {
				folder.setStorage(Integer.parseInt(vm.getValueAsString("storage")));
			} catch (Throwable t) {
				folder.setStorage(null);
			}
			try {
				folder.setMaxVersions(Integer.parseInt(vm.getValueAsString("maxVersions")));
				if (folder.getMaxVersions() != null && folder.getMaxVersions() < 1)
					folder.setMaxVersions(null);
			} catch (Throwable t) {
				folder.setMaxVersions(null);
			}
		}
		return !vm.hasErrors();
	}
}