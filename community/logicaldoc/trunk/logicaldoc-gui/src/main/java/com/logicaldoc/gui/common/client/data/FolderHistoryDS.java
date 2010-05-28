package com.logicaldoc.gui.common.client.data;

import com.google.gwt.i18n.client.LocaleInfo;
import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle versions grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FolderHistoryDS extends DataSource {
	public FolderHistoryDS(long folderId) {
		setRecordXPath("/list/history");
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceTextField comment = new DataSourceTextField("comment");

		setFields(user, date, event, comment);
		setClientOnly(true);

		setDataURL("data/folderhistory.xml?sid=" + Session.get().getSid() + "&id=" + folderId + "&locale="
				+ LocaleInfo.getCurrentLocale().getLocaleName());
	}
}