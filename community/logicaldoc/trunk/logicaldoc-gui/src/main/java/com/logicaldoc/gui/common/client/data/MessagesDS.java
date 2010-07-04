package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve the system messages the current user.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MessagesDS extends DataSource {

	private static MessagesDS instance;

	public static MessagesDS get() {
		if (instance == null)
			instance = new MessagesDS();
		return instance;
	}

	private MessagesDS() {
		setTitleField("subject");
		setRecordXPath("/list/message");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);

		DataSourceTextField subject = new DataSourceTextField("subject");
		DataSourceImageField priority = new DataSourceImageField("priority");
		DataSourceTextField from = new DataSourceTextField("from");
		DataSourceDateTimeField sent = new DataSourceDateTimeField("sent");
		DataSourceTextField read = new DataSourceTextField("read");

		setFields(id, subject, priority, from, sent, read);
		setClientOnly(true);
		setDataURL("data/messages.xml?sid=" + Session.get().getSid());
	}
}