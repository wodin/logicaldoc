package com.logicaldoc.gui.frontend.client.security;

import java.util.Map;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows group's standard properties
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GroupPropertiesPanel extends HLayout {
	private DynamicForm form1 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private GUIGroup group;

	private ChangedHandler changedHandler;

	public GroupPropertiesPanel(GUIGroup group, ChangedHandler changedHandler) {
		if (group == null) {
			setMembers(GroupsPanel.SELECT_GROUP);
			return;
		}

		this.group = group;
		this.changedHandler = changedHandler;
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		refresh();
	}

	private void refresh() {
		boolean readonly = (changedHandler == null);
		vm.clearValues();
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);

		TextItem id = new TextItem();
		id.setTitle(I18N.getMessage("id"));
		id.setDisabled(true);
		id.setValue(group.getId());

		TextItem name = new TextItem("name");
		name.setTitle(I18N.getMessage("name"));
		name.setValue(group.getName());
		name.setRequired(true);
		name.setDisabled(readonly || group.getId() != 0);
		if (!readonly)
			name.addChangedHandler(changedHandler);

		TextItem description = new TextItem("description");
		description.setTitle(I18N.getMessage("description"));
		description.setValue(group.getDescription());
		description.setDisabled(readonly);
		if (!readonly)
			description.addChangedHandler(changedHandler);

		ComboBoxItem inherit = ItemFactory.newGroupSelector("inherit", I18N.getMessage("inheritgroup"));
		inherit.setVisible(!readonly);
		if (!readonly)
			inherit.addChangedHandler(changedHandler);

		form1.setItems(id, name, description, inherit);
		addMember(form1);

	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			group.setDescription((String) values.get("description"));
			group.setName((String) values.get("name"));
			if (values.get("inherit") != null)
				group.setInheritGroupId(Long.parseLong((String) values.get("inherit")));
			else
				group.setInheritGroupId(null);
		}
		return !vm.hasErrors();
	}
}