package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
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

	protected boolean tagsInitialized = false;
	
	private MultiComboBoxItem tagItem;

	public BulkStandardPropertiesPanel(GUIDocument document) {
		super(document, null, null);
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
		language.setDisabled(!updateEnabled);

		language.setValue(document.getLanguage());
		items.add(language);

		if (Feature.enabled(Feature.TAGS)) {
			String mode = Session.get().getInfo().getConfig("tag.mode");
			final DataSource ds;
			if ("preset".equals(mode)) {
				ds = new TagsDS(mode);
			} else {
				ds = new TagsDS(null);
			}

			tagItem = new MultiComboBoxItem("tag", I18N.message("tag"));
			tagItem.setLayoutStyle(MultiComboBoxLayoutStyle.FLOW);
			tagItem.setWidth(200);
			tagItem.setMultiple(true);
			tagItem.setOptionDataSource(ds);
			tagItem.setValueField("word");
			tagItem.setDisplayField("word");
			tagItem.setValues((Object[]) document.getTags());
			tagItem.setDisabled(!updateEnabled);
			tagItem.addChangedHandler(new ChangedHandler() {

				@Override
				public void onChanged(ChangedEvent event) {
					/*
					 * At initialization time this method is invoked several
					 * times until when it contains all the tags of the document
					 */
					if (tagsInitialized)
						changedHandler.onChanged(null);
					else {
						if ((tagItem.getValues().length == 0 && document.getTags() == null)
								|| tagItem.getValues().length == document.getTags().length)
							// The item contains all the tags of the document,
							// so consider it as initialized
							tagsInitialized = true;
					}

				}
			});

			PickerIcon addPicker = new PickerIcon(new Picker("[SKIN]/actions/add.png"), new FormItemClickHandler() {
				public void onFormItemClick(FormItemIconClickEvent event) {
					LD.askforValue(I18N.message("newtag"), I18N.message("tag"), "", "200", new ValueCallback() {
						@Override
						public void execute(String value) {
							if (value == null)
								return;

							String input = value.trim().replaceAll(",", "");
							if (!"".equals(input)) {
								// Get the user's inputed tags, he may have
								// wrote more than one tag
								List<String> tags = new ArrayList<String>();
								String token = input.trim().replace(',', ' ');
								if (!"".equals(token)) {
									tags.add(token);

									// Put the new tag in the options
									Record record = new Record();
									record.setAttribute("word", token);
									ds.addData(record);
								}

								if (tags.isEmpty())
									return;

								// Add the old tags to the new ones
								String[] oldVal = tagItem.getValues();
								for (int i = 0; i < oldVal.length; i++)
									if (!tags.contains(oldVal[i]))
										tags.add(oldVal[i]);

								tagItem.setValues((Object[]) tags.toArray(new String[0]));
								changedHandler.onChanged(null);
							}
						}
					});
				}
			});
			addPicker.setWidth(16);
			addPicker.setHeight(16);
			addPicker.setPrompt(I18N.message("newtag"));

			if ("free".equals(mode))
				tagItem.setIcons(addPicker);
			if (updateEnabled)
				items.add(tagItem);
			
			FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
			int i = 0;
			if (document.getTags() != null)
				for (String str : document.getTags()) {
					final StaticTextItem tgItem = ItemFactory.newStaticTextItem("tag" + i++, "tag", str);
					if (updateEnabled) {
						tgItem.setIcons(icon);
						tgItem.addIconClickHandler(new IconClickHandler() {
							public void onIconClick(IconClickEvent event) {
								document.removeTag((String) tgItem.getValue());
								refresh();
							}
						});
					}
					tgItem.setDisabled(!updateEnabled);
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
			document.setTags(tagItem.getValues());
		}
		return !vm.hasErrors();
	}
}