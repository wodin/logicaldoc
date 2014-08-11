package com.logicaldoc.gui.common.client.util;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.data.ArchivesDS;
import com.logicaldoc.gui.common.client.data.ContactsDS;
import com.logicaldoc.gui.common.client.data.EventsDS;
import com.logicaldoc.gui.common.client.data.ExtendedAttributeOptionsDS;
import com.logicaldoc.gui.common.client.data.FolderTemplatesDS;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.data.TemplatesDS;
import com.logicaldoc.gui.common.client.data.TenantsDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.validators.EmailValidator;
import com.logicaldoc.gui.common.client.validators.EmailsValidator;
import com.logicaldoc.gui.common.client.validators.SimpleTextValidator;
import com.logicaldoc.gui.common.client.widgets.UserSelector;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.RowSpacerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.TimeItem;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.IsFloatValidator;
import com.smartgwt.client.widgets.form.validator.IsIntegerValidator;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * Collection of useful factory methods for form items.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ItemFactory {

	/**
	 * Creates a new DateItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (optional)
	 */
	public static DateItem newDateItem(String name, String title) {
		DateItem date = new DateItem(filterItemName(name));
		if (title != null)
			date.setTitle(I18N.message(title));
		else
			date.setShowTitle(false);
		date.setUseTextField(true);
		date.setUseMask(true);
		date.setShowPickerIcon(true);
		date.setHintStyle("hint");
		date.setWidth(110);

		if (I18N.message("format_dateshort").startsWith("MM/dd"))
			date.setDateFormatter(DateDisplayFormat.TOUSSHORTDATE);
		else
			date.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);

		return date;
	}

	/**
	 * Creates a new DateItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 */
	public static DateItem newDateItemForExtendedAttribute(String name, String label) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		final DateItem date = new DateItem(itemName);
		date.setTitle(label);
		date.setUseTextField(true);
		date.setUseMask(false);
		date.setShowPickerIcon(true);
		date.setWidth(110);
		date.setName(itemName);
		date.setHintStyle("hint");

		if (I18N.message("format_dateshort").startsWith("MM/dd"))
			date.setDateFormatter(DateDisplayFormat.TOUSSHORTDATE);
		else
			date.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);

		return date;
	}

	public static SelectItem newUserSelectorForExtendedAttribute(String name, String title, String groupIdOrName) {
		final SelectItem item = new UserSelector("_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER), title,
				groupIdOrName);
		return item;
	}

	public static SelectItem newRecipientTypeSelector(String name) {
		SelectItem selector = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("to", I18N.message("to"));
		opts.put("cc", I18N.message("cc"));
		selector.setValueMap(opts);
		selector.setName(filterItemName(name));
		selector.setShowTitle(false);
		selector.setValue("to");
		selector.setRequired(true);
		return selector;
	}

	public static SelectItem newDateOperator(String name, String title) {
		SelectItem dateOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.message("nolimits"));
		opts.put("before", I18N.message("before"));
		opts.put("after", I18N.message("after"));
		dateOperator.setValueMap(opts);
		dateOperator.setName(filterItemName(name));
		if (title != null)
			dateOperator.setTitle(I18N.message(title));
		else
			dateOperator.setShowTitle(false);
		dateOperator.setDefaultValue("nolimits");
		dateOperator.setWidth(80);
		dateOperator.setHintStyle("hint");
		return dateOperator;
	}

	public static SelectItem newSizeOperator(String name, String title) {
		SelectItem sizeOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.message("nolimits"));
		opts.put("lessthan", I18N.message("lessthan"));
		opts.put("greaterthan", I18N.message("greaterthan"));
		sizeOperator.setValueMap(opts);
		sizeOperator.setName(filterItemName(name));
		if (title != null)
			sizeOperator.setTitle(I18N.message(title));
		else
			sizeOperator.setShowTitle(false);
		sizeOperator.setDefaultValue("nolimits");
		sizeOperator.setWidth(85);
		sizeOperator.setHintStyle("hint");
		return sizeOperator;
	}

	public static SelectItem newLanguageSelector(String name, boolean withEmpty, boolean gui) {
		SelectItem item = new SelectItem();
		if (gui)
			item.setValueMap(I18N.getSupportedGuiLanguages(withEmpty));
		else
			item.setValueMap(I18N.getSupportedLanguages(withEmpty));
		item.setName(filterItemName(name));
		item.setTitle(I18N.message("language"));
		item.setWrapTitle(false);
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newEncodingSelector(String name) {
		SelectItem item = new SelectItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message("encoding"));
		item.setWrapTitle(false);
		item.setDefaultValue("UTF8");

		// String platformEncoding = System.getProperty("file.encoding");
		// Charset ce = Charset.forName(platformEncoding);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		// Eight-bit Unicode (or UCS) Transformation Format
		map.put("UTF8", "UTF-8 Unicode");
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
		item.setHintStyle("hint");
		return item;
	}

	public static TextItem newEmailItem(String name, String title, boolean multiple) {
		TextItem item = new TextItem();
		item.setName(filterItemName(name));
		if (title != null)
			item.setTitle(I18N.message(title));
		else
			item.setShowTitle(false);
		if (multiple)
			item.setValidators(new EmailsValidator());
		else
			item.setValidators(new EmailValidator());
		item.setHintStyle("hint");
		item.setWidth(180);
		return item;
	}

	public static ComboBoxItem newEmailSelector(String name, String title) {
		ComboBoxItem selector = new ComboBoxItem(filterItemName(name));
		selector.setTitle(I18N.message(title));
		selector.setWrapTitle(false);
		selector.setValueField("email");
		selector.setDisplayField("email");
		selector.setPickListWidth(350);
		selector.setFetchDelay(2000);
		selector.setHideEmptyPickList(true);
		ListGridField email = new ListGridField("email", I18N.message("email"));
		email.setWidth("*");
		ListGridField firstName = new ListGridField("firstName", I18N.message("firstname"));
		firstName.setWidth(90);
		ListGridField lastName = new ListGridField("lastName", I18N.message("lastname"));
		lastName.setWidth(90);
		selector.setPickListFields(email, firstName, lastName);
		selector.setOptionDataSource(new ContactsDS());
		selector.setHintStyle("hint");
		return selector;
	}

	public static SelectItem newGroupSelector(String name, String title) {
		SelectItem group = new SelectItem(filterItemName(name));
		group.setTitle(I18N.message(title));
		group.setWrapTitle(false);
		group.setValueField("id");
		group.setDisplayField("name");
		group.setPickListWidth(300);
		ListGridField n = new ListGridField("name", I18N.message("name"));
		ListGridField description = new ListGridField("description", I18N.message("description"));
		group.setPickListFields(n, description);
		group.setOptionDataSource(GroupsDS.get());
		group.setHintStyle("hint");
		return group;
	}

	public static SelectItem newUserSelector(String name, String title, String groupIdOrName) {
		SelectItem user = new SelectItem(filterItemName(name));
		user.setTitle(I18N.message(title));
		user.setWrapTitle(false);
		ListGridField id = new ListGridField("id", I18N.message("id"));
		id.setHidden(true);
		ListGridField username = new ListGridField("username", I18N.message("username"));
		ListGridField label = new ListGridField("label", I18N.message("name"));
		user.setValueField("id");
		user.setDisplayField("username");
		user.setPickListWidth(300);
		user.setPickListFields(id, username, label);
		user.setOptionDataSource(new UsersDS(groupIdOrName));
		user.setHintStyle("hint");
		return user;
	}

	public static SelectItem newTenantSelector() {
		SelectItem tenant = new SelectItem("tenant");
		tenant.setTitle(I18N.message("tenant"));
		tenant.setWrapTitle(false);
		ListGridField id = new ListGridField("id", I18N.message("id"));
		id.setHidden(true);
		ListGridField _name = new ListGridField("name", I18N.message("name"));
		ListGridField displayName = new ListGridField("displayName", I18N.message("displayname"));
		tenant.setValueField("id");
		tenant.setDisplayField("displayName");
		tenant.setPickListWidth(300);
		tenant.setPickListFields(id, _name, displayName);
		tenant.setHintStyle("hint");
		tenant.setOptionDataSource(new TenantsDS());
		return tenant;
	}

	public static RadioGroupItem newRadioGroup(String name, String title) {
		RadioGroupItem radioGroupItem = new RadioGroupItem();
		radioGroupItem.setName(filterItemName(name));
		radioGroupItem.setVertical(false);
		radioGroupItem.setTitle(I18N.message(title));
		radioGroupItem.setWidth(80);
		radioGroupItem.setHintStyle("hint");
		return radioGroupItem;
	}

	public static RadioGroupItem newBooleanSelector(String name, String title) {
		RadioGroupItem radioGroupItem = newRadioGroup(name, title);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("yes", I18N.message("yes"));
		map.put("no", I18N.message("no"));
		radioGroupItem.setValueMap(map);
		return radioGroupItem;
	}

	public static CheckboxItem newCheckbox(String name, String title) {
		CheckboxItem item = new CheckboxItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newMultipleSelector(String name, String title) {
		SelectItem selectItemMultipleGrid = new SelectItem();
		selectItemMultipleGrid.setName(filterItemName(name));
		selectItemMultipleGrid.setTitle(I18N.message(title));
		selectItemMultipleGrid.setMultiple(true);
		selectItemMultipleGrid.setValueMap("");
		selectItemMultipleGrid.setHintStyle("hint");
		return selectItemMultipleGrid;
	}

	public static SelectItem newPrioritySelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), title);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("low"));
		map.put("1", I18N.message("medium"));
		map.put("2", I18N.message("high"));
		select.setValueMap(map);
		select.setValue("0");
		select.setHintStyle("hint");
		return select;
	}

	public static SelectItem newWelcomeScreenSelector(String name, Integer value) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message("welcomescreen"));
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("1500", I18N.message("documents"));
		map.put("1510", I18N.message("search"));
		map.put("1520", I18N.message("dashboard"));
		select.setValueMap(map);
		if (value != null)
			select.setValue(value.toString());
		else
			select.setValue("1500");
		return select;
	}

	public static SelectItem newDashletSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), title);
		select.setAllowEmptyValue(false);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("" + Constants.DASHLET_CHECKOUT, I18N.message(Constants.EVENT_CHECKEDOUT + "docs"));
		map.put("" + Constants.DASHLET_CHECKIN, I18N.message(Constants.EVENT_CHECKEDIN + "docs"));
		map.put("" + Constants.DASHLET_LOCKED, I18N.message(Constants.EVENT_LOCKED + "docs"));
		map.put("" + Constants.DASHLET_CHANGED, I18N.message(Constants.EVENT_CHANGED + "docs"));
		map.put("" + Constants.DASHLET_DOWNLOADED, I18N.message(Constants.EVENT_DOWNLOADED + "docs"));
		map.put("" + Constants.DASHLET_LAST_NOTES, I18N.message("lastnotes"));
		map.put("" + Constants.DASHLET_TAGCLOUD, I18N.message("tagcloud"));

		select.setValueMap(map);
		select.setValue(map.keySet().iterator().next());
		return select;
	}

	public static SelectItem newEventsSelector(String name, String title, boolean folder, boolean workflow, boolean user) {
		SelectItem select = newMultipleSelector(filterItemName(name), title);
		select.setWidth(300);
		select.setHeight(200);
		select.setMultipleAppearance(MultipleAppearance.GRID);
		select.setMultiple(true);
		select.setOptionDataSource(new EventsDS(Session.get().getUser().getLanguage(), folder, workflow, user));
		select.setValueField("code");
		select.setDisplayField("label");
		select.setHintStyle("hint");
		return select;
	}

	public static SelectItem newSelectItem(String name, String title) {
		SelectItem select = newMultipleSelector(filterItemName(name),
				title != null ? I18N.message(title) : I18N.message(name));
		select.setMultiple(false);
		select.setWrapTitle(false);
		select.setHintStyle("hint");
		return select;
	}

	public static SpinnerItem newSpinnerItem(String name, String title, Integer value) {
		SpinnerItem spinner = new SpinnerItem(name);
		spinner.setTitle(I18N.message(title));
		spinner.setMin(0);
		spinner.setStep(1);
		spinner.setWidth(50);
		if (value != null)
			spinner.setValue(value.intValue());
		else
			spinner.setValue((Integer) null);
		return spinner;
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

	public static Img newBrandImg(String name, GUIInfo info) {
		Img img = null;

		if (name.equals("logo.png"))
			img = new Img(info.getLogoSrc());
		else if (name.equals("logo_head.png"))
			img = new Img(info.getLogoHeadSrc());
		else if (name.equals("logo_oem.png"))
			img = new Img(info.getLogoOemSrc());
		else if (name.equals("logo_head_oem.png"))
			img = new Img(info.getLogoHeadOemSrc());
		else if (name.equals("banner.gif"))
			img = new Img(info.getBannerSrc());
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
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		return item;
	}

	public static RowSpacerItem newRowSpacer() {
		RowSpacerItem item = new RowSpacerItem();
		item.setCellStyle("row");
		item.setHeight(5);
		return item;
	}

	/**
	 * Creates a new TextItem for the Extended Attributes.
	 */
	public static FormItem newStringItemForExtendedAttribute(Long templateId, GUIExtendedAttribute att) {
		// We cannot use spaces in items name
		String itemName = "_" + att.getName().replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		FormItem item = new TextItem();

		if (att.getEditor() == GUIExtendedAttribute.EDITOR_LISTBOX) {
			item = new SelectItem();
			item.setOptionDataSource(new ExtendedAttributeOptionsDS(templateId, att.getName(), !att.isMandatory()));

			ListGridField value = new ListGridField("value", I18N.message("value"));
			((SelectItem) item).setPickListWidth(200);
			((SelectItem) item).setPickListFields(value);
			((SelectItem) item).setValueField("value");
			((SelectItem) item).setDisplayField("value");
		}

		item.setName(itemName);
		item.setTitle(att.getLabel());
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		item.setRequired(att.isMandatory());

		return item;
	}

	public static PasswordItem newPasswordItem(String name, String title, String value) {
		PasswordItem password = new PasswordItem();
		password.setTitle(I18N.message(title));
		password.setName(filterItemName(name));
		if (value != null)
			password.setValue(value);
		password.setHintStyle("hint");
		return password;
	}

	/**
	 * Creates a new TextItem that validates a simple text.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextItem newSimpleTextItem(String name, String title, String value) {
		TextItem item = newTextItem(filterItemName(name), I18N.message(title), value);
		item.setValidators(new SimpleTextValidator());
		item.setHintStyle("hint");
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
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setWrapTitle(false);
		item.setHintStyle("hint");
		return item;
	}

	/**
	 * Creates a new IntegerItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newLongItem(String name, String title, Long value) {
		IntegerItem item = new IntegerItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
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
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new IntegerItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newIntegerItemForExtendedAttribute(String name, String label, Integer value) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		IntegerItem item = newIntegerItem(itemName, label, value);
		return item;
	}

	/**
	 * Simple boolean selector for Extended Attribute
	 */
	public static SelectItem newBooleanSelectorForExtendedAttribute(String name, String title, boolean allowEmpty) {
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		SelectItem select = new SelectItem();
		select.setName(itemName);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (allowEmpty)
			map.put("", " ");

		map.put("1", I18N.message("yes"));
		map.put("0", I18N.message("no"));
		select.setValueMap(map);
		select.setTitle(I18N.message(title));
		select.setWidth(80);
		select.setHintStyle("hint");
		select.setValue("");

		return select;
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
		IntegerItem item = newIntegerItem(filterItemName(name), I18N.message(title), value);
		IntegerRangeValidator rv = null;
		if (min != null || max != null) {
			rv = new IntegerRangeValidator();
			if (min != null)
				rv.setMin(min);
			if (max != null)
				rv.setMax(max);
		}
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		if (rv == null)
			item.setValidators(iv);
		else
			item.setValidators(iv, rv);

		item.setHintStyle("hint");
		return item;
	}

	public static LinkItem newLinkItem(String name, String title) {
		LinkItem linkItem = new LinkItem(filterItemName(name));
		if (!title.trim().isEmpty()) {
			linkItem.setTitle(I18N.message(title));
			linkItem.setLinkTitle(I18N.message(title));
		}
		linkItem.setWrapTitle(false);
		linkItem.setHintStyle("hint");
		return linkItem;
	}

	/**
	 * Creates a new TextAreaItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextAreaItem newTextAreaItem(String name, String title, String value) {
		TextAreaItem item = new TextAreaItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		item.setHeight(50);
		item.setWidth("100%");
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newDueTimeSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("minute", I18N.message("minutes"));
		map.put("hour", I18N.message("hours"));
		map.put("day", I18N.message("ddays"));
		map.put("week", I18N.message("weeks"));
		select.setValueMap(map);
		select.setValue("minute");
		select.setHintStyle("hint");
		return select;
	}

	public static TimeItem newTimeItem(String name, String title) {
		TimeItem item = new TimeItem(name, I18N.message(title));
		item.setHintStyle("hint");
		item.setWidth(40);
		return item;
	}

	public static SelectItem newTemplateSelector(boolean allowEmpty, Long templateId) {
		SelectItem templateItem = new SelectItem("template", I18N.message("template"));
		templateItem.setDisplayField("name");
		templateItem.setValueField("id");
		templateItem.setPickListWidth(250);
		templateItem.setMultiple(false);
		templateItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		if (!allowEmpty)
			templateItem.setOptionDataSource(new TemplatesDS(true, templateId, null, false));
		else
			templateItem.setOptionDataSource(new TemplatesDS(false, templateId, null, false));
		if (!Feature.enabled(Feature.TEMPLATE))
			templateItem.setDisabled(true);
		templateItem.setHintStyle("hint");
		return templateItem;
	}

	public static SelectItem newFrequencySelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "");
		map.put("1", I18N.message("daily"));
		map.put("7", I18N.message("weekly"));
		map.put("15", I18N.message("biweekly"));
		map.put("30", I18N.message("monthly"));
		map.put("180", I18N.message("sixmonthly"));
		map.put("365", I18N.message("yearly"));

		select.setValueMap(map);
		select.setHintStyle("hint");
		select.setWidth(100);
		return select;
	}

	public static SelectItem newEventStatusSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "");
		map.put("1", I18N.message("working"));
		map.put("2", I18N.message("completed"));
		map.put("3", I18N.message("canceled"));

		select.setValueMap(map);
		select.setHintStyle("hint");
		select.setWidth(90);
		return select;
	}

	public static SelectItem newEmailProtocolSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		select.setValueMap("pop3", "imap");
		return select;
	}

	public static SelectItem newEmailFolderingSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("none"));
		map.put("1", I18N.message("year"));
		map.put("2", I18N.message("month"));
		map.put("3", I18N.message("day"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newEffectSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "");
		map.put("move", I18N.message("move"));
		map.put("copy", I18N.message("copy"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newEmailFields(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("subject"));
		map.put("1", I18N.message("sender"));
		map.put("2", I18N.message("content"));
		select.setValueMap(map);
		select.setHintStyle("hint");
		return select;
	}

	public static SelectItem newArchiveTypeSelector() {
		SelectItem item = new SelectItem();
		item.setName("archivetype");
		item.setTitle(I18N.message("type"));
		item.setWrapTitle(false);
		item.setDefaultValue(Integer.toString(GUIArchive.TYPE_DEFAULT));

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(Integer.toString(GUIArchive.TYPE_DEFAULT), I18N.message("default"));
		map.put(Integer.toString(GUIArchive.TYPE_STORAGE), I18N.message("paperdematerialization"));

		item.setValueMap(map);

		if (!Feature.enabled(Feature.ARCHIVES))
			item.setDisabled(true);
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newArchiveSelector(int mode, Integer status) {
		SelectItem item = new SelectItem("archive");
		item.setTitle("");
		item.setRequiredMessage(I18N.message("fieldrequired"));
		ListGridField name = new ListGridField("name", I18N.message("name"));
		ListGridField description = new ListGridField("description", I18N.message("description"));
		item.setValueField("id");
		item.setDisplayField("name");
		item.setPickListWidth(300);
		item.setPickListFields(name, description);
		item.setOptionDataSource(new ArchivesDS(mode, null, status, null));
		if (!Feature.enabled(Feature.ARCHIVES))
			item.setDisabled(true);
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newImportCustomIds() {
		SelectItem item = newSelectItem("importcids", null);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(Integer.toString(GUIArchive.CUSTOMID_NOT_IMPORT), I18N.message("ignore"));
		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_RELEASE), I18N.message("importasnewversion"));
		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_SUBVERSION), I18N.message("importasnewsubversion"));
		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_DOCUMENT), I18N.message("importasnewdoc"));
		item.setValueMap(map);
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newWorkflowSelector() {
		SelectItem item = new SelectItem("workflow", I18N.message("workflow"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		ListGridField name = new ListGridField("name", I18N.message("name"));
		ListGridField description = new ListGridField("description", I18N.message("description"));
		item.setPickListWidth(300);
		item.setPickListFields(name, description);
		item.setDisplayField("name");
		item.setValueField("id");
		item.setOptionDataSource(new WorkflowsDS(false, false, true));
		if (!Feature.enabled(Feature.WORKFLOW))
			item.setDisabled(true);
		item.setHintStyle("hint");
		return item;
	}

	public static Label newLinkLabel(String title) {
		Label label = new Label(I18N.message(title));
		label.setWrap(false);
		label.setCursor(Cursor.HAND);
		label.setAutoWidth();
		return label;
	}

	/**
	 * Creates a new FloatItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static FloatItem newFloatItem(String name, String title, Float value) {
		FloatItem item = new FloatItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		IsFloatValidator iv = new IsFloatValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new FloatItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static FloatItem newFloatItemForExtendedAttribute(String name, String label, Float value) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		FloatItem item = newFloatItem(itemName, label, value);
		return item;
	}

	/**
	 * Simple yes/no radio button. yes=true, no=false
	 */
	public static RadioGroupItem newYesNoItem(String name, String label) {
		RadioGroupItem item = new RadioGroupItem(filterItemName(name), I18N.message(label));
		item.setVertical(false);
		item.setShowTitle(true);
		item.setWrap(false);
		item.setWrapTitle(false);

		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		values.put("true", I18N.message("yes"));
		values.put("false", I18N.message("no"));
		item.setValueMap(values);
		item.setValue("true");

		return item;
	}

	public static SelectItem newTagInputMode(String name, String title) {
		SelectItem mode = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("free", I18N.message("free"));
		opts.put("preset", I18N.message("preset"));
		mode.setValueMap(opts);
		mode.setName(filterItemName(name));
		if (title != null)
			mode.setTitle(I18N.message(title));
		else
			mode.setShowTitle(false);
		mode.setDefaultValue("free");
		mode.setWidth(100);
		mode.setHintStyle("hint");
		return mode;
	}

	public static SelectItem newFolderTemplateSelector() {
		SelectItem item = new SelectItem("foldertemplate");
		item.setTitle(I18N.message("ttemplate"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setValueField("id");
		item.setDisplayField("name");
		item.setOptionDataSource(new FolderTemplatesDS());
		if (!Feature.enabled(Feature.FOLDER_TEMPLATE))
			item.setDisabled(true);
		item.setHintStyle("hint");
		return item;
	}

	/**
	 * Filter the name from problematic chars
	 */
	public static String filterItemName(String name) {
		return name.replaceAll("\\.", "_");
	}

	/**
	 * Obtain the original item name
	 */
	public static String originalItemName(String name) {
		return name.replaceAll("_", "\\.");
	}
}