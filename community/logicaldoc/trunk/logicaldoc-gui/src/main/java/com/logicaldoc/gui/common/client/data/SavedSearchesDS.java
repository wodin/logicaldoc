package com.logicaldoc.gui.common.client.data;

import com.google.gwt.i18n.client.LocaleInfo;
import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle versions grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SavedSearchesDS extends DataSource {

	private static SavedSearchesDS instance;

	public static SavedSearchesDS get() {
		if (instance == null)
			instance = new SavedSearchesDS();
		return instance;
	}

	public SavedSearchesDS() {
		setRecordXPath("/list/search");
		DataSourceTextField name = new DataSourceTextField("name");
		name.setPrimaryKey(true);

		DataSourceTextField type = new DataSourceTextField("typeLabel");
		DataSourceTextField description = new DataSourceTextField("description");

		setFields(name, type, description);
		setClientOnly(true);
		setDataURL("data/savedsearches.xml?sid=" + Session.get().getSid() + "&locale="
				+ LocaleInfo.getCurrentLocale().getLocaleName());
	}
}