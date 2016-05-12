package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle versions grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class VersionsDS extends DataSource {
	public VersionsDS(Long docId, Long archiveId, int max) {
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
		DataSourceTextField docid = new DataSourceTextField("docid");
		DataSourceTextField customid = new DataSourceTextField("customid");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceFloatField size = new DataSourceFloatField("size");
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField template = new DataSourceTextField("attributeSet");
		DataSourceTextField type = new DataSourceTextField("type");

		setFields(id, user, event, version, fileVersion, date, comment, docid, customid, title, type, size, icon,
				template);
		setClientOnly(true);
		setDataURL("data/versions.xml?max=" + max + (docId != null ? "&docId=" + docId : "&archiveId=" + archiveId)
				+ "&locale=" + I18N.getLocale());
	}
}