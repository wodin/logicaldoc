package com.logicaldoc.gui.frontend.client.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserPropertiesPanel extends HLayout {
	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private GUIUser user;

	private ChangedHandler changedHandler;

	public UserPropertiesPanel(GUIUser user, ChangedHandler changedHandler) {
		if (user == null) {
			setMembers(UsersPanel.SELECT_USER);
			return;
		}

		this.user = user;
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
		form1.setNumCols(3);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);

		TextItem id = new TextItem();
		id.setTitle(I18N.getMessage("id"));
		id.setDisabled(true);
		id.setValue(user.getId());

		TextItem username = new TextItem("username");
		username.setTitle(I18N.getMessage("username"));
		username.setValue(user.getUserName());
		username.setRequired(true);
		username.setDisabled(readonly || user.getId() != 0);
		if (!readonly)
			username.addChangedHandler(changedHandler);

		CheckboxItem expires = new CheckboxItem("expires", I18N.getMessage("passwordexpires"));
		expires.setValue(user.isPasswordExpires());
		expires.setDisabled(readonly);
		if (!readonly)
			expires.addChangedHandler(changedHandler);

		CheckboxItem enabled = new CheckboxItem("enabled", I18N.getMessage("enabled"));
		enabled.setValue(user.isEnabled());
		if (readonly || "admin".equals(user.getUserName())) {
			enabled.setDisabled(true);
		} else {
			enabled.addChangedHandler(changedHandler);
		}

		TextItem firstname = new TextItem("firstname");
		firstname.setTitle(I18N.getMessage("firstname"));
		firstname.setValue(user.getFirstName());
		firstname.setRequired(true);
		firstname.setDisabled(readonly);
		if (!readonly)
			firstname.addChangedHandler(changedHandler);

		TextItem name = new TextItem("name");
		name.setTitle(I18N.getMessage("lastname"));
		name.setValue(user.getName());
		name.setRequired(true);
		name.setDisabled(readonly);
		if (!readonly)
			name.addChangedHandler(changedHandler);

		TextItem address = new TextItem("address");
		address.setTitle(I18N.getMessage("address"));
		address.setValue(user.getAddress());
		address.setDisabled(readonly);
		if (!readonly)
			address.addChangedHandler(changedHandler);

		TextItem postalcode = new TextItem("postalcode");
		postalcode.setTitle(I18N.getMessage("postalcode"));
		postalcode.setValue(user.getPostalCode());
		postalcode.setDisabled(readonly);
		if (!readonly)
			postalcode.addChangedHandler(changedHandler);

		TextItem city = new TextItem("city");
		city.setTitle(I18N.getMessage("city"));
		city.setValue(user.getCity());
		city.setDisabled(readonly);
		if (!readonly)
			city.addChangedHandler(changedHandler);

		TextItem country = new TextItem("country");
		country.setTitle(I18N.getMessage("country"));
		country.setValue(user.getCountry());
		country.setDisabled(readonly);
		if (!readonly)
			country.addChangedHandler(changedHandler);

		SelectItem language = ItemFactory.newLanguageSelector("language", false);
		language.addChangedHandler(changedHandler);
		language.setDisabled(readonly);
		language.setValue(user.getLanguage());
		if (!readonly)
			language.addChangedHandler(changedHandler);

		TextItem state = new TextItem("state");
		state.setTitle(I18N.getMessage("state"));
		state.setValue(user.getState());
		state.setDisabled(readonly);
		if (!readonly)
			state.addChangedHandler(changedHandler);

		TextItem phone = new TextItem("phone");
		phone.setTitle(I18N.getMessage("phone"));
		phone.setValue(user.getPhone());
		phone.setDisabled(readonly);
		if (!readonly)
			phone.addChangedHandler(changedHandler);

		TextItem cell = new TextItem("cell");
		cell.setTitle(I18N.getMessage("cell"));
		cell.setValue(user.getCell());
		cell.setDisabled(readonly);
		if (!readonly)
			cell.addChangedHandler(changedHandler);

		TextItem email = ItemFactory.newEmailItem("email", I18N.getMessage("email"), false);
		email.setRequired(true);
		email.setDisabled(readonly);
		email.setValue(user.getEmail());
		if (!readonly)
			email.addChangedHandler(changedHandler);

		form1.setItems(id, enabled, expires, username, firstname, name, email, language, address, postalcode, city,
				country, state, phone, cell);
		addMember(form1);

		/*
		 * Prepare the second form for the groups
		 */
		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();

		List<FormItem> items = new ArrayList<FormItem>();
		final ComboBoxItem group = new ComboBoxItem("group");
		group.setTitle(I18N.getMessage("group"));
		group.setPickListWidth(250);
		group.setOptionDataSource(GroupsDS.get());
		group.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord record = group.getSelectedRecord();
				GUIGroup group = new GUIGroup();
				group.setId(Long.parseLong(record.getAttributeAsString("id")));
				group.setName(record.getAttributeAsString("name"));
				group.setDescription(record.getAttributeAsString("description"));
				user.addGroup(group);
				refresh();
				changedHandler.onChanged(null);
			}
		});
		items.add(group);

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(Util.imageUrl("application/delete.png"));
		int i = 0;
		for (GUIGroup grp : user.getGroups()) {
			final StaticTextItem gp = new StaticTextItem();
			gp.setValue(grp.getName());
			gp.setName("group" + i++);
			if (!("admin".equals(user.getUserName()) && "admin".equals(grp.getName())))
				gp.setIcons(icon);
			gp.setTitle(I18N.getMessage("group"));
			gp.setWrap(false);
			gp.addIconClickHandler(new IconClickHandler() {
				public void onIconClick(IconClickEvent event) {
					user.removeGroup((String) gp.getValue());
					changedHandler.onChanged(null);
					refresh();
				}
			});
			items.add(gp);
		}

		form2.setItems(items.toArray(new FormItem[0]));
		addMember(form2);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			user.setUserName((String) values.get("username"));
			user.setExpired((Boolean) values.get("expires"));
			user.setEnabled((Boolean) values.get("enabled"));
			user.setName((String) values.get("name"));
			user.setFirstName((String) values.get("firstname"));
			user.setAddress((String) values.get("address"));
			user.setCity((String) values.get("city"));
			user.setCountry((String) values.get("country"));
			user.setState((String) values.get("state"));
			user.setPostalCode((String) values.get("postalcode"));
			user.setLanguage((String) values.get("language"));
			user.setPhone((String) values.get("phone"));
			user.setCell((String) values.get("cell"));
			user.setEmail((String) values.get("email"));

		}
		return !vm.hasErrors();
	}
}