package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
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
import com.smartgwt.client.widgets.layout.HLayout;
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

	private VLayout container = new VLayout();

	private HLayout formsContainer = new HLayout();

	private VLayout rightPanel = new VLayout();

	private ValuesManager vm = new ValuesManager();

	private Canvas path;

	public StandardPropertiesPanel(GUIDocument document, ChangedHandler changedHandler) {
		super(document, changedHandler);
		setWidth100();
		setHeight100();
		container.setWidth100();
		container.setMembersMargin(5);
		addMember(container);

		path = new Label(I18N.getMessage("path") + ": " + document.getPathExtended());
		path.setWidth100();
		path.setHeight(15);

		formsContainer.setWidth100();
		formsContainer.setMembersMargin(10);

		container.setMembers(path, formsContainer);
		refresh();
	}

	private void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (formsContainer.contains(form1))
			formsContainer.removeChild(form1);

		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);

		TextItem id = new TextItem();
		id.setTitle(I18N.getMessage("id"));
		id.setValue(document.getId());
		id.setDisabled(true);

		TextItem customId = new TextItem("customId");
		customId.setTitle(I18N.getMessage("customid"));
		customId.setValue(document.getCustomId());
		customId.addChangedHandler(changedHandler);
		customId.setRequired(true);
		customId.setDisabled(!update);

		DateItem creation = ItemFactory.newDateItem("creation", I18N.getMessage("createdon"));
		creation.setValue(document.getCreation());
		creation.setShowPickerIcon(false);
		creation.setDisabled(true);

		TextItem creator = new TextItem("creator");
		creator.setTitle(I18N.getMessage("creator"));
		creator.setValue(document.getCreator());
		creator.setDisabled(true);

		DateItem date = ItemFactory.newDateItem("date", I18N.getMessage("publishedon"));
		date.setValue(document.getDate());
		date.setShowPickerIcon(false);
		date.setDisabled(true);

		TextItem publisher = new TextItem("publisher");
		publisher.setTitle(I18N.getMessage("publisher"));
		publisher.setValue(document.getPublisher());
		publisher.setDisabled(true);

		TextItem title = new TextItem("title");
		title.setTitle(I18N.getMessage("title"));
		title.setValue(document.getTitle());
		title.addChangedHandler(changedHandler);
		title.setRequired(true);
		title.setDisabled(!update);

		TextItem version = new TextItem("version");
		version.setTitle(I18N.getMessage("version"));
		version.setValue(document.getVersion());
		version.setDisabled(true);

		TextItem fileVersion = new TextItem("fileVersion");
		fileVersion.setTitle(I18N.getMessage("fileversion"));
		fileVersion.setValue(document.getFileVersion());
		fileVersion.setDisabled(true);

		TextItem filename = new TextItem("fileName");
		filename.setTitle(I18N.getMessage("filename"));
		filename.setValue(document.getFileName());
		filename.setDisabled(true);

		form1.setItems(id, customId, version, title, fileVersion, filename, creation, date, creator, publisher);
		formsContainer.addMember(form1);

		/*
		 * Prepare the second form for the tags
		 */
		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();
//		form2.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();
		final ComboBoxItem tagItem = new ComboBoxItem("tag");
		tagItem.setTitle(I18N.getMessage("tag"));
		tagItem.setPickListWidth(250);
		tagItem.setOptionDataSource(TagsDS.getInstance());
		tagItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase())) {
					document.addTag(tagItem.getValue().toString().trim());
					tagItem.clearValue();
					changedHandler.onChanged(null);
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
		formsContainer.addMember(form2);

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

		formsContainer.addMember(rightPanel);
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