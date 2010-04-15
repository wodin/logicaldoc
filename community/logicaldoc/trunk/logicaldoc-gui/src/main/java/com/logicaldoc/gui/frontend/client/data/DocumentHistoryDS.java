package com.logicaldoc.gui.frontend.client.data;

import com.logicaldoc.gui.common.client.I18N;
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
public class DocumentHistoryDS extends DataSource {
	public DocumentHistoryDS(long docId) {
		setID("DocumentHistoryDS");
		setRecordXPath("/list/history");
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceTextField comment = new DataSourceTextField("comment");
		DataSourceTextField version = new DataSourceTextField("version");

		setFields(user, date, event, comment, version);
		setClientOnly(true);
		setDataURL("data/documenthistory.xml?sid=" + Session.getInstance().getSid() + "&id=" + docId + "&lang="
				+ I18N.getLanguage());
	}
}