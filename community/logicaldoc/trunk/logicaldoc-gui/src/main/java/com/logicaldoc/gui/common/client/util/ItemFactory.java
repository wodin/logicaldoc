package com.logicaldoc.gui.common.client.util;

import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.I18N;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;

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
		SelectItem languageItem = new SelectItem();
		languageItem.setValueMap(I18N.getSupportedLanguages(withEmpty));
		languageItem.setName(name);
		languageItem.setTitle(I18N.getMessage("language"));
		languageItem.setWrapTitle(false);
		languageItem.setDefaultValue("en");
		return languageItem;
	}
}