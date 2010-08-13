package com.logicaldoc.gui.frontend.client.impex.archives;

import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the settings form for an import archive.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImportSettingsPanel extends VLayout {
	private GUIArchive archive;

	private ValuesManager vm = new ValuesManager();

	private DynamicForm form = new DynamicForm();

	public ImportSettingsPanel(GUIArchive archive, ChangedHandler changedHandler) {
		this.archive = archive;

		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);

		TextItem description = ItemFactory.newTextItem("description", "description", archive.getDescription());
		description.addChangedHandler(changedHandler);

		RadioGroupItem importTemplates = ItemFactory.newBooleanSelector("importtemplates", "importtemplates");
		importTemplates.setValue(archive.getImportTemplate() == 1 ? "yes" : "no");
		importTemplates.addChangedHandler(changedHandler);

		SelectItem options = ItemFactory.newImportCustomIds();
		options.setValue(Integer.toString(archive.getImportCustomId()));

		form.setFields(description, importTemplates, options);

		addMember(form);
	}

	public boolean validate() {
		vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			archive.setDescription(vm.getValueAsString("description").toString());
			archive.setImportCustomId("yes".equals(vm.getValueAsString("importcids")) ? 1 : 0);
			archive.setImportTemplate("yes".equals(vm.getValueAsString("importtemplates")) ? 1 : 0);
			return true;
		} else
			return false;
	}
}