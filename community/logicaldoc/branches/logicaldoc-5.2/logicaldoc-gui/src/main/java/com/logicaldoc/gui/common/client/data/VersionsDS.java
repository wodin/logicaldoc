package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle versions grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class VersionsDS extends DataSource {
	public VersionsDS(long docId) {
		setRecordXPath("/list/version");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceTextField version = new DataSourceTextField("version");
		DataSourceTextField fileVersion = new DataSourceTextField("fileVersion");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField comment = new DataSourceTextField("comment");

		setFields(id, user, event, version, fileVersion, date, comment);
		setClientOnly(true);
		setDataURL("data/versions.xml?sid=" + Session.get().getSid() + "&docId=" + docId + "&locale="
				+ I18N.getLocale());
	}
}