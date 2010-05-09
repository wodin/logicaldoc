package com.logicaldoc.gui.common.client.util;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.validators.EmailValidator;
import com.logicaldoc.gui.common.client.validators.EmailsValidator;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * Collection of useful factory methods for form items.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 * 
 */
public class ItemFactory {
	public static DateItem newDateItem(String name, String title) {
		DateItem date = new DateItem(name);
		if (title != null)
			date.setTitle(title);
		else
			date.setShowTitle(false);
		date.setUseTextField(true);
		date.setUseMask(true);
		date.setShowPickerIcon(true);
		date.setWidth(90);
		date.setName(name);
		date.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
		return date;
	}

	public static SelectItem newDateOperator(String name, String title) {
		SelectItem dateOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.getMessage("nolimits"));
		opts.put("before", I18N.getMessage("before"));
		opts.put("after", I18N.getMessage("after"));
		dateOperator.setValueMap(opts);
		dateOperator.setName(name);
		if (title != null)
			dateOperator.setTitle(title);
		else
			dateOperator.setShowTitle(false);
		dateOperator.setDefaultValue("nolimits");
		dateOperator.setWidth(80);
		return dateOperator;
	}

	public static SelectItem newSizeOperator(String name, String title) {
		SelectItem sizeOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.getMessage("nolimits"));
		opts.put("lessthan", I18N.getMessage("lessthan"));
		opts.put("greaterthan", I18N.getMessage("greaterthan"));
		sizeOperator.setValueMap(opts);
		sizeOperator.setName(name);
		if (title != null)
			sizeOperator.setTitle(title);
		else
			sizeOperator.setShowTitle(false);
		sizeOperator.setDefaultValue("nolimits");
		sizeOperator.setWidth(80);
		return sizeOperator;
	}

	public static SelectItem newLanguageSelector(String name, boolean withEmpty) {
		SelectItem item = new SelectItem();
		item.setValueMap(I18N.getSupportedLanguages(withEmpty));
		item.setName(name);
		item.setTitle(I18N.getMessage("language"));
		item.setWrapTitle(false);
		item.setDefaultValue("en");
		return item;
	}

	public static TextItem newEmailItem(String name, String title, boolean multiple) {
		TextItem item = new TextItem();
		item.setName(name);
		if (title != null)
			item.setTitle(title);
		else
			item.setShowTitle(false);
		if (multiple)
			item.setValidators(new EmailsValidator());
		else
			item.setValidators(new EmailValidator());
		return item;
	}

	public static ComboBoxItem newGroupSelector(String name, String title) {
		ComboBoxItem group = new ComboBoxItem(name);
		group.setTitle(I18N.getMessage(title));
		group.setValueField("id");
		group.setDisplayField("name");
		group.setPickListWidth(300);
		ListGridField n = new ListGridField("name");
		ListGridField description = new ListGridField("description");
		group.setPickListFields(n, description);
		group.setOptionDataSource(GroupsDS.get());
		return group;
	}

	public static ComboBoxItem newUserSelector(String name, String title) {
		ComboBoxItem user = new ComboBoxItem(name);
		user.setTitle(title);
		ListGridField username = new ListGridField("username");
		ListGridField label = new ListGridField("label");
		user.setValueField("id");
		user.setDisplayField("username");
		user.setPickListWidth(300);
		user.setPickListFields(username, label);
		user.setOptionDataSource(UsersDS.get());
		return user;
	}
}