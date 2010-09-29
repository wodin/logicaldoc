package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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

		path = new Label(I18N.message("path") + ": " + document.getPathExtended());
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
			formsContainer.removeMember(form1);

		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);
		form1.setWidth(300);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(document.getId()));

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_date"));
		StaticTextItem creation = ItemFactory.newStaticTextItem("creation", "createdon",
				formatter.format((Date) document.getCreation()));

		StaticTextItem creator = ItemFactory.newStaticTextItem("creator", "creator", document.getCreator());

		StaticTextItem date = ItemFactory.newStaticTextItem("date", "publishedon",
				formatter.format((Date) document.getDate()));

		StaticTextItem publisher = ItemFactory.newStaticTextItem("publisher", "publisher", document.getPublisher());

		StaticTextItem title = ItemFactory.newStaticTextItem("title", "title", document.getTitle());
		title.addChangedHandler(changedHandler);
		title.setRequired(true);
		title.setDisabled(!update);

		StaticTextItem version = ItemFactory.newStaticTextItem("version", "version", document.getVersion());

		StaticTextItem fileVersion = ItemFactory.newStaticTextItem("fileVersion", "fileversion",
				document.getFileVersion());

		StaticTextItem filename = ItemFactory.newStaticTextItem("fileName", "filename", document.getFileName());

		form1.setItems(id, title, version, fileVersion, filename, creation, date, creator, publisher);
		formsContainer.addMember(form1, 0);

		/*
		 * Prepare the second form for the tags
		 */
		if (formsContainer.contains(form2))
			formsContainer.removeMember(form2);

		form2 = new DynamicForm();
		form2.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();

		TextItem customId = ItemFactory.newTextItem("customId", "customid", document.getCustomId());
		customId.addChangedHandler(changedHandler);
		customId.setRequired(true);
		customId.setDisabled(!update);
		items.add(customId);

		SelectItem language = ItemFactory.newLanguageSelector("language", false, false);
		language.addChangedHandler(changedHandler);
		language.setDisabled(!update);
		language.setValue(document.getLanguage());
		items.add(language);

		final ComboBoxItem tagItem = new ComboBoxItem("tag");
		tagItem.setTitle(I18N.message("tag"));
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

		FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
		int i = 0;
		for (String str : document.getTags()) {
			final StaticTextItem tgItem = ItemFactory.newStaticTextItem("tag" + i++, "tag", str);
			tgItem.setIcons(icon);
			tgItem.addIconClickHandler(new IconClickHandler() {
				public void onIconClick(IconClickEvent event) {
					document.removeTag((String) tgItem.getValue());
					changedHandler.onChanged(null);

					// Mark the item as deleted
					tgItem.setTextBoxStyle("deletedItem");
					tgItem.setTitleStyle("deletedItem");
					tgItem.setIcons(ItemFactory.newItemIcon("blank.gif"));
				}
			});
			items.add(tgItem);
		}

		form2.setItems(items.toArray(new FormItem[0]));
		formsContainer.addMember(form2, 1);

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

		if (Feature.visible(Feature.PREVIEW)) {
			if (Feature.enabled(Feature.PREVIEW)) {
				Image preview = new Image("thumbnail?docId=" + document.getId() + "&fileVersion="
						+ document.getFileVersion());
				rightPanel.addMember(preview);
			} else
				rightPanel.addMember(new FeatureDisabled());
		}

		formsContainer.addMember(rightPanel, 2);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setCustomId((String) values.get("customId"));
			document.setTitle((String) values.get("title"));
			document.setLanguage((String) values.get("language"));
		}
		return !vm.hasErrors();
	}
}