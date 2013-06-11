package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle document notes grid lists. It is based on Xml parsing
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class DocumentNotesDS extends DataSource {
	public DocumentNotesDS(long documentId) {
		setRecordXPath("/list/note");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);

		DataSourceTextField docId = new DataSourceTextField("docId");
		DataSourceTextField userId = new DataSourceTextField("userId");
		DataSourceTextField username = new DataSourceTextField("username");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField message = new DataSourceTextField("message");

		setFields(id, docId, username, userId, date, message);
		setClientOnly(true);
		setDataURL("data/documentnotes.xml?sid=" + Session.get().getSid() + "&docId=" + documentId);
	}
}