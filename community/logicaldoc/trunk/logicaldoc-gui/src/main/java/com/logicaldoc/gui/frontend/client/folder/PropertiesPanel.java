package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ColorItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.DoesntContainValidator;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows folder's standard properties and read-only data
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.0
 */
public class PropertiesPanel extends FolderDetailTab {

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private static final int DEFAULT_ITEM_WIDTH = 250;

	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private boolean updateEnabled = false;

	private HLayout columns = new HLayout();

	protected MultiComboBoxItem tagItem = null;

	public PropertiesPanel(GUIFolder folder, ChangedHandler changedHandler) {
		super(folder, changedHandler);
		setWidth100();
		setHeight100();

		this.updateEnabled = changedHandler != null;
		columns.setWidth100();
		columns.setMembersMargin(10);
		setMembers(columns);

		refresh();
	}

	private void refresh() {
		vm = new ValuesManager();

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);

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

		name.setDisabled(!updateEnabled);
		description.setDisabled(!updateEnabled);
		position.setDisabled(!updateEnabled);
		color.setDisabled(!updateEnabled);

		List<FormItem> items = new ArrayList<FormItem>();
		items.addAll(Arrays.asList(new FormItem[] { idItem, pathItem, name, description, color, position, storage,
				maxVersions, creation, documents, subfolders, barcode }));
		if (!Feature.enabled(Feature.BARCODES))
			items.remove(barcode);
		if (!Feature.enabled(Feature.MULTI_STORAGE))
			items.remove(storage);
		if (folder.isDefaultWorkspace())
			items.remove(name);

		form1.setItems(items.toArray(new FormItem[0]));

		columns.addMember(form1);

		/*
		 * Prepare the second form for the tags
		 */
		prepareRightForm();

	}

	private void prepareRightForm() {
		if (columns.contains(form2)) {
			columns.removeMember(form2);
			form2.destroy();
		}

		form2 = new DynamicForm();
		form2.setAutoWidth();
		form2.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();

		if (Feature.enabled(Feature.TAGS)) {
			String mode = Session.get().getConfig("tag.mode");
			final TagsDS ds = new TagsDS(null, true, null, folder.getId());

			tagItem = ItemFactory.newTagsComboBoxItem("tag", "tag", ds, (Object[]) folder.getTags());
			tagItem.setDisabled(!updateEnabled);
			tagItem.addChangedHandler(changedHandler);

			final TextItem newTagItem = ItemFactory.newTextItem("newtag", "newtag", null);
			newTagItem.setRequired(false);
			newTagItem.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if (newTagItem.validate() && newTagItem.getValue() != null && event.getKeyName() != null
							&& "enter".equals(event.getKeyName().toLowerCase())) {
						String input = newTagItem.getValueAsString().trim();
						newTagItem.clearValue();

						if (!"".equals(input)) {
							String[] tokens = input.split("\\,");

							int min = Integer.parseInt(Session.get().getConfig("tag.minsize"));
							int max = Integer.parseInt(Session.get().getConfig("tag.maxsize"));
							boolean containsInvalid = false;
							List<String> tags = new ArrayList<String>();
							for (String token : tokens) {
								String t = token.trim();

								if (t.length() < min || t.length() > max) {
									containsInvalid = true;
									continue;
								}

								tags.add(t);

								// Add the old tags to the new ones
								String[] oldVal = tagItem.getValues();
								for (int i = 0; i < oldVal.length; i++)
									if (!tags.contains(oldVal[i]))
										tags.add(oldVal[i]);

								// Put the new tag in the options
								Record record = new Record();
								record.setAttribute("index", t);
								record.setAttribute("word", t);
								ds.addData(record);
							}

							// Update the tag item and trigger the change
							tagItem.setValues((Object[]) tags.toArray(new String[0]));
							changedHandler.onChanged(null);

							if (containsInvalid)
								SC.warn(I18N.message("sometagaddedbecauseinvalid"));
						}
					}
				}
			});

			items.add(tagItem);
			if ("free".equals(mode) && updateEnabled)
				items.add(newTagItem);
		}

		ButtonItem applyTags = new ButtonItem(I18N.message("applytosubfolders"));
		applyTags.setAutoFit(true);
		applyTags.setEndRow(true);
		applyTags.setColSpan(2);
		applyTags.setAlign(Alignment.RIGHT);
		applyTags.setDisabled(!updateEnabled);
		applyTags.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ContactingServer.get().show();
				folderService.applyTags(folder.getId(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void v) {
						ContactingServer.get().hide();
					}
				});
			}
		});
		items.add(applyTags);

		form2.setItems(items.toArray(new FormItem[0]));
		columns.addMember(form2);
	}

	boolean validate() {
		vm.validate();

		if (!vm.hasErrors()) {
			folder.setPosition(Integer.parseInt(vm.getValueAsString("position")));
			folder.setColor(vm.getValueAsString("color"));
			folder.setTags(tagItem.getValues());

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
		}
		return !vm.hasErrors();
	}
}