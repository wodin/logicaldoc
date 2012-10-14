package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties available for bulk update.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class BulkStandardPropertiesPanel extends DocumentDetailTab {
	private DynamicForm form = new DynamicForm();

	private VLayout container = new VLayout();

	private HLayout formsContainer = new HLayout();

	private ValuesManager vm = new ValuesManager();

	public BulkStandardPropertiesPanel(GUIDocument document) {
		super(document, null);
		setWidth100();
		setHeight100();
		container.setWidth100();
		container.setMembersMargin(5);
		addMember(container);

		formsContainer.setWidth100();
		formsContainer.setMembersMargin(10);

		container.setMembers(formsContainer);
		refresh();
	}

	private void refresh() {
		vm.clearErrors(false);

		/*
		 * Prepare the second form for the tags
		 */
		prepareForm();
		formsContainer.addMember(form);
	}

	private void prepareForm() {
		if (formsContainer.contains(form)) {
			formsContainer.removeMember(form);
			form.destroy();
		}

		form = new DynamicForm();
		form.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();

		SelectItem language = ItemFactory.newLanguageSelector("language", true, false);
		if (document.getLanguage() != null)
			language = ItemFactory.newLanguageSelector("language", false, false);

		language.setValue(document.getLanguage());
		items.add(language);

		if (Feature.enabled(Feature.TAGS)) {
			String mode = Session.get().getInfo().getConfig("tag.mode");

			final FormItem tagItem;
			if ("preset".equals(mode)) {
				tagItem = new SelectItem("tag");
				tagItem.setOptionDataSource(new TagsDS(mode));
			} else {
				tagItem = new ComboBoxItem("tag");
				((ComboBoxItem) tagItem).setPickListWidth(250);
				((ComboBoxItem) tagItem).setHideEmptyPickList(true);
				((ComboBoxItem) tagItem).setOptionDataSource(new TagsDS(null));
				tagItem.setHint(I18N.message("pressentertoaddtag"));
			}

			tagItem.setValueField("word");
			tagItem.setTitle(I18N.message("tag"));
			tagItem.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					if (event.getItem().getValue() != null) {
						String value = event.getItem().getValue() + "";
						event.getItem().clearValue();
						event.getItem().setValue(value);
					}
				}
			});
			tagItem.setHintStyle("hint");

			tagItem.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase())) {
						document.addTag(tagItem.getValue().toString().trim());
						tagItem.clearValue();
						refresh();
					}
				}
			});

			items.add(tagItem);
			FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
			int i = 0;
			if (document.getTags() != null)
				for (String str : document.getTags()) {
					final StaticTextItem tgItem = ItemFactory.newStaticTextItem("tag" + i++, "tag", str);
					tgItem.setIcons(icon);
					tgItem.addIconClickHandler(new IconClickHandler() {
						public void onIconClick(IconClickEvent event) {
							document.removeTag((String) tgItem.getValue());
							refresh();
						}
					});
					items.add(tgItem);
				}
		}

		form.setItems(items.toArray(new FormItem[0]));
	}

	@SuppressWarnings("unchecked")
	public boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setLanguage((String) values.get("language"));
		}
		return !vm.hasErrors();
	}
}