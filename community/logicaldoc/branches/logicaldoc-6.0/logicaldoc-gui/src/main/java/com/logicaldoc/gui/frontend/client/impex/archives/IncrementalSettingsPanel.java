package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.JavaScriptObject;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIIncrementalArchive;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FolderChangeListener;
import com.logicaldoc.gui.common.client.widgets.FolderSelector;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the settings of an incremental import configuration
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class IncrementalSettingsPanel extends VLayout {
	private GUIIncrementalArchive incremental;

	private ValuesManager vm = new ValuesManager();

	private DynamicForm form = new DynamicForm();

	private FolderSelector folderSelector = new FolderSelector("folder", false);

	public IncrementalSettingsPanel(GUIIncrementalArchive incremental, ChangedHandler changedHandler,
			FolderChangeListener folderListener) {
		this.incremental = incremental;

		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);

		TextItem prefix = ItemFactory.newSimpleTextItem("prefix", "prefix", incremental.getPrefix());
		prefix.setRequired(true);
		prefix.addChangedHandler(changedHandler);

		SelectItem type = ItemFactory.newArchiveTypeSelector();
		type.setValue(Integer.toString(incremental.getType()));
		type.addChangedHandler(changedHandler);

		IntegerItem frequency = ItemFactory.newIntegerItem("frequency", "frequency", incremental.getFrequency());
		IntegerRangeValidator min = new IntegerRangeValidator();
		min.setMin(1);
		frequency.setValidators(min);
		frequency.addChangedHandler(changedHandler);

		folderSelector.setFolder(incremental.getFolder());
		folderSelector.setRequired(true);
		folderSelector.addFolderChangeListener(folderListener);

		SelectItem templates = ItemFactory.newTemplateSelector(true, null);
		templates.addChangedHandler(changedHandler);
		templates.setValues(incremental.getTemplateIds());

		if (Feature.visible(Feature.PAPER_DEMATERIALIZATION) && incremental.getId() == 0) {
			form.setFields(prefix, type, frequency, folderSelector, templates);
			if (!Feature.enabled(Feature.PAPER_DEMATERIALIZATION))
				type.setDisabled(true);
		} else
			form.setFields(prefix, frequency, folderSelector, templates);

		addMember(form);
	}

	public boolean validate() {
		vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			incremental.setPrefix(vm.getValueAsString("prefix").toString());
			if (vm.getValueAsString("archivetype") != null)
				incremental.setType(Integer.parseInt(vm.getValueAsString("archivetype")));
			incremental.setFrequency(Integer.parseInt(vm.getValueAsString("frequency")));
			incremental.setFolder(folderSelector.getFolder());

			if (vm.getValues().get("template") != null) {
				String templateIdString = vm.getValues().get("template").toString().trim().replace("[", "")
						.replace("]", "");
				if (!templateIdString.isEmpty()) {
					String[] selection = templateIdString.split(",");
					List<GUITemplate> templates = new ArrayList<GUITemplate>();
					for (String selectionId : selection) {
						GUITemplate currentTemplate = new GUITemplate();
						currentTemplate.setId(Long.parseLong(selectionId.trim()));
						templates.add(currentTemplate);
					}
					incremental.setTemplates(templates.toArray(new GUITemplate[0]));
				}
			}

			return true;
		} else
			return false;
	}
}