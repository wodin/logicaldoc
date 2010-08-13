package com.logicaldoc.gui.frontend.client.impex.folders;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIShare;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows import folder's advanced properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImportFolderAdvancedProperties extends ImportFolderDetailsTab {
	private DynamicForm form = new DynamicForm();

	private HLayout formsContainer = new HLayout();

	public ImportFolderAdvancedProperties(GUIShare share, ChangedHandler changedHandler) {
		super(share, changedHandler);
		setWidth100();
		setHeight100();
		setMembers(formsContainer);
		refresh();
	}

	private void refresh() {
		form.clearValues();
		form.clearErrors(false);

		if (form != null)
			form.destroy();

		if (formsContainer.contains(form))
			formsContainer.removeChild(form);

		form = new DynamicForm();
		form.setNumCols(2);
		form.setTitleOrientation(TitleOrientation.TOP);

		SelectItem depth = ItemFactory.newSelectItem("depth", null);
		depth.addChangedHandler(changedHandler);
		depth.setValueMap("5", "10", "15");
		depth.setValue("5");

		IntegerItem size = ItemFactory.newIntegerItem("sizemax", "sizemax", share.getMaxSize());
		size.addChangedHandler(changedHandler);
		size.setHint("KB");

		TextItem include = ItemFactory.newTextItem("include", "include", share.getIncludes());
		include.addChangedHandler(changedHandler);

		TextItem exclude = ItemFactory.newTextItem("exclude", "exclude", share.getExcludes());
		include.addChangedHandler(changedHandler);

		SelectItem template = ItemFactory.newTemplateSelector(false);
		template.addChangedHandler(changedHandler);

		CheckboxItem delImport = new CheckboxItem();
		delImport.setName("delImport");
		delImport.setTitle(I18N.message("deleteafterimport"));
		delImport.setRedrawOnChange(true);
		delImport.setWidth(50);
		delImport.setValue(share.isDelImport());
		delImport.addChangedHandler(changedHandler);

		TextItem tags = ItemFactory.newTextItem("tags", "tags", share.getTags());
		include.addChangedHandler(changedHandler);

		form.setItems(depth, size, template, include, exclude, tags, delImport);

		formsContainer.addMember(form);

	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		form.validate();
		if (!form.hasErrors()) {
			if(values.get("sizemax")==null)
				share.setMaxSize(null);
			else if (values.get("sizemax") instanceof Integer)
				share.setMaxSize((Integer) form.getValue("sizemax"));
			else
				share.setMaxSize(Integer.parseInt((String) values.get("sizemax")));

			share.setDepth(Integer.parseInt(values.get("depth").toString()));
			share.setIncludes((String) values.get("include"));
			share.setExcludes((String) values.get("exclude"));
			if (values.get("template") == null || "".equals((String) values.get("template")))
				share.setTemplateId(null);
			else
				share.setTemplateId(Long.parseLong((String) values.get("template")));
			share.setDelImport((Boolean) values.get("delImport"));
			share.setTags((String) values.get("tags"));
		}
		return !form.hasErrors();
	}
}