package com.logicaldoc.gui.frontend.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all supported languages. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LanguagesDS extends DataSource {
	private static LanguagesDS instance;

	public LanguagesDS() {
		setID("LanguagesDS");
		setTitleField("language");
		setRecordXPath("/list/language");

		DataSourceTextField localeField = new DataSourceTextField("locale");
		localeField.setPrimaryKey(true);
		DataSourceTextField nameField = new DataSourceTextField("name");

		setFields(localeField, nameField);
		setDataURL("data/languages.xml");
		setClientOnly(true);
	}

	public static LanguagesDS getInstance() {
		if (instance == null)
			instance = new LanguagesDS();
		return instance;
	}
}