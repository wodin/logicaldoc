package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.data.TagsDS;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StandardPropertiesPanel extends DocumentDetailTab {
	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private VLayout rightPanel = new VLayout();

	private ValuesManager vm = new ValuesManager();

	public StandardPropertiesPanel(GUIDocument document, ChangedHandler changedHandler) {
		super(document, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		refresh();
	}

	private void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);
		List<FormItem> items = new ArrayList<FormItem>();

		TextItem idItem = new TextItem();
		idItem.setTitle(I18N.getMessage("id"));
		idItem.setValue(document.getId());
		idItem.setDisabled(true);

		TextItem customIdItem = new TextItem("customId");
		customIdItem.setTitle(I18N.getMessage("customid"));
		customIdItem.setValue(document.getCustomId());
		customIdItem.addChangedHandler(changedHandler);
		customIdItem.setRequired(true);
		customIdItem.setDisabled(!document.getFolder().isWrite());

		DateItem creationItem = ItemFactory.newDateItem("creation", I18N.getMessage("createdon"));
		creationItem.setValue(document.getCreation());
		creationItem.setShowPickerIcon(false);
		creationItem.setDisabled(true);

		TextItem creatorItem = new TextItem("creator");
		creatorItem.setTitle(I18N.getMessage("creator"));
		creatorItem.setValue(document.getCreator());
		creatorItem.setDisabled(true);

		DateItem dateItem = ItemFactory.newDateItem("date", I18N.getMessage("publishedon"));
		dateItem.setValue(document.getDate());
		dateItem.setShowPickerIcon(false);
		dateItem.setDisabled(true);

		TextItem publisherItem = new TextItem("publisher");
		publisherItem.setTitle(I18N.getMessage("publisher"));
		publisherItem.setValue(document.getPublisher());
		publisherItem.setDisabled(true);

		TextItem titleItem = new TextItem("title");
		titleItem.setTitle(I18N.getMessage("title"));
		titleItem.setValue(document.getTitle());
		titleItem.addChangedHandler(changedHandler);
		titleItem.setRequired(true);
		titleItem.setDisabled(!document.getFolder().isWrite());

		TextItem versionItem = new TextItem("version");
		versionItem.setTitle(I18N.getMessage("version"));
		versionItem.setValue(document.getVersion());
		versionItem.setDisabled(true);

		TextItem fileVersionItem = new TextItem("fileVersion");
		fileVersionItem.setTitle(I18N.getMessage("fileversion"));
		fileVersionItem.setValue(document.getFileVersion());
		fileVersionItem.setDisabled(true);

		TextItem fileNameItem = new TextItem("fileName");
		fileNameItem.setTitle(I18N.getMessage("filename"));
		fileNameItem.setValue(document.getFileName());
		fileNameItem.setDisabled(true);

		items.add(idItem);
		items.add(customIdItem);
		items.add(versionItem);
		items.add(fileVersionItem);
		items.add(titleItem);
		items.add(fileNameItem);
		items.add(creationItem);
		items.add(creatorItem);
		items.add(dateItem);
		items.add(publisherItem);

		form1.setItems(items.toArray(new FormItem[0]));
		addMember(form1);

		/*
		 * Prepare the second form for the tags
		 */
		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();
		form2.setValuesManager(vm);
		items.clear();
		final ComboBoxItem tagItem = new ComboBoxItem("tag");
		tagItem.addChangedHandler(changedHandler);
		tagItem.setTitle(I18N.getMessage("tag"));
		tagItem.setPickListWidth(250);
		tagItem.setOptionDataSource(TagsDS.getInstance());
		tagItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase())) {
					document.addTag(tagItem.getValue().toString().trim());
					tagItem.clearValue();
					refresh();
				}
			}
		});
		items.add(tagItem);

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(Util.imageUrl("application/delete.png"));
		int i = 0;
		for (String str : document.getTags()) {
			final StaticTextItem tgItem = new StaticTextItem();
			tgItem.setDefaultValue(str);
			tgItem.setName("tag" + i++);
			tgItem.setIcons(icon);
			tgItem.setTitle(I18N.getMessage("tag"));
			tgItem.addIconClickHandler(new IconClickHandler() {
				public void onIconClick(IconClickEvent event) {
					document.removeTag((String) tgItem.getValue());
					changedHandler.onChanged(null);
					refresh();
				}
			});
			items.add(tgItem);
		}

		form2.setItems(items.toArray(new FormItem[0]));
		addMember(form2);

		prepareRightPanel();
	}

	/**
	 * Prepare the right panel with the document's preview
	 */
	private void prepareRightPanel() {
		if (rightPanel != null) {
			rightPanel.destroy();
			if (contains(rightPanel))
				removeMember(rightPanel);
		}
		rightPanel = new VLayout();
		rightPanel.setMembersMargin(5);
		rightPanel.setPadding(4);
		rightPanel.setAlign(VerticalAlignment.TOP);

		Image preview = new Image("thumbnail?docId=" + document.getId() + "&versionId=" + document.getVersion());

		rightPanel.addMember(preview);

		addMember(rightPanel);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setCustomId((String) values.get("customId"));
			document.setTitle((String) values.get("title"));
			document.clearTags();

			for (String name : values.keySet()) {
				if (name.startsWith("tag"))
					document.addTag((String) values.get(name));
			}
		}
		return !vm.hasErrors();
	}
}