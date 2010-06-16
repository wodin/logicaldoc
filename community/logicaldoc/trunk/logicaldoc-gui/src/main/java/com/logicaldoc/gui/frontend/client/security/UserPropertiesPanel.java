package com.logicaldoc.gui.frontend.client.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserPropertiesPanel extends HLayout {
	private HLayout addingGroup = new HLayout();

	private DynamicForm form1 = new DynamicForm();

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
		form1.setWrapItemTitles(false);
		form1.setTitleOrientation(TitleOrientation.TOP);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(user.getId()));
		id.setDisabled(true);
		id.setWrap(false);

		TextItem username = ItemFactory.newSimpleTextItem("username", "username", user.getUserName());
		username.setRequired(true);
		username.setDisabled(readonly || user.getId() != 0);
		if (!readonly)
			username.addChangedHandler(changedHandler);

		CheckboxItem expires = new CheckboxItem("expires", I18N.message("passwordexpires"));
		expires.setValue(user.isPasswordExpires());
		expires.setDisabled(readonly);
		if (!readonly)
			expires.addChangedHandler(changedHandler);

		CheckboxItem enabled = new CheckboxItem("eenabled", I18N.message("enabled"));
		enabled.setValue(user.isEnabled());
		if (readonly || "admin".equals(user.getUserName())) {
			enabled.setDisabled(true);
		} else {
			enabled.addChangedHandler(changedHandler);
		}

		TextItem firstname = ItemFactory.newTextItem("firstname", "firstname", user.getFirstName());
		firstname.setRequired(true);
		firstname.setDisabled(readonly);
		if (!readonly)
			firstname.addChangedHandler(changedHandler);

		TextItem name = ItemFactory.newTextItem("name", "lastname", user.getName());
		name.setRequired(true);
		name.setDisabled(readonly);
		if (!readonly)
			name.addChangedHandler(changedHandler);

		TextItem address = ItemFactory.newTextItem("address", "address", user.getAddress());
		address.setDisabled(readonly);
		if (!readonly)
			address.addChangedHandler(changedHandler);

		TextItem postalcode = ItemFactory.newTextItem("postalcode", "postalcode", user.getPostalCode());
		postalcode.setDisabled(readonly);
		if (!readonly)
			postalcode.addChangedHandler(changedHandler);

		TextItem city = ItemFactory.newTextItem("city", "city", user.getCity());
		city.setDisabled(readonly);
		if (!readonly)
			city.addChangedHandler(changedHandler);

		TextItem country = ItemFactory.newTextItem("country", "country", user.getCountry());
		country.setDisabled(readonly);
		if (!readonly)
			country.addChangedHandler(changedHandler);

		SelectItem language = ItemFactory.newLanguageSelector("language", false);
		language.addChangedHandler(changedHandler);
		language.setDisabled(readonly);
		language.setValue(user.getLanguage());
		if (!readonly)
			language.addChangedHandler(changedHandler);

		TextItem state = ItemFactory.newTextItem("state", "state", user.getState());
		state.setDisabled(readonly);
		if (!readonly)
			state.addChangedHandler(changedHandler);

		TextItem phone = ItemFactory.newTextItem("phone", "phone", user.getPhone());
		phone.setDisabled(readonly);
		if (!readonly)
			phone.addChangedHandler(changedHandler);

		TextItem cell = ItemFactory.newTextItem("cell", "cell", user.getCell());
		cell.setDisabled(readonly);
		if (!readonly)
			cell.addChangedHandler(changedHandler);

		TextItem email = ItemFactory.newEmailItem("email", "email", false);
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
		if (addingGroup != null)
			addingGroup.destroy();
		if (contains(addingGroup))
			removeChild(addingGroup);
		DynamicForm form2 = new DynamicForm();

		List<FormItem> items = new ArrayList<FormItem>();
		final ComboBoxItem group = new ComboBoxItem("group");
		group.setTitle(I18N.message("group"));
		group.setValueField("id");
		group.setDisplayField("name");
		group.setPickListWidth(300);
		ListGridField n = new ListGridField("name");
		ListGridField description = new ListGridField("description");
		group.setPickListFields(n, description);
		group.setOptionDataSource(GroupsDS.get(user.getId()));
		items.add(group);

		FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
		int i = 0;
		for (GUIGroup grp : user.getGroups()) {
			if (grp.getName().contains("_user"))
				continue;
			final StaticTextItem gp = ItemFactory.newStaticTextItem("group" + i++, "group", grp.getName());
			if (!("admin".equals(user.getUserName()) && "admin".equals(grp.getName())))
				gp.setIcons(icon);
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
		addingGroup.addMember(form2);

		Button addGroup = new Button(I18N.message("addgroup"));
		addGroup.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = group.getSelectedRecord();
				if (selectedRecord == null)
					return;

				if (!user.isMemberOf(selectedRecord.getAttributeAsString("name"))) {
					GUIGroup group = new GUIGroup();
					group.setId(Long.parseLong(selectedRecord.getAttributeAsString("id")));
					group.setName(selectedRecord.getAttributeAsString("name"));
					group.setDescription(selectedRecord.getAttributeAsString("description"));
					user.addGroup(group);
					refresh();
					changedHandler.onChanged(null);
				}
			}
		});
		addingGroup.addMember(addGroup);

		addMember(addingGroup);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			user.setUserName((String) values.get("username"));
			user.setExpired((Boolean) values.get("expires"));
			user.setEnabled((Boolean) values.get("eenabled"));
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