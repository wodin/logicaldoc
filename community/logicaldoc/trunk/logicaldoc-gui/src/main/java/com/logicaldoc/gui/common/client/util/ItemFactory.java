package com.logicaldoc.gui.common.client.util;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
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
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
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
			date.setTitle(I18N.message(title));
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
		opts.put("nolimits", I18N.message("nolimits"));
		opts.put("before", I18N.message("before"));
		opts.put("after", I18N.message("after"));
		dateOperator.setValueMap(opts);
		dateOperator.setName(name);
		if (title != null)
			dateOperator.setTitle(I18N.message(title));
		else
			dateOperator.setShowTitle(false);
		dateOperator.setDefaultValue("nolimits");
		dateOperator.setWidth(80);
		return dateOperator;
	}

	public static SelectItem newSizeOperator(String name, String title) {
		SelectItem sizeOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.message("nolimits"));
		opts.put("lessthan", I18N.message("lessthan"));
		opts.put("greaterthan", I18N.message("greaterthan"));
		sizeOperator.setValueMap(opts);
		sizeOperator.setName(name);
		if (title != null)
			sizeOperator.setTitle(I18N.message(title));
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
		item.setTitle(I18N.message("language"));
		item.setWrapTitle(false);
		item.setDefaultValue("en");
		return item;
	}
	
	public static SelectItem newGUILanguageSelector(String name, boolean withEmpty) {
		SelectItem item = new SelectItem();
		item.setValueMap(I18N.getSupportedLanguages(withEmpty));
		item.setName(name);
		item.setTitle(I18N.message("language"));
		item.setWrapTitle(false);
		item.setDefaultValue(I18N.getLocale());
		return item;
	}

	public static SelectItem newEncodingSelector(String name) {
		SelectItem item = new SelectItem();
		item.setName(name);
		item.setTitle(I18N.message("encoding"));
		item.setWrapTitle(false);
		item.setDefaultValue("UTF8");

		// String platformEncoding = System.getProperty("file.encoding");
		// Charset ce = Charset.forName(platformEncoding);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		// Eight-bit Unicode (or UCS) Transformation Format
		map.put("UTF8", "UTF-8 Unicode");
		// Default plaform encoding for file names
		// TODO I need the Charset class
		// map.put(platformEncoding, ce.name() + " Default");
		// PC Greek
		map.put("Cp737", "PC Greek");
		// ISO-8859-1, Latin Alphabet No. 1
		map.put("ISO8859_1", "Latin Alphabet No. 1");
		// Latin/Cyrillic Alphabet
		map.put("ISO8859_5", "Latin/Cyrillic");
		// Latin/Arabic Alphabet
		map.put("ISO8859_6", "Latin/Arabic");
		// Latin/Greek Alphabet (ISO-8859-7:2003)
		map.put("ISO8859_7", "Latin/Greek");
		// Simplified Chinese, PRC standard
		map.put("GB18030", "GB18030 Simplified Chinese");
		// GB2312, EUC encoding, Simplified Chinese
		map.put("EUC_CN", "GB2312 Simplified Chinese");
		// JISX 0201, 0208 and 0212, EUC encoding Japanese
		map.put("EUC_JP", "EUC-JP Japanese");
		// Shift-JIS, Japanese
		map.put("SJIS", "Shift_JIS Japanese");
		// KS C 5601, EUC encoding, Korean
		map.put("EUC_KR", "EUC-KR Korean");
		// Windows Eastern European
		map.put("Cp1250", "windows-1250 Eastern European");
		// Windows Latin-1
		map.put("Cp1252", "windows-1252 Latin-1");
		// Windows Greek
		map.put("Cp1253", "windows-1253 Greek");
		// Windows Arabic
		map.put("Cp1256", "windows-1256 Arabic");

		item.setValueMap(map);
		return item;
	}

	public static TextItem newEmailItem(String name, String title, boolean multiple) {
		TextItem item = new TextItem();
		item.setName(name);
		if (title != null)
			item.setTitle(I18N.message(title));
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
		group.setTitle(I18N.message(title));
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
		user.setTitle(I18N.message(title));
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
		map.put("yes", I18N.message("yes"));
		map.put("no", I18N.message("no"));
		radioGroupItem.setValueMap(map);
		radioGroupItem.setRedrawOnChange(true);
		radioGroupItem.setTitle(I18N.message(title));
		radioGroupItem.setWidth(80);
		return radioGroupItem;
	}

	public static SelectItem newMultipleSelector(String name, String title) {
		SelectItem selectItemMultipleGrid = new SelectItem();
		selectItemMultipleGrid.setName(name);
		selectItemMultipleGrid.setTitle(I18N.message(title));
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
		map.put("event.archived", I18N.message("event.archived"));
		map.put("event.changed", I18N.message("event.changed"));
		map.put("event.checkedin", I18N.message("event.checkedin"));
		map.put("event.checkedout", I18N.message("event.checkedout"));
		map.put("event.deleted", I18N.message("event.deleted"));
		map.put("event.downloaded", I18N.message("event.downloaded"));
		map.put("event.folder.created", I18N.message("event.folder.created"));
		map.put("event.folder.deleted", I18N.message("event.folder.deleted"));
		map.put("event.folder.permission", I18N.message("event.folder.permission"));
		map.put("event.folder.renamed", I18N.message("event.folder.renamed"));
		map.put("event.folder.subfolder.created", I18N.message("event.folder.subfolder.created"));
		map.put("event.folder.subfolder.deleted", I18N.message("event.folder.subfolder.deleted"));
		map.put("event.folder.subfolder.permission", I18N.message("event.folder.subfolder.permission"));
		map.put("event.folder.subfolder.renamed", I18N.message("event.folder.subfolder.renamed"));
		map.put("event.makeimmutable", I18N.message("event.makeimmutable"));
		map.put("event.locked", I18N.message("event.locked"));
		map.put("event.moved", I18N.message("event.moved"));
		map.put("event.renamed", I18N.message("event.renamed"));
		map.put("event.stored", I18N.message("event.stored"));
		map.put("event.unlocked", I18N.message("event.unlocked"));
		// User events
		map.put("event.user.deleted", I18N.message("event.user.deleted"));
		map.put("event.user.login", I18N.message("event.user.login"));
		map.put("event.user.logout", I18N.message("event.user.logout"));
		map.put("event.user.passwordchanged", I18N.message("event.user.passwordchanged"));
		// Workflow events
		map.put("event.workflow.start", I18N.message("event.workflow.start"));
		map.put("event.workflow.end", I18N.message("event.workflow.end"));
		map.put("event.workflow.task.start", I18N.message("event.workflow.task.start"));
		map.put("event.workflow.task.end", I18N.message("event.workflow.task.end"));
		map.put("event.workflow.task.suspended", I18N.message("event.workflow.task.suspended"));
		map.put("event.workflow.task.resumed", I18N.message("event.workflow.task.resumed"));
		map.put("event.workflow.task.reassigned", I18N.message("event.workflow.task.reassigned"));
		map.put("event.workflow.docappended", I18N.message("event.workflow.docappended"));

		select.setValueMap(map);

		return select;
	}

	public static Img newImgIcon(String name) {
		Img img = newImg(name);
		img.setWidth("16px");
		return img;
	}

	public static Img newImg(String name) {
		Img img = new Img(Util.imageUrl(name));
		return img;
	}
	
	public static Img newBrandImg(String name) {
		Img img = new Img(Util.brandUrl(name));
		return img;
	}

	public static FormItemIcon newItemIcon(String image) {
		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(ItemFactory.newImgIcon(image).getSrc());
		return icon;
	}

	public static HeaderIcon newHeaderIcon(String image) {
		HeaderIcon icon = new HeaderIcon(ItemFactory.newImgIcon(image).getSrc());
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
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setRequiredMessage(I18N.message("fieldrequired"));
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

	/**
	 * Creates a new StaticTextItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static StaticTextItem newStaticTextItem(String name, String title, String value) {
		StaticTextItem item = new StaticTextItem();
		if (name.trim().isEmpty())
			item.setShouldSaveValue(false);
		item.setName(name);
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		return item;
	}

	/**
	 * Creates a new IntegerItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newIntegerItem(String name, String title, Integer value) {
		IntegerItem item = new IntegerItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		return item;
	}

	/**
	 * Creates a new IntegerItem with a range validator.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 * @param min The item minimum value (optional)
	 * @param min The item maximum value (optional)
	 */
	public static IntegerItem newValidateIntegerItem(String name, String title, Integer value, Integer min, Integer max) {
		IntegerItem item = newIntegerItem(name, title, value);
		if (min != null || max != null) {
			IntegerRangeValidator iv = new IntegerRangeValidator();
			if (min != null)
				iv.setMin(min);
			if (max != null)
				iv.setMax(max);
			item.setValidators(iv);
		}

		return item;

	}
}