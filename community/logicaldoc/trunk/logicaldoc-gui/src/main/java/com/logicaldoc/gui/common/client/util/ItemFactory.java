package com.logicaldoc.gui.common.client.util;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.validators.EmailValidator;
import com.logicaldoc.gui.common.client.validators.EmailsValidator;
import com.logicaldoc.gui.common.client.validators.SimpleTextValidator;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * Collection of useful factory methods for form items.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
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

	public static RadioGroupItem newBooleanSelector(String name, String title) {
		RadioGroupItem radioGroupItem = new RadioGroupItem();
		radioGroupItem.setName(name);
		radioGroupItem.setVertical(false);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("yes", I18N.getMessage("yes"));
		map.put("no", I18N.getMessage("no"));
		radioGroupItem.setValueMap(map);
		radioGroupItem.setRedrawOnChange(true);
		radioGroupItem.setTitle(title);
		radioGroupItem.setWidth(80);
		return radioGroupItem;
	}

	public static SelectItem newMultipleSelector(String name, String title) {
		SelectItem selectItemMultipleGrid = new SelectItem();
		selectItemMultipleGrid.setName(name);
		selectItemMultipleGrid.setTitle(title);
		selectItemMultipleGrid.setMultiple(true);
		selectItemMultipleGrid.setValueMap("");
		return selectItemMultipleGrid;
	}

	public static SelectItem newEventsSelector(String name, String title) {
		SelectItem select = newMultipleSelector(name, title);
		select.setWidth(300);
		select.setHeight(150);
		select.setMultipleAppearance(MultipleAppearance.GRID);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		// Document and folder events
		map.put("event.archived", I18N.getMessage("event.archived"));
		map.put("event.changed", I18N.getMessage("event.changed"));
		map.put("event.checkedin", I18N.getMessage("event.checkedin"));
		map.put("event.checkedout", I18N.getMessage("event.checkedout"));
		map.put("event.deleted", I18N.getMessage("event.deleted"));
		map.put("event.downloaded", I18N.getMessage("event.downloaded"));
		map.put("event.folder.created", I18N.getMessage("event.folder.created"));
		map.put("event.folder.deleted", I18N.getMessage("event.folder.deleted"));
		map.put("event.folder.permission", I18N.getMessage("event.folder.permission"));
		map.put("event.folder.renamed", I18N.getMessage("event.folder.renamed"));
		map.put("event.folder.subfolder.created", I18N.getMessage("event.folder.subfolder.created"));
		map.put("event.folder.subfolder.deleted", I18N.getMessage("event.folder.subfolder.deleted"));
		map.put("event.folder.subfolder.permission", I18N.getMessage("event.folder.subfolder.permission"));
		map.put("event.folder.subfolder.renamed", I18N.getMessage("event.folder.subfolder.renamed"));
		map.put("event.makeimmutable", I18N.getMessage("event.makeimmutable"));
		map.put("event.locked", I18N.getMessage("event.locked"));
		map.put("event.moved", I18N.getMessage("event.moved"));
		map.put("event.renamed", I18N.getMessage("event.renamed"));
		map.put("event.stored", I18N.getMessage("event.stored"));
		map.put("event.unlocked", I18N.getMessage("event.unlocked"));
		// User events
		map.put("event.user.deleted", I18N.getMessage("event.user.deleted"));
		map.put("event.user.login", I18N.getMessage("event.user.login"));
		map.put("event.user.logout", I18N.getMessage("event.user.logout"));
		map.put("event.user.passwordchanged", I18N.getMessage("event.user.passwordchanged"));
		// Workflow events
		map.put("event.workflow.start", I18N.getMessage("event.workflow.start"));
		map.put("event.workflow.end", I18N.getMessage("event.workflow.end"));
		map.put("event.workflow.task.start", I18N.getMessage("event.workflow.task.start"));
		map.put("event.workflow.task.end", I18N.getMessage("event.workflow.task.end"));
		map.put("event.workflow.task.suspended", I18N.getMessage("event.workflow.task.suspended"));
		map.put("event.workflow.task.resumed", I18N.getMessage("event.workflow.task.resumed"));
		map.put("event.workflow.task.reassigned", I18N.getMessage("event.workflow.task.reassigned"));
		map.put("event.workflow.docappended", I18N.getMessage("event.workflow.docappended"));

		select.setValueMap(map);

		return select;
	}

	public static Img newImg(String name) {
		Img img = new Img("../" + Util.imageUrl(name));
		img.setWidth("16px");
		return img;
	}

	public static FormItemIcon newItemIcon(String image) {
		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(ItemFactory.newImg(image).getSrc());
		return icon;
	}

	public static Image newImage(String image) {
		Image tmp = new Image(Util.imageUrl(image));
		return tmp;
	}

	public static HeaderIcon newHeaderIcon(String image) {
		HeaderIcon icon = new HeaderIcon(ItemFactory.newImg(image).getSrc());
		return icon;
	}

	/**
	 * Creates a new TextItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextItem newTextItem(String name, String title, String value) {
		TextItem item = new TextItem();
		item.setName(name);
		item.setTitle(title);
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setRequiredMessage(I18N.getMessage("fieldrequired"));
		return item;
	}

	/**
	 * Creates a new TextItem that validates a simple text.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextItem newSimpleTextItem(String name, String title, String value) {
		TextItem item = newTextItem(name, title, value);
		item.setValidators(new SimpleTextValidator());
		return item;
	}
}