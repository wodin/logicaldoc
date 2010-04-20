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
public class VersionsDS extends DataSource {
	public VersionsDS(long docId) {
		setID("VersionsDS");
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
		setDataURL("data/versions.xml?sid=" + Session.get().getSid() + "&docId=" + docId + "&lang="
				+ I18N.getLanguage());
	}
}