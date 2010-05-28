package com.logicaldoc.gui.common.client.data;

import com.google.gwt.i18n.client.LocaleInfo;
import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle user history grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserHistoryDS extends DataSource {
	public UserHistoryDS(long userId) {
		setRecordXPath("/list/history");
		DataSourceTextField user = new DataSourceTextField("user");

		DataSourceTextField docId = new DataSourceTextField("docId");
		DataSourceTextField folderId = new DataSourceTextField("folderId");

		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceTextField comment = new DataSourceTextField("comment");
		DataSourceTextField version = new DataSourceTextField("version");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField path = new DataSourceTextField("path");
		DataSourceTextField sid = new DataSourceTextField("sid");

		setFields(user, date, event, comment, version, title, path, sid, folderId, docId);
		setClientOnly(true);
		setDataURL("data/userhistory.xml?sid=" + Session.get().getSid() + "&id=" + userId + "&locale="
				+ LocaleInfo.getCurrentLocale().getLocaleName());
	}
}